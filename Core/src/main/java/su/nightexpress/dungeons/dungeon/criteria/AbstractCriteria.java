package su.nightexpress.dungeons.dungeon.criteria;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.dungeons.api.criteria.Criteria;
import su.nightexpress.dungeons.api.criteria.CriteriaPredicate;
import su.nightexpress.dungeons.api.criteria.CriteriaValidator;

public abstract class AbstractCriteria<T, E> implements Criteria<T, E> {

    protected final CriteriaValidator<T> validator;
    protected final String               name;

    public AbstractCriteria(@NotNull CriteriaValidator<T> validator, @NotNull String name) {
        this.validator = validator;
        this.name = name;
    }

    @NotNull
    @Override
    public CriteriaValidator<T> getValidator() {
        return this.validator;
    }

    @NotNull
    @Override
    public String getName() {
        return this.name;
    }

    @NotNull
    @Override
    public CriteriaPredicate<T, E> validate(@NotNull String string) {
        return this.predicate(this.validator.deserialize(string));
    }

    @NotNull
    @Override
    public CriteriaPredicate<T, E> predicate(@NotNull T value) {
        return new CriteriaPredicate<>(this, value);
    }
}
