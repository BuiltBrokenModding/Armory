package com.builtbroken.armory.data;

import com.builtbroken.mc.prefab.json.imp.IJsonGenObject;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 11/15/2016.
 */
public class ArmoryEntry implements IJsonGenObject
{
    private String name;
    private String type;

    public ArmoryEntry(String type, String name)
    {
        this.type = type;
        this.name = name;
    }

    public String name()
    {
        return name;
    }

    @Override
    public void register()
    {
        //TODO register to ArmoryRegistry
    }
}
