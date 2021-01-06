package space.devport.wertik.orbs.system.struct;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.util.Objects;
import java.util.UUID;

public class PlayerAccount implements Account {

    @Getter
    private final UUID uniqueID;

    private double balance;

    private transient OfflinePlayer offlinePlayer;

    @Getter
    @Setter
    private transient IslandAccount parent;

    public PlayerAccount(UUID uniqueID) {
        this.uniqueID = uniqueID;
    }

    public PlayerAccount(UUID uniqueID, double balance) {
        this.uniqueID = uniqueID;
        this.balance = balance;
    }

    private void updateParent() {
        if (parent != null)
            parent.updateBalance();
    }

    public synchronized void setBalance(double value) {
        this.balance = Math.max(0, value);
        updateParent();
    }

    public synchronized double addBalance(double value) {
        this.balance += value;
        updateParent();
        return balance;
    }

    public synchronized double subtractBalance(double value) {
        this.balance = Math.max(0, balance - value);
        updateParent();
        return balance;
    }

    public OfflinePlayer getOfflinePlayer() {
        if (offlinePlayer == null)
            this.offlinePlayer = Bukkit.getOfflinePlayer(uniqueID);
        return offlinePlayer;
    }

    @Override
    public double getBalance() {
        return balance;
    }

    public String getNickname() {
        return getOfflinePlayer().getName();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PlayerAccount)) return false;
        PlayerAccount that = (PlayerAccount) o;
        return uniqueID.equals(that.uniqueID);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uniqueID);
    }
}
