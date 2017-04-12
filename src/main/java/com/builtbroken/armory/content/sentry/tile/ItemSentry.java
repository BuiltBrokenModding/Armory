package com.builtbroken.armory.content.sentry.tile;

import com.builtbroken.armory.Armory;
import com.builtbroken.armory.content.prefab.ItemMetaArmoryEntry;
import com.builtbroken.armory.data.sentry.SentryData;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

/**
 * Item used to hold sentry data and handled placement
 *
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 4/10/2017.
 */
public class ItemSentry extends ItemMetaArmoryEntry<SentryData>
{
    public ItemSentry()
    {
        super("sentry", "sentry");
    }


    /**
     * Called to actually place the block, after the location is determined
     * and all permission checks have been made.
     *
     * @param stack  The item stack that was used to place the block. This can be changed inside the method.
     * @param player The player who is placing the block. Can be null if the block is not being placed by a player.
     * @param side   The side the player (or machine) right-clicked on.
     */
    public boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ, int metadata)
    {
        if (!world.setBlock(x, y, z, Armory.blockSentry, metadata, 3))
        {
            return false;
        }

        if (world.getBlock(x, y, z) == Armory.blockSentry)
        {
            Armory.blockSentry.onBlockPlacedBy(world, x, y, z, player, stack);
            Armory.blockSentry.onPostBlockPlaced(world, x, y, z, metadata);

            TileEntity tile = world.getTileEntity(x, y, z);
            if (tile instanceof TileSentry)
            {
                ((TileSentry) tile).setSentryStack(stack.copy());
            }
        }
        return true;
    }


    @Override
    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float xHit, float yHit, float zHit)
    {
        Block block = world.getBlock(x, y, z);

        if (block == Blocks.snow_layer && (world.getBlockMetadata(x, y, z) & 7) < 1)
        {
            side = 1;
        }
        else if (block != Blocks.vine && block != Blocks.tallgrass && block != Blocks.deadbush && !block.isReplaceable(world, x, y, z))
        {
            if (side == 0)
            {
                --y;
            }

            if (side == 1)
            {
                ++y;
            }

            if (side == 2)
            {
                --z;
            }

            if (side == 3)
            {
                ++z;
            }

            if (side == 4)
            {
                --x;
            }

            if (side == 5)
            {
                ++x;
            }
        }

        if (stack.stackSize == 0)
        {
            return false;
        }
        else if (!player.canPlayerEdit(x, y, z, side, stack))
        {
            return false;
        }
        else if (y == 255 && Armory.blockSentry.getMaterial().isSolid())
        {
            return false;
        }
        else if (world.canPlaceEntityOnSide(Armory.blockSentry, x, y, z, false, side, player, stack))
        {
            int placementMeta = this.getMetadata(stack.getItemDamage());
            int actualMeta = Armory.blockSentry.onBlockPlaced(world, x, y, z, side, xHit, yHit, zHit, placementMeta);
            if (placeBlockAt(stack, player, world, x, y, z, side, xHit, yHit, zHit, actualMeta))
            {
                world.playSoundEffect((double) ((float) x + 0.5F), (double) ((float) y + 0.5F), (double) ((float) z + 0.5F), Armory.blockSentry.stepSound.func_150496_b(), (Armory.blockSentry.stepSound.getVolume() + 1.0F) / 2.0F, Armory.blockSentry.stepSound.getPitch() * 0.8F);
                --stack.stackSize;
            }
            return true;
        }
        else
        {
            return false;
        }
    }
}
