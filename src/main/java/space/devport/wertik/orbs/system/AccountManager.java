package space.devport.wertik.orbs.system;

import lombok.Getter;
import lombok.extern.java.Log;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import space.devport.utils.utility.json.GsonHelper;
import space.devport.wertik.orbs.OrbsPlugin;
import space.devport.wertik.orbs.system.struct.IslandAccount;
import space.devport.wertik.orbs.system.struct.PlayerAccount;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Log
public class AccountManager {

    private final OrbsPlugin plugin;

    private final GsonHelper gsonHelper = new GsonHelper();

    @Getter
    private final AccountCache<IslandAccount> islandAccounts = new AccountCache<>(IslandAccount::new);
    @Getter
    private final AccountCache<PlayerAccount> playerAccounts;

    @Getter
    private final TopCache topCache;

    public AccountManager(OrbsPlugin plugin) {
        this.plugin = plugin;
        this.playerAccounts = new AccountCache<>(uuid -> new PlayerAccount(uuid, plugin.getConfig().getDouble("default-balance", 0)));
        this.topCache = new TopCache(plugin);
    }

    public void load() {
        playerAccounts.loadFromJson(gsonHelper, plugin.getDataFolder() + "/player-accounts.json", PlayerAccount.class)
                .thenAcceptAsync(count -> log.info(String.format("Loaded %d player account(s)...", count)))
                .thenRun(() -> islandAccounts.loadFromJson(gsonHelper, plugin.getDataFolder() + "/island-accounts.json", IslandAccount.class)
                        .thenAcceptAsync(count -> {
                            islandAccounts.getValues().forEach(IslandAccount::updateBalance);
                            log.info(String.format("Loaded %d island account(s)...", count));
                        }));
    }

    public void save() {
        islandAccounts.saveToJson(gsonHelper, plugin.getDataFolder() + "/island-accounts.json")
                .thenRun(() -> log.info(String.format("Saved %d island account(s)...", islandAccounts.size())));

        playerAccounts.saveToJson(gsonHelper, plugin.getDataFolder() + "/player-accounts.json")
                .thenRun(() -> log.info(String.format("Saved %d player account(s)...", playerAccounts.size())));
    }

    public IslandAccount getIslandAccount(UUID leaderUUID) {
        return islandAccounts.getOrCreate(leaderUUID);
    }

    @SuppressWarnings("deprecation")
    public Optional<IslandAccount> getIslandAccount(String name) {
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(name);
        UUID uniqueID = offlinePlayer.getUniqueId();
        return islandAccounts.get(a -> a.hasAccount(uniqueID));
    }

    public Optional<PlayerAccount> getPlayerAccount(String name) {
        return playerAccounts.get(a -> a.getNickname().equals(name));
    }

    @SuppressWarnings("deprecation")
    public Optional<PlayerAccount> getOrCreatePlayerAccount(String name) {
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(name);
        UUID uniqueID = offlinePlayer.getUniqueId();
        return Optional.of(playerAccounts.getOrCreate(uniqueID));
    }

    // Reset player's balance and update island account
    public boolean resetPlayer(String name) {
        Optional<PlayerAccount> playerAccount = playerAccounts.get(a -> a.getNickname().equals(name));

        if (playerAccount.isPresent()) {
            UUID uniqueID = playerAccount.get().getUniqueID();

            playerAccount.get().setBalance(plugin.getConfig().getDouble("default-balance", 0));
            islandAccounts.get(acc -> acc.hasAccount(uniqueID)).ifPresent(IslandAccount::updateBalance);
            return true;
        }
        return false;
    }
}
