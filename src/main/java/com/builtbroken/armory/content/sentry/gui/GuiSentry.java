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
import com.builtbroken.mc.prefab.gui.buttons.GuiButtonCheck;
import com.builtbroken.mc.prefab.gui.buttons.GuiImageButton;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.awt.*;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 4/9/2017.
 */
public class GuiSentry extends GuiContainerBase<TileSentry>
{
    public static final ResourceLocation GUI_BUTTONS = new ResourceLocation(Armory.DOMAIN, "textures/gui/gui.buttons.32pix.png");
    public static final int TARGET_LIST_SPACE_Y = 10;

    private final int id;
    private GuiImageButton mainWindowButton;
    private GuiImageButton targetWindowButton;
    private GuiImageButton permissionWindowButton;
    private GuiImageButton upgradeWindowButton;
    private GuiImageButton settingsWindowButton;

    private GuiButton2 onButton;
    private GuiButton2 offButton;

    private GuiButton2 scrollUpButton;
    private GuiButton2 scrollDownButton;

    private int scrollTargetList = 0;
    private GuiButtonCheck[][] targetListButtons;

    public GuiSentry(EntityPlayer player, TileSentry sentry, int id)
    {
        super(new ContainerSentry(player, sentry, id), sentry);
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
        onButton = (GuiButton2) add(GuiButton9px.newOnButton(10, x, y - 10).setEnabled(!host.getSentry().turnedOn));
        offButton = (GuiButton2) add(GuiButton9px.newOffButton(11, x + 9, y - 10).setEnabled(host.getSentry().turnedOn));


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
                int tx = 114;
                int ty = 17;
                x = guiLeft + tx;
                y = guiTop + ty;
                int rows = Math.min(6, host.getSentry().getSentryData().getAllowedTargetTypes().length);
                targetListButtons = new GuiButtonCheck[rows][TargetMode.values().length];
                for (int i = 0; i < rows; i++)
                {
                    int yOffset = (i * TARGET_LIST_SPACE_Y);
                    targetListButtons[i][0] = addButton(new GuiButtonCheck(20 + i, x, y + yOffset, 1, false));
                    targetListButtons[i][1] = addButton(new GuiButtonCheck(30 + i, x + 9, y + yOffset, 1, false));
                    targetListButtons[i][2] = addButton(new GuiButtonCheck(40 + i, x + 9 * 2, y + yOffset, 1, false));
                    targetListButtons[i][3] = addButton(new GuiButtonCheck(50 + i, x + 9 * 3, y + yOffset, 1, false));
                    targetListButtons[i][4] = addButton(new GuiButtonCheck(60 + i, x + 9 * 4, y + yOffset, 1, false));

                    addToolTip(new Rectangle(tx, ty + yOffset, tx + 9, ty + yOffset + 9), "sentry.gui.tooltip.target.all", true);
                    addToolTip(new Rectangle(tx + 9, ty + yOffset, tx + 9 * 2, ty + yOffset + 9), "sentry.gui.tooltip.target.friendly.non", true);
                    addToolTip(new Rectangle(tx + 9 * 2, ty + yOffset, tx + 9 * 3, ty + yOffset + 9), "sentry.gui.tooltip.target.hostile", true);
                    addToolTip(new Rectangle(tx + 9 * 3, ty + yOffset, tx + 9 * 4, ty + yOffset + 9), "sentry.gui.tooltip.target.neutral", true);
                    addToolTip(new Rectangle(tx + 9 * 4, ty + yOffset, tx + 9 * 5, ty + yOffset + 9), "sentry.gui.tooltip.target.none", true);
                }

                //Add scroll bar buttons
                x = guiLeft + 161;
                y = guiTop + 14;
                scrollUpButton = (GuiButton2) add(GuiButton9px.newUpButton(12, x, y).disable()); //Up is disabled by default

                y = guiTop + 69;
                scrollDownButton = (GuiButton2) add(GuiButton9px.newDownButton(13, x, y).setEnabled(host.getSentry().getSentryData().getAllowedTargetTypes().length > 6)); //Down is disabled if not enough entries
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

        if (host != null && host.getSentry() != null)
        {
            //Update power state
            if (host.getSentry().turnedOn)
            {
                onButton.disable();
                offButton.enable();
            }
            else
            {
                onButton.enable();
                offButton.disable();
            }

            //Target settings GUI
            if (id == 1)
            {
                //Update check state for target mode settings
                if (targetListButtons != null)
                {
                    for (int i = 0; i < targetListButtons.length; i++)
                    {
                        int index = i + scrollTargetList;
                        if (index < host.getSentry().getSentryData().getAllowedTargetTypes().length)
                        {
                            String key = host.getSentry().getSentryData().getAllowedTargetTypes()[index];
                            TargetMode mode = host.getSentry().targetModes.get(key);
                            if (mode != null)
                            {
                                //Enable all buttons
                                for (int z = 0; z < targetListButtons[i].length; z++)
                                {
                                    targetListButtons[i][z].enable();
                                    targetListButtons[i][z].uncheck();
                                }
                                targetListButtons[i][mode.ordinal()].disable();
                                targetListButtons[i][mode.ordinal()].check();
                            }
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
            host.sendPacketToServer(new PacketTile(host, 3, true)); //TODO move to method in host
        }
        //Turn sentry off
        else if (buttonId == 11)
        {
            host.sendPacketToServer(new PacketTile(host, 3, false)); //TODO move to method in host
        }
        //Tab switch buttons
        else if (buttonId >= 0 && buttonId < TileSentry.MAX_GUI_TABS && buttonId != id)
        {
            host.sendPacketToServer(new PacketOpenGUI(host, buttonId));
        }
        //Main GUI
        else if (id == 0)
        {

        }
        //Target setting GUI
        else if (id == 1)
        {
            //Scroll up button
            if (buttonId == 12)
            {
                if (scrollTargetList > 0)
                {
                    scrollTargetList--;
                    scrollDownButton.enable();
                    if (scrollTargetList == 0)
                    {
                        scrollUpButton.disable();
                    }
                }
                else
                {
                    scrollUpButton.disable();
                }
            }
            //Scroll down button
            else if (buttonId == 13)
            {
                int maxScroll = host.getSentry().getSentryData().getAllowedTargetTypes().length - 6;
                if (scrollTargetList < maxScroll)
                {
                    scrollTargetList++;
                    scrollUpButton.enable();
                    if (scrollTargetList == maxScroll)
                    {
                        scrollDownButton.disable();
                    }
                }
                else
                {
                    scrollDownButton.disable();
                }
            }
            else if (buttonId >= 60)
            {
                int index = buttonId - 60 + scrollTargetList;  //TODO move to method in host
                if (index < host.getSentry().getSentryData().getAllowedTargetTypes().length)
                {
                    PacketTile packetTile = new PacketTile(host, 4, host.getSentry().getSentryData().getAllowedTargetTypes()[index], (byte) TargetMode.NONE.ordinal());
                    host.sendPacketToServer(packetTile);
                }
            }
            else if (buttonId >= 50)
            {
                int index = buttonId - 50 + scrollTargetList;  //TODO move to method in host
                if (index < host.getSentry().getSentryData().getAllowedTargetTypes().length)
                {
                    PacketTile packetTile = new PacketTile(host, 4, host.getSentry().getSentryData().getAllowedTargetTypes()[index], (byte) TargetMode.NEUTRAL.ordinal());
                    host.sendPacketToServer(packetTile);
                }
            }
            else if (buttonId >= 40)
            {
                int index = buttonId - 40 + scrollTargetList;  //TODO move to method in host
                if (index < host.getSentry().getSentryData().getAllowedTargetTypes().length)
                {
                    PacketTile packetTile = new PacketTile(host, 4, host.getSentry().getSentryData().getAllowedTargetTypes()[index], (byte) TargetMode.HOSTILE.ordinal());
                    host.sendPacketToServer(packetTile);
                }
            }
            else if (buttonId >= 30)
            {
                int index = buttonId - 30 + scrollTargetList;  //TODO move to method in host
                if (index < host.getSentry().getSentryData().getAllowedTargetTypes().length)
                {
                    PacketTile packetTile = new PacketTile(host, 4, host.getSentry().getSentryData().getAllowedTargetTypes()[index], (byte) TargetMode.NOT_FRIEND.ordinal());
                    host.sendPacketToServer(packetTile);
                }
            }
            else if (buttonId >= 20)
            {
                int index = buttonId - 20 + scrollTargetList;  //TODO move to method in host
                if (index < host.getSentry().getSentryData().getAllowedTargetTypes().length)
                {
                    PacketTile packetTile = new PacketTile(host, 4, host.getSentry().getSentryData().getAllowedTargetTypes()[index], (byte) TargetMode.ALL.ordinal());
                    host.sendPacketToServer(packetTile);
                }
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

        //Target setting GUI
        if (id == 1)
        {
            //TODO add background behind scroll area
            //TODO add scroll bar
            this.mc.renderEngine.bindTexture(SharedAssets.GREY_TEXTURE_40pAlpha);

            float c = 100f / 255f;
            GL11.glColor4f(c, c, c, 1.0F);

            this.drawTexturedModalRect(guiLeft + 5, guiTop + 14, 0, 0, 163, 14 + (5 * TARGET_LIST_SPACE_Y));

            c = 160f / 255f;
            GL11.glColor4f(c, c, c, 1.0F);

            this.drawTexturedModalRect(guiLeft + 7, guiTop + 17, 0, 0, 152, 9 + (5 * TARGET_LIST_SPACE_Y));

            c = 192f / 255f;
            GL11.glColor4f(c, c, c, 1.0F);

            if (targetListButtons != null)
            {
                for (int i = 0; i < targetListButtons.length; i++)
                {
                    if ((i + scrollTargetList) % 2 == 0)
                    {
                        this.drawTexturedModalRect(guiLeft + 7, guiTop + (17 + (i * TARGET_LIST_SPACE_Y)), 0, 0, 152, 10);
                    }
                }
            }


            //Set texture and reset color
            this.mc.renderEngine.bindTexture(SharedAssets.GUI_COMPONENTS_BARS);
            c = 192f / 255f;
            GL11.glColor4f(c, c, c, 1.0F);

            //Position cache
            final int yStart = 23;
            final int xStart = xSize - 15;

            //Size cache
            final int topHeight = 20;
            final int bottomHeight = 26;
            final int totalSize = topHeight + bottomHeight;

            //Render background for scroll bar TODO make reusable
            drawTexturedModalRect(guiLeft + xStart, guiTop + yStart, 16, 0, 9, topHeight);
            drawTexturedModalRect(guiLeft + xStart, guiTop + yStart + topHeight, 16, 139 - bottomHeight, 9, bottomHeight);

            //Render scroll bar
            int maxScroll = host.getSentry().getSentryData().getAllowedTargetTypes().length - 6;
            float scrollBar = (float) scrollTargetList / (float) maxScroll;
            float heightP = Math.min(1f, 6f / (float) host.getSentry().getSentryData().getAllowedTargetTypes().length);
            int height = (int) (heightP * totalSize);
            int yPos = Math.max((int) (scrollBar * totalSize) - height + yStart, yStart);

            //Set color to red and render texture
            c = 80f / 255f;
            GL11.glColor4f(c, c, c, 1.0F);
            drawTexturedModalRect(guiLeft + xStart, guiTop + yPos, 16, 0, 9, 2 + height);
        }
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
    {
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);
        //Main GUI
        if (id == 0)
        {
            drawString(LanguageUtility.getLocal("sentry.gui.ammo.bay"), 7, 4);
            if (host.getEnergyBufferSize() > 0)
            {
                drawString(LanguageUtility.getLocal("sentry.gui.battery.bay"), 110, 4);
            }
            if (host.getSentry().gunInstance._clip != null)
            {
                String translation = String.format(LanguageUtility.getLocal("sentry.gui.ammo"), host.getSentry().gunInstance._clip.getAmmoCount(), host.getSentry().gunInstance._clip.getMaxAmmo());
                drawString(translation, 7, 52);
            }
            else
            {
                drawString(LanguageUtility.getLocal("sentry.gui.ammo.empty"), 7, 52);
            }
            drawString(LanguageUtility.getLocal("sentry.gui.inventory"), 7, 74);
        }
        //Target Settings GUI
        else if (id == 1)
        {
            drawString(LanguageUtility.getLocal("sentry.gui.targeting"), 7, 4);

            for (int i = 0; i < targetListButtons.length; i++)
            {
                int index = i + scrollTargetList;
                if (index < host.getSentry().getSentryData().getAllowedTargetTypes().length)
                {
                    drawString(LanguageUtility.getLocal("entry.type." + host.getSentry().getSentryData().getAllowedTargetTypes()[index]), 8, 17 + (i * TARGET_LIST_SPACE_Y));
                }
            }
        }
        //Permission GUI
        else if (id == 2)
        {
            drawString(LanguageUtility.getLocal("sentry.gui.permissions"), 7, 4);

            drawString("Not currently Implemented", 7, 20, Color.RED.getRGB());
        }
        //Upgrades GUI
        else if (id == 3)
        {
            drawString(LanguageUtility.getLocal("sentry.gui.upgrades"), 7, 4);

            drawString(LanguageUtility.getLocal("Not currently Implemented"), 7, 20, Color.RED.getRGB());
        }
        //Settings GUI
        else if (id == 4)
        {
            drawString(LanguageUtility.getLocal("sentry.gui.settings"), 7, 4);

            drawString(LanguageUtility.getLocal("Not currently Implemented"), 7, 20, Color.RED.getRGB());
        }
    }
}
