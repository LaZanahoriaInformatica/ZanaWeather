package com.kalderius.agus.zanaweather;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class MainActivity extends AppCompatActivity {
TextView tvTitulo;
TextView tvPoblacion;
Spinner spPoblaciones;
TextView tvTemp;
TextView tvViento;
TextView tvCielo;
Button btnOK;
ArrayAdapter<String> adapter;
MainActivity main=this;
List<Poblacion> listaPoblaciones;
String ruta="poblaciones.txt";
Poblacion poblacion;
WifiManager wifiManager;
String temp,viento,cielo;
Tiempo tiempo;

    public Context getContext() {
        return MainActivity.this;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        wifiManager= (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        spPoblaciones=(Spinner) findViewById(R.id.spPoblaciones);
        btnOK=(Button) findViewById(R.id.btnOK);
        tvCielo=(TextView) findViewById(R.id.tvCielo);
        tvPoblacion=(TextView) findViewById(R.id.tvPoblacion);
        tvTemp= (TextView) findViewById(R.id.tvTemp);
        tvViento = findViewById(R.id.tvViento);
        adapter=new ArrayAdapter(main,R.layout.support_simple_spinner_dropdown_item);
        listaPoblaciones=ES_F.leerFichero(ruta,main);
        for (int i=0;i<listaPoblaciones.size();i++){
            poblacion=listaPoblaciones.get(i);
            adapter.add(poblacion.getNombre());
        }
        spPoblaciones.setAdapter(adapter);
        spPoblaciones.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                poblacion=listaPoblaciones.get(i);

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                poblacion=null;
            }
        });
        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                comprobarDatos();

            }
        });

    }
    public void comprobarDatos(){
        if(poblacion==null){
            Toast.makeText(main,"No hay población seleccionada. Seleccione una.",Toast.LENGTH_SHORT).show();
        }else{
            if (wifiManager.isWifiEnabled()){
                if (wifiManager.getWifiState()==WifiManager.WIFI_STATE_ENABLED){
                    recogerTemp();
                }else{
                    Intent i=new Intent(main,actividad_wifi.class);
                    startActivity(i);
                }
            }else{
                wifiManager.setWifiEnabled(true);
                if (wifiManager.getWifiState()==WifiManager.WIFI_STATE_ENABLED){
                    recogerTemp();
                }else{
                    Intent i=new Intent(main,actividad_wifi.class);
                    startActivity(i);
                }

            }
        }
    }
    public void recogerTemp(){
        Internete internete=new Internete(poblacion.getId(), this);
        internete.execute();
        while(!internete.isCancelled()){

        }
        tiempo = internete.getTiempo();

        //temp=tiempo.getTemperatura();
        temp = "2";
        Notificador noti = new Notificador(Integer.parseInt(temp), this);
        if(Integer.parseInt(temp)< 4 || Integer.parseInt(temp) >= 35) noti.lanzarNotificacion();
        viento=tiempo.getViento();
        cielo=tiempo.getCielo();
        tvTemp.setText(temp+"ºC");
        tvCielo.setText(cielo);
        tvViento.setText(viento);

    }

}
