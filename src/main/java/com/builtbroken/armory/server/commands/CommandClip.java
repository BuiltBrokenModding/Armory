package com.builtbroken.armory.server.commands;

import com.builtbroken.armory.Armory;
import com.builtbroken.armory.api.ArmoryAPI;
import com.builtbroken.armory.data.ArmoryDataHandler;
import com.builtbroken.armory.data.ammo.AmmoData;
import com.builtbroken.armory.data.clip.ClipData;
import com.builtbroken.mc.api.data.weapon.IAmmoData;
import com.builtbroken.mc.core.commands.prefab.SubCommand;
import com.builtbroken.mc.imp.transform.vector.Location;
import com.builtbroken.mc.prefab.inventory.InventoryUtility;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;

import java.util.List;

/**
 * Chat command for giving ammo clips
 *
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 12/24/2017.
 */
public class CommandClip extends SubCommand
{
    public CommandClip()
    {
        super("clip");
    }

    @Override
    public boolean handleEntityPlayerCommand(EntityPlayer player, String[] args)
    {
        if (args.length == 1)
        {
            ArmoryDataHandler.ArmoryData<ClipData> clipData = ArmoryDataHandler.INSTANCE.get(ArmoryAPI.CLIP_ID);

            ClipData clipEntry = clipData.get(args[0]);
            if (clipEntry != null)
            {
                if (clipEntry.getAmmoType().getAmmoData().size() > 0)
                {
                    IAmmoData ammoData = clipEntry.getAmmoType().getAmmoData().get(0);
                    ItemStack stack = new ItemStack(Armory.itemClip, 1, clipEntry.meta);
                    Armory.itemClip.loadAmmo(stack, ammoData, clipEntry.maxAmmo);

                    if (!player.inventory.addItemStackToInventory(stack))
                    {
                        InventoryUtility.dropItemStack(new Location(player), stack);
                    }
                }
                else
                {
                    player.addChatMessage(new ChatComponentText("Failed to find any valid ammo types for ammo container with id '" + args[0] + "'"));
                }
            }

            return true;
        }
        else if (args.length >= 5)
        {
            handle(player, args[0].equalsIgnoreCase("self") ? player : getPlayer(player, args[0]), args);
            return true;
        }
        return handleHelp(player, args);
    }

    @Override
    public boolean handleConsoleCommand(ICommandSender sender, String[] args)
    {
        if (args.length >= 5)
        {
            handle(sender, getPlayer(sender, args[0]), args);
            return true;
        }
        return handleHelp(sender, args);
    }

    protected void handle(ICommandSender sender, EntityPlayer target, String[] args)
    {
        final String userName = args[0];
        final String itemID = args[1];
        final String ammoID = args[2];
        final String bulletCountString = args[3];
        final String stackSizeString = args[4];

        if (target != null)
        {
            int bulletCount;
            int stackSize;

            try
            {
                bulletCount = Integer.parseInt(bulletCountString);
            }
            catch (NumberFormatException e)
            {
                sender.addChatMessage(new ChatComponentText("Failed to parse bullet count '" + bulletCountString + "' as an integer"));
                return;
            }

            try
            {
                stackSize = Integer.parseInt(stackSizeString);
            }
            catch (NumberFormatException e)
            {
                sender.addChatMessage(new ChatComponentText("Failed to parse stack size '" + stackSizeString + "' as an integer"));
                return;
            }

            ArmoryDataHandler.ArmoryData<ClipData> clipData = ArmoryDataHandler.INSTANCE.get(ArmoryAPI.CLIP_ID);
            ArmoryDataHandler.ArmoryData<AmmoData> ammoData = ArmoryDataHandler.INSTANCE.get(ArmoryAPI.AMMO_ID);

            ClipData clipEntry = clipData.get(itemID);
            if (clipEntry != null)
            {
                IAmmoData ammoEntry;
                if (ammoID.equalsIgnoreCase("any"))
                {
                    if (clipEntry.getAmmoType().getAmmoData().size() > 0)
                    {
                        ammoEntry = clipEntry.getAmmoType().getAmmoData().get(0);
                    }
                    else
                    {
                        sender.addChatMessage(new ChatComponentText("Failed to find any valid ammo types for ammo container with id '" + itemID + "'"));
                        return;
                    }
                }
                else
                {
                    ammoEntry = ammoData.get(ammoID);
                    if (ammoEntry == null)
                    {
                        sender.addChatMessage(new ChatComponentText("Failed to find any valid ammo with id '" + ammoID + "'"));
                        return;
                    }
                    else if (ammoEntry.getAmmoType() != clipEntry.getAmmoType())
                    {
                        sender.addChatMessage(new ChatComponentText("Ammo with id '" + ammoID + "' is not valid for ammo container '" + itemID + "'"));
                        return;
                    }
                }
                ItemStack stack = new ItemStack(Armory.itemClip, Math.min(64, Math.max(1, stackSize)), clipEntry.meta);
                Armory.itemClip.loadAmmo(stack, ammoEntry, bulletCount);

                if (!target.inventory.addItemStackToInventory(stack))
                {
                    InventoryUtility.dropItemStack(new Location(target), stack);
                }

                if (args.length > 5)
                {
                    if (args[5].equalsIgnoreCase("none") || args[5].equalsIgnoreCase("null"))
                    {
                        return;
                    }

                    //TODO allow injecting info into the string
                    //ex player name, item ids, ammo types, time of day, custom data, etc

                    String msg = "";
                    for (int i = 5; i < args.length; i++)
                    {
                        msg += " " + args[i];
                    }
                    target.addChatComponentMessage(new ChatComponentText(msg));
                }
                else
                {
                    target.addChatComponentMessage(new ChatComponentText("You have been given " + stackSizeString + " containers of " + itemID + " filled with " + ammoID));
                }
            }
            else
            {
                sender.addChatMessage(new ChatComponentText("Failed to find ammo container with id '" + itemID + "'"));
            }

        }
        else
        {
            sender.addChatMessage(new ChatComponentText("Failed to find player '" + userName + "'"));
        }
    }

    @Override
    public void getHelpOutput(ICommandSender sender, List<String> items)
    {
        items.add("[clipID]");
        items.add("[playerName/self] [clipID] [ammoID/any] [bulletCount] [stackSize] <message...>");
    }
}
