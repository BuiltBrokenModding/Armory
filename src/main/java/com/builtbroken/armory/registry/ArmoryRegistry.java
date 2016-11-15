package com.builtbroken.armory.registry;

import com.builtbroken.armory.data.ArmoryEntry;
import com.builtbroken.mc.prefab.json.block.processor.JsonBlockSubProcessor;
import com.builtbroken.mc.prefab.json.processors.JsonProcessor;

import java.util.HashMap;

/**
 * Handles registering new weapons and armors
 *
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 11/15/2016.
 */
public class ArmoryRegistry extends JsonProcessor<ArmoryEntry>
{
    public final HashMap<String, JsonBlockSubProcessor> subProcessors = new HashMap();
}
