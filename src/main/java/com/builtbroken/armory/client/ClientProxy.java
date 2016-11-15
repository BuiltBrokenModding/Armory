package com.builtbroken.armory.client;

import com.builtbroken.armory.CommonProxy;
import cpw.mods.fml.client.registry.RenderingRegistry;

/**
 * Created by robert on 12/7/2014.
 */
public class ClientProxy extends CommonProxy
{
    public int addArmor(String armor)
    {
        return RenderingRegistry.addNewArmourRendererPrefix(armor);
    }
}
