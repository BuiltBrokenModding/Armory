package com.builtbroken.armory.content.mediveal;

import com.builtbroken.armory.Armory;
import com.builtbroken.armory.content.prefab.armor.ItemArmorSet;
import com.builtbroken.mc.core.registry.ModManager;
import com.builtbroken.mc.lib.mod.loadable.AbstractLoadable;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraftforge.common.util.EnumHelper;

/**
 * Created by Spencer on 8/13/2015.
 */
public class MedivealModular extends AbstractLoadable
{

    //Materials
    public static ItemArmor.ArmorMaterial bronArmor = EnumHelper.addArmorMaterial("Bronze Armor", 30, new int[]{4, 8, 4, 4}, 10);
    public static ItemArmor.ArmorMaterial steelArmor = EnumHelper.addArmorMaterial("Steel Armor", 40, new int[]{4, 10, 4, 2}, 15);

    public static Item.ToolMaterial bronToolTier1 = EnumHelper.addToolMaterial("Bronze Material Tier 1", 2, 200, 3.0F, 1.0F, 14);
    public static Item.ToolMaterial bronToolTier2 = EnumHelper.addToolMaterial("Bronze Material Tier 2", 2, 225, 3.2F, 1.5F, 14);
    public static Item.ToolMaterial bronToolTier3 = EnumHelper.addToolMaterial("Bronze Material Tier 3", 2, 250, 3.4F, 2.0F, 14);
    public static Item.ToolMaterial steelToolTier1 = EnumHelper.addToolMaterial("Steel Material Tier 1", 2, 550, 5.0F, 2.5F, 16);
    public static Item.ToolMaterial steelToolTier2 = EnumHelper.addToolMaterial("Steel Material Tier 2", 2, 575, 5.2F, 3.0F, 16);
    public static Item.ToolMaterial steelToolTier3 = EnumHelper.addToolMaterial("Steel Material Tier 3", 2, 600, 5.4F, 3.5F, 16);

    //Define
    //Tools
    public static Item bronArmingSword;
    public static Item bronLongSword;
    public static Item bronBroadSword;

    public static Item steelArmingSword;
    public static Item steelLongSword;
    public static Item steelBroadSword;


    //Armor
    //Bronze
    //Tier 1
    public static Item bronNasal;
    public static Item bronCurass;
    public static Item bronChausses;
    public static Item bronSabaton;
    //Tier 2
    public static Item bronArmet;
    public static Item bronFauld;
    public static Item bronTasset;
    public static Item bronSolleret;
    //Steel
    //Tier 1
    public static Item steelNasal;
    public static Item steelCurass;
    public static Item steelChausses;
    public static Item steelSabaton;
    //Tier 2
    public static Item steelArmet;
    public static Item steelFauld;
    public static Item steelTasset;
    public static Item steelSolleret;

    private final ModManager manager;

    public MedivealModular(ModManager manager)
    {
        this.manager = manager;
    }

    @Override
    public void preInit()
    {
        //TODO replace with modmanager calls
        initializeItem();
        registerItem();

        manager.newItem("MedievalArmorSet", ItemArmorSet.class);
    }

    public static void initializeItem()
    {

        //Items

        //Tools

        //Weapons
        //TODO you don't need to set the creative tab as it is already set by the modmanager, that is if you actually use it
        //TODO reset of these should have been set in the item class, don't follow MC's example for creating items unless you use prefabs
        bronArmingSword = new ItemBronArmingSword(bronToolTier1).setUnlocalizedName("bronArmingSword").setCreativeTab(Armory.CREATIVE_TAB).setTextureName(Armory.ASSETS_PATH + Armory.ITEM_PATH + "bronArmingSword.png");
        bronLongSword = new ItemBronLongSword(bronToolTier2).setUnlocalizedName("bronLongSword").setCreativeTab(Armory.CREATIVE_TAB).setTextureName(Armory.ASSETS_PATH + Armory.TEXTURE_PATH + ":bronLongSword.png");
        bronBroadSword = new ItemBronBroadSword(bronToolTier3).setUnlocalizedName("bronBroadSword").setCreativeTab(Armory.CREATIVE_TAB).setTextureName(Armory.ASSETS_PATH + Armory.TEXTURE_PATH + ":bronBroadSword.png");

        steelArmingSword = new ItemSteelArmingSword(steelToolTier1).setUnlocalizedName("steelArmingSword").setCreativeTab(Armory.CREATIVE_TAB).setTextureName(Armory.ASSETS_PATH + Armory.TEXTURE_PATH + ":steelArmingSword.png");
        steelLongSword = new ItemSteelLongSword(steelToolTier2).setUnlocalizedName("steelLongSword").setCreativeTab(Armory.CREATIVE_TAB).setTextureName(Armory.ASSETS_PATH + Armory.TEXTURE_PATH + ":steelLongSword.png");
        steelBroadSword = new ItemSteelBroadSword(steelToolTier3).setUnlocalizedName("steelBroadSword").setCreativeTab(Armory.CREATIVE_TAB).setTextureName(Armory.ASSETS_PATH + Armory.TEXTURE_PATH + ":steelBroadSword.png");

        //Armors
       /* bronNasal = new ItemBronArmor(bronArmor, Armory.proxy.addArmor("ItemBronArmor"), 0).setUnlocalizedName("bronNasal").setCreativeTab(Armory.CREATIVE_TAB).setTextureName(Armory.ASSETS_PATH + Armory.TEXTURE_PATH + ":bronNasal");
        bronCurass = new ItemBronArmor(bronArmor, Armory.proxy.addArmor("ItemBronArmor"), 1).setUnlocalizedName("bronCurass").setCreativeTab(Armory.CREATIVE_TAB).setTextureName(Armory.ASSETS_PATH + Armory.TEXTURE_PATH + ":bronCurass");
        bronChausses = new ItemBronArmor(bronArmor, Armory.proxy.addArmor("ItemBronArmor"), 2).setUnlocalizedName("bronChausses").setCreativeTab(Armory.CREATIVE_TAB).setTextureName(Armory.ASSETS_PATH + Armory.TEXTURE_PATH + ":bronChausses");
        bronSabaton = new ItemBronArmor(bronArmor, Armory.proxy.addArmor("ItemBronArmor"), 3).setUnlocalizedName("bronSabaton").setCreativeTab(Armory.CREATIVE_TAB).setTextureName(Armory.ASSETS_PATH + Armory.TEXTURE_PATH + ":bronSabaton");

        bronArmet = new ItemBronArmor(bronArmor, Armory.proxy.addArmor("ItemBronArmor"), 0).setUnlocalizedName("bronArmet").setCreativeTab(Armory.CREATIVE_TAB).setTextureName(Armory.ASSETS_PATH + Armory.TEXTURE_PATH + ":bronArmet");
        bronFauld = new ItemBronArmor(bronArmor, Armory.proxy.addArmor("ItemBronArmor"), 1).setUnlocalizedName("bronFauld").setCreativeTab(Armory.CREATIVE_TAB).setTextureName(Armory.ASSETS_PATH + Armory.TEXTURE_PATH + ":bronFauld");
        bronTasset = new ItemBronArmor(bronArmor, Armory.proxy.addArmor("ItemBronArmor"), 2).setUnlocalizedName("bronTasset").setCreativeTab(Armory.CREATIVE_TAB).setTextureName(Armory.ASSETS_PATH + Armory.TEXTURE_PATH + ":bronTasset");
        bronSolleret = new ItemBronArmor(bronArmor, Armory.proxy.addArmor("ItemBronArmor"), 3).setUnlocalizedName("bronSolleret").setCreativeTab(Armory.CREATIVE_TAB).setTextureName(Armory.ASSETS_PATH + Armory.TEXTURE_PATH + ":bronSolleret");


        steelNasal = new ItemSteelArmor(steelArmor, Armory.proxy.addArmor("ItemSteelArmor"), 0).setUnlocalizedName("steelNasal").setCreativeTab(Armory.CREATIVE_TAB).setTextureName(Armory.ASSETS_PATH + Armory.TEXTURE_PATH + ":steelNasal");
        steelCurass = new ItemSteelArmor(steelArmor, Armory.proxy.addArmor("ItemSteelArmor"), 1).setUnlocalizedName("steelCurass").setCreativeTab(Armory.CREATIVE_TAB).setTextureName(Armory.ASSETS_PATH + Armory.TEXTURE_PATH + ":steelCurass");
        steelChausses = new ItemSteelArmor(steelArmor, Armory.proxy.addArmor("ItemSteelArmor"), 2).setUnlocalizedName("steelChausses").setCreativeTab(Armory.CREATIVE_TAB).setTextureName(Armory.ASSETS_PATH + Armory.TEXTURE_PATH + ":steelChausses");
        steelSabaton = new ItemSteelArmor(steelArmor, Armory.proxy.addArmor("ItemSteelArmor"), 3).setUnlocalizedName("steelSabaton").setCreativeTab(Armory.CREATIVE_TAB).setTextureName(Armory.ASSETS_PATH + Armory.TEXTURE_PATH + ":steelSabaton");

        steelArmet = new ItemSteelArmor(steelArmor, Armory.proxy.addArmor("ItemSteelArmor"), 0).setUnlocalizedName("steelArmet").setCreativeTab(Armory.CREATIVE_TAB).setTextureName(Armory.ASSETS_PATH + Armory.TEXTURE_PATH + ":steelArmet");
        steelFauld = new ItemSteelArmor(steelArmor, Armory.proxy.addArmor("ItemSteelArmor"), 1).setUnlocalizedName("steelFauld").setCreativeTab(Armory.CREATIVE_TAB).setTextureName(Armory.ASSETS_PATH + Armory.TEXTURE_PATH + ":steelFauld");
        steelTasset = new ItemSteelArmor(steelArmor, Armory.proxy.addArmor("ItemSteelArmor"), 2).setUnlocalizedName("steelTasset").setCreativeTab(Armory.CREATIVE_TAB).setTextureName(Armory.ASSETS_PATH + Armory.TEXTURE_PATH + ":steelTasset");
        steelSolleret = new ItemSteelArmor(steelArmor, Armory.proxy.addArmor("ItemSteelArmor"), 3).setUnlocalizedName("steelSolleret").setCreativeTab(Armory.CREATIVE_TAB).setTextureName(Armory.ASSETS_PATH + Armory.TEXTURE_PATH + ":steelSolleret");
*/
    }

    public static void registerItem()
    {

        /*GameRegistry.registerItem(bronNasal, bronNasal.getUnlocalizedName());
        GameRegistry.registerItem(bronCurass, bronCurass.getUnlocalizedName());
        GameRegistry.registerItem(bronChausses, bronChausses.getUnlocalizedName());
        GameRegistry.registerItem(bronSabaton, bronSabaton.getUnlocalizedName());

        GameRegistry.registerItem(bronArmet, bronArmet.getUnlocalizedName());
        GameRegistry.registerItem(bronFauld, bronFauld.getUnlocalizedName());
        GameRegistry.registerItem(bronTasset, bronTasset.getUnlocalizedName());
        GameRegistry.registerItem(bronSolleret, bronSolleret.getUnlocalizedName());

        GameRegistry.registerItem(steelNasal, steelNasal.getUnlocalizedName());
        GameRegistry.registerItem(steelCurass, steelCurass.getUnlocalizedName());
        GameRegistry.registerItem(steelChausses, steelChausses.getUnlocalizedName());
        GameRegistry.registerItem(steelSabaton, steelSabaton.getUnlocalizedName());

        GameRegistry.registerItem(steelArmet, steelArmet.getUnlocalizedName());
        GameRegistry.registerItem(steelFauld, steelFauld.getUnlocalizedName());
        GameRegistry.registerItem(steelTasset, steelTasset.getUnlocalizedName());
        GameRegistry.registerItem(steelSolleret, steelSolleret.getUnlocalizedName());*/

        GameRegistry.registerItem(bronArmingSword, bronArmingSword.getUnlocalizedName());
        GameRegistry.registerItem(bronLongSword, bronLongSword.getUnlocalizedName());
        GameRegistry.registerItem(bronBroadSword, bronBroadSword.getUnlocalizedName());

        GameRegistry.registerItem(steelArmingSword, steelArmingSword.getUnlocalizedName());
        GameRegistry.registerItem(steelLongSword, steelLongSword.getUnlocalizedName());
        GameRegistry.registerItem(steelBroadSword, steelBroadSword.getUnlocalizedName());

    }
}
