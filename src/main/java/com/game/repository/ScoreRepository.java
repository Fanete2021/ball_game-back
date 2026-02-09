package com.game.repository;

import com.game.config.DatabaseConfig;
import com.game.dto.LeaderboardEntryDTO;
import com.game.utils.LogUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ScoreRepository {
    private static final String REPO_NAME = "ScoreRepository";

    public void saveScore(String sessionId, int score) throws SQLException {
        long startTime = System.currentTimeMillis();
        LogUtil.logRepoStart(REPO_NAME, "saveScore","sessionId", sessionId, "score", score);

        String nickname = getNicknameBySessionId(sessionId);
        if (nickname == null) {
            LogUtil.logRepoError(REPO_NAME, "saveScore", startTime,"Сессия не найдена " + sessionId, null);
            throw new SQLException("Сессия не найдена " + sessionId);
        }

        String sql = "INSERT INTO scores (nickname, score, \"when\") VALUES (?, ?, ?)";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, nickname);
            stmt.setInt(2, score);
            stmt.setLong(3, System.currentTimeMillis() / 1000);

            stmt.executeUpdate();

            LogUtil.logRepoSuccess(REPO_NAME, "saveScore", startTime,"sessionId", sessionId, "nickname", nickname, "score", score);

        } catch (SQLException e) {
            LogUtil.logRepoError(REPO_NAME, "saveScore", startTime,"Ошибка SQL", e);
            throw e;
        }
    }

    private String getNicknameBySessionId(String sessionId) throws SQLException {
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

    public List<LeaderboardEntryDTO> getTopPlayers(int limit) throws SQLException {
        long startTime = System.currentTimeMillis();
        LogUtil.logRepoStart(REPO_NAME, "getTopPlayers", "limit", limit);

        List<LeaderboardEntryDTO> topPlayers = new ArrayList<>();

        String sql = "SELECT nickname, score, \"when\" " +
                "FROM scores " +
                "ORDER BY score DESC, \"when\" ASC " +
                "LIMIT ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, limit);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    LeaderboardEntryDTO entry = new LeaderboardEntryDTO(
                        rs.getString("nickname"),
                        rs.getInt("score"),
                        rs.getLong("when")
                    );
                    topPlayers.add(entry);
                }
            }

            LogUtil.logRepoSuccess(REPO_NAME, "getTopPlayers", startTime,"найдено", topPlayers.size(), "limit", limit);
        } catch (SQLException e) {
            LogUtil.logRepoError(REPO_NAME, "getTopPlayers", startTime,"Ошибка SQL", e);
            throw e;
        }

        return topPlayers;
    }

    public int getBestScore(String nickname) throws SQLException {
        long startTime = System.currentTimeMillis();

        String sql = "SELECT MAX(score) as best_score FROM scores WHERE nickname = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, nickname);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int bestScore = rs.getInt("best_score");
                    LogUtil.logRepoDebug(REPO_NAME, "getBestScore", startTime,"Лучший счет для " + nickname + ": " + bestScore);
                    return bestScore;
                }
            }
        } catch (SQLException e) {
            LogUtil.logRepoError(REPO_NAME, "getBestScore", startTime,"Ошибка SQL", e);
            throw e;
        }

        LogUtil.logRepoDebug(REPO_NAME, "getBestScore", startTime,"Лучший счет для " + nickname + " не найден");
        return 0;
    }

    public int getGlobalRank(String nickname, int score, long when) throws SQLException {
        long startTime = System.currentTimeMillis();

        String sql = """
            SELECT COUNT(*) + 1 as rank 
            FROM scores s2 
            WHERE (
                s2.score > ? OR 
                (s2.score = ? AND s2."when" < ?)
            )
        """;

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, score);
            stmt.setInt(2, score);
            stmt.setLong(3, when);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int rank = rs.getInt("rank");
                    LogUtil.logRepoDebug(REPO_NAME, "getGlobalRank", startTime,"Глобальный ранг для " + nickname + " score=" + score + ": " + rank);
                    return rank;
                }
            }
        } catch (SQLException e) {
            LogUtil.logRepoError(REPO_NAME, "getGlobalRank", startTime,"Ошибка SQL", e);
            throw e;
        }

        LogUtil.logRepoDebug(REPO_NAME, "getGlobalRank", startTime,"Ранг не найден для " + nickname + " (score=" + score + ")");
        return 0;
    }
}