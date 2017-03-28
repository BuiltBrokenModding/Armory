package com.builtbroken.armory.json.processor;

import com.builtbroken.armory.data.ArmoryDataHandler;
import com.builtbroken.armory.data.ammo.AmmoData;
import com.builtbroken.armory.data.ammo.AmmoType;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

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
        ensureValuesExist(object, "id", "name", "type", "damage");

        //Get common data
        String id = object.get("id").getAsString();
        String name = object.get("name").getAsString();

        //Get ammo type
        String ammoTypeString = object.get("AmmoType").getAsString();
        AmmoType ammoType = (AmmoType) ArmoryDataHandler.INSTANCE.get("ammoType").get(ammoTypeString);

        //Get damage
        JsonObject damageObject = object.get("damage").getAsJsonObject();
        ensureValuesExist(damageObject, "value", "type");

        String source = damageObject.get("type").getAsString();
        float damage = damageObject.getAsJsonPrimitive("value").getAsFloat();

        //Get velocity
        float velocity = -1;
        if (object.has("velocity"))
        {
            velocity = object.getAsJsonPrimitive("velocity").getAsFloat();
        }

        //Create object
        AmmoData data = new AmmoData(this, id, name, ammoType, source, damage, velocity);

        //Process shared data
        processExtraData(object, data);


        return data;
    }
}
