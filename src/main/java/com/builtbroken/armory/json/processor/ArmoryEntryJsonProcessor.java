package com.builtbroken.armory.json.processor;

import com.builtbroken.armory.Armory;
import com.builtbroken.armory.data.ArmoryEntry;
import com.builtbroken.mc.lib.json.processors.JsonProcessor;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * Handles processing a single type of item
 *
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 11/15/2016.
 */
public abstract class ArmoryEntryJsonProcessor<E extends ArmoryEntry> extends JsonProcessor<E>
{
    /** Key used to load data from a json file */
    public final String jsonKey;

    public ArmoryEntryJsonProcessor(String jsonKey)
    {
        this.jsonKey = jsonKey;
    }

    @Override
    public String getMod()
    {
        return Armory.DOMAIN;
    }

    @Override
    public String getJsonKey()
    {
        return jsonKey;
    }

    public abstract E process(JsonElement element);

    /**
     * Called to process common shared data
     *
     * @param object
     * @param e
     * @return
     */
    public E processExtraData(JsonObject object, E e)
    {
        final JsonElement mass = object.get("mass");
        if (mass != null)
        {
            if (mass.isJsonPrimitive())
            {
                e.mass = mass.getAsInt();
            }
            else
            {
                throw new IllegalArgumentException("Invalid mass value " + mass + " when reading " + object);
            }
        }

        final JsonElement contentGroup = object.get("contentGroup");
        if (contentGroup != null)
        {
            if (contentGroup.isJsonPrimitive())
            {
                e.contentGroup = mass.getAsString();
            }
            else
            {
                throw new IllegalArgumentException("Invalid content group value " + contentGroup + " when reading " + object);
            }
        }

        final JsonElement translation = object.get("translationKey");
        if (translation != null)
        {
            if (translation.isJsonPrimitive())
            {
                e.translationKey = jsonKey + "." + (e.contentGroup != null && !e.contentGroup.isEmpty() ? e.contentGroup + ":" : "") + translation.getAsString();
            }
            else
            {
                throw new IllegalArgumentException("Invalid translation value " + translation + " when reading " + object);
            }
        }
        return e;
    }

    public void ensureValuesExist(JsonObject object, String... values)
    {
        for (String value : values)
        {
            if (!object.has("name"))
            {
                throw new IllegalArgumentException("File is missing " + value + " value " + object);
            }
        }
    }
}
