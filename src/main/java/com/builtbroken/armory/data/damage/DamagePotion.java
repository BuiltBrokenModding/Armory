package com.builtbroken.armory.data.damage;

import com.builtbroken.armory.Armory;
import com.builtbroken.jlib.data.Colors;
import com.builtbroken.mc.core.Engine;
import com.builtbroken.mc.framework.json.imp.IJsonProcessor;
import com.builtbroken.mc.framework.json.loading.JsonProcessorData;
import com.builtbroken.mc.lib.debug.DebugHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;

/**
 * Damage type that applied a potion effect to the entity
 *
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 9/27/2017.
 */
public class DamagePotion extends DamageData
{
    /** Unique name of the potion to use */
    public final String potionName;

    //TODO move potion effect data into its own JSON object for reuse
    /** The duration of the potion effect */
    @JsonProcessorData(value = "duration", type = "int")
    public int duration;

    /** The amplifier of the potion effect */
    @JsonProcessorData(value = "amplifier", type = "int")
    public int amplifier;

    private Potion potion;

    public DamagePotion(IJsonProcessor processor, String id)
    {
        super(processor);
        this.potionName = id; //TODO map potion to unique id to prevent issues
    }

    @Override
    public boolean onImpact(Entity attacker, World world, int x, int y, int z, double hitX, double hitY, double hitZ, float velocity, float scale)
    {
        if (!world.isRemote && attacker instanceof EntityLivingBase)
        {
            Potion potion = getPotion();
            if (potion != null)
            {
                ((EntityLivingBase) attacker).addPotionEffect(new PotionEffect(potion.id, duration, amplifier));
            }
            else
            {
                DebugHelper.outputMethodDebug(Armory.INSTANCE.logger(), "doImpact", "\nnull potion for id '" + potionName + "'", attacker, world, x, y, z, velocity, scale);
            }
        }
        return true;
    }

    @Override
    public String getDisplayString()
    {
        if(getPotion() == null)
        {
            return Engine.runningAsDev ? Colors.RED.code + "Error: potion " + potionName : null;
        }
        return getPotion().getName() + " x" + amplifier + " for " + duration; //TODO converter amplifier to romain numerals, change duration to seconds
    }

    public Potion getPotion()
    {
        if (potion == null)
        {
            for (Potion potion : Potion.potionTypes)
            {
                if (potion != null && potion.getName() != null)
                {
                    String name = potion.getName().toLowerCase().trim();
                    if (name.equalsIgnoreCase(potionName) || name.replace("potion.", "").equalsIgnoreCase(potionName))
                    {
                        this.potion = potion;
                        break;
                    }
                }
            }
        }
        return potion;
    }

    @Override
    public boolean onImpact(Entity attacker, Entity entity, double hitX, double hitY, double hitZ, float velocity, float scale)
    {
        return true;
    }

    @Override
    public String toString()
    {
        return "DamagePotion[" + potionName + "x" + amplifier + "@" + duration + "]@" + hashCode();
    }
}
