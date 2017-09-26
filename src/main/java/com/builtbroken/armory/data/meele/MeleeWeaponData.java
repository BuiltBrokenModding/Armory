package com.builtbroken.armory.data.meele;

import com.builtbroken.mc.framework.json.imp.IJsonProcessor;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 9/26/2017.
 */
public class MeleeWeaponData extends MeleeToolData
{
    private float damageVsEntity;

    public MeleeWeaponData(IJsonProcessor processor, String id, String type, String name)
    {
        super(processor, id, type, name);
        blockBreakDamage = 2;
    }

    public float getDamageVsEntity()
    {
        return damageVsEntity;
    }
}
