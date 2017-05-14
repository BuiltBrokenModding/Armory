package com.builtbroken.tests.processors;

import com.builtbroken.armory.data.ArmoryDataHandler;
import com.builtbroken.armory.data.ammo.AmmoType;
import com.builtbroken.armory.data.clip.ClipData;
import com.builtbroken.mc.api.data.weapon.ReloadType;
import com.builtbroken.mc.api.data.EnumProjectileTypes;
import com.builtbroken.armory.json.processors.ClipJsonProcessor;
import com.builtbroken.mc.testing.junit.AbstractTest;
import com.builtbroken.mc.testing.junit.VoltzTestRunner;
import com.google.gson.JsonElement;
import com.google.gson.internal.Streams;
import com.google.gson.stream.JsonReader;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.StringReader;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 11/16/2016.
 */
@RunWith(VoltzTestRunner.class)
public class TestClipProcessor extends AbstractTest
{
    public void testLoading()
    {
        AmmoType ammoType = new AmmoType(null, "9mm", "9mm", EnumProjectileTypes.BULLET);
        ArmoryDataHandler.INSTANCE.add(new ArmoryDataHandler.ArmoryData(new File("tmp"), "ammoType"));
        ArmoryDataHandler.INSTANCE.get("ammoType").add(ammoType);

        JsonReader jsonReader = new JsonReader(new StringReader(
                "{\"clip\": {\n" +
                        "    \"name\": \"9mm.clip.15\",\n" +
                        "    \"id\": \"armory:clip.9mm.15\",\n" +
                        "    \"reloadType\": \"clip\",\n" +
                        "    \"ammoType\": \"9mm\",\n" +
                        "    \"maxAmmo\": 15\n" +
                        "  }}"
        ));
        JsonElement element = Streams.parse(jsonReader);
        ClipJsonProcessor processor = new ClipJsonProcessor();
        ClipData data = processor.process(element.getAsJsonObject().get("clip"));
        assertEquals("9mm.clip.15", data.name());
        assertEquals(ReloadType.CLIP, data.reloadType);
        assertEquals(ammoType, data.ammoType);
        assertEquals(15, data.maxAmmo);
    }
}
