package com.quasys;

import java.io.IOException;
import java.util.*;
import java.util.logging.Level;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
public class BeyondInsight {
    public static JSONObject getSecrets(){



        Utils.log("APP VERSION: " + Settings.APP_VERSION, Level.INFO);
        Utils.log("Starting Execution..." + Settings.EXCECUTION_ID, Level.INFO);
        Utils.log("Getting secrets..", Level.INFO);

        String secretList = Settings.SECRETS_LIST.toLowerCase();
        String folderList = Settings.FOLDER_LIST.toLowerCase();
        String managedAccountList = Settings.MANAGED_ACCOUNTS_LIST.toLowerCase();

        try {

            if (Settings.BT_API_URL.isEmpty() || Settings.BT_API_URL.isBlank()){
                throw new Exception("BT_API_URL is empty!");
            }
            if (Settings.BT_CLIENT_CERTIFICATE.isEmpty() || Settings.BT_CLIENT_CERTIFICATE.isBlank()){
                throw new Exception("BT_CLIENT_CERTIFICATE is empty!");
            }

            String user = Service.signAppIn();
            System.out.println(user);
            if (user != null) {
                JSONObject secrets = getSecretsFromBT(secretList, folderList, managedAccountList);

                Utils.log("Execution Ended." + Settings.EXCECUTION_ID, Level.INFO);

                if (!Service.signAppOut()) {
                    throw new Exception("Sign out failed");
                }
                return  secrets;
            } else {
                Utils.log("Sign in failed", Level.SEVERE);
            }
        } catch (IOException e) {
            Utils.log(String.format("IO Error: %s", e.getMessage()), Level.SEVERE);
        } catch (JSONException e) {
            Utils.log(String.format("JSON Error: %s", e.getMessage()), Level.SEVERE);
        } catch (Exception e) {
            Utils.log(String.format("Unexpected Error: %s", e.getMessage()), Level.SEVERE);
        }
        return null;
    }

    private static JSONObject getSecretsFromBT(String secretList, String folderList, String managedAccountList) {
        JSONArray secrets = null;
        try {
            if (!secretList.isBlank() || !folderList.isBlank()) {
                secrets = getSecretsByFolderPathOrSecretPath(secretList, folderList);
            }
            if (Settings.FETCH_ALL_MANAGED_ACCOUNTS) {
                String[] managedAccounts = getManagedAccounts();
                JSONArray managedSecrets = getSecretsBySystemNameAndAccountName(managedAccounts);
                secrets = joinSecrets(secrets, managedSecrets);
            } else if (!managedAccountList.isBlank()) {
                JSONArray managedSecrets = getSecretsBySystemNameAndAccountName(managedAccountList.split(","));
                secrets = joinSecrets(secrets, managedSecrets);
            }
            JSONObject result = generateSecretJSONObject(secrets);
            return result;
        } catch (Exception e) {
            Utils.log(String.format("Error: %s", e.getMessage()), Level.SEVERE);
        }
        return null;
    }
    public static JSONArray getSecretsByFolderPathOrSecretPath(String secretsBySecretPath, String secretsByFolderPath) throws Exception {
        JSONArray secrets = new JSONArray();
        char separator = '/';


        if (secretsBySecretPath != null && !secretsBySecretPath.isEmpty()) {
            String[] paths = secretsBySecretPath.split(",");
            for (String secretPath : paths) {
                if (secretPath.isEmpty()) continue;

                String[] foldersInPath = secretPath.trim().split(String.valueOf(separator));
                String title = foldersInPath[foldersInPath.length - 1];
                String path = String.join(String.valueOf(separator), Arrays.copyOf(foldersInPath, foldersInPath.length - 1));

                String response = Service.getSecretByPath(path, title, separator, true);
                if (response.length() <= 2){
                    Utils.log(String.format("Secret %s/%s was not Found, Validating Folder: %s", path, title, Arrays.toString(foldersInPath)), Level.INFO);
                    response = Service.getSecretByPath(String.join(String.valueOf(separator), foldersInPath), title, separator, false);
                    if (response.length() <= 2){
                        Utils.log(String.format("Invalid path or Invalid Secret: %s", secretPath), Level.SEVERE);
                        continue;
                    }
                    JSONArray jsonArray = new JSONArray(response);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        JSONObject secret = get_secrets_in_folder(jsonObject);
                        if (secret != null) {
                            secrets.put(secret);
                        }
                    }
                } else {
                    JSONArray jsonArray = new JSONArray(response);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        if (jsonObject.getString("SecretType").equals("File")) {
                            String fileContent = Service.getSecretFilebyId(jsonObject.getString("Id"));
                            if (!fileContent.isEmpty() && fileContent != null) {
                                JSONObject secret =  Utils.createSecretFile(jsonObject, fileContent);
                                if (secret != null) {
                                    secrets.put(secret);
                                }
                            } else {
                                Utils.log(String.format("Error Getting File secret, secret metadata: %s", jsonObject.toString()), Level.SEVERE);
                                continue;
                            }
                        } else {
                            JSONObject secret =  Utils.convertSecretToObject(jsonObject);
                            if (secret != null) {
                                secrets.put(secret);
                            }
                        }
                    }
                }
            }
        }

        if (secretsByFolderPath != null && !secretsByFolderPath.isEmpty()) {
            String[] folders = secretsByFolderPath.split(",");
            Utils.log(String.format("Getting secrets by folders %s", Arrays.toString(folders)), Level.INFO);


            for (String folder : folders) {
                String response = Service.getSecretByPath(folder, "", separator, false);

                if (response.length() <= 2) {
                    Utils.log(String.format("Invalid path or Invalid Secret: %s", folder), Level.SEVERE);
                    continue;
                }
                JSONArray jsonArray = new JSONArray(response);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    JSONObject secret = get_secrets_in_folder(jsonObject);
                    if (secret != null) {
                        secrets.put(secret);
                    }
                }
            }
        }
        return  secrets;
    }

    public static JSONArray getSecretsBySystemNameAndAccountName(String[] managedAccounts) throws Exception {
        JSONArray secrets = new JSONArray();
        for (String manageAccount: managedAccounts) {

            String[] data = manageAccount.strip().split("/");
            if (data.length != 2) {
                Utils.log(String.format("Invalid Managed Account: %s", manageAccount.strip()), Level.SEVERE);
                continue;
            }
            String systemName = data[0];
            String accountName = data[1];

            String secretPath = systemName + "/" + accountName;
            String response = Service.getManagedAccounts(systemName, accountName);
            if (response.isBlank() || response.equals("Managed Account not found")) {
                Utils.log(String.format("Invalid Managed Account: %s", secretPath), Level.SEVERE);
                continue;
            }
            JSONObject jsonObject = new JSONObject(response);
            int systemID = (Integer) jsonObject.get("SystemId");
            int accountID = (Integer) jsonObject.get("AccountId");
            String requestID = Service.createRequestInPasswordSafe(systemID, accountID);
            String credential = Service.getCredentialByRequestID(requestID);
            secrets.put(Utils.convertManagedAccountToObject(jsonObject, credential));
        }
        return secrets;
    }
    public static JSONObject get_secrets_in_folder(JSONObject secret) throws Exception {
        if ("File".equals(secret.getString("SecretType"))) {
            String fileContent = Service.getSecretFilebyId(secret.getString("Id"));
            if (!fileContent.isEmpty() && fileContent != null) {
                return Utils.createSecretFile(secret, fileContent);
            } else {
                Utils.log(String.format("Error Getting File secret, secret metadata: %s", secret.toString()), Level.SEVERE);
                return  null;
            }
        } else {
            return  Utils.convertSecretToObject(secret);
        }
    }

    public static String[] getManagedAccounts() throws Exception {
        char separator = '/';
        String response = Service.getManagedAccounts("", "");
        JSONArray jsonArray = new JSONArray(response);
        String[] managedAccounts = new String[jsonArray.length()];
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            managedAccounts[i] = jsonObject.getString("SystemName") + "/" + jsonObject.getString("AccountName");
        }
        return  managedAccounts;
    }
    public static JSONObject generateSecretJSONObject(JSONArray jsonArray) {
        JSONObject result = new JSONObject();

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject item = jsonArray.getJSONObject(i);
            String folderPath = item.getString("FolderPath");
            String[] folders = folderPath.split("/");

            JSONObject current = result;
            for (int j = 0; j < folders.length; j++) {
                String folder = folders[j];
                if (!current.has(folder)) {
                    if (item.has("AccountName") && folder.equals(folders[folders.length - 1])) {
                        current.put(item.getString("AccountName"), item);
                    } else {
                        current.put(folder, new JSONObject());
                    }
                }
                current = current.getJSONObject(folder);
            }

            if (item.has("Title")) {
                current.put(item.getString("Title"), item);
            }
        }
        return result;
    }
    public static JSONArray joinSecrets(JSONArray secrets1, JSONArray secrets2){
        for (int i = 0; i < secrets2.length(); i++){
            secrets1.put(secrets2.get(i));
        }
        return secrets1;
    }
}
