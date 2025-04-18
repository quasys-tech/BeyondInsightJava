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

    public static final String BT_CLIENT_CERTIFICATE = """
                                                          -----BEGIN CERTIFICATE-----
                                                          MIIDSTCCAjGgAwIBAgIEZtA4gTANBgkqhkiG9w0BAQsFADARMQ8wDQYDVQQDDAZR
                                                          VUFTWVMwHhcNMjQwODI5MDg1OTQ1WhcNMjUwODI5MDg1OTQ1WjARMQ8wDQYDVQQD
                                                          DAZRVUFTWVMwggEiMA0GCSqGSIb3DQEBAQUAA4IBDwAwggEKAoIBAQDQCSTDbxQV
                                                          /LRfLaez5lSkeUH2Q6mIBYpUMchwxraRjiAWaSA9gzVJcyrLcTHnprDip7XjwKQf
                                                          0M+JySBF5C+AnOxXIO8MBzlMvUMfbTNLLalx3FdJy1KY+shB7FJRK6rGtXFF6gWf
                                                          Vku26nCOpSrjc+9QlVr1kIWFxev5ITSKyqUsNil7JvRhCicFS0uEqLY8srNyLRgR
                                                          0sDodpPAbd0HUgyJAVyH08vvCiBvohHySu0crit5WPD6IY/1muCB+UeoQUGYmwow
                                                          JqrtewP5p3OeMv/NrYKdC0YDX67itxBd1djJwQ8ph0ViTYokHeark+P7B4IwcAKF
                                                          Oc+e8r0Sq6sFAgMBAAGjgagwgaUwCQYDVR0TBAIwADBNBgNVHQEERjBEgCDBoC09
                                                          iYgw6dq+bZs0LDV8whaFB/eVorGUVOjfxAoDVaEWMBQxEjAQBgNVBAMTCWVFeWVF
                                                          bXNDQYII38Buh913OFowHQYDVR0OBBYEFEt5hHJWvZApg5C+k/6BR3IFjk93MAsG
                                                          A1UdDwQEAwIDuDAdBgNVHSUEFjAUBggrBgEFBQcDAQYIKwYBBQUHAwIwDQYJKoZI
                                                          hvcNAQELBQADggEBAJN6bOa0rIRWqlpVu21JB7xNB3XwN5apENGoROS6G8hFYRcr
                                                          8EL6wglvEG8kyIxyA/6XhQYFDimnNjENg4YgHbkvt7NyRITSHstQ01sOP2P6rNmY
                                                          JxfrEx07vBUhJDK8rD/0RnxZh7RSVmPJoaTpHV9bxPkUH6V6tcDi7Oo2gB+OFLka
                                                          Tm1Ni9m0aWlAomKRhof0WGHUr4/i0AkzSAPzusHEzFePF/3+OKYkcAxLlX/C8nN9
                                                          qRLUhENaFXXKGKTYhYxw8QqgqdV+0q5V5I7R3D5IHx2Anukcz0EzkU5ghnqrncdK
                                                          7cFyN72R3+ZZ9IoLk2FtC8nPN6yV7YPmKxWbOTs=
                                                          -----END CERTIFICATE-----""";

//    public static final String BT_CLIENT_CERTIFICATE = env.get("BT_CLIENT_CERTIFICATE");

//    public static final String BT_CLIENT_CERTIFICATE_PASSWORD = env.getOrDefault("BT_CLIENT_CERTIFICATE_PASSWORD", "");

    public static String EXCECUTION_ID = null;
    public static final String APP_VERSION = "2.0.0";
}
