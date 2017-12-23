package com.builtbroken.armory.json.damage.type;

import com.builtbroken.armory.data.damage.area.DamageBlast;
import com.builtbroken.armory.json.damage.DamageTypeJsonProcessor;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 3/30/2017.
 */
public class DamageJsonProcessorBlast extends DamageTypeJsonProcessor<DamageBlast>
{
    public DamageJsonProcessorBlast()
    {
        super(DamageBlast.class);
    }

    @Override
    public DamageBlast process(JsonElement element)
    {
        JsonObject damageObject = element.getAsJsonObject();
        ensureValuesExist(damageObject, "blast");

        //Get blast object TODO move blast code to its own loader
        JsonObject blastObject = damageObject.getAsJsonObject("blast");
        ensureValuesExist(blastObject, "id", "size");

        //Load blast data
        String blast = blastObject.get("id").getAsJsonPrimitive().getAsString();
        float size = blastObject.get("size").getAsJsonPrimitive().getAsFloat();

        return new DamageBlast(this, blast, size);
    }

    @Override
    public JsonElement build(String type, Object data, String... args)
    {
        if(data instanceof DamageBlast)
        {
            JsonObject object = new JsonObject();

            JsonObject blast = new JsonObject();
            blast.add("id", new JsonPrimitive(((DamageBlast) data).blastName));
            blast.add("size", new JsonPrimitive(((DamageBlast) data).size));

            object.add("blast", blast);

            return object;
        }
        return null;
    }
}
