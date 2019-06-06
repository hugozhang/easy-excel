package me.about.example;

import me.about.poi.ExcelColumn;

public class Lili {
    
    @ExcelColumn(name = "new_tgm_customer_id", width = 30)
    private Integer new_tgm_customer_id;
    
    @ExcelColumn(name = "old_tgm_customer_id", width = 30)
    private Integer old_tgm_customer_id;

    public Integer getNew_tgm_customer_id() {
        return new_tgm_customer_id;
    }

    public void setNew_tgm_customer_id(Integer new_tgm_customer_id) {
        this.new_tgm_customer_id = new_tgm_customer_id;
    }

    public Integer getOld_tgm_customer_id() {
        return old_tgm_customer_id;
    }

    public void setOld_tgm_customer_id(Integer old_tgm_customer_id) {
        this.old_tgm_customer_id = old_tgm_customer_id;
    }

}
