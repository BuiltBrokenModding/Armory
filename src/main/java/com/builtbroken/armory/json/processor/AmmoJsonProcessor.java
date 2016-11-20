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
        ensureValuesExist(object, "id", "name", "type", "source", "damage");

        String id = object.get("id").getAsString();
        String name = object.get("name").getAsString();
        String type = object.get("type").getAsString();
        String source = object.get("source").getAsString();
        float damage = object.getAsJsonPrimitive("damage").getAsFloat();
        AmmoData data = new AmmoData(id, name, (AmmoType) ArmoryDataHandler.INSTANCE.get("ammoType").get(type), source, damage);
        processExtraData(object, data);
        return data;
    }
}
