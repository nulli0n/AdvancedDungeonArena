package su.nightexpress.dungeons.api.criteria;

import org.jetbrains.annotations.NotNull;

import java.util.function.Predicate;

public class CriteriaPredicate<T, E> implements Predicate<E> {

    private final Criteria<T, E> criteria;
    private final T              value;

    public CriteriaPredicate(@NotNull Criteria<T, E> criteria, @NotNull T value) {
        this.criteria = criteria;
        this.value = value;
    }

    @NotNull
    public Criteria<T, E> getCriteria() {
        return this.criteria;
    }

    @NotNull
    public T getValue() {
        return this.value;
    }

    @NotNull
    public String getRawValue() {
        return this.criteria.getValidator().serialize(this.value);
    }

    @Override
    public boolean test(@NotNull E entity) {
        return this.criteria.test(entity, this.value);
    }
}
