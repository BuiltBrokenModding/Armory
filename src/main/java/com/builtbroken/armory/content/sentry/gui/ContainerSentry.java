package com.builtbroken.armory.content.sentry.gui;

import com.builtbroken.armory.content.sentry.tile.TileSentry;
import com.builtbroken.mc.prefab.gui.ContainerBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 4/9/2017.
 */
public class ContainerSentry extends ContainerBase
{
    public ContainerSentry(EntityPlayer player, TileSentry sentry)
    {
        super(player, sentry);
        int slotID = 0;
        //Inventory slots
        if(sentry != null && sentry.getSentry() != null)
        {
            int ammoBaySize = (sentry.getSentry().getSentryData().getInventoryAmmoEnd() - sentry.getSentry().getSentryData().getInventoryAmmoStart());
            int rows = (ammoBaySize / 5) + 1;
            for (int y = 0; y < rows; y++)
            {
                for (int x = 0; x < 5; x++)
                {
                    addSlotToContainer(new Slot(sentry, slotID++, 10 + 18 * x, 10 + 18 * y));
                }
            }

            //Battery slots
            addSlotToContainer(new Slot(sentry, slotID++, 120, 10));
            addSlotToContainer(new Slot(sentry, slotID++, 120, 29));
        }

        //Player inventory
        addPlayerInventory(player);
    }
}
