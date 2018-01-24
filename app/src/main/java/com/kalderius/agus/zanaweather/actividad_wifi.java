package com.kalderius.agus.zanaweather;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Thread.sleep;

public class actividad_wifi extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gestion_wifi);
        //Asignacion de la ListView a una variable
        ListView puntosacceso= (ListView) findViewById(R.id.lista);
        //Creacion del elemento gestor de las redes inalámbricas
        final WifiManager wifimanager= (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        //Rellenamos la lista con los wifi obtenidos del escaneo
        final List<ScanResult> listapuntos=wifimanager.getScanResults();
        //Cogemos la redes obtenida anteriormente y le creamos una lista con sus SSID
        final ArrayList<String> listassid = new ArrayList<>();
        for (ScanResult s : listapuntos) {
            listassid.add(s.SSID);
        }

        //Asignamos la lista a una ListView
        ArrayAdapter adaptador = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,listassid);
        puntosacceso.setAdapter(adaptador);

        //Evento que se realizara al pulsar sobre una red
        puntosacceso.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                configuraconexion(listapuntos.get(i),wifimanager);
            }
        });
    }

    /**
     * Metodo que configura la conexion en base al tipo de encriptacion
     * @param red La red seleccionada
     * @param wifimanager El gestor de wifi
     */
    public void configuraconexion(ScanResult red,WifiManager wifimanager){

        //Creamos una configuracion para asignar la red
        WifiConfiguration wifiConfig = new WifiConfiguration();

        wifiConfig.SSID = String.format("\"%s\"",red.SSID);

        //Evaluamos el tipo de encriptacion y procedemos
        if(Tipodepass(red).equals("OPEN")){
            //Red abierta conecta direcmente
            wifiConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            conectaopen(wifimanager,wifiConfig);
            actividad_wifi.this.finish();
        }
        else{
            //Red con clave procedemos a solicitarla
            dialogoclave(red,wifimanager,wifiConfig);
        }

        //Opcion Emergencia
        //startActivity(new Intent(WifiManager.ACTION_PICK_WIFI_NETWORK));

    }

    /**
     * Evalua el tipo de encriptacion que tiene la red
     * @param scanResult
     * @return
     */
    public String Tipodepass(ScanResult scanResult) {


        final String cap = scanResult.capabilities;
        final String[] tipos = { "WEP", "WPA2","WPA"};
        //Recorre la red evaluando que tipo de encriptacion tiene
        for (int i = 0; i < tipos.length; i++) {
            if (cap.contains(tipos[i])) {
                return tipos[i];
            }
        }

        return "OPEN";
    }

    /**
     * Metodo para mostrar el dialogo para introducir la clave de la red seleccionada
     * @param red La red a conectar
     * @param wifimanager El gestor Wifi
     * @param wifiConfig El objeto que contiene la configuracion de la red
     */
    @SuppressLint("ResourceAsColor")
    public void dialogoclave(final ScanResult red, final WifiManager wifimanager, final WifiConfiguration wifiConfig){
        final EditText edittext = new EditText(getApplicationContext());
        final String[] cla = {""};
        edittext.setBackgroundColor(R.color.colorPrimary);
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("SSID: "+red.SSID);
        alert.setMessage("Introduce Clave:");

        alert.setView(edittext);

        alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //Al pulsar ok en base al tipo de encriptacion realiza una tarea o otra
                switch ( Tipodepass(red)) {
                    case "WEP":
                        //String clavewep="";
                        conectawep(wifimanager, wifiConfig, edittext.getText().toString(),red);
                        break;
                    case "WPA2":
                        conectawpa(wifimanager, wifiConfig, edittext.getText().toString(),red);
                        break;
                    case "WPA":
                        conectawpa(wifimanager, wifiConfig, edittext.getText().toString(),red);
                        break;
                }

            }
        });

        alert.show();
    }

    /**
     * Metodo para conectarse a redes abiertas
     * @param wifimanager
     * @param wifiConfig
     */
    public void conectaopen(WifiManager wifimanager,WifiConfiguration wifiConfig){
        int netId = wifimanager.addNetwork(wifiConfig);
        wifimanager.disconnect();
        wifimanager.enableNetwork(netId, true);
        wifimanager.reconnect();
    }

    /**
     * Metodo para conectarte a redes WPA2 o WPA
     * @param wifimanager
     * @param wifiConfig
     * @param clavewpa
     * @param red
     */
    public void conectawpa(WifiManager wifimanager,WifiConfiguration wifiConfig,String clavewpa,ScanResult red){
        wifiConfig.preSharedKey = String.format("\"%s\"",clavewpa);

        int netId = wifimanager.addNetwork(wifiConfig);
        wifimanager.disconnect();
        wifimanager.enableNetwork(netId, true);
        wifimanager.reconnect();
        //Comprobamos si la conexion a sido correcta sino es asi volvemos a pedir la clave
        if(conectadoWifi()){
            //meter un intent hacia el principio
        }
        else{
            Toast.makeText(actividad_wifi.this,"Error de auntenticacion",Toast.LENGTH_LONG).show();
            wifimanager.removeNetwork(netId);
            dialogoclave(red,wifimanager,wifiConfig);
        }
    }

    /**
     * Metodo para conectarse a WEP
     * @param wifimanager
     * @param wifiConfig
     * @param clavewep
     * @param red
     */
    public void conectawep(WifiManager wifimanager,WifiConfiguration wifiConfig,String clavewep,ScanResult red){
        wifiConfig.wepKeys[0] = "\"" + clavewep + "\"";
        wifiConfig.wepTxKeyIndex = 0;
        wifiConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
        wifiConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);

        int netId = wifimanager.addNetwork(wifiConfig);
        wifimanager.disconnect();
        wifimanager.enableNetwork(netId, true);
        wifimanager.reconnect();
        //Comprobamos si la conexion a sido correcta sino es asi volvemos a pedir la clave
        if(conectadoWifi()){
            //meter un intent hacia el principio
        }
        else{
            Toast.makeText(actividad_wifi.this,"Error de auntenticacion",Toast.LENGTH_LONG).show();
            wifimanager.removeNetwork(netId);
            dialogoclave(red,wifimanager,wifiConfig);
        }
    }

    /**
     * Metodo para comprobar si estamos conectados con exito a la red
     * @return
     */
    private Boolean conectadoWifi(){
        try {
            sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        ConnectivityManager connectivity = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo info = connectivity.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            if (info != null) {
                if (info.isConnected()) {
                    return true;
                }
            }
        }
        return false;
    }



}
