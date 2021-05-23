package com.mydoomsite.astreaserver.datatypes;

import java.lang.reflect.Method;

import com.mydoomsite.astreaserver.helpers.ReflectionHelper;

public final class MethodOverriddenChecker
{
    private final String BASE_CLASS_NAME;
    private final String METHOD_DEBUG_NAME;
    private final String METHOD_COMPILED_NAME;
    private final Class<?>[] METHOD_PARAMETER_TYPES;
    
    private final String selectedMethodName;
    
    public MethodOverriddenChecker(Class<?> baseClass, String methodDebugName, String methodCompiledName)
    {
        if(baseClass == null)
            throw new IllegalArgumentException("'baseClass' cannot be null");
        
        if(methodDebugName == null || methodDebugName.length() <= 0)
            throw new IllegalArgumentException("'methodDebugName' cannot be empty");
        
        if(methodCompiledName == null || methodCompiledName.length() <= 0)
            throw new IllegalArgumentException("'methodCompiledName' cannot be empty");
        
        BASE_CLASS_NAME = baseClass.getCanonicalName();
        METHOD_DEBUG_NAME = methodDebugName;
        METHOD_COMPILED_NAME = methodCompiledName;
        
        String methodName = null;
        Class<?>[] parameterTypes = null;
        
        for(Method method : baseClass.getDeclaredMethods())
        {
            methodName = method.getName();
            if(methodName == METHOD_COMPILED_NAME || methodName == METHOD_DEBUG_NAME)
            {
                parameterTypes = method.getParameterTypes();
                break;
            }
        }
        
        if(methodName == null || parameterTypes == null)
            throw new NullPointerException(String.format("Failed to find method '%s' in %s", METHOD_DEBUG_NAME, BASE_CLASS_NAME));
        
        selectedMethodName = methodName;
        METHOD_PARAMETER_TYPES = parameterTypes;
    }
    
    public boolean IsMethodOverriddenIn(Object obj)
    {
        try
        {
            return ReflectionHelper.FindMethod(obj.getClass(), selectedMethodName, METHOD_PARAMETER_TYPES).getDeclaringClass().getCanonicalName() != BASE_CLASS_NAME;
        }
        catch (NoSuchMethodException e)
        {
            return false;
        }
    }
}
