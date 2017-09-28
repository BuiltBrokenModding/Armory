package com.builtbroken.armory.data.meele;

import com.builtbroken.armory.api.ArmoryAPI;
import com.builtbroken.armory.data.damage.DamageData;
import com.builtbroken.mc.framework.json.imp.IJsonProcessor;
import com.builtbroken.mc.framework.json.loading.JsonProcessorData;

import java.util.ArrayList;
import java.util.List;

/**
 * Data for a weapon that works via melee action
 *
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 9/26/2017.
 */
public class MeleeWeaponData extends MeleeToolData
{
    private float damageVsEntity;
    private List<DamageData> extraDamageToApply = new ArrayList();

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

    /**
     * Base attack damage to apply to entities when hit
     *
     * @return damage greater than zero
     */
    public float getDamageVsEntity()
    {
        return damageVsEntity;
    }

    @JsonProcessorData(value = "damageEffects", type = "list.array", args = "DamageData")
    public void setExtraDamageToApply(List<DamageData> extraDamageToApply)
    {
        this.extraDamageToApply.addAll(extraDamageToApply);
    }

    /**
     * Extra effects and damage to apply when attacking entities
     *
     * @return list of effects to apply, in order
     */
    public List<DamageData> getExtraDamageToApply()
    {
        return extraDamageToApply;
    }
}
