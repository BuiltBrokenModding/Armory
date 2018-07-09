package com.builtbroken.armory.data.ranged.barrels;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 7/8/2018.
 */
public class GunBarrelData
{
    public GunBarrel[] gunBarrels;
    public int barrelIndex = 0;
    public BarrelFireMode barrelFireMode = BarrelFireMode.CURRENT;
    public BarrelDamageMode barrelDamageMode = BarrelDamageMode.INDIVIDUAL;
}
