
package com.game.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogUtil {
    private static final Logger requestLogger = LoggerFactory.getLogger("REQUEST_LOG");

    public static void logRepoStart(String repoName, String method, Object... params) {
        log(repoName, "Запуск", method, 0, null, null, params);
    }

    public static void logRepoSuccess(String repoName, String method, long startTime, Object... results) {
        long duration = System.currentTimeMillis() - startTime;
        log(repoName, "Успех", method, duration, null, null, results);
        requestLogger.info("REPO | {} | {} | время={}ms",repoName, method, duration);
    }

    public static void logRepoError(String repoName, String method, long startTime, String error, Exception e) {
        long duration = System.currentTimeMillis() - startTime;

        log(repoName, "Ошибка", method, duration, error, e);
        requestLogger.error("REPO | {} | {} | время={}ms | ошибка={}", repoName, method, duration, error);
    }

    public static void logRepoDebug(String repoName, String method, long startTime, String message) {
        Logger logger = LoggerFactory.getLogger(repoName);
        long duration = System.currentTimeMillis() - startTime;
        logger.debug("[{}:{}] {} | время={}ms", repoName, method, message, duration);
    }

    public static void logRepoWarn(String repoName, String method, long startTime, String message) {
        Logger logger = LoggerFactory.getLogger(repoName);
        long duration = System.currentTimeMillis() - startTime;
        logger.warn("[{}: {}] {} | время={}ms", repoName, method, message, duration);
    }

    private static void log(String repoName, String status, String method, long duration, String error, Exception e, Object... params) {
        Logger logger = LoggerFactory.getLogger(repoName);
        String paramStr = buildParamString(params);
        if ("Ошибка".equals(status)) {
            logger.error("[{}] {}: {} | время={}ms | {}", repoName, status, method, duration, error, e);
        } else {
            logger.info("[{}] {}: {} {} | время={}ms", repoName, status, method, paramStr, duration);
        }
    }

    private static String buildParamString(Object... params) {
        if (params == null || params.length == 0) return "";

        StringBuilder string = new StringBuilder();
        for (int i = 0; i < params.length; i += 2) {
            if (i + 1 < params.length) {
                if (!string.isEmpty()) string.append(", ");
                string.append(params[i]).append("=").append(params[i + 1]);
            }
        }
        return string.toString();
    }
}
