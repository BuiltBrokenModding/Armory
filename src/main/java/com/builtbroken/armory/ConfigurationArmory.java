package com.builtbroken.armory;

import cpw.mods.fml.common.FMLCommonHandler;

/**
 * Created by Spencer on 8/12/2015.
 */
public class ConfigurationArmory {

    public static boolean recipeToggle;
    public static final boolean RECIPETOGGLE_DEFAULT = true;
    public static final String RECIPETOGGLE_NAME= "Vanilla Armor Recipes. True = recipes off";

    public static void syncConfig() {

        FMLCommonHandler.instance().bus().register(Armory.INSTANCE);

        //Recipe Category
        final String RECIPES = Armory.config.CATEGORY_GENERAL + Armory.config.CATEGORY_SPLITTER + "Recipe Toggles";
        //Within
        Armory.config.addCustomCategoryComment(RECIPES, "Enable or disable recipe toggles");
        recipeToggle = Armory.config.get(RECIPES, RECIPETOGGLE_NAME, RECIPETOGGLE_DEFAULT).getBoolean(RECIPETOGGLE_DEFAULT);
        if(Armory.config.hasChanged())
            Armory.config.save();


        //Other Categories
    }

}
