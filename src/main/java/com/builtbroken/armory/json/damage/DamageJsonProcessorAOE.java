package com.builtbroken.armory.json.damage;

import com.builtbroken.armory.data.damage.DamageAOE;
import com.builtbroken.armory.data.damage.DamageData;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 3/30/2017.
 */
public class DamageJsonProcessorAOE extends DamageJsonProcessor
{
    @Override
    public DamageData process(JsonElement element)
    {
        JsonObject damageObject = element.getAsJsonObject();
        ensureValuesExist(damageObject, "damage", "range");
        DamageData damageData = processor.process(damageObject.get("damage"));
        float range = damageObject.get("range").getAsJsonPrimitive().getAsFloat();
        return new DamageAOE(this, damageData, range);
    }
}
