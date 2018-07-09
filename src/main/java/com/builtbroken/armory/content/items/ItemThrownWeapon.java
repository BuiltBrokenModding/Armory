package com.builtbroken.armory.content.items;

import com.builtbroken.armory.Armory;
import com.builtbroken.armory.api.ArmoryAPI;
import com.builtbroken.armory.api.events.EventWeaponThrown;
import com.builtbroken.armory.content.entity.projectile.EntityAmmoProjectile;
import com.builtbroken.armory.content.prefab.ItemMetaArmoryEntry;
import com.builtbroken.armory.data.ranged.ThrowableData;
import com.builtbroken.armory.data.user.WeaponUserPlayer;
import com.builtbroken.mc.api.data.weapon.IAmmoData;
import com.builtbroken.mc.api.items.weapons.IItemAmmo;
import com.builtbroken.mc.api.items.weapons.IItemReloadableWeapon;
import com.builtbroken.mc.imp.transform.vector.Pos;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;

/**
 * Simple throwable weapon weapon used for (grenades, daggers, knives, throwing stars, spears, etc)
 *
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 8/1/2017.
 */
public class ItemThrownWeapon extends ItemMetaArmoryEntry<ThrowableData> implements IItemAmmo
{
    public static int HOLD_DURATION = 5 * 20; //TODO move to data

    public ItemThrownWeapon()
    {
        super("armoryThrownWeapon", ArmoryAPI.THROWABLE_WEAPON_ID, ArmoryAPI.THROWABLE_WEAPON_ID);
        this.setHasSubtypes(true);
    }

    @Override
    public ItemStack onEaten(ItemStack par1ItemStack, World par2World, EntityPlayer par3EntityPlayer)
    {
        return par1ItemStack;
    }

    @Override
    public EnumAction getItemUseAction(ItemStack par1ItemStack)
    {
        return EnumAction.bow;
    }

    @Override
    public int getMaxItemUseDuration(ItemStack par1ItemStack)
    {
        return HOLD_DURATION;
    }

    @Override
    public ItemStack onItemRightClick(ItemStack itemStack, World world, EntityPlayer entityPlayer)
    {
        if (itemStack != null)
        {
            //Fire event to let devs block action
            EventWeaponThrown evt = new EventWeaponThrown.PreHold(entityPlayer);
            MinecraftForge.EVENT_BUS.post(evt);

            if (!evt.isCanceled())
            {
                //Set item in use to allow holding throw
                entityPlayer.setItemInUse(itemStack, this.getMaxItemUseDuration(itemStack));
            }
            else
            {
                entityPlayer.addChatMessage(new ChatComponentText("Grenades are banned in this region."));
            }
        }

        return itemStack;
    }

    @Override
    public void onPlayerStoppedUsing(ItemStack itemStack, World world, EntityPlayer entityPlayer, int ticks)
    {
        if (!world.isRemote)
        {
            //Fire event to let devs block action
            EventWeaponThrown evt = new EventWeaponThrown.PreThrow(entityPlayer);
            MinecraftForge.EVENT_BUS.post(evt);

            if (!evt.isCanceled())
            {
                //Get ammo data
                IAmmoData nextRound = getAmmoData(itemStack);
                if (nextRound != null)
                {
                    final WeaponUserPlayer weaponUser = new WeaponUserPlayer(entityPlayer); //TODO cache

                    //Calculate position and aim
                    final Pos aim = weaponUser.getEntityAim().toPos();
                    final Pos entityPos = weaponUser.getEntityPosition();

                    //Create projectile
                    EntityAmmoProjectile projectile = new EntityAmmoProjectile(world, nextRound, null, weaponUser.getShooter());

                    float power = Math.min(1, (float) (this.getMaxItemUseDuration(itemStack) - ticks) / 4f);

                    //Set entity spawn point
                    Pos spawnPoint = entityPos.add(aim).add(weaponUser.getProjectileSpawnOffset());
                    projectile.setPosition(spawnPoint.x(), spawnPoint.y(), spawnPoint.z());

                    //Set velocity
                    float velocity = nextRound.getProjectileVelocity();
                    velocity *= power;
                    projectile.setThrowableHeading(aim.x(), aim.y(), aim.z(), velocity, 0);

                    //Spawn
                    world.spawnEntityInWorld(projectile);

                    //Play feedback sound
                    //TODO add JSON audio trigger for throw
                    //TODO add JSON particle trigger for throw

                    //Consume item last
                    if (!entityPlayer.capabilities.isCreativeMode)
                    {
                        itemStack.stackSize--;

                        if (itemStack.stackSize <= 0)
                        {
                            entityPlayer.inventory.setInventorySlotContents(entityPlayer.inventory.currentItem, null);
                        }
                    }

                    //Fire post event to allow actions to be taken
                    evt = new EventWeaponThrown.Post(entityPlayer);
                    MinecraftForge.EVENT_BUS.post(evt);
                }
                else
                {
                    entityPlayer.addChatMessage(new ChatComponentTranslation("error." + Armory.PREFIX + "weapon.thrown.ammo.null"));
                }
            }
            else
            {
                entityPlayer.addChatMessage(new ChatComponentTranslation(evt.cancelReason));
            }
        }
    }

    @Override
    public IAmmoData getAmmoData(ItemStack stack)
    {
        return getData(stack).ammoData;
    }

    @Override
    public int getAmmoCount(ItemStack ammoStack)
    {
        return 1;
    }

    @Override
    public void consumeAmmo(IItemReloadableWeapon weapon, ItemStack weaponStack, ItemStack ammoStack, int shotsFired)
    {
        ammoStack.stackSize--;
    }
}
