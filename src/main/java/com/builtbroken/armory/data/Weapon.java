package com.builtbroken.armory.data;

import net.minecraft.item.Item;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 11/15/2016.
 */
public abstract class Weapon extends ArmoryEntry
{
    public Item itemFile;
    public int meta;

    public Weapon(String type, String name)
    {
        super(type, name);
    }
}
