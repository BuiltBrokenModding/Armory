package com.builtbroken.armory.mod.waila;

import com.builtbroken.armory.content.sentry.tile.TileSentry;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

/**
 * Fixes waila showing camo blocks as something other than camo
 *
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 10/29/2017.
 */
public class WailaSentryHandler extends WailaHandler
{
    @Override
    public ItemStack getWailaStack(IWailaDataAccessor accessor, IWailaConfigHandler config)
    {
        TileEntity tile = accessor.getTileEntity();
        if (tile instanceof TileSentry)
        {
            return ((TileSentry) tile).toItemStack();
        }
        return null;
    }
}
