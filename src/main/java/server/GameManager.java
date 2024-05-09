package server;
import java.util.concurrent.ConcurrentHashMap;
import common.GameSession;

public class GameManager {
    private ConcurrentHashMap<String, GameSession> sessions;

    public GameManager() {
        this.sessions = new ConcurrentHashMap<>();
    }

    public String createGame(String playerOne, String playerTwo) {
        String sessionId = generateSessionId();
        GameSession session = new GameSession(playerOne, playerTwo);
        sessions.put(sessionId, session);
        return sessionId;
    }

    public boolean endGame(String sessionId) {
        return sessions.remove(sessionId) != null;
    }

    public GameSession getSession(String sessionId) {
        return sessions.get(sessionId);
    }

    private String generateSessionId() {
        // Generate a unique ID for each session
        return Long.toString(System.nanoTime());
    }
}
