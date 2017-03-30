package com.builtbroken.armory.data.damage.simple;

import net.minecraft.entity.Entity;
import net.minecraft.util.DamageSource;

/**
 * Object that takes a damage source name and converts it to a damage source object
 *
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 3/30/2017.
 */
public class DamageType
{
    public final String source;

    public DamageType(String source)
    {
        this.source = source;
    }

    public DamageSource createDamage(Entity attacker)
    {
        return null;
    }
}
