package com.yunbian.adsscoring.adgroup.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class AdgroupSampleItem {

    private Long sid;
    private LocalDate logDate;
    private Integer effect;
    private Long campaignId;
    private String campaignName;
    private Long adgroupId;
    private String adgroupName;
    private BigDecimal charge;
    private BigDecimal ecpc;
    private BigDecimal cvr;
    private BigDecimal roi;
    private BigDecimal cartCost;

    public Long getSid() {
        return sid;
    }

    public void setSid(Long sid) {
        this.sid = sid;
    }

    public LocalDate getLogDate() {
        return logDate;
    }

    public void setLogDate(LocalDate logDate) {
        this.logDate = logDate;
    }

    public Integer getEffect() {
        return effect;
    }

    public void setEffect(Integer effect) {
        this.effect = effect;
    }

    public Long getCampaignId() {
        return campaignId;
    }

    public void setCampaignId(Long campaignId) {
        this.campaignId = campaignId;
    }

    public String getCampaignName() {
        return campaignName;
    }

    public void setCampaignName(String campaignName) {
        this.campaignName = campaignName;
    }

    public Long getAdgroupId() {
        return adgroupId;
    }

    public void setAdgroupId(Long adgroupId) {
        this.adgroupId = adgroupId;
    }

    public String getAdgroupName() {
        return adgroupName;
    }

    public void setAdgroupName(String adgroupName) {
        this.adgroupName = adgroupName;
    }

    public BigDecimal getCharge() {
        return charge;
    }

    public void setCharge(BigDecimal charge) {
        this.charge = charge;
    }

    public BigDecimal getEcpc() {
        return ecpc;
    }

    public void setEcpc(BigDecimal ecpc) {
        this.ecpc = ecpc;
    }

    public BigDecimal getCvr() {
        return cvr;
    }

    public void setCvr(BigDecimal cvr) {
        this.cvr = cvr;
    }

    public BigDecimal getRoi() {
        return roi;
    }

    public void setRoi(BigDecimal roi) {
        this.roi = roi;
    }

    public BigDecimal getCartCost() {
        return cartCost;
    }

    public void setCartCost(BigDecimal cartCost) {
        this.cartCost = cartCost;
    }
}