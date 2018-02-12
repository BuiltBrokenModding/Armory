package com.builtbroken.armory.content.sentry.gui;

import com.builtbroken.armory.content.sentry.tile.TileSentry;
import com.builtbroken.mc.prefab.gui.ContainerBase;
import com.builtbroken.mc.prefab.gui.slot.SlotAmmo;
import com.builtbroken.mc.prefab.gui.slot.SlotEnergyItem;
import com.builtbroken.mc.prefab.gui.slot.SlotOutput;
import net.minecraft.entity.player.EntityPlayer;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 4/9/2017.
 */
public class ContainerSentry extends ContainerBase
{
    private final int id;
    public final TileSentry sentry;

    public ContainerSentry(EntityPlayer player, TileSentry sentry, int id)
    {
        super(player, sentry);
        this.sentry = sentry;
        this.id = id;
        if (id == 0)
        {
            int slotID = 0;
            //Inventory slots
            if (sentry != null && sentry.getSentry() != null)
            {
                int ammoBaySize = (sentry.getSentry().getSentryData().getInventoryAmmoEnd() - sentry.getSentry().getSentryData().getInventoryAmmoStart());
                int rows = (ammoBaySize / 5) + 1; //TODO add scroll bar
                for (int y = 0; y < rows; y++)
                {
                    for (int x = 0; x < 5; x++)
                    {
                        addSlotToContainer(new SlotAmmo(sentry, sentry.getSentry().getSentryData().getGunData().getAmmoType(), slotID++, 8 + 18 * x, 16 + 18 * y));
                    }
                }

                if (sentry.getEnergyBufferSize() > 0)
                {
                    //Battery slots
                    if (sentry.getSentry().getSentryData().getBatteryIn() != null) //TODO add scroll bar
                    {
                        int y = 0;
                        for (int i = 0; i < sentry.getSentry().getSentryData().getBatteryIn().length; i++)
                        {
                            addSlotToContainer(new SlotEnergyItem(sentry, sentry.getSentry().getSentryData().getBatteryIn()[i], 110, 16 + 18 * (y++)));
                        }
                    }
                    if (sentry.getSentry().getSentryData().getBatteryOut() != null)
                    {
                        int y = 0;
                        for (int i = 0; i < sentry.getSentry().getSentryData().getBatteryOut().length; i++)
                        {
                            addSlotToContainer(new SlotOutput(sentry, sentry.getSentry().getSentryData().getBatteryOut()[i], 128, 16 + 18 * (y++)));
                        }
                    }
                }
            }
        }

        //Player inventory
        addPlayerInventory(player);
    }
}
