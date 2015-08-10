package com.builtbroken.armory.content.blocks;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;

/**
 * Created by Spencer on 8/9/2015.
 */
public class bronzeOre {

    public static void mainRegistry() {
        initializeBlock();
        registerBlock();
    }

    public static Block bronOre;

    public static void initializeBlock() {

        bronOre = new bronOre(Material.ground).setBlockName("bronOre").setCreativeTab(CreativeTabs.tabBlock);

    }

    public static void registerBlock() {

        GameRegistry.registerBlock(bronOre, bronOre.getLocalizedName());

    }

}
