package com.kalderius.agus.zanaweather;

import android.os.AsyncTask;


public class Internete extends AsyncTask {
    private String id;
    private String temperatura;
    private String viento;
    private String cielo;


    public Internete(){
        super();

    }

    public Internete(String id){
        super();
        this.id = id;
    }
    @Override
    protected Object doInBackground(Object[] objects) {


        return null;
    }
}
