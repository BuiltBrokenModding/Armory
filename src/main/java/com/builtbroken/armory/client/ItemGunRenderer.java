package com.builtbroken.armory.client;

import com.builtbroken.armory.Armory;
import com.builtbroken.armory.content.items.ItemGun;
import com.builtbroken.armory.data.ranged.GunData;
import com.builtbroken.armory.data.ranged.GunInstance;
import com.builtbroken.mc.client.SharedAssets;
import com.builtbroken.mc.client.json.ClientDataHandler;
import com.builtbroken.mc.client.json.render.RenderData;
import com.builtbroken.mc.client.json.render.item.ItemJsonRenderer;
import com.builtbroken.mc.core.References;
import com.builtbroken.mc.lib.render.model.loader.EngineModelLoader;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.client.model.IModelCustom;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 11/19/2016.
 */
public class ItemGunRenderer extends ItemJsonRenderer
{
    private IModelCustom handgun = EngineModelLoader.loadModel(new ResourceLocation(Armory.DOMAIN, References.MODEL_PATH + "handgun.tcn"));

    @SubscribeEvent
    public void onPlayerRender(RenderPlayerEvent.Pre event)
    {
        ItemStack stack = event.entityPlayer.getHeldItem();
        if (stack != null && stack.getItem() instanceof ItemGun)
        {
            ItemGun item = (ItemGun) stack.getItem();
            RenderPlayer render = event.renderer;

            GunInstance instance = item.getGunInstance(stack, event.entityPlayer);
            if (instance != null)
            {
                render.modelArmorChestplate.aimedBow = render.modelArmor.aimedBow = render.modelBipedMain.aimedBow = instance.isSighted;
            }
        }
        //event.renderer.modelBipedMain.bipedRightArm.showModel = false;
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
                return data.shouldRenderType(type, null, item);
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
                return data.shouldRenderType(type, null, item);
            }
            return type != ItemRenderType.INVENTORY;
        }
        return false;
    }

    @Override
    protected List<String> getRenderStatesToTry(ItemRenderType type, ItemStack item, Object... dataArray)
    {
        if (item.getItem() instanceof ItemGun)
        {
            List<String> list = new ArrayList();
            //Find entity that is holding this weapon or is the dropped item on the ground
            Entity entity = null;
            for (Object obj : dataArray)
            {
                if (obj instanceof Entity)
                {
                    entity = (Entity) obj;
                }
            }
            //Try to get gun instance version of data
            if (entity != null)
            {
                GunInstance gun = ((ItemGun) item.getItem()).getGunInstance(item, entity);
                String key = ((ItemGun) item.getItem()).getRenderKey(gun);
                //If gun instance doesn't provide data use ItemStack raw data
                if (key == null)
                {
                    key = ((ItemGun) item.getItem()).getRenderKey(item);
                }
                if (gun.isSighted)
                {
                    list.add(key + ".sighted"); //legacy
                    list.add(key + ".aimed");
                }
                else if (gun.isLowered)
                {
                    list.add(key + ".lowered");
                }
                list.add(key);
            }
            return list.isEmpty() ? null : list;
        }
        return null;
    }

    @Override
    protected RenderData getRenderData(ItemRenderType type, ItemStack item, Object... dataArray)
    {
        if (item.getItem() instanceof ItemGun)
        {
            GunData gunData = ((ItemGun) item.getItem()).getData(item);
            if (gunData != null)
            {
                return ClientDataHandler.INSTANCE.getRenderData(gunData.getUniqueID());
            }
        }
        return null;
    }

    @Override
    protected void doBackupRender(ItemRenderType type)
    {
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

        FMLClientHandler.instance().getClient().renderEngine.bindTexture(SharedAssets.GREY_TEXTURE);
        handgun.renderAll();
    }
}
