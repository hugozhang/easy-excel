package me.about.poi;

public class Mapping {

    private String customer_name;
    
    @ExcelColumn(name="old_area_code")
    private String old_area_code;

    @ExcelColumn(name="new_area_code")
    private String new_area_code;

    public String getOld_area_code() {
        return old_area_code;
    }

    public void setOld_area_code(String old_area_code) {
        this.old_area_code = old_area_code;
    }

    public String getNew_area_code() {
        return new_area_code;
    }

    public void setNew_area_code(String new_area_code) {
        this.new_area_code = new_area_code;
    }

    public String getCustomer_name() {
        return customer_name;
    }

    public void setCustomer_name(String customer_name) {
        this.customer_name = customer_name;
    }
   
}
