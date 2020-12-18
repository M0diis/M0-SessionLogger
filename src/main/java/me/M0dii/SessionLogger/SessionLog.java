package me.M0dii.SessionLogger;

import org.bukkit.entity.Player;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public class SessionLog
{
    private final UUID uuid;
    private final String name;
    private final Long joinTime;
    private int issuedCommands;
    private String joinDate;
    
    public SessionLog(Player p)
    {
        this.uuid = p.getUniqueId();
        this.name = p.getName();
        
        this.joinTime = System.currentTimeMillis();
        this.issuedCommands = 0;
        
        this.setJoinDate();
    }
    
    public void issuedCommand()
    {
        this.issuedCommands++;
    }
    
    public UUID getPlayerUUID() {
        return this.uuid;
    }
    
    public Long getJoinTime() {
        return this.joinTime;
    }
    
    public String getPlayerName() {
        return this.name;
    }
    
    public int getIssuedCommands() {
        return this.issuedCommands;
    }
    
    public String getJoinDate() {
        return this.joinDate;
    }
    
    public void setJoinDate()
    {
        SimpleDateFormat formatter = new SimpleDateFormat(Config.DATE_FORMAT);
        
        this.joinDate = formatter.format(new Date());
    }
}
