package com.kalderius.agus.zanaweather;

import android.net.ParseException;

import org.xml.sax.InputSource;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;


public class ParseadorSAX {
    private InputSource url;
    private Internete in;


    public ParseadorSAX(URL url, Internete in) {
        InputSource is = null;
        try {
            this.in = in;
            //Creo una conexion y la abro
            HttpURLConnection conexion = (HttpURLConnection) url.openConnection();
            conexion.connect();
            //Cogemos el Stream de entrada de la conexion y le seteamos el sistema de caracteres para que lea las tildes
            is = new InputSource(conexion.getInputStream());
            is.setEncoding("ISO-8859-1");
            this.url = is;
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * Método que crea una instancia del parseador y llama a la clase que parsea el documento
     */
    public void parse()
    {
        SAXParserFactory factory = SAXParserFactory.newInstance();

        try
        {
            //Creamos una nueva instancia del parseador y parseamos los datos del Stream de entrada.
            SAXParser parser = factory.newSAXParser();
            ManejadorSax manejador = new ManejadorSax(in);
            parser.parse(this.url, manejador);


        } catch(ParseException ex){
            ex.printStackTrace();
        } catch (Exception e){
            e.printStackTrace();
        }
    }



}
