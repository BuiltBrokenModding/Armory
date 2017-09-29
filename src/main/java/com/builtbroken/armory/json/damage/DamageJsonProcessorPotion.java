package com.builtbroken.armory.json.damage;

import com.builtbroken.armory.data.damage.effect.DamagePotion;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 3/30/2017.
 */
public class DamageJsonProcessorPotion extends DamageJsonProcessor<DamagePotion>
{
    public DamageJsonProcessorPotion()
    {
        super(DamagePotion.class);
    }

    @Override
    public DamagePotion process(JsonElement element)
    {
        JsonObject damageObject = element.getAsJsonObject();
        ensureValuesExist(damageObject, "potion");

        String id = damageObject.get("potion").getAsString();
        DamagePotion damagePotion = new DamagePotion(this, id);

        //handle injection
        processAdditionalKeys(damagePotion, damageObject);

        return damagePotion;
    }
}
