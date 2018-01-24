package com.kalderius.agus.zanaweather;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
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
        AlertDialog.Builder builderwifi;
        final Intent i=new Intent(main,actividad_wifi.class);
        if(poblacion==null){
            Toast.makeText(main,"No hay población seleccionada. Seleccione una.",Toast.LENGTH_SHORT).show();
        }else{
            if (wifiManager.isWifiEnabled()){
                if (wifiManager.getWifiState()==WifiManager.WIFI_STATE_ENABLED){
                    recogerTemp();
                }else{
                    startActivity(i);
                }
            }else{
                builderwifi= new AlertDialog.Builder(main);
                builderwifi.setMessage("¿Quierés activar el uso de WIFI?");
                builderwifi.setPositiveButton("Si", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int j) {
                        wifiManager.setWifiEnabled(true);
                        if (wifiManager.getWifiState()==WifiManager.WIFI_STATE_ENABLED){
                            recogerTemp();
                        }else{

                            startActivity(i);
                        }
                    }
                });
                builderwifi.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        main.finish();
                    }
                });
                AlertDialog dialog= builderwifi.create();
                dialog.show();

            }
        }
    }
    public void recogerTemp(){
        Internete internete=new Internete(poblacion.getId(), this);
        internete.execute();
        //Solución para esperar que se acabe de recoger la información
        while(tiempo == null){

        }
        temp=tiempo.getTemperatura();
        Notificador noti = new Notificador(Integer.parseInt(temp), this);
        //Si la temperatura es menor a 4 o mayor que 35 se llama a la clase que lanza las notificaciones.
        if(Integer.parseInt(temp)< 4 || Integer.parseInt(temp) >= 35) noti.lanzarNotificacion();
        viento=tiempo.getViento();
        cielo=tiempo.getCielo();
        tvTemp.setText(temp+"ºC");
        tvCielo.setText(cielo);
        tvViento.setText(viento);
        //Se resetea el tiempo para evitar errores con el bucle.
        tiempo = null;

    }

}
