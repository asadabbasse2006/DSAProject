package com.example.dsaproject.Util;

import com.example.dsaproject.Model.User;

/**
 * Session Manager - Singleton Pattern
 * Manages current user session
 */
public class SessionManager {
    private static SessionManager instance;
    private User currentUser;
    private long loginTime;

    private SessionManager() {}

    public static SessionManager getInstance() {
        if (instance == null) {
            synchronized (SessionManager.class) {
                if (instance == null) {
                    instance = new SessionManager();
                }
            }
        }
        return instance;
    }

    public void setCurrentUser(User user) {
        this.currentUser = user;
        this.loginTime = System.currentTimeMillis();
    }

    public com.example.dsaproject.Model.User getCurrentUser() {
        return currentUser;
    }

    public boolean isLoggedIn() {
        return currentUser != null;
    }

    public void logout() {
        currentUser = null;
        loginTime = 0;
    }

    public long getSessionDuration() {
        if (loginTime == 0) return 0;
        return System.currentTimeMillis() - loginTime;
    }
}