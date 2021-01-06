package space.devport.wertik.orbs.system.struct;

import com.google.gson.annotations.JsonAdapter;
import lombok.Getter;
import lombok.extern.java.Log;
import space.devport.utils.logging.DebugLevel;
import space.devport.wertik.orbs.system.json.IslandAccountJsonAdapter;

import java.util.*;
import java.util.stream.Collectors;

@Log
@JsonAdapter(IslandAccountJsonAdapter.class)
public class IslandAccount implements Account {

    @Getter
    private final UUID islandUUID;

    private final Set<PlayerAccount> playerAccounts = new HashSet<>();

    private transient double balance;

    public IslandAccount(UUID islandUUID) {
        this.islandUUID = islandUUID;
    }

    public void addAccount(PlayerAccount account) {
        addAccount(account, true);
    }

    public void addAccount(PlayerAccount account, boolean update) {
        if (hasAccount(account.getUniqueID()))
            return;

        this.playerAccounts.add(account);
        account.setParent(this);
        log.log(DebugLevel.DEBUG, "Added player " + account.getUniqueID().toString() + " to " + islandUUID.toString());

        if (!update) return;

        updateBalance();
        log.log(DebugLevel.DEBUG, "Updated balance to " + balance);
    }

    public void removeAccount(UUID uniqueID) {
        removeAccount(new PlayerAccount(uniqueID));
    }

    public void removeAccount(PlayerAccount account) {
        if (!this.playerAccounts.remove(account))
            return;

        account.setParent(null);
        updateBalance();
        log.log(DebugLevel.DEBUG, "Removed player " + account.getUniqueID().toString() + " from " + islandUUID.toString() + "; Updated balance to " + getBalance());
    }

    public boolean hasAccount(UUID uniqueID) {
        return playerAccounts.contains(new PlayerAccount(uniqueID));
    }

    public void updateBalance() {
        double newValue = 0;
        for (PlayerAccount playerAccount : playerAccounts) {
            newValue += playerAccount.getBalance();
        }
        this.balance = newValue;
    }

    public Set<UUID> getAccountIds() {
        return this.playerAccounts.stream()
                .map(PlayerAccount::getUniqueID)
                .collect(Collectors.toSet());
    }

    private synchronized double addBalance(double value) {
        return this.balance += value;
    }

    private synchronized double subtractBalance(double value) {
        return this.balance -= value;
    }

    @Override
    public double getBalance() {
        return balance;
    }

    public Set<PlayerAccount> getPlayerAccounts() {
        return Collections.unmodifiableSet(playerAccounts);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof IslandAccount)) return false;
        IslandAccount that = (IslandAccount) o;
        return islandUUID.equals(that.islandUUID);
    }

    @Override
    public int hashCode() {
        return Objects.hash(islandUUID);
    }
}
