package com.builtbroken.armory.json.processors;

import com.builtbroken.armory.api.ArmoryAPI;
import com.builtbroken.armory.data.ArmoryDataHandler;
import com.builtbroken.armory.data.ammo.AmmoType;
import com.builtbroken.armory.data.ranged.ThrowableData;
import com.builtbroken.armory.json.ArmoryEntryJsonProcessor;
import com.builtbroken.jlib.lang.DebugPrinter;
import com.builtbroken.mc.core.Engine;
import com.builtbroken.mc.lib.json.JsonContentLoader;
import com.builtbroken.mc.lib.json.loading.JsonProcessorInjectionMap;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.apache.logging.log4j.LogManager;

import java.util.Map;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 11/16/2016.
 */
public class ThrownJsonProcessor extends ArmoryEntryJsonProcessor<ThrowableData>
{
    protected final JsonProcessorInjectionMap keyHandler;
    protected final DebugPrinter debugPrinter;

    public ThrownJsonProcessor()
    {
        super(ArmoryAPI.THROWABLE_WEAPON_ID);
        keyHandler = new JsonProcessorInjectionMap(ThrowableData.class);
        debugPrinter = JsonContentLoader.INSTANCE != null ? JsonContentLoader.INSTANCE.debug : new DebugPrinter(LogManager.getLogger());
    }

    @Override
    public String getLoadOrder()
    {
        return "after:ammo";
    }

    @Override
    public ThrowableData process(JsonElement element)
    {
        debugPrinter.start("ThrowableWeaponProcessor", "Processing entry", Engine.runningAsDev);

        final JsonObject throwableJsonObject = element.getAsJsonObject();
        ensureValuesExist(throwableJsonObject, "id", "name", "ammoType");

        //Get common data
        String id = throwableJsonObject.get("id").getAsString();
        String name = throwableJsonObject.get("name").getAsString();

        //Get ammo type
        String ammoTypeString = throwableJsonObject.get("ammoType").getAsString();
        AmmoType ammoType = (AmmoType) ArmoryDataHandler.INSTANCE.get("ammoType").get(ammoTypeString);

        debugPrinter.log("Name: " + name);
        debugPrinter.log("ID: " + id);
        debugPrinter.log("Type: " + ammoType);

        ThrowableData throwableData = new ThrowableData(this, id, name, ammoType);

        //Inject data
        for (Map.Entry<String, JsonElement> entry : throwableJsonObject.entrySet())
        {
            if (keyHandler.handle(throwableData, entry.getKey().toLowerCase(), entry.getValue()))
            {
                debugPrinter.log("Injected Key: " + entry.getKey());
            }
        }

        //Process shared data
        processExtraData(throwableJsonObject, throwableData);

        debugPrinter.end("Done...");
        return throwableData;
    }
}
