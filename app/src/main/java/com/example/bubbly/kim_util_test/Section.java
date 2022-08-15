package com.example.bubbly.kim_util_test;
import java.util.List;

// TODO https://www.youtube.com/watch?v=x5afKIu0JmY
// Sectione Recycler View

public class Section {

    private String sectionName;
    private List<String> sectionItems;

    public Section(String sectionName, List<String> sectionItems) {
        this.sectionName = sectionName;
        this.sectionItems = sectionItems;
    }

    public String getSectionName() {
        return sectionName;
    }

    public void setSectionName(String sectionName) {
        this.sectionName = sectionName;
    }

    public List<String> getSectionItems() {
        return sectionItems;
    }

    public void setSectionItems(List<String> sectionItems) {
        this.sectionItems = sectionItems;
    }

    @Override
    public String toString() {
        return "Section{" +
                "sectionName='" + sectionName + '\'' +
                ", sectionItems=" + sectionItems +
                '}';
    }
}
