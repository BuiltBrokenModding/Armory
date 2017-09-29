package com.builtbroken.armory.data.damage.area;

import com.builtbroken.armory.Armory;
import com.builtbroken.armory.data.damage.DamageData;
import com.builtbroken.jlib.data.Colors;
import com.builtbroken.mc.api.edit.IWorldChangeAction;
import com.builtbroken.mc.api.event.TriggerCause;
import com.builtbroken.mc.api.explosive.IExplosiveHandler;
import com.builtbroken.mc.core.Engine;
import com.builtbroken.mc.framework.explosive.ExplosiveRegistry;
import com.builtbroken.mc.framework.json.imp.IJsonProcessor;
import com.builtbroken.mc.lib.debug.DebugHelper;
import com.builtbroken.mc.lib.world.edit.WorldChangeHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.World;

/**
 * Damage type that triggers a blast on impact
 *
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 3/30/2017.
 */
public class DamageBlast extends DamageData
{
    public final String blastName;

    private IExplosiveHandler handlerCache;
    private float size;

    public DamageBlast(IJsonProcessor processor, String blastName, float size)
    {
        super(processor);
        this.blastName = blastName;
        this.size = size;
    }

    public IExplosiveHandler getExplosiveHandler()
    {
        if (handlerCache == null)
        {
            handlerCache = ExplosiveRegistry.get(blastName);
        }
        return handlerCache;
    }

    @Override
    public boolean onImpact(Entity attacker, World world, int x, int y, int z, double hitX, double hitY, double hitZ, float velocity, float scale)
    {
        if (!world.isRemote)
        {
            doAOE(attacker, world, hitX, hitY, hitZ, velocity, scale);
        }
        return true;
    }

    @Override
    public String getDisplayString()
    {
        if(getExplosiveHandler() == null)
        {
            return Engine.runningAsDev ? Colors.RED.code + "Error: blast " + blastName : null;
        }
        return "Blast x" + size + " of " + blastName;
    }

    @Override
    public boolean onImpact(Entity attacker, Entity entity, double hitX, double hitY, double hitZ, float velocity, float scale)
    {
        if (entity != null)
        {
            //TODO apply impact damage to hit entity
            doAOE(attacker, entity.worldObj, hitX, hitY, hitZ, velocity, scale);
        }
        return true;
    }

    protected void doAOE(Entity attacker, World world, double x, double y, double z, float velocity, float scale)
    {
        IExplosiveHandler handler = getExplosiveHandler();
        if (handler != null)
        {
            TriggerCause cause = new TriggerCause.TriggerCauseImpact(attacker, velocity);
            IWorldChangeAction action = handler.createBlastForTrigger(world, x, y, z, cause, size, new NBTTagCompound());
            if (action != null)
            {
                WorldChangeHelper.ChangeResult result = WorldChangeHelper.doAction(world, x, y, z, action, cause);
                if (attacker instanceof EntityPlayer)
                {
                    switch (result)
                    {
                        case FAILED:
                            ((EntityPlayer) attacker).addChatComponentMessage(new ChatComponentText("Blast failed to trigger due to an unknown error."));
                            break;
                        case BLOCKED:
                            ((EntityPlayer) attacker).addChatComponentMessage(new ChatComponentText("Blast was blocked from completing, this may be due to a protection system or plugin."));
                            break;
                        case PARTIAL_COMPLETE_WITH_FAILURE:
                            ((EntityPlayer) attacker).addChatComponentMessage(new ChatComponentText("Blast completed some steps but failed."));
                            break;
                    }
                }
            }
            else
            {
                DebugHelper.outputMethodDebug(Armory.INSTANCE.logger(), "doAOE", "\nnull blast from " + handler, attacker, world, x, y, z, velocity, scale);
            }
        }
        else
        {
            DebugHelper.outputMethodDebug(Armory.INSTANCE.logger(), "doAOE", "\nno blast handler for " + this, attacker, world, x, y, z, velocity, scale);
        }
    }

    @Override
    public String toString()
    {
        return "DamageBlast[" + blastName + "@" + size + "]@" + hashCode();
    }
}
