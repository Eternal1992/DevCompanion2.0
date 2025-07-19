package com.ethereon.app;

public class ModelItem {
    private String name;
    private String path;
    private boolean isActive;

    public ModelItem(String name, String path, boolean isActive) {
        this.name = name;
        this.path = path;
        this.isActive = isActive;
    }

    public String getName() {
        return name;
    }

    public String getPath() {
        return path;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setActive(boolean active) {
        isActive = active;
    }
}