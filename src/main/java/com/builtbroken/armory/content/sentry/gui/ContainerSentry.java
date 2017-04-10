package com.builtbroken.armory.content.sentry.gui;

import com.builtbroken.armory.content.sentry.TileSentry;
import com.builtbroken.mc.prefab.gui.ContainerBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 4/9/2017.
 */
public class ContainerSentry extends ContainerBase
{
    public ContainerSentry(EntityPlayer player, TileSentry inventory)
    {
        super(player, inventory);
        addSlotToContainer(new Slot(inventory, 0, 10, 10));
        addSlotToContainer(new Slot(inventory, 1, 30, 10));
        addSlotToContainer(new Slot(inventory, 2, 30, 29));
        addPlayerInventory(player);
    }
}
