package example.com.notes.model;

import java.io.Serializable;

/**
 * Created by VISHU on 06-Aug-2016.
 */

public class NotesInfo implements Serializable {

    private int id = 0;
    private String title = "";
    private String content = "";
    private boolean selected = false;

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
