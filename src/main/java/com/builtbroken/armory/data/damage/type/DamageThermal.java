package com.builtbroken.armory.data.damage.type;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;

/**
 * Simple heat based damage
 *
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 7/6/2018.
 */
public class DamageThermal extends EntityDamageSource
{
    public DamageThermal(Entity entity)
    {
        super("thermal", entity);
        this.setFireDamage();
    }

    @Override
    public IChatComponent func_151519_b(EntityLivingBase victim)
    {
        ItemStack itemstack = this.damageSourceEntity instanceof EntityLivingBase ? ((EntityLivingBase) this.damageSourceEntity).getHeldItem() : null;
        String s = "death.armory:attack." + this.damageType;
        String s1 = s + ".item";
        return itemstack != null && itemstack.hasDisplayName() && StatCollector.canTranslate(s1) ? new ChatComponentTranslation(s1, new Object[]{victim.func_145748_c_(), this.damageSourceEntity.func_145748_c_(), itemstack.func_151000_E()}) : new ChatComponentTranslation(s, new Object[]{victim.func_145748_c_(), this.damageSourceEntity.func_145748_c_()});
    }

    public static final class DamageType extends com.builtbroken.armory.data.damage.type.DamageType
    {
        public DamageType()
        {
            super("thermal");
        }

        @Override
        public DamageSource createDamage(Entity attacker)
        {
            return new DamageThermal(attacker);
        }
    }
}
