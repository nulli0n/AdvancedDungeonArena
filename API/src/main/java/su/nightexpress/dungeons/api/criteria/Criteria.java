package su.nightexpress.dungeons.api.criteria;

import org.jetbrains.annotations.NotNull;

public interface Criteria<T, E> {

    @NotNull CriteriaValidator<T> getValidator();

    @NotNull String getName();

    /**
     * Validates (deserializes) the provided string and wraps it to criteria predicate.
     * @param string String to validate (deserialize) for predicate.
     * @return A wrapped Predicate
     */
    @NotNull CriteriaPredicate<T, E> validate(@NotNull String string);

    @NotNull CriteriaPredicate<T, E> predicate(@NotNull T value);

    boolean test(@NotNull E entity, @NotNull T value);
}
