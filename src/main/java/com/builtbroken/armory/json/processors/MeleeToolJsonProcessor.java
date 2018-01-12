package com.builtbroken.armory.json.processors;

import com.builtbroken.armory.api.ArmoryAPI;
import com.builtbroken.armory.data.meele.MeleeToolData;
import com.builtbroken.armory.json.ArmoryEntryJsonProcessor;
import com.builtbroken.jlib.debug.DebugPrinter;
import com.builtbroken.mc.core.Engine;
import com.builtbroken.mc.framework.json.JsonContentLoader;
import com.builtbroken.mc.framework.json.loading.JsonProcessorInjectionMap;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.apache.logging.log4j.LogManager;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 11/16/2016.
 */
public class MeleeToolJsonProcessor extends ArmoryEntryJsonProcessor<MeleeToolData>
{
    public MeleeToolJsonProcessor()
    {
        super(ArmoryAPI.MELEE_TOOL_ID);
        keyHandler = new JsonProcessorInjectionMap(MeleeToolData.class);
        debugPrinter = JsonContentLoader.INSTANCE != null ? JsonContentLoader.INSTANCE.debug : new DebugPrinter(LogManager.getLogger());
    }

    @Override
    public String getLoadOrder()
    {
        return null;
    }

    @Override
    public MeleeToolData process(JsonElement element)
    {
        debugPrinter.start("MeleeToolProcessor", "Processing entry", Engine.runningAsDev);

        final JsonObject weaponJSON = element.getAsJsonObject();
        ensureValuesExist(weaponJSON, "id", "name");

        //Get common data
        String id = weaponJSON.get("id").getAsString();
        String name = weaponJSON.get("name").getAsString();

        debugPrinter.log("Name: " + name);
        debugPrinter.log("ID: " + id);

        //Create object
        MeleeToolData weaponData = new MeleeToolData(this, id, name);

        //Process shared data
        processExtraData(weaponJSON, weaponData);

        debugPrinter.end("Done...");
        return weaponData;
    }
}
