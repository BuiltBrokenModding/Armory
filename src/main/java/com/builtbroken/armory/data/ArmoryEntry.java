package com.builtbroken.armory.data;

import com.builtbroken.mc.lib.json.imp.IJsonGenObject;

/**
 * All armory objects extend this for common shared data and functions
 *
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 11/15/2016.
 */
public abstract class ArmoryEntry implements IJsonGenObject
{
    /** Simple name for the item, used as translation key as backup */
    private String name;
    /** Type of the item {ammo, ammoType, clip, gun} */
    private String type;

    /** Person or group that created the object */
    public String author;
    /** Weight of the object in grams */
    public int mass = -1;
    /** Translation key used for langauge files */
    public String translationKey;
    /** Group to cluster the items in, normally this is the mod domain or author name */
    public String contentGroup;

    /**
     * Creates a new instance
     *
     * @param type - Type of the item {ammo, ammoType, clip, gun}
     * @param name - simple name of the item
     */
    public ArmoryEntry(String type, String name)
    {
        this.type = type;
        this.name = name;
    }

    /**
     * Simple name of the item
     *
     * @return string
     */
    public String name()
    {
        return name;
    }

    /** Type of the item {ammo, ammoType, clip, gun} */
    public String type()
    {
        return type;
    }


    @Override
    public abstract void register();

}
