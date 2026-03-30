package com.yunbian.adsscoring.scoring.service.impl;

import com.yunbian.adsscoring.campaign.dto.CampaignMetricsMatrixItem;
import com.yunbian.adsscoring.campaign.mapper.CampaignMetricsMatrixMapper;
import com.yunbian.adsscoring.scoring.dto.CampaignRankingPreviewResponse;
import com.yunbian.adsscoring.scoring.enums.ScoringMetricKey;
import com.yunbian.adsscoring.scoring.service.ScoringPreviewService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class ScoringPreviewServiceImpl implements ScoringPreviewService {

    private static final BigDecimal ONE_HUNDRED = new BigDecimal("100");

    @Resource
    private CampaignMetricsMatrixMapper campaignMetricsMatrixMapper;

    @Override
    public CampaignRankingPreviewResponse previewCampaignRanking(
            Long sid,
            LocalDate logDate,
            ScoringMetricKey metricKey,
            Integer effectDays
    ) {
        List<CampaignMetricsMatrixItem> sourceRows =
                campaignMetricsMatrixMapper.selectAllMetricsMatrixBySidAndLogDate(sid, logDate);

        List<MetricCandidate> candidates = new ArrayList<>();
        for (CampaignMetricsMatrixItem item : sourceRows) {
            BigDecimal metricValue = extractMetricValue(item, metricKey, effectDays);
            if (metricValue == null) {
                continue;
            }

            MetricCandidate candidate = new MetricCandidate();
            candidate.setCampaignId(item.getCampaignId());
            candidate.setCampaignName(item.getCampaignName());
            candidate.setMetricValue(metricValue);
            candidates.add(candidate);
        }

        Comparator<BigDecimal> metricValueComparator = metricKey.isHigherBetter()
                ? (left, right) -> right.compareTo(left)
                : BigDecimal::compareTo;

        candidates.sort(
                Comparator.comparing(MetricCandidate::getMetricValue, metricValueComparator)
                        .thenComparing(MetricCandidate::getCampaignId, Comparator.nullsLast(Long::compareTo))
        );

        List<CampaignRankingPreviewResponse.CampaignRankingRow> rankingRows = new ArrayList<>();
        int comparisonCount = candidates.size();

        for (int i = 0; i < candidates.size(); i++) {
            MetricCandidate candidate = candidates.get(i);

            CampaignRankingPreviewResponse.CampaignRankingRow row =
                    new CampaignRankingPreviewResponse.CampaignRankingRow();
            row.setCampaignId(candidate.getCampaignId());
            row.setCampaignName(candidate.getCampaignName());
            row.setMetricValue(candidate.getMetricValue());
            row.setRank(i + 1);
            row.setScore(buildRankingScore(i + 1, comparisonCount));

            rankingRows.add(row);
        }

        CampaignRankingPreviewResponse response = new CampaignRankingPreviewResponse();
        response.setSid(sid);
        response.setLogDate(logDate);
        response.setEntityLevel("campaign");
        response.setRuleType("ranking");
        response.setMetricKey(metricKey.getCode());
        response.setMetricName(metricKey.getName());
        response.setMetricDirection(metricKey.getDirection());
        response.setEffectDays(effectDays);
        response.setRawRowCount(sourceRows.size());
        response.setComparisonCount(comparisonCount);
        response.setExcludedNullCount(sourceRows.size() - comparisonCount);
        response.setRows(rankingRows);
        return response;
    }

    private BigDecimal buildRankingScore(int rank, int comparisonCount) {
        if (comparisonCount <= 0) {
            return null;
        }
        if (comparisonCount == 1) {
            return ONE_HUNDRED.setScale(4, RoundingMode.HALF_UP);
        }

        BigDecimal numerator = BigDecimal.valueOf(comparisonCount - rank);
        BigDecimal denominator = BigDecimal.valueOf(comparisonCount - 1);

        return numerator
                .multiply(ONE_HUNDRED)
                .divide(denominator, 4, RoundingMode.HALF_UP);
    }

    private BigDecimal extractMetricValue(
            CampaignMetricsMatrixItem item,
            ScoringMetricKey metricKey,
            Integer effectDays
    ) {
        return switch (effectDays) {
            case 1 -> extract1dMetricValue(item, metricKey);
            case 3 -> extract3dMetricValue(item, metricKey);
            case 7 -> extract7dMetricValue(item, metricKey);
            default -> null;
        };
    }

    private BigDecimal extract1dMetricValue(CampaignMetricsMatrixItem item, ScoringMetricKey metricKey) {
        return switch (metricKey) {
            case ROI -> item.getRoi1d();
            case CVR -> item.getCvr1d();
            case CPC -> item.getEcpc1d();
            case CART_COST -> item.getCartCost1d();
            case DEAL_NEW_CUSTOMER_RATIO -> item.getDealNewCustomerRatio1d();
            case NEW_CUSTOMER_RATIO -> item.getNewCustomerRatio1d();
            case DIRECT_DEAL_RATIO -> item.getDirectDealRatio1d();
        };
    }

    private BigDecimal extract3dMetricValue(CampaignMetricsMatrixItem item, ScoringMetricKey metricKey) {
        return switch (metricKey) {
            case ROI -> item.getRoi3d();
            case CVR -> item.getCvr3d();
            case CPC -> item.getEcpc3d();
            case CART_COST -> item.getCartCost3d();
            case DEAL_NEW_CUSTOMER_RATIO -> item.getDealNewCustomerRatio3d();
            case NEW_CUSTOMER_RATIO -> item.getNewCustomerRatio3d();
            case DIRECT_DEAL_RATIO -> item.getDirectDealRatio3d();
        };
    }

    private BigDecimal extract7dMetricValue(CampaignMetricsMatrixItem item, ScoringMetricKey metricKey) {
        return switch (metricKey) {
            case ROI -> item.getRoi7d();
            case CVR -> item.getCvr7d();
            case CPC -> item.getEcpc7d();
            case CART_COST -> item.getCartCost7d();
            case DEAL_NEW_CUSTOMER_RATIO -> item.getDealNewCustomerRatio7d();
            case NEW_CUSTOMER_RATIO -> item.getNewCustomerRatio7d();
            case DIRECT_DEAL_RATIO -> item.getDirectDealRatio7d();
        };
    }

    private static class MetricCandidate {

        private Long campaignId;
        private String campaignName;
        private BigDecimal metricValue;

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

        public BigDecimal getMetricValue() {
            return metricValue;
        }

        public void setMetricValue(BigDecimal metricValue) {
            this.metricValue = metricValue;
        }
    }
}