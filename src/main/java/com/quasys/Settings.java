package com.quasys;

import java.util.Map;
public class Settings {
    private static final Map<String, String> env = System.getenv();
    public static final String BT_API_URL = env.get("BT_API_URL");
    public static final String REQUEST_HEADERS = "Authorization: PS-Auth key=" + env.get("BT_API_KEY");
//    public static final boolean BT_VERIFY_CA = env.getOrDefault("BT_VERIFY_CA", "false").equalsIgnoreCase("true");
    public static final boolean FETCH_ALL_MANAGED_ACCOUNTS = !env.getOrDefault("FETCH_ALL_MANAGED_ACCOUNTS", "true").equalsIgnoreCase("false");

    public static final String APP_PATH = "/usr/src/app";
    public static final String DEFAULT_SECRETS_FOLDER = "secrets_files";
    public static String SECRETS_PATH = APP_PATH + "/" + DEFAULT_SECRETS_FOLDER;

    static {
        String secretsPath = env.get("SECRETS_PATH");
        if (secretsPath != null && !secretsPath.trim().isEmpty()) {
            SECRETS_PATH = secretsPath;
        }
    }

    public static final String SECRETS_LIST = env.getOrDefault("SECRETS_LIST", "");
    public static final String FOLDER_LIST = env.getOrDefault("FOLDER_LIST", "");
    public static final String MANAGED_ACCOUNTS_LIST = env.getOrDefault("MANAGED_ACCOUNTS_LIST", "");

//    public static final String BT_CLIENT_CERTIFICATE_PATH = env.getOrDefault("BT_CLIENT_CERTIFICATE_PATH", null);

    public static final String BT_CLIENT_CERTIFICATE = env.get("BT_CLIENT_CERTIFICATE");

//    public static final String BT_CLIENT_CERTIFICATE_PASSWORD = env.getOrDefault("BT_CLIENT_CERTIFICATE_PASSWORD", "");

    public static String EXCECUTION_ID = null;
    public static final String APP_VERSION = "2.0.0";
}