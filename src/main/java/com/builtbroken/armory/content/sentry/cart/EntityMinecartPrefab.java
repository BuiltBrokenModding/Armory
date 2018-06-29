package com.builtbroken.armory.content.sentry.cart;

import com.builtbroken.armory.content.sentry.SentryRefs;
import com.builtbroken.jlib.data.network.IByteBufWriter;
import com.builtbroken.mc.api.IWorldPosition;
import com.builtbroken.mc.api.tile.IPlayerUsing;
import com.builtbroken.mc.core.Engine;
import com.builtbroken.mc.core.network.IPacketIDReceiver;
import com.builtbroken.mc.core.network.packet.PacketEntity;
import com.builtbroken.mc.core.network.packet.PacketType;
import cpw.mods.fml.common.registry.IEntityAdditionalSpawnData;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import java.util.*;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 6/29/2018.
 */
public class EntityMinecartPrefab extends EntityMinecart implements IEntityAdditionalSpawnData, IPacketIDReceiver, IPlayerUsing, IWorldPosition, IByteBufWriter
{
    private static final String NBT_TILE_OWNER_NAME = "tileOwnerUsername";
    private static final String NBT_TILE_OWNER_UUID_M = "tileOwnerMostSigBit";
    private static final String NBT_TILE_OWNER_UUID_L = "tileOwnerLeastSigBit";

    private static final int PACKET_DESC = -1;
    private static final int PACKET_GUI = -2;

    private List<EntityPlayer> guiUsers = new ArrayList();

    private String ownerName;
    private UUID ownerUUID;

    public EntityMinecartPrefab(World world)
    {
        super(world);
    }

    @Override
    public void onUpdate()
    {
        super.onUpdate();

        //Send data to GUI
        doUpdateGuiUsers();
    }

    protected void doUpdateGuiUsers()
    {
        if (ticksExisted % 3 == 0 && getPlayersUsing().size() > 0)
        {
            Iterator<EntityPlayer> it = getPlayersUsing().iterator();
            while (it.hasNext())
            {
                EntityPlayer player = it.next();
                if (player instanceof EntityPlayerMP && shouldGetGuiPacket(player))
                {
                    List data = new ArrayList();
                    getGuiPacketData(player, data);
                    if (data != null && !data.isEmpty())
                    {
                        //Make packet
                        PacketEntity packet = new PacketEntity(this, SentryRefs.PACKET_GUI_DATA, data);

                        //Send
                        Engine.packetHandler.sendToPlayer(packet, (EntityPlayerMP) player);
                    }
                }
                else
                {
                    //Remove invalid
                    it.remove();
                }
            }
        }
    }

    protected boolean shouldGetGuiPacket(EntityPlayer player)
    {
        return true;
    }

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

    //<editor-fold desc="save/load">
    @Override
    protected void readEntityFromNBT(NBTTagCompound nbt)
    {
        super.readEntityFromNBT(nbt);
        if (nbt.hasKey(NBT_TILE_OWNER_UUID_M) && nbt.hasKey(NBT_TILE_OWNER_UUID_L))
        {
            this.ownerUUID = new UUID(nbt.getLong(NBT_TILE_OWNER_UUID_M), nbt.getLong(NBT_TILE_OWNER_UUID_L));
        }
        if (nbt.hasKey(NBT_TILE_OWNER_NAME))
        {
            this.ownerName = nbt.getString(NBT_TILE_OWNER_NAME);
        }
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound nbt)
    {
        super.readEntityFromNBT(nbt);
        if (ownerUUID != null)
        {
            nbt.setLong(NBT_TILE_OWNER_UUID_M, this.ownerUUID.getMostSignificantBits());
            nbt.setLong(NBT_TILE_OWNER_UUID_L, this.ownerUUID.getLeastSignificantBits());
        }
        if (ownerName != null && !ownerName.isEmpty())
        {
            nbt.setString(NBT_TILE_OWNER_NAME, this.ownerName);
        }
    }
    //</editor-fold>

    //<editor-fold desc="packet">
    @Override
    public void writeSpawnData(ByteBuf buf)
    {
        writeDescPacket(buf, null);
    }

    @Override
    public void readSpawnData(ByteBuf buf)
    {
        readDescPacket(buf, null);
    }

    public void writeDescPacket(ByteBuf buf, EntityPlayer player)
    {

    }

    public void readDescPacket(ByteBuf buf, EntityPlayer player)
    {

    }

    protected void getGuiPacketData(EntityPlayer player, List data)
    {

    }

    protected void readGuiPacket(ByteBuf buf, EntityPlayer player)
    {

    }

    @Override
    public ByteBuf writeBytes(ByteBuf buf)
    {
        writeDescPacket(buf, null);
        return buf;
    }

    @Override
    public boolean read(ByteBuf buf, int id, EntityPlayer player, PacketType type)
    {
        if (worldObj.isRemote)
        {
            if (id == PACKET_DESC)
            {
                readDescPacket(buf, player);
                return true;
            }
            else if (id == PACKET_GUI)
            {
                readGuiPacket(buf, player);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean shouldReadPacket(EntityPlayer player, IWorldPosition receiveLocation, PacketType packet)
    {
        return player.getDistanceSqToEntity(this) <= 1000;
    }

    public void sendDescPacket()
    {
        PacketEntity packetEntity = new PacketEntity(this, PACKET_DESC, this);
        Engine.packetHandler.sendToAllAround(packetEntity, worldObj, posX, posY, posZ, 100);
    }
    //</editor-fold>

    //<editor-fold desc="data">
    @Override
    public int getMinecartType()
    {
        return 0;
    }

    public String getOwnerName()
    {
        return ownerName;
    }

    public UUID getOwnerID()
    {
        return ownerUUID;
    }

    @Override
    public World oldWorld()
    {
        return worldObj;
    }

    @Override
    public double x()
    {
        return posX;
    }

    @Override
    public double y()
    {
        return posY;
    }

    @Override
    public double z()
    {
        return posZ;
    }

    @Override
    public Collection<EntityPlayer> getPlayersUsing()
    {
        return guiUsers;
    }
    //</editor-fold>
}
