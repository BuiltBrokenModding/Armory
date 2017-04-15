package com.builtbroken.armory.content.sentry.tile;

import cofh.api.energy.IEnergyHandler;
import com.builtbroken.armory.Armory;
import com.builtbroken.armory.content.sentry.Sentry;
import com.builtbroken.armory.content.sentry.entity.EntitySentry;
import com.builtbroken.armory.content.sentry.gui.ContainerSentry;
import com.builtbroken.armory.content.sentry.imp.ISentryHost;
import com.builtbroken.armory.data.sentry.SentryData;
import com.builtbroken.mc.api.energy.IEnergyBufferProvider;
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

/**
 * Reference point and storage point for {@link EntitySentry}
 *
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 3/26/2017.
 */
public class TileSentry extends TileModuleMachine<ExternalInventory> implements IGuiTile, IPacketIDReceiver, ISentryHost, IEnergyBufferProvider, IEnergyHandler
{
    protected Sentry sentry;
    private ItemStack sentryStack;
    private EntitySentry sentryEntity;

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
        if (getSentry() != null)
        {
            float width = Math.max(1, getSentry().getSentryData() != null ? getSentry().getSentryData().getBodyWidth() : 0);
            float height = Math.max(1, getSentry().getSentryData() != null ? getSentry().getSentryData().getBodyHeight() : 0);
            float x = xi() + 0.5f;
            float y = yi() + 0.5f;
            float z = zi() + 0.5f;
            return AxisAlignedBB.getBoundingBox(x - width, y, z - width, x + width, y + height + 0.2, z + width);
        }
        return super.getRenderBoundingBox();
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

        //Server logic
        if (isServer() && getSentry() != null)
        {
            //Create entity if null
            if (getSentryEntity() == null)
            {
                sentryEntity = new EntitySentry(world(), sentry, this);
                getSentryEntity().setPosition(xi() + 0.5, yi() + 0.5, zi() + 0.5);
                world().spawnEntityInWorld(sentryEntity);
            }

            //Force position of entity
            getSentryEntity().setPosition(xi() + 0.5, yi() + bounds.max().y() + 0.05, zi() + 0.5);

            //Update client about sentry
            if (ticks % getSentry().getPacketRefreshRate() == 0)
            {
                sendDescPacket();
            }
        }
    }

    @Override
    public void invalidate()
    {
        super.invalidate();
        if (getSentryEntity() != null)
        {
            world().removeEntity(getSentryEntity());
        }
    }

    @Override
    public ItemStack toItemStack()
    {
        if (sentryStack != null)
        {
            return sentryStack;
        }
        return super.toItemStack();
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt)
    {
        if (nbt.hasKey("sentryStack"))
        {
            setSentryStack(ItemStack.loadItemStackFromNBT(nbt.getCompoundTag("sentryStack")));
        }
        if (getSentry() != null && nbt.hasKey("sentryData"))
        {
            getSentry().load(nbt.getCompoundTag("sentryData"));
        }
        super.readFromNBT(nbt);
    }

    public void setSentryStack(ItemStack stack)
    {
        sentryStack = stack;
        if (stack != null)
        {
            SentryData data = Armory.itemSentry.getData(sentryStack);
            if (data != null)
            {
                sentry = new Sentry(data);
            }
            else
            {
                Armory.INSTANCE.logger().error("Could not read sentry data from " + stack, new RuntimeException());
                setSentryStack(null);
            }
        }
        else
        {
            sentry = null;
            if (getSentryEntity() != null)
            {
                world().removeEntity(getSentryEntity());
            }
        }
    }


    @Override
    public void writeToNBT(NBTTagCompound nbt)
    {
        super.writeToNBT(nbt);
        if (sentryStack != null)
        {
            nbt.setTag("sentryStack", sentryStack.writeToNBT(new NBTTagCompound()));
        }
        if (getSentry() != null)
        {
            NBTTagCompound sentryTag = new NBTTagCompound();
            getSentry().save(sentryTag);
            nbt.setTag("sentryData", sentryTag);
        }
    }

    @Override
    public void writeDescPacket(ByteBuf buf)
    {
        super.writeDescPacket(buf);
        buf.writeInt(getSentryEntity() == null ? -1 : getSentryEntity().getEntityId());
        ByteBufUtils.writeItemStack(buf, sentryStack != null ? sentryStack : new ItemStack(Items.apple));
        buf.writeBoolean(getSentry() != null);
        if (getSentry() != null)
        {
            getSentry().writeBytes(buf);
        }
    }

    @Override
    protected ExternalInventory createInventory()
    {
        if (getSentry() != null && getSentry().getSentryData() != null && getSentry().getSentryData().getInventorySize() > 0)
        {
            return new ExternalInventory(this, getSentry().getSentryData().getInventorySize());
        }
        return null;
    }

    @Override
    public int getEnergyBufferSize()
    {
        if (getSentry() != null && getSentry().getSentryData() != null && getSentry().getSentryData().getEnergyBuffer() > 0)
        {
            return getSentry().getSentryData().getEnergyBuffer();
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
    public Object getServerGuiElement(int ID, EntityPlayer player)
    {
        return new ContainerSentry(player, this);
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player)
    {
        return null;
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


    @Override
    public String toString()
    {
        return "TileSentryBase[" + (world() != null && world().provider != null ? world().provider.dimensionId : "?") + "w, " + xCoord + "x, " + yCoord + "y, " + zCoord + "z, " + sentry + "]@" + hashCode();
    }
}
