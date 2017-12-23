package com.builtbroken.armory.json.damage;

import com.builtbroken.armory.data.damage.DamageData;
import com.builtbroken.mc.framework.json.conversion.IJsonConverter;
import com.builtbroken.mc.framework.json.processors.JsonProcessor;
import com.google.gson.JsonElement;

import java.util.List;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 3/30/2017.
 */
public abstract class DamageTypeJsonProcessor<D extends DamageData> extends JsonProcessor<D> implements IJsonConverter<D>
{
    public DamageTypeJsonProcessor(Class<D> clazz)
    {
        super(clazz);
    }

    @Override
    public String getMod()
    {
        return null;
    }

    @Override
    public String getJsonKey()
    {
        return null;
    }

    @Override
    public String getLoadOrder()
    {
        return null;
    }

    @Override
    public List<String> getKeys()
    {
        return null;
    }

    @Override
    public D convert(JsonElement element, String... args)
    {
        return process(element);
    }
}
