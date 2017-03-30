package com.builtbroken.armory.json.damage;

import com.builtbroken.armory.Armory;
import com.builtbroken.armory.data.damage.DamageBlast;
import com.builtbroken.armory.data.damage.DamageData;
import com.builtbroken.mc.api.explosive.IExplosiveHandler;
import com.builtbroken.mc.lib.world.explosive.ExplosiveRegistry;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 3/30/2017.
 */
public class DamageJsonProcessorBlast extends DamageJsonProcessor
{
    @Override
    public DamageData process(JsonElement element)
    {
        JsonObject damageObject = element.getAsJsonObject();
        ensureValuesExist(damageObject, "blast");

        //Get blast object TODO move blast code to its own loader
        JsonObject blastObject = damageObject.getAsJsonObject("blast");
        ensureValuesExist(blastObject, "id", "size");

        //Load blast data
        String blast = blastObject.get("id").getAsJsonPrimitive().getAsString();
        float size = blastObject.get("size").getAsJsonPrimitive().getAsFloat();

        //Get handler and check if is not null
        IExplosiveHandler handler = ExplosiveRegistry.get(blast);
        if (handler == null)
        {
            Armory.INSTANCE.logger().error("Failed to get a blast by name " + blast + " while parsing " + element + " this will most likely cause issues.");
        }
        return new DamageBlast(this, handler, size);
    }
}
