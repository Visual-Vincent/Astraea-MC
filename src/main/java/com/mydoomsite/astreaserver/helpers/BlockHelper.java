package com.mydoomsite.astreaserver.helpers;

import com.mydoomsite.astreaserver.datatypes.MethodOverriddenChecker;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;

public final class BlockHelper
{
    private static final MethodOverriddenChecker USE_METHOD_CHECKER = new MethodOverriddenChecker(BlockBehaviour.class, "use", "m_6227_");
    
    public static void Init() {};
    
    public static boolean IsInteractable(Block block)
    {
        // Checks whether the BlockBehaviour.use() method has been overridden
        return USE_METHOD_CHECKER.IsMethodOverriddenIn(block);
    }
}
