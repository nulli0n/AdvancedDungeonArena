package su.nightexpress.dungeons.api.mob;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.config.ConfigValue;
import su.nightexpress.nightcore.config.FileConfig;
import su.nightexpress.nightcore.config.Writeable;

import java.util.Objects;

public class MobIdentifier implements Writeable {

    private static final String DELIMITER = ":";

    private final String providerId;
    private final String mobId;

    public MobIdentifier(@NotNull String providerId, @NotNull String mobId) {
        this.providerId = providerId;
        this.mobId = mobId;
    }

    @NotNull
    public static MobIdentifier from(@NotNull MobProvider provider, @NotNull String mobId) {
        return new MobIdentifier(provider.getName(), mobId);
    }

    @NotNull
    public static MobIdentifier read(@NotNull FileConfig config, @NotNull String path) {
        String string = ConfigValue.create(path, "null" + DELIMITER + "null").read(config);
        return deserialize(string);
    }

    @Override
    public void write(@NotNull FileConfig config, @NotNull String path) {
        config.set(path, this.serialize());
    }

    @NotNull
    public static MobIdentifier deserialize(@NotNull String string) {
        String[] split = string.split(DELIMITER);
        if (split.length < 2) throw new IllegalStateException("String " + string + " does not have required params!");

        return new MobIdentifier(split[0], split[1]);
    }

    @NotNull
    public String serialize() {
        return this.providerId + DELIMITER + this.mobId;
    }

    @NotNull
    public String getProviderId() {
        return this.providerId;
    }

    @NotNull
    public String getMobId() {
        return this.mobId;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (!(object instanceof MobIdentifier that)) return false;
        return Objects.equals(providerId, that.providerId) && Objects.equals(mobId, that.mobId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(providerId, mobId);
    }
}
