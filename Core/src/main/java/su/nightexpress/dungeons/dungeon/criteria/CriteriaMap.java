package su.nightexpress.dungeons.dungeon.criteria;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.dungeons.api.criteria.CriteriaPredicate;
import su.nightexpress.dungeons.api.criteria.CriteriaProvider;
import su.nightexpress.dungeons.dungeon.criteria.registry.CriteriaHolder;
import su.nightexpress.nightcore.config.FileConfig;
import su.nightexpress.nightcore.config.Writeable;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

public class CriteriaMap<C extends AbstractCriteria<?, E>, E> implements CriteriaProvider<E>, Writeable {

    private final Map<C, CriteriaPredicate<?, E>> criterias;

    public CriteriaMap(@NotNull Map<C, CriteriaPredicate<?, E>> criterias) {
        this.criterias = criterias;
    }

    @NotNull
    public static <C extends AbstractCriteria<?, E>, E> CriteriaMap<C, E> read(@NotNull FileConfig config, @NotNull String path, @NotNull CriteriaHolder<C> holder) {
        Map<C, CriteriaPredicate<?, E>> criterias = new HashMap<>();

        config.getSection(path).forEach(id -> {
            C criteria = holder.get(id);
            if (criteria == null) return;

            String value = config.getString(path + "." + id);
            if (value == null) return;

            CriteriaPredicate<?, E> predicate = criteria.validate(value);
            criterias.put(criteria, predicate);
        });

        return new CriteriaMap<>(criterias);
    }

    @Override
    public void write(@NotNull FileConfig config, @NotNull String path) {
        config.remove(path);
        this.criterias.forEach((criteria, predicate) -> config.set(path + "." + criteria.getName(), predicate.getRawValue()));
    }

    @Override
    @NotNull
    public Predicate<E> getPredicate() {
        return this.getPredicate(entity -> true);
    }

    @NotNull
    @Override
    public Predicate<E> getPredicate(@NotNull Predicate<E> fallback) {
        return this.criterias.values().stream().map(predicate -> (Predicate<E>) predicate).reduce(Predicate::and).orElse(fallback);
    }
}
