package com.builtbroken.armory.data.ranged;

import com.builtbroken.armory.api.ArmoryAPI;
import com.builtbroken.armory.data.ArmoryDataHandler;
import com.builtbroken.mc.api.data.weapon.IAmmoData;
import com.builtbroken.mc.api.data.weapon.IAmmoType;
import com.builtbroken.mc.framework.json.imp.IJsonProcessor;
import com.builtbroken.mc.framework.json.loading.JsonProcessorData;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 8/1/2017.
 */
public class ThrowableData extends RangeWeaponData //TODO build interface to abstract data for reuse
{
    public IAmmoData ammoData;

    public ThrowableData(IJsonProcessor processor, String id, String name, IAmmoType ammoType)
    {
        super(processor, id, ArmoryAPI.THROWABLE_WEAPON_ID, name, ammoType);
    }

    @JsonProcessorData("ammo")
    public void setAmmoData(String input)
    {
        String id = input.toLowerCase().trim();
        if (ArmoryDataHandler.INSTANCE.get(ArmoryAPI.AMMO_ID).containsKey(id))
        {
            ammoData = (IAmmoData) ArmoryDataHandler.INSTANCE.get(ArmoryAPI.AMMO_ID).get(id);
        }
        else
        {
            throw new IllegalArgumentException("Failed to locate ammo data for id '" + id + "'");
        }
    }
}
