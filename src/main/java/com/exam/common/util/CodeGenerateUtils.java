package com.exam.common.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Predicate;

/**
 * 业务编码生成工具。
 */
public final class CodeGenerateUtils {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.BASIC_ISO_DATE;

    private CodeGenerateUtils() {
    }

    public static String generate(String prefix, Predicate<String> existsPredicate) {
        String datePart = LocalDate.now().format(DATE_FORMATTER);
        for (int i = 0; i < 100; i++) {
            String code = prefix + datePart + String.format("%04d", ThreadLocalRandom.current().nextInt(10_000));
            if (!existsPredicate.test(code)) {
                return code;
            }
        }
        throw new IllegalStateException("业务编码生成失败，请稍后重试");
    }
}
