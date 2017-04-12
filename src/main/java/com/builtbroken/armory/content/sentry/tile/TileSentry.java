package com.builtbroken.armory.content.sentry.tile;

import com.builtbroken.armory.Armory;
import com.builtbroken.armory.content.sentry.Sentry;
import com.builtbroken.armory.content.sentry.entity.EntitySentry;
import com.builtbroken.armory.content.sentry.gui.ContainerSentry;
import com.builtbroken.armory.content.sentry.imp.ISentryHost;
import com.builtbroken.armory.data.sentry.SentryData;
import com.builtbroken.mc.api.tile.access.IGuiTile;
import com.builtbroken.mc.core.Engine;
import com.builtbroken.mc.core.network.IPacketIDReceiver;
import com.builtbroken.mc.core.network.packet.PacketTile;
import com.builtbroken.mc.imp.transform.region.Cube;
import com.builtbroken.mc.imp.transform.vector.Pos;
import com.builtbroken.mc.prefab.inventory.ExternalInventory;
import com.builtbroken.mc.prefab.tile.Tile;
import com.builtbroken.mc.prefab.tile.TileModuleMachine;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraftforge.common.util.ForgeDirection;

/**
 * Reference point and storage point for {@link EntitySentry}
 *
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 3/26/2017.
 */
public class TileSentry extends TileModuleMachine<ExternalInventory> implements IGuiTile, IPacketIDReceiver, ISentryHost
{
    public SentryData sentryData;
    private EntitySentry sentryEntity;
    private Sentry sentry;

    protected ItemStack sentryStack;

    protected boolean running = false;
    protected boolean turnedOn = true;

    protected boolean sentryHasAmmo = false;
    protected boolean sentryIsAlive = false;

    public TileSentry()
    {
        super("sentry", Material.iron);
        // this.itemBlock = ItemBlockSentry.class; TODO use item instead of an item block
        bounds = new Cube(0, 0, 0, 1, .2, 1);
        this.renderNormalBlock = true;
        this.renderTileEntity = true;
        this.isOpaque = false;
    }

    @Override
    public Cube getCollisionBounds()
    {
        return Cube.EMPTY;
    }

    @Override
    public Cube getSelectBounds()
    {
        return Cube.FULL;
    }

    @Override
    public Cube getBlockBounds()
    {
        return bounds;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getRenderBoundingBox()
    {
        float width = Math.max(1, sentryData != null ? sentryData.getBodyWidth() : 0);
        float height = Math.max(1, sentryData != null ? sentryData.getBodyHeight() : 0);
        float x = xi() + 0.5f;
        float y = yi() + 0.5f;
        float z = zi() + 0.5f;
        return AxisAlignedBB.getBoundingBox(x - width, y, z - width, x + width, y + height + 0.2, z + width);
    }

    @Override
    public void firstTick()
    {
        super.firstTick();
    }

    @Override
    public void update()
    {
        super.update();

        //TODO remove
        if (getSentry() == null || sentryData == null)
        {
            loadSentryData();
        }

        //Server logic
        if (isServer())
        {
            //Reset state
            sentryHasAmmo = false;
            running = false;
            sentryIsAlive = false;

            if (getSentry() != null)
            {
                //Force position
                getSentryEntity().setPosition(xi() + 0.5, yi() + bounds.max().y() + 0.05, zi() + 0.5);

                //Update has ammo for renders
                if (getSentry().gunInstance != null)
                {
                    sentryHasAmmo = getSentry().gunInstance.hasAmmo();
                    sentryIsAlive = getSentryEntity().getHealth() > 0;
                }
            }

            if (sentryData != null && turnedOn)
            {
                //Consume energy per tick
                if (sentryData.getEnergyCost() > 0)
                {
                    //energy is consumed even if there is not enough for a full cycle
                    int drained = getEnergyBuffer(ForgeDirection.UNKNOWN).removeEnergyFromStorage(sentryData.getEnergyCost(), true);
                    running = drained >= sentryData.getEnergyCost();
                    //TODO add negative effects
                    //      TODO add percentage performance if energy is above 40%
                    //      TODO if bellow 40% start to check for brown out damage
                }
            }

            if (sentryData == null)
            {
                Engine.logger().error("Removing corrupted sentry tile from world, " + this);
                world().setBlockToAir(xCoord, yCoord, zCoord);
            }

            if (ticks % 3 == 0)
            {
                sendDescPacket();
            }
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt)
    {
        running = nbt.getBoolean("running");
        turnedOn = nbt.getBoolean("enabled");
        if (nbt.hasKey("sentryStack"))
        {
            sentryStack = ItemStack.loadItemStackFromNBT(nbt.getCompoundTag("sentryStack"));
            sentryData = Armory.itemSentry.getData(sentryStack);
            sentry = new Sentry(sentryData);
        }
        loadSentryData();
        super.readFromNBT(nbt);
    }

    protected void loadSentryData()
    {
        if (sentryData != null && getSentry() == null && isServer())
        {
            sentryEntity = new EntitySentry(world());
            getSentryEntity().setPosition(xi() + 0.5, yi() + 0.5, zi() + 0.5); //TODO adjust based on data
            getSentryEntity().host = this;
            world().spawnEntityInWorld(sentryEntity);
            setSentryEntity(sentryEntity);
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt)
    {
        super.writeToNBT(nbt);
        nbt.setBoolean("running", running);
        nbt.setBoolean("enabled", turnedOn);
        if (sentryStack != null)
        {
            nbt.setTag("sentryStack", sentryStack.writeToNBT(new NBTTagCompound()));
        }
    }

    @Override
    protected ExternalInventory createInventory()
    {
        if (sentryData != null && sentryData.getInventorySize() > 0)
        {
            return new ExternalInventory(this, sentryData.getInventorySize());
        }
        return null;
    }

    @Override
    public int getEnergyBufferSize()
    {
        if (sentryData != null && sentryData.getInventorySize() > 0)
        {
            return sentryData.getEnergyBuffer();
        }
        return 0;
    }

    @Override
    public Tile newTile()
    {
        return new TileSentry();
    }

    @Override
    protected boolean onPlayerRightClick(EntityPlayer player, int side, Pos hit)
    {
        if (isServer())
        {
            openGui(player, Armory.INSTANCE);
        }
        return false;
    }

    public void sendSentryIDToClient()
    {
        if (world() != null && isServer())
        {
            PacketTile packetTile = new PacketTile(this, 2, getSentry() == null ? -1 : getSentryEntity().getEntityId());
            Engine.instance.packetHandler.sendToAllAround(packetTile, this);
        }
    }

    @Override
    public void writeDescPacket(ByteBuf buf)
    {
        super.writeDescPacket(buf);
        buf.writeInt(getSentry() == null ? -1 : getSentryEntity().getEntityId());
        buf.writeBoolean(sentryHasAmmo);
        buf.writeBoolean(sentryIsAlive);
        ByteBufUtils.writeItemStack(buf, sentryStack != null ? sentryStack : new ItemStack(Items.apple));
        if (getSentry() != null)
        {
            ByteBufUtils.writeUTF8String(buf, getSentry().status);
        }
        else
        {
            ByteBufUtils.writeUTF8String(buf, "null");
        }
    }

    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player)
    {
        return new ContainerSentry(player, this);
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player)
    {
        return null;
    }

    @Override
    public String toString()
    {
        return "TileSentryBase[" + (world() != null && world().provider != null ? world().provider.dimensionId : "?") + "w, " + xCoord + "x, " + yCoord + "y, " + zCoord + "z, " + sentryData + "]@" + hashCode();
    }

    public EntitySentry getSentryEntity()
    {
        return sentryEntity;
    }

    public void setSentryEntity(EntitySentry sentryEntity)
    {
        this.sentryEntity = sentryEntity;
        this.sentryEntity.setSentry(sentry);
        sendSentryIDToClient();
    }

    @Override
    public Sentry getSentry()
    {
        return sentry;
    }
}
