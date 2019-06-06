package me.about.example.vms;

import me.about.poi.ExcelColumn;

public class VmsData2 {

    @ExcelColumn(name = "租户ID", width = 30)
    private Integer tenantId;
    @ExcelColumn(name = "TMS车辆ID", width = 30)
    private Integer truckId;
    @ExcelColumn(name = "AMS车辆ID", width = 30)
    private Integer vehicleId;
    @ExcelColumn(name = "新关联承运商ID", width = 30)
    private Integer vendorId;
    @ExcelColumn(name = "现关联承运商ID", width = 30)
    private Integer oldVendorId;

    public Integer getTruckId() {
        return truckId;
    }

    public void setTruckId(Integer truckId) {
        this.truckId = truckId;
    }

    public Integer getVendorId() {
        return vendorId;
    }

    public void setVendorId(Integer vendorId) {
        this.vendorId = vendorId;
    }

    public Integer getOldVendorId() {
        return oldVendorId;
    }

    public void setOldVendorId(Integer oldVendorId) {
        this.oldVendorId = oldVendorId;
    }

    public Integer getTenantId() {
        return tenantId;
    }

    public void setTenantId(Integer tenantId) {
        this.tenantId = tenantId;
    }

    public Integer getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(Integer vehicleId) {
        this.vehicleId = vehicleId;
    }

}
