package com.yunbian.adsscoring.campaign.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class CampaignMetricsMatrixItem {

    private Long sid;
    private LocalDate logDate;
    private Long campaignId;
    private String campaignName;

    private BigDecimal roi1d;
    private BigDecimal cvr1d;
    private BigDecimal ecpc1d;
    private BigDecimal cartCost1d;

    private BigDecimal roi3d;
    private BigDecimal cvr3d;
    private BigDecimal ecpc3d;
    private BigDecimal cartCost3d;

    private BigDecimal roi7d;
    private BigDecimal cvr7d;
    private BigDecimal ecpc7d;
    private BigDecimal cartCost7d;

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

    public BigDecimal getRoi1d() {
        return roi1d;
    }

    public void setRoi1d(BigDecimal roi1d) {
        this.roi1d = roi1d;
    }

    public BigDecimal getCvr1d() {
        return cvr1d;
    }

    public void setCvr1d(BigDecimal cvr1d) {
        this.cvr1d = cvr1d;
    }

    public BigDecimal getEcpc1d() {
        return ecpc1d;
    }

    public void setEcpc1d(BigDecimal ecpc1d) {
        this.ecpc1d = ecpc1d;
    }

    public BigDecimal getCartCost1d() {
        return cartCost1d;
    }

    public void setCartCost1d(BigDecimal cartCost1d) {
        this.cartCost1d = cartCost1d;
    }

    public BigDecimal getRoi3d() {
        return roi3d;
    }

    public void setRoi3d(BigDecimal roi3d) {
        this.roi3d = roi3d;
    }

    public BigDecimal getCvr3d() {
        return cvr3d;
    }

    public void setCvr3d(BigDecimal cvr3d) {
        this.cvr3d = cvr3d;
    }

    public BigDecimal getEcpc3d() {
        return ecpc3d;
    }

    public void setEcpc3d(BigDecimal ecpc3d) {
        this.ecpc3d = ecpc3d;
    }

    public BigDecimal getCartCost3d() {
        return cartCost3d;
    }

    public void setCartCost3d(BigDecimal cartCost3d) {
        this.cartCost3d = cartCost3d;
    }

    public BigDecimal getRoi7d() {
        return roi7d;
    }

    public void setRoi7d(BigDecimal roi7d) {
        this.roi7d = roi7d;
    }

    public BigDecimal getCvr7d() {
        return cvr7d;
    }

    public void setCvr7d(BigDecimal cvr7d) {
        this.cvr7d = cvr7d;
    }

    public BigDecimal getEcpc7d() {
        return ecpc7d;
    }

    public void setEcpc7d(BigDecimal ecpc7d) {
        this.ecpc7d = ecpc7d;
    }

    public BigDecimal getCartCost7d() {
        return cartCost7d;
    }

    public void setCartCost7d(BigDecimal cartCost7d) {
        this.cartCost7d = cartCost7d;
    }
}