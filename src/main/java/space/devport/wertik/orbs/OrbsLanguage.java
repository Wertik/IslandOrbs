package space.devport.wertik.orbs;

import space.devport.utils.DevportPlugin;
import space.devport.utils.text.language.LanguageDefaults;

public class OrbsLanguage extends LanguageDefaults {

    public OrbsLanguage(DevportPlugin plugin) {
        super(plugin);
    }

    @Override
    public void setDefaults() {
        addDefault("Commands.Invalid-Player", "&cPlayer &f%param% &cdoes not exist.");
        addDefault("Commands.No-Record", "&cPlayer &f%param% &chas no record.");
        addDefault("Commands.Not-A-Number", "&cParam &f%param% &cis not a number.");
        addDefault("Commands.No-Island", "&cPlayer &f%player% &chas no island.");

        addDefault("Commands.Give.Done", "&7Added &f%amount% &7to &f%player%&7's balance. New balance: &f%balance%");

        addDefault("Commands.Update.Done", "&7Update balance for island of &f%player%");

        addDefault("Commands.Take.Done", "&7Subtracted &f%amount% &7from &f%player%&7's balance. New Balance: &f%balance%");

        addDefault("Commands.Set.Done", "&7Set &f%player%&7's balance to &f%balance%");

        addDefault("Commands.Reset.Done", "&7Reset &f%player%&7's balance from &f%oldBalance% &7to &f%balance%&7.");
        addDefault("Commands.Reset.Done-All", "&7Reset &f%count% &7accounts.");

        addDefault("Commands.Balance.Done", "&7Balance of &f%player%: &f%balance% &7( island: &e%islandBalance% &7)");
        addDefault("Commands.Balance.Done-Me", "&7Your balance: &f%balance% &7( island: &e%islandBalance% &7)");
        addDefault("Commands.Balance.You", "&6You");
        addDefault("Commands.Balance.None", "&c0");

        addDefault("Placeholders.No-Player", "no_player");
        addDefault("Placeholders.Not-Enough-Args", "not_enough_args");
        addDefault("Placeholders.Invalid-Position", "invalid_position");
        addDefault("Placeholders.Not-Filled", "not_filled");
        addDefault("Placeholders.No-Island", "no_island");
        addDefault("Placeholders.Invalid-Params", "invalid_params");
        addDefault("Placeholders.Not-Placed", "not_placed");
        addDefault("Placeholders.Member-Line", "&8 - &7%nick% (&f%balance%&7)");
    }
}
