package com.builtbroken.armory.content.sentry.tile;

import com.builtbroken.armory.Armory;
import com.builtbroken.armory.content.sentry.SentryRefs;
import com.builtbroken.armory.content.sentry.TargetMode;
import com.builtbroken.armory.content.sentry.entity.EntitySentry;
import com.builtbroken.armory.content.sentry.gui.GuiSentry;
import com.builtbroken.armory.content.sentry.render.RenderSentry;
import com.builtbroken.mc.core.network.packet.PacketType;
import com.builtbroken.mc.imp.transform.vector.Pos;
import com.builtbroken.mc.prefab.tile.Tile;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 4/10/2017.
 */
public class TileSentryClient extends TileSentry
{
    private static IIcon icon;

    @Override
    public Tile newTile()
    {
        return new TileSentryClient();
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player)
    {
        return new GuiSentry(player, this, ID);
    }

    @Override
    public boolean read(ByteBuf buf, int id, EntityPlayer player, PacketType type)
    {
        if (!super.read(buf, id, player, type))
        {
            if (id == SentryRefs.PACKET_SENTRY)
            {
                int entityID = buf.readInt();
                setEntity(entityID);
                return true;
            }
            else if (id == SentryRefs.PACKET_GUI_DATA)
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
                return true;
            }
            return false;
        }
        return true;
    }


    @Override
    public void readDescPacket(ByteBuf buf)
    {
        super.readDescPacket(buf);
        int entityID = buf.readInt();
        setEntity(entityID);
        ItemStack sentryStack = ByteBufUtils.readItemStack(buf);
        if (sentryStack.getItem() == Items.apple)
        {
            sentryStack = null;
        }
        setSentryStack(sentryStack);
        if (buf.readBoolean() && getSentry() != null)
        {
            getSentry().readBytes(buf);
        }
    }

    protected void setEntity(int entityID)
    {
        if (entityID >= 0)
        {
            Entity entity = oldWorld().getEntityByID(entityID);
            if (entity instanceof EntitySentry)
            {
                setSentryEntity((EntitySentry) entity);
                ((EntitySentry) entity).host = this;
            }
        }
        else
        {
            setSentryEntity(null);
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void renderDynamic(Pos pos, float deltaFrame, int pass)
    {
        RenderSentry.render(getSentry(), worldObj, pos.x() + 0.5, pos.y() + 0.5, pos.z() + 0.5, deltaFrame, pass);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon()
    {
        return icon;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister iconRegister)
    {
        icon = iconRegister.registerIcon(Armory.PREFIX + "sentryBase");
    }
}
