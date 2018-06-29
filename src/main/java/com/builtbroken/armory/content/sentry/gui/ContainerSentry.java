package com.builtbroken.armory.content.sentry.gui;

import com.builtbroken.armory.content.sentry.Sentry;
import com.builtbroken.armory.content.sentry.imp.ISentryHost;
import com.builtbroken.mc.prefab.gui.ContainerBase;
import com.builtbroken.mc.prefab.gui.slot.SlotAmmo;
import com.builtbroken.mc.prefab.gui.slot.SlotEnergyItem;
import com.builtbroken.mc.prefab.gui.slot.SlotOutput;
import net.minecraft.entity.player.EntityPlayer;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 4/9/2017.
 */
public class ContainerSentry extends ContainerBase<ISentryHost>
{
    private final int id;

    public ContainerSentry(EntityPlayer player, ISentryHost host, int id)
    {
        super(player, host);
        this.id = id;

        final Sentry sentry = host.getSentry();
        if (id == 0)
        {
            int slotID = 0;
            //Inventory slots
            if (sentry != null)
            {
                int ammoBaySize = (host.getSentry().getSentryData().getInventoryAmmoEnd() - host.getSentry().getSentryData().getInventoryAmmoStart());
                int rows = (ammoBaySize / 5) + 1; //TODO add scroll bar
                for (int y = 0; y < rows; y++)
                {
                    for (int x = 0; x < 5; x++)
                    {
                        addSlotToContainer(new SlotAmmo(host.getSentry().getInventory(), host.getSentry().getSentryData().getGunData().getAmmoType(), slotID++, 8 + 18 * x, 16 + 18 * y));
                    }
                }

                if (sentry.getEnergyBuffer(null) != null)
                {
                    //Battery slots
                    if (host.getSentry().getSentryData().getBatteryIn() != null) //TODO add scroll bar
                    {
                        int y = 0;
                        for (int i = 0; i < host.getSentry().getSentryData().getBatteryIn().length; i++)
                        {
                            addSlotToContainer(new SlotEnergyItem(host.getSentry().getInventory(), host.getSentry().getSentryData().getBatteryIn()[i], 110, 16 + 18 * (y++)));
                        }
                    }
                    if (host.getSentry().getSentryData().getBatteryOut() != null)
                    {
                        int y = 0;
                        for (int i = 0; i < host.getSentry().getSentryData().getBatteryOut().length; i++)
                        {
                            addSlotToContainer(new SlotOutput(host.getSentry().getInventory(), host.getSentry().getSentryData().getBatteryOut()[i], 128, 16 + 18 * (y++)));
                        }
                    }
                }
            }
        }

        //Player inventory
        addPlayerInventory(player);
    }
}
