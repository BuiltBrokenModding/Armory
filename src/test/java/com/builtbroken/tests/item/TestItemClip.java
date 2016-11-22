package com.builtbroken.tests.item;

import com.builtbroken.armory.content.items.ItemClip;
import com.builtbroken.armory.data.ArmoryDataHandler;
import com.builtbroken.armory.data.ammo.AmmoData;
import com.builtbroken.armory.data.ammo.AmmoType;
import com.builtbroken.armory.data.ammo.ClipData;
import com.builtbroken.armory.data.projectiles.EnumProjectileTypes;
import com.builtbroken.mc.api.data.weapon.IAmmoData;
import com.builtbroken.mc.api.data.weapon.ReloadType;
import com.builtbroken.mc.testing.junit.AbstractTest;
import com.builtbroken.mc.testing.junit.VoltzTestRunner;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import org.junit.runner.RunWith;

import java.io.File;
import java.util.Stack;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 11/21/2016.
 */
@RunWith(VoltzTestRunner.class)
public class TestItemClip extends AbstractTest
{
    private static ItemClip item;

    public void testInit()
    {
        assertTrue(item.isClip(null));
        assertNull(item.getAmmoData(null));
    }

    public void testGetAmmoCount()
    {
        ItemStack stack = new ItemStack(item, 1, 0);

        stack.setTagCompound(new NBTTagCompound());
        stack.getTagCompound().setInteger("ammo", 5);

        assertEquals(5, item.getAmmoCount(stack));
    }

    public void testSetAmmoCount()
    {
        ItemStack stack = new ItemStack(item, 1, 0);

        item.setAmmoCount(stack, 5);

        assertEquals(5, item.getAmmoCount(stack));
    }

    public void testGetAmmoStored()
    {
        ItemStack stack = new ItemStack(item, 1, 0);
        stack.setTagCompound(new NBTTagCompound());

        Stack<IAmmoData> data = new Stack();
        for (int i = 0; i < 4; i++)
        {
            data.add((IAmmoData) ArmoryDataHandler.INSTANCE.get("ammo").get("" + i));
        }

        ItemClip.setAmmoDataStackIntoNBT("ammoData", stack.getTagCompound(), data);

        data = item.getStoredAmmo(stack);
        assertEquals("0", data.get(0).getUniqueID());
        assertEquals("1", data.get(1).getUniqueID());
        assertEquals("2", data.get(2).getUniqueID());
        assertEquals("3", data.get(3).getUniqueID());
    }

    public void testSetAmmoStored()
    {
        ItemStack stack = new ItemStack(item, 1, 0);
        stack.setTagCompound(new NBTTagCompound());

        Stack<IAmmoData> data = new Stack();
        for (int i = 0; i < 4; i++)
        {
            data.add((IAmmoData) ArmoryDataHandler.INSTANCE.get("ammo").get("" + i));
        }

        item.setAmmoStored(stack, data);
        assertNotNull(stack.getTagCompound());
        assertTrue(stack.getTagCompound().hasKey("ammoData"));
        assertEquals(4, stack.getTagCompound().getCompoundTag("ammoData").getInteger("number"));
        assertEquals("0", stack.getTagCompound().getCompoundTag("ammoData").getString("round0"));
        assertEquals("1", stack.getTagCompound().getCompoundTag("ammoData").getString("round1"));
        assertEquals("2", stack.getTagCompound().getCompoundTag("ammoData").getString("round2"));
        assertEquals("3", stack.getTagCompound().getCompoundTag("ammoData").getString("round3"));
    }

    public void testLoadAmmo()
    {
        ItemStack stack = new ItemStack(item, 1, 0);
        stack.setTagCompound(new NBTTagCompound());

        Stack<IAmmoData> data = new Stack();
        for (int i = 0; i < 4; i++)
        {
            data.add((IAmmoData) ArmoryDataHandler.INSTANCE.get("ammo").get("" + i));
        }

        item.setAmmoStored(stack, data);

        item.loadAmmo(stack, (IAmmoData) ArmoryDataHandler.INSTANCE.get("ammo").get("5"), 1);
        item.loadAmmo(stack, (IAmmoData) ArmoryDataHandler.INSTANCE.get("ammo").get("4"), 1);

        data = item.getStoredAmmo(stack);
        assertEquals("0", data.get(0).getUniqueID());
        assertEquals("1", data.get(1).getUniqueID());
        assertEquals("2", data.get(2).getUniqueID());
        assertEquals("3", data.get(3).getUniqueID());
        assertEquals("5", data.get(4).getUniqueID());
        assertEquals("4", data.get(5).getUniqueID());
    }

    public void testConsumeAmmo()
    {
        ItemStack stack = new ItemStack(item, 1, 0);
        stack.setTagCompound(new NBTTagCompound());

        Stack<IAmmoData> data = new Stack();
        for (int i = 0; i < 4; i++)
        {
            data.add((IAmmoData) ArmoryDataHandler.INSTANCE.get("ammo").get("" + i));
        }

        item.setAmmoStored(stack, data);

        item.consumeAmmo(null, null, stack, 2);
        assertEquals("0", data.get(0).getUniqueID());
        assertEquals("1", data.get(1).getUniqueID());
    }

    @Override
    public void setUpForEntireClass()
    {
        item = new ItemClip(); //TODO register item for more valid tests

        File folder = new File(System.getProperty("user.dir"), "tmp");
        AmmoType type = new AmmoType("9mm", "9mm", EnumProjectileTypes.BULLET);
        ArmoryDataHandler.INSTANCE.add(new ArmoryDataHandler.ArmoryData(folder, "clip"));
        ArmoryDataHandler.INSTANCE.get("clip").metaToEntry.put(0, new ClipData("clip", "testClip", ReloadType.CLIP, type, 20));


        ArmoryDataHandler.INSTANCE.add(new ArmoryDataHandler.ArmoryData(folder, "ammo"));
        for (int i = 0; i < 6; i++)
        {
            ArmoryDataHandler.INSTANCE.get("ammo").add(new AmmoData("" + i, "ammo" + i, type, "impact", i, -1));
        }
    }

    @Override
    public void tearDownForEntireClass()
    {
        item = null;
        ArmoryDataHandler.INSTANCE.DATA.remove("ammo");
    }
}
