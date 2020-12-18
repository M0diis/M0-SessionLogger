package me.M0dii.SessionLogger;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandSendEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class PlayerListener implements Listener
{
    private final Main plugin;
    
    public PlayerListener(Main plugin)
    {
        this.plugin = plugin;

        this.sessionLogs = new ArrayList<>();
    }
    
    private final List<SessionLog> sessionLogs;
    
    @EventHandler
    public void logIn(PlayerLoginEvent e)
    {
        if(e.getPlayer().hasPermission("m0sessionlogger.log"))
        {
            this.sessionLogs.add(new SessionLog(e.getPlayer()));
        }
    }
    
    @EventHandler
    public void addCommand(PlayerCommandSendEvent e)
    {
        for(SessionLog log : sessionLogs)
        {
            if(log.getPlayerUUID() == e.getPlayer().getUniqueId())
            {
                log.issuedCommand();
            }
        }
    }
    
    @EventHandler
    public void logOut(PlayerQuitEvent e)
    {
        SessionLog log = null;
        
        for(SessionLog lg : sessionLogs)
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
            
            this.sessionLogs.remove(log);
            
            this.plugin.logToFile(entry);
        }
    }
}
