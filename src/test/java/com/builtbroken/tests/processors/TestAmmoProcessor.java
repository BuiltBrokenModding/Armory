package com.builtbroken.tests.processors;

import com.builtbroken.armory.data.ArmoryDataHandler;
import com.builtbroken.armory.data.ammo.AmmoData;
import com.builtbroken.armory.data.ammo.AmmoType;
import com.builtbroken.armory.data.projectiles.EnumProjectileTypes;
import com.builtbroken.armory.json.processor.AmmoJsonProcessor;
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
public class TestAmmoProcessor extends AbstractTest
{
    public void testLoading()
    {
        AmmoType ammoType = new AmmoType(null, "9mm", "9mm", EnumProjectileTypes.BULLET);
        ArmoryDataHandler.INSTANCE.add(new ArmoryDataHandler.ArmoryData(new File("tmp"), "ammoType"));
        ArmoryDataHandler.INSTANCE.get("ammoType").add(ammoType);

        JsonReader jsonReader = new JsonReader(new StringReader(
                "{\n" +
                        "\"ammo\": {\n" +
                        "    \"name\": \"basic\",\n" +
                        "    \"id\": \"basic\",\n" +
                        "    \"translationKey\": \"9mm.basic\",\n" +
                        "    \"type\": \"9mm\",\n" +
                        "    \"damage\": 5,\n" +
                        "    \"source\": \"impact\"\n" +
                        "  }\n" +
                        "}"
        ));
        JsonElement element = Streams.parse(jsonReader);
        AmmoJsonProcessor processor = new AmmoJsonProcessor();
        AmmoData data = processor.process(element.getAsJsonObject().get("ammo"));
        assertEquals("impact", data.damageSource);
        assertEquals(5f, data.damage);
        assertSame(ammoType, data.ammoType);
    }
}
