package com.builtbroken.tests;

import com.builtbroken.armory.data.ArmoryDataHandler;
import com.builtbroken.mc.testing.junit.AbstractTest;

import java.io.File;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 11/22/2016.
 */
public abstract class AbstractArmoryTest extends AbstractTest
{
    protected File folder;

    @Override
    public void setUpForEntireClass()
    {
        super.setUpForEntireClass();
        folder = new File(System.getProperty("user.dir"), "tmp");
        ArmoryDataHandler.INSTANCE.add(new ArmoryDataHandler.ArmoryData(folder, "ammo"));
        ArmoryDataHandler.INSTANCE.add(new ArmoryDataHandler.ArmoryData(folder, "ammoType"));
        ArmoryDataHandler.INSTANCE.add(new ArmoryDataHandler.ArmoryData(folder, "gun"));
        ArmoryDataHandler.INSTANCE.add(new ArmoryDataHandler.ArmoryData(folder, "clip"));
    }

    @Override
    public void tearDownForEntireClass()
    {
        super.setUpForEntireClass();
        ArmoryDataHandler.INSTANCE.DATA.remove("ammo");
        ArmoryDataHandler.INSTANCE.DATA.remove("ammoType");
        ArmoryDataHandler.INSTANCE.DATA.remove("gun");
        ArmoryDataHandler.INSTANCE.DATA.remove("clip");
    }
}
