package org.example.shopdemo.service;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;

@Slf4j
@UtilityClass
public class LogService {
    public static void logInfo(String action, String message, Object... params) {
        log.info("For requestId: {}, {} {}", MDC.get("requestId"), action, String.format(message, params));
    }

    public static void logWarn(String action, String message, Object... params) {
        log.warn("For requestId: {}, {} {}", MDC.get("requestId"), action, String.format(message, params));
    }

    public static void logError(String action, String message, Object... params) {
        log.error("For requestId: {}, {} {}", MDC.get("requestId"), action, String.format(message, params));
    }

    public static void logDebug(String action, String message, Object... params) {
        log.debug("For requestId: {}, {} {}", MDC.get("requestId"), action, String.format(message, params));
    }
}
