package com.builtbroken.armory.json.damage;

import com.builtbroken.armory.data.damage.DamageData;
import com.builtbroken.armory.data.damage.delayed.DamageDelayed;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 3/30/2017.
 */
public class DamageJsonProcessorDelay extends DamageJsonProcessor<DamageDelayed>
{
    @Override
    public DamageDelayed process(JsonElement element)
    {
        JsonObject damageObject = element.getAsJsonObject();
        ensureValuesExist(damageObject, "damage", "ticks");

        DamageData damageData = processor.process(damageObject.get("damage"));

        int ticks = damageObject.get("ticks").getAsJsonPrimitive().getAsInt();
        return new DamageDelayed(this, damageData, ticks);
    }
}
