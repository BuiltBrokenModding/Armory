package com.builtbroken.armory.data.ammo;

import com.builtbroken.armory.data.ArmoryEntry;
import com.builtbroken.mc.api.data.weapon.IAmmoData;
import com.builtbroken.mc.api.data.weapon.IAmmoType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 11/16/2016.
 */
public class AmmoData extends ArmoryEntry implements IAmmoData
{
    public final AmmoType ammoType;
    public final String damageSource;

    public final float damage;
    public final float velocity;

    //TODO add optional damage types
    //TODO add effect handlers
    //TODO add damage calculations

    public AmmoData(String id, String name, AmmoType ammoType, String source, float damage, float velocity)
    {
        super(id, "ammo", name);
        this.ammoType = ammoType;
        this.damageSource = source;
        this.damage = damage;
        this.velocity = velocity;
    }

    @Override
    public void register()
    {
        super.register();
        ammoType.addAmmoData(this);
    }

    @Override
    public IAmmoType getAmmoType()
    {
        return ammoType;
    }

    @Override
    public float getBaseDamage()
    {
        return damage;
    }

    @Override
    public float getProjectileVelocity()
    {
        return velocity;
    }

    @Override
    public boolean onImpactEntity(Entity shooter, Entity entity, float velocity)
    {
        if (shooter instanceof EntityPlayer)
        {
            ((EntityPlayer) shooter).addChatComponentMessage(new ChatComponentText("Hit: " + entity));
        }
        if (damageSource != null && damage > 0)
        {
            //TODO create damage source with shooter, gun data, and damage type
            //TODO calculate armor
            //TODO apply force
            entity.attackEntityFrom(DamageSource.generic, damage);
        }
        return true;
    }

    @Override
    public boolean onImpactGround(Entity shooter, World world, int x, int y, int z, double hitX, double hitY, double hitZ, float velocity)
    {
        if (shooter instanceof EntityPlayer)
        {
            ((EntityPlayer) shooter).addChatComponentMessage(new ChatComponentText("Hit: " + hitX + "x " + hitY + "y " + hitZ + "z "));
        }
        return true;
    }

    @Override
    public String toString()
    {
        return "Ammo[" + getUniqueID() + "]@" + hashCode();
    }
}
