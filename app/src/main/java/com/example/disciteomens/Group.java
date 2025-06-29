package com.example.disciteomens;

import java.util.List;

public class Group {
    private String id;
    private String name;
    private List<String> members;

    public Group() {}

    public Group(String name, List<String> members) {
        this.name = name;
        this.members = members;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getMembers() {
        return members;
    }

    public void setMembers(List<String> members) {
        this.members = members;
    }
}
