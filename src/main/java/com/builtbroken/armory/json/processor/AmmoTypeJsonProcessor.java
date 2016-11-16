package com.builtbroken.armory.json.processor;

import com.builtbroken.armory.data.ammo.AmmoType;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
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
    public String getSortingString()
    {
        return "ammoType";
    }

    @Override
    public AmmoType process(JsonElement element)
    {
        JsonObject object = element.getAsJsonObject();
        JsonObject blockData = object.get("ammoType").getAsJsonObject();
        return new AmmoType(blockData.get("name").getAsString());
    }
}
