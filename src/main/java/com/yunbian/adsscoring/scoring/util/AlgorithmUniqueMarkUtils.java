package com.yunbian.adsscoring.scoring.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HexFormat;
import java.util.Locale;

public final class AlgorithmUniqueMarkUtils {

    private static final DateTimeFormatter LOG_DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private AlgorithmUniqueMarkUtils() {
    }

    public static String buildUniqueMark(
            Long enterpriseId,
            Long sid,
            Long algorithmTemplateId,
            String businessType,
            Long businessId,
            LocalDate logDate
    ) {
        String normalizedBusinessType = businessType == null
                ? ""
                : businessType.toLowerCase(Locale.ROOT);
        String formattedLogDate = logDate == null
                ? ""
                : logDate.format(LOG_DATE_FORMATTER);

        String rawUniqueKey = String.join("|",
                String.valueOf(enterpriseId),
                String.valueOf(sid),
                String.valueOf(algorithmTemplateId),
                normalizedBusinessType,
                String.valueOf(businessId),
                formattedLogDate
        );

        return md5(rawUniqueKey);
    }

    private static String md5(String value) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(value.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(digest);
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("MD5 algorithm is not available", exception);
        }
    }
}
