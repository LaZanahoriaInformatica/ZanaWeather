package com.kalderius.agus.zanaweather;

import android.app.Activity;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by MIGUE on 16/01/2018.
 */

public class ES_F {

    public static List leerFichero(String ruta, Activity act){
        // Declaramos todo lo necesario para leer el fichero
        ArrayList<Poblacion> listapoblaciones=new ArrayList<Poblacion>();
        BufferedReader bfr = null;
        List lista=null;
        InputStream flujo=null;
        BufferedReader lector=null;
        try {

            //Creamos un flujo de dato del fichero que se encuentra en RES
            flujo= act.getResources().openRawResource(R.raw.poblaciones);
            bfr = new BufferedReader(new InputStreamReader(flujo));
            lista = new ArrayList();
            String linea = new String();
            //Recorremos el fichero leyendo los valores del mismo
            while((linea = bfr.readLine()) != null){
                lista.add(linea);
            }
            bfr.close();
            flujo.close();


            //Procedemos a trabajar los valores de la lista que obtenemos de la lista para darle un formato util
            for (Object valor:lista) {
                String pob= (String) valor;
                //Separamos lo valores por ; para coger solo lo que necesitemos
                String[] cadena=pob.split(";");
                String id=cadena[1]+cadena[2];
                //Dado que tenemos nombre en 2 idiomas separamos y usamos solo 1
                String nombretemp=cadena[4].split("/")[0];
                String nombre="";
                //Hay nombres que estan al reves separados por , asi pues los invierto
                if(nombretemp.contains(",")){
                    String[] cadnom=nombretemp.split(",");
                    nombre=cadnom[1]+" "+cadnom[0];
                }
                else{
                    nombre=nombretemp;
                }
                //Creamos la poblacion y la añadimos a la lista
                listapoblaciones.add(new Poblacion(id,nombre));

            }

        } catch (FileNotFoundException ex) {
            System.err.println("Error: No se encuentra el archivo.");
        } catch (IOException ex) {
            System.err.println("Error de Entrada/Salida.");
        }

        return listapoblaciones;
    }
}
