package com.builtbroken.armory.content.sentry.cart;

import com.builtbroken.armory.content.sentry.tile.ItemSentry;
import com.builtbroken.mc.client.json.render.RenderData;
import net.minecraft.block.BlockDispenser;
import net.minecraft.block.BlockRailBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import java.util.List;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 6/29/2018.
 */
public class ItemSentryCart extends ItemSentry
{
    public ItemSentryCart()
    {
        super();
        this.maxStackSize = Items.minecart.getItemStackLimit();
        BlockDispenser.dispenseBehaviorRegistry.putObject(this, new DispenserBehaviorSentryCart());
    }

    @Override
    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ)
    {
        if (BlockRailBase.func_150051_a(world.getBlock(x, y, z)))
        {
            if (!world.isRemote)
            {
                EntitySentryCart entityminecart = getNewEntity(stack, world, x + 0.5, y + 0.5, z + 0.5);
                if (stack.hasDisplayName())
                {
                    entityminecart.setMinecartName(stack.getDisplayName());
                }

                entityminecart.setOwner(player);

                world.spawnEntityInWorld(entityminecart);
            }
            stack.stackSize--;
            return true;
        }
        else
        {
            return false;
        }
    }

    public static EntitySentryCart getNewEntity(ItemStack stack, World world, double x, double y, double z)
    {
        EntitySentryCart entityminecart = new EntitySentryCart(world);
        entityminecart.setPosition(x, y, z);
        entityminecart.setSentryStack(copy(stack));
        return entityminecart;
    }

    private static ItemStack copy(ItemStack stack)
    {
        ItemStack re = stack.copy();
        re.stackSize = 1;
        return re;
    }

    @Override
    public String getUnlocalizedName(ItemStack stack)
    {
        return super.getUnlocalizedName(stack) + ".cart";
    }

    @Override
    public void getPossibleRenderStateKeys(List<String> keys, int meta, int pass)
    {
        keys.add(RenderData.INVENTORY_RENDER_KEY + ".cart");
        super.getPossibleRenderStateKeys(keys, meta, pass);
    }
}
