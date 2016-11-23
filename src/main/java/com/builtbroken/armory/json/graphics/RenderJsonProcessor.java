package com.builtbroken.armory.json.graphics;

import com.builtbroken.armory.Armory;
import com.builtbroken.armory.client.data.RenderData;
import com.builtbroken.armory.client.data.RenderState;
import com.builtbroken.mc.lib.json.processors.JsonProcessor;
import com.builtbroken.mc.lib.transform.rotation.EulerAngle;
import com.builtbroken.mc.lib.transform.vector.Pos;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 11/22/2016.
 */
public class RenderJsonProcessor extends JsonProcessor<RenderData>
{
    @Override
    public String getMod()
    {
        return Armory.DOMAIN;
    }

    @Override
    public String getJsonKey()
    {
        return "render";
    }

    @Override
    public String getLoadOrder()
    {
        return null;
    }

    @Override
    public RenderData process(JsonElement element)
    {
        final JsonObject object = element.getAsJsonObject();
        ensureValuesExist(object, "contentID", "states");

        String id = object.get("contentID").getAsString();
        RenderData data = new RenderData(id);
        JsonArray array = object.get("states").getAsJsonArray();
        for (JsonElement e : array)
        {
            JsonObject obj = e.getAsJsonObject();
            ensureValuesExist(obj, "id");
            JsonPrimitive stateID = obj.getAsJsonPrimitive("id");
            if (obj.has("modelID"))
            {
                ensureValuesExist(obj, "offset", "rotation", "parts");
                String model = obj.get("modelID").getAsString();
                String parts = obj.get("parts").getAsString();
                String texture = null;

                JsonObject offsetObject = obj.get("offset").getAsJsonObject();
                Pos offset = new Pos(offsetObject.getAsJsonPrimitive("x").getAsDouble(), offsetObject.getAsJsonPrimitive("y").getAsDouble(), offsetObject.getAsJsonPrimitive("z").getAsDouble());

                JsonObject rotationObject = obj.get("rotation").getAsJsonObject();
                EulerAngle rotation = new EulerAngle(rotationObject.getAsJsonPrimitive("yaw").getAsDouble(), rotationObject.getAsJsonPrimitive("pitch").getAsDouble(), rotationObject.getAsJsonPrimitive("roll").getAsDouble());

                RenderState state = new RenderState(model, texture, offset, rotation);
                if (!parts.equals("all"))
                {
                    state.parts = parts.split(",");
                }
                if (stateID.isNumber())
                {
                    data.add(stateID.getAsInt(), state);
                }
                else
                {
                    data.add(stateID.getAsString(), state);
                }
            }
            else if (obj.has("textureID"))
            {
                String texture = obj.get("textureID").getAsString();
                RenderState state = new RenderState(texture);
                if (stateID.isNumber())
                {
                    data.add(stateID.getAsInt(), state);
                }
                else
                {
                    data.add(stateID.getAsString(), state);
                }
            }
        }
        return data;
    }
}
