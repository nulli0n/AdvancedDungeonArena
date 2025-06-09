package su.nightexpress.dungeons.api.criteria;

import org.jetbrains.annotations.NotNull;

public interface CriteriaValidator<T> {

    @NotNull T deserialize(@NotNull String string);

    @NotNull String serialize(@NotNull T value);
}
