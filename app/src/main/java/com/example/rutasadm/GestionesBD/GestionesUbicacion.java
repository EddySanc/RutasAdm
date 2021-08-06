package com.example.rutasadm.GestionesBD;

import com.example.rutasadm.Clases.Paradas;
import com.example.rutasadm.Clases.Ruta;
import com.example.rutasadm.Clases.Ubicacion;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class GestionesUbicacion {
    private String IP = "http://sigegapp.000webhostapp.com/";//Direccion del hosting donde se alojó nuestro web service
    private String agregar = IP + "paradas_insertar.php";//Noormbre del archivo que hace la función de insertar
    private String obtener = IP + "paradas_obtener.php";
    private String obtener_id = IP + "paradas_obtener_por_id.php";
    private String obtener_ruta = IP + "paradas_obtener_por_ruta.php";
    private String editar = IP + "ubicacion_actualizar.php";
    private String eliminar = IP + "paradas_borrar.php";
    private URL url = null;//Direccion a donde mandaremos la peticion
    private HttpURLConnection urlConnection;

    private ArrayList<Paradas> listaParadas = new ArrayList<>();

    private int id;
    private String descripcion;
    private Double latitud;
    private Double longitud;
    private int ruta;

    public GestionesUbicacion() {



    }



    public boolean Editar(Ubicacion ubicacion){

        try {

            url = new URL(editar);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(true);
            urlConnection.setUseCaches(false);
            urlConnection.setRequestProperty("Content-Type", "application/json");
            urlConnection.setRequestProperty("Accept", "application/json");
            urlConnection.connect();
            //Creo el Objeto JSON
            JSONObject jsonParam = new JSONObject();
            jsonParam.put("colectivo", ubicacion.getColectivo());
            jsonParam.put("latitud", ubicacion.getLatitud());
            jsonParam.put("longitud", ubicacion.getLongitud());


            // Envio los parámetros post.
            OutputStream os = urlConnection.getOutputStream();
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(os, "UTF-8"));
            writer.write(jsonParam.toString());
            writer.flush();
            writer.close();

            int respuesta = urlConnection.getResponseCode();



            StringBuilder result = new StringBuilder();
            System.out.println("result::::::::::"+respuesta);

            if (respuesta == HttpURLConnection.HTTP_OK) {

                String line;
                BufferedReader br=new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                while ((line=br.readLine()) != null) {
                    result.append(line);
                    //response+=line;
                }

                System.out.println("result::::::::::"+result);

                //Creamos un objeto JSONObject para poder acceder a los atributos (campos) del objeto.
                JSONObject respuestaJSON = new JSONObject(result.toString());   //Creo un JSONObject a partir del StringBuilder pasado a cadena
                //Accedemos al vector de resultados

                String resultJSON = respuestaJSON.getString("estado");   // estado es el nombre del campo en el JSON

                if (resultJSON.equals("1")) {      // hay un alumno que mostrar
                    return true;

                } else {
                    return false;
                }

            }

        } catch (MalformedURLException e) {
            System.out.println(e.toString());
        } catch (IOException e) {
            System.out.println(e.toString());
        } catch (JSONException e) {
            System.out.println(e.toString());
        }

        return false;
    }


}
