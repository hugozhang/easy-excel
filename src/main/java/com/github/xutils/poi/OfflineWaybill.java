package com.github.xutils.poi;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.github.xutils.poi.ExcelColumn;
/**
 * offline_waybill - offline_waybill
 * 
 * @author  2017-02-08
 * @version 1.0 
 */
public class OfflineWaybill implements Serializable {
    private static final long serialVersionUID = -6385666915263290751L;
    private Map<String,Integer> receiptTypeMapping = new HashMap<String,Integer>();
    
    {
        receiptTypeMapping.put("微信支付", 1);
        receiptTypeMapping.put("现金支付", 2);
        receiptTypeMapping.put("项目结算", 3);
        receiptTypeMapping.put("付现金，司机收款", 4);
        receiptTypeMapping.put("付现金，客户经理收款", 5);
    }
    
    private Integer offlineWaybillId;
	private Integer truckCustomerId;
	@ExcelColumn(name="用车人电话")
	private String customerPhone;
	@ExcelColumn(name="用车人")
	private String truckCustomerName;
	@ExcelColumn(name="用车时间")
	private Date planDeliveryTime;
	private Integer driverId;
	@ExcelColumn(name="司机电话")
	private String driverPhone;
	@ExcelColumn(name="司机")
	private String driverName;
	@ExcelColumn(name="车牌号")
	private String plateNumber;
	@ExcelColumn(name="税前费用")
	private Double estimateFreight;
	@ExcelColumn(name="税后费用")
	private Double afterTaxFreight;
	private Integer taxRateId;
	@ExcelColumn(name="是否开票")
	private Float taxRateValue;
	private Integer receiptType;
	@ExcelColumn(name="付款方式")
	private String receiptTypeValue;
	private Integer customerId;
	@ExcelColumn(name="所属客户")
	private String customerName;
	@ExcelColumn(name="省")
	private String province;
	@ExcelColumn(name="市")
	private String city;
	@ExcelColumn(name="区")
	private String district;
	@ExcelColumn(name="街道")
	private String street;
	@ExcelColumn(name="货物重量")
	private String goodsWeight;
	@ExcelColumn(name="货物类型")
	private String goodsType;
	@ExcelColumn(name="货物体积")
	private String goodsVolume;
	@ExcelColumn(name="是否回单")
	private String isBackValue;
	private Integer isBack;
	@ExcelColumn(name="是否搬运")
	private String isCarryValue;
	private Integer isCarry;
	@ExcelColumn(name="司机搬运费")
	private Double driverHandlingCost;
	@ExcelColumn(name="小工搬运费")
	private Double laborerHandlingCost;
	@ExcelColumn(name="配送点")
	private Integer distributionPointNo;
	@ExcelColumn(name="下单备注")
	private String remark;
	private String result;
	private Integer status;
	private Integer createUserId;
	private Date createTime;

	public Integer getOfflineWaybillId() {
		return offlineWaybillId;
	}

	public void setOfflineWaybillId(Integer offlineWaybillId) {
		this.offlineWaybillId = offlineWaybillId;
	}

	public String getCustomerPhone() {
		return customerPhone;
	}

	public void setCustomerPhone(String customerPhone) {
		this.customerPhone = customerPhone;
	}

	public Date getPlanDeliveryTime() {
		return planDeliveryTime;
	}

	public void setPlanDeliveryTime(Date planDeliveryTime) {
		this.planDeliveryTime = planDeliveryTime;
	}

	public String getDriverPhone() {
		return driverPhone;
	}

	public void setDriverPhone(String driverPhone) {
		this.driverPhone = driverPhone;
	}

	public String getPlateNumber() {
		return plateNumber;
	}

	public void setPlateNumber(String plateNumber) {
		this.plateNumber = plateNumber;
	}

	public Double getEstimateFreight() {
		return estimateFreight;
	}

	public void setEstimateFreight(Double estimateFreight) {
		this.estimateFreight = estimateFreight;
	}

	public Integer getTaxRateId() {
		return taxRateId;
	}

	public void setTaxRateId(Integer taxRateId) {
		this.taxRateId = taxRateId;
	}

	public Double getAfterTaxFreight() {
		return afterTaxFreight;
	}

	public void setAfterTaxFreight(Double afterTaxFreight) {
		this.afterTaxFreight = afterTaxFreight;
	}

	public Integer getReceiptType() {
		return receiptType;
	}

	public void setReceiptType(Integer receiptType) {
		this.receiptType = receiptType;
	}

	public Integer getCustomerId() {
		return customerId;
	}

	public void setCustomerId(Integer customerId) {
		this.customerId = customerId;
	}

	public String getProvince() {
		return province;
	}

	public void setProvince(String province) {
		this.province = province;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getDistrict() {
		return district;
	}

	public void setDistrict(String district) {
		this.district = district;
	}

	public String getStreet() {
		return street;
	}

	public void setStreet(String street) {
		this.street = street;
	}

	public String getGoodsWeight() {
		return goodsWeight;
	}

	public void setGoodsWeight(String goodsWeight) {
		this.goodsWeight = goodsWeight;
	}

	public String getGoodsType() {
		return goodsType;
	}

	public void setGoodsType(String goodsType) {
		this.goodsType = goodsType;
	}

	public String getGoodsVolume() {
		return goodsVolume;
	}

	public void setGoodsVolume(String goodsVolume) {
		this.goodsVolume = goodsVolume;
	}

	public Integer getIsBack() {
		return isBack;
	}

	public void setIsBack(Integer isBack) {
		this.isBack = isBack;
	}

	public Integer getIsCarry() {
		return isCarry;
	}

	public void setIsCarry(Integer isCarry) {
		this.isCarry = isCarry;
	}

	public Double getDriverHandlingCost() {
		return driverHandlingCost;
	}

	public void setDriverHandlingCost(Double driverHandlingCost) {
		this.driverHandlingCost = driverHandlingCost;
	}

	public Double getLaborerHandlingCost() {
		return laborerHandlingCost;
	}

	public void setLaborerHandlingCost(Double laborerHandlingCost) {
		this.laborerHandlingCost = laborerHandlingCost;
	}

	public Integer getDistributionPointNo() {
		return distributionPointNo;
	}

	public void setDistributionPointNo(Integer distributionPointNo) {
		this.distributionPointNo = distributionPointNo;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public Integer getCreateUserId() {
		return createUserId;
	}

	public void setCreateUserId(Integer createUserId) {
		this.createUserId = createUserId;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Float getTaxRateValue() {
        return taxRateValue;
    }

    public void setTaxRateValue(Float taxRateValue) {
        this.taxRateValue = taxRateValue;
    }

    public String getReceiptTypeValue() {
        return receiptTypeValue;
    }

    public void setReceiptTypeValue(String receiptTypeValue) {
        this.receiptTypeValue = receiptTypeValue;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getIsBackValue() {
        return isBackValue;
    }

    public void setIsBackValue(String isBackValue) {
        this.isBackValue = isBackValue;
    }

    public String getIsCarryValue() {
        return isCarryValue;
    }

    public void setIsCarryValue(String isCarryValue) {
        this.isCarryValue = isCarryValue;
    }

    public Integer getTruckCustomerId() {
        return truckCustomerId;
    }

    public void setTruckCustomerId(Integer truckCustomerId) {
        this.truckCustomerId = truckCustomerId;
    }

    public Integer getDriverId() {
        return driverId;
    }

    public void setDriverId(Integer driverId) {
        this.driverId = driverId;
    }

    public Integer getReceiptTypeMappingValue(String key){
        return receiptTypeMapping.get(key);
    }
    
    public Map<String, Integer> getReceiptTypeMapping() {
        return receiptTypeMapping;
    }

    public void setReceiptTypeMapping(Map<String, Integer> receiptTypeMapping) {
        this.receiptTypeMapping = receiptTypeMapping;
    }

    public String getTruckCustomerName() {
        return truckCustomerName;
    }

    public void setTruckCustomerName(String truckCustomerName) {
        this.truckCustomerName = truckCustomerName;
    }

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

}