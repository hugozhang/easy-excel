package me.about.example;

import java.util.Date;

import me.about.poi.ExcelColumn;

public class CustomerMapping {

    @ExcelColumn(name = "客户名称", width = 30)
    private String customerName;
    
    @ExcelColumn(name = "运单开始时间", width = 30)
    private Date start;
    
    @ExcelColumn(name = "运单结束时间", width = 30)
    private Date end;

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public Date getStart() {
        return start;
    }

    public void setStart(Date start) {
        this.start = start;
    }

    public Date getEnd() {
        return end;
    }

    public void setEnd(Date end) {
        this.end = end;
    }
    
}
