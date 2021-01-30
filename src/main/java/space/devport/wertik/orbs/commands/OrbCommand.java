package space.devport.wertik.orbs.commands;

import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import space.devport.utils.commands.MainCommand;
import space.devport.utils.commands.build.BuildableSubCommand;
import space.devport.utils.commands.struct.CommandResult;
import space.devport.wertik.orbs.OrbsPlugin;
import space.devport.wertik.orbs.commands.sub.*;

public class OrbCommand extends MainCommand {

    private final BalanceSubCommand balanceSubCommand;

    public OrbCommand(OrbsPlugin plugin) {
        super(plugin, "islandorbs");

        withSubCommand(new BuildableSubCommand(plugin, "reload")
                .withDefaultDescription("Reloads the plugin.")
                .withRange(0)
                .withExecutor((sender, label, args) -> {
                    plugin.reload(sender);
                    return CommandResult.SUCCESS;
                }));

        withSubCommand(new GiveSubCommand(plugin));
        withSubCommand(new TakeSubCommand(plugin));
        withSubCommand(new ResetSubCommand(plugin));
        withSubCommand(new SetSubCommand(plugin));
        withSubCommand(new UpdateSubCommand(plugin));

        this.balanceSubCommand = new BalanceSubCommand(plugin);
        withSubCommand(balanceSubCommand);
    }

    @Override
    protected @NotNull CommandResult perform(@NotNull CommandSender sender, @NotNull String label, String[] args) {
        if (args.length == 0) {
            balanceSubCommand.runCommand(sender, label, args);
            return CommandResult.SUCCESS;
        }
        return super.perform(sender, label, args);
    }

    @Override
    public @Nullable String getDefaultUsage() {
        return "/%label%";
    }

    @Override
    public @Nullable String getDefaultDescription() {
        return "Displays this.";
    }
}
