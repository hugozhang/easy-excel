package me.about.example;

import me.about.poi.ExcelColumn;

public class Waybill {
    
    @ExcelColumn(name = "运单号", width = 30)
    private String waybillNo;
    
    @ExcelColumn(name = "所属客户", width = 30)
    private String customerName;

    public String getWaybillNo() {
        return waybillNo;
    }

    public void setWaybillNo(String waybillNo) {
        this.waybillNo = waybillNo;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

}
