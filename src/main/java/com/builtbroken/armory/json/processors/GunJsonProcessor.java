package com.builtbroken.armory.json.processors;

import com.builtbroken.armory.api.ArmoryAPI;
import com.builtbroken.armory.data.ArmoryDataHandler;
import com.builtbroken.armory.data.ammo.AmmoType;
import com.builtbroken.armory.data.clip.ClipData;
import com.builtbroken.armory.data.ranged.GunData;
import com.builtbroken.armory.data.ranged.barrels.BarrelDamageMode;
import com.builtbroken.armory.data.ranged.barrels.BarrelFireMode;
import com.builtbroken.armory.data.ranged.barrels.GunBarrel;
import com.builtbroken.armory.json.ArmoryEntryJsonProcessor;
import com.builtbroken.jlib.debug.DebugPrinter;
import com.builtbroken.mc.api.data.weapon.ReloadType;
import com.builtbroken.mc.core.Engine;
import com.builtbroken.mc.framework.json.JsonContentLoader;
import com.builtbroken.mc.framework.json.conversion.data.transform.JsonConverterPos;
import com.builtbroken.mc.framework.json.loading.JsonProcessorInjectionMap;
import com.builtbroken.mc.imp.transform.vector.Pos;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import org.apache.logging.log4j.LogManager;

import java.util.*;

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
        super(ArmoryAPI.GUN_ID);
        keyHandler = new JsonProcessorInjectionMap(GunData.class);
        debugPrinter = JsonContentLoader.INSTANCE != null ? JsonContentLoader.INSTANCE.debug : new DebugPrinter(LogManager.getLogger());
    }

    @Override
    public String getJsonKey()
    {
        return ArmoryAPI.GUN_ID;
    }

    @Override
    public String getLoadOrder()
    {
        return "after:" + ArmoryAPI.AMMO_ID;
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

        if (gunJsonData.has("barrels"))
        {
            JsonObject barrelJsonData = gunJsonData.getAsJsonObject("barrels");
            ensureValuesExist(barrelJsonData, "fire", "damage", "points");

            String fireMode = barrelJsonData.getAsJsonPrimitive("fire").getAsString();
            String damageMode = barrelJsonData.getAsJsonPrimitive("damage").getAsString();
            JsonArray points = barrelJsonData.getAsJsonArray("points");

            if (fireMode.equalsIgnoreCase("all"))
            {
                gunData.gunBarrelData.barrelFireMode = BarrelFireMode.ALL;
            }
            else if (fireMode.equalsIgnoreCase("single"))
            {
                gunData.gunBarrelData.barrelFireMode = BarrelFireMode.SINGLE;
            }
            else if (fireMode.equalsIgnoreCase("CURRENT"))
            {
                gunData.gunBarrelData.barrelFireMode = BarrelFireMode.CURRENT;
            }
            else
            {
                throw new IllegalArgumentException("Unknown firing mode '" + damageMode + "' for barrel data");
            }

            if (damageMode.equalsIgnoreCase("all"))
            {
                gunData.gunBarrelData.barrelDamageMode = BarrelDamageMode.ALL;
            }
            else if (damageMode.equalsIgnoreCase("individual"))
            {
                gunData.gunBarrelData.barrelDamageMode = BarrelDamageMode.INDIVIDUAL;
            }
            else
            {
                throw new IllegalArgumentException("Unknown damage mode '" + damageMode + "' for barrel data");
            }

            List<GunBarrel> barrelList = new ArrayList();
            for (JsonElement barrelElement : points)
            {
                if (barrelElement.isJsonObject())
                {
                    JsonObject barrelPointData = barrelElement.getAsJsonObject();
                    ensureValuesExist(barrelPointData, "name", "index", "pos");

                    String barrelName = barrelPointData.getAsJsonPrimitive("name").getAsString();
                    int index = barrelPointData.getAsJsonPrimitive("index").getAsInt();
                    Pos pos = JsonConverterPos.fromJson(barrelJsonData.get("pos"));

                    GunBarrel gunBarrel = new GunBarrel();
                    gunBarrel.index = index;
                    gunBarrel.name = barrelName;
                    gunBarrel.pos = pos;

                    if (barrelPointData.has("group"))
                    {
                        gunBarrel.group = barrelPointData.getAsJsonPrimitive("group").getAsInt();
                    }

                    barrelList.add(gunBarrel);
                }
                else
                {
                    throw new IllegalArgumentException("Unknown barrel data structure '" + barrelElement + "' for barrel data");
                }
            }

            if (barrelList.isEmpty())
            {
                throw new IllegalArgumentException("Failed to provide barrel end points for barrel data");
            }

            Collections.sort(barrelList, Comparator.comparingInt(a -> a.index));
            gunData.gunBarrelData.gunBarrels = barrelList.stream().toArray(GunBarrel[]::new);
        }

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
