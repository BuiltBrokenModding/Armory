package com.builtbroken.armory.json.damage;

import com.builtbroken.armory.data.damage.area.DamageBlast;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 3/30/2017.
 */
public class DamageJsonProcessorBlast extends DamageJsonProcessor<DamageBlast>
{
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
}
