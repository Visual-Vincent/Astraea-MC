package com.mydoomsite.astreaserver.helpers;

import java.lang.reflect.Method;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;

public final class BlockHelper
{
    private static final String USE_METHOD_NAME = "use";
    private static final Class<?>[] USE_METHOD_PARAMETER_TYPES;
    
    public static void Init() {};
    
    static
    {
        Class<?>[] parameterTypes = null;
        for(Method method : AbstractBlock.class.getMethods())
        {
            if(method.getName() == USE_METHOD_NAME)
            {
                parameterTypes = method.getParameterTypes();
                break;
            }
        }
        
        if(parameterTypes == null)
            throw new NullPointerException(String.format("FATAL ERROR: Failed to find method '%s' in %s", USE_METHOD_NAME, AbstractBlock.class.getCanonicalName()));
        
        USE_METHOD_PARAMETER_TYPES = parameterTypes;
    }
    
    public static boolean IsInteractable(Block block)
    {
        try
        {
            // Checks whether the AbstractBlock.use() method has been overridden
            return block.getClass().getMethod("use", USE_METHOD_PARAMETER_TYPES).getDeclaringClass().getCanonicalName() != AbstractBlock.class.getCanonicalName();
        }
        catch (NoSuchMethodException e)
        {
            return false;
        }
    }
}
