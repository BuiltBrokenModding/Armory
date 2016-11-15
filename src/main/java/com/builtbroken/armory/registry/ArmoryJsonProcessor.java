package com.builtbroken.armory.registry;

import com.builtbroken.armory.data.ArmoryEntry;
import com.builtbroken.armory.registry.processor.ArmoryEntryJsonProcessor;
import com.builtbroken.mc.prefab.json.processors.JsonProcessor;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.HashMap;

/**
 * Handles registering new weapons and armors
 *
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 11/15/2016.
 */
public class ArmoryJsonProcessor extends JsonProcessor<ArmoryEntry>
{
    public final HashMap<String, ArmoryEntryJsonProcessor> subProcessors = new HashMap();

    @Override
    public boolean canProcess(JsonElement element)
    {
        if (element.isJsonObject())
        {
            JsonObject obj = element.getAsJsonObject();
            for (ArmoryEntryJsonProcessor processor : subProcessors.values())
            {
                if (processor.canProcess(obj))
                {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public ArmoryEntry process(final JsonElement element)
    {
        final JsonObject object = element.getAsJsonObject();
        for (ArmoryEntryJsonProcessor processor : subProcessors.values())
        {
            if (processor.canProcess(object))
            {
                final JsonObject data = object.get(processor.jsonKey).getAsJsonObject();
                if (data.has("name"))
                {
                    final ArmoryEntry entry = processor.process(data);
                    //TODO handle recipes
                    //TODO handle sub parts
                    return entry;
                }
                else
                {
                    throw new IllegalArgumentException("JsonBlockProcessor: BlockData requires a name and a material value");
                }
            }
        }
        return null;
    }
}
