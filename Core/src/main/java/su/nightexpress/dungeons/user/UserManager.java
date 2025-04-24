package su.nightexpress.dungeons.user;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.dungeons.DungeonPlugin;
import su.nightexpress.dungeons.data.DataHandler;
import su.nightexpress.nightcore.db.AbstractUserManager;

import java.util.UUID;

public class UserManager extends AbstractUserManager<DungeonPlugin, DungeonUser> {

    public UserManager(@NotNull DungeonPlugin plugin, @NotNull DataHandler dataHandler) {
        super(plugin, dataHandler);
    }

    @Override
    @NotNull
    public DungeonUser create(@NotNull UUID uuid, @NotNull String name) {
        return DungeonUser.create(uuid, name);
    }
}
