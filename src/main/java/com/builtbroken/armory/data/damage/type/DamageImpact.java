package com.builtbroken.armory.data.damage.type;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;

/**
 * Simple impact damage caused by a projectile
 *
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 10/26/2017.
 */
public class DamageImpact extends EntityDamageSource
{
    public DamageImpact(Entity entity)
    {
        super("impact", entity);
    }

    @Override
    public IChatComponent func_151519_b(EntityLivingBase victim)
    {
        ItemStack itemstack = this.damageSourceEntity instanceof EntityLivingBase ? ((EntityLivingBase) this.damageSourceEntity).getHeldItem() : null;
        String s = "death.armory:attack." + this.damageType;
        String s1 = s + ".item";
        return itemstack != null && itemstack.hasDisplayName() && StatCollector.canTranslate(s1) ? new ChatComponentTranslation(s1, new Object[]{victim.func_145748_c_(), this.damageSourceEntity.func_145748_c_(), itemstack.func_151000_E()}) : new ChatComponentTranslation(s, new Object[]{victim.func_145748_c_(), this.damageSourceEntity.func_145748_c_()});
    }

    public static final class DamageTypeImpact extends DamageType
    {
        public DamageTypeImpact()
        {
            super("impact");
        }

        @Override
        public DamageSource createDamage(Entity attacker)
        {
            return new DamageImpact(attacker);
        }
    }
}
