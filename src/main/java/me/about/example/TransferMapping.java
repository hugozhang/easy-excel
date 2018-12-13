package me.about.example;

import me.about.poi.ExcelColumn;

public class TransferMapping {
    
    @ExcelColumn(name = "customer_id", width = 30)
    private Integer customer_id;
    
    @ExcelColumn(name = "所属客户", width = 30)
    private String customer_name;
    
    @ExcelColumn(name = "area_code", width = 30)
    private String area_code;
    
    @ExcelColumn(name = "waybill_no", width = 30)
    private String waybillNo;
    
    @ExcelColumn(name = "customer_manager_id", width = 30)
    private Integer customer_manager_id;
    
    @ExcelColumn(name = "project_id", width = 30)
    private Integer project_id;

    public Integer getCustomer_id() {
        return customer_id;
    }

    public void setCustomer_id(Integer customer_id) {
        this.customer_id = customer_id;
    }

    public String getArea_code() {
        return area_code;
    }

    public void setArea_code(String area_code) {
        this.area_code = area_code;
    }

    public String getWaybillNo() {
        return waybillNo;
    }

    public void setWaybillNo(String waybillNo) {
        this.waybillNo = waybillNo;
    }

    public Integer getCustomer_manager_id() {
        return customer_manager_id;
    }

    public void setCustomer_manager_id(Integer customer_manager_id) {
        this.customer_manager_id = customer_manager_id;
    }

    public Integer getProject_id() {
        return project_id;
    }

    public void setProject_id(Integer project_id) {
        this.project_id = project_id;
    }

    public String getCustomer_name() {
        return customer_name;
    }

    public void setCustomer_name(String customer_name) {
        this.customer_name = customer_name;
    }
}
