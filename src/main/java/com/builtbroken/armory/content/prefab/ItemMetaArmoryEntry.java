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
import com.builtbroken.mc.framework.item.ItemBase;
import com.builtbroken.mc.framework.json.imp.IJSONMetaConvert;
import cpw.mods.fml.common.FMLCommonHandler;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 11/20/2016.
 */
public class ItemMetaArmoryEntry<E extends ArmoryEntry> extends ItemBase implements IPacketReceiver, IPostInit, IJSONMetaConvert
{
    /** Type of the item @see {@link ArmoryDataHandler} */
    public final String typeName;

    public CreativeTabs[] creativeTabs;

    public ItemMetaArmoryEntry(String id, String name, String typeName)
    {
        super(id, Armory.DOMAIN, name);
        ArmoryDataHandler.INSTANCE.get(typeName).add(this);
        this.typeName = typeName;
        this.setHasSubtypes(true);
    }

    @Override
    public String getRenderContentID(int meta)
    {
        if (getData(meta) == null)
        {
            return node.owner + ":" + typeName;
        }
        return getData(meta).getUniqueID();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean b)
    {
        super.addInformation(stack, player, list, b);
        if (Engine.runningAsDev)
        {
            list.add("Key: " + (getData(stack) != null ? getData(stack).getUniqueID() : "null"));
        }
    }

    @Override
    public String getUnlocalizedName(ItemStack stack)
    {
        E data = getData(stack);
        if (data != null)
        {
            return data.getUnlocalizedName();
        }
        return "item." + this.unlocalizedName;
    }

    public E getData(ItemStack stack)
    {
        return getData(stack.getItemDamage());
    }

    public E getData(int meta)
    {
        return getArmoryData().metaToEntry.get(meta);
    }

    public ArmoryDataHandler.ArmoryData<E> getArmoryData()
    {
        return ArmoryDataHandler.INSTANCE.get(typeName);
    }

    @Override
    public void onPostInit()
    {
        MinecraftForge.EVENT_BUS.register(this);
        FMLCommonHandler.instance().bus().register(this);
    }

    @SubscribeEvent
    public void onWorldLoad(WorldEvent.Load event)
    {
        if (event.world.provider.dimensionId == 0)
        {
            ArmoryDataHandler.INSTANCE.get(typeName).init(this);
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
        ArmoryDataHandler.INSTANCE.get(typeName).readBytes(buf);
    }

    protected void sendSyncPacket(EntityPlayerMP player)
    {
        PacketPlayerItem packet = new PacketPlayerItem(Item.getIdFromItem(this) * -1);
        ArmoryDataHandler.INSTANCE.get(typeName).writeBytes(packet.data());
        Engine.packetHandler.sendToPlayer(packet, player);
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
        if(tab != null)
        {
            String tabName = tab.getTabLabel();
            Map<Integer, E> map = ArmoryDataHandler.INSTANCE.get(typeName).metaToEntry;
            for (Map.Entry<Integer, E> entry : map.entrySet())
            {
                E value = entry.getValue();
                if (value != null && value.showInCreativeTab && (tab == null || tab == getCreativeTab() && value.creativeTabToUse == null || tabName.equalsIgnoreCase(value.creativeTabToUse)))
                {
                    getSubItems(item, entry.getKey(), entry.getValue(), tab, items);
                }
            }
        }
    }

    @Override
    public CreativeTabs[] getCreativeTabs()
    {
        if (creativeTabs == null)
        {
            List<String> tabNames = new ArrayList();
            for (E entry : getArmoryData().values())
            {
                if (entry.creativeTabToUse != null)
                {
                    String key = entry.creativeTabToUse.toLowerCase();
                    if (!tabNames.contains(key))
                    {
                        tabNames.add(key);
                    }
                }
            }

            if (!tabNames.isEmpty())
            {
                List<CreativeTabs> tabs = new ArrayList();
                for (CreativeTabs tab : CreativeTabs.creativeTabArray)
                {
                    if (tab != getCreativeTab())
                    {
                        String key = tab.getTabLabel().toLowerCase();
                        if (tabNames.contains(key))
                        {
                            tabs.add(tab);
                        }
                    }
                }

                creativeTabs = new CreativeTabs[tabs.size() + 1];
                creativeTabs[0] = getCreativeTab();
                int i = 1;
                for (CreativeTabs tab : tabs)
                {
                    creativeTabs[i++] = tab;
                }
            }
            else
            {
                creativeTabs = new CreativeTabs[]{getCreativeTab()};
            }
        }
        return creativeTabs;
    }

    @SideOnly(Side.CLIENT)
    protected void getSubItems(Item item, int meta, E armoryEntry, CreativeTabs tab, List items)
    {
        items.add(new ItemStack(item, 1, meta));
    }

    @Override
    public int getMetaForValue(String value)
    {
        ArmoryDataHandler.ArmoryData data = ArmoryDataHandler.INSTANCE.get(typeName);
        if (data != null && data.containsKey(value))
        {
            return data.get(value).meta;
        }
        return -1;
    }
}
