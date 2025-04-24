package su.nightexpress.dungeons.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.dungeons.DungeonPlugin;
import su.nightexpress.dungeons.user.DungeonUser;
import su.nightexpress.nightcore.db.AbstractUserDataManager;
import su.nightexpress.nightcore.db.sql.column.Column;
import su.nightexpress.nightcore.db.sql.column.ColumnType;
import su.nightexpress.nightcore.db.sql.query.impl.SelectQuery;
import su.nightexpress.nightcore.db.sql.query.type.ValuedQuery;

import java.sql.ResultSet;
import java.util.List;
import java.util.function.Function;

public class DataHandler extends AbstractUserDataManager<DungeonPlugin, DungeonUser> {

    public static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public static final Column COLUMN_KITS     = Column.of("kits", ColumnType.STRING);
    public static final Column COLUMN_COOLDOWN = Column.of("cooldown", ColumnType.STRING);

    public DataHandler(@NotNull DungeonPlugin plugin) {
        super(plugin);
    }

    @Override
    public void onSynchronize() {
        this.plugin.getUserManager().getLoaded().forEach(user -> {
            if (!user.isAutoSyncReady() || user.isAutoSavePlanned()) return;

            DungeonUser fetch = this.getUser(user.getId());
            if (fetch == null) return;

            fetch.getCooldownMap().clear();
            fetch.getCooldownMap().putAll(user.getCooldownMap());
        });
    }

    @Override
    @NotNull
    protected GsonBuilder registerAdapters(@NotNull GsonBuilder builder) {
        return builder;
    }

    @Override
    @NotNull
    protected Function<ResultSet, DungeonUser> createUserFunction() {
        return DataQueries.USER_LOADER;
    }

    @Override
    protected void addSelectQueryData(@NotNull SelectQuery<DungeonUser> query) {
        query.column(COLUMN_KITS);
        query.column(COLUMN_COOLDOWN);
    }

    @Override
    protected void addUpsertQueryData(@NotNull ValuedQuery<?, DungeonUser> query) {
        query.setValue(COLUMN_KITS, user -> GSON.toJson(user.getPurchasedKits()));
        query.setValue(COLUMN_COOLDOWN, user -> GSON.toJson(user.getCooldownMap()));
    }

    @Override
    protected void addTableColumns(@NotNull List<Column> columns) {
        columns.add(COLUMN_KITS);
        columns.add(COLUMN_COOLDOWN);
    }
}
