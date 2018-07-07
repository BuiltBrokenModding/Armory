package com.builtbroken.armory.content.sentry.render;

import com.builtbroken.armory.Armory;
import com.builtbroken.armory.content.sentry.Sentry;
import com.builtbroken.mc.client.SharedAssets;
import com.builtbroken.mc.client.json.ClientDataHandler;
import com.builtbroken.mc.client.json.imp.IModelState;
import com.builtbroken.mc.client.json.imp.IRenderState;
import com.builtbroken.mc.client.json.render.RenderData;
import com.builtbroken.mc.core.Engine;
import com.builtbroken.mc.core.References;
import com.builtbroken.mc.lib.render.RenderUtility;
import com.builtbroken.mc.lib.render.model.loader.EngineModelLoader;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.client.model.IModelCustom;
import org.lwjgl.opengl.GL11;

import java.awt.*;

/**
 * Handles rendering of sentries
 *
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 6/29/2018.
 */
@SideOnly(Side.CLIENT)
public class RenderSentry
{
    private static IModelCustom sentryBackupModel = EngineModelLoader.loadModel(new ResourceLocation(Armory.DOMAIN, References.MODEL_PATH + "test.turret.tcn"));
    private static String[] parts = new String[]{"LeftFace", "RightFace", "Head", "Barrel", "BarrelBrace", "BarrelCap"};

    /**
     * Called to render a sentry as  3D render
     *
     * @param sentry     - instance of a sentry
     * @param world      - world to render inside
     * @param x          - render position
     * @param y          - render position
     * @param z          - render position
     * @param deltaFrame - time since last frame
     * @param pass       - render pass, unused at the moment
     */
    public static void render(Sentry sentry, World world, double x, double y, double z, float deltaFrame, int pass)
    {
        if (sentry != null)
        {
            double height = sentry != null ? sentry.getSentryData().getBodyHeight() : 0;

            if (Engine.runningAsDev)
            {
                RenderUtility.renderFloatingText(String.format("Yaw: %3.2f", sentry.yaw()), x, y + height + 1.8, z, Color.red.getRGB());
                RenderUtility.renderFloatingText(String.format("Pitch: %3.2f", sentry.pitch()), x, y + height + 1.5, z, Color.red.getRGB());
                RenderUtility.renderFloatingText(String.format("Status: %s", sentry.status), x, y + height + 1.2, z, Color.red.getRGB());
                String clip = sentry.gunInstance._clip != null ? (sentry.gunInstance._clip.getAmmoCount() + "/" + sentry.gunInstance._clip.getMaxAmmo()) : "null";
                RenderUtility.renderFloatingText(String.format("Ammo: %s", clip), x, y + height + 0.9, z, Color.red.getRGB());
            }

            //Get render data
            RenderData renderData = null;
            if (sentry.getSentryData() != null)
            {
                renderData = ClientDataHandler.INSTANCE.getRenderData(sentry.getSentryData().getUniqueID());
            }

            //Render parts
            boolean renderedBase = false;
            boolean renderedTurret = false;
            boolean renderedCannon = false;

            if (renderData != null)
            {
                final String emptyS = (sentry.sentryHasAmmo ? "" : ".empty");

                //Render base
                renderedBase = render(renderData, x, y, z, 0, 0, "entity.sentry.base.dead" + emptyS, "entity.sentry.base.dead", "entity.sentry.base" + emptyS, "entity.sentry.base");

                //Render turret
                renderedTurret = render(renderData, x, y, z, sentry.yaw(), 0, "entity.sentry.yaw.dead" + emptyS, "entity.sentry.yaw.dead", "entity.sentry.yaw" + emptyS, "entity.sentry.yaw");

                //Render cannon
                renderedCannon = render(renderData, x, y, z, sentry.yaw(), sentry.pitch(), "entity.sentry.pitch.dead" + emptyS, "entity.sentry.pitch.dead", "entity.sentry.pitch" + emptyS, "entity.sentry.pitch");
            }

            //If didn't render run backup
            if (!(renderedBase || renderedTurret || renderedCannon))
            {
                GL11.glPushMatrix();
                GL11.glTranslated(x, y, z);
                doBackupRender(sentry.yaw(), sentry.pitch());
                GL11.glPopMatrix();
            }
        }
        else
        {
            GL11.glPushMatrix();
            GL11.glTranslated(x, y, z);
            doBackupRender(0, 0);
            GL11.glPopMatrix();
        }
    }

    private static boolean render(RenderData renderData, double x, double y, double z, double yaw, double pitch, String... keys)
    {
        GL11.glPushMatrix();
        GL11.glTranslated(x, y, z);

        for (String key : keys)
        {
            IRenderState renderState = renderData.getState(key);
            if (renderState instanceof IModelState)
            {
                IModelState modelState = ((IModelState) renderState);
                if (modelState.render(false, (float) yaw, (float) pitch, 0))
                {
                    GL11.glPopMatrix();
                    return true;
                }
            }
        }
        GL11.glPopMatrix();
        return false;
    }

    /**
     * Backup render if no sentry data is provided or sentry is null
     *
     * @param yaw
     * @param pitch
     */
    protected static void doBackupRender(double yaw, double pitch)
    {
        FMLClientHandler.instance().getClient().renderEngine.bindTexture(SharedAssets.GREY_TEXTURE);

        //Render base
        sentryBackupModel.renderAllExcept(parts);

        //Render turret
        GL11.glRotated(yaw, 0, 1, 0);
        GL11.glRotated(pitch, 1, 0, 0);
        sentryBackupModel.renderOnly(parts);
    }
}
