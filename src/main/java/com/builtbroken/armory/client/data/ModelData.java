package com.builtbroken.armory.client.data;

import com.builtbroken.armory.client.ClientDataHandler;
import com.builtbroken.mc.lib.json.imp.IJsonGenObject;
import com.builtbroken.mc.lib.render.model.loader.EngineModelLoader;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IModelCustom;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 11/23/2016.
 */
public class ModelData implements IJsonGenObject
{
    String key;
    String domain;
    String name;
    IModelCustom model;

    public ModelData(String key, String domain, String name)
    {
        this.key = key;
        this.domain = domain;
        this.name = name;
    }

    public IModelCustom getModel()
    {
        if (model == null)
        {
            model = EngineModelLoader.loadModel(new ResourceLocation(domain, "models/" + name));
        }
        return model;
    }

    public void render(String... parts)
    {
        if (parts != null)
        {
            model.renderOnly(parts);
        }
        else
        {
            model.renderAll();
        }
    }

    @Override
    public void register()
    {
        ClientDataHandler.INSTANCE.addModel(key, this);
    }
}
