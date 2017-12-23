package com.builtbroken.armory.json.processors;

import com.builtbroken.armory.api.ArmoryAPI;
import com.builtbroken.armory.data.ArmoryDataHandler;
import com.builtbroken.armory.data.ranged.GunData;
import com.builtbroken.armory.data.sentry.SentryData;
import com.builtbroken.armory.json.ArmoryEntryJsonProcessor;
import com.builtbroken.mc.core.Engine;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.Map;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 11/16/2016.
 */
public class SentryJsonProcessor extends ArmoryEntryJsonProcessor<SentryData>
{
    public SentryJsonProcessor()
    {
        super(ArmoryAPI.SENTRY_ID, SentryData.class);
    }

    @Override
    public String getLoadOrder()
    {
        return "after:" + ArmoryAPI.GUN_ID;
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
}
