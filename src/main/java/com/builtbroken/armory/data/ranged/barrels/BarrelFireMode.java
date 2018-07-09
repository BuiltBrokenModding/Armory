package com.builtbroken.armory.data.ranged.barrels;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 7/8/2018.
 */
public enum BarrelFireMode
{
    /** Fire all barrels at once */
    ALL,
    /** Fire 1 barrel after another */
    SINGLE,
    /** Fire current selected barrel only (used for custom logic) */
    CURRENT,
    /** Fire weapons based on groups */
    GROUP
}
