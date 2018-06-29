package com.builtbroken.armory.content.sentry.cart;

import com.builtbroken.armory.content.sentry.render.RenderSentry;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelMinecart;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;
import org.lwjgl.opengl.GL11;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 6/29/2018.
 */
public class RenderSentryCart extends Render
{
    private static final ResourceLocation minecartTextures = new ResourceLocation("textures/entity/minecart.png");

    /** instance of ModelMinecart for rendering */
    protected ModelBase modelMinecart = new ModelMinecart();

    public RenderSentryCart()
    {
        this.shadowSize = 0.5F;
    }

    @Override
    public void doRender(Entity entity, double xx, double yy, double zz, float yaw, float delta)
    {
        if (entity instanceof EntitySentryCart)
        {
            EntitySentryCart entitySentryCart = (EntitySentryCart) entity;

            GL11.glPushMatrix();

            this.bindEntityTexture(entitySentryCart);

            long i = (long) entitySentryCart.getEntityId() * 493286711L;
            i = i * i * 4392167121L + i * 98761L;

            float f2 = (((float) (i >> 16 & 7L) + 0.5F) / 8.0F - 0.5F) * 0.004F;
            float f3 = (((float) (i >> 20 & 7L) + 0.5F) / 8.0F - 0.5F) * 0.004F;
            float f4 = (((float) (i >> 24 & 7L) + 0.5F) / 8.0F - 0.5F) * 0.004F;

            GL11.glTranslatef(f2, f3, f4);

            double d3 = entitySentryCart.lastTickPosX + (entitySentryCart.posX - entitySentryCart.lastTickPosX) * (double) delta;
            double d4 = entitySentryCart.lastTickPosY + (entitySentryCart.posY - entitySentryCart.lastTickPosY) * (double) delta;
            double d5 = entitySentryCart.lastTickPosZ + (entitySentryCart.posZ - entitySentryCart.lastTickPosZ) * (double) delta;
            double d6 = 0.30000001192092896D;
            Vec3 vec3 = entitySentryCart.func_70489_a(d3, d4, d5);
            float f5 = entitySentryCart.prevRotationPitch + (entitySentryCart.rotationPitch - entitySentryCart.prevRotationPitch) * delta;

            if (vec3 != null)
            {
                Vec3 vec31 = entitySentryCart.func_70495_a(d3, d4, d5, d6);
                Vec3 vec32 = entitySentryCart.func_70495_a(d3, d4, d5, -d6);

                if (vec31 == null)
                {
                    vec31 = vec3;
                }

                if (vec32 == null)
                {
                    vec32 = vec3;
                }

                xx += vec3.xCoord - d3;
                yy += (vec31.yCoord + vec32.yCoord) / 2.0D - d4;
                zz += vec3.zCoord - d5;
                Vec3 vec33 = vec32.addVector(-vec31.xCoord, -vec31.yCoord, -vec31.zCoord);

                if (vec33.lengthVector() != 0.0D)
                {
                    vec33 = vec33.normalize();
                    yaw = (float) (Math.atan2(vec33.zCoord, vec33.xCoord) * 180.0D / Math.PI);
                    f5 = (float) (Math.atan(vec33.yCoord) * 73.0D);
                }
            }

            GL11.glTranslatef((float) xx, (float) yy, (float) zz);
            GL11.glRotatef(180.0F - yaw, 0.0F, 1.0F, 0.0F);
            GL11.glRotatef(-f5, 0.0F, 0.0F, 1.0F);
            float f7 = (float) entitySentryCart.getRollingAmplitude() - delta;
            float f8 = entitySentryCart.getDamage() - delta;

            if (f8 < 0.0F)
            {
                f8 = 0.0F;
            }

            if (f7 > 0.0F)
            {
                GL11.glRotatef(MathHelper.sin(f7) * f7 * f8 / 10.0F * (float) entitySentryCart.getRollingDirection(), 1.0F, 0.0F, 0.0F);
            }

            RenderSentry.render(entitySentryCart.sentry, entity.worldObj, xx, yy, zz, delta, 0);

            GL11.glScalef(-1.0F, -1.0F, 1.0F);
            this.modelMinecart.render(entitySentryCart, 0.0F, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F);
            GL11.glPopMatrix();
        }
    }

    @Override
    protected ResourceLocation getEntityTexture(Entity p_110775_1_)
    {
        return minecartTextures;
    }
}
