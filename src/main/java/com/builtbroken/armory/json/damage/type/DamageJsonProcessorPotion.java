package com.builtbroken.armory.json.damage.type;

import com.builtbroken.armory.data.damage.effect.DamagePotion;
import com.builtbroken.armory.json.damage.DamageTypeJsonProcessor;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 9/27/2017.
 */
public class DamageJsonProcessorPotion extends DamageTypeJsonProcessor<DamagePotion>
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

        return damagePotion;
    }

    @Override
    public JsonElement build(String type, Object data, String... args)
    {
        if(data instanceof DamagePotion)
        {
            JsonObject object = new JsonObject();

            object.add("potion", new JsonPrimitive(((DamagePotion) data).potionName));

            return object;
        }
        return null;
    }
}
