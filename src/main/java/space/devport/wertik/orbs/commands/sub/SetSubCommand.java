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

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SetSubCommand extends OrbSubCommand {

    public SetSubCommand(OrbsPlugin plugin) {
        super(plugin, "set");
    }

    @Override
    protected @NotNull CommandResult perform(@NotNull CommandSender sender, @NotNull String label, String[] args) {
        return modifyPlayerBalance(sender, args, (playerAccount, value) -> {
            playerAccount.setBalance(value);
            language.getPrefixed("Commands.Set.Done")
                    .replace("%player%", args[0])
                    .replace("%balance%", plugin.format(playerAccount.getBalance()))
                    .send(sender);
        });
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
        return "/%label% set <player> <amount>";
    }

    @Override
    public @Nullable String getDefaultDescription() {
        return "Set player's balance.";
    }

    @Override
    public @Nullable ArgumentRange getRange() {
        return new ArgumentRange(2);
    }
}
