package su.nightexpress.dungeons.dungeon.criteria.registry;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.dungeons.dungeon.criteria.AbstractCriteria;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class CriteriaHolder<C extends AbstractCriteria<?, ?>> {

    private final Map<String, C> byId;

    public CriteriaHolder() {
        this.byId = new HashMap<>();
    }

    public void clear() {
        this.byId.clear();
    }

    @NotNull
    public <T extends C> T register(@NotNull T criteria) {
        this.byId.put(criteria.getName().toLowerCase(), criteria);
        return criteria;
    }

    @Nullable
    public C get(@NotNull String name) {
        return this.byId.get(name.toLowerCase());
    }

    @NotNull
    public Set<C> values() {
        return new HashSet<>(this.byId.values());
    }
}
