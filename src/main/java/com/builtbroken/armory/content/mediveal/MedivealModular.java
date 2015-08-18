package com.builtbroken.armory.content.mediveal;

import com.builtbroken.armory.Armory;
import com.builtbroken.mc.lib.mod.ModCreativeTab;
import com.builtbroken.mc.lib.mod.loadable.AbstractLoadable;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraftforge.common.util.EnumHelper;

/**
 * Created by Spencer on 8/13/2015.
 */
public class MedivealModular extends AbstractLoadable{

    public static void mainRegistry(){
        //Inint
        initializeItem();
        initalizeBlock();
        //Reg
        registerItem();
        registerBlock();
    }

    //Materials
    public static ItemArmor.ArmorMaterial bronArmor = EnumHelper.addArmorMaterial("Bronze Armor", 30, new int[]{4, 8, 4, 4}, 10);
    public static ItemArmor.ArmorMaterial steelArmor = EnumHelper.addArmorMaterial("Steel Armor", 40, new int[]{4, 10, 4, 2}, 15);

    //Define
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

    public static void initializeItem() {

        //Items

        //Tools

        //Armors
        bronNasal = new ItemBronArmor(bronArmor, Armory.proxy.addArmor("ItemBronArmor"), 0).setUnlocalizedName("bronNasal").setCreativeTab(ModCreativeTab.tabTools).setTextureName(Armory.ASSETS_PATH + Armory.TEXTURE_PATH + ":bronNasal");
        bronCurass = new ItemBronArmor(bronArmor, Armory.proxy.addArmor("ItemBronArmor"), 1).setUnlocalizedName("bronCurass").setCreativeTab(ModCreativeTab.tabTools).setTextureName(Armory.ASSETS_PATH + Armory.TEXTURE_PATH + ":bronCurass");
        bronChausses = new ItemBronArmor(bronArmor, Armory.proxy.addArmor("ItemBronArmor"), 2).setUnlocalizedName("bronChausses").setCreativeTab(ModCreativeTab.tabTools).setTextureName(Armory.ASSETS_PATH + Armory.TEXTURE_PATH + ":bronChausses");
        bronSabaton = new ItemBronArmor(bronArmor, Armory.proxy.addArmor("ItemBronArmor"), 3).setUnlocalizedName("bronSabaton").setCreativeTab(ModCreativeTab.tabTools).setTextureName(Armory.ASSETS_PATH + Armory.TEXTURE_PATH + ":bronSabaton");

        bronArmet = new ItemBronArmor(bronArmor, Armory.proxy.addArmor("ItemBronArmor"), 0).setUnlocalizedName("bronArmet").setCreativeTab(ModCreativeTab.tabTools).setTextureName(Armory.ASSETS_PATH + Armory.TEXTURE_PATH + ":bronArmet");
        bronFauld = new ItemBronArmor(bronArmor, Armory.proxy.addArmor("ItemBronArmor"), 1).setUnlocalizedName("bronFauld").setCreativeTab(ModCreativeTab.tabTools).setTextureName(Armory.ASSETS_PATH + Armory.TEXTURE_PATH + ":bronFauld");
        bronTasset = new ItemBronArmor(bronArmor, Armory.proxy.addArmor("ItemBronArmor"), 2).setUnlocalizedName("bronTasset").setCreativeTab(ModCreativeTab.tabTools).setTextureName(Armory.ASSETS_PATH + Armory.TEXTURE_PATH + ":bronTasset");
        bronSolleret = new ItemBronArmor(bronArmor, Armory.proxy.addArmor("ItemBronArmor"), 3).setUnlocalizedName("bronSolleret").setCreativeTab(ModCreativeTab.tabTools).setTextureName(Armory.ASSETS_PATH + Armory.TEXTURE_PATH + ":bronSolleret");


        steelNasal = new ItemSteelArmor(steelArmor, Armory.proxy.addArmor("ItemSteelArmor"), 0).setUnlocalizedName("steelNasal").setCreativeTab(ModCreativeTab.tabTools).setTextureName(Armory.ASSETS_PATH + Armory.TEXTURE_PATH + ":steelNasal");
        steelCurass = new ItemSteelArmor(steelArmor, Armory.proxy.addArmor("ItemSteelArmor"), 1).setUnlocalizedName("steelCurass").setCreativeTab(ModCreativeTab.tabTools).setTextureName(Armory.ASSETS_PATH + Armory.TEXTURE_PATH + ":steelCurass");
        steelChausses = new ItemSteelArmor(steelArmor, Armory.proxy.addArmor("ItemSteelArmor"), 2).setUnlocalizedName("steelChausses").setCreativeTab(ModCreativeTab.tabTools).setTextureName(Armory.ASSETS_PATH + Armory.TEXTURE_PATH + ":steelChausses");
        steelSabaton = new ItemSteelArmor(steelArmor, Armory.proxy.addArmor("ItemSteelArmor"), 3).setUnlocalizedName("steelSabaton").setCreativeTab(ModCreativeTab.tabTools).setTextureName(Armory.ASSETS_PATH + Armory.TEXTURE_PATH + ":steelSabaton");

        steelArmet = new ItemSteelArmor(steelArmor, Armory.proxy.addArmor("ItemSteelArmor"), 0).setUnlocalizedName("steelArmet").setCreativeTab(ModCreativeTab.tabTools).setTextureName(Armory.ASSETS_PATH + Armory.TEXTURE_PATH + ":steelArmet");
        steelFauld = new ItemSteelArmor(steelArmor, Armory.proxy.addArmor("ItemSteelArmor"), 1).setUnlocalizedName("steelFauld").setCreativeTab(ModCreativeTab.tabTools).setTextureName(Armory.ASSETS_PATH + Armory.TEXTURE_PATH + ":steelFauld");
        steelTasset = new ItemSteelArmor(steelArmor, Armory.proxy.addArmor("ItemSteelArmor"), 2).setUnlocalizedName("steelTasset").setCreativeTab(ModCreativeTab.tabTools).setTextureName(Armory.ASSETS_PATH + Armory.TEXTURE_PATH + ":steelTasset");
        steelSolleret = new ItemSteelArmor(steelArmor, Armory.proxy.addArmor("ItemSteelArmor"), 3).setUnlocalizedName("steelSolleret").setCreativeTab(ModCreativeTab.tabTools).setTextureName(Armory.ASSETS_PATH + Armory.TEXTURE_PATH + ":steelSolleret");

    }

    public static void initalizeBlock(){

    }

    public static void registerItem(){

        GameRegistry.registerItem(bronNasal, bronNasal.getUnlocalizedName());
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
        GameRegistry.registerItem(steelSolleret, steelSolleret.getUnlocalizedName());

    }

    public static void registerBlock(){

    }

}
