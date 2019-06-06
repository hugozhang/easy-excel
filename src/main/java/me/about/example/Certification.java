package me.about.example;

import me.about.poi.ExcelColumn;

public class Certification {

    @ExcelColumn(name = "userid", width = 30)
    private Integer userId;
    @ExcelColumn(name = "承运商id", width = 30)
    private Integer vendorId;
    @ExcelColumn(name = "承运商类型", width = 30)
    private String vendorType;
    @ExcelColumn(name = "承运商名称", width = 30)
    private String vendorName;
    @ExcelColumn(name = "业务范围", width = 30)
    private String areaName;
    @ExcelColumn(name = "联系人姓名", width = 30)
    private String contactName;
    @ExcelColumn(name = "联系电话", width = 30)
    private String contactPhone;
    @ExcelColumn(name = "身份证号", width = 30)
    private String idCardNo;
    @ExcelColumn(name = "租户名称", width = 30)
    private Integer tenantId;
    @ExcelColumn(name = "实名认证", width = 30)
    private String result;

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getVendorId() {
        return vendorId;
    }

    public void setVendorId(Integer vendorId) {
        this.vendorId = vendorId;
    }

    public String getVendorType() {
        return vendorType;
    }

    public void setVendorType(String vendorType) {
        this.vendorType = vendorType;
    }

    public String getVendorName() {
        return vendorName;
    }

    public void setVendorName(String vendorName) {
        this.vendorName = vendorName;
    }

    public String getAreaName() {
        return areaName;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }

    public String getIdCardNo() {
        return idCardNo;
    }

    public void setIdCardNo(String idCardNo) {
        this.idCardNo = idCardNo;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public Integer getTenantId() {
        return tenantId;
    }

    public void setTenantId(Integer tenantId) {
        this.tenantId = tenantId;
    }

}