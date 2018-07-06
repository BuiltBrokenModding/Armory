package com.builtbroken.armory.content.sentry.cart;

import com.builtbroken.armory.Armory;
import com.builtbroken.armory.content.sentry.Sentry;
import com.builtbroken.armory.content.sentry.SentryRefs;
import com.builtbroken.armory.content.sentry.TargetMode;
import com.builtbroken.armory.content.sentry.gui.ContainerSentry;
import com.builtbroken.armory.content.sentry.gui.GuiSentry;
import com.builtbroken.armory.content.sentry.imp.ISentryHost;
import com.builtbroken.armory.data.sentry.SentryData;
import com.builtbroken.mc.api.energy.IEnergyBuffer;
import com.builtbroken.mc.api.energy.IEnergyBufferProvider;
import com.builtbroken.mc.api.tile.access.IGuiTile;
import com.builtbroken.mc.api.tile.provider.IInventoryProvider;
import com.builtbroken.mc.core.Engine;
import com.builtbroken.mc.core.network.packet.PacketEntity;
import com.builtbroken.mc.core.network.packet.PacketType;
import com.builtbroken.mc.framework.energy.data.EnergyBuffer;
import com.builtbroken.mc.framework.mod.AbstractProxy;
import com.builtbroken.mc.imp.transform.rotation.EulerAngle;
import com.builtbroken.mc.imp.transform.vector.Pos;
import com.builtbroken.mc.prefab.inventory.ExternalInventory;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.relauncher.Side;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 6/29/2018.
 */
public class EntitySentryCart extends EntityMinecartPrefab implements ISentryHost, IGuiTile, IInventoryProvider<ExternalInventory>, IEnergyBufferProvider
{
    //TODO rewrite entire class once TileSentry is NodeSentry. As this class can be abstracted as a generic cart that supports Nodes.
    private static final String NBT_SENTRY_STACK = "sentryStack";
    private static final String NBT_SENTRY_DATA = "sentryData";

    /** Sentry instance */
    protected Sentry sentry;

    /** Last time rotation was updated, used in {@link EulerAngle#lerp(EulerAngle, double)} function for smooth rotation */
    protected long lastSentryUpdate = System.nanoTime();
    /** Percent of time that passed since last tick, should be 1.0 on a stable server */
    protected float deltaTime;

    private ItemStack sentryStack;
    private ExternalInventory inventory;
    private IEnergyBuffer energyBuffer;

    public EntitySentryCart(World world)
    {
        super(world);
    }

    @Override
    public void onUpdate()
    {
        super.onUpdate();
        if (getSentry() != null)
        {
            //TODO add check to see if we have moved
            getSentry().center = new Pos(posX, posY + (height / 2f), posZ);
            if (getSentry().getSentryData() != null && getSentry().getSentryData().getCenterOffset() != null)
            {
                getSentry().center = getSentry().center.add(getSentry().getSentryData().getCenterOffset());
            }
            getSentry().searchArea = null; //TODO Replace search area with object that doesn't require updating

            //Track time between updates
            deltaTime = (float) ((System.nanoTime() - lastSentryUpdate) / 100000000.0); // time / time_tick, client uses different value

            //Update sentry
            if (getSentry().update(ticksExisted, deltaTime))
            {
                //Log time of last update
                lastSentryUpdate = System.nanoTime();
            }

            //Update client about sentry
            if (ticksExisted % getSentry().getPacketRefreshRate() == 0)
            {
                sendDescPacket();
            }

            //Send data to GUI
            doUpdateGuiUsers();
        }
    }

    @Override
    public boolean interactFirst(EntityPlayer player)
    {
        if (!worldObj.isRemote)
        {
            openGui(player, 0);
        }
        return true;
    }

    @Override
    public void killMinecart(DamageSource source)
    {
        super.killMinecart(source);
        //TODO drop sentry
    }

    @Override
    public ItemStack getCartItem()
    {
        //TODO return sentry cart
        return super.getCartItem();
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
                Armory.INSTANCE.logger().error("EntitySentryCart: Could not read sentry data from " + stack, new RuntimeException());
                setSentry(null);
            }
        }
        else
        {
            setSentry(null);
        }
    }

    //<editor-fold desc="save/load">
    @Override
    protected void readEntityFromNBT(NBTTagCompound nbt)
    {
        super.readEntityFromNBT(nbt);
        if (nbt.hasKey(NBT_SENTRY_STACK))
        {
            setSentryStack(ItemStack.loadItemStackFromNBT(nbt.getCompoundTag(NBT_SENTRY_STACK)));
        }
        if (getSentry() != null && nbt.hasKey(NBT_SENTRY_DATA))
        {
            getSentry().load(nbt.getCompoundTag(NBT_SENTRY_DATA));
        }
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound nbt)
    {
        super.readEntityFromNBT(nbt);
        if (sentryStack != null)
        {
            nbt.setTag(NBT_SENTRY_STACK, sentryStack.writeToNBT(new NBTTagCompound()));
        }
        if (getSentry() != null)
        {
            NBTTagCompound sentryTag = new NBTTagCompound();
            getSentry().save(sentryTag);
            nbt.setTag(NBT_SENTRY_DATA, sentryTag);
        }
    }
    //</editor-fold>

    //<editor-fold desc="packet">
    @Override
    public void sendDataPacket(int id, Side side, Object... data)
    {
        PacketEntity packetEntity = new PacketEntity(this).add(id).add(data);
        if (side == Side.CLIENT)
        {
            Engine.packetHandler.sendToAllAround(packetEntity, worldObj, posX, posY, posZ, 100);
        }
        else
        {
            Engine.packetHandler.sendToServer(packetEntity);
        }
    }

    @Override
    public void writeDescPacket(ByteBuf buf, EntityPlayer player)
    {
        //Write sentry stack
        ByteBufUtils.writeItemStack(buf, sentryStack != null ? sentryStack : new ItemStack(Items.apple));

        //Write sentry data
        buf.writeBoolean(getSentry() != null);
        if (getSentry() != null)
        {
            getSentry().writeBytes(buf);
        }
    }

    @Override
    public void readDescPacket(ByteBuf buf, EntityPlayer player)
    {
        //Read sentry stack
        ItemStack sentryStack = ByteBufUtils.readItemStack(buf);
        if (sentryStack.getItem() == Items.apple)
        {
            sentryStack = null;
        }
        setSentryStack(sentryStack);

        //Read sentry data
        if (buf.readBoolean() && getSentry() != null)
        {
            getSentry().readBytes(buf);
        }
    }

    @Override
    protected void getGuiPacketData(EntityPlayer player, List data)
    {
        //Write target data
        Set<Map.Entry<String, TargetMode>> set = getSentry().targetModes.entrySet();
        data.add(set.size());
        for (Map.Entry<String, TargetMode> entry : set)
        {
            data.add(entry.getKey());
            data.add((byte)entry.getValue().ordinal());
        }
    }

    @Override
    protected void readGuiPacket(ByteBuf buf, EntityPlayer player)
    {
        getSentry().targetModes.clear();
        final int l = buf.readInt();
        for (int i = 0; i < l; i++)
        {
            String key = ByteBufUtils.readUTF8String(buf);
            byte value = buf.readByte();
            if (value >= 0 && value < TargetMode.values().length)
            {
                getSentry().targetModes.put(key, TargetMode.values()[value]);
            }
        }
    }

    @Override
    public boolean read(ByteBuf buf, int id, EntityPlayer player, PacketType type)
    {
        if (!super.read(buf, id, player, type))
        {
            if (!worldObj.isRemote)
            {
                if (id == SentryRefs.PACKET_GUI_BUTTON)
                {
                    openGui(player, buf.readInt());
                    return true;
                }
                else if (id == SentryRefs.PACKET_POWER)
                {
                    getSentry().turnedOn = buf.readBoolean();
                    return true;
                }
                else if (id == SentryRefs.PACKET_SET_TARGET_MODE)
                {
                    String key = ByteBufUtils.readUTF8String(buf);
                    byte value = buf.readByte();
                    if (value >= 0 && value < TargetMode.values().length)
                    {
                        getSentry().targetModes.put(key, TargetMode.values()[value]);
                    }
                    return true;
                }
                else if (id == SentryRefs.PACKET_SET_PROFILE_ID)
                {
                    getSentry().profileID = ByteBufUtils.readUTF8String(buf).trim();
                    return true;
                }
            }
            return false;
        }
        return true;
    }
    //</editor-fold>

    //<editor-fold desc="gui">
    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player)
    {
        return new ContainerSentry(player, this, ID);
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player)
    {
        return new GuiSentry(player, this, ID);
    }

    @Override
    public boolean openGui(EntityPlayer player, int requestedID)
    {
        if (requestedID >= 0 && requestedID < SentryRefs.MAX_GUI_TABS)
        {
            player.openGui(Armory.INSTANCE, AbstractProxy.GUI_ENTITY, worldObj, getEntityId(), requestedID, 0);
            return true;
        }
        return false;
    }

    @Override
    protected boolean shouldGetGuiPacket(EntityPlayer player)
    {
        return player.openContainer instanceof ContainerSentry;
    }
    //</editor-fold>

    @Override
    public Sentry getSentry()
    {
        return sentry;
    }

    public void setSentry(Sentry sentry)
    {
        this.sentry = sentry;
        if (sentry != null)
        {
            sentry.host = this;
        }
    }

    @Override
    public ExternalInventory getInventory()
    {
        if (inventory == null)
        {
            inventory = createInventory();
        }
        return inventory;
    }

    protected ExternalInventory createInventory()
    {
        if (getSentry() != null && getSentry().getSentryData() != null && getSentry().getSentryData().getInventorySize() > 0)
        {
            return new ExternalInventory(this, getSentry().getSentryData().getInventorySize());
        }
        return null;
    }

    @Override
    public IEnergyBuffer getEnergyBuffer(ForgeDirection side)
    {
        if (energyBuffer == null)
        {
            energyBuffer = createEnergyBuffer();
        }
        return energyBuffer;
    }

    protected IEnergyBuffer createEnergyBuffer()
    {
        if (getSentry() != null && getSentry().getSentryData() != null && getSentry().getSentryData().getEnergyBuffer() > 0)
        {
            return new EnergyBuffer(getSentry().getSentryData().getEnergyBuffer());
        }
        return null;
    }
}
