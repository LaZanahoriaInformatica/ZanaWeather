package com.kalderius.agus.zanaweather;

import android.net.ParseException;

import org.xml.sax.InputSource;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

/**
 * Created by Agus on 15/01/2018.
 */

public class ParseadorSAX {
    private InputSource url;


    public ParseadorSAX(URL url) {
        InputSource is = null;
        try {
            HttpURLConnection conexion = (HttpURLConnection) url.openConnection();
            conexion.connect();
            is = new InputSource(conexion.getInputStream());
            is.setEncoding("ISO-8859-1");
            this.url = is;
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void parse()
    {
        SAXParserFactory factory = SAXParserFactory.newInstance();

        try
        {
            SAXParser parser = factory.newSAXParser();
            ManejadorSax manejador = new ManejadorSax();
            parser.parse(this.url, manejador);

        } catch(ParseException ex){
            ex.printStackTrace();
        } catch (Exception e){
            e.printStackTrace();
        }
    }



}
