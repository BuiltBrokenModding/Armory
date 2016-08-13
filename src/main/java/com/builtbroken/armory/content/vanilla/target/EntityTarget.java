package com.builtbroken.armory.content.vanilla.target;

import com.builtbroken.armory.content.vanilla.target.types.ITargetType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

/**
 * Target for the player to test weapons on, or just to stare at when lonely
 *
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 8/12/2016.
 */
public class EntityTarget extends Entity
{
    ITargetType type;

    public EntityTarget(World world)
    {
        super(world);
    }

    public EntityTarget(World world, ITargetType type)
    {
        super(world);
        this.type = type;
    }

    @Override
    public boolean interactFirst(EntityPlayer p_130002_1_)
    {
        //TODO if human target allow armor to be added
        return false;
    }

    @Override
    public boolean attackEntityFrom(DamageSource p_70097_1_, float p_70097_2_)
    {
        //TODO cause motion
        //TODO report damage
        //TODO animation damage numbers
        if (this.isEntityInvulnerable())
        {
            return false;
        }
        else
        {
            this.setBeenAttacked();
            return false;
        }
    }

    @Override
    public ItemStack getPickedResult(MovingObjectPosition target)
    {
        //TODO return target base with settings
        return null;
    }

    @Override
    public AxisAlignedBB getCollisionBox(Entity p_70114_1_)
    {
        //TODO implement custom collision boxes
        return null;
    }

    @Override
    public void onUpdate()
    {

    }

    @Override
    public void onEntityUpdate()
    {

    }


    @Override
    protected void entityInit()
    {

    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound p_70037_1_)
    {

    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound p_70014_1_)
    {

    }
}
