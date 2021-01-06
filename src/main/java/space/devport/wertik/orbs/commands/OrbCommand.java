package space.devport.wertik.orbs.commands;

import org.jetbrains.annotations.Nullable;
import space.devport.utils.commands.MainCommand;
import space.devport.utils.commands.build.BuildableSubCommand;
import space.devport.utils.commands.struct.CommandResult;
import space.devport.wertik.orbs.OrbsPlugin;
import space.devport.wertik.orbs.commands.sub.*;

public class OrbCommand extends MainCommand {

    public OrbCommand(OrbsPlugin plugin) {
        super(plugin, "islandorbs");

        withSubCommand(new BuildableSubCommand(plugin, "reload")
                .withDefaultDescription("Reload the plugin.")
                .withExecutor((sender, label, args) -> {
                    plugin.reload(sender);
                    return CommandResult.SUCCESS;
                }));

        withSubCommand(new GiveSubCommand(plugin));
        withSubCommand(new TakeSubCommand(plugin));
        withSubCommand(new ResetSubCommand(plugin));
        withSubCommand(new SetSubCommand(plugin));
        withSubCommand(new UpdateSubCommand(plugin));
        withSubCommand(new BalanceSubCommand(plugin));
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
