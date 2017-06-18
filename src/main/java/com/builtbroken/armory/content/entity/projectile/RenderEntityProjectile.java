package com.builtbroken.armory.content.entity.projectile;

import com.builtbroken.armory.Armory;
import com.builtbroken.mc.client.SharedAssets;
import com.builtbroken.mc.client.json.ClientDataHandler;
import com.builtbroken.mc.client.json.imp.IModelState;
import com.builtbroken.mc.client.json.imp.IRenderState;
import com.builtbroken.mc.client.json.render.RenderData;
import cpw.mods.fml.client.FMLClientHandler;
import net.minecraft.client.renderer.entity.RenderEntity;
import net.minecraft.entity.Entity;
import org.lwjgl.opengl.GL11;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 3/29/2017.
 */
public class RenderEntityProjectile extends RenderEntity
{
    @Override
    public void doRender(Entity entity, double rx, double ry, double rz, float p_76986_8_, float p_76986_9_)
    {
        GL11.glPushMatrix();

        //Get render data
        RenderData data = null;
        if (entity instanceof EntityAmmoProjectile && ((EntityAmmoProjectile) entity).ammoData != null)
        {
            data = ClientDataHandler.INSTANCE.getRenderData(((EntityAmmoProjectile) entity).ammoData.getUniqueID());
        }

        //Render object
        boolean rendered = false;
        if (data != null)
        {
            if (data.canRenderState("projectile"))
            {
                GL11.glPushMatrix();
                GL11.glTranslated(rx, ry, rz);
                GL11.glRotated(entity.rotationYaw, 0, 1, 0);
                GL11.glRotated(-entity.rotationPitch, 1, 0, 0);

                try
                {
                    IRenderState state = data.getState("projectile");
                    if (state instanceof IModelState)
                    {
                        rendered = ((IModelState) state).render(false, entity.rotationYaw, -entity.rotationPitch, 0);
                    }
                }
                catch (Exception e)
                {
                    Armory.INSTANCE.logger().error("RenderEntityProjectile: Error rendering projectile " + entity, e);
                }

                GL11.glPopMatrix();
            }
        }

        //Backup render
        if (!rendered)
        {
            FMLClientHandler.instance().getClient().renderEngine.bindTexture(SharedAssets.GREY_TEXTURE);
            renderOffsetAABB(entity.boundingBox, rx - entity.lastTickPosX, ry - entity.lastTickPosY, rz - entity.lastTickPosZ);
        }

        GL11.glPopMatrix();
    }
}
