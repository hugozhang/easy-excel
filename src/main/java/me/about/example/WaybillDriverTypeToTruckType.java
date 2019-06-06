package me.about.example;

import me.about.poi.ExcelColumn;

public class WaybillDriverTypeToTruckType {

    @ExcelColumn(name = "waybill_id", width = 30)
    private Integer waybill_id;
    
    @ExcelColumn(name = "driver_type", width = 30)
    private Integer driver_type;

    public Integer getWaybill_id() {
        return waybill_id;
    }

    public void setWaybill_id(Integer waybill_id) {
        this.waybill_id = waybill_id;
    }

    public Integer getDriver_type() {
        return driver_type;
    }

    public void setDriver_type(Integer driver_type) {
        this.driver_type = driver_type;
    }

    
    
}
