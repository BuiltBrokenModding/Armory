package com.builtbroken.armory.content.sentry;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 4/23/2017.
 */
public enum TargetMode
{
    /** Everything but owner */
    ALL,
    /** Attack hostile and neutrals */
    NOT_FRIEND,
    /** Attack hostiles */
    HOSTILE,
    /** Attack neutrals */
    NEUTRAL,
    /** Target nothing */
    NONE
}
