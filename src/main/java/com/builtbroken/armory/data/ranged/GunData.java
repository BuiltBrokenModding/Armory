package com.builtbroken.armory.data.ranged;

import com.builtbroken.mc.api.data.weapon.IAmmoType;
import com.builtbroken.mc.api.data.weapon.IClipData;
import com.builtbroken.mc.api.data.weapon.IGunData;
import com.builtbroken.mc.api.data.weapon.ReloadType;
import com.builtbroken.mc.lib.json.imp.IJsonProcessor;

/**
 * Holds all data about a gun
 *
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 11/15/2016.
 */
public class GunData extends RangeWeaponData implements IGunData
{
    /** Type of reload/clip this can accept */
    public final ReloadType reloadType;
    /** Every weapon gets a single fire reload ability */
    public final IClipData builtInClip;

    public final String gunType;

    /** Does the weapon need to be sighted in order to be fired */
    public boolean sightToFire = false;

    /** Reload time in ticks */
    public int reloadTime = 20;

    /** Rounds a min that can be fired from the weapon, does not include reload time */
    private int rateOfFire = 60;

    private int firingDelay;

    public GunData(IJsonProcessor processor, String id, String type, String name, IAmmoType ammoType, ReloadType clipType, IClipData singleFireData)
    {
        super(processor, id, "gun", name, ammoType);
        this.gunType = type;
        this.reloadType = clipType;
        this.builtInClip = singleFireData;
    }

    @Override
    public ReloadType getReloadType()
    {
        return reloadType;
    }

    @Override
    public IClipData getBuiltInClipData()
    {
        return builtInClip;
    }

    @Override
    public IAmmoType getAmmoType()
    {
        return ammoType;
    }

    @Override
    public String getGunType()
    {
        return gunType;
    }

    @Override
    public boolean isSightedRequiredToFire()
    {
        return sightToFire;
    }

    @Override
    public int getReloadTime()
    {
        return reloadTime;
    }

    @Override
    public int getRateOfFire()
    {
        return rateOfFire;
    }

    @Override
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

    @Override
    public String toString()
    {
        return "Gun[" + name() + ", " + getGunType() + "]@" + hashCode();
    }
}
