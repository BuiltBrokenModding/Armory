package com.builtbroken.armory.json.processors;

import com.builtbroken.armory.Armory;
import com.builtbroken.armory.data.ArmoryDataHandler;
import com.builtbroken.armory.data.ammo.AmmoData;
import com.builtbroken.armory.data.ammo.AmmoType;
import com.builtbroken.armory.data.damage.DamageData;
import com.builtbroken.armory.json.ArmoryEntryJsonProcessor;
import com.builtbroken.armory.json.damage.DamageJsonProcessor;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.Map;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 11/16/2016.
 */
public class AmmoJsonProcessor extends ArmoryEntryJsonProcessor<AmmoData>
{
    public AmmoJsonProcessor()
    {
        super("ammo");
    }

    @Override
    public String getLoadOrder()
    {
        return "after:ammoType";
    }

    @Override
    public AmmoData process(JsonElement element)
    {
        final JsonObject object = element.getAsJsonObject();
        ensureValuesExist(object, "id", "name", "ammoType");

        //Get common data
        String id = object.get("id").getAsString();
        String name = object.get("name").getAsString();

        //Get ammo type
        String ammoTypeString = object.get("ammoType").getAsString();
        AmmoType ammoType = (AmmoType) ArmoryDataHandler.INSTANCE.get("ammoType").get(ammoTypeString);

        //Get velocity
        float velocity = -1;
        if (object.has("velocity"))
        {
            velocity = object.getAsJsonPrimitive("velocity").getAsFloat();
        }

        //Create object
        AmmoData data = new AmmoData(this, id, name, ammoType, velocity);

        //Load damage data
        boolean damageDetected = false;
        for (Map.Entry<String, JsonElement> entry : object.entrySet())
        {
            if (entry.getKey().startsWith("damage"))
            {
                DamageData damageData = DamageJsonProcessor.processor.process(entry.getValue());
                if(damageData != null)
                {
                    data.damageData.add(damageData);
                    damageDetected = true;
                }
            }
        }

        if (!damageDetected && Armory.INSTANCE != null)
        {
            Armory.INSTANCE.logger().error("No damage type was detected for ammo " + data + "\n this may cause unexpected behavior.");
        }

        //Process shared data
        processExtraData(object, data);

        return data;
    }
}
