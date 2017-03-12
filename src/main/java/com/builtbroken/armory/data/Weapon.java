package com.builtbroken.armory.data;

import com.builtbroken.mc.lib.json.imp.IJsonProcessor;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 11/15/2016.
 */
public abstract class Weapon extends ArmoryEntry
{
    public Weapon(IJsonProcessor processor, String id, String type, String name)
    {
        super(processor, id, type, name);
    }
}
