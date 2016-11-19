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
        ItemGun.metaToGun.put(0, new GunData("gun0", null, null, null, null, null));
        ItemGun.metaToGun.put(1, new GunData("gun1", null, null, null, null, null));
        ItemGun.metaToGun.put(2, new GunData("gun2", null, null, null, null, null));
        ItemGun.metaToGun.put(3, new GunData("gun3", null, null, null, null, null));
        File file = new File("tmp");
        File save = new File(file, "testDataSave.json");
        if (!file.exists())
        {
            file.mkdirs();
        }
        ItemGun.saveGunDataToFile(save, new HashMap());
        assertTrue(save.exists());

        try (FileReader stream = new FileReader(save))
        {
            JsonReader jsonReader = new JsonReader(new BufferedReader(stream));
            JsonObject data = Streams.parse(jsonReader).getAsJsonObject();
            JsonArray array = data.get("guns").getAsJsonArray();
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
            fail();
        }

        save.delete();
    }

    public void testDataLoad()
    {
        ItemGun.metaToGun.put(0, new GunData("gun0", null, null, null, null, null));
        ItemGun.metaToGun.put(1, new GunData("gun1", null, null, null, null, null));
        ItemGun.metaToGun.put(2, new GunData("gun2", null, null, null, null, null));
        ItemGun.metaToGun.put(3, new GunData("gun3", null, null, null, null, null));

        ArmoryDataHandler.add(new GunData("gun0", null, null, null, null, null));
        ArmoryDataHandler.add(new GunData("gun1", null, null, null, null, null));
        ArmoryDataHandler.add(new GunData("gun2", null, null, null, null, null));
        ArmoryDataHandler.add(new GunData("gun3", null, null, null, null, null));

        File file = new File("tmp");
        File save = new File(file, "testDataSave.json");
        if (!file.exists())
        {
            file.mkdirs();
        }
        ItemGun.saveGunDataToFile(save, new HashMap());
        assertTrue(save.exists());
        ItemGun.metaToGun.clear();

        ItemGun.loadGunDataFromFile(save, new HashMap());
        assertEquals("gun0", ItemGun.metaToGun.get(0).ID);
        assertEquals("gun1", ItemGun.metaToGun.get(1).ID);
        assertEquals("gun2", ItemGun.metaToGun.get(2).ID);
        assertEquals("gun3", ItemGun.metaToGun.get(3).ID);

        save.delete();
    }
}
