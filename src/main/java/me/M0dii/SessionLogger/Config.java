package me.M0dii.SessionLogger;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.List;

public class Config
{
    public static String DATE_FORMAT;
    public static String ENTRY_FORMAT;
    public static List<String> EXCLUDED_CMDS;
    
    public static void load(SessionLogger plugin)
    {
        FileConfiguration cfg = plugin.getConfig();
        
        DATE_FORMAT = cfg.getString("M0-SessionLogger.DateFormat");
        ENTRY_FORMAT = cfg.getString("M0-SessionLogger.EntryFormat");
        EXCLUDED_CMDS = cfg.getStringList("M0-SessionLogger.ExcludeCommands");
    }
    
    private static String format(String text)
    {
        return ChatColor.translateAlternateColorCodes('&', text);
    }
}
