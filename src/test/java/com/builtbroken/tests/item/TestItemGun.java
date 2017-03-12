package com.builtbroken.tests.item;

import com.builtbroken.armory.data.ArmoryDataHandler;
import com.builtbroken.armory.data.ArmoryEntry;
import com.builtbroken.armory.data.ranged.GunData;
import com.builtbroken.mc.testing.junit.VoltzTestRunner;
import com.builtbroken.tests.AbstractArmoryTest;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.internal.Streams;
import com.google.gson.stream.JsonReader;
import org.junit.runner.RunWith;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 11/19/2016.
 */
@RunWith(VoltzTestRunner.class)
public class TestItemGun extends AbstractArmoryTest
{
    public void testDataSave()
    {
        ArmoryDataHandler.INSTANCE.get("gun").metaToEntry.put(0, new GunData(null, "gun0", null, null, null, null, null));
        ArmoryDataHandler.INSTANCE.get("gun").metaToEntry.put(1, new GunData(null, "gun1", null, null, null, null, null));
        ArmoryDataHandler.INSTANCE.get("gun").metaToEntry.put(2, new GunData(null, "gun2", null, null, null, null, null));
        ArmoryDataHandler.INSTANCE.get("gun").metaToEntry.put(3, new GunData(null, "gun3", null, null, null, null, null));

        ArmoryDataHandler.INSTANCE.get("gun").saveDataToFile(new HashMap());
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
        //Init some data into meta values for save run
        ArmoryDataHandler.INSTANCE.get("gun").metaToEntry.put(0, new GunData(null, "gun0", null, null, null, null, null));
        ArmoryDataHandler.INSTANCE.get("gun").metaToEntry.put(1, new GunData(null, "gun1", null, null, null, null, null));
        ArmoryDataHandler.INSTANCE.get("gun").metaToEntry.put(2, new GunData(null, "gun2", null, null, null, null, null));
        ArmoryDataHandler.INSTANCE.get("gun").metaToEntry.put(3, new GunData(null, "gun3", null, null, null, null, null));

        //Init data into handler so loading works correctly
        ArmoryDataHandler.INSTANCE.get("gun").add(new GunData(null, "gun0", null, null, null, null, null));
        ArmoryDataHandler.INSTANCE.get("gun").add(new GunData(null, "gun1", null, null, null, null, null));
        ArmoryDataHandler.INSTANCE.get("gun").add(new GunData(null, "gun2", null, null, null, null, null));
        ArmoryDataHandler.INSTANCE.get("gun").add(new GunData(null, "gun3", null, null, null, null, null));
        assertEquals(4, ArmoryDataHandler.INSTANCE.get("gun").size());

        //Save data to file to test loading
        ArmoryDataHandler.INSTANCE.get("gun").saveDataToFile(new HashMap());

        //Ensure save worked correctly
        assertTrue(ArmoryDataHandler.INSTANCE.get("gun").save.exists());
        assertEquals(4, ArmoryDataHandler.INSTANCE.get("gun").size());

        //Clear meta values as we are reloading them
        ArmoryDataHandler.INSTANCE.get("gun").metaToEntry.clear();

        ArmoryDataHandler.INSTANCE.get("gun").loadDataFromFile(new HashMap());
        assertEquals("gun0", ((ArmoryEntry) ArmoryDataHandler.INSTANCE.get("gun").metaToEntry.get(0)).getUniqueID());
        assertEquals("gun1", ((ArmoryEntry) ArmoryDataHandler.INSTANCE.get("gun").metaToEntry.get(1)).getUniqueID());
        assertEquals("gun2", ((ArmoryEntry) ArmoryDataHandler.INSTANCE.get("gun").metaToEntry.get(2)).getUniqueID());
        assertEquals("gun3", ((ArmoryEntry) ArmoryDataHandler.INSTANCE.get("gun").metaToEntry.get(3)).getUniqueID());

        folder.delete();
    }

    @Override
    public void setUpForEntireClass()
    {
        super.setUpForEntireClass();
    }

    @Override
    public void tearDownForEntireClass()
    {
        super.setUpForEntireClass();
    }
}
