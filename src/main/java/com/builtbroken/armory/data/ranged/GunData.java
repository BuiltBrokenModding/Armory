package com.builtbroken.armory.data.ranged;

import com.builtbroken.armory.data.ArmoryDataHandler;
import com.builtbroken.armory.data.ammo.AmmoData;
import com.builtbroken.armory.data.ranged.barrels.GunBarrelData;
import com.builtbroken.mc.api.data.energy.IEnergyBufferData;
import com.builtbroken.mc.api.data.energy.IEnergyChargeData;
import com.builtbroken.mc.api.data.weapon.*;
import com.builtbroken.mc.framework.json.imp.IJsonProcessor;
import com.builtbroken.mc.framework.json.loading.JsonProcessorData;
import com.builtbroken.mc.imp.transform.vector.Pos;

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

    private IAmmoData overrideAmmo;

    /** Does the weapon need to be sighted in order to be fired */
    private boolean sightToFire = false;

    /** Reload time in ticks */
    private int reloadTime = 20;

    /** Rounds a min that can be fired from the weapon, does not include reload time */
    private int rateOfFire = 60;

    private int firingDelay;

    private Pos projectileSpawnOffset = Pos.zero;
    private Pos ejectSpawnOffset = Pos.zero;
    private Pos ejectSpawnVector = Pos.zero;

    private IEnergyChargeData chargeData;
    private IEnergyBufferData bufferData;

    public final GunBarrelData gunBarrelData = new GunBarrelData();

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

    public Pos getProjectileSpawnOffset()
    {
        return projectileSpawnOffset;
    }

    @JsonProcessorData(value = "projectileSpawnOffset", type = "pos")
    public void setProjectileSpawnOffset(Pos pos)
    {
        this.projectileSpawnOffset = pos;
    }

    @Override
    public Pos getEjectionSpawnOffset()
    {
        return ejectSpawnOffset;
    }

    @JsonProcessorData(value = "ejectionSpawnOffset", type = "pos")
    public void setEjectionSpawnOffset(Pos pos)
    {
        this.ejectSpawnOffset = pos;
    }

    @Override
    public Pos getEjectionSpawnVector()
    {
        return ejectSpawnVector;
    }

    @JsonProcessorData(value = "ejectionSpawnVector", type = "pos")
    public void setEjectSpawnVector(Pos pos)
    {
        this.ejectSpawnVector = pos;
    }

    @Override
    public boolean isSightedRequiredToFire()
    {
        return sightToFire;
    }

    @JsonProcessorData(value = "sightToFire")
    public void setSightToFire(boolean b)
    {
        this.sightToFire = b;
    }

    @Override
    public int getReloadTime()
    {
        return reloadTime;
    }

    @JsonProcessorData(value = "reloadTime", type = "int")
    public void setReloadTime(int time)
    {
        this.reloadTime = time;
    }

    @Override
    public int getRateOfFire()
    {
        return rateOfFire;
    }

    @JsonProcessorData(value = "rateOfFire", type = "int")
    public void setRateOfFire(int rateOfFire)
    {
        this.rateOfFire = rateOfFire;
        //Seconds in a min * millis in a second / rounds in a min
        this.firingDelay = 60 * 1000 / rateOfFire;
    }

    @Override
    public int getFiringDelay()
    {
        return firingDelay;
    }

    public IEnergyChargeData getChargeData()
    {
        return chargeData;
    }

    @JsonProcessorData(value = "energyChargeData", type = "IEnergyChargeData")
    public void setChargeData(IEnergyChargeData chargeData)
    {
        this.chargeData = chargeData;
    }

    public IEnergyBufferData getBufferData()
    {
        return bufferData;
    }

    @JsonProcessorData(value = "energyBufferData", type = "IEnergyBufferData")
    public void setBufferData(IEnergyBufferData bufferData)
    {
        this.bufferData = bufferData;
    }

    @Override
    public String toString()
    {
        return "Gun[" + name() + ", " + getGunType() + "]@" + hashCode();
    }

    /** Used by energy weapons that enforce single ammo type */
    public IAmmoData getOverrideAmmo()
    {
        return overrideAmmo;
    }

    @JsonProcessorData(value = "overrideAmmo")
    public void setOverrideAmmo(String ammoKey)
    {
        Object ammoData = ArmoryDataHandler.INSTANCE.get("ammo").get(ammoKey);
        if (ammoData == null)
        {
            throw new IllegalArgumentException("Failed to location ammo data by ID[" + ammoKey + "]");
        }
        else if (!(ammoData instanceof AmmoData))
        {
            throw new IllegalArgumentException("Failed to get ammo data by ID[" + ammoKey + "] due to return not being an ammo data object, this is a bug");
        }
        setOverrideAmmo((AmmoData) ammoData);
    }

    public void setOverrideAmmo(IAmmoData overrideAmmo)
    {
        this.overrideAmmo = overrideAmmo;
    }
}
