package com.builtbroken.armory.data.damage.simple;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;

/**
 * Wrapper around a damage type to ensure that the shooter or attacker is marked as the source of the damage.
 *
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 3/30/2017.
 */
public class DamageSourceShooter extends EntityDamageSource
{
    /** Actual damage source being applied to the entity */
    public final DamageSource damageSource;

    public DamageSourceShooter(String name, Entity attacker, DamageSource damageSource)
    {
        super(name, attacker);
        this.damageSource = damageSource;
    }

    @Override
    public Entity getSourceOfDamage()
    {
        return this.damageSourceEntity;
    }

    @Override
    public IChatComponent func_151519_b(EntityLivingBase killed)
    {
        IChatComponent ichatcomponent = damageSourceEntity.func_145748_c_();
        ItemStack itemstack = this.damageSourceEntity instanceof EntityLivingBase ? ((EntityLivingBase) this.damageSourceEntity).getHeldItem() : null;
        String s = "death.attack." + this.damageType;
        String s1 = s + ".item";
        return itemstack != null && itemstack.hasDisplayName() && StatCollector.canTranslate(s1) ? new ChatComponentTranslation(s1, new Object[]{killed.func_145748_c_(), ichatcomponent, itemstack.func_151000_E()}) : new ChatComponentTranslation(s, new Object[]{killed.func_145748_c_(), ichatcomponent});
    }

    @Override
    public boolean isProjectile()
    {
        return super.isProjectile() || damageSource.isProjectile();
    }

    @Override
    public boolean isExplosion()
    {
        return damageSource.isExplosion();
    }

    @Override
    public boolean isUnblockable()
    {
        return damageSource.isUnblockable();
    }

    @Override
    public float getHungerDamage()
    {
        return damageSource.getHungerDamage();
    }

    @Override
    public boolean canHarmInCreative()
    {
        return damageSource.canHarmInCreative();
    }

    @Override
    public boolean isDamageAbsolute()
    {
        return damageSource.isDamageAbsolute();
    }

    @Override
    public boolean isFireDamage()
    {
        return damageSource.isFireDamage();
    }

    @Override
    public String getDamageType()
    {
        return damageSource.getDamageType();
    }

    @Override
    public boolean isMagicDamage()
    {
        return damageSource.isMagicDamage();
    }
}
