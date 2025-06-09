package su.nightexpress.dungeons.api.criteria;

import org.jetbrains.annotations.NotNull;

import java.util.function.Predicate;

public interface CriteriaProvider<E> {

    @NotNull Predicate<E> getPredicate();

    @NotNull Predicate<E> getPredicate(@NotNull Predicate<E> fallback);
}
