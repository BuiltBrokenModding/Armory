package com.builtbroken.tests.item;

import com.builtbroken.armory.content.items.ItemAmmo;
import com.builtbroken.mc.testing.junit.AbstractTest;
import com.builtbroken.mc.testing.junit.VoltzTestRunner;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.junit.runner.RunWith;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 11/21/2016.
 */
@RunWith(VoltzTestRunner.class)
public class TestItemAmmo extends AbstractTest
{
    public void testInit()
    {
        ItemAmmo item = new ItemAmmo(); //TODO register item for more valid tests
        //Stack and item input are null due to item not being registered
        assertTrue(item.isAmmo(null));
        assertFalse(item.isClip(null));

        //getAmmoData() is not tested as that return is already tested in other classes
    }

    public void testConsumeAmmo()
    {
        ItemAmmo item = new ItemAmmo(); //TODO register item for more valid tests

        ItemStack stack = new ItemStack((Item) null, 5);
        item.consumeAmmo(null, null, stack, 2);
        assertEquals(3, item.getAmmoCount(stack));
    }

    public void testGetAmmoCount()
    {
        ItemAmmo item = new ItemAmmo(); //TODO register item for more valid tests
        assertEquals(5, item.getAmmoCount(new ItemStack((Item) null, 5)));
    }
}
