package com.kalderius.agus.zanaweather;

import android.os.AsyncTask;

import java.net.MalformedURLException;
import java.net.URL;


public class Internete extends AsyncTask {
    private String id;
    private Tiempo tiempo;


    public Internete(){
        super();

    }



    public Internete(String id){
        super();
        this.id = id;

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

    @Override
    protected Object doInBackground(Object[] objects) {

        try {
            ParseadorSAX parseador = new ParseadorSAX(new URL("http://www.aemet.es/xml/municipios/localidad_"+this.id+".xml"));
            parseador.parse();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return null;
    }
}
