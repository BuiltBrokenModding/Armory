package com.builtbroken.armory.json.graphics;

import com.builtbroken.armory.Armory;
import com.builtbroken.armory.client.data.ModelData;
import com.builtbroken.mc.lib.json.processors.JsonProcessor;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 11/22/2016.
 */
public class ModelJsonProcessor extends JsonProcessor<ModelData>
{
    @Override
    public String getMod()
    {
        return Armory.DOMAIN;
    }

    @Override
    public String getJsonKey()
    {
        return "model";
    }

    @Override
    public String getLoadOrder()
    {
        return null;
    }

    @Override
    public ModelData process(JsonElement element)
    {
        final JsonObject object = element.getAsJsonObject();
        ensureValuesExist(object, "domain", "name", "key");

        String domain = object.get("domain").getAsString();
        String name = object.get("name").getAsString();
        String key = object.get("key").getAsString();

        return new ModelData(key, domain, name);
    }
}
