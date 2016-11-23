package com.builtbroken.armory.client;

import com.builtbroken.armory.client.data.ModelData;
import com.builtbroken.armory.client.data.RenderData;
import com.builtbroken.armory.client.data.TextureData;

import java.util.HashMap;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 11/22/2016.
 */
public class ClientDataHandler
{
    public HashMap<String, ModelData> models = new HashMap();
    public HashMap<String, TextureData> textures = new HashMap();
    public HashMap<String, RenderData> renderData = new HashMap();

    public static final ClientDataHandler INSTANCE = new ClientDataHandler();

    public void addTexture(String key, TextureData texture)
    {
        textures.put(key, texture);
    }

    public void addModel(String key, ModelData model)
    {
        models.put(key, model);
    }

    public void addRenderData(String key, RenderData data)
    {
        renderData.put(key, data);
    }

    public RenderData getRenderData(String key)
    {
        return renderData.get(key);
    }

    public ModelData getModel(String key)
    {
        return models.get(key);
    }

    public TextureData getTexture(String key)
    {
        return textures.get(key);
    }
}
