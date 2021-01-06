package space.devport.wertik.orbs.system.json;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import space.devport.wertik.orbs.OrbsPlugin;
import space.devport.wertik.orbs.system.struct.IslandAccount;
import space.devport.wertik.orbs.system.struct.PlayerAccount;

import java.lang.reflect.Type;
import java.util.Set;
import java.util.UUID;

public class IslandAccountJsonAdapter implements JsonSerializer<IslandAccount>, JsonDeserializer<IslandAccount> {

    @Override
    public IslandAccount deserialize(JsonElement json, Type type, JsonDeserializationContext context) throws JsonParseException {

        if (!json.isJsonObject())
            return null;

        JsonObject jsonObject = json.getAsJsonObject();
        UUID islandUUID = context.deserialize(jsonObject.get("islandUUID"), new TypeToken<UUID>() {
        }.getType());

        if (islandUUID == null)
            throw new JsonParseException("Island UUID cannot be null");

        IslandAccount islandAccount = new IslandAccount(islandUUID);

        Set<UUID> uuids = context.deserialize(jsonObject.get("members"), new TypeToken<Set<UUID>>() {
        }.getType());

        OrbsPlugin plugin = OrbsPlugin.getPlugin(OrbsPlugin.class);

        for (UUID uuid : uuids) {
            PlayerAccount playerAccount = plugin.getAccountManager().getPlayerAccounts().get(uuid);
            islandAccount.addAccount(playerAccount, false);
        }

        return islandAccount;
    }

    @Override
    public JsonElement serialize(IslandAccount account, Type type, JsonSerializationContext context) {
        JsonObject json = new JsonObject();
        Set<UUID> uuids = account.getAccountIds();

        json.add("islandUUID", context.serialize(account.getIslandUUID()));
        json.add("members", context.serialize(uuids));

        return json;
    }
}
