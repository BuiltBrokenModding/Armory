package com.builtbroken.armory.json.damage;

import com.builtbroken.armory.Armory;
import com.builtbroken.armory.data.damage.DamageData;
import com.builtbroken.armory.data.damage.DamageSimple;
import com.builtbroken.mc.lib.json.processors.JsonProcessor;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.HashMap;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 3/30/2017.
 */
public class DamageJsonProcessor extends JsonProcessor<DamageData>
{
    public HashMap<String, DamageJsonProcessor> processors = new HashMap();

    public static DamageJsonProcessor processor = new DamageJsonProcessor();

    static
    {
        processor.processors.put("aoe", new DamageJsonProcessorAOE());
        processor.processors.put("blast", new DamageJsonProcessorBlast());
    }

    @Override
    public String getMod()
    {
        return Armory.DOMAIN;
    }

    @Override
    public String getJsonKey()
    {
        return "damage";
    }

    @Override
    public String getLoadOrder()
    {
        return null;
    }

    @Override
    public DamageData process(JsonElement element)
    {
        JsonObject damageObject = element.getAsJsonObject();
        ensureValuesExist(damageObject, "type");
        final String source = damageObject.get("type").getAsString().toLowerCase();
        if (processors.containsKey(source))
        {
            return processors.get(source).process(element);
        }
        else
        {
            ensureValuesExist(damageObject, "value");
            float damage = damageObject.getAsJsonPrimitive("value").getAsFloat();
            return new DamageSimple(this, source, damage);
        }
    }
}
