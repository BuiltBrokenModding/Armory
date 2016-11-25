package com.builtbroken.armory.client;

import com.builtbroken.armory.Armory;
import com.builtbroken.armory.CommonProxy;
import com.builtbroken.armory.json.graphics.ModelJsonProcessor;
import com.builtbroken.armory.json.graphics.RenderJsonProcessor;
import com.builtbroken.armory.json.graphics.TextureJsonProcessor;
import com.builtbroken.mc.lib.json.JsonContentLoader;
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
        JsonContentLoader.INSTANCE.add(new TextureJsonProcessor());
        JsonContentLoader.INSTANCE.add(new ModelJsonProcessor());
        JsonContentLoader.INSTANCE.add(new RenderJsonProcessor());

        JsonContentLoader.INSTANCE.process("texture");
        MinecraftForge.EVENT_BUS.register(ClientDataHandler.INSTANCE);
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
