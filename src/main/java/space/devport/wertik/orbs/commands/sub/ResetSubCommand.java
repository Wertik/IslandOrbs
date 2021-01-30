package space.devport.wertik.orbs.commands.sub;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import space.devport.utils.commands.struct.ArgumentRange;
import space.devport.utils.commands.struct.CommandResult;
import space.devport.wertik.orbs.OrbsPlugin;
import space.devport.wertik.orbs.commands.OrbSubCommand;
import space.devport.wertik.orbs.system.struct.PlayerAccount;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ResetSubCommand extends OrbSubCommand {

    public ResetSubCommand(OrbsPlugin plugin) {
        super(plugin, "reset");
        setAliases("delete", "remove");
    }

    @Override
    protected @NotNull CommandResult perform(@NotNull CommandSender sender, @NotNull String label, String[] args) {

        if (args.length == 0) {
            int count = plugin.getAccountManager().resetPlayers();
            language.getPrefixed("Commands.Reset.Done-All")
                    .replace("%count%", count)
                    .send(sender);
            return CommandResult.SUCCESS;
        }

        Optional<PlayerAccount> playerAccount = plugin.getAccountManager().getPlayerAccount(args[0]);

        if (!playerAccount.isPresent()) {
            language.getPrefixed("Commands.No-Record")
                    .replace("%param%", args[0])
                    .send(sender);
            return CommandResult.FAILURE;
        }

        double oldBalance = playerAccount.get().getBalance();

        if (!plugin.getAccountManager().resetPlayer(args[0])) {
            language.getPrefixed("Commands.Invalid-Player")
                    .replace("%param%", args[0])
                    .send(sender);
            return CommandResult.FAILURE;
        }

        language.getPrefixed("Commands.Reset.Done")
                .replace("%player%", args[0])
                .replace("%balance%", plugin.format(playerAccount.get().getBalance()))
                .replace("%oldBalance%", plugin.format(oldBalance))
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
        return "/%label% reset (player)";
    }

    @Override
    public @Nullable String getDefaultDescription() {
        return "Resets balance..";
    }

    @Override
    public @Nullable ArgumentRange getRange() {
        return new ArgumentRange(0, 1);
    }
}
