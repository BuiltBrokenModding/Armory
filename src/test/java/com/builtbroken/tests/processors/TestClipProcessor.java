package com.builtbroken.tests.processors;

import com.builtbroken.armory.data.ArmoryDataHandler;
import com.builtbroken.armory.data.ammo.AmmoType;
import com.builtbroken.armory.data.ammo.ClipData;
import com.builtbroken.armory.data.ammo.ClipTypes;
import com.builtbroken.armory.data.projectiles.EnumProjectileTypes;
import com.builtbroken.armory.json.processor.ClipJsonProcessor;
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
        AmmoType ammoType = new AmmoType("0", "9mm", EnumProjectileTypes.BULLET);
        ArmoryDataHandler.INSTANCE.add(new ArmoryDataHandler.ArmoryData(new File("tmp"), "ammoType"));
        ArmoryDataHandler.INSTANCE.get("ammoType").add(ammoType);

        JsonReader jsonReader = new JsonReader(new StringReader(
                "{\n" +
                        "  \"clip\": {\n" +
                        "    \"id\": \"9mmClip\",\n" +
                        "    \"name\": \"9mmClip\",\n" +
                        "    \"translationKey\": \"9mm\",\n" +
                        "    \"type\": \"clip\",\n" +
                        "    \"ammo\": \"9mm\",\n" +
                        "    \"maxAmmo\": 15\n" +
                        "  }\n" +
                        "}"
        ));
        JsonElement element = Streams.parse(jsonReader);
        ClipJsonProcessor processor = new ClipJsonProcessor();
        ClipData data = processor.process(element.getAsJsonObject().get("clip"));
        assertEquals("9mmClip", data.name());
        assertEquals("clip.9mm", data.translationKey);
        assertEquals(ClipTypes.CLIP, data.type);
        assertEquals(ammoType, data.ammoType);
        assertEquals(15, data.maxAmmo);
    }
}
