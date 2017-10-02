package com.builtbroken.armory.json.damage;

import com.builtbroken.armory.data.damage.effect.DamageEntityEffect;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 9/30/2017.
 */
public class DamageJsonProcessorEntityEffect extends DamageJsonProcessor<DamageEntityEffect>
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
}
