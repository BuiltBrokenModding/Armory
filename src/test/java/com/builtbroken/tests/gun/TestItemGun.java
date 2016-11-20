package com.builtbroken.tests.gun;

import com.builtbroken.armory.content.items.ItemGun;
import com.builtbroken.armory.data.ArmoryDataHandler;
import com.builtbroken.armory.data.ranged.GunData;
import com.builtbroken.mc.testing.junit.AbstractTest;
import com.builtbroken.mc.testing.junit.VoltzTestRunner;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.internal.Streams;
import com.google.gson.stream.JsonReader;
import org.junit.runner.RunWith;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 11/19/2016.
 */
@RunWith(VoltzTestRunner.class)
public class TestItemGun extends AbstractTest
{
    public void testDataSave()
    {
        File folder = new File(System.getProperty("user.dir"), "tmp");
        folder.deleteOnExit();

        ArmoryDataHandler.INSTANCE.add(new ArmoryDataHandler.ArmoryData(folder, "gun"));
        ItemGun itemGun = new ItemGun();

        itemGun.metaToData.put(0, new GunData("gun0", null, null, null, null, null));
        itemGun.metaToData.put(1, new GunData("gun1", null, null, null, null, null));
        itemGun.metaToData.put(2, new GunData("gun2", null, null, null, null, null));
        itemGun.metaToData.put(3, new GunData("gun3", null, null, null, null, null));

        ArmoryDataHandler.INSTANCE.get("gun").saveDataToFile(new HashMap(), itemGun.metaToData);
        assertTrue(ArmoryDataHandler.INSTANCE.get("gun").save.exists());

        try (FileReader stream = new FileReader(ArmoryDataHandler.INSTANCE.get("gun").save))
        {
            JsonReader jsonReader = new JsonReader(new BufferedReader(stream));
            JsonObject data = Streams.parse(jsonReader).getAsJsonObject();
            JsonArray array = data.get("gun").getAsJsonArray();
            for (int i = 0; i < array.size(); i++)
            {
                JsonObject element = array.get(i).getAsJsonObject();
                Long lastWriteTime = element.getAsJsonPrimitive("writeTime").getAsLong();
                String name = element.getAsJsonPrimitive("ID").getAsString();
                int meta = element.getAsJsonPrimitive("meta").getAsInt();

                assertEquals(i, meta);
                assertEquals("gun" + i, name);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail();
        }

        folder.delete();
    }

    public void testDataLoad()
    {
        File folder = new File(System.getProperty("user.dir"), "tmp");
        folder.deleteOnExit();

        ArmoryDataHandler.INSTANCE.add(new ArmoryDataHandler.ArmoryData(folder, "gun"));
        ItemGun itemGun = new ItemGun();

        //Init some data into meta values for save run
        itemGun.metaToData.put(0, new GunData("gun0", null, null, null, null, null));
        itemGun.metaToData.put(1, new GunData("gun1", null, null, null, null, null));
        itemGun.metaToData.put(2, new GunData("gun2", null, null, null, null, null));
        itemGun.metaToData.put(3, new GunData("gun3", null, null, null, null, null));

        //Init data into handler so loading works correctly
        ArmoryDataHandler.INSTANCE.get("gun").add(new GunData("gun0", null, null, null, null, null));
        ArmoryDataHandler.INSTANCE.get("gun").add(new GunData("gun1", null, null, null, null, null));
        ArmoryDataHandler.INSTANCE.get("gun").add(new GunData("gun2", null, null, null, null, null));
        ArmoryDataHandler.INSTANCE.get("gun").add(new GunData("gun3", null, null, null, null, null));
        assertEquals(4, ArmoryDataHandler.INSTANCE.get("gun").size());

        //Save data to file to test loading
        ArmoryDataHandler.INSTANCE.get("gun").saveDataToFile(new HashMap(), itemGun.metaToData);

        //Ensure save worked correctly
        assertTrue(ArmoryDataHandler.INSTANCE.get("gun").save.exists());
        assertEquals(4, ArmoryDataHandler.INSTANCE.get("gun").size());

        //Clear meta values as we are reloading them
        itemGun.metaToData.clear();

        ArmoryDataHandler.INSTANCE.get("gun").loadDataFromFile(new HashMap(), itemGun.metaToData);
        assertEquals("gun0", itemGun.metaToData.get(0).ID);
        assertEquals("gun1", itemGun.metaToData.get(1).ID);
        assertEquals("gun2", itemGun.metaToData.get(2).ID);
        assertEquals("gun3", itemGun.metaToData.get(3).ID);

        folder.delete();
    }
}
