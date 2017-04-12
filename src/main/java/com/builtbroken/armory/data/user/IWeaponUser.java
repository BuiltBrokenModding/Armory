package com.builtbroken.armory.data.user;

import com.builtbroken.mc.api.IWorldPosition;
import com.builtbroken.mc.imp.transform.rotation.IRotation;
import com.builtbroken.mc.imp.transform.vector.Pos;
import net.minecraft.entity.Entity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

/**
 * Used to wrapper shooters to provide clean data access and to provide support to non-entity shooters.
 *
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 4/11/2017.
 */
public interface IWeaponUser extends IWorldPosition, IRotation
{
    /**
     * Gets the shooter as an entity. If
     * the shooter is not an entity use
     * a wrapper object.
     *
     * @return
     */
    Entity getShooter();

    /**
     * Gets the position to user for the
     * entity. Used to calculate ray traces
     * and bullet spawn positions.
     *
     * @return
     */
    Pos getEntityPosition();

    /**
     * Gets the vector that represents
     * the entity's aim
     *
     * @return
     */
    Pos getEntityAim();

    /**
     * Gets the position to spawn projectiles
     * realitive to the entity.
     *
     * @return
     */
    Pos getProjectileSpawnOffset();

    /**
     * Inventory to use for weapon ammo access
     *
     * @return
     */
    IInventory getInventory();

    /**
     * Used to check if the slot is a usable ammo slot
     *
     * @param slot - slot being checked
     * @return true if can be used for ammo
     */
    boolean isAmmoSlot(int slot);

    default void updateWeaponStack(ItemStack stack, String editor)
    {

    }
}
