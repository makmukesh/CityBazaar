package com.vpipl.citybazaar.model;
public class ProductList_Data {
    private String ProdId;
    private String ProductName;
    private String Qty;
    private String ProductDesc;
    private String BatchNo;
    private String AvailQty;
    private String MRP;
    private String DP;
    private String Rate;
    private String BV;
    private String PV;
    private String RP;
    private String DiscPer;
    private String DiscAmt;
    private String TaxType;
    private String TaxPer;
    private String IsKit;
    private String ProdType;
    private String TaxAmt;  //Amount , TotalAmt
    private String IsCommssnAsTDisc;
    private String CommssnOn;
    private String CGSTPer;
    private String CGSTAmt;
    private String IGSTPer;
    private String IGSTAmt;

    private String Barcode;
    private String HSNCode;
    private String UOM;
    private String InvRate;

    public String getBarcode() {
        return Barcode;
    }

    public void setBarcode(String barcode) {
        Barcode = barcode;
    }

    public String getHSNCode() {
        return HSNCode;
    }

    public void setHSNCode(String HSNCode) {
        this.HSNCode = HSNCode;
    }

    public String getUOM() {
        return UOM;
    }

    public void setUOM(String UOM) {
        this.UOM = UOM;
    }

    public String getInvRate() {
        return InvRate;
    }

    public void setInvRate(String invRate) {
        InvRate = invRate;
    }

    public String getCGSTPer() {
        return CGSTPer;
    }

    public void setCGSTPer(String CGSTPer) {
        this.CGSTPer = CGSTPer;
    }

    public String getCGSTAmt() {
        return CGSTAmt;
    }

    public void setCGSTAmt(String CGSTAmt) {
        this.CGSTAmt = CGSTAmt;
    }

    public String getIGSTPer() {
        return IGSTPer;
    }

    public void setIGSTPer(String IGSTPer) {
        this.IGSTPer = IGSTPer;
    }

    public String getIGSTAmt() {
        return IGSTAmt;
    }

    public void setIGSTAmt(String IGSTAmt) {
        this.IGSTAmt = IGSTAmt;
    }

    public String getSGST_TaxPer() {
        return SGST_TaxPer;
    }

    public void setSGST_TaxPer(String SGST_TaxPer) {
        this.SGST_TaxPer = SGST_TaxPer;
    }

    public String getSGST_TaxAmt() {
        return SGST_TaxAmt;
    }

    public void setSGST_TaxAmt(String SGST_TaxAmt) {
        this.SGST_TaxAmt = SGST_TaxAmt;
    }

    private String SGST_TaxPer;
    private String SGST_TaxAmt;

    public String getProdId() {
        return ProdId;
    }

    public void setProdId(String prodId) {
        ProdId = prodId;
    }

    public String getProductName() {
        return ProductName;
    }

    public void setProductName(String productName) {
        ProductName = productName;
    }

    public String getDP() {
        return DP;
    }

    public String getProductDesc() {
        return ProductDesc;
    }

    public void setProductDesc(String productDesc) {
        ProductDesc = productDesc;
    }

    public String getBatchNo() {
        return BatchNo;
    }

    public void setBatchNo(String batchNo) {
        BatchNo = batchNo;
    }

    public String getAvailQty() {
        return AvailQty;
    }

    public void setAvailQty(String availQty) {
        AvailQty = availQty;
    }

    public String getMRP() {
        return MRP;
    }

    public void setMRP(String MRP) {
        this.MRP = MRP;
    }

    public String getPV() {
        return PV;
    }

    public void setPV(String PV) {
        this.PV = PV;
    }

    public String getRP() {
        return RP;
    }

    public void setRP(String RP) {
        this.RP = RP;
    }

    public String getDiscPer() {
        return DiscPer;
    }

    public void setDiscPer(String discPer) {
        DiscPer = discPer;
    }

    public String getDiscAmt() {
        return DiscAmt;
    }

    public void setDiscAmt(String discAmt) {
        DiscAmt = discAmt;
    }

    public String getTaxType() {
        return TaxType;
    }

    public void setTaxType(String taxType) {
        TaxType = taxType;
    }

    public String getTaxPer() {
        return TaxPer;
    }

    public void setTaxPer(String taxPer) {
        TaxPer = taxPer;
    }

    public String getIsKit() {
        return IsKit;
    }

    public void setIsKit(String isKit) {
        IsKit = isKit;
    }

    public String getProdType() {
        return ProdType;
    }

    public void setProdType(String prodType) {
        ProdType = prodType;
    }

    public String getTaxAmt() {
        return TaxAmt;
    }

    public void setTaxAmt(String taxAmt) {
        TaxAmt = taxAmt;
    }

    public String getIsCommssnAsTDisc() {
        return IsCommssnAsTDisc;
    }

    public void setIsCommssnAsTDisc(String isCommssnAsTDisc) {
        IsCommssnAsTDisc = isCommssnAsTDisc;
    }

    public String getCommssnOn() {
        return CommssnOn;
    }

    public void setCommssnOn(String commssnOn) {
        CommssnOn = commssnOn;
    }

    public void setDP(String DP) {
        this.DP = DP;
    }

    public String getRate() {
        return Rate;
    }

    public void setRate(String rate) {
        Rate = rate;
    }

    public String getBV() {
        return BV;
    }

    public void setBV(String BV) {
        this.BV = BV;
    }

    public String getQty() {
        return Qty;
    }

    public void setQty(String qty) {
        Qty = qty;
    }
}