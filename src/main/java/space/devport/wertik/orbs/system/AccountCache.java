package space.devport.wertik.orbs.system;

import lombok.extern.java.Log;
import org.jetbrains.annotations.NotNull;
import space.devport.utils.logging.DebugLevel;
import space.devport.utils.utility.json.GsonHelper;
import space.devport.wertik.orbs.system.struct.Account;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;

@Log
public class AccountCache<T extends Account> {

    private final Map<UUID, T> cache = new ConcurrentHashMap<>();

    private final Function<UUID, T> instanceProvider;

    public AccountCache(Function<UUID, T> instanceProvider) {
        this.instanceProvider = instanceProvider;
    }

    public int size() {
        return cache.size();
    }

    public T get(UUID uniqueID) {
        return cache.get(uniqueID);
    }

    public Optional<T> get(Predicate<T> condition) {
        for (T t : cache.values()) {
            if (condition.test(t)) {
                return Optional.of(t);
            }
        }
        return Optional.empty();
    }

    public boolean has(UUID uniqueID) {
        return cache.containsKey(uniqueID);
    }

    @NotNull
    public T getOrCreate(UUID uniqueID) {
        return has(uniqueID) ? get(uniqueID) : create(uniqueID);
    }

    @NotNull
    public T create(UUID uniqueID) {
        T obj = instanceProvider.apply(uniqueID);
        cache.put(uniqueID, obj);
        log.log(DebugLevel.DEBUG, "Created account " + uniqueID.toString());
        return obj;
    }

    public void remove(UUID uniqueID) {
        cache.remove(uniqueID);
        log.log(DebugLevel.DEBUG, "Removed account " + uniqueID.toString());
    }

    public CompletableFuture<Integer> loadFromJson(GsonHelper gsonHelper, String path, Class<T> clazz) {
        return gsonHelper.loadMapAsync(path, UUID.class, clazz).thenApplyAsync(loaded -> {
            if (loaded == null)
                loaded = new HashMap<>();

            cache.clear();
            cache.putAll(loaded);
            return cache.size();
        }).exceptionally(e -> {
            log.severe("Could not load accounts.");
            e.printStackTrace();
            return null;
        });
    }

    public CompletableFuture<Void> saveToJson(GsonHelper gsonHelper, String path) {
        return gsonHelper.save(cache, path);
    }

    public List<T> getValues() {
        return new ArrayList<>(cache.values());
    }
}
