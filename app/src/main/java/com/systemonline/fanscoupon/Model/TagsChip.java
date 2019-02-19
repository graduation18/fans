package com.systemonline.fanscoupon.Model;

public class TagsChip {

    private String id = "";
    private String name = "";
    private String usage = "";

    public TagsChip() {
    }

    public TagsChip(String id, String name, String usage) {
        this.id = id;
        this.name = name;
        this.usage = usage;
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

    public String getUsage() {
        return usage;
    }

    public void setUsage(String usage) {
        this.usage = usage;
    }
}