package su.nightexpress.dungeons.selection.visual;

import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class FakeEntity {

    private final int  id;
    private final UUID uuid;

    public FakeEntity(int id, @NotNull UUID uuid) {
        this.id = id;
        this.uuid = uuid;
    }

    public int getId() {
        return id;
    }

    @NotNull
    public UUID getUUID() {
        return uuid;
    }
}
