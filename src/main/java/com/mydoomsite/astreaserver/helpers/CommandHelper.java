package com.mydoomsite.astreaserver.helpers;

import com.mydoomsite.astreaserver.main.MainRegistry;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.TextComponent;

public final class CommandHelper
{
    public static boolean IsExecutedByServer(CommandSourceStack src)
    {
        return src.hasPermission(4) && src.getTextName() == "Server";
    }
    
    public static void LogCommandSuccess(CommandSourceStack src, String text, boolean logToConsole, boolean informAdmins)
    {
        if(logToConsole && !informAdmins && !IsExecutedByServer(src))
        {
            String consoleText = text;
            try
            {
                consoleText = "[" + src.getPlayerOrException().getName().getContents() + ": " + text + "\u00A7r]";
            }
            catch(Exception ex) {}
            
            // TODO: Strip formatting codes?
            MainRegistry.Logger.info(consoleText);
        }
        
        src.sendSuccess(new TextComponent(text + "\u00A7r"), informAdmins);
    }
    
    public static void LogCommandFailure(CommandSourceStack src, String text)
    {
        src.sendFailure(new TextComponent(text + "\u00A7r"));
    }
}
