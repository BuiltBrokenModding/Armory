package com.builtbroken.armory.json.processors;

import com.builtbroken.armory.data.ammo.AmmoType;
import com.builtbroken.armory.data.projectiles.EnumProjectileTypes;
import com.builtbroken.armory.json.ArmoryEntryJsonProcessor;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * Loads ammo types from json
 *
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 11/16/2016.
 */
public class AmmoTypeJsonProcessor extends ArmoryEntryJsonProcessor<AmmoType>
{
    public AmmoTypeJsonProcessor()
    {
        super("ammoType");
    }

    @Override
    public String getLoadOrder()
    {
        return null;
    }

    @Override
    public AmmoType process(JsonElement element)
    {
        JsonObject object = element.getAsJsonObject();
        ensureValuesExist(object, "id", "name", "projectileType");

        //Load common data
        String id = object.get("id").getAsString();
        String name = object.get("name").getAsString();

        //Get projectile type
        String projectileType = object.get("projectileType").getAsString();
        EnumProjectileTypes type = EnumProjectileTypes.get(projectileType);

        //Create object
        AmmoType data = new AmmoType(this, id, name, type);

        //Load shared data
        processExtraData(object, data);

        return data;
    }
}
