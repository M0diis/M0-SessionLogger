package me.M0dii.SessionLogger;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;

public class SessionLogger extends JavaPlugin
{
    private static SessionLogger plugin;
    
    private PluginManager manager;
    
    private boolean loaded;
    
    public SessionLogger()
    {
        this.manager = getServer().getPluginManager();
    }
    
    FileConfiguration config = null;
    File configFile = null;
    
    public void onEnable()
    {
        this.configFile = new File(getDataFolder(), "config.yml");
        this.config = YamlConfiguration.loadConfiguration(this.configFile);
    
        if(!this.configFile.exists())
        {
            this.configFile.getParentFile().mkdirs();
        
            copy(getResource("config.yml"), configFile);
        }
        
        Config.load(this);
        
        this.manager.registerEvents(new PlayerListener(this), this);
    }
    
    public void logToFile(String message)
    {
        try
        {
            File dataFolder = getDataFolder();
            if(!dataFolder.exists())
            {
                dataFolder.mkdir();
            }
            
            File saveTo = new File(getDataFolder(), "sessionLog.txt");
            
            if (!saveTo.exists())
            {
                saveTo.createNewFile();
            }
            
            FileWriter fw = new FileWriter(saveTo, true);
            
            PrintWriter pw = new PrintWriter(fw);
            
            pw.println(message);
            
            pw.flush();
            
            pw.close();
            
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
    
    private void copy(InputStream in, File file)
    {
        if(in == null)
        {
            this.getLogger().warning("Cannot copy, resource null");
            
            return;
        }
        
        try
        {
            OutputStream out = new FileOutputStream(file);
            
            byte[] buf = new byte[1024];
            
            int len;
            
            while((len = in.read(buf)) > 0)
            {
                out.write(buf, 0, len);
            }
            
            out.close();
            in.close();
        }
        catch(Exception e)
        {
            this.getLogger().warning("Error copying resource: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    
    public void onDisable()
    {
        this.manager.disablePlugin(this);
    }
}
