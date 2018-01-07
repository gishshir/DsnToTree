package fr.tsadeo.app.dsntotree.gui.table.common;

public class Column {

    private final int index;
    private final int width;
    private final String title;

    public int getIndex() {
        return index;
    }

    public int getWidth() {
        return width;
    }

    public String getTitle() {
        return title;
    }

    public Column(int index, int width, String title) {
        this.index = index;
        this.width = width;
        this.title = title;
    }
}
