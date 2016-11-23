package com.builtbroken.armory.client;

import com.builtbroken.armory.Armory;
import com.builtbroken.armory.client.data.RenderData;
import com.builtbroken.armory.content.items.ItemGun;
import com.builtbroken.armory.data.ranged.GunData;
import com.builtbroken.mc.core.References;
import com.builtbroken.mc.lib.render.model.loader.EngineModelLoader;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.client.model.IModelCustom;
import org.lwjgl.opengl.GL11;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 11/19/2016.
 */
public class ItemGunRenderer implements IItemRenderer
{
    private boolean init = false;

    private IModelCustom handgun = EngineModelLoader.loadModel(new ResourceLocation(Armory.DOMAIN, References.MODEL_PATH + "handgun.tcn"));

    @SubscribeEvent
    public void onPlayerRender(RenderPlayerEvent.Pre event)
    {
        if (event.entityPlayer.getHeldItem() != null && event.entityPlayer.getHeldItem().getItem() instanceof ItemGun)
        {
            //event.renderer.modelBipedMain.bipedRightArm.showModel = false;
        }
    }

    @SubscribeEvent
    public void onPlayerRender(RenderPlayerEvent.Post event)
    {
        if (event.entityPlayer.getHeldItem() != null && event.entityPlayer.getHeldItem().getItem() instanceof ItemGun)
        {
            //event.renderer.modelBipedMain.bipedRightArm.showModel = true;
        }
    }

    @Override
    public boolean handleRenderType(ItemStack item, ItemRenderType type)
    {
        if (item.getItem() instanceof ItemGun && ((ItemGun) item.getItem()).getData(item) != null)
        {
            RenderData data = ClientDataHandler.INSTANCE.getRenderData(((ItemGun) item.getItem()).getData(item).getUniqueID());
            if (data != null)
            {
                return data.shouldRenderType(type);
            }
            return type != ItemRenderType.INVENTORY;
        }
        return false;
    }

    @Override
    public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper)
    {
        //TODO see if we can return true as we really don't need to check this twice
        if (item.getItem() instanceof ItemGun && ((ItemGun) item.getItem()).getData(item) != null)
        {
            RenderData data = ClientDataHandler.INSTANCE.getRenderData(((ItemGun) item.getItem()).getData(item).getUniqueID());
            if (data != null)
            {
                return data.shouldRenderType(type);
            }
            return type != ItemRenderType.INVENTORY;
        }
        return false;
    }

    @Override
    public void renderItem(ItemRenderType type, ItemStack item, Object... dataArray)
    {
        GunData gunData = ((ItemGun) item.getItem()).getData(item);
        GL11.glPushMatrix();
        switch (type)
        {
            case ENTITY:
                GL11.glTranslatef(0, 0.3f, 0);
                break;
            case EQUIPPED:
                GL11.glRotatef(-75, 1, 0, 0);
                GL11.glRotatef(30, 0, 0, 1);
                GL11.glRotatef(20, 0, 1, 0);
                GL11.glTranslatef(0.2f, -0.5f, -0f);
                GL11.glScalef(2, 2, 2);
                break;
            case EQUIPPED_FIRST_PERSON:
                GL11.glTranslatef(-0.4f, 1.3f, 1f);
                GL11.glRotatef(-30, 0, 1, 0);
                GL11.glRotatef(13, 1, 0, 0);
                GL11.glScaled(1.8f, 1.8f, 1.8f);
                break;
        }
        RenderData data = ClientDataHandler.INSTANCE.getRenderData(gunData.getUniqueID());
        if (data == null || !data.render(type))
        {
            FMLClientHandler.instance().getClient().renderEngine.bindTexture(References.GREY_TEXTURE);
            handgun.renderAll();
        }
        GL11.glPopMatrix();
    }
}
