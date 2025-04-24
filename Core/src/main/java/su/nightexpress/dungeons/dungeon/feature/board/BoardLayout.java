package su.nightexpress.dungeons.dungeon.feature.board;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.config.FileConfig;
import su.nightexpress.nightcore.config.Writeable;

import java.util.ArrayList;
import java.util.List;

public class BoardLayout implements Writeable {

    //private final String       id;
    private final String       title;
    private final List<String> lines;

    public BoardLayout(/*@NotNull String id, */@NotNull String title, @NotNull List<String> lines) {
        //this.id = id.toLowerCase();
        this.title = title;
        this.lines = lines;
    }

    @NotNull
    public static BoardLayout read(@NotNull FileConfig config, @NotNull String path/*, @NotNull String id*/) {
        String title = config.getString(path +  ".Title", "");
        List<String> lines = config.getStringList(path + ".List");
        return new BoardLayout(/*id, */title, lines);
    }

    @Override
    public void write(@NotNull FileConfig config, @NotNull String path) {
        config.set(path + ".Title", this.title);
        config.set(path + ".List", this.lines);
    }

//    @NotNull
//    public String getId() {
//        return id;
//    }

    @NotNull
    public String getTitle() {
        return title;
    }

    @NotNull
    public List<String> getLines() {
        return new ArrayList<>(lines);
    }
}
