package com.builtbroken.armory.json.damage;

import com.builtbroken.armory.Armory;
import com.builtbroken.armory.data.damage.DamageData;
import com.builtbroken.armory.data.damage.simple.DamageSimple;
import com.builtbroken.armory.json.damage.type.*;
import com.builtbroken.mc.framework.json.conversion.IJsonConverter;
import com.builtbroken.mc.framework.json.imp.IJsonProcessor;
import com.builtbroken.mc.framework.json.processors.JsonProcessor;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 3/30/2017.
 */
public final class DamageJsonProcessor extends JsonProcessor<DamageData> implements IJsonConverter<DamageData>
{
    public HashMap<String, DamageTypeJsonProcessor> processors = new HashMap();

    public static DamageJsonProcessor INSTANCE = new DamageJsonProcessor();

    private ArrayList<String> keys = new ArrayList();

    private DamageJsonProcessor()
    {
        super();
        keys.add("damage");
        keys.add("damagedata");
    }

    static
    {
        INSTANCE.processors.put("aoe", new DamageJsonProcessorAOE());
        INSTANCE.processors.put("blast", new DamageJsonProcessorBlast());
        INSTANCE.processors.put("potion", new DamageJsonProcessorPotion());
        INSTANCE.processors.put("force", new DamageJsonProcessorForce());
        INSTANCE.processors.put("delay", new DamageJsonProcessorDelay());
        INSTANCE.processors.put("effect", new DamageJsonProcessorEntityEffect());
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
            return processors.get(source).convert(element);
        }
        else
        {
            ensureValuesExist(damageObject, "value");
            float damage = damageObject.getAsJsonPrimitive("value").getAsFloat();
            return new DamageSimple(this, source, damage);
        }
    }

    @Override
    public DamageData convert(JsonElement element, String... args)
    {
        return process(element);
    }

    @Override
    public JsonElement build(String type, Object data, String... args)
    {
        if (data instanceof DamageSimple)
        {
            JsonObject object = new JsonObject();
            object.add("type", new JsonPrimitive(((DamageSimple) data).damageName));
            object.add("value", new JsonPrimitive(((DamageSimple) data).damage));
            return object;
        }
        else if (data instanceof DamageData)
        {
            IJsonProcessor processor = ((DamageData) data).processor;
            if (processor instanceof IJsonConverter)
            {
                return ((IJsonConverter) processor).build(type, data, args);
            }
        }
        return null;
    }

    @Override
    public List<String> getKeys()
    {
        return keys;
    }
}
