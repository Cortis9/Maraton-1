package com.bda.main;

import java.util.AbstractMap;

import com.bda.core.InitialSettings;
import com.bda.core.LocalVars;
import com.bda.db.DB;
import com.bda.ui.MainFrame;

public class Main {
    public static void main(String[] args) {
        DB.getInstance(
            "127.0.0.1", 
            3306, 
            "QA_MARATON", 
            "root", 
            null
        );

        AbstractMap.SimpleImmutableEntry m = new InitialSettings().CheckConfigs();
        System.out.println(m.getKey());
        System.out.println(m.getValue());

        // TODO: Hacer la interfaz gráfica 
        // TODO: Hacer la configuración inicial
        // TODO: Hacer la planificación de la sincronización de la app y servidor de bd
    }
}
