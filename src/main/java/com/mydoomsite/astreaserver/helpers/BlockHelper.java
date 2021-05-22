package com.mydoomsite.astreaserver.helpers;

import java.lang.reflect.Method;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;

public final class BlockHelper
{
    private static final String USE_METHOD_DEBUG_NAME = "use";
    private static final String USE_METHOD_COMPILED_NAME = "func_225533_a_";
    
    private static final String USE_METHOD_NAME;
    private static final Class<?>[] USE_METHOD_PARAMETER_TYPES;
    
    
    public static void Init() {};
    
    static
    {
        String methodName = null;
        Class<?>[] parameterTypes = null;
        
        for(Method method : AbstractBlock.class.getMethods())
        {
            methodName = method.getName();
            if(methodName == USE_METHOD_COMPILED_NAME || methodName == USE_METHOD_DEBUG_NAME)
            {
                parameterTypes = method.getParameterTypes();
                break;
            }
        }
        
        if(methodName == null || parameterTypes == null)
            throw new NullPointerException(String.format("FATAL ERROR: Failed to find method '%s' in %s", USE_METHOD_DEBUG_NAME, AbstractBlock.class.getCanonicalName()));
        
        USE_METHOD_NAME = methodName;
        USE_METHOD_PARAMETER_TYPES = parameterTypes;
    }
    
    public static boolean IsInteractable(Block block)
    {
        try
        {
            // Checks whether the AbstractBlock.use() method has been overridden
            return block.getClass().getMethod(USE_METHOD_NAME, USE_METHOD_PARAMETER_TYPES).getDeclaringClass().getCanonicalName() != AbstractBlock.class.getCanonicalName();
        }
        catch (NoSuchMethodException e)
        {
            return false;
        }
    }
}
