package com.bda.core;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.AbstractMap;
import java.util.LinkedHashMap;

import org.json.simple.JSONObject;


public class InitialSettings {

    /**
     * Crea el archivo json de configuración
     * @param path  String
     * @throws IOException
     */
    private void CreateConfigFile(String path) throws IOException {
        JSONObject jsonObject = new JSONObject();
        
        LinkedHashMap<String, Object> fields = new LinkedHashMap<>();
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
     * <p>
     * #NOTE: Esto me permite evitar peticiones a la base de datos para requerir preguntas, 
     *  mas si deben estar actualizadas
     * #TODO: Implementar un checksum de preguntas o que cuente los totales
     * @param path  String
     * @throws IOException
     */
    private void CreateQuestionsFile(String path) throws IOException {
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
     * Comprueba si las configuraciones existen y las crea en caso de ser necesario
     * @return AbstractMap.SimpleImmutableEntry<Boolean, String> 
     */
    public AbstractMap.SimpleImmutableEntry<Boolean, String> CheckConfigs(){
        
        boolean correctConfiguration = true;
        String message = "Success!";

        try {
            String configFilePath = LocalVars.filePath.concat("/config.json");
            File configFile = new File(configFilePath);
        
            if(!configFile.exists() && configFile.createNewFile()){
                this.CreateConfigFile(configFilePath);
                message = "Success! - Config file created";
            }

            String questionsFilePath = LocalVars.filePath.concat("/questions.json");
            File questionsfFile = new File(questionsFilePath);
            if(!questionsfFile.exists() && questionsfFile.createNewFile()){
                this.CreateQuestionsFile(questionsFilePath);
                message += " - Question file created";
            }

        } 
        catch (NullPointerException e) {
            correctConfiguration = false;
            message = "Problems opening file \"config.json\"";
        }
        catch(IOException | SecurityException e){
            correctConfiguration = false;
            message = "Problems creating file \"config.json\"";
        }
        catch(Exception e){
            correctConfiguration = false;
            message = String.format("%s: %s", e.getLocalizedMessage(), e.getMessage());
        }

        // NOTE: https://docs.oracle.com/javase/8/docs/api/java/util/AbstractMap.SimpleImmutableEntry.html
        // Es un par de valores que son inmutables, como una tupla de length 2
        return new AbstractMap.SimpleImmutableEntry<>(correctConfiguration, message);
    }
}
