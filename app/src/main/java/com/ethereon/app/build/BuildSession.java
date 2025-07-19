package com.ethereon.app.build;

import com.ethereon.app.models.AppIdeaModel;

import java.util.ArrayList;
import java.util.List;

public class BuildSession {

    private final String sessionId;
    private final AppIdeaModel appIdea;
    private final long startTime;
    private long endTime;
    private boolean completed;
    private final List<String> buildSteps;
    private final BuildOrchestrationLog log;

    public BuildSession(String sessionId, AppIdeaModel appIdea) {
        this.sessionId = sessionId;
        this.appIdea = appIdea;
        this.startTime = System.currentTimeMillis();
        this.buildSteps = new ArrayList<>();
        this.log = new BuildOrchestrationLog();
        this.completed = false;
    }

    public String getSessionId() {
        return sessionId;
    }

    public AppIdeaModel getAppIdea() {
        return appIdea;
    }

    public long getStartTime() {
        return startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void markCompleted() {
        this.completed = true;
        this.endTime = System.currentTimeMillis();
        log.add("Session", "Build session marked as complete.");
    }

    public boolean isCompleted() {
        return completed;
    }

    public void addBuildStep(String step) {
        buildSteps.add(step);
        log.add("Step", step);
    }

    public List<String> getBuildSteps() {
        return buildSteps;
    }

    public BuildOrchestrationLog getLog() {
        return log;
    }
}