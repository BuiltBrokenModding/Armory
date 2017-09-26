package com.builtbroken.armory.content.items;

import com.builtbroken.armory.api.ArmoryAPI;
import com.builtbroken.armory.data.meele.MeleeWeaponData;
import com.google.common.collect.Multimap;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.item.ItemStack;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 9/26/2017.
 */
public class ItemMeleeWeapon extends ItemTool<MeleeWeaponData>
{
    public ItemMeleeWeapon()
    {
        super("armoryMeleeWeapon", ArmoryAPI.MELEE_WEAPON_ID, ArmoryAPI.MELEE_WEAPON_ID);
        this.maxStackSize = 1;
    }

    @Override
    public Multimap getAttributeModifiers(ItemStack stack)
    {
        Multimap multimap = super.getAttributeModifiers(stack);
        multimap.put(SharedMonsterAttributes.attackDamage.getAttributeUnlocalizedName(), new AttributeModifier(field_111210_e, "Weapon modifier", (double) getDamageVsEntity(stack), 0));
        return multimap;
    }

    public float getDamageVsEntity(ItemStack stack)
    {
        MeleeWeaponData data = getData(stack);
        if(data != null)
        {
            return data.getDamageVsEntity();
        }
        return 1;
    }
}
