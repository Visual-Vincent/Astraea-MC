package com.mydoomsite.astreaserver.helpers;

import com.mydoomsite.astreaserver.datatypes.MethodOverriddenChecker;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;

public final class BlockHelper
{
    private static final MethodOverriddenChecker USE_METHOD_CHECKER = new MethodOverriddenChecker(AbstractBlock.class, "use", "func_225533_a_");
    
    public static void Init() {};
    
    public static boolean IsInteractable(Block block)
    {
        // Checks whether the AbstractBlock.use() method has been overridden
        return USE_METHOD_CHECKER.IsMethodOverriddenIn(block);
    }
}
