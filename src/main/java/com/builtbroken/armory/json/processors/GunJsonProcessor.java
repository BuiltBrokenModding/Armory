package com.builtbroken.armory.json.processors;

import com.builtbroken.armory.data.ArmoryDataHandler;
import com.builtbroken.armory.data.ammo.AmmoType;
import com.builtbroken.armory.data.clip.ClipData;
import com.builtbroken.armory.data.ranged.GunData;
import com.builtbroken.armory.json.ArmoryEntryJsonProcessor;
import com.builtbroken.jlib.lang.DebugPrinter;
import com.builtbroken.mc.api.data.weapon.ReloadType;
import com.builtbroken.mc.core.Engine;
import com.builtbroken.mc.lib.json.JsonContentLoader;
import com.builtbroken.mc.lib.json.loading.JsonProcessorInjectionMap;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import org.apache.logging.log4j.LogManager;

import java.util.Map;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 11/16/2016.
 */
public class GunJsonProcessor extends ArmoryEntryJsonProcessor<GunData>
{
    protected final JsonProcessorInjectionMap keyHandler;
    protected final DebugPrinter debugPrinter;

    public GunJsonProcessor()
    {
        super("gun");
        keyHandler = new JsonProcessorInjectionMap(GunData.class);
        debugPrinter = JsonContentLoader.INSTANCE != null ? JsonContentLoader.INSTANCE.debug : new DebugPrinter(LogManager.getLogger());
    }

    @Override
    public String getJsonKey()
    {
        return "gun";
    }

    @Override
    public String getLoadOrder()
    {
        return "after:ammo";
    }

    @Override
    public GunData process(JsonElement element)
    {
        debugPrinter.start("SentryProcessor", "Processing entry", Engine.runningAsDev);
        final JsonObject gunJsonData = element.getAsJsonObject();
        ensureValuesExist(gunJsonData, "ID", "name", "gunType", "reloadType", "ammoType");

        //common data
        final String name = gunJsonData.get("name").getAsString();
        final String ID = gunJsonData.get("ID").getAsString();

        debugPrinter.log("Name: " + name);
        debugPrinter.log("ID: " + ID);


        //Gun type
        final String type = gunJsonData.get("gunType").getAsString();
        debugPrinter.log("Type: " + type);


        //Get the reload type of the gun
        JsonPrimitive clipTypeValue = gunJsonData.getAsJsonPrimitive("reloadType");
        ReloadType reloadType = ReloadType.get(clipTypeValue.getAsString());
        debugPrinter.log("ReloadType: " + reloadType);


        //Get and validate ammo type
        final String ammoTypeValue = gunJsonData.get("ammoType").getAsString();
        final AmmoType ammoType = (AmmoType) ArmoryDataHandler.INSTANCE.get("ammoType").get(ammoTypeValue);
        if (ammoType == null)
        {
            throw new IllegalArgumentException("Invalid ammo type " + ammoType + " while reading " + element);
        }
        debugPrinter.log("AmmoType: " + ammoType);


        //Build single fire clip type used to breach load the weapon, also doubles as the clip type for muskets & bold action rifles
        final ClipData builtInClip;
        if (reloadType == ReloadType.FRONT_LOADED)
        {
            builtInClip = new ClipData(this, ID, name + "@frontLoaded", ReloadType.FRONT_LOADED, ammoType, 1);
        }
        else if (reloadType == ReloadType.HAND_FEED)
        {
            ensureValuesExist(gunJsonData, "clipSize");
            builtInClip = new ClipData(this, ID, name + "@handFeed", ReloadType.HAND_FEED, ammoType, gunJsonData.getAsJsonPrimitive("clipSize").getAsInt());
        }
        else
        {
            builtInClip = new ClipData(this, ID, name + "@singleFire", ReloadType.BREACH_LOADED, ammoType, 1);
        }

        //Make gun object
        final GunData gunData = new GunData(this, ID, type, name, ammoType, reloadType, builtInClip);

        //Call to process injection tags
        for (Map.Entry<String, JsonElement> entry : gunJsonData.entrySet())
        {
            if (keyHandler.handle(gunData, entry.getKey().toLowerCase(), entry.getValue()))
            {
                debugPrinter.log("Injected Key: " + entry.getKey());
            }
        }

        //Process extra data that all objects share
        processExtraData(gunJsonData, gunData);
        debugPrinter.end("Done...");
        return gunData;
    }
}
