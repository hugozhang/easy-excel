package me.about.example.vms;

import me.about.poi.ExcelColumn;

public class VmsData3 {

    @ExcelColumn(name = "租户ID", width = 30)
    private Integer tenantId;
    @ExcelColumn(name = "司机id", width = 30)
    private Integer driverId;
    @ExcelColumn(name = "新关联承运商ID", width = 30)
    private Integer vendorId;
    @ExcelColumn(name = "现关联承运商ID", width = 30)
    private Integer oldVendorId;
    public Integer getDriverId() {
        return driverId;
    }
    public void setDriverId(Integer driverId) {
        this.driverId = driverId;
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

    
}
