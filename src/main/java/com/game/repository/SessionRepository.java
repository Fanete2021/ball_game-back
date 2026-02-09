package com.game.repository;

import com.game.config.DatabaseConfig;
import com.game.utils.LogUtil;

import java.sql.*;
import java.util.UUID;

public class SessionRepository {
    private static final String REPO_NAME = "SessionRepository";

    public String createSession(String nickname, String clientVersion) throws SQLException {
        long startTime = System.currentTimeMillis();
        LogUtil.logRepoStart(REPO_NAME, "createSession","nickname", nickname, "clientVersion", clientVersion);

        String sessionId = UUID.randomUUID().toString();
        long startedAt = System.currentTimeMillis() / 1000;

        String sql = "INSERT INTO sessions (sessionId, nickname, startedAt, clientVersion) VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, sessionId);
            stmt.setString(2, nickname);
            stmt.setLong(3, startedAt);
            stmt.setString(4, clientVersion);

            stmt.executeUpdate();

            LogUtil.logRepoSuccess(REPO_NAME, "createSession", startTime,"sessionId", sessionId, "nickname", nickname);

            return sessionId;
        } catch (SQLException e) {
            LogUtil.logRepoError(REPO_NAME, "createSession", startTime,
                    "Ошибка SQL", e);
            throw e;
        }
    }

    public void finishSession(String sessionId) throws SQLException {
        long startTime = System.currentTimeMillis();
        LogUtil.logRepoStart(REPO_NAME, "finishSession", "sessionId", sessionId);

        String sql = "UPDATE sessions SET finishedAt = ? WHERE sessionId = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, System.currentTimeMillis() / 1000);
            stmt.setString(2, sessionId);

            int updated = stmt.executeUpdate();

            LogUtil.logRepoSuccess(REPO_NAME, "finishSession", startTime,"sessionId", sessionId, "updated", updated);

        } catch (SQLException e) {
            LogUtil.logRepoError(REPO_NAME, "finishSession", startTime,"Ошибка SQL", e);
            throw e;
        }
    }

    public String getNicknameBySessionId(String sessionId) throws SQLException {
        long startTime = System.currentTimeMillis();

        String sql = "SELECT nickname FROM sessions WHERE sessionId = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, sessionId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String nickname = rs.getString("nickname");
                    LogUtil.logRepoDebug(REPO_NAME, "getNicknameBySessionId", startTime,"Найден nickname " + nickname + " для sessionId=" + sessionId);
                    return nickname;
                }
            }
        } catch (SQLException e) {
            LogUtil.logRepoError(REPO_NAME, "getNicknameBySessionId", startTime,"Ошибка SQL", e);
            throw e;
        }

        LogUtil.logRepoWarn(REPO_NAME, "getNicknameBySessionId", startTime,"Не найден nickname для sessionId=" + sessionId);
        return null;
    }

    public boolean sessionExists(String sessionId) throws SQLException {
        long startTime = System.currentTimeMillis();

        String sql = "SELECT 1 FROM sessions WHERE sessionId = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, sessionId);
            try (ResultSet rs = stmt.executeQuery()) {
                boolean exists = rs.next();
                LogUtil.logRepoDebug(REPO_NAME, "sessionExists", startTime,"sessionId=" + sessionId + " exists=" + exists);
                return exists;
            }
        } catch (SQLException e) {
            LogUtil.logRepoError(REPO_NAME, "sessionExists", startTime,"Ошибка SQL", e);
            throw e;
        }
    }

    public long getSessionStartTime(String sessionId) throws SQLException {
        long startTime = System.currentTimeMillis();

        String sql = "SELECT startedAt FROM sessions WHERE sessionId = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, sessionId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    long startedAt = rs.getLong("startedAt");
                    LogUtil.logRepoDebug(REPO_NAME, "getSessionStartTime", startTime,"sessionId=" + sessionId + " startedAt=" + startedAt);
                    return startedAt;
                }
            }
        } catch (SQLException e) {
            LogUtil.logRepoError(REPO_NAME, "getSessionStartTime", startTime,"Ошибка SQL", e);
            throw e;
        }

        LogUtil.logRepoWarn(REPO_NAME, "getSessionStartTime", startTime,"Не найдено время начала для sessionId=" + sessionId);
        return 0;
    }
}