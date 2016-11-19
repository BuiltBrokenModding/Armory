package com.builtbroken.armory.client;

import com.builtbroken.armory.Armory;
import com.builtbroken.armory.CommonProxy;
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
    public void init()
    {
        super.init();
        ItemGunRenderer renderer = new ItemGunRenderer();
        MinecraftForgeClient.registerItemRenderer(Armory.itemGun, renderer);
        MinecraftForge.EVENT_BUS.register(renderer);
    }
}
