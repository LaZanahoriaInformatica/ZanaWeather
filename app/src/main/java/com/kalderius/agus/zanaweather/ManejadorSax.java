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
    private boolean candado;
    private int estado;
    private Internete in;
    public final int EMPEZADO = 0;
    public final int TEMPERATURA = 1;
    public final int VIENTO = 2;
    public final int CIELO = 3;
    public final int DATOTEMP = 5;
    public final int FINALIZADO = 4;

    public ManejadorSax(Internete in) {
        super();
        candado = true;
        this.in = in;
        this.tiempo = new Tiempo();
        cadena = new String();
        fecha = Calendar.getInstance();
    }

    /**
     * Metodo que se ejecuta cuando se esta parseando el contenido de una etiqueta
     * @param ch
     * @param start
     * @param length
     * @throws SAXException
     */
    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        cadena = "";
        //Para cada caracter dentro de la cadena de texto
        for (char c :
                ch) {
            cadena = cadena +" "+ c;
        }
    }

    @Override
    public void endDocument() throws SAXException {
        //Cuando acabe el documento se setea el tiempo de la clase principal
        this.in.setTiempo(tiempo);
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        String[] trozos;
        switch (localName){
            case "direccion":
                 trozos = cadena.split(" ");
                if(estado == VIENTO && candado)tiempo.setViento(trozos[1]);
                break;
            case "velocidad":
                if(estado == VIENTO && candado){
                     trozos = cadena.split(" ");
                    tiempo.setViento(tiempo.getViento()+" "+trozos[1]+"Km/h");
                    candado = false;
                }
            case "dato":
                if (estado== DATOTEMP) {
                    sacarLinea();
                }

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
                    //Si la etiqueta se llama estado_cielo
                case "estado_cielo":
                    //Si el intervalo de horas se encuentra fuera de los grupos grandes de horas
                    if(attributes != null && !attributes.getValue("periodo").equalsIgnoreCase("00-24") &&
                            !attributes.getValue("periodo").equalsIgnoreCase("00-12") &&
                            !attributes.getValue("periodo").equalsIgnoreCase("00-06")){
                        String[] horas = attributes.getValue("periodo").split("-");
                        //Si la hora coincide con la hora del equipo
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
                case "temperatura":
                    //Si viene de leer el viento se cambia el estado a temperatura
                    if(estado == VIENTO) estado = TEMPERATURA;
                    break;
                case "dato":
                    if(estado == TEMPERATURA){
                        //Si el estado es temperatura y la hora coincide con la hora actual se cambia el estado para coger el dato

                        if((fecha.get(Calendar.HOUR_OF_DAY)+1)< 6){
                            if(attributes.getValue("hora").equalsIgnoreCase("06")){
                                estado = DATOTEMP;
                            }
                        }
                        else{
                            if((fecha.get(Calendar.HOUR_OF_DAY)+1)< 12){
                                if(attributes.getValue("hora").equalsIgnoreCase("12")){
                                    estado = DATOTEMP;
                                }
                            }
                            else{
                                if((fecha.get(Calendar.HOUR_OF_DAY)+1)< 18){
                                    if(attributes.getValue("hora").equalsIgnoreCase("18")){
                                        estado = DATOTEMP;
                                    }
                                }
                                else{
                                    if((fecha.get(Calendar.HOUR_OF_DAY)+1)< 24){
                                        if(attributes.getValue("hora").equalsIgnoreCase("24")){
                                            estado = DATOTEMP;
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
            case DATOTEMP:
                //Si se puede coger el dato de la temperatura se hace un split de la linea ya que viene con palabras extra
                trozos = cadena.split(" ");
                try{
                    this.tiempo.setTemperatura(trozos[1]+Integer.parseInt(trozos[2]));
                }
                catch(NumberFormatException ex){
                    this.tiempo.setTemperatura(trozos[1]);
                }
                estado = EMPEZADO;

                break;
            case CIELO:
                this.tiempo.setCielo(cadena);
                break;
        }
    }
}
