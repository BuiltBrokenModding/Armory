package com.builtbroken.armory.content.items;

import com.builtbroken.armory.Armory;
import com.builtbroken.armory.content.prefab.ItemMetaArmoryEntry;
import com.builtbroken.armory.data.ArmoryDataHandler;
import com.builtbroken.armory.data.ranged.GunData;
import com.builtbroken.armory.data.ranged.GunInstance;
import com.builtbroken.mc.api.data.weapon.IAmmoType;
import com.builtbroken.mc.api.data.weapon.IGunData;
import com.builtbroken.mc.api.items.IMouseButtonHandler;
import com.builtbroken.mc.api.items.weapons.IItemClip;
import com.builtbroken.mc.api.items.weapons.IItemReloadableWeapon;
import com.builtbroken.mc.core.Engine;
import com.builtbroken.mc.lib.helper.LanguageUtility;
import com.builtbroken.mc.prefab.inventory.InventoryUtility;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 11/16/2016.
 */
public class ItemGun extends ItemMetaArmoryEntry<GunData> implements IMouseButtonHandler, IItemReloadableWeapon
{
    /** Cache of the last weapon the entity has out */
    public static final HashMap<Entity, GunInstance> gunCache = new HashMap();
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

    @SideOnly(Side.CLIENT)
    private IIcon[] defaultGunIcons;

    private GunInstance clientSideGun;

    public ItemGun()
    {
        super("gun", "gun");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean b)
    {
        IGunData data = getData(stack);
        if (data != null)
        {
            GunInstance gun = getGunInstance(stack, player);

            //TODO translate
            list.add("Type: " + data.getGunType());
            list.add("Ammo: " + data.getAmmoType().getDisplayString());
            if (gun != null)
            {
                list.add("Chamber: " + (gun.getChamberedRound() != null ? gun.getChamberedRound().getDisplayString() : "empty"));
                if (gun.getLoadedClip() != null)
                {
                    if (gun.getLoadedClip().getMaxAmmo() > 1)
                    {
                        list.add("Rounds: " + gun.getLoadedClip().getAmmoCount() + "/" + gun.getLoadedClip().getMaxAmmo());
                    }
                }
                else
                {
                    list.add("ReloadType: " + LanguageUtility.capitalizeFirst(data.getReloadType().name().toLowerCase()));
                }
            }
            else
            {
                list.add("Error: Gun instance is null");
            }
        }
        else
        {
            list.add("Error: Gun data is null");
        }
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
            else if (button == 1)
            {
                GunInstance gun = getGunInstance(stack, player);
                if (gun != null)
                {
                    gun.sightWeapon(); //TODO add true/false to this for better control
                }
            }
            else if (button == 2)
            {
                GunInstance gun = getGunInstance(stack, player);
                if (gun != null)
                {
                    gun.reloadWeapon(player.inventory);
                    player.inventoryContainer.detectAndSendChanges();
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
                if (leftClickHeld.containsKey(entity) && slot == ((EntityPlayer) entity).inventory.currentItem)
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
        if (getData(stack) != null)
        {
            //TODO check performance against reloaded the stack vs doing NBT check
            if (player.worldObj.isRemote)
            {
                if (clientSideGun == null || !InventoryUtility.stacksMatch(clientSideGun.toStack(), stack))
                {
                    clientSideGun = loadInstance(player, getGun(stack), stack);
                }
                return clientSideGun;
            }
            else if (gunCache.containsKey(player))
            {
                GunInstance instance = gunCache.get(player);
                if (instance != null && InventoryUtility.stacksMatch(instance.toStack(), stack))
                {
                    if (instance.entity != null && instance.entity == player && instance.getGunData() != null)
                    {
                        return instance;
                    }
                    else
                    {
                        if (Armory.INSTANCE != null)
                        {
                            Armory.INSTANCE.logger().error("Failed to continue using gun instance " + instance + " due to invalid data. This way cause issues with user experience and may result in data loss.", new RuntimeException());
                        }
                        else
                        {
                            throw new RuntimeException("Failed to continue using gun instance " + instance + " due to invalid data. This way cause issues with user experience and may result in data loss.");
                        }
                    }
                }
                else if (Engine.runningAsDev)
                {
                    if (Armory.INSTANCE != null)
                    {
                        if (instance == null)
                        {
                            Armory.INSTANCE.logger().info("Gun cache contained null value for player '" + player + "'");
                        }
                        else if (!InventoryUtility.stacksMatch(instance.toStack(), stack))
                        {
                            Armory.INSTANCE.logger().info("Gun cache contained value but it did not match player held item for player '" + player + "' stack '" + stack + "' expected '" + instance.toStack() + "'");
                        }
                    }
                }
            }
            GunInstance instance = loadInstance(player, getGun(stack), stack);
            if (instance != null)
            {
                gunCache.put(player, instance);
                return instance;
            }
        }
        else
        {
            if (Armory.INSTANCE != null)
            {
                Armory.INSTANCE.logger().error("Failed to get gun instance due to gun data being null for " + stack);
            }
            else
            {
                throw new RuntimeException("Failed to get gun instance due to gun data being null for " + stack);
            }
        }
        return null;
    }

    /**
     * Loads a gun instance from a save
     *
     * @param entity   - entity who will use the weapon
     * @param data     - gun data
     * @param gunStack - this
     * @return gun instance
     */
    public GunInstance loadInstance(Entity entity, GunData data, ItemStack gunStack)
    {
        GunInstance instance = new GunInstance(gunStack, entity, data);
        if (gunStack.getTagCompound() != null)
        {
            instance.load(gunStack.getTagCompound());
        }
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
        return ArmoryDataHandler.INSTANCE.get("gun").metaToEntry.containsKey(meta) ? (GunData) ArmoryDataHandler.INSTANCE.get("gun").metaToEntry.get(meta) : null;
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

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister reg)
    {
        super.registerIcons(reg);
        defaultGunIcons = new IIcon[3];
        defaultGunIcons[0] = reg.registerIcon(Armory.PREFIX + "shotgun");
        defaultGunIcons[1] = reg.registerIcon(Armory.PREFIX + "assaultRifle");
        defaultGunIcons[2] = reg.registerIcon(Armory.PREFIX + "sniperRifle");
    }

    @Override
    protected String[] getIconStringKeys(ItemStack stack, int pass)
    {
        if (stack.getTagCompound() != null)
        {
            NBTTagCompound nbt = stack.getTagCompound();
            boolean hasChamberedRound = nbt.hasKey(GunInstance.NBT_ROUND);
            boolean hasClipLoaded = nbt.hasKey(GunInstance.NBT_CLIP);
            boolean hasAmmo = false;
            if (hasClipLoaded)
            {
                NBTTagCompound clipTag = nbt.getCompoundTag(GunInstance.NBT_CLIP);
                if (clipTag.hasKey("stack"))
                {
                    ItemStack clipStack = ItemStack.loadItemStackFromNBT(clipTag.getCompoundTag("stack"));
                    if (clipStack.getItem() instanceof IItemClip)
                    {
                        hasAmmo = ((IItemClip) clipStack.getItem()).getAmmoCount(clipStack) > 0;
                    }
                }
                //Built in clip, has ammo and has clip are the same
                else
                {
                    hasAmmo = hasClipLoaded = clipTag.hasKey("ammoData");
                }
            }
            //TODO add more types
            if (!hasChamberedRound && !hasAmmo)
            {
                return new String[]{"gun.empty"};
            }
            else if (!hasChamberedRound)
            {
                return new String[]{"gun.chamber.none"};
            }
            else if (!hasClipLoaded)
            {
                return new String[]{"gun.clip.none"};
            }
            else if (!hasAmmo)
            {
                return new String[]{"gun.clip.empty"};
            }
        }
        return null;
    }

    @SideOnly(Side.CLIENT)
    protected IIcon getDefaultIcon(int meta)
    {
        GunData data = getData(meta);
        if ("assaultRifle".equalsIgnoreCase(data.getGunType()))
        {
            return defaultGunIcons[1];
        }
        else if ("sniperRifle".equalsIgnoreCase(data.getGunType()))
        {
            return defaultGunIcons[2];
        }
        return defaultGunIcons[0];
    }
}
