package com.ethereon.app.models;

import java.util.ArrayList;
import java.util.List;

public class AppIdeaModel {

    private String appName;
    private String description;
    private String language;
    private String platform;
    private final List<String> features;

    public AppIdeaModel() {
        this.features = new ArrayList<>();
    }

    public AppIdeaModel(String appName) {
        this.appName = appName;
        this.features = new ArrayList<>();
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String name) {
        this.appName = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public List<String> getFeatures() {
        return features;
    }

    public void addFeature(String feature) {
        features.add(feature);
    }

    public boolean hasFeature(String feature) {
        return features.contains(feature);
    }

    @Override
    public String toString() {
        return "AppIdeaModel{" +
                "appName='" + appName + '\'' +
                ", description='" + description + '\'' +
                ", language='" + language + '\'' +
                ", platform='" + platform + '\'' +
                ", features=" + features +
                '}';
    }
}