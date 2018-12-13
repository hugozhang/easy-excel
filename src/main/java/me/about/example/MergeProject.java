package me.about.example;

import me.about.poi.ExcelColumn;

public class MergeProject {
    
    @ExcelColumn(name = "customer_id", width = 30)
    private Integer customerId;
    
    @ExcelColumn(name = "project_id", width = 30)
    private Integer projectId;

    @ExcelColumn(name = "project_name", width = 30)
    private String projectName;
    
    public Integer getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Integer customerId) {
        this.customerId = customerId;
    }

    public Integer getProjectId() {
        return projectId;
    }

    public void setProjectId(Integer projectId) {
        this.projectId = projectId;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

}
