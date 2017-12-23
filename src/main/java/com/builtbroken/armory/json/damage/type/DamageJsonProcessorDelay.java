package com.builtbroken.armory.json.damage.type;

import com.builtbroken.armory.data.damage.DamageData;
import com.builtbroken.armory.data.damage.delayed.DamageDelayed;
import com.builtbroken.armory.json.damage.DamageJsonProcessor;
import com.builtbroken.armory.json.damage.DamageTypeJsonProcessor;
import com.builtbroken.mc.framework.json.loading.JsonLoader;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 3/30/2017.
 */
public class DamageJsonProcessorDelay extends DamageTypeJsonProcessor<DamageDelayed>
{
    public DamageJsonProcessorDelay()
    {
        super(DamageDelayed.class);
    }

    @Override
    public DamageDelayed process(JsonElement element)
    {
        JsonObject damageObject = element.getAsJsonObject();
        ensureValuesExist(damageObject, "damage", "ticks");

        DamageData damageData = DamageJsonProcessor.INSTANCE.process(damageObject.get("damage"));

        int ticks = damageObject.get("ticks").getAsJsonPrimitive().getAsInt();

        return new DamageDelayed(this, damageData, ticks);
    }

    @Override
    public JsonElement build(String type, Object data, String... args)
    {
        if(data instanceof DamageDelayed)
        {
            JsonObject object = new JsonObject();
            object.add("damage", JsonLoader.buildElement("damage", ((DamageDelayed) data).damageToApply));
            object.add("ticks", new JsonPrimitive(((DamageDelayed) data).ticks));
            return object;
        }
        return null;
    }
}
