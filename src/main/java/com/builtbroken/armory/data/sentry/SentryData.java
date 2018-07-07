package com.builtbroken.armory.data.sentry;

import com.builtbroken.armory.data.ArmoryEntry;
import com.builtbroken.armory.data.ranged.GunData;
import com.builtbroken.mc.framework.json.imp.IJsonProcessor;
import com.builtbroken.mc.framework.json.loading.JsonProcessorData;
import com.builtbroken.mc.framework.json.loading.JsonProcessorDataGetter;
import com.builtbroken.mc.imp.transform.vector.Pos;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 3/26/2017.
 */
public class SentryData extends ArmoryEntry
{
    //TODO consider lombok support to cut down on getters and setters needed
    //Gun to use
    private GunData gunData;

    private int inventorySize = 10; // 1 ammo slot, 2 battery slots
    private int inventoryAmmoStart = 0;
    private int inventoryAmmoEnd = 7;
    private int[] batteryIn = new int[]{8};
    private int[] batteryOut = new int[]{9};

    private int energyCost = -1;
    private int energyBuffer = -1;

    private int armor = -1;
    private int maxHealth = 20;

    private int range = 50;
    private int targetSearchDelay = 10;
    private int targetAttackDelay = 3;
    private int targetLossTimer = 5;

    protected int empStunTimerPerEnergyUnit = 20 * 60;
    protected int empStunEnergyUnit = 200;
    protected int empAbsorptionLimit = 200;
    protected int empMaxStun = -1;

    private float barrelLength = 0.8f;
    private float bodyWidth = 0.7f;
    private float bodyHeight = 0.7f;
    private float rotationSpeed = 10.0f;

    private Pos centerOffset;
    private Pos barrelOffset;

    private String[] defaultTargetTypes = new String[]{"mobs"};
    private String[] allowedTargetTypes = new String[]{"living", "players", "flying", "animals", "tameable", "mobs", "npcs"};

    public SentryData(IJsonProcessor processor, String id, String name)
    {
        super(processor, id, "sentry", name);
    }


    @Override
    public void validate()
    {
        super.validate();
        //Ensure battery arrays do not share values
        if (getBatteryIn() != null && getBatteryOut() != null)
        {
            for (int i = 0; i < getBatteryIn().length; i++)
            {
                for (int z = 0; z < getBatteryOut().length; z++)
                {
                    if (getBatteryIn()[i] == getBatteryOut()[z])
                    {
                        throw new IllegalArgumentException("Slot IDS can not be shared between battery in and out arrays");
                    }
                }
            }
        }
        //Ensure that allowed group contains everything in default types
        if (getDefaultTargetTypes() != null && getAllowedTargetTypes() != null)
        {
            for (int i = 0; i < getDefaultTargetTypes().length; i++)
            {
                boolean contained = false;
                for (int z = 0; z < getAllowedTargetTypes().length; z++)
                {
                    if (getDefaultTargetTypes()[i].equals(getAllowedTargetTypes()[z]))
                    {
                        contained = true;
                    }
                }
                if (!contained)
                {
                    throw new IllegalArgumentException("Allow types does not contain '" + getDefaultTargetTypes()[i] + "'");
                }
            }
        }
        else
        {
            throw new IllegalArgumentException("Allow or default types can not be null");
        }
    }

    public GunData getGunData()
    {
        return gunData;
    }

    public void setGunData(GunData gunData)
    {
        this.gunData = gunData;
    }

    @JsonProcessorDataGetter(value = "inventorySize", type = "int")
    public int getInventorySize()
    {
        return inventorySize;
    }

    @JsonProcessorData(value = "inventorySize", type = "int", allowRuntimeChanges = true)
    public void setInventorySize(int inventorySize)
    {
        this.inventorySize = inventorySize;
    }

    @JsonProcessorDataGetter(value = "inventoryAmmoStart", type = "int")
    public int getInventoryAmmoStart()
    {
        return inventoryAmmoStart;
    }

    @JsonProcessorData(value = "inventoryAmmoStart", type = "int", allowRuntimeChanges = true)
    public void setInventoryAmmoStart(int inventoryAmmoStart)
    {
        this.inventoryAmmoStart = inventoryAmmoStart;
    }

    @JsonProcessorDataGetter(value = "inventoryAmmoEnd", type = "int")
    public int getInventoryAmmoEnd()
    {
        return inventoryAmmoEnd;
    }

    @JsonProcessorData(value = "inventoryAmmoEnd", type = "int", allowRuntimeChanges = true)
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

    @JsonProcessorData(value = "energyCostTick", type = "int", allowRuntimeChanges = true)
    public void setEnergyCost(int energyCost)
    {
        this.energyCost = energyCost;
    }

    @JsonProcessorDataGetter(value = "energyBufferSize", type = "int")
    public int getEnergyBuffer()
    {
        return energyBuffer;
    }

    @JsonProcessorData(value = "energyBufferSize", type = "int", allowRuntimeChanges = true)
    public void setEnergyBuffer(int energyBuffer)
    {
        this.energyBuffer = energyBuffer;
    }

    @JsonProcessorDataGetter(value = "armorValue", type = "int")
    public int getArmor()
    {
        return armor;
    }

    @JsonProcessorData(value = "armorValue", type = "int", allowRuntimeChanges = true)
    public void setArmor(int armor)
    {
        this.armor = armor;
    }

    @JsonProcessorDataGetter(value = "healthMax", type = "int")
    public int getMaxHealth()
    {
        return maxHealth;
    }

    @JsonProcessorData(value = {"healthMax", "health"}, type = "int", allowRuntimeChanges = true)
    public void setMaxHealth(int hp)
    {
        this.maxHealth = hp;
    }

    /** Range to look for targets inside */
    @JsonProcessorDataGetter(value = "targetRange", type = "int")
    public int getRange()
    {
        return range;
    }

    @JsonProcessorData(value = "targetRange", type = "int", allowRuntimeChanges = true)
    public void setRange(int range)
    {
        this.range = range;
    }

    /** How long to wait before searching for targets */
    @JsonProcessorDataGetter(value = "targetSearchDelay", type = "int")
    public int getTargetSearchDelay()
    {
        return targetSearchDelay;
    }

    @JsonProcessorData(value = "targetSearchDelay", type = "int", allowRuntimeChanges = true)
    public void setTargetSearchDelay(int targetSearchDelay)
    {
        this.targetSearchDelay = targetSearchDelay;
    }

    /** How long to wait before attacking a target */
    @JsonProcessorDataGetter(value = "targetAttackDelay", type = "int")
    public int getTargetAttackDelay()
    {
        return targetAttackDelay;
    }

    @JsonProcessorData(value = "targetAttackDelay", type = "int", allowRuntimeChanges = true)
    public void setTargetAttackDelay(int targetAttackDelay)
    {
        this.targetAttackDelay = targetAttackDelay;
    }

    /** How long to wait before switching targets */
    @JsonProcessorDataGetter(value = "targetLossDelay", type = "int")
    public int getTargetLossTimer()
    {
        return targetLossTimer;
    }

    @JsonProcessorData(value = "targetLossDelay", type = "int", allowRuntimeChanges = true)
    public void setTargetLossTimer(int targetLossTimer)
    {
        this.targetLossTimer = targetLossTimer;
    }

    @JsonProcessorDataGetter(value = "barrelLength", type = "float")
    public float getBarrelLength()
    {
        return barrelLength;
    }

    @JsonProcessorData(value = "barrelLength", type = "float", allowRuntimeChanges = true)
    public void setBarrelLength(float barrelLength)
    {
        this.barrelLength = barrelLength;
    }

    @JsonProcessorDataGetter(value = "bodyWidth", type = "float")
    public float getBodyWidth()
    {
        return bodyWidth;
    }

    @JsonProcessorData(value = "bodyWidth", type = "float", allowRuntimeChanges = true)
    public void setBodyWidth(float bodyWidth)
    {
        this.bodyWidth = bodyWidth;
    }

    @JsonProcessorDataGetter(value = "bodyHeight", type = "float")
    public float getBodyHeight()
    {
        return bodyHeight;
    }

    @JsonProcessorData(value = "bodyHeight", type = "float", allowRuntimeChanges = true)
    public void setBodyHeight(float bodyHeight)
    {
        this.bodyHeight = bodyHeight;
    }

    @JsonProcessorDataGetter(value = "centerOffset", type = "pos")
    public Pos getCenterOffset()
    {
        return centerOffset;
    }

    @JsonProcessorData(value = "centerOffset", type = "pos", allowRuntimeChanges = true)
    public void setCenterOffset(Pos centerOffset)
    {
        this.centerOffset = centerOffset;
    }

    @JsonProcessorDataGetter(value = "barrelOffset", type = "pos")
    public Pos getBarrelOffset()
    {
        return barrelOffset;
    }

    @JsonProcessorData(value = "barrelOffset", type = "pos", allowRuntimeChanges = true)
    public void setBarrelOffset(Pos barrelOffset)
    {
        this.barrelOffset = barrelOffset;
    }

    @JsonProcessorDataGetter(value = "rotationSpeed", type = "float")
    public float getRotationSpeed()
    {
        return rotationSpeed;
    }

    @JsonProcessorData(value = "rotationSpeed", type = "float", allowRuntimeChanges = true)
    public void setRotationSpeed(float rotationSpeed)
    {
        this.rotationSpeed = rotationSpeed;
    }

    @JsonProcessorDataGetter(value = "defaultTargetTypes", type = "array.string")
    public String[] getDefaultTargetTypes()
    {
        return defaultTargetTypes;
    }

    @JsonProcessorData(value = "defaultTargetTypes", type = "array.string", allowRuntimeChanges = true)
    public void setDefaultTargetTypes(String[] types)
    {
        this.defaultTargetTypes = types;
        if (defaultTargetTypes != null)
        {
            for (int i = 0; i < defaultTargetTypes.length; i++)
            {
                defaultTargetTypes[i] = defaultTargetTypes[i].toLowerCase().trim();
            }
        }
    }

    @JsonProcessorDataGetter(value = "allowedTargetTypes", type = "array.string")
    public String[] getAllowedTargetTypes()
    {
        return allowedTargetTypes;
    }

    @JsonProcessorData(value = "allowedTargetTypes", type = "array.string", allowRuntimeChanges = true)
    public void setAllowedTargetTypes(String[] types)
    {
        this.allowedTargetTypes = types;
        if (allowedTargetTypes != null)
        {
            for (int i = 0; i < allowedTargetTypes.length; i++)
            {
                allowedTargetTypes[i] = allowedTargetTypes[i].toLowerCase().trim();
            }
        }
    }

    @JsonProcessorDataGetter(value = "empStunTimer", type = "int")
    public int getEmpStunTimerPerEnergyUnit()
    {
        return empStunTimerPerEnergyUnit;
    }

    @JsonProcessorData(value = "empStunTimer", type = "int", allowRuntimeChanges = true)
    public void setEmpStunTimerPerEnergyUnit(int empStunTimerPerEnergyUnit)
    {
        this.empStunTimerPerEnergyUnit = empStunTimerPerEnergyUnit;
    }

    @JsonProcessorDataGetter(value = "empStunEnergyCost", type = "int")
    public int getEmpStunEnergyUnit()
    {
        return empStunEnergyUnit;
    }

    @JsonProcessorData(value = "empStunEnergyCost", type = "int", allowRuntimeChanges = true)
    public void setEmpStunEnergyUnit(int empStunEnergyUnit)
    {
        this.empStunEnergyUnit = empStunEnergyUnit;
    }

    @JsonProcessorDataGetter(value = "empAbsorptionLimit", type = "int")
    public int getEmpAbsorptionLimit()
    {
        return empAbsorptionLimit;
    }

    @JsonProcessorData(value = "empAbsorptionLimit", type = "int", allowRuntimeChanges = true)
    public void setEmpAbsorptionLimit(int empAbsorptionLimit)
    {
        this.empAbsorptionLimit = empAbsorptionLimit;
    }

    public void calcEmpData()
    {
        this.empMaxStun = (int) (((float) getEmpStunEnergyUnit() / (float) getEmpAbsorptionLimit()) * getEmpStunTimerPerEnergyUnit());
    }

    public int getEmpMaxStun()
    {
        if (empMaxStun == -1)
        {
            calcEmpData();
        }
        return empMaxStun;
    }
}
