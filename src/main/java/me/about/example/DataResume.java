package me.about.example;

import me.about.poi.ExcelColumn;

public class DataResume {

    @ExcelColumn(name = "reconcilication_item_id", width = 30)
    private Integer reconcilication_item_id;

    @ExcelColumn(name = "settle_account_id", width = 30)
    private Integer settle_account_id;

    @ExcelColumn(name = "settle_account_name", width = 30)
    private String settle_account_name;

    @ExcelColumn(name = "waybill_id", width = 30)
    private Integer waybill_id;

    public Integer getReconcilication_item_id() {
        return reconcilication_item_id;
    }

    public void setReconcilication_item_id(Integer reconcilication_item_id) {
        this.reconcilication_item_id = reconcilication_item_id;
    }

    public Integer getSettle_account_id() {
        return settle_account_id;
    }

    public void setSettle_account_id(Integer settle_account_id) {
        this.settle_account_id = settle_account_id;
    }

    public String getSettle_account_name() {
        return settle_account_name;
    }

    public void setSettle_account_name(String settle_account_name) {
        this.settle_account_name = settle_account_name;
    }

    public Integer getWaybill_id() {
        return waybill_id;
    }

    public void setWaybill_id(Integer waybill_id) {
        this.waybill_id = waybill_id;
    }

    

}
