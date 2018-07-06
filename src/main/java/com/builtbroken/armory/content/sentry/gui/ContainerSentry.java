package com.builtbroken.armory.content.sentry.gui;

import com.builtbroken.armory.content.sentry.Sentry;
import com.builtbroken.armory.content.sentry.imp.ISentryHost;
import com.builtbroken.mc.framework.energy.UniversalEnergySystem;
import com.builtbroken.mc.prefab.gui.ContainerBase;
import com.builtbroken.mc.prefab.gui.slot.SlotAmmo;
import com.builtbroken.mc.prefab.gui.slot.SlotEnergyItem;
import com.builtbroken.mc.prefab.gui.slot.SlotOutput;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 4/9/2017.
 */
public class ContainerSentry extends ContainerBase<ISentryHost>
{
    private final int id;

    private int playerInventoryStart;
    private int ammoBayEnd;
    private int batteryBayEnd;

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
                int count = 0;
                for (int y = 0; y < rows && count < ammoBaySize; y++)
                {
                    for (int x = 0; x < 5 && count < ammoBaySize; x++)
                    {
                        addSlotToContainer(new SlotAmmo(host.getSentry().getInventory(), host.getSentry().getSentryData().getGunData().getAmmoType(), slotID++, 8 + 18 * x, 16 + 18 * y));
                        count++;
                    }
                }
                ammoBayEnd = inventorySlots.size();

                if (sentry.getEnergyBuffer(null) != null)
                {
                    //Battery input slots
                    if (host.getSentry().getSentryData().getBatteryIn() != null) //TODO add scroll bar
                    {
                        int y = 0;
                        for (int i = 0; i < host.getSentry().getSentryData().getBatteryIn().length; i++)
                        {
                            addSlotToContainer(new SlotEnergyItem(host.getSentry().getInventory(), host.getSentry().getSentryData().getBatteryIn()[i], 110, 16 + 18 * (y++)));
                        }
                    }
                    batteryBayEnd = inventorySlots.size();

                    //Battery output slots
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
        playerInventoryStart = inventorySlots.size();
        addPlayerInventory(player);
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int slotIndex)
    {
        final int playerInvStart = this.playerInventoryStart;
        final int playerHotBarStart = playerInvStart + 27;
        final int playerInvEnd = playerInvStart + 36;


        ItemStack itemstack = null;
        Slot slot = (Slot) this.inventorySlots.get(slotIndex);

        if (slot != null && slot.getHasStack())
        {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();

            if (slotIndex > playerInvStart)
            {
                if (SlotAmmo.ammoTypeMatch(itemstack1, host.getSentry().getSentryData().getGunData().getAmmoType()))
                {
                    if (!this.mergeItemStack(itemstack1, 0, ammoBayEnd, false))
                    {
                        return null;
                    }
                }
                else if (UniversalEnergySystem.isHandler(itemstack1, null))
                {
                    if (!this.mergeItemStack(itemstack1, ammoBayEnd, batteryBayEnd, false))
                    {
                        return null;
                    }
                }
                else if (slotIndex >= playerInvStart && slotIndex < playerHotBarStart)
                {
                    if (!this.mergeItemStack(itemstack1, playerHotBarStart, playerInvEnd, false))
                    {
                        return null;
                    }
                }
                else if (slotIndex >= playerHotBarStart && slotIndex < playerInvEnd && !this.mergeItemStack(itemstack1, playerInvStart, playerHotBarStart, false))
                {
                    return null;
                }
            }
            else if (!this.mergeItemStack(itemstack1, playerInvStart, playerInvEnd, false))
            {
                return null;
            }

            if (itemstack1.stackSize == 0)
            {
                slot.putStack((ItemStack) null);
            }
            else
            {
                slot.onSlotChanged();
            }

            if (itemstack1.stackSize == itemstack.stackSize)
            {
                return null;
            }

            slot.onPickupFromSlot(player, itemstack1);
        }

        return itemstack;
    }
}
