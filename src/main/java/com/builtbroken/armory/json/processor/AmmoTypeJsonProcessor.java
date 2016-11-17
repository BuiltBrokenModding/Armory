package com.builtbroken.armory.json.processor;

import com.builtbroken.armory.data.ammo.AmmoType;
import com.builtbroken.armory.data.projectiles.EnumProjectileTypes;
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
        super("ammo");
    }

    @Override
    public String getJsonKey()
    {
        return "ammoType";
    }

    @Override
    public AmmoType process(JsonElement element)
    {
        JsonObject object = element.getAsJsonObject();
        int type = object.getAsJsonPrimitive("type").getAsInt();
        if (type < 0 || type >= EnumProjectileTypes.values().length)
        {
            throw new IllegalArgumentException("Invalid projectile type " + type + " while reading " + element);
        }
        return new AmmoType(object.get("name").getAsString(), EnumProjectileTypes.get(type));
    }
}
