package com.kalderius.agus.zanaweather;

import android.app.NotificationManager;
import android.content.Context;
import android.support.v4.app.NotificationCompat;


/**
 * Created by ivm19 on 16/01/2018.
 */

public class Notificador { //Clase para mostrar las notificaciones
    private int Temperatura;//Entero para marcar el tipo de notificacion
    public MainActivity cont;//contexto para que puedan lanzarse las notificaciones

    public Notificador(int temperatura, MainActivity main) {//Constructor
        Temperatura = temperatura;
        cont=main;
    }

    public Notificador() {//Constructor vacio
    }

    public void lanzarNotificacion(){//Metodo para lanzar notificacion
        if(Temperatura<4){//Si la temperatura es menor que 4, hace frio
            NotificationManager notificationManager = (NotificationManager)
                    cont.getSystemService(Context.NOTIFICATION_SERVICE);//Se crea el constructor de la notificacion
            NotificationCompat.Builder notificationBuilder;//Se declara la notificacion
            notificationBuilder = new
                    NotificationCompat.Builder(cont.getContext(), "Canal_Notificaciones_1")//Sigue declarando la notificacion
                    .setSmallIcon(R.drawable.copo_de_nieve)//Se declara el icono
                    .setContentTitle("Alerta por Frio")//Se declara el titulo
                    .setContentText("En tu poblacion  hay "+Temperatura+" ºC");//Se declara el contenido de la notificacion
            try{//Evita fallos
                notificationManager.notify(1, notificationBuilder.build());//Manda la notificacion
            }
            catch (NullPointerException es){
                es.printStackTrace();
            }

        }else if(Temperatura>=35){//Si la temperatura es mayor o 35, hace calor

            NotificationManager notificationManager = (NotificationManager)
                    cont.getSystemService(Context.NOTIFICATION_SERVICE);//Se crea el constructor de la notificacion
            NotificationCompat.Builder notificationBuilder = new//Se declara la notificacion
                    NotificationCompat.Builder(cont, "CanalCalor")//Se declaran los valores de la notificacion
                    .setSmallIcon(R.drawable.sol)//Se declara el icono
                    .setContentTitle("Alerta por Calor")//Se declara el titulo
                    .setContentText("En tu poblacion  hay "+Temperatura+" ºC");//Se declara el contenido de la notificacion
            try{//Evita fallos
                notificationManager.notify(2, notificationBuilder.build());//Manda la notificacion
            }
            catch (NullPointerException es){
                es.printStackTrace();
            }
        }
    }
}
