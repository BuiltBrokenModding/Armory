package com.builtbroken.armory.content.items;

import com.builtbroken.armory.Armory;
import com.builtbroken.armory.api.ArmoryAPI;
import com.builtbroken.armory.content.prefab.ItemMetaArmoryEntry;
import com.builtbroken.armory.data.ArmoryDataHandler;
import com.builtbroken.armory.data.ranged.GunData;
import com.builtbroken.armory.data.ranged.GunInstance;
import com.builtbroken.armory.data.user.IWeaponUser;
import com.builtbroken.armory.data.user.WeaponUserEntity;
import com.builtbroken.armory.data.user.WeaponUserPlayer;
import com.builtbroken.jlib.data.Colors;
import com.builtbroken.mc.api.data.energy.IEnergyBufferData;
import com.builtbroken.mc.api.data.energy.IEnergyChargeData;
import com.builtbroken.mc.api.data.weapon.IAmmoData;
import com.builtbroken.mc.api.data.weapon.IAmmoType;
import com.builtbroken.mc.api.data.weapon.IGunData;
import com.builtbroken.mc.api.data.weapon.ReloadType;
import com.builtbroken.mc.api.items.IMouseButtonHandler;
import com.builtbroken.mc.api.items.energy.IEnergyBufferItem;
import com.builtbroken.mc.api.items.energy.IEnergyItem;
import com.builtbroken.mc.api.items.weapons.IItemClip;
import com.builtbroken.mc.api.items.weapons.IItemReloadableWeapon;
import com.builtbroken.mc.core.Engine;
import com.builtbroken.mc.lib.helper.LanguageUtility;
import com.builtbroken.mc.prefab.inventory.InventoryUtility;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.World;
import org.lwjgl.input.Keyboard;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 11/16/2016.
 */
public class ItemGun extends ItemMetaArmoryEntry<GunData> implements IMouseButtonHandler, IItemReloadableWeapon, IEnergyItem, IEnergyBufferItem
{
    public static final String NBT_RELOAD_ENERGY = "reloadEnergy";
    public static final String NBT_ENERGY = "energy";

    /** Cache of the last weapon the entity has out */
    public static final HashMap<Entity, GunInstance> gunCache = new HashMap();
    /** Who has the left click held down */
    public static final Map<EntityPlayer, Integer> leftClickHeld = new HashMap();

    /** CLIENT SIDE ONLY, is the player aiming the gun */
    public static boolean isAiming = false;

    private GunInstance clientSideGun;
    private long lastDebugKeyHit = 0;

    public ItemGun()
    {
        super("armoryGun", ArmoryAPI.GUN_ID, ArmoryAPI.GUN_ID);
        if (!Engine.isJUnitTest())
        {
            FMLCommonHandler.instance().bus().register(this);
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean b)
    {
        super.addInformation(stack, player, list, b);
        IGunData data = getData(stack);
        if (data != null)
        {
            GunInstance gun = getGunInstance(stack, player);

            //TODO translate
            list.add("Type: " + data.getGunType());
            list.add("Ammo: " + data.getAmmoType().getDisplayString());
            if (gun != null)
            {
                if (!gun.getGunData().getReloadType().requiresItems())
                {
                    //TODO show ammo type if gun has more than one option
                    if (gun.getGunData().getReloadType() == ReloadType.ENERGY)
                    {
                        IAmmoData ammoData = gun.getChamberedRound();

                        int power = gun.power;
                        int cap = getEnergyCapacity(stack);
                        int rounds = (int) Math.floor((float) power / (float) ammoData.getEnergyCost());

                        if (ammoData != null)
                        {
                            list.add("Rounds: " + rounds + " @ " + ammoData.getEnergyCost() + " watts each");
                        }
                        else
                        {
                            list.add("Rounds: " + Colors.RED.code + "Error no ammo data");
                        }

                        list.add("Energy: " + power + "/" + cap + " watts"); //TODO color code energy value (red lower, green high)
                    }
                }
                else if (gun.getLoadedClip() != null)
                {
                    list.add("Chamber: " + (gun.getChamberedRound() != null ? gun.getChamberedRound().getDisplayString() : "empty"));
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
        GunInstance gun = getGunInstance(stack, player);
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

                if (!player.getEntityWorld().isRemote)
                {
                    if (gun != null && gun.getChamberedRound() == null && !gun.hasMagWithAmmo())
                    {
                        gun.playAudio("empty");
                    }
                }
            }
        }
        else if (button == 1)
        {
            if (gun != null)
            {
                gun.isSighted = state;

                //Client side temp fix
                if (player.worldObj.isRemote)
                {
                    isAiming = state;
                }

                //Audio event for aiming
                if (!player.getEntityWorld().isRemote)
                {
                    gun.playAudio("aimed");
                }
            }
        }
        else if (button == 2)
        {
            if (gun != null)
            {
                if (!gun.doReload)
                {
                    gun.doReload = true;
                    gun.reloadDelay = -1;

                    if (!player.worldObj.isRemote)
                    {
                        gun.unloadWeapon(player.inventory);
                    }
                }
            }
        }
    }

    @Override
    public boolean shouldCancelMouseEvent(ItemStack stack, EntityPlayer player, int button, boolean state)
    {
        return true;
    }

    @Override
    public void onUpdate(ItemStack stack, World world, Entity entity, int slot, boolean bool)
    {
        if (stack != null && stack.getItem() != null)
        {
            //Only update if we are a player, the slot is the held item
            if (entity instanceof EntityPlayer && slot == ((EntityPlayer) entity).inventory.currentItem)
            {
                if (stack.getItem() instanceof ItemGun)
                {
                    GunInstance gun = getGunInstance(stack, entity);
                    if (gun != null)
                    {
                        gun.debugRayTrace();

                        if (!world.isRemote)
                        {
                            if (leftClickHeld.containsKey(entity))
                            {
                                //Handle firing the weapon
                                if (gun.getChamberedRound() != null || gun.hasMagWithAmmo())
                                {
                                    int ticks = leftClickHeld.get(entity) + 1;
                                    leftClickHeld.put((EntityPlayer) entity, ticks);
                                    onLeftClickHeld(stack, gun, world, (EntityPlayer) entity, slot, ticks);
                                }
                                else
                                {
                                    gun.doReload = true;
                                    leftClickHeld.remove(entity);
                                }
                            }

                            //Handle firing the weapon
                            if (gun.doReload)
                            {
                                //If reloading, we are not sighted
                                gun.isSighted = false;

                                //Set reload delay if init
                                if (gun.reloadDelay == -1)
                                {
                                    if (!gun.getGunData().getReloadType().requiresItems())
                                    {
                                        //TODO play empty audio as we can not reload
                                        //TODO if weapon can reload via items remove this
                                        gun.doReload = false;
                                    }
                                    else if (gun.reloadWeapon(((EntityPlayer) entity).inventory, false))
                                    {
                                        ((EntityPlayer) entity).addChatComponentMessage(new ChatComponentText("Reloading weapon.... eta: " + gun.getGunData().getReloadTime() + "s")); //TODO translate
                                    }
                                    else
                                    {
                                        gun.cancelReload();
                                    }
                                }

                                if (gun.doReload)
                                {
                                    //Tick reload
                                    gun.doReloadTick();
                                    //Note reload time in chat, TODO move to GUI render
                                    if (gun.reloadDelay % 20 == 0)
                                    {
                                        int time = gun.getGunData().getReloadTime() - (gun.reloadDelay / 20);
                                        ((EntityPlayer) entity).addChatComponentMessage(new ChatComponentText("" + time));
                                    }
                                }
                            }
                        }
                    }
                }
                //If current is not the gun, clear cache
                else if (gunCache.containsKey(entity))
                {
                    gunCache.remove(entity);
                }
                //TODO consider storing several gun instances per player, using Stack value and slot to ID the cache entry
                //  TODO this will allow for more complex logic like ammo regen
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
        gun.fireWeapon(world, ticks);
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
    public GunInstance getGunInstance(ItemStack stack, Entity player)
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
                    if (instance.weaponUser != null && instance.weaponUser.getShooter() == player && instance.getGunData() != null)
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
        IWeaponUser user;
        if (entity instanceof EntityPlayer)
        {
            user = new WeaponUserPlayer((EntityPlayer) entity);
        }
        else
        {
            user = new WeaponUserEntity(entity);
        }
        GunInstance instance = new GunInstance(gunStack, user, data);
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
        return true; //TODO implement
    }

    @Override
    public String getRenderKey(ItemStack stack)
    {
        if (stack.getTagCompound() != null && getData(stack) != null)
        {
            GunData data = getData(stack);
            //TODO if this is changed make sure to update ItemGunRenderer
            NBTTagCompound nbt = stack.getTagCompound();
            boolean hasChamberedRound = nbt.hasKey(GunInstance.NBT_ROUND);
            boolean hasClipLoaded = nbt.hasKey(GunInstance.NBT_CLIP);
            boolean hasAmmo = false;
            boolean manuallyFeed = data.getReloadType() == ReloadType.BREACH_LOADED
                    || data.getReloadType() == ReloadType.FRONT_LOADED
                    || data.getReloadType() == ReloadType.HAND_FEED;

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
                return "gun.empty";
            }
            //Clip feed only stuff
            if (!manuallyFeed)
            {
                if (!hasChamberedRound)
                {
                    return "gun.chamber.none";
                }
                else if (!hasClipLoaded)
                {
                    return "gun.clip.none";
                }
                else if (!hasAmmo)
                {
                    return "gun.clip.empty";
                }
            }
        }
        //Null NBT is the same as empty
        else
        {
            return "gun.empty";
        }
        return "gun";
    }

    @Override
    public String getRenderKey(ItemStack stack, Entity entity, int usesRemaining)
    {
        GunInstance instance = getGunInstance(stack, entity);
        if (instance != null)
        {
            String key = getRenderKey(instance);
            if (key != null)
            {
                return key;
            }
        }
        return super.getRenderKey(stack, entity, usesRemaining);
    }

    @Override
    public EnumAction getItemUseAction(ItemStack stack)
    {
        return EnumAction.bow;
    }

    @Override
    public int getMaxItemUseDuration(ItemStack stack)
    {
        return Integer.MAX_VALUE;
    }

    public String getRenderKey(GunInstance instance)
    {
        if (instance != null)
        {
            boolean hasChamberedRound = instance.getChamberedRound() != null;
            boolean hasClipLoaded = instance.getLoadedClip() != null;
            boolean hasAmmo = instance.hasMagWithAmmo();
            boolean manuallyFeed = instance.isManuallyFeedClip();

            //TODO add more types
            if (!hasChamberedRound && !hasAmmo)
            {
                return "gun.empty";
            }
            //Clip feed only stuff
            if (!manuallyFeed)
            {
                if (!hasChamberedRound)
                {
                    return "gun.chamber.none";
                }
                else if (!hasClipLoaded)
                {
                    return "gun.clip.none";
                }
                else if (!hasAmmo)
                {
                    return "gun.clip.empty";
                }
            }
        }
        return null;
    }

    @SubscribeEvent
    public void keyHandler(InputEvent.KeyInputEvent e)
    {
        final int key = Keyboard.getEventKey();
        final long time = System.currentTimeMillis();
        if (key == Keyboard.KEY_GRAVE && (time - lastDebugKeyHit) > 1000)
        {
            lastDebugKeyHit = time;
            GunInstance.debugRayTraces = !GunInstance.debugRayTraces;
        }
    }

    @Override
    public int recharge(ItemStack itemStack, int e, boolean doAction)
    {
        if (e > 0)
        {
            //Get gun data
            GunData data = getData(itemStack);
            if (data != null && data.getReloadType() == ReloadType.ENERGY)
            {
                //Get charge data
                IEnergyChargeData chargeData = data.getChargeData();
                if (chargeData != null)
                {
                    //Get charge limit
                    int energy = Math.min(e, chargeData.getInputEnergyLimit());
                    if (energy > 0)
                    {
                        //Get current state of item
                        int capacity = getEnergyCapacity(itemStack);
                        int prev = getEnergy(itemStack);
                        int energyStorage = prev;
                        int roomLeft = capacity - prev;

                        int energyUsed;

                        //Energy will fit into room left
                        if (energy <= roomLeft)
                        {
                            energyStorage += energy;
                            energyUsed = energy;
                        }
                        else
                        {
                            energyStorage = capacity;
                            energyUsed = roomLeft;
                        }

                        //Only update item if we should do an action
                        if (doAction)
                        {
                            setEnergy(itemStack, energyStorage);
                        }

                        return energyUsed;
                    }
                }
            }
        }
        return 0;
    }

    @Override
    public int discharge(ItemStack itemStack, int e, boolean doDischarge)
    {
        GunData data = getData(itemStack);
        if (data != null)
        {
            IEnergyChargeData chargeData = data.getChargeData();
            if (chargeData != null)
            {
                return consumeEnergy(itemStack, e, chargeData.getOutputEnergyLimit(), doDischarge);
            }
        }
        return 0;
    }

    public int consumeEnergy(ItemStack itemStack, int e, int outputLimit, boolean doDischarge)
    {
        if (e > 0)
        {
            int energy = Math.min(e, outputLimit);
            if (energy > 0)
            {
                int currentEnergy = getEnergy(itemStack);
                if (currentEnergy <= energy)
                {
                    if (doDischarge)
                    {
                        setEnergy(itemStack, 0);
                    }
                    return currentEnergy;
                }
                else
                {
                    int newEnergy = currentEnergy - energy;
                    if (doDischarge)
                    {
                        setEnergy(itemStack, newEnergy);
                    }
                    return energy;
                }
            }
        }
        return 0;
    }

    @Override
    public int getEnergy(ItemStack itemStack)
    {
        return getItemEnergy(itemStack);
    }

    @Override
    public int getEnergyCapacity(ItemStack itemStack)
    {
        GunData data = getData(itemStack);
        if (data != null)
        {
            IEnergyBufferData bufferData = data.getBufferData();
            if (bufferData != null)
            {
                return bufferData.getEnergyCapacity();
            }
        }
        return 0;
    }

    @Override
    public void setEnergy(ItemStack itemStack, int energy)
    {
        setItemEnergy(itemStack, energy);
    }

    public static int getItemEnergy(ItemStack itemStack)
    {
        if (itemStack.getTagCompound() != null)
        {
            return getItemEnergy(itemStack.getTagCompound());
        }
        return 0;
    }

    public static int getItemEnergy(NBTTagCompound tag)
    {
        return tag.getInteger(NBT_ENERGY);
    }

    public static void setItemEnergy(ItemStack itemStack, int power)
    {
        if (itemStack.getTagCompound() == null)
        {
            itemStack.setTagCompound(new NBTTagCompound());
        }
        setItemEnergy(itemStack.getTagCompound(), power);
    }

    public static void setItemEnergy(NBTTagCompound tag, int power)
    {
        tag.setInteger(NBT_ENERGY, power);
    }
}
