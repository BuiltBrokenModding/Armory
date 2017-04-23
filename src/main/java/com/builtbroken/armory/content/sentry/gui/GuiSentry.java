package com.builtbroken.armory.content.sentry.gui;

import com.builtbroken.armory.Armory;
import com.builtbroken.armory.content.sentry.tile.TileSentry;
import com.builtbroken.mc.core.network.packet.PacketTile;
import com.builtbroken.mc.core.network.packet.callback.PacketOpenGUI;
import com.builtbroken.mc.prefab.gui.GuiButton2;
import com.builtbroken.mc.prefab.gui.GuiContainerBase;
import com.builtbroken.mc.prefab.gui.buttons.GuiImageButton;
import com.builtbroken.mc.prefab.gui.buttons.GuiOnOffButton;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.util.ResourceLocation;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 4/9/2017.
 */
public class GuiSentry extends GuiContainerBase
{
    public static final ResourceLocation GUI_BUTTONS = new ResourceLocation(Armory.DOMAIN, "textures/gui/gui.buttons.32pix.png");

    private final int id;
    private GuiImageButton mainWindowButton;
    private GuiImageButton targetWindowButton;
    private GuiImageButton permissionWindowButton;
    private GuiImageButton upgradeWindowButton;
    private GuiImageButton settingsWindowButton;

    private GuiButton2 onButton;
    private GuiButton2 offButton;


    TileSentry tile;

    public GuiSentry(EntityPlayer player, TileSentry sentry, int id)
    {
        super(new ContainerSentry(player, sentry, id));
        this.tile = sentry;
        this.id = id;
    }

    @Override
    public void initGui()
    {
        super.initGui();
        mainWindowButton = addButton(GuiImageButton.newButton18(0, guiLeft - 18, guiTop + 5, 0, 0).setTexture(GUI_BUTTONS));
        targetWindowButton = addButton(GuiImageButton.newButton18(1, guiLeft - 18, guiTop + 5 + 19, 1, 0).setTexture(GUI_BUTTONS));
        permissionWindowButton = addButton(GuiImageButton.newButton18(2, guiLeft - 18, guiTop + 5 + 19 * 2, 2, 0).setTexture(GUI_BUTTONS));
        upgradeWindowButton = addButton(GuiImageButton.newButton18(3, guiLeft - 18, guiTop + 5 + 19 * 3, 7, 0).setTexture(GUI_BUTTONS));
        settingsWindowButton = addButton(GuiImageButton.newButton18(4, guiLeft - 18, guiTop + 5 + 19 * 4, 5, 0).setTexture(GUI_BUTTONS));

        int x = guiLeft;
        int y = guiTop;
        switch (id)
        {
            case 0:
                x += 153;
                y += 5;
                mainWindowButton.disable();
                onButton = addButton(new GuiOnOffButton(10, x, y, true).setEnabled(!tile.getSentry().turnedOn));
                offButton = addButton(new GuiOnOffButton(11, x + 9, y, false).setEnabled(tile.getSentry().turnedOn));
                break;
            case 1:
                x += 153;
                y += 5;
                targetWindowButton.disable();
                addButton(GuiImageButton.newSaveButton(10, x, y));
                break;
            case 2:
                x += 153;
                y += 5;
                permissionWindowButton.disable();
                addButton(GuiImageButton.newSaveButton(10, x, y));
                break;
            case 3:
                upgradeWindowButton.disable();
                break;
            case 4:
                x += 153;
                y += 5;
                settingsWindowButton.disable();
                addButton(GuiImageButton.newSaveButton(10, x, y));
                break;
        }
    }

    @Override
    public void updateScreen()
    {
        super.updateScreen();
        if (id == 0)
        {
            if (tile.getSentry().turnedOn)
            {
                onButton.disable();
                offButton.enable();
            }
            else
            {
                onButton.enable();
                offButton.disable();
            }
        }
    }

    @Override
    protected void actionPerformed(GuiButton button)
    {
        final int buttonId = button.id;
        //Tab switch buttons
        if (buttonId >= 0 && buttonId < TileSentry.MAX_GUI_TABS && buttonId != id)
        {
            tile.sendPacketToServer(new PacketOpenGUI(tile, buttonId));
        }
        else if (id == 0)
        {
            //Turn sentry on
            if (buttonId == 10)
            {
                tile.sendPacketToServer(new PacketTile(tile, 3, true));
            }
            //Turn sentry off
            else if (buttonId == 11)
            {
                tile.sendPacketToServer(new PacketTile(tile, 3, false));
            }
        }
        else if (id == 2)
        {
            //Save permission ID
            if (buttonId == 10)
            {

            }
        }
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float f, int mouseX, int mouseY)
    {
        super.drawGuiContainerBackgroundLayer(f, mouseX, mouseY);
        for (Object object : inventorySlots.inventorySlots)
        {
            if (object instanceof Slot)
            {
                drawSlot((Slot) object);
            }
        }
    }
}
