package space.devport.wertik.orbs;

import lombok.Getter;
import lombok.extern.java.Log;
import org.bukkit.ChatColor;
import org.bukkit.plugin.Plugin;
import space.devport.utils.DevportPlugin;
import space.devport.utils.UsageFlag;
import space.devport.utils.utility.VersionUtil;
import space.devport.wertik.orbs.commands.OrbCommand;
import space.devport.wertik.orbs.listeners.IslandListener;
import space.devport.wertik.orbs.system.AccountManager;

import java.text.DecimalFormat;
import java.text.NumberFormat;

@Log
public class OrbsPlugin extends DevportPlugin {

    //TODO Code a better hooking system for SuperiorSkyblock

    @Getter
    private AccountManager accountManager;

    private OrbsExpansion expansion;

    @Getter
    private NumberFormat numberFormat;

    @Override
    public void onPluginEnable() {

        loadOptions();

        this.accountManager = new AccountManager(this);
        accountManager.load();

        registerMainCommand(new OrbCommand(this));

        new OrbsLanguage(this).register();

        registerListener(new IslandListener(this));

        accountManager.getTopCache().start();

        registerExpansion();
    }

    private void loadOptions() {
        this.numberFormat = new DecimalFormat(configuration.getString("number-format", "#.#"));
    }

    @Override
    public void onPluginDisable() {
        accountManager.getTopCache().stop();
        accountManager.save();

        unregisterExpansion();
    }

    @Override
    public void onReload() {
        accountManager.getTopCache().start();
        loadOptions();
    }

    @Override
    public UsageFlag[] usageFlags() {
        return new UsageFlag[]{UsageFlag.COMMANDS, UsageFlag.LANGUAGE, UsageFlag.CONFIGURATION};
    }

    @Override
    public ChatColor getPluginColor() {
        return ChatColor.AQUA;
    }

    private void unregisterExpansion() {
        Plugin plugin = getPluginManager().getPlugin("PlaceholderAPI");
        if (plugin != null && plugin.isEnabled() &&
                VersionUtil.compareVersions(plugin.getDescription().getVersion(), "2.10.9") > 0 &&
                expansion != null && expansion.isRegistered()) {

            expansion.unregister();
            this.expansion = null;
            log.info("Unregistered placeholder expansion.");
        }
    }

    private void registerExpansion() {
        if (expansion != null)
            unregisterExpansion();

        this.expansion = new OrbsExpansion(this);
        expansion.register();
        log.info("Registered placeholder expansion.");
    }

    public String format(double value) {
        return numberFormat.format(value);
    }
}
