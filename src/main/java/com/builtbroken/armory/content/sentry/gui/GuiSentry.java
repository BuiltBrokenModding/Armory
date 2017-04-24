package com.builtbroken.armory.content.sentry.gui;

import com.builtbroken.armory.Armory;
import com.builtbroken.armory.content.sentry.TargetMode;
import com.builtbroken.armory.content.sentry.tile.TileSentry;
import com.builtbroken.mc.client.SharedAssets;
import com.builtbroken.mc.core.network.packet.PacketTile;
import com.builtbroken.mc.core.network.packet.callback.PacketOpenGUI;
import com.builtbroken.mc.imp.transform.region.Rectangle;
import com.builtbroken.mc.lib.helper.LanguageUtility;
import com.builtbroken.mc.prefab.gui.GuiButton2;
import com.builtbroken.mc.prefab.gui.GuiContainerBase;
import com.builtbroken.mc.prefab.gui.buttons.GuiButton9px;
import com.builtbroken.mc.prefab.gui.buttons.GuiImageButton;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 4/9/2017.
 */
public class GuiSentry extends GuiContainerBase
{
    public static final ResourceLocation GUI_BUTTONS = new ResourceLocation(Armory.DOMAIN, "textures/gui/gui.buttons.32pix.png");
    public static final int TARGET_LIST_SPACE_Y = 11;

    private final int id;
    private GuiImageButton mainWindowButton;
    private GuiImageButton targetWindowButton;
    private GuiImageButton permissionWindowButton;
    private GuiImageButton upgradeWindowButton;
    private GuiImageButton settingsWindowButton;

    private GuiButton2 onButton;
    private GuiButton2 offButton;

    TileSentry tile;

    private int scrollTargetList = 0;
    private GuiButton2[][] targetListButtons;

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
        int x = guiLeft - 18;
        int y = guiTop + 10;

        //Menu Tabs
        mainWindowButton = addButton(GuiImageButton.newButton18(0, x, y, 0, 0).setTexture(GUI_BUTTONS));
        targetWindowButton = addButton(GuiImageButton.newButton18(1, x, y + 19, 1, 0).setTexture(GUI_BUTTONS));
        permissionWindowButton = addButton(GuiImageButton.newButton18(2, x, y + 19 * 2, 2, 0).setTexture(GUI_BUTTONS));
        upgradeWindowButton = addButton(GuiImageButton.newButton18(3, x, y + 19 * 3, 7, 0).setTexture(GUI_BUTTONS));
        settingsWindowButton = addButton(GuiImageButton.newButton18(4, x, y + 19 * 4, 5, 0).setTexture(GUI_BUTTONS));

        //Power buttons
        onButton = addButton(GuiButton9px.newOnButton(10, x, y - 10).setEnabled(!tile.getSentry().turnedOn));
        offButton = addButton(GuiButton9px.newOffButton(11, x + 9, y - 10).setEnabled(tile.getSentry().turnedOn));


        //Per tab components
        x = guiLeft;
        y = guiTop;
        switch (id)
        {
            case 0:
                mainWindowButton.disable();
                break;
            case 1:
                targetWindowButton.disable();
                //Target list
                int tx = 115;
                int ty = 14;
                x = guiLeft + tx;
                y = guiTop + ty;
                int rows = Math.min(6, tile.getSentry().getSentryData().getAllowedTargetTypes().length);
                targetListButtons = new GuiButton2[rows][TargetMode.values().length];
                for (int i = 0; i < rows; i++)
                {
                    int yOffset = (i * TARGET_LIST_SPACE_Y);
                    targetListButtons[i][0] = addButton(GuiButton9px.newBlankButton(20 + i, x, y + yOffset));
                    targetListButtons[i][1] = addButton(GuiButton9px.newBlankButton(30 + i, x + 9, y + yOffset));
                    targetListButtons[i][2] = addButton(GuiButton9px.newBlankButton(40 + i, x + 9 * 2, y + yOffset));
                    targetListButtons[i][3] = addButton(GuiButton9px.newBlankButton(50 + i, x + 9 * 3, y + yOffset));
                    targetListButtons[i][4] = addButton(GuiButton9px.newBlankButton(60 + i, x + 9 * 4, y + yOffset));

                    tooltips.put(new Rectangle(tx, ty + yOffset, tx + 9, ty + yOffset + 9), "Target All");
                    tooltips.put(new Rectangle(tx + 9, ty + yOffset, tx + 9 * 2, ty + yOffset + 9), "Target Non-Friendly");
                    tooltips.put(new Rectangle(tx + 9 * 2, ty + yOffset, tx + 9 * 3, ty + yOffset + 9), "Target Hostile");
                    tooltips.put(new Rectangle(tx + 9 * 3, ty + yOffset, tx + 9 * 4, ty + yOffset + 9), "Target Neutral");
                    tooltips.put(new Rectangle(tx + 9 * 4, ty + yOffset, tx + 9 * 5, ty + yOffset + 9), "Target None");
                }

                x = guiLeft + 162;
                y = guiTop + 14;
                addButton(GuiButton9px.newPlusButton(12, x, y).disable());

                y = guiTop + 69;
                addButton(GuiButton9px.newMinusButton(13, x, y).setEnabled(tile.getSentry().getSentryData().getAllowedTargetTypes().length > 6));
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

        if (id == 1)
        {
            if (targetListButtons != null)
            {
                for (int i = 0; i < targetListButtons.length; i++)
                {
                    int index = i + scrollTargetList;
                    if (index < tile.getSentry().getSentryData().getAllowedTargetTypes().length)
                    {
                        String key = tile.getSentry().getSentryData().getAllowedTargetTypes()[index];
                        TargetMode mode = tile.getSentry().targetModes.get(key);
                        if (mode != null)
                        {
                            //Enable all buttons
                            for (int z = 0; z < targetListButtons[i].length; z++)
                            {
                                targetListButtons[i][z].enable();
                            }
                            targetListButtons[i][mode.ordinal()].disable();
                        }
                    }
                }
            }
        }
    }

    @Override
    protected void actionPerformed(GuiButton button)
    {
        final int buttonId = button.id;
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
        //Tab switch buttons
        else if (buttonId >= 0 && buttonId < TileSentry.MAX_GUI_TABS && buttonId != id)
        {
            tile.sendPacketToServer(new PacketOpenGUI(tile, buttonId));
        }
        else if (id == 0)
        {

        }
        else if (id == 1)
        {
            //Save permission ID
            if (buttonId == 12)
            {
                if (scrollTargetList > 0)
                {
                    scrollTargetList--;
                }
            }
            else if (buttonId == 13)
            {
                if (scrollTargetList < (tile.getSentry().getSentryData().getAllowedTargetTypes().length) - 1)
                {
                    scrollTargetList++;
                }
            }
            else if (buttonId >= 60)
            {
                int index = buttonId - 60 + scrollTargetList;
                PacketTile packetTile = new PacketTile(tile, 4, tile.getSentry().getSentryData().getAllowedTargetTypes()[index], (byte) TargetMode.NONE.ordinal());
                tile.sendPacketToServer(packetTile);
            }
            else if (buttonId >= 50)
            {
                int index = buttonId - 50 + scrollTargetList;
                PacketTile packetTile = new PacketTile(tile, 4, tile.getSentry().getSentryData().getAllowedTargetTypes()[index], (byte) TargetMode.NEUTRAL.ordinal());
                tile.sendPacketToServer(packetTile);
            }
            else if (buttonId >= 40)
            {
                int index = buttonId - 40 + scrollTargetList;
                PacketTile packetTile = new PacketTile(tile, 4, tile.getSentry().getSentryData().getAllowedTargetTypes()[index], (byte) TargetMode.HOSTILE.ordinal());
                tile.sendPacketToServer(packetTile);
            }
            else if (buttonId >= 30)
            {
                int index = buttonId - 30 + scrollTargetList;
                PacketTile packetTile = new PacketTile(tile, 4, tile.getSentry().getSentryData().getAllowedTargetTypes()[index], (byte) TargetMode.NOT_FRIEND.ordinal());
                tile.sendPacketToServer(packetTile);
            }
            else if (buttonId >= 20)
            {
                int index = buttonId - 20 + scrollTargetList;
                PacketTile packetTile = new PacketTile(tile, 4, tile.getSentry().getSentryData().getAllowedTargetTypes()[index], (byte) TargetMode.ALL.ordinal());
                tile.sendPacketToServer(packetTile);
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

        if (id == 1)
        {
            this.mc.renderEngine.bindTexture(SharedAssets.GREY_TEXTURE_40pAlpha);
            float c = 192f / 255f;
            GL11.glColor4f(c, c, c, 1.0F);

            if (targetListButtons != null)
            {
                for (int i = 0; i < targetListButtons.length; i++)
                {
                    this.drawTexturedModalRect(guiLeft + 7, guiTop + (14 + (i * TARGET_LIST_SPACE_Y)), 0, 0, 129, 9);
                }
            }
        }
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
    {
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);
        if (id == 0)
        {
            drawString(LanguageUtility.getLocal("sentry.gui.ammo.bay"), 7, 4);
            if (tile.getEnergyBufferSize() > 0)
            {
                drawString(LanguageUtility.getLocal("sentry.gui.battery.bay"), 110, 4);
            }
            if (tile.getSentry().gunInstance._clip != null)
            {
                String translation = String.format(LanguageUtility.getLocal("sentry.gui.ammo"), tile.getSentry().gunInstance._clip.getAmmoCount(), tile.getSentry().gunInstance._clip.getMaxAmmo());
                drawString(translation, 7, 52);
            }
            else
            {
                drawString(LanguageUtility.getLocal("sentry.gui.ammo.empty"), 7, 52);
            }
            drawString(LanguageUtility.getLocal("sentry.gui.inventory"), 7, 74);
        }
        else if (id == 1)
        {
            drawString(LanguageUtility.getLocal("sentry.gui.targeting"), 7, 4);

            for (int i = 0; i < targetListButtons.length; i++)
            {
                int index = i + scrollTargetList;
                if (index < tile.getSentry().getSentryData().getAllowedTargetTypes().length)
                {
                    drawString(LanguageUtility.getLocal("entry.type." + tile.getSentry().getSentryData().getAllowedTargetTypes()[index]), 8, 15 + (i * TARGET_LIST_SPACE_Y));
                }
            }
        }
        else if (id == 2)
        {
            drawString(LanguageUtility.getLocal("sentry.gui.permissions"), 7, 4);
        }
        else if (id == 3)
        {
            drawString(LanguageUtility.getLocal("sentry.gui.upgrades"), 7, 4);
        }
        else if (id == 4)
        {
            drawString(LanguageUtility.getLocal("sentry.gui.settings"), 7, 4);
        }
    }
}
