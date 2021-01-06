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

public class TakeSubCommand extends OrbSubCommand {

    public TakeSubCommand(OrbsPlugin plugin) {
        super(plugin, "take");
        setAliases("sub", "remove", "subtract");
    }

    @Override
    protected @NotNull CommandResult perform(@NotNull CommandSender sender, @NotNull String label, String[] args) {
        return modifyPlayerBalance(sender, args, (playerAccount, value) -> {
            double balance = playerAccount.subtractBalance(value);
            language.getPrefixed("Commands.Take.Done")
                    .replace("%player%", args[0])
                    .replace("%amount%", plugin.format(value))
                    .replace("%balance%", plugin.format(balance))
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
        return "/%label% take <player> <amount>";
    }

    @Override
    public @Nullable String getDefaultDescription() {
        return "Take balance from player.";
    }

    @Override
    public @Nullable ArgumentRange getRange() {
        return new ArgumentRange(2);
    }
}
