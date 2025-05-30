package com.itstep.smakmobile.auth;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.UUID;

public class AuthManager {
    private static final String PREF_NAME = "smak_auth_prefs";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_ROLE = "role";
    private static final String KEY_NAME = "name";

    private final SharedPreferences prefs;
    private final TokenManager tokenManager;

    public AuthManager(Context context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        tokenManager = new TokenManager(context);
    }

    public void saveUserInfo(UUID id, String role, String name) {
        prefs.edit()
                .putString(KEY_USER_ID, id.toString())
                .putString(KEY_ROLE, role)
                .putString(KEY_NAME, name)
                .apply();
    }

    public void logout() {
        prefs.edit().clear().apply();
        tokenManager.clearTokens();
    }

    public boolean isAuthenticated() {
        return tokenManager.getAccessToken() != null;
    }

    public UUID getUserId() {
        String idStr = prefs.getString(KEY_USER_ID, null);
        if (idStr == null) {
            return null;
        }
        return UUID.fromString(idStr);
    }

    public String getRole() {
        return prefs.getString(KEY_ROLE, null);
    }

    public String getUserName() {
        return prefs.getString(KEY_NAME, null);
    }

    public boolean isChef() {
        return "Chef".equalsIgnoreCase(getRole());
    }

    public boolean isAdmin() {
        return "Admin".equalsIgnoreCase(getRole());
    }
}
