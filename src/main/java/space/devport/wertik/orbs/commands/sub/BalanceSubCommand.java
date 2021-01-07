package space.devport.wertik.orbs.commands.sub;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import space.devport.utils.commands.struct.ArgumentRange;
import space.devport.utils.commands.struct.CommandResult;
import space.devport.wertik.orbs.OrbsPlugin;
import space.devport.wertik.orbs.commands.OrbSubCommand;
import space.devport.wertik.orbs.system.struct.IslandAccount;
import space.devport.wertik.orbs.system.struct.PlayerAccount;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class BalanceSubCommand extends OrbSubCommand {

    public BalanceSubCommand(OrbsPlugin plugin) {
        super(plugin, "balance");
        setAliases("bal", "money");
    }

    @Override
    protected @NotNull CommandResult perform(@NotNull CommandSender sender, @NotNull String label, String[] args) {

        boolean me = false;

        String target;
        if (args.length < 1) {
            if (!(sender instanceof Player))
                return CommandResult.NO_CONSOLE;

            target = ((OfflinePlayer) sender).getName();
            me = true;
        } else {
            if (!sender.hasPermission(String.format("%s.others", craftPermission())))
                return CommandResult.NO_PERMISSION;

            target = args[0];
        }

        Optional<PlayerAccount> playerAccount = plugin.getAccountManager().getOrCreatePlayerAccount(target);

        if (!playerAccount.isPresent()) {
            language.getPrefixed("Commands.No-Record")
                    .replace("%param%", target)
                    .send(sender);
            return CommandResult.FAILURE;
        }

        Optional<IslandAccount> islandAccount = plugin.getAccountManager().getIslandAccount(target);

        language.getPrefixed(me ? "Commands.Balance.Done-Me" : "Commands.Balance.Done")
                .replace("%balance%", plugin.format(playerAccount.get().getBalance()))
                .replace("%islandBalance%", islandAccount.isPresent() ?
                        plugin.format(islandAccount.get().getBalance()) : language.get("Commands.Balance.None").toString())
                .replace("%player%", me ? language.get("Commands.Balance.You").toString() : target)
                .send(sender);
        return CommandResult.SUCCESS;
    }

    @Override
    public @NotNull List<String> requestTabComplete(CommandSender sender, String[] args) {
        if (args.length == 1) {
            return Bukkit.getOnlinePlayers().stream().map(OfflinePlayer::getName).collect(Collectors.toList());
        }
        return new ArrayList<>();
    }

    @Override
    public @Nullable String getDefaultUsage() {
        return "/%label% balance (player)";
    }

    @Override
    public @Nullable String getDefaultDescription() {
        return "View yours (or other's) balance.";
    }

    @Override
    public @Nullable ArgumentRange getRange() {
        return new ArgumentRange(0, 1);
    }
}
