package lt.imas.react_native_signal.signal;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.whispersystems.libsignal.IdentityKey;
import org.whispersystems.libsignal.IdentityKeyPair;
import org.whispersystems.libsignal.InvalidKeyException;
import org.whispersystems.libsignal.SignalProtocolAddress;
import org.whispersystems.libsignal.state.PreKeyRecord;
import org.whispersystems.libsignal.state.SessionRecord;
import org.whispersystems.libsignal.state.SignalProtocolStore;
import org.whispersystems.libsignal.state.SignedPreKeyRecord;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import lt.imas.react_native_signal.helpers.Base64;
import timber.log.Timber;

public class MessageStorage {
    private Context context;
    private String MESSAGES_JSON_FILENAME = "messages.json";

    public MessageStorage(Context context) {
        this.context = context;
    }

    private String readFromStorage(String fileName) {
        String path = context.getFilesDir().getAbsolutePath() + "/messages/" + fileName;
        File file = new File(path);
        if (file.exists()){
            try {
                FileInputStream fis = context.openFileInput(fileName);
                InputStreamReader isr = new InputStreamReader(fis);
                BufferedReader bufferedReader = new BufferedReader(isr);
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    sb.append(line);
                }
                String data = sb.toString();
                return data;
            } catch (FileNotFoundException fileNotFound) {
                return null;
            } catch (IOException ioException) {
                return null;
            }
        } else {
            return null;
        }
    }

    private void writeToStorageFile(String fileName, String data) {
        try {
            FileOutputStream fos = context.openFileOutput("messages/" + fileName, Context.MODE_PRIVATE);
            if (data != null) fos.write(data.getBytes());
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void deleteAll(){
        File dir = new File(context.getFilesDir().getAbsolutePath() + "/messages");
        if (dir.isDirectory()){
            String[] children = dir.list();
            for (String aChildren : children) {
                new File(dir, aChildren).delete();
            }
        }
    }

    public void deleteContactMessages(String username){
        File dir = new File(context.getFilesDir().getAbsolutePath() + "/messages/" + username);
        if (dir.isDirectory()){
            String[] children = dir.list();
            for (String aChildren : children) {
                new File(dir, aChildren).delete();
            }
        }
    }

    public void storeMessage(String username, JSONObject newMessagesJSONO){
        String userPath = username + "/" + MESSAGES_JSON_FILENAME;
        String data = readFromStorage(userPath);
        if (data == null || data.isEmpty()) data = "[]";
        try {
            JSONArray existingMessagesJSONA = new JSONArray(data);
            existingMessagesJSONA.put(newMessagesJSONO);
            writeToStorageFile(userPath, existingMessagesJSONA.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public JSONArray getContactMessages(String username){
        JSONArray messagesJSONA = new JSONArray();
        String userPath = username + "/" + MESSAGES_JSON_FILENAME;
        String data = readFromStorage(userPath);
        if (data == null || data.isEmpty()) data = "[]";
        try {
            messagesJSONA = new JSONArray(data);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return messagesJSONA;
    }
}