package com.builtbroken.armory.content.sentry;

import com.builtbroken.armory.Armory;
import com.builtbroken.mc.client.SharedAssets;
import com.builtbroken.mc.core.References;
import com.builtbroken.mc.lib.render.RenderUtility;
import com.builtbroken.mc.lib.render.model.loader.EngineModelLoader;
import cpw.mods.fml.client.FMLClientHandler;
import net.minecraft.client.renderer.entity.RenderEntity;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IModelCustom;
import org.lwjgl.opengl.GL11;

import java.awt.*;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 3/29/2017.
 */
public class RenderEntitySentry extends RenderEntity
{
    private IModelCustom handgun = EngineModelLoader.loadModel(new ResourceLocation(Armory.DOMAIN, References.MODEL_PATH + "handgun.tcn"));

    @Override
    public void doRender(Entity entity, double rx, double ry, double rz, float p_76986_8_, float p_76986_9_)
    {
        RenderUtility.renderFloatingText("Yaw: " + entity.rotationYaw, rx, ry + 1.3, rz, Color.red.getRGB());
        RenderUtility.renderFloatingText("Pitch: " + entity.rotationPitch, rx, ry + 1, rz, Color.red.getRGB());

        GL11.glPushMatrix();

        GL11.glTranslated(rx, ry, rz);

        GL11.glRotated(entity.rotationYaw, 0, 1, 0);
        GL11.glRotated(entity.rotationPitch, 1, 0, 0);
        FMLClientHandler.instance().getClient().renderEngine.bindTexture(SharedAssets.GREY_TEXTURE);
        handgun.renderAll();

        GL11.glPopMatrix();
    }
}
