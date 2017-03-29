package com.builtbroken.armory.json.processor;

import com.builtbroken.armory.data.ArmoryDataHandler;
import com.builtbroken.armory.data.ammo.AmmoType;
import com.builtbroken.armory.data.clip.ClipData;
import com.builtbroken.mc.api.data.weapon.ReloadType;
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
        ensureValuesExist(object, "id", "name", "reloadType", "maxAmmo", "ammoType");

        //Load common data
        String id = object.get("id").getAsString();
        String name = object.get("name").getAsString();

        //Get ammo Type
        String ammo = object.get("ammoType").getAsString();
        AmmoType ammoType = (AmmoType) ArmoryDataHandler.INSTANCE.get("ammoType").get(ammo);

        //Get reload type
        JsonPrimitive clipTypeValue = object.getAsJsonPrimitive("reloadType");
        ReloadType reloadType = ReloadType.get(clipTypeValue.getAsString());

        //Get max ammo
        int maxAmmo = Math.max(1, object.getAsJsonPrimitive("maxAmmo").getAsInt());

        //Create object
        ClipData data = new ClipData(this, id, name, reloadType, ammoType, maxAmmo);

        //Load shared data
        processExtraData(object, data);

        return data;
    }
}
