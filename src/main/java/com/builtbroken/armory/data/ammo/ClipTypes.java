package com.builtbroken.armory.data.ammo;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 11/16/2016.
 */
public enum ClipTypes
{
    /** Muskets */
    FRONT_LOADED,
    /** Bolt action */
    BREACH_LOADED,
    /** Shotgun */
    HAND_FEED,
    /** Most weapons */
    CLIP,
    /** Tommy gun */
    DRUM,
    /** LMG HMG */
    BELT;

    public static ClipTypes get(int clipType)
    {
        if (clipType >= 0 && clipType < values().length)
        {
            return values()[clipType];
        }
        return BREACH_LOADED;
    }
}
