package com.builtbroken.armory;

import com.builtbroken.armory.content.items.ItemGun;
import com.builtbroken.armory.json.processor.AmmoJsonProcessor;
import com.builtbroken.armory.json.processor.AmmoTypeJsonProcessor;
import com.builtbroken.armory.json.processor.ClipJsonProcessor;
import com.builtbroken.armory.json.processor.GunJsonProcessor;
import com.builtbroken.mc.core.Engine;
import com.builtbroken.mc.lib.mod.AbstractMod;
import com.builtbroken.mc.lib.mod.ModCreativeTab;
import com.builtbroken.mc.lib.json.JsonContentLoader;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import net.minecraft.item.Item;

/**
 * Created by robert on 11/18/2014.
 */
@Mod(modid = Armory.DOMAIN, name = "Armory", version = Armory.VERSION, dependencies = "required-after:VoltzEngine")
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

    @Mod.Instance(DOMAIN)
    public static Armory INSTANCE;

    @SidedProxy(clientSide = "com.builtbroken.armory.client.ClientProxy", serverSide = "com.builtbroken.armory.server.CommonProxy")
    public static CommonProxy proxy;

    public static ModCreativeTab CREATIVE_TAB;

    public static Item itemGun;

    public Armory()
    {
        super(DOMAIN, "Armory");
        CREATIVE_TAB = new ModCreativeTab("armory");
        getManager().setTab(CREATIVE_TAB);
        modIssueTracker = "https://github.com/BuiltBrokenModding/Armory/issues";
    }

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        super.preInit(event);
        Engine.requestOres();
        Engine.requestResources();
        Engine.requestSheetMetalContent();
        Engine.requestMultiBlock();
        Engine.requestSimpleTools();
        Engine.requestCircuits();
        Engine.requestCraftingParts();

        JsonContentLoader.INSTANCE.add(new AmmoTypeJsonProcessor());
        JsonContentLoader.INSTANCE.add(new AmmoJsonProcessor());
        JsonContentLoader.INSTANCE.add(new ClipJsonProcessor());
        JsonContentLoader.INSTANCE.add(new GunJsonProcessor());

        itemGun = getManager().newItem("armoryGun", new ItemGun());
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event)
    {
        super.init(event);
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event)
    {
        super.postInit(event);
    }

    @Override
    public CommonProxy getProxy()
    {
        return proxy;
    }
}
