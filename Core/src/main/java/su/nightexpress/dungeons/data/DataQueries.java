package su.nightexpress.dungeons.data;

import com.google.gson.reflect.TypeToken;
import su.nightexpress.dungeons.user.DungeonUser;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;

public class DataQueries {

    public static final Function<ResultSet, DungeonUser> USER_LOADER = resultSet -> {
        try {
            UUID uuid = UUID.fromString(resultSet.getString(DataHandler.COLUMN_USER_ID.getName()));
            String name = resultSet.getString(DataHandler.COLUMN_USER_NAME.getName());
            long dateCreated = resultSet.getLong(DataHandler.COLUMN_USER_DATE_CREATED.getName());
            long lastOnline = resultSet.getLong(DataHandler.COLUMN_USER_LAST_ONLINE.getName());

            Set<String> kits = DataHandler.GSON.fromJson(resultSet.getString(DataHandler.COLUMN_KITS.getName()), new TypeToken<Set<String>>() {}.getType());
            Map<String, Long> cooldownMap = DataHandler.GSON.fromJson(resultSet.getString(DataHandler.COLUMN_COOLDOWN.getName()), new TypeToken<Map<String, Long>>() {}.getType());
            if (cooldownMap == null) cooldownMap = new HashMap<>();

            return new DungeonUser(uuid, name, dateCreated, lastOnline, kits, cooldownMap);
        }
        catch (SQLException exception) {
            exception.printStackTrace();
            return null;
        }
    };
}
