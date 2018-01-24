package com.kalderius.agus.zanaweather;

import android.app.Activity;
import android.os.AsyncTask;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Clase que hace la funcion en segundo plano
 */
public class Internete extends AsyncTask {
    private String id;
    private Tiempo tiempo;
    private MainActivity act;


    public Internete(){
        super();

    }


    public Internete(String id, MainActivity act){
        super();
        this.id = id;
        this.act = act;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Tiempo getTiempo() {
        return tiempo;
    }

    public void setTiempo(Tiempo tiempo) {
        this.tiempo = tiempo;
    }

    /**
     * método que se ejecuta en segundo plano cuando se usa el metodo execute del objeto de esta clase
     * @param objects
     * @return
     */
    @Override
    protected Object doInBackground(Object[] objects) {
        try {
            ParseadorSAX parseador = new ParseadorSAX(new URL("http://www.aemet.es/xml/municipios/localidad_"+this.id+".xml"), this);
            parseador.parse();
            this.act.tiempo = tiempo;
            this.cancel(true);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
