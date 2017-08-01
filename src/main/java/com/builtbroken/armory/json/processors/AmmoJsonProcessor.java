package com.builtbroken.armory.json.processors;

import com.builtbroken.armory.Armory;
import com.builtbroken.armory.api.ArmoryAPI;
import com.builtbroken.armory.data.ArmoryDataHandler;
import com.builtbroken.armory.data.ammo.AmmoData;
import com.builtbroken.armory.data.ammo.AmmoType;
import com.builtbroken.armory.data.damage.DamageData;
import com.builtbroken.armory.json.ArmoryEntryJsonProcessor;
import com.builtbroken.armory.json.damage.DamageJsonProcessor;
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
public class AmmoJsonProcessor extends ArmoryEntryJsonProcessor<AmmoData>
{
    protected final JsonProcessorInjectionMap keyHandler;
    protected final DebugPrinter debugPrinter;

    public AmmoJsonProcessor()
    {
        super(ArmoryAPI.AMMO_ID);
        keyHandler = new JsonProcessorInjectionMap(AmmoData.class);
        debugPrinter = JsonContentLoader.INSTANCE != null ? JsonContentLoader.INSTANCE.debug : new DebugPrinter(LogManager.getLogger());
    }

    @Override
    public String getLoadOrder()
    {
        return "after:" + ArmoryAPI.AMMO_TYPE_ID;
    }

    @Override
    public AmmoData process(JsonElement element)
    {
        debugPrinter.start("AmmoProcessor", "Processing entry", Engine.runningAsDev);

        final JsonObject ammoJsonObject = element.getAsJsonObject();
        ensureValuesExist(ammoJsonObject, "id", "name", "ammoType");

        //Get common data
        String id = ammoJsonObject.get("id").getAsString();
        String name = ammoJsonObject.get("name").getAsString();

        //Get ammo type
        String ammoTypeString = ammoJsonObject.get("ammoType").getAsString();
        AmmoType ammoType = (AmmoType) ArmoryDataHandler.INSTANCE.get("ammoType").get(ammoTypeString);

        debugPrinter.log("Name: " + name);
        debugPrinter.log("ID: " + id);
        debugPrinter.log("Type: " + ammoType);

        //Create object
        AmmoData ammoData = new AmmoData(this, id, name, ammoType);


        boolean damageDetected = false;
        for (Map.Entry<String, JsonElement> entry : ammoJsonObject.entrySet())
        {
            if (keyHandler.handle(ammoData, entry.getKey().toLowerCase(), entry.getValue()))
            {
                debugPrinter.log("Injected Key: " + entry.getKey());
            }
            //Load damage data
            else if (entry.getKey().startsWith("damage"))
            {
                DamageData damageData = DamageJsonProcessor.processor.process(entry.getValue());
                debugPrinter.log("Damage: " + damageData);
                if (damageData != null)
                {
                    ammoData.damageData.add(damageData);
                    damageDetected = true;
                }
            }
            else if (entry.getKey().startsWith("droppedItem"))
            {
                String itemKey = entry.getValue().getAsJsonPrimitive().getAsString();
                ammoData.droppedItemData.add(itemKey);
                debugPrinter.log("Dropped Item: " + itemKey);
            }
        }

        if (!damageDetected && Armory.INSTANCE != null)
        {
            debugPrinter.error("No damage type was detected in ammo data, this may cause unexpected behavior. Data: " + ammoData);
        }
        //Process shared data
        processExtraData(ammoJsonObject, ammoData);

        debugPrinter.end("Done...");
        return ammoData;
    }
}
