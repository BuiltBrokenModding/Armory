package com.builtbroken.armory.json.processors;

import com.builtbroken.armory.api.ArmoryAPI;
import com.builtbroken.armory.data.ArmoryDataHandler;
import com.builtbroken.armory.data.ranged.GunData;
import com.builtbroken.armory.data.sentry.SentryData;
import com.builtbroken.armory.json.ArmoryEntryJsonProcessor;
import com.builtbroken.jlib.debug.DebugPrinter;
import com.builtbroken.mc.core.Engine;
import com.builtbroken.mc.framework.json.JsonContentLoader;
import com.builtbroken.mc.framework.json.imp.IJsonGenObject;
import com.builtbroken.mc.framework.json.loading.JsonLoader;
import com.builtbroken.mc.framework.json.loading.JsonProcessorData;
import com.builtbroken.mc.framework.json.loading.JsonProcessorInjectionMap;
import com.builtbroken.mc.framework.json.override.IModifableJson;
import com.builtbroken.mc.framework.json.override.JsonOverrideProcessor;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import org.apache.logging.log4j.LogManager;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 11/16/2016.
 */
public class SentryJsonProcessor extends ArmoryEntryJsonProcessor<SentryData> implements IModifableJson<SentryData>
{
    protected final JsonProcessorInjectionMap<SentryData> keyHandler;
    protected final DebugPrinter debugPrinter;

    public SentryJsonProcessor()
    {
        super(ArmoryAPI.SENTRY_ID);
        keyHandler = new JsonProcessorInjectionMap(SentryData.class);
        debugPrinter = JsonContentLoader.INSTANCE != null ? JsonContentLoader.INSTANCE.debug : new DebugPrinter(LogManager.getLogger());
    }

    @Override
    public String getLoadOrder()
    {
        return "after:" + ArmoryAPI.GUN_ID;
    }

    @Override
    public SentryData process(JsonElement element)
    {
        debugPrinter.start("SentryProcessor", "Processing entry", Engine.runningAsDev);

        final JsonObject sentryJsonObject = element.getAsJsonObject();
        ensureValuesExist(sentryJsonObject, "id", "name", "gunID");

        String gunID = sentryJsonObject.get("gunID").getAsString();
        String id = sentryJsonObject.getAsJsonPrimitive("id").getAsString();
        String name = sentryJsonObject.get("name").getAsString();

        debugPrinter.log("Name: " + name);
        debugPrinter.log("ID: " + id);
        debugPrinter.log("Gun: " + gunID);

        SentryData sentryData = new SentryData(this, id, name);

        //Loading gun
        Object gunData = ArmoryDataHandler.INSTANCE.get("gun").get(gunID);
        if (gunData == null)
        {
            throw new IllegalArgumentException("Failed to location gun by ID[" + gunID + "]");
        }
        else if (!(gunData instanceof GunData))
        {
            throw new IllegalArgumentException("Failed to get gun by ID[" + gunID + "] due to return not being a gun data object, this is a bug");
        }
        sentryData.setGunData((GunData) gunData);

        //Call to process injection tags
        for (Map.Entry<String, JsonElement> entry : sentryJsonObject.entrySet())
        {
            if (keyHandler.handle(sentryData, entry.getKey().toLowerCase(), entry.getValue()))
            {
                debugPrinter.log("Injected Key: " + entry.getKey());
            }
        }

        //Process shared data
        processExtraData(sentryJsonObject, sentryData);

        debugPrinter.end("Done...");
        return sentryData;
    }

    @Override
    public boolean addData(String key, JsonElement data, SentryData generatedObject)
    {
        if (keyHandler.handle(generatedObject, key, data, true, "add"))
        {
            debugPrinter.log("Injected Add Override >> Key: " + key + " Data: " + data);
            return true;
        }
        return false;
    }

    @Override
    public boolean removeData(String key, SentryData generatedObject)
    {
        if (keyHandler.handle(generatedObject, key, null, true, "remove"))
        {
            debugPrinter.log("Injected Remove Override >> Key: " + key);
            return true;
        }
        return false;
    }

    @Override
    public boolean replaceData(String key, JsonElement data, SentryData generatedObject)
    {
        if (keyHandler.handle(generatedObject, key, data, true, "replace"))
        {
            debugPrinter.log("Injected Replacement Override >> Key: " + key + " Data: " + data);
            return true;
        }
        return false;
    }

    @Override
    public Object getData(String _key, SentryData object)
    {
        final String key = _key.toLowerCase();
        JsonProcessorData anno = keyHandler.jsonDataAnnotation.get(key);
        if (anno != null && anno.allowRuntimeChanges())
        {
            //Try getter map first
            if (keyHandler.jsonDataGetters.containsKey(key))
            {
                try
                {
                    Method method = keyHandler.jsonDataGetters.get(key);
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
                    Field field = keyHandler.jsonDataFields.get(key);
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
        override.add(JsonOverrideProcessor.JSON_PROCESSOR_KEY, new JsonPrimitive(ArmoryAPI.SENTRY_ID));
        override.add(JsonOverrideProcessor.JSON_CONTENT_KEY, new JsonPrimitive(object.getContentID()));
        override.add(JsonOverrideProcessor.JSON_ACTION_KEY, new JsonPrimitive("edit"));
        override.add(JsonOverrideProcessor.JSON_ENABLE_KEY, new JsonPrimitive("false"));

        JsonObject data = new JsonObject();
        override.add("_note", new JsonPrimitive("Remove _ underscore in front of key to allow it to be read."));
        override.add("_note2", new JsonPrimitive("_ underscore, is added to show all values while allowing selective changes. " +
                "Only change what needs to be changed to avoid old values replacing new values in future updates." +
                "Feel free to remove any value you do not need. It will not effect the load process so long as the minimal data exists. " +
                "Action type, content id, processor id, and data to use. See VoltzEngine's Github Wiki for more information."));

        for (String key : keyHandler.injectionKeys)
        {
            if (keyHandler.jsonDataAnnotation.containsKey(key))
            {
                JsonProcessorData anno = keyHandler.jsonDataAnnotation.get(key);
                if (anno.allowRuntimeChanges())
                {
                    Object value = null;

                    //Try getter map first
                    if (keyHandler.jsonDataGetters.containsKey(key))
                    {
                        try
                        {
                            Method method = keyHandler.jsonDataGetters.get(key);
                            method.setAccessible(true);
                            value = method.invoke(object);
                        }
                        catch (Exception e)
                        {
                            Engine.logger().error("SentryJsonProcessor: Error while building modification json data for key '" + key + "' for '" + object + "'", e);
                            data.add(key, new JsonPrimitive("Error: Unexpected error invoking getter '" + e.toString() + "' see console for more details."));
                            continue;
                        }
                    }
                    //then try fields
                    else if (keyHandler.jsonDataFields.containsKey(key))
                    {
                        try
                        {
                            Field field = keyHandler.jsonDataFields.get(key);
                            field.setAccessible(true);
                            value = field.get(object);
                        }
                        catch (Exception e)
                        {
                            Engine.logger().error("SentryJsonProcessor: Error while building modification json data for key '" + key + "' for '" + object + "'", e);
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
