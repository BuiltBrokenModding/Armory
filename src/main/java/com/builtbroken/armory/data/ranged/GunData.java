package com.builtbroken.armory.data.ranged;

import com.builtbroken.armory.data.ArmoryDataHandler;
import com.builtbroken.armory.data.ammo.AmmoType;
import com.builtbroken.armory.data.ammo.ClipData;
import com.builtbroken.armory.data.ammo.ClipTypes;

/**
 * Holds all data about a gun
 *
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 11/15/2016.
 */
public class GunData extends RangeWeaponData
{
    /** Type of reload/clip this can accept */
    public final ClipTypes clipType;
    /** Every weapon gets a single fire reload ability */
    public final ClipData singleFireClipData;

    /** Unique save ID for the gun */
    public final String ID;

    /** Reload time in ticks */
    public int reloadTime = 20;

    /** Rounds a min that can be fired from the weapon, does not include reload time */
    private int rateOfFire = 60;

    private int firingDelay;

    public GunData(String id, String type, String name, AmmoType ammoType, ClipTypes clipType, ClipData singleFireData)
    {
        super(type, name, ammoType);
        this.ID = id;
        this.clipType = clipType;
        this.singleFireClipData = singleFireData;
    }

    @Override
    public void register()
    {
        ArmoryDataHandler.add(this);
    }

    /**
     * Number of rounds that can be fired in
     * a single min with the weapon ignoring
     * max clip size.
     */
    public int getRateOfFire()
    {
        return rateOfFire;
    }

    /**
     * Delay in milliseconds between rounds firing
     *
     * @return time milliseconds
     */
    public int getFiringDelay()
    {
        return firingDelay;
    }

    public void setRateOfFire(int rateOfFire)
    {
        this.rateOfFire = rateOfFire;
        //Seconds in a min * millis in a second / rounds in a min
        this.firingDelay = 60 * 1000 / rateOfFire;
    }
}
