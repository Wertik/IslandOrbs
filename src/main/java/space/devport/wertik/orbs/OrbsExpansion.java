package space.devport.wertik.orbs;

import com.bgsoftware.superiorskyblock.api.SuperiorSkyblockAPI;
import com.bgsoftware.superiorskyblock.api.island.Island;
import lombok.Getter;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import space.devport.utils.text.language.LanguageManager;
import space.devport.utils.utility.ParseUtil;
import space.devport.wertik.orbs.system.struct.IslandAccount;
import space.devport.wertik.orbs.system.struct.PlayerAccount;

import java.util.Optional;

public class OrbsExpansion extends PlaceholderExpansion {

    private final OrbsPlugin plugin;

    private final LanguageManager language;

    @Getter
    private final String placeholderIdentifier;

    public OrbsExpansion(OrbsPlugin plugin) {
        this.plugin = plugin;
        this.language = plugin.getManager(LanguageManager.class);
        this.placeholderIdentifier = plugin.getConfig().getString("placeholder-identifier", "islandorbs");
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public @NotNull String getIdentifier() {
        return placeholderIdentifier;
    }

    @Override
    public @NotNull String getAuthor() {
        return String.join(", ", plugin.getDescription().getAuthors());
    }

    @Override
    public @NotNull String getVersion() {
        return plugin.getDescription().getVersion();
    }

    /*
     * %islandorbs_top_<position>_<leader/island/amount>%
     * %islandorbs_position%
     * %islandorbs_balance%
     * %islandorbs_islandbalance%
     * */
    @Override
    public String onRequest(OfflinePlayer player, @NotNull String params) {

        if (player == null)
            return language.get("Placeholders.No-Player").color().toString();

        String[] arr = params.toLowerCase().split("_");

        if (arr.length < 1)
            return language.get("Placeholders.Not-Enough-Args").color().toString();

        switch (arr[0]) {
            case "balance":
                PlayerAccount playerAccount = plugin.getAccountManager().getPlayerAccounts().getOrCreate(player.getUniqueId());
                return plugin.format(playerAccount.getBalance());
            case "islandbalance":
                Optional<IslandAccount> islandAccount = plugin.getAccountManager().getIslandAccounts().get(a -> a.hasAccount(player.getUniqueId()));
                return islandAccount.map(account -> plugin.format(account.getBalance())).orElse(language.get("Placeholders.No-Island").color().toString());
            case "position":
                int position = plugin.getAccountManager().getTopCache().getPosition(player.getUniqueId());
                return position == -1 ? language.get("Placeholders.Not-Placed").color().toString() : String.valueOf(position + 1);
            case "top":
                if (arr.length < 3)
                    return language.get("Placeholders.Not-Enough-Args").color().toString();

                int pos = ParseUtil.parseInteger(arr[1], -1);

                if (pos <= 0)
                    return language.get("Placeholders.Invalid-Position").color().toString();

                IslandAccount account = plugin.getAccountManager().getTopCache().get(pos - 1);

                if (account == null)
                    return language.get("Placeholders.Not-Filled").color().toString();

                if (arr[2].equalsIgnoreCase("island")) {
                    Island island = SuperiorSkyblockAPI.getSuperiorSkyblock().getGrid().getIslandByUUID(account.getIslandUUID());
                    if (island == null)
                        return language.get("Placeholders.No-Island").color().toString();
                    return island.getName();
                } else if (arr[2].equalsIgnoreCase("balance"))
                    return plugin.format(account.getBalance());
                else if (arr[2].equalsIgnoreCase("leader")) {
                    Island island = SuperiorSkyblockAPI.getSuperiorSkyblock().getGrid().getIslandByUUID(account.getIslandUUID());
                    if (island == null)
                        return language.get("Placeholders.No-Island").color().toString();
                    return island.getOwner().getName();
                }
        }
        return language.get("Placeholders.Invalid-Params").color().toString();
    }
}
