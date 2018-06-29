package com.builtbroken.armory.content.sentry.cart;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDispenser;
import net.minecraft.block.BlockRailBase;
import net.minecraft.block.material.Material;
import net.minecraft.dispenser.BehaviorDefaultDispenseItem;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 6/29/2018.
 */
public class DispenserBehaviorSentryCart extends BehaviorDefaultDispenseItem
{
    private final BehaviorDefaultDispenseItem behaviourDefaultDispenseItem = new BehaviorDefaultDispenseItem();

    @Override
    public ItemStack dispenseStack(IBlockSource blockSource, ItemStack stack)
    {
        EnumFacing enumfacing = BlockDispenser.func_149937_b(blockSource.getBlockMetadata());
        World world = blockSource.getWorld();
        double d0 = blockSource.getX() + (double) ((float) enumfacing.getFrontOffsetX() * 1.125F);
        double d1 = blockSource.getY() + (double) ((float) enumfacing.getFrontOffsetY() * 1.125F);
        double d2 = blockSource.getZ() + (double) ((float) enumfacing.getFrontOffsetZ() * 1.125F);
        int i = blockSource.getXInt() + enumfacing.getFrontOffsetX();
        int j = blockSource.getYInt() + enumfacing.getFrontOffsetY();
        int k = blockSource.getZInt() + enumfacing.getFrontOffsetZ();
        Block block = world.getBlock(i, j, k);
        double d3;

        if (BlockRailBase.func_150051_a(block))
        {
            d3 = 0.0D;
        }
        else
        {
            if (block.getMaterial() != Material.air || !BlockRailBase.func_150051_a(world.getBlock(i, j - 1, k)))
            {
                return this.behaviourDefaultDispenseItem.dispense(blockSource, stack);
            }

            d3 = -1.0D;
        }

        EntitySentryCart entityminecart = ItemSentryCart.getNewEntity(stack, world, d0, d1 + d3, d2);

        if (stack.hasDisplayName())
        {
            entityminecart.setMinecartName(stack.getDisplayName());
        }

        world.spawnEntityInWorld(entityminecart);
        stack.splitStack(1);
        return stack;
    }

    /**
     * Play the dispense sound from the specified block.
     */
    protected void playDispenseSound(IBlockSource p_82485_1_)
    {
        p_82485_1_.getWorld().playAuxSFX(1000, p_82485_1_.getXInt(), p_82485_1_.getYInt(), p_82485_1_.getZInt(), 0);
    }
}
