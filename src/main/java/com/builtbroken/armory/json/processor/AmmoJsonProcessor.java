package com.builtbroken.armory.json.processor;

import com.builtbroken.armory.Armory;
import com.builtbroken.armory.data.ArmoryDataHandler;
import com.builtbroken.armory.data.ammo.AmmoData;
import com.builtbroken.armory.data.ammo.AmmoType;
import com.builtbroken.armory.data.damage.DamageAOE;
import com.builtbroken.armory.data.damage.DamageData;
import com.builtbroken.armory.data.damage.DamageSimple;
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
                data.damageData.add(loadDamageData(entry.getValue().getAsJsonObject()));
                damageDetected = true;
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

    /**
     * Called to load damage data from json
     *
     * @param damageObject
     * @return
     */
    protected DamageData loadDamageData(JsonObject damageObject)
    {
        ensureValuesExist(damageObject, "type");
        final String source = damageObject.get("type").getAsString();

        //TODO replace with json loaders to allow more damage types
        if (source.equalsIgnoreCase("aoe"))
        {
            ensureValuesExist(damageObject, "damage", "range");
            DamageData damageData = loadDamageData(damageObject.get("damage").getAsJsonObject());
            float range = damageObject.get("range").getAsJsonPrimitive().getAsFloat();
            return new DamageAOE(damageData, range);
        }
        else
        {
            ensureValuesExist(damageObject, "value");
            float damage = damageObject.getAsJsonPrimitive("value").getAsFloat();
            return new DamageSimple(source, damage);
        }
    }
}
