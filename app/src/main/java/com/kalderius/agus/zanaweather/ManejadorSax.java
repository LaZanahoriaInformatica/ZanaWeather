package com.kalderius.agus.zanaweather;


import android.icu.text.DecimalFormat;
import android.icu.text.NumberFormat;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ManejadorSax extends DefaultHandler{
    private Tiempo tiempo;
    private Calendar fecha;
    private String cadena;
    private int contador;
    private int estado;
    private Internete in;
    public final int EMPEZADO = 0;
    public final int TEMPERATURA = 1;
    public final int VIENTO = 2;
    public final int CIELO = 3;
    public final int FINALIZADO = 4;

    public ManejadorSax(Internete in) {
        super();
        this.in = in;
        this.tiempo = new Tiempo();
        cadena = new String();
        fecha = Calendar.getInstance();
    }


    @Override
    public void startDocument() throws SAXException {

    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        cadena = "";
        for (char c :
                ch) {
            cadena = cadena + c;
        }
    }

    @Override
    public void endDocument() throws SAXException {
        this.in.setTiempo(tiempo);
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        String[] trozos;
        switch (localName){
            case "direccion":
                 trozos = cadena.split(" ");
                if(estado == VIENTO)tiempo.setViento(trozos[0]);
                break;
            case "velocidad":
                if(estado == VIENTO){
                     trozos = cadena.split(" ");
                    tiempo.setViento(tiempo.getViento()+trozos[0]+"Km/h");
                    estado = EMPEZADO;
                }
            case "temperatura":
                sacarLinea();
                break;
            case "dia":
                estado = FINALIZADO;
                break;
        }
    }


    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        if (estado != FINALIZADO){
            switch (localName){
                case "dia":
                    DecimalFormat df = new DecimalFormat("00");
                    if(attributes != null && attributes.getValue(0).equalsIgnoreCase(fecha.get(Calendar.YEAR)+"-"+(df.format(fecha.get(Calendar.MONTH)+1))+"-"+df.format(fecha.get(Calendar.DAY_OF_MONTH)))){
                        estado = EMPEZADO;
                    }
                    break;
                case "estado_cielo":
                    if(attributes != null && !attributes.getValue("periodo").equalsIgnoreCase("00-24") &&
                            !attributes.getValue("periodo").equalsIgnoreCase("00-12") &&
                            !attributes.getValue("periodo").equalsIgnoreCase("00-06")){
                        String[] horas = attributes.getValue("periodo").split("-");
                        if((fecha.get(Calendar.HOUR_OF_DAY)+1)>= Integer.valueOf(horas[0]) && (fecha.get(Calendar.HOUR_OF_DAY)+1)<= Integer.valueOf(horas[1]) && estado == EMPEZADO){
                            estado = CIELO;
                            cadena = attributes.getValue("descripcion");
                            sacarLinea();
                            estado = EMPEZADO;
                        }
                    }
                    break;
                case "viento":
                    if(attributes != null && !attributes.getValue("periodo").equalsIgnoreCase("00-24") &&
                            !attributes.getValue("periodo").equalsIgnoreCase("00-12") &&
                            !attributes.getValue("periodo").equalsIgnoreCase("12-24")){
                        String[] horas = attributes.getValue("periodo").split("-");
                        if((fecha.get(Calendar.HOUR_OF_DAY)+1)>= Integer.valueOf(horas[0]) && (fecha.get(Calendar.HOUR_OF_DAY)+1)<= Integer.valueOf(horas[1]) && estado == EMPEZADO){
                            estado = VIENTO;
                        }

                    }
                    break;
                case "dato":
                    if(estado == EMPEZADO){
                        if((fecha.get(Calendar.HOUR_OF_DAY)+1)< 6){
                            if(attributes.getValue("hora").equalsIgnoreCase("06")){
                                estado = TEMPERATURA;
                            }
                        }
                        else{
                            if((fecha.get(Calendar.HOUR_OF_DAY)+1)< 12){
                                if(attributes.getValue("hora").equalsIgnoreCase("12")){
                                    estado = TEMPERATURA;
                                }
                            }
                            else{
                                if((fecha.get(Calendar.HOUR_OF_DAY)+1)< 18){
                                    if(attributes.getValue("hora").equalsIgnoreCase("18")){
                                        estado = TEMPERATURA;
                                    }
                                }
                                else{
                                    if((fecha.get(Calendar.HOUR_OF_DAY)+1)< 24){
                                        if(attributes.getValue("hora").equalsIgnoreCase("24")){
                                            estado = TEMPERATURA;
                                        }
                                    }
                                }
                            }
                        }
                    }

            }
        }

    }

    public void sacarLinea(){
        String[] trozos;
        switch (estado){
            case TEMPERATURA:
                trozos = cadena.split(" ");
                this.tiempo.setTemperatura(trozos[0]+"ºC");
                break;
            case CIELO:
                trozos = cadena.split(" ");
                this.tiempo.setCielo(trozos[0]);
                break;
        }
    }
}
