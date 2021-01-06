package space.devport.wertik.orbs.listeners;

import com.bgsoftware.superiorskyblock.api.events.IslandCreateEvent;
import com.bgsoftware.superiorskyblock.api.events.IslandDisbandEvent;
import com.bgsoftware.superiorskyblock.api.events.IslandJoinEvent;
import com.bgsoftware.superiorskyblock.api.events.IslandQuitEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import space.devport.wertik.orbs.OrbsPlugin;
import space.devport.wertik.orbs.system.struct.IslandAccount;
import space.devport.wertik.orbs.system.struct.PlayerAccount;

import java.util.UUID;

public class IslandListener implements Listener {

    private final OrbsPlugin plugin;

    public IslandListener(OrbsPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onIslandQuit(IslandQuitEvent event) {
        UUID islandUUID = event.getIsland().getUniqueId();
        UUID playerUUID = event.getPlayer().getUniqueId();

        IslandAccount islandAccount = plugin.getAccountManager().getIslandAccounts().get(islandUUID);

        if (islandAccount == null)
            return;

        // Remove player form island account
        islandAccount.removeAccount(playerUUID);

        if (plugin.getConfig().getBoolean("wipe-on-island-quit", false))
            plugin.getAccountManager().getPlayerAccounts().remove(playerUUID);
    }

    @EventHandler
    public void onIslandJoin(IslandJoinEvent event) {
        UUID islandUUID = event.getIsland().getUniqueId();
        UUID playerUUID = event.getPlayer().getUniqueId();

        // Add player to island account
        PlayerAccount playerAccount = plugin.getAccountManager().getPlayerAccounts().getOrCreate(playerUUID);
        plugin.getAccountManager().getIslandAccount(islandUUID).addAccount(playerAccount);
    }

    @EventHandler
    public void onIslandDisband(IslandDisbandEvent event) {
        UUID islandUUID = event.getIsland().getUniqueId();

        // Destroy island account
        plugin.getAccountManager().getIslandAccounts().remove(islandUUID);
    }

    @EventHandler
    public void onIslandCreate(IslandCreateEvent event) {
        UUID islandUUID = event.getIsland().getUniqueId();

        IslandAccount islandAccount = plugin.getAccountManager().getIslandAccounts().create(islandUUID);

        // Add leader to island account
        UUID leaderUUID = event.getIsland().getOwner().getUniqueId();
        PlayerAccount playerAccount = plugin.getAccountManager().getPlayerAccounts().getOrCreate(leaderUUID);
        islandAccount.addAccount(playerAccount);
    }
}
