package com.bda.core;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import java.util.AbstractMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedHashMap;

import com.bda.db.ConnectionData;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

// @SuppressWarnings("unchecked")
public class Settings {

    /**
     * Crea el archivo json que contiene las configuraciones del usuario.
     * 
     * @param path String
     * @throws IOException
     */
    @SuppressWarnings("unchecked")
    private static void CreateConfigFile(String path) throws IOException {
        JSONObject jsonObject = new JSONObject();

        LinkedHashMap<String, Object> fields = new LinkedHashMap<>();
        fields.put("host", null);
        fields.put("port", 0);
        fields.put("database", null);
        fields.put("user", null);
        fields.put("password", null);
        jsonObject.put("Database Connection", fields);

        fields = new LinkedHashMap<>();
        fields.put("username", null);
        fields.put("email", null);
        fields.put("user_token", null);
        jsonObject.put("session", fields);

        FileWriter fWriter = new FileWriter(path);
        fWriter.write(jsonObject.toJSONString());
        fWriter.flush();
        fWriter.close();
    }

    /**
     * Crea el archivo json que contiene las preguntas y los temas.
     * 
     * @param path String
     * @throws IOException
     */
    @SuppressWarnings("unchecked")
    private static void CreateQuestionsFile(String path) throws IOException {
        JSONObject jsonObject = new JSONObject();

        LinkedHashMap<String, Object> fields = new LinkedHashMap<>();
        jsonObject.put("topics", fields);
        jsonObject.put("questions", fields);

        FileWriter fWriter = new FileWriter(path);
        fWriter.write(jsonObject.toJSONString());
        fWriter.flush();
        fWriter.close();
    }

    /**
     * Obtiene las configuraciones del archivo config.json en un objeto JSONObject
     * @return JSONObject
     */
    private static JSONObject getConfigs() {
        JSONParser jsonParser = new JSONParser();
        JSONObject configs = null;
        try {
            FileReader fr = new FileReader(LocalVars.filePath.concat("/config.json"));
            Object obj = jsonParser.parse(fr);
            configs = (JSONObject) obj;
        } catch (Exception e) {
            return null;
        }
        return configs;
    }

    /**
     * Escribe el archivo config.json para ajustar las configuraciones del usuario 
     * 
     * @param sessionData Hashtable<String, Object> 
     * @param connectionData ConnectionData
     */
    @SuppressWarnings("unchecked")
    public static void setConfigs(Hashtable<String, Object> sessionData, ConnectionData connectionData){
        JSONObject jsonObject = new JSONObject();
        String path = LocalVars.filePath.concat("/config.json");
        
        LinkedHashMap<String, Object> fields = new LinkedHashMap<String, Object>();

        // Escribiendo en la información de la BD
        ConnectionData currentConnectionData = connectionData != null ? 
            connectionData : Settings.getDBConfigs();
        fields.put("host", currentConnectionData.getHost());
        fields.put("port", currentConnectionData.getPort());
        fields.put("database", currentConnectionData.getDatabase());
        fields.put("user", currentConnectionData.getUser());
        fields.put("password", currentConnectionData.getPassword());
        jsonObject.put("Database Connection", fields);

        // Escribiendo en la información de la sesión
        Hashtable<String, Object> currentSessionData = sessionData != null ? 
            sessionData : Settings.getSessionConfigs();
        fields = new LinkedHashMap<String, Object>();
        fields.put("username", (String) currentSessionData.get("username"));
        fields.put("email", (String) currentSessionData.get("email"));
        fields.put("user_token", (String) currentSessionData.get("user_token"));
        jsonObject.put("session", fields);

        // Borrando el archivo de settings para reescribirlos
        try{
            new File(path).delete(); 
            FileWriter fWriter = new FileWriter(path);
            fWriter.write(jsonObject.toJSONString());
            fWriter.flush();
            fWriter.close();
        } catch (IOException e){
            e.printStackTrace();
            System.exit(-1);
        } catch ( Exception e){
            e.printStackTrace();
            System.exit(-1);
        }
    }

    /**
     * Lee el archivo config.json si este existe y devuelve una Hashtable 
     * con todo el contenido de la sesion del usuario local
     * 
     * @return Hashtable | null
     */
    public static Hashtable<String, Object> getSessionConfigs() {
        // Comprueba y crea un archivo de configuraciones en caso de que no exista
        if (!Settings.CheckConfigs().getKey())
            return null;

        Hashtable<String, Object> sessionConfigs = null;

        try {
            sessionConfigs = Settings.JSONtoHashtable((JSONObject) Settings.getConfigs().get("session"));
        } catch (Exception e) {
            return null;
        }
        return sessionConfigs;
    }

    /**
     * Lee el archivo config.json si este existe y devuelve una Hashtable 
     * con todo el contenido de la configuración de la conexión a la BD
     * 
     * @return ConnectionData | null
     */
    public static ConnectionData getDBConfigs() {
        // Comprueba y crea un archivo de configuraciones en caso de que no exista
        if (!Settings.CheckConfigs().getKey())
            return null;

        ConnectionData connectionData = new ConnectionData();

        try {
            Hashtable<String, Object> dbConfigs = Settings.JSONtoHashtable(
                (JSONObject) Settings.getConfigs().get("Database Connection")
            );
            connectionData.setDatabase( (String) dbConfigs.get("database") );
            long port = (long) dbConfigs.get("port");
            connectionData.setPort((int) port);
            connectionData.setPassword( (String) dbConfigs.get("password") );
            connectionData.setHost( (String) dbConfigs.get("host") );
            connectionData.setUser( (String) dbConfigs.get("user") );
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return connectionData;
    }

    /**
     * Convierte un JSONObject en un Hashtable<String, Object>
     * @param jsonObj JSONObject
     * @return Hashtable<String, Object>
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    private static Hashtable<String, Object> JSONtoHashtable(JSONObject jsonObj) throws Exception {
        Iterator<String> keyIterator = jsonObj.keySet().iterator();
        Hashtable<String, Object> hashTable = new Hashtable<String, Object>();

        while (keyIterator.hasNext()) {
            String keyValue = keyIterator.next();
            Object value = jsonObj.get(keyValue);
            if(value == null)
                value = "";
            hashTable.put(keyValue, value);
        }
        return hashTable;
    }

    /**
     * Comprueba si las configuraciones existen y las crea en caso de ser necesario
     * 
     * @return AbstractMap.SimpleImmutableEntry<Boolean, String>
     */
    public static AbstractMap.SimpleImmutableEntry<Boolean, String> CheckConfigs() {

        boolean correctConfiguration = true;
        String message = "Success!";

        try {
            String configFilePath = LocalVars.filePath.concat("/config.json");
            File configFile = new File(configFilePath);

            if (!configFile.exists() && configFile.createNewFile()) {
                Settings.CreateConfigFile(configFilePath);
                message = "Success! - Config file created";
            }

            String questionsFilePath = LocalVars.filePath.concat("/questions.json");
            File questionsfFile = new File(questionsFilePath);
            if (!questionsfFile.exists() && questionsfFile.createNewFile()) {
                Settings.CreateQuestionsFile(questionsFilePath);
                message += " - Question file created";
            }

        } catch (NullPointerException e) {
            correctConfiguration = false;
            message = "Problems opening file \"config.json\"";
        } catch (IOException | SecurityException e) {
            correctConfiguration = false;
            message = "Problems creating file \"config.json\"";
        } catch (Exception e) {
            correctConfiguration = false;
            message = String.format("%s: %s", e.getLocalizedMessage(), e.getMessage());
        }

        // NOTE:
        // https://docs.oracle.com/javase/8/docs/api/java/util/AbstractMap.SimpleImmutableEntry.html
        // Es un par de valores que son inmutables, como una tupla de length 2
        return new AbstractMap.SimpleImmutableEntry<Boolean, String>(correctConfiguration, message);
    }
}