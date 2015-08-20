package com.builtbroken.armory.content.prefab.armor;

import net.minecraft.block.BlockDispenser;
import net.minecraft.command.IEntitySelector;
import net.minecraft.dispenser.BehaviorDefaultDispenseItem;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.EnumFacing;

import java.util.List;

/**
 * Created by Dark on 8/20/2015.
 */
public class DispenserBehaviorArmor extends BehaviorDefaultDispenseItem
{
    @Override
    protected ItemStack dispenseStack(IBlockSource dispenserSource, ItemStack armorStack)
    {
        //TODO recode to support new armor system
        EnumFacing enumfacing = BlockDispenser.func_149937_b(dispenserSource.getBlockMetadata());
        int i = dispenserSource.getXInt() + enumfacing.getFrontOffsetX();
        int j = dispenserSource.getYInt() + enumfacing.getFrontOffsetY();
        int k = dispenserSource.getZInt() + enumfacing.getFrontOffsetZ();
        AxisAlignedBB axisalignedbb = AxisAlignedBB.getBoundingBox((double) i, (double) j, (double) k, (double) (i + 1), (double) (j + 1), (double) (k + 1));
        List list = dispenserSource.getWorld().selectEntitiesWithinAABB(EntityLivingBase.class, axisalignedbb, new IEntitySelector.ArmoredMob(armorStack));

        if (list.size() > 0)
        {
            EntityLivingBase entitylivingbase = (EntityLivingBase) list.get(0);
            int l = entitylivingbase instanceof EntityPlayer ? 1 : 0;
            int i1 = EntityLiving.getArmorPosition(armorStack);
            ItemStack itemstack1 = armorStack.copy();
            itemstack1.stackSize = 1;
            entitylivingbase.setCurrentItemOrArmor(i1 - l, itemstack1);

            if (entitylivingbase instanceof EntityLiving)
            {
                ((EntityLiving) entitylivingbase).setEquipmentDropChance(i1, 2.0F);
            }

            --armorStack.stackSize;
            return armorStack;
        }
        else
        {
            return super.dispenseStack(dispenserSource, armorStack);
        }
    }
}
