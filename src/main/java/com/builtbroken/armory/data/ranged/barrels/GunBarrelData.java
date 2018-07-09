package com.builtbroken.armory.data.ranged.barrels;

import com.builtbroken.jlib.data.vector.IPos3D;
import com.builtbroken.mc.api.data.weapon.BarrelDamageMode;
import com.builtbroken.mc.api.data.weapon.BarrelFireMode;
import com.builtbroken.mc.api.data.weapon.IGunBarrelData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 7/8/2018.
 */
public class GunBarrelData implements IGunBarrelData
{
    public GunBarrel[] gunBarrels;
    public BarrelFireMode barrelFireMode = BarrelFireMode.CURRENT;
    public BarrelDamageMode barrelDamageMode = BarrelDamageMode.INDIVIDUAL;

    private int[][] groups;
    private int[] indexes;

    @Override
    public boolean hasData()
    {
        return gunBarrels != null;
    }

    @Override
    public int nextBarrelIndex(int index)
    {
        if (hasData())
        {
            index++;
            if (barrelFireMode == BarrelFireMode.GROUP)
            {
                if (index >= getGroups().length)
                {
                    index = 0;
                }
            }
            else if (index >= gunBarrels.length)
            {
                index = 0;
            }
            return index;
        }
        return 0;
    }

    @Override
    public IPos3D getBarrelOffset(int index)
    {
        if (hasData() && index >= 0 && index < gunBarrels.length)
        {
            return gunBarrels[index].pos;
        }
        return null;
    }

    @Override
    public BarrelDamageMode getDamageMode()
    {
        return barrelDamageMode;
    }

    @Override
    public BarrelFireMode getFireMode()
    {
        return barrelFireMode;
    }

    @Override
    public int[] getBarrelsInGroup(int index)
    {
        if (index < getGroups().length && index >= 0)
        {
            return getGroups()[index];
        }
        else if (index == -1)
        {
            return indexes; //getGroups() needs to be called first or NPE
        }
        return null;
    }

    protected int[][] getGroups()
    {
        if (groups == null)
        {
            indexes = new int[gunBarrels.length];

            //Collect data
            HashMap<Integer, List<Integer>> map = new HashMap();
            for (int i = 0; i < gunBarrels.length; i++)
            {
                indexes[i] = i;
                GunBarrel barrel = gunBarrels[i];
                if (!map.containsKey(barrel.group))
                {
                    map.put(barrel.group, new ArrayList());
                }
                map.get(barrel.group).add(i);
            }

            //Convert data to groups
            groups = new int[map.keySet().size()][];
            for (int group : map.keySet())
            {
                List<Integer> ids = map.get(group);
                groups[group] = new int[ids.size()];
                for (int i = 0; i < ids.size(); i++)
                {
                    groups[group][i] = ids.get(i);
                }
            }
        }
        return groups;
    }
}
