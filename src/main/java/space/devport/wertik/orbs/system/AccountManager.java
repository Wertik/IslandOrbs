package space.devport.wertik.orbs.system;

import com.bgsoftware.superiorskyblock.api.SuperiorSkyblockAPI;
import com.bgsoftware.superiorskyblock.api.island.Island;
import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;
import lombok.Getter;
import lombok.extern.java.Log;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import space.devport.utils.utility.ThreadUtil;
import space.devport.utils.utility.json.GsonHelper;
import space.devport.wertik.orbs.OrbsPlugin;
import space.devport.wertik.orbs.system.struct.IslandAccount;
import space.devport.wertik.orbs.system.struct.PlayerAccount;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Log
public class AccountManager {

    private final OrbsPlugin plugin;

    private final GsonHelper gsonHelper = new GsonHelper();

    @Getter
    private final AccountCache<IslandAccount> islandAccounts;
    @Getter
    private final AccountCache<PlayerAccount> playerAccounts;

    @Getter
    private final TopCache topCache;

    private Thread saveTask;

    public AccountManager(OrbsPlugin plugin) {
        this.plugin = plugin;

        this.playerAccounts = new AccountCache<>(uniqueID -> new PlayerAccount(uniqueID, plugin.getConfig().getInt("default-balance", 0)));

        this.islandAccounts = new AccountCache<>(IslandAccount::new);

        this.topCache = new TopCache(plugin);
    }

    public void stopAutoSave() {
        if (saveTask == null)
            return;

        saveTask.interrupt();
        this.saveTask = null;
    }

    public void startAutoSave() {
        stopAutoSave();

        if (!plugin.getConfig().getBoolean("auto-save.enabled", false))
            return;

        this.saveTask = ThreadUtil.createRepeatingTask(this::save, plugin.getConfig().getInt("auto-save.interval", 300) * 1000L);
        saveTask.start();
    }

    public void load() {
        playerAccounts.loadFromJson(gsonHelper, plugin.getDataFolder() + "/player-accounts.json", PlayerAccount.class)
                .thenAcceptAsync(count -> log.info(String.format("Loaded %d player account(s)...", count)))
                .thenRun(() -> islandAccounts.loadFromJson(gsonHelper, plugin.getDataFolder() + "/island-accounts.json", IslandAccount.class)
                        .thenAcceptAsync(count -> {
                            islandAccounts.getValues().forEach(IslandAccount::updateBalance);
                            log.info(String.format("Loaded %d island account(s)...", count));
                        })
                        // Update islands
                        .thenRun(() -> ensureIslands().thenRun(this::updateBalances)));
    }

    public void save() {
        islandAccounts.saveToJson(gsonHelper, plugin.getDataFolder() + "/island-accounts.json")
                .thenRun(() -> log.info(String.format("Saved %d island account(s)...", islandAccounts.size())));

        playerAccounts.saveToJson(gsonHelper, plugin.getDataFolder() + "/player-accounts.json")
                .thenRun(() -> log.info(String.format("Saved %d player account(s)...", playerAccounts.size())));
    }

    public IslandAccount getIslandAccount(UUID islandUUID) {
        return islandAccounts.getOrCreate(islandUUID);
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

    public Optional<IslandAccount> getOrCreateIslandAccount(UUID playerUUID) {
        return plugin.getAccountManager().getIslandAccounts().getOrCreate(a -> a.hasAccount(playerUUID),
                () -> {
                    Island island = SuperiorSkyblockAPI.getPlayer(playerUUID).getIsland();

                    if (island == null)
                        return null;

                    IslandAccount account = new IslandAccount(island.getUniqueId());
                    account.addAccount(plugin.getAccountManager().getPlayerAccounts().getOrCreate(playerUUID));
                    return account;
                });
    }

    public int deletePlayers() {
        playerAccounts.forEach(PlayerAccount::delete);
        return playerAccounts.empty();
    }

    // Delete player.
    public boolean deletePlayer(String name) {
        Optional<PlayerAccount> playerAccount = playerAccounts.get(a -> a.getNickname().equals(name));

        if (playerAccount.isPresent()) {
            playerAccount.get().delete();
            playerAccounts.remove(playerAccount.get().getUniqueID());
            return true;
        }
        return false;
    }

    public CompletableFuture<Void> updateBalances() {
        return islandAccounts.forEach(IslandAccount::updateBalance);
    }

    public CompletableFuture<Integer> ensureIslands() {
        return CompletableFuture.supplyAsync(() -> {
            int count = 0;
            for (Island island : SuperiorSkyblockAPI.getSuperiorSkyblock().getGrid().getIslands()) {
                UUID islandUUID = island.getUniqueId();

                IslandAccount account = islandAccounts.getOrCreate(islandUUID);

                island.getIslandMembers(true).stream()
                        .map(SuperiorPlayer::getUniqueId)
                        .forEach(u -> account.addAccount(playerAccounts.getOrCreate(u)));
                count++;
            }
            return count;
        });
    }
}
