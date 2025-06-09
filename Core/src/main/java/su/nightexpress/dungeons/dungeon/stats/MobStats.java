package su.nightexpress.dungeons.dungeon.stats;

public class MobStats {

    private int spawnedAmount;
    private int killedAmount;

    public void addSpawn() {
        this.spawnedAmount += 1;
    }

    public void addKill() {
        this.killedAmount += 1;
    }

    public int getSpawnedAmount() {
        return spawnedAmount;
    }

    public void setSpawnedAmount(int spawnedAmount) {
        this.spawnedAmount = spawnedAmount;
    }

    public int getKilledAmount() {
        return killedAmount;
    }

    public void setKilledAmount(int killedAmount) {
        this.killedAmount = killedAmount;
    }
}
