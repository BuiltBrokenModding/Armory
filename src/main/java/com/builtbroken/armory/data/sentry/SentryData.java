package com.builtbroken.armory.data.sentry;

import com.builtbroken.armory.data.ArmoryEntry;
import com.builtbroken.armory.data.ammo.AmmoData;
import com.builtbroken.armory.data.ranged.GunData;
import com.builtbroken.mc.imp.transform.vector.Pos;
import com.builtbroken.mc.lib.json.imp.IJsonProcessor;
import com.builtbroken.mc.lib.json.loading.JsonProcessorData;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 3/26/2017.
 */
public class SentryData extends ArmoryEntry
{
    //Gun to use
    private GunData gunData;
    //Ammo to use, forces ammo to a single type should only be used by energy weapons
    private AmmoData ammoData;

    private int inventorySize = 10; // 1 ammo slot, 2 battery slots
    private int inventoryAmmoStart = 0;
    private int inventoryAmmoEnd = 7;
    private int[] batteryIn = new int[]{8};
    private int[] batteryOut = new int[]{9};

    private int energyCost = -1;
    private int energyBuffer = -1;

    private int armor = -1;
    private int hp = 20;

    private int range = 50;
    private int targetSearchDelay = 10;
    private int targetAttackDelay = 3;
    private int targetLossTimer = 5;

    private float barrelLength = 0.8f;
    private float bodyWidth = 0.7f;
    private float bodyHeight = 0.7f;
    private float rotationSpeed = 10.0f;

    private Pos centerOffset;
    private Pos barrelOffset;

    public SentryData(IJsonProcessor processor, String id, String name)
    {
        super(processor, id, "sentry", name);
    }

    public GunData getGunData()
    {
        return gunData;
    }

    public void setGunData(GunData gunData)
    {
        this.gunData = gunData;
    }

    public AmmoData getAmmoData()
    {
        return ammoData;
    }

    public void setAmmoData(AmmoData ammoData)
    {
        this.ammoData = ammoData;
    }

    public int getInventorySize()
    {
        return inventorySize;
    }

    @JsonProcessorData(value = "inventorySize", type = "int")
    public void setInventorySize(int inventorySize)
    {
        this.inventorySize = inventorySize;
    }

    public int getInventoryAmmoStart()
    {
        return inventoryAmmoStart;
    }

    @JsonProcessorData(value = "inventoryAmmoStart", type = "int")
    public void setInventoryAmmoStart(int inventoryAmmoStart)
    {
        this.inventoryAmmoStart = inventoryAmmoStart;
    }

    public int getInventoryAmmoEnd()
    {
        return inventoryAmmoEnd;
    }

    @JsonProcessorData(value = "inventoryAmmoEnd", type = "int")
    public void setInventoryAmmoEnd(int inventoryAmmoEnd)
    {
        this.inventoryAmmoEnd = inventoryAmmoEnd;
    }

    public int[] getBatteryIn()
    {
        return batteryIn;
    }

    public void setBatteryIn(int[] batteryIn)
    {
        this.batteryIn = batteryIn;
    }

    public int[] getBatteryOut()
    {
        return batteryOut;
    }

    public void setBatteryOut(int[] batteryOut)
    {
        this.batteryOut = batteryOut;
    }

    public int getEnergyCost()
    {
        return energyCost;
    }

    @JsonProcessorData(value = "energyCostTick", type = "int")
    public void setEnergyCost(int energyCost)
    {
        this.energyCost = energyCost;
    }

    public int getEnergyBuffer()
    {
        return energyBuffer;
    }

    @JsonProcessorData(value = "energyBufferSize", type = "int")
    public void setEnergyBuffer(int energyBuffer)
    {
        this.energyBuffer = energyBuffer;
    }

    public int getArmor()
    {
        return armor;
    }

    @JsonProcessorData(value = "armorValue", type = "int")
    public void setArmor(int armor)
    {
        this.armor = armor;
    }

    public int getHp()
    {
        return hp;
    }

    @JsonProcessorData(value = "healthMax", type = "int")
    public void setHp(int hp)
    {
        this.hp = hp;
    }

    /** Range to look for targets inside */
    public int getRange()
    {
        return range;
    }

    @JsonProcessorData(value = "targetRange", type = "int")
    public void setRange(int range)
    {
        this.range = range;
    }

    /** How long to wait before searching for targets */
    public int getTargetSearchDelay()
    {
        return targetSearchDelay;
    }

    @JsonProcessorData(value = "targetSearchDelay", type = "int")
    public void setTargetSearchDelay(int targetSearchDelay)
    {
        this.targetSearchDelay = targetSearchDelay;
    }

    /** How long to wait before attacking a target */
    public int getTargetAttackDelay()
    {
        return targetAttackDelay;
    }

    @JsonProcessorData(value = "targetAttackDelay", type = "int")
    public void setTargetAttackDelay(int targetAttackDelay)
    {
        this.targetAttackDelay = targetAttackDelay;
    }

    /** How long to wait before switching targets */
    public int getTargetLossTimer()
    {
        return targetLossTimer;
    }

    @JsonProcessorData(value = "targetLossDelay", type = "int")
    public void setTargetLossTimer(int targetLossTimer)
    {
        this.targetLossTimer = targetLossTimer;
    }

    public float getBarrelLength()
    {
        return barrelLength;
    }

    @JsonProcessorData(value = "barrelLength", type = "float")
    public void setBarrelLength(float barrelLength)
    {
        this.barrelLength = barrelLength;
    }

    public float getBodyWidth()
    {
        return bodyWidth;
    }

    @JsonProcessorData(value = "bodyWidth", type = "float")
    public void setBodyWidth(float bodyWidth)
    {
        this.bodyWidth = bodyWidth;
    }

    public float getBodyHeight()
    {
        return bodyHeight;
    }

    @JsonProcessorData(value = "bodyHeight", type = "float")
    public void setBodyHeight(float bodyHeight)
    {
        this.bodyHeight = bodyHeight;
    }

    public Pos getCenterOffset()
    {
        return centerOffset;
    }

    @JsonProcessorData(value = "centerOffset", type = "pos")
    public void setCenterOffset(Pos centerOffset)
    {
        this.centerOffset = centerOffset;
    }

    public Pos getBarrelOffset()
    {
        return barrelOffset;
    }

    @JsonProcessorData(value = "barrelOffset", type = "pos")
    public void setBarrelOffset(Pos barrelOffset)
    {
        this.barrelOffset = barrelOffset;
    }

    public float getRotationSpeed()
    {
        return rotationSpeed;
    }

    @JsonProcessorData(value = "rotationSpeed", type = "float")
    public void setRotationSpeed(float rotationSpeed)
    {
        this.rotationSpeed = rotationSpeed;
    }
}
