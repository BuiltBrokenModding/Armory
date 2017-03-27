package com.builtbroken.armory.content.prefab;

import com.builtbroken.armory.data.ArmoryDataHandler;
import com.builtbroken.armory.data.ArmoryEntry;
import com.builtbroken.mc.api.IWorldPosition;
import com.builtbroken.mc.client.json.ClientDataHandler;
import com.builtbroken.mc.client.json.render.RenderData;
import com.builtbroken.mc.client.json.render.RenderState;
import com.builtbroken.mc.core.Engine;
import com.builtbroken.mc.core.network.IPacketReceiver;
import com.builtbroken.mc.core.network.packet.PacketPlayerItem;
import com.builtbroken.mc.core.network.packet.PacketType;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.WorldEvent;

import java.util.List;
import java.util.Map;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 3/26/2017.
 */
public class ItemBlockMetaArmoryEntry<E extends ArmoryEntry> extends ItemBlock implements IPacketReceiver
{
    /** Type of the item @see {@link ArmoryDataHandler} */
    public final String typeName;

    public ItemBlockMetaArmoryEntry(Block block, String typeName)
    {
        super(block);
        ArmoryDataHandler.INSTANCE.get(typeName).add(this);
        this.typeName = typeName;
        this.setHasSubtypes(true);

        MinecraftForge.EVENT_BUS.register(this);
        FMLCommonHandler.instance().bus().register(this);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean b)
    {
        if (Engine.runningAsDev)
        {
            list.add("" + getData(stack));
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister reg)
    {

    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIconFromDamage(int meta)
    {
        E data = getData(meta);
        if (data != null)
        {
            RenderData renderData = ClientDataHandler.INSTANCE.getRenderData(data.getUniqueID());
            if (renderData != null)
            {
                RenderState state = renderData.getState(RenderData.INVENTORY_RENDER_ID);
                if (state != null && !state.isModelRenderer())
                {
                    IIcon icon = state.getIcon();
                    if (icon != null)
                    {
                        return icon;
                    }
                }
                IIcon icon = getIconFromState(renderData, "meta" + meta);
                if (icon != null)
                {
                    return icon;
                }
            }
        }
        return getDefaultIcon(meta);
    }

    @SideOnly(Side.CLIENT)
    protected IIcon getDefaultIcon(int meta)
    {
        return Items.stick.getIconFromDamage(meta);
    }

    @Override
    public IIcon getIcon(ItemStack stack, int pass)
    {
        E data = getData(stack);
        if (data != null)
        {
            RenderData renderData = ClientDataHandler.INSTANCE.getRenderData(data.getUniqueID());
            if (renderData != null)
            {
                String[] keys = getIconStringKeys(stack, pass);
                if (keys != null)
                {
                    for (String key : keys)
                    {
                        IIcon icon = getIconFromState(renderData, key);
                        if (icon != null)
                        {
                            return icon;
                        }
                    }
                }
            }
        }
        return getIconFromDamageForRenderPass(stack.getItemDamage(), pass);
    }

    @Override
    public int getRenderPasses(int metadata)
    {
        //TODO add override for this in the render data
        return 1;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean requiresMultipleRenderPasses()
    {
        return true;
    }

    protected String[] getIconStringKeys(ItemStack stack, int pass)
    {
        return null;
    }

    @SideOnly(Side.CLIENT)
    private IIcon getIconFromState(RenderData renderData, String key)
    {
        RenderState state = renderData.getState(key);
        if (state != null && !state.isModelRenderer())
        {
            IIcon icon = state.getIcon();
            if (icon != null)
            {
                return icon;
            }
        }
        return null;
    }

    public E getData(ItemStack stack)
    {
        return getData(stack.getItemDamage());
    }

    public E getData(int meta)
    {
        return (E) ArmoryDataHandler.INSTANCE.get(typeName).metaToEntry.get(meta);
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
        Map<Integer, E> map = ArmoryDataHandler.INSTANCE.get(typeName).metaToEntry;
        for (Map.Entry<Integer, E> entry : map.entrySet())
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
