package com.builtbroken.armory;

import net.minecraftforge.common.config.Configuration;

/**
 * Created by Spencer on 8/12/2015.
 */
public class ConfigurationArmory
{
    public static boolean recipeToggle;
    public static final boolean RECIPETOGGLE_DEFAULT = true;

    //TODO no need to cache names of config fields, they are only used when the method is called
    public static final String RECIPETOGGLE_NAME = "Vanilla Armor Recipes. True = recipes off";

    public static void syncConfig(Configuration config)
    {
        //Note: save and load are already done by AbstractMod

        //Recipe Category
        final String RECIPES = config.CATEGORY_GENERAL + config.CATEGORY_SPLITTER + "Recipe Toggles";
        //Within
        config.addCustomCategoryComment(RECIPES, "Enable or disable recipe toggles");
        recipeToggle = config.get(RECIPES, RECIPETOGGLE_NAME, RECIPETOGGLE_DEFAULT).getBoolean(RECIPETOGGLE_DEFAULT);

    }
}
