package me.about.data;

import me.about.poi.ExcelColumn;

public class Yingfu {

    @ExcelColumn(name = "ap_no", width = 30)
    private String ap_no;
    
    @ExcelColumn(name = "department_id", width = 30)
    private Integer department_id;

    public String getAp_no() {
        return ap_no;
    }

    public void setAp_no(String ap_no) {
        this.ap_no = ap_no;
    }

    public Integer getDepartment_id() {
        return department_id;
    }

    public void setDepartment_id(Integer department_id) {
        this.department_id = department_id;
    }
    
}
