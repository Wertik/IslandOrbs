package space.devport.wertik.orbs.system;

import lombok.extern.java.Log;
import org.jetbrains.annotations.Nullable;
import space.devport.utils.logging.DebugLevel;
import space.devport.utils.utility.ThreadUtil;
import space.devport.wertik.orbs.OrbsPlugin;
import space.devport.wertik.orbs.system.struct.Account;
import space.devport.wertik.orbs.system.struct.IslandAccount;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

@Log
public class TopCache {

    private final OrbsPlugin plugin;

    private final List<IslandAccount> cache = new LinkedList<>();

    private Thread refreshTask;

    public TopCache(OrbsPlugin plugin) {
        this.plugin = plugin;
    }

    public void stop() {
        if (refreshTask == null)
            return;

        refreshTask.interrupt();
        this.refreshTask = null;
    }

    public void start() {
        if (refreshTask != null)
            stop();

        this.refreshTask = ThreadUtil.createRepeatingTask(this::update, plugin.getConfig().getInt("top-cache.refresh", 10) * 1000L, "Top Refresher");
        refreshTask.start();
    }

    private void update() {
        List<IslandAccount> accounts = plugin.getAccountManager().getIslandAccounts().getValues();
        accounts.sort(Comparator.comparingDouble(Account::getBalance).reversed());

        cache.clear();
        cache.addAll(accounts);

        log.log(DebugLevel.DEBUG, "Updated top cache.");
    }

    @Nullable
    public IslandAccount get(int position) {
        return position >= cache.size() || position < 0 ? null : cache.get(position);
    }

    public int getPosition(UUID playerUUID) {
        for (int i = 0; i < cache.size(); i++) {
            IslandAccount islandAccount = cache.get(i);
            if (islandAccount.hasAccount(playerUUID))
                return i;
        }
        return -1;
    }
}
