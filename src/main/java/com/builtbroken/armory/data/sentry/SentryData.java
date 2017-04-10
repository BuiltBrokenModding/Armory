package com.builtbroken.armory.data.sentry;

import com.builtbroken.armory.data.ArmoryEntry;
import com.builtbroken.armory.data.ranged.GunData;
import com.builtbroken.mc.lib.json.imp.IJsonProcessor;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 3/26/2017.
 */
public class SentryData extends ArmoryEntry
{
    public GunData gunData;

    public int inventorySize = 3; // 1 ammo slot, 2 battery slots
    public int inventoryAmmoStart = 0;
    public int inventoryAmmoEnd = 0;
    public int energyCost = -1;
    public int energyBuffer = -1;

    public int armor = -1;
    public int hp = 20;

    /** Range to look for targets inside */
    public int range = 50;
    /** How long to wait before searching for targets */
    public int targetSearchDelay = 10;
    /** How long to wait before attacking a target */
    public int targetAttackDelay = 3;
    /** How long to wait before switching targets */
    public int targetLossTimer = 5;

    public SentryData(IJsonProcessor processor, String id, String name)
    {
        super(processor, id, "sentry", name);
    }
}
