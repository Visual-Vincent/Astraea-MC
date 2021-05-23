package com.mydoomsite.astreaserver.helpers;

import com.mydoomsite.astreaserver.datatypes.MethodOverriddenChecker;

import net.minecraft.entity.Entity;
import net.minecraft.entity.MobEntity;

public final class EntityHelper
{
    private static final MethodOverriddenChecker INTERACT_METHOD_CHECKER     = new MethodOverriddenChecker(Entity.class, "interact", "func_184230_a");
    private static final MethodOverriddenChecker INTERACT_AT_METHOD_CHECKER  = new MethodOverriddenChecker(Entity.class, "interactAt", "func_184199_a");
    private static final MethodOverriddenChecker MOB_INTERACT_METHOD_CHECKER = new MethodOverriddenChecker(MobEntity.class, "mobInteract", "func_230254_b_");
    
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
    
    public static boolean IsInteractable(MobEntity entity)
    {
        // Checks whether the MobEntity.mobInteract() method has been overridden
        return MOB_INTERACT_METHOD_CHECKER.IsMethodOverriddenIn(entity);
    }
}
