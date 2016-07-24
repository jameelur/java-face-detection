package data;

import java.util.ArrayList;

public class CascadingClassifier {

    private int height;
    private int width;

    private ArrayList<Stage> stages = new ArrayList<Stage>();

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public ArrayList<Stage> getStages() {
        return stages;
    }

    public void setStages(ArrayList<Stage> stages) {
        this.stages = stages;
    }
}
