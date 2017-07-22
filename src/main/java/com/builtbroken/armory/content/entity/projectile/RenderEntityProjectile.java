package com.builtbroken.armory.content.entity.projectile;

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
    public static final String[] renderKeys = new String[]{"projectile", "entity"};

    @Override
    public void doRender(Entity entity, double x, double y, double z, float f, float f1)
    {
        GL11.glPushMatrix();
        GL11.glTranslated(x, y, z);

        float pitch = interpolateRotation(entity.prevRotationPitch, entity.rotationPitch, f1);
        float yaw = interpolateRotation(entity.prevRotationYaw, entity.rotationYaw, f1);
        boolean rendered = false;
        if (entity instanceof EntityAmmoProjectile && ((EntityAmmoProjectile) entity).ammoData != null)
        {
            rendered = renderProjectile(((EntityAmmoProjectile) entity).ammoData.getUniqueID(), yaw, pitch);
        }

        if (!rendered)
        {
            FMLClientHandler.instance().getClient().renderEngine.bindTexture(SharedAssets.GREY_TEXTURE);
            renderOffsetAABB(entity.boundingBox, x - entity.lastTickPosX, y - entity.lastTickPosY, z - entity.lastTickPosZ);
        }
        GL11.glPopMatrix();
    }

    /**
     * Called to render a missile
     * <p>
     * Does not translate or wrap in push & pop matrix calls
     *
     * @param yaw
     * @param pitch
     */
    public static boolean renderProjectile(String contentID, float yaw, float pitch)
    {
        RenderData data = ClientDataHandler.INSTANCE.getRenderData(contentID);
        if (data != null)
        {
            for (String stateID : renderKeys)
            {
                IRenderState state = data.getState(stateID);
                if (state instanceof IModelState)
                {
                    if (((IModelState) state).render(false, yaw, pitch, 0))
                    {
                        return true;
                    }
                }
            }
        }
        return false;


    }

    public static float interpolateRotation(float prev, float rotation, float f)
    {
        float f3 = rotation - prev;

        while (f3 < -180.0F)
        {
            f3 += 360.0F;
        }

        while (f3 >= 180.0F)
        {
            f3 -= 360.0F;
        }

        return prev + f * f3;
    }
}
