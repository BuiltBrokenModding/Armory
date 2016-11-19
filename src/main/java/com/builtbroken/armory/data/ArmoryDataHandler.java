package com.builtbroken.armory.data;

import com.builtbroken.armory.data.ammo.AmmoData;
import com.builtbroken.armory.data.ammo.AmmoType;
import com.builtbroken.armory.data.ammo.ClipData;
import com.builtbroken.armory.data.ranged.GunData;

import java.util.HashMap;

/**
 * Handles, stores, and offers access all data processed by the mod
 *
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 11/16/2016.
 */
public class ArmoryDataHandler
{
    public static HashMap<String, AmmoType> AMMO_TYPE_DATA = new HashMap();
    public static HashMap<String, AmmoData> AMMO_DATA = new HashMap();
    public static HashMap<String, ClipData> CLIP_DATA = new HashMap();
    public static HashMap<String, GunData> GUN_DATA = new HashMap();

    public static HashMap<String, ArmorData> ARMOR_DATA = new HashMap();

    public static void add(AmmoType ammoType)
    {
        AMMO_TYPE_DATA.put(ammoType.name(), ammoType);
    }

    public static void add(AmmoData ammo)
    {
        AMMO_DATA.put(ammo.name(), ammo);
    }

    public static void add(ClipData clip)
    {
        CLIP_DATA.put(clip.name(), clip);
    }

    public static void add(GunData gun)
    {
        GUN_DATA.put(gun.ID, gun);
    }

    public static void add(ArmorData armorData)
    {
        ARMOR_DATA.put(armorData.name(), armorData);
    }

    public static AmmoType getAmmoType(String name)
    {
        return AMMO_TYPE_DATA.get(name);
    }

    public static AmmoData getAmmoData(String name)
    {
        return AMMO_DATA.get(name);
    }

    public static ClipData getClipData(String name)
    {
        return CLIP_DATA.get(name);
    }

    public static GunData getGunData(String name)
    {
        return GUN_DATA.get(name);
    }
}
