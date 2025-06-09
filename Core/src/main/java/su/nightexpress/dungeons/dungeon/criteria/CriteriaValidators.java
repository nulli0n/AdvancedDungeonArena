package su.nightexpress.dungeons.dungeon.criteria;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.dungeons.api.criteria.CriteriaValidator;
import su.nightexpress.nightcore.util.Enums;

import java.util.function.Function;

public class CriteriaValidators {

    public static final CriteriaValidator<Boolean> BOOLEAN = create(Boolean::parseBoolean, Object::toString);

    public static final CriteriaValidator<String> STRING = create(string -> string, string -> string);

    public static <E extends Enum<E>> CriteriaValidator<E> forEnum(@NotNull Class<E> clazz) {
        return create(string -> Enums.parse(string, clazz).orElseThrow(), Enum::name);
    }

    @NotNull
    private static <T> CriteriaValidator<T> create(@NotNull Function<String, T> deserializer, @NotNull Function<T, String> serializer) {
        return new CriteriaValidator<>() {
            @NotNull
            @Override
            public T deserialize(@NotNull String string) {
                return deserializer.apply(string);
            }

            @NotNull
            @Override
            public String serialize(@NotNull T value) {
                return serializer.apply(value);
            }
        };
    }
}
