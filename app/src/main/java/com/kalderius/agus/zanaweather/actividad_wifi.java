package com.kalderius.agus.zanaweather;

import android.content.Context;
import android.content.DialogInterface;
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
        final ArrayList<String> listassid = new ArrayList<>();
        for (ScanResult s : listapuntos) {
            listassid.add(s.SSID);
        }

        ArrayAdapter adaptador = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,listassid);
        puntosacceso.setAdapter(adaptador);


        puntosacceso.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                configuraconexion(listapuntos.get(i),wifimanager);
            }
        });
    }

    public void configuraconexion(ScanResult red, WifiManager wifimanager){

        WifiConfiguration wifiConfig = new WifiConfiguration();

        wifiConfig.SSID = String.format("\"%s\"",red.SSID);

        switch ( Tipodepass(red)) {
            case "OPEN":
                //Aqui se configura las propiedades para una red abierta
                wifiConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
                conectaopen(wifimanager,wifiConfig);
                break;
            case "WEP":
                String clavewep="";
                break;
            case "WPA2":
                dialogoclave(red,wifimanager,wifiConfig);
                //wifiConfig.preSharedKey = String.format("\"%s\"",clavewpa);
                //conectawpa(wifimanager,wifiConfig);
                break;
        }
        //dialogoclave(red);
        //clave=dialogoclave2(red);
        //Toast.makeText(this,clave,Toast.LENGTH_SHORT).show();



        //Opcion Emergencia
        //startActivity(new Intent(WifiManager.ACTION_PICK_WIFI_NETWORK));

        Toast.makeText(actividad_wifi.this,""+red.SSID+" "+red.capabilities,Toast.LENGTH_LONG).show();
    }

    public void conectaopen(WifiManager wifimanager,WifiConfiguration wifiConfig){
        int netId = wifimanager.addNetwork(wifiConfig);
        wifimanager.disconnect();
        wifimanager.enableNetwork(netId, true);
        wifimanager.reconnect();
    }
    public void conectawpa(WifiManager wifimanager,WifiConfiguration wifiConfig,String clavewpa){
        wifiConfig.preSharedKey = String.format("\"%s\"",clavewpa);

        int netId = wifimanager.addNetwork(wifiConfig);
        Toast.makeText(actividad_wifi.this,netId+"",Toast.LENGTH_LONG).show();
        wifimanager.disconnect();
        wifimanager.enableNetwork(netId, true);
        wifimanager.reconnect();
    }

    public String Tipodepass(ScanResult scanResult) {


        final String cap = scanResult.capabilities;
        final String[] tipos = { "WEP", "WPA2","WPA" };

        for (int i = 0; i < tipos.length; i++) {
            if (cap.contains(tipos[i])) {
                return tipos[i];
            }
        }

        return "OPEN";
    }

    public void dialogoclave(ScanResult red, final WifiManager wifimanager, final WifiConfiguration wifiConfig){
        final EditText edittext = new EditText(getApplicationContext());
        final String[] cla = {""};
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("SSID: "+red.SSID);
        alert.setMessage("Introduce Clave:");

        alert.setView(edittext);

        alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //cla[0] = edittext.getText().toString();
                conectawpa(wifimanager,wifiConfig,edittext.getText().toString());
            }
        });

        alert.show();
    }
}
