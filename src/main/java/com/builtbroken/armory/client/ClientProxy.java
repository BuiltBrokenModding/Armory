package com.builtbroken.armory.client;

import com.builtbroken.armory.Armory;
import com.builtbroken.armory.CommonProxy;
import com.builtbroken.armory.client.effects.VEProviderGunSmoke;
import com.builtbroken.armory.content.entity.projectile.EntityAmmoProjectile;
import com.builtbroken.armory.content.entity.projectile.RenderEntityProjectile;
import com.builtbroken.mc.client.effects.VisualEffectRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.common.MinecraftForge;

/**
 * Created by robert on 12/7/2014.
 */
public class ClientProxy extends CommonProxy
{
    public int addArmor(String armor)
    {
        return RenderingRegistry.addNewArmourRendererPrefix(armor);
    }

    @Override
    public void preInit()
    {
        super.preInit();
        VisualEffectRegistry.addEffectProvider(new VEProviderGunSmoke());
    }

    @Override
    public void init()
    {
        super.init();
        ItemGunRenderer renderer = new ItemGunRenderer();
        MinecraftForgeClient.registerItemRenderer(Armory.itemGun, renderer);
        MinecraftForge.EVENT_BUS.register(renderer);
        RenderingRegistry.registerEntityRenderingHandler(EntityAmmoProjectile.class, new RenderEntityProjectile());
    }
}
