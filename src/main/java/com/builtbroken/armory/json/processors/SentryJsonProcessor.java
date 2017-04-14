package com.builtbroken.armory.json.processors;

import com.builtbroken.armory.data.ArmoryDataHandler;
import com.builtbroken.armory.data.ammo.AmmoData;
import com.builtbroken.armory.data.ranged.GunData;
import com.builtbroken.armory.data.sentry.SentryData;
import com.builtbroken.armory.json.ArmoryEntryJsonProcessor;
import com.builtbroken.jlib.lang.DebugPrinter;
import com.builtbroken.mc.core.Engine;
import com.builtbroken.mc.lib.json.JsonContentLoader;
import com.builtbroken.mc.lib.json.loading.JsonProcessorInjectionMap;
import com.builtbroken.mc.lib.json.override.IModifableJson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.apache.logging.log4j.LogManager;

import java.util.Map;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 11/16/2016.
 */
public class SentryJsonProcessor extends ArmoryEntryJsonProcessor<SentryData> implements IModifableJson<SentryData>
{
    protected final JsonProcessorInjectionMap keyHandler;
    protected final DebugPrinter debugPrinter;

    public SentryJsonProcessor()
    {
        super("sentry");
        keyHandler = new JsonProcessorInjectionMap(SentryData.class);
        debugPrinter = JsonContentLoader.INSTANCE != null ? JsonContentLoader.INSTANCE.debug : new DebugPrinter(LogManager.getLogger());
    }

    @Override
    public String getLoadOrder()
    {
        return "after:gun";
    }

    @Override
    public SentryData process(JsonElement element)
    {
        debugPrinter.start("SentryProcessor", "Processing entry", Engine.runningAsDev);

        final JsonObject sentryJsonObject = element.getAsJsonObject();
        ensureValuesExist(sentryJsonObject, "id", "name", "gunID");

        String gunID = sentryJsonObject.get("gunID").getAsString();
        String id = sentryJsonObject.getAsJsonPrimitive("id").getAsString();
        String name = sentryJsonObject.get("name").getAsString();

        debugPrinter.log("Name: " + name);
        debugPrinter.log("ID: " + id);
        debugPrinter.log("Gun: " + gunID);

        SentryData sentryData = new SentryData(this, id, name);

        //Loading gun
        Object gunData = ArmoryDataHandler.INSTANCE.get("gun").get(gunID);
        if (gunData == null)
        {
            throw new IllegalArgumentException("Failed to location gun by ID[" + gunID + "]");
        }
        else if (!(gunData instanceof GunData))
        {
            throw new IllegalArgumentException("Failed to get gun by ID[" + gunID + "] due to return not being a gun data object, this is a bug");
        }
        sentryData.setGunData((GunData) gunData);

        //Load ammo data
        if (sentryJsonObject.has("ammoData"))
        {
            String ammoKey = sentryJsonObject.get("ammoData").getAsString();
            debugPrinter.log("AmmoData: " + ammoKey);

            Object ammoData = ArmoryDataHandler.INSTANCE.get("ammo").get(ammoKey);
            if (ammoData == null)
            {
                throw new IllegalArgumentException("Failed to location ammo data by ID[" + ammoKey + "]");
            }
            else if (!(ammoData instanceof AmmoData))
            {
                throw new IllegalArgumentException("Failed to get ammo data by ID[" + ammoKey + "] due to return not being an ammo data object, this is a bug");
            }
            sentryData.setAmmoData((AmmoData) ammoData);
        }

        //Call to process injection tags
        for (Map.Entry<String, JsonElement> entry : sentryJsonObject.entrySet())
        {
            if (keyHandler.handle(sentryData, entry.getKey().toLowerCase(), entry.getValue()))
            {
                debugPrinter.log("Injected Key: " + entry.getKey());
            }
        }

        //Process shared data
        processExtraData(sentryJsonObject, sentryData);

        debugPrinter.end("Done...");
        return sentryData;
    }

    @Override
    public void addData(String key, JsonElement data, SentryData generatedObject)
    {
        if (keyHandler.handle(generatedObject, key, data, true, "add"))
        {
            debugPrinter.log("Injected Add Override >> Key: " + key + " Data: " + data);
        }
    }

    @Override
    public void removeData(String key, SentryData generatedObject)
    {
        if (keyHandler.handle(generatedObject, key, null, true, "remove"))
        {
            debugPrinter.log("Injected Remove Override >> Key: " + key);
        }
    }

    @Override
    public void replaceData(String key, JsonElement data, SentryData generatedObject)
    {
        if (keyHandler.handle(generatedObject, key, data, true, "replace"))
        {
            debugPrinter.log("Injected Replacement Override >> Key: " + key + " Data: " + data);
        }
    }
}
