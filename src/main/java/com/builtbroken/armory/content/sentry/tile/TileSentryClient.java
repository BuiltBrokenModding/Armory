package com.builtbroken.armory.content.sentry.tile;

import com.builtbroken.armory.Armory;
import com.builtbroken.armory.content.sentry.entity.EntitySentry;
import com.builtbroken.armory.content.sentry.gui.GuiSentry;
import com.builtbroken.mc.client.SharedAssets;
import com.builtbroken.mc.client.json.ClientDataHandler;
import com.builtbroken.mc.client.json.imp.IModelState;
import com.builtbroken.mc.client.json.imp.IRenderState;
import com.builtbroken.mc.client.json.render.RenderData;
import com.builtbroken.mc.core.Engine;
import com.builtbroken.mc.core.References;
import com.builtbroken.mc.core.network.packet.PacketType;
import com.builtbroken.mc.imp.transform.vector.Pos;
import com.builtbroken.mc.lib.render.RenderUtility;
import com.builtbroken.mc.lib.render.model.loader.EngineModelLoader;
import com.builtbroken.mc.prefab.tile.Tile;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IModelCustom;
import org.lwjgl.opengl.GL11;

import java.awt.*;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 4/10/2017.
 */
public class TileSentryClient extends TileSentry
{
    private static IModelCustom sentryBackupModel = EngineModelLoader.loadModel(new ResourceLocation(Armory.DOMAIN, References.MODEL_PATH + "test.turret.tcn"));
    private static String[] parts = new String[]{"LeftFace", "RightFace", "Head", "Barrel", "BarrelBrace", "BarrelCap"};
    private static IIcon icon;

    @Override
    public Tile newTile()
    {
        return new TileSentryClient();
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player)
    {
        return new GuiSentry(player, this);
    }

    @Override
    public boolean read(ByteBuf buf, int id, EntityPlayer player, PacketType type)
    {
        if (!super.read(buf, id, player, type))
        {
            if (id == 2)
            {
                int entityID = buf.readInt();
                setEntity(entityID);
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
        sentryHasAmmo = buf.readBoolean();
        sentryIsAlive = buf.readBoolean();
        sentryStack = ByteBufUtils.readItemStack(buf);
        if (sentryStack.getItem() == Items.apple)
        {
            sentryStack = null;
        }
        if (sentryStack != null)
        {
            sentryData = Armory.itemSentry.getData(sentryStack);
        }
        String status = ByteBufUtils.readUTF8String(buf);
        if(getSentry() != null)
        {
            getSentry().status = status;
        }
    }

    protected void setEntity(int entityID)
    {
        if (entityID >= 0)
        {
            Entity entity = world().getEntityByID(entityID);
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

    @SideOnly(Side.CLIENT)
    public void renderDynamic(Pos pos, float frame, int pass)
    {
        if (getSentry() != null)
        {
            double rx = pos.x() + 0.5;
            double ry = pos.y() + 0.5;
            double rz = pos.z() + 0.5;

            if (Engine.runningAsDev)
            {
                RenderUtility.renderFloatingText("Yaw: " + getSentry().yaw(), rx, ry + 1.3, rz, Color.red.getRGB());
                RenderUtility.renderFloatingText("Pitch: " + getSentry().pitch(), rx, ry + 1, rz, Color.red.getRGB());
            }

            GL11.glPushMatrix();
            GL11.glTranslated(rx, ry, rz);

            //Get render data
            RenderData renderData = null;
            if (sentryData != null)
            {
                renderData = ClientDataHandler.INSTANCE.getRenderData(sentryData.getUniqueID());
            }

            //Render parts
            boolean rendered = false;
            if (renderData != null)
            {
                final String emptyS = (sentryHasAmmo ? "" : ".empty");
                //Render base
                for (String key : new String[]{"entity.sentry.base.dead" + emptyS, "entity.sentry.base.dead", "entity.sentry.base" + emptyS, "entity.sentry.base"})
                {
                    IRenderState renderState = renderData.getState(key);
                    if (renderState instanceof IModelState && ((IModelState) renderState).render())
                    {
                        rendered = true;
                        break;
                    }
                }

                //Render turret
                GL11.glRotated(getSentry().yaw(), 0, 1, 0);
                for (String key : new String[]{"entity.sentry.yaw.dead" + emptyS, "entity.sentry.yaw.dead", "entity.sentry.yaw" + emptyS, "entity.sentry.yaw"})
                {
                    IRenderState renderState = renderData.getState(key);
                    if (renderState instanceof IModelState && ((IModelState) renderState).render())
                    {
                        rendered = true;
                        break;
                    }
                }

                GL11.glRotated(getSentry().pitch(), 1, 0, 0);
                for (String key : new String[]{"entity.sentry.pitch.dead" + emptyS, "entity.sentry.pitch.dead", "entity.sentry.pitch" + emptyS, "entity.sentry.pitch"})
                {
                    IRenderState renderState = renderData.getState(key);
                    if (renderState instanceof IModelState && ((IModelState) renderState).render())
                    {
                        rendered = true;
                        break;
                    }
                }
            }

            //If didn't render run backup
            if (!rendered)
            {
                doBackupRender();
            }

            GL11.glPopMatrix();
        }
    }

    protected void doBackupRender()
    {
        if (getSentry() != null)
        {
            FMLClientHandler.instance().getClient().renderEngine.bindTexture(SharedAssets.GREY_TEXTURE);

            //Render base
            sentryBackupModel.renderAllExcept(parts);

            //Render turret
            GL11.glRotated(getSentry().y(), 0, 1, 0);
            GL11.glRotated(getSentry().pitch(), 1, 0, 0);
            sentryBackupModel.renderOnly(parts);
        }
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
