package com.builtbroken.armory.json.processor;

import com.builtbroken.armory.data.ArmoryDataHandler;
import com.builtbroken.armory.data.ammo.AmmoType;
import com.builtbroken.armory.data.ammo.ClipData;
import com.builtbroken.armory.data.ammo.ClipTypes;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 11/16/2016.
 */
public class ClipJsonProcessor extends ArmoryEntryJsonProcessor<ClipData>
{
    public ClipJsonProcessor()
    {
        super("clip");
    }

    @Override
    public String getJsonKey()
    {
        return "clip";
    }

    @Override
    public String getLoadOrder()
    {
        return "after:ammoType";
    }

    @Override
    public ClipData process(JsonElement element)
    {
        final JsonObject object = element.getAsJsonObject();
        ensureValuesExist(object, "id", "name", "type", "maxAmmo", "ammo");

        String name = object.get("name").getAsString();
        String ammo = object.get("ammo").getAsString();
        String id = object.get("id").getAsString();

        JsonPrimitive clipTypeValue = object.getAsJsonPrimitive("type");
        ClipTypes clipType;
        if (clipTypeValue.isString())
        {
            clipType = ClipTypes.get(clipTypeValue.getAsString());
        }
        else
        {
            clipType = ClipTypes.get(clipTypeValue.getAsInt());
        }

        int maxAmmo = object.getAsJsonPrimitive("maxAmmo").getAsInt();
        ClipData data = new ClipData(id, name, clipType, (AmmoType) ArmoryDataHandler.INSTANCE.get("ammoType").get(ammo), maxAmmo);
        processExtraData(object, data);
        return data;
    }
}
