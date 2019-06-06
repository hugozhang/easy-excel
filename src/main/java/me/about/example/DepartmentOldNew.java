package me.about.example;

import me.about.poi.ExcelColumn;

public class DepartmentOldNew {

    @ExcelColumn(name = "customer_id", width = 30)
    private Integer customer_id;
    
    @ExcelColumn(name = "new_department_id", width = 30)
    private Integer new_department_id;
    
    @ExcelColumn(name = "old_department_id", width = 30)
    private Integer old_department_id;

    public Integer getCustomer_id() {
        return customer_id;
    }

    public void setCustomer_id(Integer customer_id) {
        this.customer_id = customer_id;
    }

    public Integer getNew_department_id() {
        return new_department_id;
    }

    public void setNew_department_id(Integer new_department_id) {
        this.new_department_id = new_department_id;
    }

    public Integer getOld_department_id() {
        return old_department_id;
    }

    public void setOld_department_id(Integer old_department_id) {
        this.old_department_id = old_department_id;
    }
    
}
