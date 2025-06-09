package su.nightexpress.dungeons.dungeon.script.condition;

public class ConditionId {

    public static final String CHANCE        = "chance";
    public static final String TICK_INTERVAL = "tick_interval";
    public static final String MOB_ID        = "mob_id";
    public static final String TASK_ID       = "task_id";
    public static final String STAGE_ID      = "stage_id";

    public static final String MOBS_AMOUNT  = "mobs_amount";
    public static final String MOBS_KILLED  = "mobs_killed";
    public static final String MOBS_SPAWNED = "mobs_spawned";

    @Deprecated
    public static final String ALIVE_MOB_AMOUNT = "alive_mob_amount";
    @Deprecated
    public static final String ALIVE_MOBS_AMOUNT   = "alive_mobs_amount";
    @Deprecated
    public static final String KILLED_MOB_AMOUNT   = "killed_mob_amount";
    @Deprecated
    public static final String KILLED_MOBS_AMOUNT  = "killed_mobs_amount";
    @Deprecated
    public static final String SPAWNED_MOB_AMOUNT  = "spawned_mob_amount";
    @Deprecated
    public static final String SPAWNED_MOBS_AMOUNT = "spawned_mobs_amount";

    public static final String SPOT_HAS_STATE      = "spot_in_state";
    public static final String SPOT_NOT_IN_STATE   = "spot_not_in_state";
    public static final String TASK_PRESENT        = "task_present";
    public static final String TASK_NOT_PRESENT    = "task_not_present";
    public static final String TASK_COMPLETED      = "task_completed";
    public static final String TASK_INCOMPLETED    = "task_incompleted";
}
