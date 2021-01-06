package space.devport.wertik.orbs.commands;

import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.Nullable;
import space.devport.utils.commands.SubCommand;
import space.devport.utils.commands.struct.ArgumentRange;
import space.devport.utils.commands.struct.CommandResult;
import space.devport.utils.text.StringUtil;
import space.devport.wertik.orbs.OrbsPlugin;
import space.devport.wertik.orbs.system.struct.PlayerAccount;

import java.util.Optional;
import java.util.function.BiConsumer;

public abstract class OrbSubCommand extends SubCommand {

    protected final OrbsPlugin plugin;

    public OrbSubCommand(OrbsPlugin plugin, String name) {
        super(plugin, name);
        this.plugin = plugin;
        setPermissions();
    }

    protected CommandResult modifyPlayerBalance(CommandSender sender, String[] args, BiConsumer<PlayerAccount, Double> action) {
        Optional<PlayerAccount> playerAccount = plugin.getAccountManager().getOrCreatePlayerAccount(args[0]);

        if (!playerAccount.isPresent()) {
            language.getPrefixed("Commands.Invalid-Player")
                    .replace("%param%", args[0])
                    .send(sender);
            return CommandResult.FAILURE;
        }

        Double value = parse(sender, args[1], Double::parseDouble, "Commands.Not-A-Number");
        if (value == null)
            return CommandResult.FAILURE;

        action.accept(playerAccount.get(), value);
        return CommandResult.SUCCESS;
    }

    @Override
    public abstract @Nullable String getDefaultUsage();

    @Override
    public abstract @Nullable String getDefaultDescription();

    @Override
    public abstract @Nullable ArgumentRange getRange();
}
