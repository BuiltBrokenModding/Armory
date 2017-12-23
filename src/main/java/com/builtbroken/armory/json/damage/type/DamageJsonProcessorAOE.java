package com.builtbroken.armory.json.damage.type;

import com.builtbroken.armory.data.damage.area.DamageAOE;
import com.builtbroken.armory.data.damage.DamageData;
import com.builtbroken.armory.json.damage.DamageJsonProcessor;
import com.builtbroken.armory.json.damage.DamageTypeJsonProcessor;
import com.builtbroken.mc.framework.json.loading.JsonLoader;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 3/30/2017.
 */
public class DamageJsonProcessorAOE extends DamageTypeJsonProcessor<DamageAOE>
{
    public DamageJsonProcessorAOE()
    {
        super(DamageAOE.class);
    }

    @Override
    public DamageAOE process(JsonElement element)
    {
        JsonObject damageObject = element.getAsJsonObject();
        ensureValuesExist(damageObject, "damage", "range");

        DamageData damageData = DamageJsonProcessor.INSTANCE.process(damageObject.get("damage"));

        float range = damageObject.get("range").getAsJsonPrimitive().getAsFloat();

        return new DamageAOE(this, damageData, range);
    }

    @Override
    public JsonElement build(String type, Object data, String... args)
    {
        if(data instanceof DamageAOE)
        {
            JsonObject object = new JsonObject();
            object.add("damage", JsonLoader.buildElement("damage", ((DamageAOE) data).damageToApply));
            object.add("range", new JsonPrimitive(((DamageAOE) data).range));
            return object;
        }
        return null;
    }
}
