package me.about.example;

import me.about.poi.ExcelColumn;

public class ReconalicionHis {

    @ExcelColumn(name = "project_id", width = 30)
    private Integer projectId;
    
    @ExcelColumn(name = "label", width = 30)
    private String label;

    public Integer getProjectId() {
        return projectId;
    }

    public void setProjectId(Integer projectId) {
        this.projectId = projectId;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }
    
}
