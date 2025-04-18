package com.quasys;

import org.json.JSONObject;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.security.KeyStore;
import java.util.Date;
import java.util.UUID;
import java.util.logging.*;
import java.security.cert.CertificateFactory;
import java.security.cert.Certificate;
import java.io.ByteArrayInputStream;
public class Utils {
    private static final Logger logger = Logger.getLogger(Utils.class.getName());

    static {
        if (Settings.EXCECUTION_ID == null) {
            Settings.EXCECUTION_ID = UUID.randomUUID().toString();
        }
        LogManager.getLogManager().reset();
        logger.setLevel(Level.ALL);
        ConsoleHandler handler = new ConsoleHandler();
        handler.setLevel(Level.ALL);
        handler.setFormatter(new SimpleFormatter() {
            private static final String format = " %1$tF %1$tT %2$s (" + Settings.EXCECUTION_ID + ") %3$s%n";

            @Override
            public synchronized String format(LogRecord lr) {
                return String.format(format, new Date(lr.getMillis()), lr.getLevel().getLocalizedName(), lr.getMessage());
            }
        });
        logger.addHandler(handler);
    }

    public static void log(String message, Level level) {
        logger.log(level, message);
    }

    public static JSONObject convertSecretToObject(JSONObject secret) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("Password", secret.getString("Password"));
        jsonObject.put("Title", secret.getString("Title"));
        jsonObject.put("Username", secret.optString("Username", ""));
        jsonObject.put("FolderPath", secret.getString("FolderPath"));
        jsonObject.put("FilePath", "");
        jsonObject.put("IsFileSecret", false);
        return jsonObject;
    }

    public static JSONObject convertManagedAccountToObject(JSONObject secret, String content) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("Password", content);
        jsonObject.put("SystemName", secret.getString("SystemName"));
        jsonObject.put("AccountName", secret.getString("AccountName"));
        jsonObject.put("FolderPath", secret.getString("SystemName") + "/" + secret.getString("AccountName"));
        jsonObject.put("IsFileSecret", false);
        return jsonObject;
    }

    public static JSONObject createSecretFile(JSONObject secret, String content) throws IOException {
        String path = secret.getString("FolderPath").replace("\\", "/") + "/" + secret.get("Title");
        String filePath = createFolders(path);
        Files.writeString(Paths.get(filePath), content + "\n", StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("Password", secret.optString("Password", ""));
        jsonObject.put("Title", secret.getString("Title"));
        jsonObject.put("Username", secret.optString("Username", ""));
        jsonObject.put("FolderPath", secret.getString("FolderPath"));
        jsonObject.put("FilePath", filePath);
        jsonObject.put("IsFileSecret", true);
        return jsonObject;
    }

    public static String createFolders(String path) throws IOException {
        Path folderPath = Paths.get(Settings.SECRETS_PATH, path);
        Files.createDirectories(folderPath.getParent());
        return folderPath.toString();
    }

    public static SSLContext generateSSL() throws Exception {
        final CertificateFactory cf = CertificateFactory.getInstance("X.509");
        final Certificate cert = cf.generateCertificate(new ByteArrayInputStream(Settings.BT_CLIENT_CERTIFICATE.getBytes()));
        final KeyStore ks = KeyStore.getInstance("JKS");
        ks.load(null);
        ks.setCertificateEntry("BTTlsCaPath", cert);

        final TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
        tmf.init(ks);
        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, tmf.getTrustManagers(), null);
        return sslContext;
    }
}
