package com.bda.main;

import java.io.File;
import java.util.Scanner;

import com.bda.core.LocalVars;
import com.bda.core.Settings;
import com.bda.db.*;

public class Init {

     /**
      * Crea el schema de la base de datos basándose en un archivo .sql en
      * el directorio path/files del proyecto. Lee el archivo y pregunta línea
      * a línea ejecutar cada instrucción en la BD.
      */
    private static void CreateSchema(){
        try{
            File file = new File(LocalVars.filePath + "/schema.sql");
            Scanner sc = new Scanner(file);
            while (sc.hasNextLine()){
                String currentLine = sc.nextLine();
                if(currentLine.startsWith("--")) continue;
                DB.GetOrUpdatetData(currentLine, true);
            }
            sc.close();
        }
        catch(Exception e){
            e.printStackTrace();
            System.exit(-1);
        }
    }

    /**
     * Inicializa las conexiones a la base de datos y corrige detalles sobre esta
     */
    public static void StartProgramConnections(){
        //Parámetros de conexión a Servidore de BDs
        ConnectionData data = new ConnectionData();
        data.setHost( "127.0.0.1");
        data.setPort(3306);
        data.setDatabase("QA_MARATON");
        data.setUser("root");
        data.setPassword(null);

        // Se escriben parámetros de conexión a la BD en la configuración
        Settings.setConfigs(null, data);

        // Conectando a Servidor de DBs
        DB.getInstance(data);

        if(DB.didCreateDatabase()){
            CreateSchema();
            System.out.println("Schema creado y listo!!!");
        }

        // Cerrando conexión con la BD
        DB.CloseConnection();

        // Reconectar con la BD si ya existen parámetros de configuración
        DB.getInstance(Settings.getDBConfigs());
        
    }
}
