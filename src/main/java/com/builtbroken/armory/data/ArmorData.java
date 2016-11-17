package com.builtbroken.armory.data;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 11/15/2016.
 */
public class ArmorData extends ArmoryEntry
{
    public ArmorData(String name)
    {
        super("armor", name);
    }

    @Override
    public void register()
    {
        ArmoryDataHandler.add(this);
    }
}
