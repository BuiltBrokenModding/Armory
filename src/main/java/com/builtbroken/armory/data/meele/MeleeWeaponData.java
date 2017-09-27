package com.builtbroken.armory.data.meele;

import com.builtbroken.armory.api.ArmoryAPI;
import com.builtbroken.mc.framework.json.imp.IJsonProcessor;
import com.builtbroken.mc.framework.json.loading.JsonProcessorData;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 9/26/2017.
 */
public class MeleeWeaponData extends MeleeToolData
{
    private float damageVsEntity;

    public MeleeWeaponData(IJsonProcessor processor, String id, String name)
    {
        super(processor, id, ArmoryAPI.MELEE_WEAPON_ID, name);
        blockBreakDamage = 2;
    }

    @JsonProcessorData(value = "attack", type = "float")
    public void setDamageVsEntity(float damageVsEntity)
    {
        this.damageVsEntity = damageVsEntity;
    }

    public float getDamageVsEntity()
    {
        return damageVsEntity;
    }
}
