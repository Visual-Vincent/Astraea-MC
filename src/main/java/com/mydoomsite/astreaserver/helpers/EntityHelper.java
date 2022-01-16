package com.mydoomsite.astreaserver.helpers;

import com.mydoomsite.astreaserver.datatypes.MethodOverriddenChecker;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;

public final class EntityHelper
{
    private static final MethodOverriddenChecker INTERACT_METHOD_CHECKER     = new MethodOverriddenChecker(Entity.class, "interact", "m_6096_");
    private static final MethodOverriddenChecker INTERACT_AT_METHOD_CHECKER  = new MethodOverriddenChecker(Entity.class, "interactAt", "m_7111_");
    private static final MethodOverriddenChecker MOB_INTERACT_METHOD_CHECKER = new MethodOverriddenChecker(Mob.class, "mobInteract", "m_6071_");
    
    public static void Init() {};
    
    public static boolean IsInteractable(Entity entity)
    {
        // Checks whether the Entity.interact() method has been overridden
        return INTERACT_METHOD_CHECKER.IsMethodOverriddenIn(entity);
    }
    
    public static boolean IsInteractableSpecific(Entity entity)
    {
        // Checks whether the Entity.interactAt() method has been overridden
        return INTERACT_AT_METHOD_CHECKER.IsMethodOverriddenIn(entity);
    }
    
    public static boolean IsInteractable(Mob entity)
    {
        // Checks whether the MobEntity.mobInteract() method has been overridden
        return MOB_INTERACT_METHOD_CHECKER.IsMethodOverriddenIn(entity);
    }
}
