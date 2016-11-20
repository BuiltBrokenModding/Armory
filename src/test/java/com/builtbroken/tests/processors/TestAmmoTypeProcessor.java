package com.builtbroken.tests.processors;

import com.builtbroken.armory.data.ammo.AmmoType;
import com.builtbroken.armory.json.processor.AmmoTypeJsonProcessor;
import com.builtbroken.mc.testing.junit.AbstractTest;
import com.builtbroken.mc.testing.junit.VoltzTestRunner;
import com.google.gson.JsonElement;
import com.google.gson.internal.Streams;
import com.google.gson.stream.JsonReader;
import org.junit.runner.RunWith;

import java.io.StringReader;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 11/16/2016.
 */
@RunWith(VoltzTestRunner.class)
public class TestAmmoTypeProcessor extends AbstractTest
{
    public void testLoading()
    {
        JsonReader jsonReader = new JsonReader(new StringReader(
                "{\n" +
                        "  \"ammoType\": {\n" +
                        "    \"id\": \"9mm\",\n" +
                        "    \"name\": \"9mm\",\n" +
                        "    \"type\": 4,\n" +
                        "    \"translationKey\": \"9mm\"\n" +
                        "  }\n" +
                        "}"
        ));
        JsonElement element = Streams.parse(jsonReader);
        AmmoTypeJsonProcessor processor = new AmmoTypeJsonProcessor();
        AmmoType data = processor.process(element.getAsJsonObject().get("ammoType"));
        assertEquals("ammoType.9mm", data.translationKey);
        assertEquals("9mm", data.name());
    }
}
