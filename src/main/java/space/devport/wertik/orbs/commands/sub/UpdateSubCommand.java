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
import space.devport.wertik.orbs.system.struct.IslandAccount;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class UpdateSubCommand extends OrbSubCommand {

    public UpdateSubCommand(OrbsPlugin plugin) {
        super(plugin, "update");
        setAliases("refresh");
    }

    @Override
    protected @NotNull CommandResult perform(@NotNull CommandSender sender, @NotNull String label, String[] args) {

        if (args.length == 0) {
            plugin.getAccountManager().ensureIslands().thenAccept(count ->
                    plugin.getAccountManager().updateBalances().thenRun(() ->
                            language.getPrefixed("Commands.Update.Done-All")
                                    .replace("%count%", count)
                                    .send(sender)));
            return CommandResult.SUCCESS;
        }

        Optional<IslandAccount> islandAccount = plugin.getAccountManager().getIslandAccount(args[0]);

        if (!islandAccount.isPresent()) {
            language.getPrefixed("Commands.No-Island")
                    .replace("%player%", args[0])
                    .send(sender);
            return CommandResult.FAILURE;
        }

        islandAccount.get().updateBalance();
        language.getPrefixed("Commands.Update.Done")
                .replace("%player%", args[0])
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
        return "/%label% update (player)";
    }

    @Override
    public @Nullable String getDefaultDescription() {
        return "Update island balance manually.";
    }

    @Override
    public @Nullable ArgumentRange getRange() {
        return new ArgumentRange(0, 1);
    }
}
