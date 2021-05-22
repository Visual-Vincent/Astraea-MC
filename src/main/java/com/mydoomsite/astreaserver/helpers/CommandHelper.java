package com.mydoomsite.astreaserver.helpers;

import com.mydoomsite.astreaserver.main.MainRegistry;

import net.minecraft.command.CommandSource;
import net.minecraft.util.text.StringTextComponent;

public final class CommandHelper
{
    public static boolean IsExecutedByServer(CommandSource src)
    {
        return src.hasPermission(4) && src.getTextName() == "Server";
    }
    
    public static void LogCommandSuccess(CommandSource src, String text, boolean logToConsole, boolean informAdmins)
    {
        if(logToConsole && !informAdmins && !IsExecutedByServer(src))
        {
            String consoleText = text;
            try
            {
                consoleText = "[" + src.getPlayerOrException().getName().getContents() + ": " + text + "]";
            }
            catch(Exception ex) {}
            
            // TODO: Strip formatting codes?
            MainRegistry.Logger.info(consoleText);
        }
        
        src.sendSuccess(new StringTextComponent(text), informAdmins);
    }
    
    public static void LogCommandFailure(CommandSource src, String text)
    {
        src.sendFailure(new StringTextComponent(text));
    }
}
