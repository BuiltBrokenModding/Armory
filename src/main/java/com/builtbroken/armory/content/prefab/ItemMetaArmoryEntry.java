package com.builtbroken.armory.content.prefab;

import com.builtbroken.armory.Armory;
import com.builtbroken.armory.data.ArmoryDataHandler;
import com.builtbroken.armory.data.ArmoryEntry;
import com.builtbroken.mc.api.IWorldPosition;
import com.builtbroken.mc.core.Engine;
import com.builtbroken.mc.core.network.IPacketReceiver;
import com.builtbroken.mc.core.network.packet.PacketPlayerItem;
import com.builtbroken.mc.core.network.packet.PacketType;
import com.builtbroken.mc.core.registry.implement.IPostInit;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.WorldEvent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 11/20/2016.
 */
public class ItemMetaArmoryEntry<E extends ArmoryEntry> extends Item implements IPacketReceiver, IPostInit
{
    /** Map of weapons to meta values for fast access */
    public final HashMap<Integer, E> metaToData = new HashMap();

    public final String typeName;

    public ItemMetaArmoryEntry(String name, String typeName)
    {
        this.typeName = typeName;
        this.setUnlocalizedName(Armory.PREFIX + name);
        this.setCreativeTab(CreativeTabs.tabCombat);
        this.setHasSubtypes(true);
    }

    public E getData(ItemStack stack)
    {
        return metaToData.get(stack.getItemDamage());
    }

    @Override
    public void onPostInit()
    {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onWorldLoad(WorldEvent.Load event)
    {
        if (event.world.provider.dimensionId == 0)
        {
            ArmoryDataHandler.INSTANCE.get(typeName).init(metaToData);
        }
    }

    @SubscribeEvent
    public void onConnect(PlayerEvent.PlayerLoggedInEvent event)
    {
        if (!event.player.worldObj.isRemote && event.player instanceof EntityPlayerMP)
        {
            sendSyncPacket((EntityPlayerMP) event.player);
        }
    }


    @Override
    public void read(ByteBuf buf, EntityPlayer player, PacketType packet)
    {
        ArmoryDataHandler.INSTANCE.get(typeName).readBytes(buf, metaToData);
    }

    protected void sendSyncPacket(EntityPlayerMP player)
    {
        PacketPlayerItem packet = new PacketPlayerItem(Item.getIdFromItem(this) * -1);
        ArmoryDataHandler.INSTANCE.get(typeName).writeBytes(packet.data(), metaToData);
        Engine.instance.packetHandler.sendToPlayer(packet, player);
    }

    @Override
    public boolean shouldReadPacket(EntityPlayer player, IWorldPosition receiveLocation, PacketType packet)
    {
        return player.worldObj.isRemote;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void getSubItems(Item item, CreativeTabs tab, List items)
    {
        for (Map.Entry<Integer, E> entry : metaToData.entrySet())
        {
            getSubItems(item, entry.getKey(), entry.getValue(), tab, items);
        }
    }

    @SideOnly(Side.CLIENT)
    protected void getSubItems(Item item, int meta, E armoryEntry, CreativeTabs tab, List items)
    {
        items.add(new ItemStack(item, 1, meta));
    }
}
