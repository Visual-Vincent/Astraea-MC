package com.mydoomsite.astreaserver.helpers;

import java.lang.reflect.Method;

public final class ReflectionHelper
{
    public static Method FindMethod(Class<?> targetClass, String methodName, Class<?>... parameterTypes) throws NoSuchMethodException
    {
        if(targetClass == null)
            throw new IllegalArgumentException("'targetClass' cannot be null");
        
        if(methodName == null || methodName.length() <= 0)
            throw new IllegalArgumentException("'methodName' cannot be empty");
        
        Class<?> current = targetClass;
        
        while(current != null)
        {
            try
            {
                Method method = current.getDeclaredMethod(methodName, parameterTypes);
                return method;
            }
            catch (NoSuchMethodException e)
            {
                current = current.getSuperclass();
            }
        }
        
        throw new NoSuchMethodException("Failed to find method '" + methodName + "' in " + targetClass.getCanonicalName() + " or any of its inherited classes");
    }
}
