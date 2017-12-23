package com.builtbroken.armory.json.damage.type;

import com.builtbroken.armory.data.damage.effect.DamageEntityEffect;
import com.builtbroken.armory.json.damage.DamageTypeJsonProcessor;
import com.builtbroken.mc.framework.json.conversion.data.mc.JsonConverterNBT;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 9/30/2017.
 */
public class DamageJsonProcessorEntityEffect extends DamageTypeJsonProcessor<DamageEntityEffect>
{
    public DamageJsonProcessorEntityEffect()
    {
        super(DamageEntityEffect.class);
    }

    @Override
    public DamageEntityEffect process(JsonElement element)
    {
        JsonObject damageObject = element.getAsJsonObject();
        ensureValuesExist(damageObject, "effectID");

        String id = damageObject.get("effectID").getAsString();
        DamageEntityEffect damagePotion = new DamageEntityEffect(this, id);

        //handle injection
        processAdditionalKeys(damagePotion, damageObject);

        return damagePotion;
    }

    @Override
    public JsonElement build(String type, Object data, String... args)
    {
        if (data instanceof DamageEntityEffect)
        {
            JsonObject object = new JsonObject();

            object.add("effectID", new JsonPrimitive(((DamageEntityEffect) data).effectID));
            if (((DamageEntityEffect) data).effectData != null)
            {
                object.add("data", JsonConverterNBT.toJson(((DamageEntityEffect) data).effectData));
            }

            return object;
        }
        return null;
    }
}
