package com.builtbroken.armory;

import com.builtbroken.armory.api.ArmoryAPI;
import com.builtbroken.armory.content.entity.projectile.EntityAmmoProjectile;
import com.builtbroken.armory.content.items.*;
import com.builtbroken.armory.content.prefab.ItemMetaArmoryEntry;
import com.builtbroken.armory.content.sentry.entity.EntitySentry;
import com.builtbroken.armory.content.sentry.tile.ItemSentry;
import com.builtbroken.armory.data.ArmoryDataHandler;
import com.builtbroken.armory.data.ammo.AmmoData;
import com.builtbroken.armory.data.clip.ClipData;
import com.builtbroken.armory.data.damage.simple.DamageSimple;
import com.builtbroken.armory.data.damage.type.DamageImpact;
import com.builtbroken.armory.data.meele.MeleeToolData;
import com.builtbroken.armory.data.meele.MeleeWeaponData;
import com.builtbroken.armory.data.ranged.GunData;
import com.builtbroken.armory.data.ranged.ThrowableData;
import com.builtbroken.armory.data.sentry.SentryData;
import com.builtbroken.armory.json.damage.DamageJsonProcessor;
import com.builtbroken.armory.json.processors.*;
import com.builtbroken.mc.core.Engine;
import com.builtbroken.mc.core.References;
import com.builtbroken.mc.core.registry.ModManager;
import com.builtbroken.mc.framework.json.JsonContentLoader;
import com.builtbroken.mc.framework.mod.AbstractMod;
import com.builtbroken.mc.framework.mod.ModCreativeTab;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.EntityRegistry;
import net.minecraft.block.Block;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

/**
 * Created by robert on 11/18/2014.
 */
@Mod(modid = Armory.DOMAIN, name = "Armory", version = Armory.VERSION, dependencies = Armory.DEPENDENCIES)
public final class Armory extends AbstractMod
{
    /** Name of the channel and mod ID. */
    public static final String DOMAIN = "armory";
    public static final String PREFIX = DOMAIN + ":";

    /** The version of WatchYourStep. */
    public static final String MAJOR_VERSION = "@MAJOR@";
    public static final String MINOR_VERSION = "@MINOR@";
    public static final String REVISION_VERSION = "@REVIS@";
    public static final String BUILD_VERSION = "@BUILD@";
    public static final String VERSION = MAJOR_VERSION + "." + MINOR_VERSION + "." + REVISION_VERSION + "." + BUILD_VERSION;
    public static final String DEPENDENCIES = "required-after:voltzengine;";

    @Mod.Instance(DOMAIN)
    public static Armory INSTANCE;

    @SidedProxy(clientSide = "com.builtbroken.armory.client.ClientProxy", serverSide = "com.builtbroken.armory.server.ServerProxy")
    public static CommonProxy proxy;

    public static ModCreativeTab CREATIVE_TAB;

    public static Block blockSentry;

    public static final String SENTRY_BLOCK_NAME = "sentryTile";
    public static final String SENTRY_BLOCK_REG = PREFIX + "sentryTile";

    public static ItemMetaArmoryEntry<GunData> itemGun;
    public static ItemMetaArmoryEntry<ClipData> itemClip;
    public static ItemMetaArmoryEntry<AmmoData> itemAmmo;
    public static ItemMetaArmoryEntry<SentryData> itemSentry;
    public static ItemMetaArmoryEntry<ThrowableData> itemThrownWeapon;
    public static ItemMetaArmoryEntry<MeleeToolData> itemMeleeTool;
    public static ItemMetaArmoryEntry<MeleeWeaponData> itemMeleeWeapon;

    //Configs
    /** Overrides the delay between attacks on entities */
    public static boolean overrideDamageDelay = true;

    public Armory()
    {
        super(DOMAIN, "Armory");
        CREATIVE_TAB = new ModCreativeTab("armory");
        getManager().setTab(CREATIVE_TAB);
        modIssueTracker = "https://github.com/BuiltBrokenModding/Armory/issues";
    }

    @Override
    public void loadJsonContentHandlers()
    {
        ArmoryDataHandler.INSTANCE.add(new ArmoryDataHandler.ArmoryData(References.BBM_CONFIG_FOLDER, ArmoryAPI.GUN_ID));
        ArmoryDataHandler.INSTANCE.add(new ArmoryDataHandler.ArmoryData(References.BBM_CONFIG_FOLDER, ArmoryAPI.AMMO_ID));
        ArmoryDataHandler.INSTANCE.add(new ArmoryDataHandler.ArmoryData(References.BBM_CONFIG_FOLDER, ArmoryAPI.AMMO_TYPE_ID));
        ArmoryDataHandler.INSTANCE.add(new ArmoryDataHandler.ArmoryData(References.BBM_CONFIG_FOLDER, ArmoryAPI.CLIP_ID));
        ArmoryDataHandler.INSTANCE.add(new ArmoryDataHandler.ArmoryData(References.BBM_CONFIG_FOLDER, ArmoryAPI.SENTRY_ID));
        ArmoryDataHandler.INSTANCE.add(new ArmoryDataHandler.ArmoryData(References.BBM_CONFIG_FOLDER, ArmoryAPI.THROWABLE_WEAPON_ID));
        ArmoryDataHandler.INSTANCE.add(new ArmoryDataHandler.ArmoryData(References.BBM_CONFIG_FOLDER, ArmoryAPI.MELEE_WEAPON_ID));
        ArmoryDataHandler.INSTANCE.add(new ArmoryDataHandler.ArmoryData(References.BBM_CONFIG_FOLDER, ArmoryAPI.MELEE_TOOL_ID));

        JsonContentLoader.INSTANCE.add(new AmmoTypeJsonProcessor());
        JsonContentLoader.INSTANCE.add(new AmmoJsonProcessor());
        JsonContentLoader.INSTANCE.add(new ClipJsonProcessor());
        JsonContentLoader.INSTANCE.add(new GunJsonProcessor());
        JsonContentLoader.INSTANCE.add(new SentryJsonProcessor());
        JsonContentLoader.INSTANCE.add(new ThrownJsonProcessor());
        JsonContentLoader.INSTANCE.add(new MeleeToolJsonProcessor());
        JsonContentLoader.INSTANCE.add(new MeleeWeaponJsonProcessor());

        JsonContentLoader.INSTANCE.add(DamageJsonProcessor.INSTANCE);
    }

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        fixConfig();

        super.preInit(event); //load items

        Engine.requestResources();
        Engine.requestSheetMetalContent();
        Engine.requestMultiBlock();
        Engine.requestSimpleTools();
        Engine.requestCircuits();
        Engine.requestCraftingParts();
    }

    @Override
    public void loadItems(ModManager manager)
    {
        itemGun = manager.newItem("armoryGun", new ItemGun());
        itemClip = manager.newItem("armoryClip", new ItemClip());
        itemAmmo = manager.newItem("armoryAmmo", new ItemAmmo());
        itemSentry = manager.newItem("armorySentry", new ItemSentry());
        itemThrownWeapon = manager.newItem("armoryThrownWeapon", new ItemThrownWeapon());
        itemMeleeTool = manager.newItem("armoryMeleeTool", new ItemTool());
        itemMeleeWeapon = manager.newItem("armoryMeleeWeapon", new ItemMeleeWeapon());

        //TODO fire registry event for mods to add content before initializing data

        ArmoryDataHandler.INSTANCE.get(itemGun.typeName).init(itemGun);
        ArmoryDataHandler.INSTANCE.get(itemClip.typeName).init(itemClip);
        ArmoryDataHandler.INSTANCE.get(itemAmmo.typeName).init(itemAmmo);
        ArmoryDataHandler.INSTANCE.get(itemSentry.typeName).init(itemSentry);
        ArmoryDataHandler.INSTANCE.get(itemThrownWeapon.typeName).init(itemThrownWeapon);
        ArmoryDataHandler.INSTANCE.get(itemMeleeTool.typeName).init(itemMeleeTool);
        ArmoryDataHandler.INSTANCE.get(itemMeleeWeapon.typeName).init(itemMeleeWeapon);
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event)
    {
        super.init(event);

        //Hide turret block
        blockSentry.setCreativeTab(null);

        //Load damage types
        DamageSimple.damageTypes.put("impact", new DamageImpact.DamageTypeImpact());

        //Register entities
        EntityRegistry.registerModEntity(EntityAmmoProjectile.class, "ArmoryProjectile", 0, this, 500, 1, true);
        EntityRegistry.registerModEntity(EntitySentry.class, "ArmorySentry", 1, this, 500, 1, true);
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event)
    {
        super.postInit(event); //Nothing to really do
    }

    protected void fixConfig()
    {
        //TODO remove in 1.12 port
        //Fix configs being in the wrong place
        File oldConfigFolder = new File(References.BBM_CONFIG_FOLDER, "bbm/armory");
        File configFolder = new File(References.BBM_CONFIG_FOLDER, "armory");
        if (!configFolder.exists())
        {
            configFolder.mkdirs();
            try
            {
                if (oldConfigFolder.exists() && oldConfigFolder.isDirectory())
                {
                    for (File file : oldConfigFolder.listFiles())
                    {
                        try
                        {
                            File newFile = new File(configFolder, file.getName());
                            if (!newFile.exists())
                            {
                                Files.move(file.toPath(), newFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                            }
                        }
                        catch (Exception e)
                        {
                            throw new RuntimeException("Error moving file: " + file, e);
                        }
                    }
                }
            }
            catch (Exception e)
            {
                logger().error("Failed to move files from old config folder to new config folder. If you want to keep these settings it will need to be manually moved.", e);
                if (!GraphicsEnvironment.isHeadless())
                {
                    int reply = JOptionPane.showConfirmDialog(null, "Failed to move config files from old folder to new folder." +
                                    "\nDo you want to close Minecraft to manual move files?" +
                                    "\nClick no to ignore but be warned can cause issues",
                            "Error moving files", JOptionPane.YES_OPTION);
                    if (reply == JOptionPane.YES_OPTION)
                    {
                        throw new RuntimeException("Exiting");
                    }
                }
                else
                {
                    throw new RuntimeException("Failed to move files, see log for details and manually move files to correct for the issues.\n" + oldConfigFolder + " needs to be moved to " + configFolder);
                }
            }
        }
    }

    @Override
    public CommonProxy getProxy()
    {
        return proxy;
    }
}
