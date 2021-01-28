package me.M0dii.SessionLogger;

import com.earth2me.essentials.IEssentials;
import com.earth2me.essentials.commands.EssentialsCommand;
import net.ess3.api.IUser;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerCommandSendEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

public class PlayerListener implements Listener
{
    private final SessionLogger plugin;
    private final IEssentials ess;
    
    public PlayerListener(SessionLogger plugin)
    {
        this.plugin = plugin;
        
        this.ess = plugin.getEssentials();
    }
    
    private final ArrayList<Log> logs = new ArrayList<>();
    
    @EventHandler
    public void logIn(PlayerLoginEvent e)
    {
        if(e.getPlayer().hasPermission("m0sessionlogger.log"))
        {
            this.logs.add(new Log(e.getPlayer()));
        }
    }
    
    @EventHandler
    public void trackVanish(PlayerCommandPreprocessEvent e)
    {
        Player p = e.getPlayer();
        
        String cmd = e.getMessage().split(" ")[0];
        
        if(cmd.equalsIgnoreCase("/vanish")
        || cmd.equalsIgnoreCase("/v"))
        {
            IUser user = ess.getUser(p.getUniqueId());
    
            new BukkitRunnable()
            {
                int time = 0;
        
                @Override
                public void run()
                {
                    if(user != null && user.isVanished())
                    {
                        time++;
                    }
                    else
                    {
                        this.cancel();
                
                        for(Log log : logs)
                        {
                            if(log.getPlayerUUID().equals(p.getUniqueId()))
                            {
                                log.addVanishedTime(time);
                            }
                        }
                    }
                }
            }.runTaskTimerAsynchronously(this.plugin, 0L, 20L);
        }
    }
    
    @EventHandler
    public void addCommand(PlayerCommandPreprocessEvent e)
    {
        Player p = e.getPlayer();
        
        List<String> excluded = Config.EXCLUDED_CMDS;
        
        String cmd = e.getMessage().split(" ")[0];
        
        if(excluded.contains(cmd))
            return;
    
        for(Log log : logs)
        {
            if(log.getPlayerUUID().equals(p.getUniqueId()))
            {
                log.issuedCommand();
            }
        }
    }
    
    @EventHandler
    public void logOut(PlayerQuitEvent e)
    {
        Log log = null;
        
        for(Log lg : logs)
        {
            if(lg.getPlayerUUID().equals(e.getPlayer().getUniqueId())) log = lg;
        }
        
        if(log != null)
        {
            long loggedIn = log.getJoinTime();
            
            long elapsed = System.currentTimeMillis() - loggedIn;
    
            String played = String.format("%02dh %02dm %02ds",
                    TimeUnit.MILLISECONDS.toHours(elapsed),
                    TimeUnit.MILLISECONDS.toMinutes(elapsed) -
                    TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(elapsed)),
                    TimeUnit.MILLISECONDS.toSeconds(elapsed) -
                    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(elapsed)));
    
            String leaveDate = new SimpleDateFormat(Config.DATE_FORMAT).format(new Date());
            
            String entry = Config.ENTRY_FORMAT.replaceAll("%join_date%", log.getJoinDate());
            
            entry = entry.replaceAll("%leave_date%", leaveDate);
            entry = entry.replaceAll("%player_name%", log.getPlayerName());
            entry = entry.replaceAll("%played_time%", played);
            entry = entry.replaceAll("%issued_commands%", String.valueOf(log.getIssuedCommands()));
            entry = entry.replaceAll("%vanished_time%", String.valueOf(log.getVanishedTime()));
            
            this.logs.remove(log);
            
            this.plugin.logToFile(entry);
        }
    }
}
