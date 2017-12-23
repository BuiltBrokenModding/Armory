package com.builtbroken.armory.data.ammo;

import com.builtbroken.armory.data.ArmoryEntry;
import com.builtbroken.armory.data.damage.DamageData;
import com.builtbroken.mc.api.data.weapon.IAmmoData;
import com.builtbroken.mc.api.data.weapon.IAmmoType;
import com.builtbroken.mc.framework.json.imp.IJsonProcessor;
import com.builtbroken.mc.framework.json.loading.JsonProcessorData;
import com.builtbroken.mc.framework.json.loading.JsonProcessorDataGetter;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

/**
 * Stores data about a projectile/ammo for a weapon
 *
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 11/16/2016.
 */
public class AmmoData extends ArmoryEntry implements IAmmoData
{
    /** Type of ammo */
    public final AmmoType ammoType;

    /** Damage applied to entity when projectile hits */
    public final List<DamageData> damageData = new ArrayList();  //TODO setup with annotations

    /** Item data used to build drop list */
    public final List<String> droppedItemData = new ArrayList(); //TODO setup with annotations

    /** Item dropped after the ammo has been fired */
    public final List<ItemStack> droppedItems = new ArrayList();

    //Cache for damage call
    private float damageCached = -1;
    private float velocity = -1;
    private int energyCost = -1;

    public AmmoData(IJsonProcessor processor, String id, String name, AmmoType ammoType)
    {
        super(processor, id, "ammo", name);
        this.ammoType = ammoType;
    }

    @Override
    public void onCreated()
    {
        super.onCreated();
        ammoType.addAmmoData(this);
    }

    @Override
    public IAmmoType getAmmoType()
    {
        return ammoType;
    }

    @Override
    public float getBaseDamage()
    {
        if (damageCached == -1)
        {
            damageCached = 0;
            for (DamageData data : damageData)
            {
                damageCached += data.getBaseDamage();
            }
        }
        return damageCached;
    }

    @Override
    public boolean onImpactEntity(Entity shooter, Entity entity, double hitX, double hitY, double hitZ, float velocity)
    {
        boolean destroy = false;
        for (DamageData data : damageData)
        {
            if (data.onImpact(shooter, entity, hitX, hitY, hitZ, velocity, 1))
            {
                destroy = true;
            }
        }
        return destroy;
    }

    @Override
    public boolean onImpactGround(Entity shooter, World world, int x, int y, int z, double hitX, double hitY, double hitZ, float velocity)
    {
        boolean destroy = false;
        for (DamageData data : damageData)
        {
            if (data.onImpact(shooter, world, x, y, z, hitX, hitY, hitZ, velocity, 1))
            {
                destroy = true;
            }
        }
        return destroy;
    }

    @Override
    public void getEjectedItems(List<ItemStack> items)
    {
        if (droppedItemData.size() > 0)
        {
            //Build list if not initialized
            if (droppedItems.size() == 0)
            {
                for (String string : droppedItemData)
                {
                    ItemStack stack = toStack(convertItemEntry(string));
                    if (stack != null && stack.getItem() != null)
                    {
                        droppedItems.add(stack);
                    }
                }
            }

            for (ItemStack stack : droppedItems)
            {
                items.add(stack.copy());
            }
        }
    }

    @Override
    @JsonProcessorDataGetter(value = "energyCost", type = "int")
    public int getEnergyCost()
    {
        return energyCost;
    }

    @JsonProcessorData(value = "energyCost", type = "int", allowRuntimeChanges = true)
    public void setEnergyCost(int energyCost)
    {
        this.energyCost = energyCost;
    }

    @Override
    @JsonProcessorDataGetter(value = "velocity", type = "float")
    public float getProjectileVelocity()
    {
        return velocity;
    }

    @JsonProcessorData(value = "velocity", type = "float", allowRuntimeChanges = true)
    public void setProjectileVelocity(float velocity)
    {
        this.velocity = velocity;
    }

    @Override
    public String toString()
    {
        return "Ammo[" + getUniqueID() + "]@" + hashCode();
    }

}
