package me.about.example;

import me.about.poi.ExcelColumn;

public class VendorTenant {

    @ExcelColumn(name = "vendor_id", width = 30)
    private Integer vendorId;
    
    @ExcelColumn(name = "tenant_id", width = 30)
    private String tenantId;

    public Integer getVendorId() {
        return vendorId;
    }

    public void setVendorId(Integer vendorId) {
        this.vendorId = vendorId;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }
    
}
