package me.about.example;

import me.about.poi.ExcelColumn;

public class CustomerDepartmentMapping {

    @ExcelColumn(name = "customer_id", width = 30)
    private Integer customer_id;
    
    @ExcelColumn(name = "department_id", width = 30)
    private Integer department_id;

    public Integer getCustomer_id() {
        return customer_id;
    }

    public void setCustomer_id(Integer customer_id) {
        this.customer_id = customer_id;
    }

    public Integer getDepartment_id() {
        return department_id;
    }

    public void setDepartment_id(Integer department_id) {
        this.department_id = department_id;
    }
    
}
