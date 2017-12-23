package com.builtbroken.armory.json;

import com.builtbroken.armory.Armory;
import com.builtbroken.armory.data.ArmoryEntry;
import com.builtbroken.mc.core.Engine;
import com.builtbroken.mc.framework.json.imp.IJsonGenObject;
import com.builtbroken.mc.framework.json.loading.JsonLoader;
import com.builtbroken.mc.framework.json.loading.JsonProcessorData;
import com.builtbroken.mc.framework.json.override.IModifableJson;
import com.builtbroken.mc.framework.json.override.JsonOverrideProcessor;
import com.builtbroken.mc.framework.json.processors.JsonProcessor;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

/**
 * Handles processing a single type of item
 *
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 11/15/2016.
 */
public abstract class ArmoryEntryJsonProcessor<E extends ArmoryEntry> extends JsonProcessor<E>  implements IModifableJson<E>
{
    /** Key used to load data from a json file */
    public final String jsonKey;

    public ArmoryEntryJsonProcessor(String jsonKey)
    {
        this.jsonKey = jsonKey;
    }

    public ArmoryEntryJsonProcessor(String jsonKey, Class<E> clazz)
    {
        super(clazz);
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

    /**
     * Called to process common shared data
     *
     * @param object
     * @param e
     * @return
     */
    public E processExtraData(JsonObject object, E e)
    {
        //TODO use JSON injection
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

        final JsonElement creativeTab = object.get("showInCreativeTab");
        if (creativeTab != null)
        {
            if (creativeTab.isJsonPrimitive())
            {
                e.showInCreativeTab = creativeTab.getAsBoolean();
            }
            else
            {
                throw new IllegalArgumentException("Invalid showInCreativeTab value " + creativeTab + " when reading " + object);
            }
        }

        final JsonElement contentGroup = object.get("contentGroup");
        if (contentGroup != null)
        {
            if (contentGroup.isJsonPrimitive())
            {
                e.contentGroup = contentGroup.getAsString();
            }
            else
            {
                throw new IllegalArgumentException("Invalid content group value " + contentGroup + " when reading " + object);
            }
        }

        final JsonElement creativeTabName = object.get("creativeTab");
        if (creativeTabName != null)
        {
            if (creativeTabName.isJsonPrimitive())
            {
                e.creativeTabToUse = creativeTabName.getAsString();
            }
            else
            {
                throw new IllegalArgumentException("Invalid creative tab value " + creativeTabName + " when reading " + object);
            }
        }
        return e;
    }



    @Override
    public boolean addData(String key, JsonElement data, E generatedObject)
    {
        if (keyHandler.handle(generatedObject, key, data, true, "add"))
        {
            debugPrinter.log("Injected Add Override >> Key: " + key + " Data: " + data);
            return true;
        }
        return false;
    }

    @Override
    public boolean removeData(String key, E generatedObject)
    {
        if (keyHandler.handle(generatedObject, key, null, true, "remove"))
        {
            debugPrinter.log("Injected Remove Override >> Key: " + key);
            return true;
        }
        return false;
    }

    @Override
    public boolean replaceData(String key, JsonElement data, E generatedObject)
    {
        if (keyHandler.handle(generatedObject, key, data, true, "replace"))
        {
            debugPrinter.log("Injected Replacement Override >> Key: " + key + " Data: " + data);
            return true;
        }
        return false;
    }

    @Override
    public Object getData(String _key, E object)
    {
        final String key = _key.toLowerCase();
        JsonProcessorData anno = (JsonProcessorData) keyHandler.jsonDataAnnotation.get(key);
        if (anno != null && anno.allowRuntimeChanges())
        {
            //Try getter map first
            if (keyHandler.jsonDataGetters.containsKey(key))
            {
                try
                {
                    Method method = (Method) keyHandler.jsonDataGetters.get(key);
                    method.setAccessible(true);
                    return method.invoke(object);
                }
                catch (IllegalAccessException e)
                {
                    e.printStackTrace();
                }
                catch (InvocationTargetException e)
                {
                    e.printStackTrace();
                }
            }
            //then try fields
            else if (keyHandler.jsonDataFields.containsKey(key))
            {
                try
                {
                    Field field = (Field) keyHandler.jsonDataFields.get(key);
                    field.setAccessible(true);
                    return field.get(object);
                }
                catch (IllegalAccessException e)
                {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    @Override
    public JsonObject getPossibleModificationsAsJson(IJsonGenObject object) //TODO move to prefab
    {
        JsonObject root = new JsonObject();

        JsonObject override = new JsonObject();
        override.add(JsonOverrideProcessor.JSON_PROCESSOR_KEY, new JsonPrimitive(this.getJsonKey()));
        override.add(JsonOverrideProcessor.JSON_CONTENT_KEY, new JsonPrimitive(object.getContentID()));
        override.add(JsonOverrideProcessor.JSON_ACTION_KEY, new JsonPrimitive("edit"));
        override.add(JsonOverrideProcessor.JSON_ENABLE_KEY, new JsonPrimitive("false"));

        JsonObject data = new JsonObject();
        override.add("_note", new JsonPrimitive("Remove _ underscore in front of key to allow it to be read."));
        override.add("_note2", new JsonPrimitive("_ underscore, is added to show all values while allowing selective changes. " +
                "Only change what needs to be changed to avoid old values replacing new values in future updates." +
                "Feel free to remove any value you do not need. It will not effect the load process so long as the minimal data exists. " +
                "Action type, content id, processor id, and data to use. See VoltzEngine's Github Wiki for more information."));

        for (String key : (List<String>)keyHandler.injectionKeys)
        {
            if (keyHandler.jsonDataAnnotation.containsKey(key))
            {
                JsonProcessorData anno = (JsonProcessorData) keyHandler.jsonDataAnnotation.get(key);
                if (anno.allowRuntimeChanges())
                {
                    Object value = null;

                    //Try getter map first
                    if (keyHandler.jsonDataGetters.containsKey(key))
                    {
                        try
                        {
                            Method method = (Method) keyHandler.jsonDataGetters.get(key);
                            method.setAccessible(true);
                            value = method.invoke(object);
                        }
                        catch (Exception e)
                        {
                            Engine.logger().error("ArmoryEntryJsonProcessor: Error while building modification json data for key '" + key + "' for '" + object + "'", e);
                            data.add(key, new JsonPrimitive("Error: Unexpected error invoking getter '" + e.toString() + "' see console for more details."));
                            continue;
                        }
                    }
                    //then try fields
                    else if (keyHandler.jsonDataFields.containsKey(key))
                    {
                        try
                        {
                            Field field = (Field) keyHandler.jsonDataFields.get(key);
                            field.setAccessible(true);
                            value = field.get(object);
                        }
                        catch (Exception e)
                        {
                            Engine.logger().error("ArmoryEntryJsonProcessor: Error while building modification json data for key '" + key + "' for '" + object + "'", e);
                            data.add(key, new JsonPrimitive("Error: Unexpected error accessing field '" + e.toString() + "' see console for more details."));
                            continue;
                        }
                    }

                    //If value then pump value into data
                    if (value != null)
                    {
                        JsonElement element = JsonLoader.buildElement(anno.type(), value, anno.args());
                        data.add("_" + key, element != null ? element : new JsonPrimitive("Error: Failed to convert"));
                    }
                    //Not print error
                    else
                    {
                        data.add("_" + key, new JsonPrimitive("Error: Failed to get value"));
                    }
                }
            }
        }

        override.add(JsonOverrideProcessor.JSON_DATA_KEY, data);

        root.add(JsonOverrideProcessor.JSON_OVERRIDE_KEY, override);

        return root;
    }
}
