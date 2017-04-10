package com.builtbroken.armory.content.sentry;

import com.builtbroken.mc.imp.transform.vector.Pos;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;

import java.util.Comparator;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 4/9/2017.
 */
public class SentryEntityTargetSorter implements Comparator<Entity>
{
    public final Pos center;

    public SentryEntityTargetSorter(Pos pos)
    {
        center = pos;
    }

    @Override
    public int compare(Entity entity, Entity entity2)
    {
        int distanceA = (int) Math.floor(center.distance(entity));
        int distanceB = (int) Math.floor(center.distance(entity2));
        int distanceCompare = Integer.compare(distanceA, distanceB);
        if (distanceCompare == 0)
        {
            float hpA = entity instanceof EntityLivingBase ? ((EntityLivingBase) entity).getHealth() : 0;
            float hpB = entity instanceof EntityLivingBase ? ((EntityLivingBase) entity2).getHealth() : 0;
            int hpCompare = Float.compare(hpA, hpB);
            if (hpCompare == 0)
            {
                int armorA = entity instanceof EntityLivingBase ? ((EntityLivingBase) entity).getTotalArmorValue() : 0;
                int armorB = entity instanceof EntityLivingBase ? ((EntityLivingBase) entity2).getTotalArmorValue() : 0;
                int armorCompare = Integer.compare(armorA, armorB);
                //TODO prioritise entity types (creeper > zombie)
                return armorCompare;
            }
            return hpCompare;
        }
        return distanceCompare;
    }
}
