package me.about.example.vms;

import me.about.poi.ExcelColumn;

public class VmsData {

    @ExcelColumn(name = "租户ID", width = 30)
    private Integer tenantId;
    @ExcelColumn(name = "TMS车辆ID", width = 30)
    private Integer truckId;
    @ExcelColumn(name = "认领部门ID", width = 30)
    private Integer truckBelongToCompany;
    public Integer getTruckId() {
        return truckId;
    }
    public void setTruckId(Integer truckId) {
        this.truckId = truckId;
    }
    public Integer getTruckBelongToCompany() {
        return truckBelongToCompany;
    }
    public void setTruckBelongToCompany(Integer truckBelongToCompany) {
        this.truckBelongToCompany = truckBelongToCompany;
    }
    public Integer getTenantId() {
        return tenantId;
    }
    public void setTenantId(Integer tenantId) {
        this.tenantId = tenantId;
    }
    
}
