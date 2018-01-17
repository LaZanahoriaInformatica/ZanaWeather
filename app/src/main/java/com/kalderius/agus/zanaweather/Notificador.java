package com.kalderius.agus.zanaweather;

import android.app.NotificationManager;
import android.content.Context;
import android.support.v4.app.NotificationCompat;


/**
 * Created by ivm19 on 16/01/2018.
 */

public class Notificador {
    private int Temperatura;
    public MainActivity cont;

    public Notificador(int temperatura, MainActivity main) {
        Temperatura = temperatura;
        cont=main;
    }

    public Notificador() {
    }

    public void lanzarNotificacion(){
        if(Temperatura<4){
            NotificationManager notificationManager = (NotificationManager)
                    cont.getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationCompat.Builder notificationBuilder;
            notificationBuilder = new
                    NotificationCompat.Builder(cont.getContext(), "Canal_Notificaciones_1")
                    .setSmallIcon(R.drawable.copo_de_nieve)
                    .setContentTitle("Alerta por Frio")
                    .setContentText("En tu poblacion  hay "+Temperatura+" ºC");
            try{
                notificationManager.notify(1, notificationBuilder.build());
            }
            catch (NullPointerException es){
                es.printStackTrace();
            }

        }else if(Temperatura>=35){

            NotificationManager notificationManager = (NotificationManager)
                    cont.getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationCompat.Builder notificationBuilder = new
                    NotificationCompat.Builder(cont, "CanalCalor")
                    .setSmallIcon(R.drawable.copo_de_nieve)
                    .setContentTitle("Alerta por Calor")
                    .setContentText("En tu poblacion  hay "+Temperatura+" ºC");
            notificationManager.notify(2, notificationBuilder.build());

        }
    }
}
