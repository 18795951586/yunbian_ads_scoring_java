package com.yunbian.adsscoring.bidword.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class BidwordSampleItem {

    private Long sid;
    private LocalDate logDate;
    private Integer effect;
    private Long campaignId;
    private String campaignName;
    private Long promotionId;
    private String promotionName;
    private Long adgroupId;
    private String adgroupName;
    private Long bidwordId;
    private String bidWordType;
    private String bidWordTypeName;
    private String originalWord;
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

    public Long getPromotionId() {
        return promotionId;
    }

    public void setPromotionId(Long promotionId) {
        this.promotionId = promotionId;
    }

    public String getPromotionName() {
        return promotionName;
    }

    public void setPromotionName(String promotionName) {
        this.promotionName = promotionName;
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

    public Long getBidwordId() {
        return bidwordId;
    }

    public void setBidwordId(Long bidwordId) {
        this.bidwordId = bidwordId;
    }

    public String getBidWordType() {
        return bidWordType;
    }

    public void setBidWordType(String bidWordType) {
        this.bidWordType = bidWordType;
    }

    public String getBidWordTypeName() {
        return bidWordTypeName;
    }

    public void setBidWordTypeName(String bidWordTypeName) {
        this.bidWordTypeName = bidWordTypeName;
    }

    public String getOriginalWord() {
        return originalWord;
    }

    public void setOriginalWord(String originalWord) {
        this.originalWord = originalWord;
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