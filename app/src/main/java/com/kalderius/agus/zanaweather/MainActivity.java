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
Spinner spPoblaciones; // lista desplegable de las poblaciones disponibles sobre las que obtener datos meteorológicos
TextView tvTemp; // Texto referente a la temperatura de la poblacion seleccionada
TextView tvViento; // Texto referente a la direccion y velocidad del viento de la poblacion seleccionada
TextView tvCielo; // Texto referente al temporal de la poblacion seleccionada
Button btnOK; // Botón que devuelve los datos meteorológico de la población seleccionada tras pulsarlo
ArrayAdapter<String> adapter; // Adapter del spinner que contiene las poblaciones para mostrar en la lista desplegable del spinner
MainActivity main=this; // Referencia a esta actividad, la actividad principal
List<Poblacion> listaPoblaciones; // Lista de objetos poblacion con el nombre y el identificador usado para recoger datos meteorológicos
String ruta="poblaciones.txt"; // Fichero de texto que contiene las poblaciones disponibles con su identificador
Poblacion poblacion; //Objeto población que recoger la población seleccionada
WifiManager wifiManager; // Clase encargada de lo referente al servicio de wifi
String temp,viento,cielo; // Strings de viento, temperatura y cielo que se mostraran en los textView referenciados previamente
Tiempo tiempo; // Objeto tiempo que contiene los datos meteorológicos de la población seleccionada tras pulsar el boton

    public Context getContext() {
        return MainActivity.this;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);// Se referencia el layout del activity principal
        wifiManager= (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE); // referencia al servicio wifi del dispositivo
        spPoblaciones=(Spinner) findViewById(R.id.spPoblaciones);
        //Referencias a componentes del layout para darles funcionalidad
        btnOK=(Button) findViewById(R.id.btnOK);
        tvCielo=(TextView) findViewById(R.id.tvCielo);
        tvPoblacion=(TextView) findViewById(R.id.tvPoblacion);
        tvTemp= (TextView) findViewById(R.id.tvTemp);
        tvViento = findViewById(R.id.tvViento);

        adapter=new ArrayAdapter(main,R.layout.support_simple_spinner_dropdown_item);// Constructor del adapter en el que entran como parametros la actividad principal y el tipo de item que mostrara
        listaPoblaciones=ES_F.leerFichero(ruta,main); // Llamada al método leerFichero(Ruta,actividad) que devuelve una lista de poblaciones procedentes de la ruta para usar en el adapter
        // Bucle que rellena el adapter con los registros de listaPoblaciones
        for (int i=0;i<listaPoblaciones.size();i++){
            poblacion=listaPoblaciones.get(i);
            adapter.add(poblacion.getNombre());
        }
        //Se referencia al adapter del spinner
        spPoblaciones.setAdapter(adapter);
        spPoblaciones.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            /**
             * Método lanzado cuando se selecciona algo en el spinner
             * @param adapterView
             * @param view
             * @param i
             * @param l
             */
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                poblacion=listaPoblaciones.get(i);

            }

            /**
             * Método lanzado al no seleccionar nada en el spinner
             * @param adapterView
             */
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
        });// Método lanzado tras pulsar el bóton ok y que lanzara el método comprobarDatos()

    }

    /**
     * Este método comprueba que se tenga activado el servicio de wifi y se este conectado a una red, si no esta activado el servicio
     * se pedirá permiso al usuario de activarlo, si dice que no se cerrara la aplicacion , y si dice que si pedira seleccionar una conexion
     * en caso de que no se conecte automáticamente a una. Tras esto revisa si hay una poblacion seleccionada y lanza el método para recoger
     * los datos en caso de que si se haya seleccionado una
     */
    public void comprobarDatos(){
        AlertDialog.Builder builderwifi;
        final Intent i=new Intent(main,actividad_wifi.class);//Intent a la ventana con las conexiones disponibles
        if(poblacion==null){
            Toast.makeText(main,"No hay población seleccionada. Seleccione una.",Toast.LENGTH_SHORT).show();//Texto que se muestra cuando no se ha seleccionado una poblacion
        }else{
            //si el wifi esta activado
            if (wifiManager.isWifiEnabled()){
                //si el dispositivo esta conectado a una red wifi
                if (wifiManager.getWifiState()==WifiManager.WIFI_STATE_ENABLED){
                    recogerTemp();
                //sino..
                }else{
                    startActivity(i);
                }
            //sino..
            }else{
                //Generamos dialogo que pedira permiso al usuario para activar el servicio de Wifi
                builderwifi= new AlertDialog.Builder(main);
                builderwifi.setMessage("¿Quierés activar el uso de WIFI?");
                builderwifi.setPositiveButton("Si", new DialogInterface.OnClickListener() {
                    //Boton de aceptar que activara el servicio wifi y lanzara el activity de conexiones disponibles si no se esta conectado a una
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
                    //Botón de denegar que cerrará la aplicación
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        main.finish();
                    }
                });
                AlertDialog dialog= builderwifi.create();
                dialog.show();//muestra el dialogo

            }
        }
    }

    /**
     * Metodo lanzado para conseguir los datos de la población seleccionada recuperando un archivo xml con la prediccion meteorológica semanal de la poblacion.
     * El documento xml se parsea de manera que de devuelve un objeto tipo tiempo referente a la poblacion seleccionada
     */

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
