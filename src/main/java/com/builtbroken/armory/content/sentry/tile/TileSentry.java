package com.builtbroken.armory.content.sentry.tile;

import cofh.api.energy.IEnergyHandler;
import com.builtbroken.armory.Armory;
import com.builtbroken.armory.content.sentry.Sentry;
import com.builtbroken.armory.content.sentry.TargetMode;
import com.builtbroken.armory.content.sentry.entity.EntitySentry;
import com.builtbroken.armory.content.sentry.gui.ContainerSentry;
import com.builtbroken.armory.content.sentry.imp.ISentryHost;
import com.builtbroken.armory.data.sentry.SentryData;
import com.builtbroken.mc.api.energy.IEMReceptiveDevice;
import com.builtbroken.mc.api.energy.IEnergyBufferProvider;
import com.builtbroken.mc.api.items.tools.IWorldPosItem;
import com.builtbroken.mc.api.tile.ILinkFeedback;
import com.builtbroken.mc.api.tile.ILinkable;
import com.builtbroken.mc.api.tile.IPassCode;
import com.builtbroken.mc.api.tile.access.IGuiTile;
import com.builtbroken.mc.core.Engine;
import com.builtbroken.mc.core.network.IPacketIDReceiver;
import com.builtbroken.mc.core.network.packet.PacketTile;
import com.builtbroken.mc.core.network.packet.PacketType;
import com.builtbroken.mc.imp.transform.region.Cube;
import com.builtbroken.mc.imp.transform.vector.Location;
import com.builtbroken.mc.imp.transform.vector.Pos;
import com.builtbroken.mc.prefab.inventory.ExternalInventory;
import com.builtbroken.mc.prefab.tile.Tile;
import com.builtbroken.mc.prefab.tile.TileModuleMachine;
import cpw.mods.fml.common.Optional;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.Map;

/**
 * Reference point and storage point for {@link EntitySentry}
 *
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 3/26/2017.
 */
@Optional.Interface(iface = "cofh.api.energy.IEnergyHandler", modid = "CoFHCore")
public class TileSentry extends TileModuleMachine<ExternalInventory> implements IGuiTile, IPacketIDReceiver, ISentryHost, IEnergyBufferProvider, IEnergyHandler, IEMReceptiveDevice, ILinkFeedback, ILinkable, IPassCode
{
    public static final int MAX_GUI_TABS = 5;
    public static final int PACKET_GUI_DATA = 1;
    public static final int PACKET_SENTRY = 2;
    public static final int PACKET_POWER = 3;
    public static final int PACKET_SET_TARGET_MODE = 4;
    public static final int PACKET_SET_PROFILE_ID = 5;

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
    public boolean isSolid(int side)
    {
        return false;
    }

    @Override
    public boolean canPlaceBlockOnSide(ForgeDirection side)
    {
        return side == ForgeDirection.UP && canPlaceBlockAt();
    }

    @Override
    public boolean canPlaceBlockAt()
    {
        if (oldWorld() != null)
        {
            Block block = oldWorld().getBlock(xi(), yi(), zi());
            if (block != null)
            {
                if (block.isReplaceable(oldWorld(), xi(), yi(), zi()))
                {
                    block = oldWorld().getBlock(xi(), yi() - 1, zi());
                    if (block != null)
                    {
                        return block.isBlockSolid(oldWorld(), xi(), yi() - 1, zi(), ForgeDirection.UP.ordinal());
                    }
                }
            }
        }
        return false;
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
                sentryEntity = new EntitySentry(oldWorld(), sentry, this);
                getSentryEntity().setPosition(xi() + 0.5, yi() + 0.5, zi() + 0.5);
                oldWorld().spawnEntityInWorld(sentryEntity);
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
    public void doUpdateGuiUsers()
    {
        super.doUpdateGuiUsers();
        if (ticks % 3 == 0 && getSentry() != null)
        {
            PacketTile packet = new PacketTile(this, PACKET_GUI_DATA);
            //Write target data
            packet.data().writeInt(getSentry().targetModes.size());
            for (Map.Entry<String, TargetMode> entry : getSentry().targetModes.entrySet())
            {
                ByteBufUtils.writeUTF8String(packet.data(), entry.getKey());
                packet.data().writeByte(entry.getValue().ordinal());
            }
            //Send
            sendPacketToGuiUsers(packet);
        }
    }

    @Override
    public void invalidate()
    {
        super.invalidate();
        if (getSentryEntity() != null)
        {
            oldWorld().removeEntity(getSentryEntity());
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
        sentryStack.stackSize = 1;
        if (stack != null)
        {
            SentryData data = Armory.itemSentry.getData(sentryStack);
            if (data != null)
            {
                setSentry(new Sentry(data));
            }
            else
            {
                Armory.INSTANCE.logger().error("Could not read sentry data from " + stack, new RuntimeException());
                setSentry(null);
            }
        }
        else
        {
            setSentry(null);
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
    public boolean read(ByteBuf buf, int id, EntityPlayer player, PacketType type)
    {
        if (!super.read(buf, id, player, type))
        {
            if (isServer())
            {
                if (id == PACKET_POWER)
                {
                    getSentry().turnedOn = buf.readBoolean();
                    return true;
                }
                else if (id == PACKET_SET_TARGET_MODE)
                {
                    String key = ByteBufUtils.readUTF8String(buf);
                    byte value = buf.readByte();
                    if (value >= 0 && value < TargetMode.values().length)
                    {
                        getSentry().targetModes.put(key, TargetMode.values()[value]);
                    }
                    return true;
                }
                else if (id == PACKET_SET_PROFILE_ID)
                {
                    getSentry().profileID = ByteBufUtils.readUTF8String(buf).trim();
                    return true;
                }
            }
            return false;
        }
        return true;
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
        ItemStack heldItem = player.getHeldItem();
        if (heldItem != null && heldItem.getItem() instanceof IWorldPosItem)
        {
            return true;
        }
        if (isServer())
        {
            openGui(player, Armory.INSTANCE);
        }
        return true;
    }

    @Override
    public void onLinked(Location location)
    {

    }

    @Override
    public String link(Location loc, short pass)
    {
        return getSentry().link(loc, pass);
    }

    @Override
    public short getCode()
    {
        return getSentry().getCode();
    }

    public void sendSentryIDToClient()
    {
        if (oldWorld() != null && isServer())
        {
            PacketTile packetTile = new PacketTile(this, PACKET_SENTRY, getSentry() == null ? -1 : getSentryEntity().getEntityId());
            Engine.packetHandler.sendToAllAround(packetTile, this);
        }
    }

    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player)
    {
        return new ContainerSentry(player, this, ID);
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player)
    {
        return null;
    }

    @Override
    public boolean openGui(EntityPlayer player, int requestedID)
    {
        if (requestedID >= 0 && requestedID < MAX_GUI_TABS)
        {
            player.openGui(Armory.INSTANCE, requestedID, oldWorld(), xi(), yi(), zi());
            return true;
        }
        return false;
    }


    public EntitySentry getSentryEntity()
    {
        return sentryEntity;
    }

    public void setSentryEntity(EntitySentry sentryEntity)
    {
        this.sentryEntity = sentryEntity;
        if (sentryEntity != null)
        {
            this.sentryEntity.setSentry(getSentry());
            sendSentryIDToClient();
        }
    }

    @Override
    public Sentry getSentry()
    {
        return sentry;
    }

    @Override
    public boolean isOwner(EntityPlayer player)
    {
        if (player != null)
        {
            if (getOwnerID() != null)
            {
                return getOwnerID().equals(player.getGameProfile().getId());
            }
            else if (getOwnerName() != null)
            {
                return player.getCommandSenderName().equalsIgnoreCase(getOwnerName());
            }
            //Fail state if no owner is set
            return true;
        }
        return false;
    }

    public void setSentry(Sentry sentry)
    {
        this.sentry = sentry;
        if (sentryEntity != null)
        {
            if (sentry != null)
            {
                sentryEntity.setSentry(sentry);
            }
            else
            {
                oldWorld().removeEntity(getSentryEntity());
            }
        }
    }

    @Override
    public double onElectromagneticRadiationApplied(double p, boolean doAction)
    {
        double power = p / 2; //TODO base on sentry size and shape compared to full block
        if (doAction)
        {
            getSentry().onEMP(power);
        }
        return Math.min(power, getSentry().getSentryData().getEmpAbsorptionLimit());
    }

    @Override
    public String toString()
    {
        return "TileSentryBase[" + (oldWorld() != null && oldWorld().provider != null ? oldWorld().provider.dimensionId : "?") + "w, " + xCoord + "x, " + yCoord + "y, " + zCoord + "z, " + sentry + "]@" + hashCode();
    }
}
