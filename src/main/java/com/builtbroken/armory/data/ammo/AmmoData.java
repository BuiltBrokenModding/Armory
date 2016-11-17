package com.builtbroken.armory.data.ammo;

import com.builtbroken.armory.data.ArmoryDataHandler;
import com.builtbroken.armory.data.ArmoryEntry;
import net.minecraft.entity.Entity;
import net.minecraft.util.DamageSource;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 11/16/2016.
 */
public class AmmoData extends ArmoryEntry
{
    public final AmmoType ammoType;
    public final String damageSource;

    public final float damage;

    //TODO add optional damage types
    //TODO add effect handlers
    //TODO add damage calculations

    public AmmoData(String name, AmmoType ammoType, String source, float damage)
    {
        super("ammo", name);
        this.ammoType = ammoType;
        this.damageSource = source;
        this.damage = damage;
    }

    public void applyDamage(Entity shooter, Entity entity)
    {
        if (damageSource != null && damage > 0)
        {
            //TODO create damage source with shooter, gun data, and damage type
            entity.attackEntityFrom(DamageSource.generic, damage);
        }
    }

    @Override
    public void register()
    {
        ArmoryDataHandler.add(this);
    }
}
