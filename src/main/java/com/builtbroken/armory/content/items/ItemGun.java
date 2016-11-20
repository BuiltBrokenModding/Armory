package com.builtbroken.armory.content.items;

import com.builtbroken.armory.content.prefab.ItemMetaArmoryEntry;
import com.builtbroken.armory.data.ranged.GunData;
import com.builtbroken.armory.data.ranged.GunInstance;
import com.builtbroken.jlib.type.Pair;
import com.builtbroken.mc.api.items.IMouseButtonHandler;
import com.builtbroken.mc.api.data.weapon.IAmmoType;
import com.builtbroken.mc.api.items.weapons.IItemReloadableWeapon;
import com.builtbroken.mc.prefab.inventory.InventoryUtility;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.Map;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 11/16/2016.
 */
public class ItemGun extends ItemMetaArmoryEntry<GunData> implements IMouseButtonHandler, IItemReloadableWeapon
{
    /** Cache of the last weapon the entity has out */
    public static final HashMap<Entity, Pair<GunInstance, ItemStack>> gunCache = new HashMap();
    /** Who has the left click held down */
    public static final Map<EntityPlayer, Integer> leftClickHeld = new HashMap();
    //TODO handle what type of gun
    //TODO handle damage to weapon
    //TODO handle damage to weapon parts
    //TODO handle to & from stack conversions
    //TODO handle ammo
    //TODO handle reloading
    //TODO handle firing
    //TODO handle aiming

    public ItemGun()
    {
        super("gun", "gun");
    }

    @Override
    public void mouseClick(ItemStack stack, EntityPlayer player, int button, boolean state)
    {
        if (!player.worldObj.isRemote)
        {
            if (button == 0)
            {
                if (state)
                {
                    if (!leftClickHeld.containsKey(player))
                    {
                        leftClickHeld.put(player, 0);
                    }
                }
                else
                {
                    leftClickHeld.remove(player);
                }
            }
        }
    }

    @Override
    public void onUpdate(ItemStack stack, World world, Entity entity, int slot, boolean bool)
    {
        if (!world.isRemote)
        {
            if (entity instanceof EntityPlayer)
            {
                if (leftClickHeld.containsKey(entity))
                {
                    int ticks = leftClickHeld.get(entity) + 1;
                    leftClickHeld.put((EntityPlayer) entity, ticks);
                    onLeftClickHeld(stack, getGunInstance(stack, (EntityPlayer) entity), world, (EntityPlayer) entity, slot, ticks);
                }
            }
        }
    }

    /**
     * Called every tick the left button is held down for firing the gun
     *
     * @param stack  - weapon stack
     * @param gun    - weapon instance
     * @param world  - world fired in
     * @param player - player who is firing the weapon
     * @param slot   - slot the stack is in
     * @param ticks  - how many ticks has the click been held
     */
    public void onLeftClickHeld(ItemStack stack, GunInstance gun, World world, EntityPlayer player, int slot, int ticks)
    {
        gun.fireWeapon(stack, world, ticks);
    }

    /**
     * Called to load the gun instance
     * <p>
     * All instances are cached, and caches are checked
     * before being returned. As long as the stacks match
     * then the cached value is returned.
     *
     * @param stack  - weapon stack
     * @param player - player who will be referenced in the cache value
     * @return gun instance, or null if something goes completely wrong
     */
    public GunInstance getGunInstance(ItemStack stack, EntityPlayer player)
    {
        //TODO check performance against reloaded the stack vs doing NBT check
        if (gunCache.containsKey(player))
        {
            Pair<GunInstance, ItemStack> pair = gunCache.get(player);
            if (pair != null && pair.left() != null && pair.right() != null && InventoryUtility.stacksMatch(pair.right(), stack))
            {
                GunInstance gunInstance = pair.left();
                if (gunInstance.entity == player && gunInstance.gun != null)
                {
                    return gunInstance;
                }
            }
        }
        GunInstance instance = loadInstance(player, getGun(stack), stack.getTagCompound());
        if (instance != null)
        {
            gunCache.put(player, new Pair<GunInstance, ItemStack>(instance, stack));
            return instance;
        }
        return null;
    }

    /**
     * Loads a gun instance from a save
     *
     * @param entity - entity who will use the weapon
     * @param data   - gun data
     * @param tag    - save data
     * @return gun instance
     */
    public GunInstance loadInstance(Entity entity, GunData data, NBTTagCompound tag)
    {
        GunInstance instance = new GunInstance(entity, data);
        instance.load(tag);
        return instance;
    }

    @Override
    public boolean onEntitySwing(EntityLivingBase entityLiving, ItemStack stack)
    {
        return true;
    }

    @Override
    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float xf, float yf, float zf)
    {
        return false;
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player)
    {
        return stack;
    }

    public GunData getGun(ItemStack stack)
    {
        return getGun(stack.getItemDamage());
    }

    public GunData getGun(int meta)
    {
        return metaToData.containsKey(meta) ? metaToData.get(meta) : null;
    }

    @Override
    public ItemStack loadAmmo(ItemStack weapon, ItemStack ammo, IAmmoType type, boolean isClip)
    {
        //TODO implement
        return ammo;
    }

    @Override
    public boolean canContainAmmo(ItemStack weapon)
    {
        return true;
    }
}
