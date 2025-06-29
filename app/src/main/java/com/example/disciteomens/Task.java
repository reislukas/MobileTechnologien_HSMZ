package com.example.disciteomens;

public class Task {
    private String id;
    private String groupId;
    private String title;
    private boolean completed;

    public Task() {}

    public Task(String title, boolean completed, String groupId) {
        this.title = title;
        this.completed = completed;
        this.groupId = groupId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }
}
