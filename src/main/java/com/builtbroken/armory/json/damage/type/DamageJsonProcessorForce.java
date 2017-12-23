package com.builtbroken.armory.json.damage.type;

import com.builtbroken.armory.data.damage.physics.DamageForce;
import com.builtbroken.armory.json.damage.DamageTypeJsonProcessor;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 9/28/2017.
 */
public class DamageJsonProcessorForce extends DamageTypeJsonProcessor<DamageForce>
{
    public DamageJsonProcessorForce()
    {
        super(DamageForce.class);
    }

    @Override
    public DamageForce process(JsonElement element)
    {
        JsonObject damageObject = element.getAsJsonObject();
        ensureValuesExist(damageObject, "power");

        return new DamageForce(this, damageObject.get("power").getAsFloat());
    }

    @Override
    public JsonElement build(String type, Object data, String... args)
    {
        if(data instanceof DamageForce)
        {
            JsonObject object = new JsonObject();
            object.add("power", new JsonPrimitive(((DamageForce) data).power));
            return object;
        }
        return null;
    }
}
