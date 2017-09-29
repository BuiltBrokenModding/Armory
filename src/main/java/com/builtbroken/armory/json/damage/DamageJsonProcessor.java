package com.builtbroken.armory.json.damage;

import com.builtbroken.armory.Armory;
import com.builtbroken.armory.data.damage.DamageData;
import com.builtbroken.armory.data.damage.simple.DamageSimple;
import com.builtbroken.mc.framework.json.processors.JsonProcessor;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.HashMap;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 3/30/2017.
 */
@Deprecated //Will be switched over to either a new setup or a JSON converter instead
public class DamageJsonProcessor<D extends DamageData> extends JsonProcessor<D>
{
    public HashMap<String, DamageJsonProcessor> processors = new HashMap();

    public static DamageJsonProcessor processor = new DamageJsonProcessor();

    public DamageJsonProcessor()
    {
        super();
    }

    public DamageJsonProcessor(Class<D> clazz)
    {
        super(clazz);
    }

    static
    {
        processor.processors.put("aoe", new DamageJsonProcessorAOE());
        processor.processors.put("blast", new DamageJsonProcessorBlast());
        processor.processors.put("potion", new DamageJsonProcessorPotion());
        processor.processors.put("force", new DamageJsonProcessorForce());
        processor.processors.put("delay", new DamageJsonProcessorDelay());
    }

    @Override
    public String getMod()
    {
        return Armory.DOMAIN;
    }

    @Override
    public String getJsonKey()
    {
        return "damageData";
    }

    @Override
    public String getLoadOrder()
    {
        return null;
    }

    @Override
    public D process(JsonElement element)
    {
        JsonObject damageObject = element.getAsJsonObject();
        ensureValuesExist(damageObject, "type");

        final String source = damageObject.get("type").getAsString().toLowerCase();
        if (processors.containsKey(source))
        {
            return (D) processors.get(source).process(element);
        }
        else
        {
            ensureValuesExist(damageObject, "value");
            float damage = damageObject.getAsJsonPrimitive("value").getAsFloat();
            return (D) new DamageSimple(this, source, damage);
        }
    }
}
