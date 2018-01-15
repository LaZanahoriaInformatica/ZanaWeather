package com.kalderius.agus.zanaweather;


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
    public final int EMPEZADO = 0;
    public final int TEMPERATURA = 1;
    public final int VIENTO = 2;
    public final int CIELO = 3;
    public final int FINALIZADO = 4;

    public ManejadorSax() {
        super();
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
    public void endElement(String uri, String localName, String qName) throws SAXException {
        switch (localName){
            case "direccion":
                if(estado == VIENTO)tiempo.setViento(cadena);
                break;
            case "velocidad":
                if(estado == VIENTO){
                    tiempo.setViento(tiempo.getViento()+cadena+"Km/h");
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
        switch (localName){
            case "dia":
                if(attributes.getValue(0).equalsIgnoreCase(fecha.get(Calendar.YEAR)+"-"+(fecha.get(Calendar.MONTH)+1)+"-"+fecha.get(Calendar.DAY_OF_MONTH))){
                    estado = EMPEZADO;
                }
                break;
            case "estado_cielo":
                if(!attributes.getValue("periodo").equalsIgnoreCase("00-24") &&
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
                if(!attributes.getValue("periodo").equalsIgnoreCase("00-24") &&
                        !attributes.getValue("periodo").equalsIgnoreCase("00-12") &&
                        !attributes.getValue("periodo").equalsIgnoreCase("12-24")){
                    String[] horas = attributes.getValue("periodo").split("-");
                    if((fecha.get(Calendar.HOUR_OF_DAY)+1)>= Integer.valueOf(horas[0]) && (fecha.get(Calendar.HOUR_OF_DAY)+1)<= Integer.valueOf(horas[1]) && estado == EMPEZADO){
                        estado = VIENTO;
                    }

                }
                break;
            case "temperatura":
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

    public void sacarLinea(){
        switch (estado){
            case TEMPERATURA:
                this.tiempo.setTemperatura(cadena+"ºC");
                break;
            case CIELO:
                this.tiempo.setCielo(cadena);
                break;
        }
    }
}
