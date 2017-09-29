package com.builtbroken.armory.json.damage;

import com.builtbroken.armory.data.damage.physics.DamageForce;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 9/28/2017.
 */
public class DamageJsonProcessorForce extends DamageJsonProcessor<DamageForce>
{
    @Override
    public DamageForce process(JsonElement element)
    {
        JsonObject damageObject = element.getAsJsonObject();
        ensureValuesExist(damageObject, "power");

        return new DamageForce(this, damageObject.get("power").getAsFloat());
    }
}
