package com.builtbroken.armory.json.converter;

import com.builtbroken.armory.data.damage.DamageData;
import com.builtbroken.armory.json.damage.DamageJsonProcessor;
import com.builtbroken.mc.framework.json.conversion.JsonConverter;
import com.google.gson.JsonElement;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 9/27/2017.
 */
public class JsonConverterDamage extends JsonConverter<DamageData>
{
    public JsonConverterDamage()
    {
        super("damageData");
    }

    @Override
    public DamageData convert(JsonElement element, String... args)
    {
        return DamageJsonProcessor.processor.process(element);
    }

    @Override
    public JsonElement build(String type, Object data, String... args)
    {
        return null;
    }
}
