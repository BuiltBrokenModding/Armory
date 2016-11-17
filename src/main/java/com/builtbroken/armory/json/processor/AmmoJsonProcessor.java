package com.builtbroken.armory.json.processor;

import com.builtbroken.armory.data.ArmoryDataHandler;
import com.builtbroken.armory.data.ammo.AmmoData;
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
    public String getJsonKey()
    {
        return "ammo@after:ammoType";
    }

    @Override
    public AmmoData process(JsonElement element)
    {
        final JsonObject blockData = element.getAsJsonObject();
        if (blockData.has("type") && blockData.has("source") && blockData.has("damage"))
        {
            String name = blockData.get("name").getAsString();
            String type = blockData.get("type").getAsString();
            String source = blockData.get("source").getAsString();
            float damage = blockData.getAsJsonPrimitive("damage").getAsFloat();
            return new AmmoData(name, ArmoryDataHandler.getAmmoType(type), source, damage);
        }
        else
        {
            throw new IllegalArgumentException("File is missing key parts " + element);
        }
    }
}
