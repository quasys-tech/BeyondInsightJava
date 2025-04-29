package com.quasys;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import javax.net.ssl.SSLContext;

public class Service {
    private static String cookie;
    private static HttpClient createHttpClient() throws Exception {
        SSLContext sslContext = Utils.generateSSL();
        HttpClient client = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
//                .connectTimeout(Duration.ofSeconds(10)) 
                .sslContext(sslContext)
                .build();
        return  client;
    }

    public static String signAppIn() throws Exception {
        String url = Settings.BT_API_URL + "/Auth/SignAppin";
        cookie = "";
        HttpClient client = createHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(url))
                .header("Authorization", Settings.REQUEST_HEADERS)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            Map<String, List<String>> headers = response.headers().map();
            List<String> cookies = headers.get("Set-Cookie");
            if (cookies != null && !cookies.isEmpty()) {
                cookie = cookies.get(0);
            }
            Utils.log("Logged in successfully", Level.INFO);
            return response.body();
        }
        Utils.log("sendPostSignAppIn: Error trying to sign app in: " + response.body(), Level.SEVERE);
        return null;
    }

    public static boolean signAppOut() throws Exception {
        String url = Settings.BT_API_URL + "/Auth/Signout";
        HttpClient client = createHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(url))
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 200) {
            cookie = "";
            return true;
        }

        Utils.log("signAppOut: Error trying to sign app out: " + response.body(), Level.SEVERE);
        return false;
    }

    public static String getSecretByPath(String path, String title, char separator, boolean sendTitle) throws Exception {
        String url = Settings.BT_API_URL + "/secrets-safe/secrets" + "?path=" + path + "&separator=" + separator;
        if (sendTitle) {
            url = Settings.BT_API_URL + "/secrets-safe/secrets" + "?title=" + title + "&path=" + path + "&separator=" + separator;
        }

        HttpClient client = createHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(url))
                .header("Authorization", Settings.REQUEST_HEADERS)
                .header("Content-Type", "application/json")
                .header("Cookie", cookie)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 200) {
            return response.body();
        }
        Utils.log("get_secret_by_path: Error trying to get secret by path:" + path + " and title " + title + ", response: " + response.body(), Level.SEVERE);

        if (!signAppOut()) {
            Utils.log("Eror trying to sign out!", Level.SEVERE);
        }
        return null;
    }

    public static String getSecretFilebyId(String secretId) throws Exception {
        String url = Settings.BT_API_URL + "/secrets-safe/secrets/"+secretId+"/file/download";
        HttpClient client = createHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(url))
                .header("Authorization", Settings.REQUEST_HEADERS)
                .header("Content-Type", "application/json")
                .header("Cookie", cookie)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 200) {
            return response.body();
        }
        Utils.log(String.format("get_file_by_id: Error trying to get file by secret Id %s: %s", secretId, response.body()), Level.SEVERE);
        if (!signAppOut()) {
            Utils.log("Eror trying to sign out!", Level.SEVERE);
        }
        return null;
    }

    public static String getManagedAccounts(String systemName, String accountName) throws Exception {
        String url = Settings.BT_API_URL + "/ManagedAccounts?systemName=" + systemName + "&accountName=" + accountName;
        HttpClient client = createHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(url))
                .header("Authorization", Settings.REQUEST_HEADERS)
                .header("Content-Type", "application/json")
                .header("Cookie", cookie)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 200) {
            return response.body();
        }
        Utils.log(String.format("getManagedAccounts: Error trying to get secret by system name: %s and account name %s, response: %s", systemName, accountName, response.body()), Level.SEVERE);
        if (!signAppOut()) {
            Utils.log("Eror trying to sign out!", Level.SEVERE);
        }
        return null;
    }

    public static String createRequestInPasswordSafe(int systemID, int accountID) throws Exception {
        String url = Settings.BT_API_URL + "/Requests";

        String body = """
{
    "SystemID": %d,
    "AccountID": %d,
    "DurationMinutes": 5,
    "Reason": "Test",
    "ConflictOption": "reuse"
}
                """.formatted(systemID, accountID);

        HttpClient client = createHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(url))
                .header("Authorization", Settings.REQUEST_HEADERS)
                .header("Content-Type", "application/json")
                .header("Cookie", cookie)
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 200 || response.statusCode() == 201) {
            return response.body();
        }
        Utils.log(String.format("create_request: Error trying to create request, payload: %s, response: %s", body, response.body()), Level.SEVERE);
        if (!signAppOut()) {
            Utils.log("Eror trying to sign out!", Level.SEVERE);
        }
        return null;
    }

    public static String getCredentialByRequestID(String requestID) throws Exception {
        String url = Settings.BT_API_URL + "/Credentials/" + requestID;
        HttpClient client = createHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(url))
                .header("Authorization", Settings.REQUEST_HEADERS)
                .header("Content-Type", "application/json")
                .header("Cookie", cookie)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 200) {
            return response.body().replaceAll("^\"|\"$", "");
        }
        Utils.log(String.format("get_credential_by_request_id: Error trying to get credential by request id: %s, response: %s", requestID, response.body()), Level.SEVERE);
        if (!signAppOut()) {
            Utils.log("Eror trying to sign out!", Level.SEVERE);
        }
        return null;
    }
}
