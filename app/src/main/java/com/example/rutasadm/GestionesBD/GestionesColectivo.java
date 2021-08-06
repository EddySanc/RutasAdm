package com.example.rutasadm.GestionesBD;

import com.example.rutasadm.Clases.Colectivo;
import com.example.rutasadm.Clases.Ruta;

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

public class GestionesColectivo {

    private String IP = "http://sigegapp.000webhostapp.com/";//Direccion del hosting donde se alojó nuestro web service
    private String agregar = IP + "colectivo_insertar.php";//Noormbre del archivo que hace la función de insertar
    private String obtener = IP + "colectivo_obtener.php";
    private String obtener_libres = IP + "ruta_obtener.php";
    private String obtener_id = IP + "colectivo_obtener_por_id.php";
    private String obtener_numero = IP + "colectivo_obtener_por_descripcion.php";
    private String editar = IP + "colectivo_actualizar.php";
    private String eliminar = IP + "colectivo_borrar.php";
    private URL url = null;//Direccion a donde mandaremos la peticion
    private HttpURLConnection urlConnection;

    private ArrayList<Colectivo> listacolectivo = new ArrayList<>();

    private int id;
    private String num_econom;
    private String placa;
    private String descripcion;
    private String agregado;
    private String modificado;
    private int ruta;


    public GestionesColectivo() {
    }


    public ArrayList<Colectivo> ConsultaGral(){

        try {
            url = new URL(obtener);

            System.out.println(obtener);

            HttpURLConnection connection = (HttpURLConnection) url.openConnection(); //Abrir Conexion
            connection.setRequestProperty("User-Agent", "Mozilla/5.0" +
                    " (Linux; Android 1.5; es-ES) Ejemplo HTTP");

            int respuesta = connection.getResponseCode();
            StringBuilder result = new StringBuilder();


            if (respuesta == HttpURLConnection.HTTP_OK) {

                InputStream in = new BufferedInputStream(connection.getInputStream());//Preparando la cadena de entrada

                BufferedReader reader = new BufferedReader(new InputStreamReader(in));

                String line;
                while ((line = reader.readLine()) != null) {
                    result.append(line);



                    System.out.println(result);

                    //Creamos un objeto JSONObject para poder acceder a los atributos del objeto
                    JSONObject respuestaJSON = new JSONObject(result.toString());
                    //Acceder al vector de resultados
                    String resultJSON = respuestaJSON.getString("estado");


                    if (resultJSON.equals("1")) {

                        JSONArray rutaJSON = respuestaJSON.getJSONArray("colectivo");

                        for (int i = 0; i < rutaJSON.length(); i++) {


                            id = rutaJSON.getJSONObject(i).getInt("id");
                            num_econom = rutaJSON.getJSONObject(i).getString("num_econom");

                            placa = rutaJSON.getJSONObject(i).getString("placa");

                            descripcion = rutaJSON.getJSONObject(i).getString("descripcion");

                            agregado = rutaJSON.getJSONObject(i).getString("agregado");

                            modificado = rutaJSON.getJSONObject(i).getString("modificado");

                            ruta = rutaJSON.getJSONObject(i).getInt("ruta");

                            listacolectivo.add(new Colectivo(id,num_econom,placa,descripcion,agregado,modificado,ruta,""));

                        }

                        return listacolectivo;
                    } else {

                        return listacolectivo;
                    }

                }

            }


        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return listacolectivo;
    }

    public ArrayList<Ruta> ConsultaRutaLibres(){

        ArrayList<Ruta> rutasLibres = new ArrayList<>();

        try {
            url = new URL(obtener_libres);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection(); //Abrir Conexion
            connection.setRequestProperty("User-Agent", "Mozilla/5.0" +
                    " (Linux; Android 1.5; es-ES) Ejemplo HTTP");

            int respuesta = connection.getResponseCode();
            StringBuilder result = new StringBuilder();
            System.out.println("Here"+connection.getResponseMessage());

            if (respuesta == HttpURLConnection.HTTP_OK) {
                InputStream in = new BufferedInputStream(connection.getInputStream());//Preparando la cadena de entrada

                BufferedReader reader = new BufferedReader(new InputStreamReader(in));

                String line;
                while ((line = reader.readLine()) != null) {
                    result.append(line);


                    //Creamos un objeto JSONObject para poder acceder a los atributos del objeto
                    JSONObject respuestaJSON = new JSONObject(result.toString());
                    //Acceder al vector de resultados
                    String resultJSON = respuestaJSON.getString("estado");


                    if (resultJSON.equals("1")) {


                        JSONArray rutaJSON = respuestaJSON.getJSONArray("ruta");

                        for (int i = 0; i < rutaJSON.length(); i++) {

                            id = rutaJSON.getJSONObject(i).getInt("id");
                            int num_ruta = rutaJSON.getJSONObject(i).getInt("num_ruta");
                            String descripcion = rutaJSON.getJSONObject(i).getString("descripcion");

                            rutasLibres.add(new Ruta(id,num_ruta,descripcion,"",""));

                        }

                        return rutasLibres;
                    } else {

                        return rutasLibres;
                    }

                }

            }


        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return rutasLibres;
    }

    public ArrayList<Colectivo> ConsultaPorId(int idColectivo){

        try {
            url = new URL(obtener_id+"?id="+idColectivo);
            System.out.println(":::::::::::::::::::::::::::"+url);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection(); //Abrir la conexión
            connection.setRequestProperty("User-Agent", "Mozilla/5.0" +
                    " (Linux; Android 1.5; es-ES) Ejemplo HTTP");
            //connection.setHeader("content-type", "application/json");

            int respuesta = connection.getResponseCode();
            StringBuilder result = new StringBuilder();

            if (respuesta == HttpURLConnection.HTTP_OK){


                InputStream in = new BufferedInputStream(connection.getInputStream());  // preparo la cadena de entrada

                BufferedReader reader = new BufferedReader(new InputStreamReader(in));  // la introduzco en un BufferedReader

                // El siguiente proceso lo hago porque el JSONOBject necesita un String y tengo
                // que tranformar el BufferedReader a String. Esto lo hago a traves de un
                // StringBuilder.

                String line;
                while ((line = reader.readLine()) != null) {
                    result.append(line);        // Paso toda la entrada al StringBuilder
                }

                System.out.println(":::::::::::::::::::::::::::::"+result);
                //Creamos un objeto JSONObject para poder acceder a los atributos (campos) del objeto.
                JSONObject respuestaJSON = new JSONObject(result.toString());   //Creo un JSONObject a partir del StringBuilder pasado a cadena
                //Accedemos al vector de resultados

                String resultJSON = respuestaJSON.getString("estado");   // estado es el nombre del campo en el JSON

                if (resultJSON.equals("1")){      // hay un alumno que mostrar

                    id = respuestaJSON.getJSONObject("colectivo").getInt("id");
                    num_econom = respuestaJSON.getJSONObject("colectivo").getString("num_econom");
                    placa = respuestaJSON.getJSONObject("colectivo").getString("placa");
                    descripcion = respuestaJSON.getJSONObject("colectivo").getString("descripcion");
                    agregado = respuestaJSON.getJSONObject("colectivo").getString("agregado");
                    modificado = respuestaJSON.getJSONObject("colectivo").getString("modificado");
                    ruta = respuestaJSON.getJSONObject("colectivo").getInt("r");
                    String ruta_descripcion = respuestaJSON.getJSONObject("colectivo").getString("d");
                    listacolectivo.add(new Colectivo(id,num_econom,placa,descripcion,agregado,modificado,ruta,ruta_descripcion));

                    return listacolectivo;

                }
                else{
                    return listacolectivo;
                }

            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return listacolectivo;

    }

    public ArrayList<Colectivo> ConsultaPorDescripcion(String descripcion){

        try {
            url = new URL(obtener_numero+"?descripcion="+descripcion);
            System.out.println(":::::::::::::::::::::::::::"+url);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection(); //Abrir la conexión
            connection.setRequestProperty("User-Agent", "Mozilla/5.0" +
                    " (Linux; Android 1.5; es-ES) Ejemplo HTTP");
            //connection.setHeader("content-type", "application/json");

            int respuesta = connection.getResponseCode();
            StringBuilder result = new StringBuilder();

            if (respuesta == HttpURLConnection.HTTP_OK){


                InputStream in = new BufferedInputStream(connection.getInputStream());  // preparo la cadena de entrada

                BufferedReader reader = new BufferedReader(new InputStreamReader(in));  // la introduzco en un BufferedReader

                // El siguiente proceso lo hago porque el JSONOBject necesita un String y tengo
                // que tranformar el BufferedReader a String. Esto lo hago a traves de un
                // StringBuilder.

                String line;
                while ((line = reader.readLine()) != null) {
                    result.append(line);        // Paso toda la entrada al StringBuilder
                }

                //Creamos un objeto JSONObject para poder acceder a los atributos (campos) del objeto.
                JSONObject respuestaJSON = new JSONObject(result.toString());   //Creo un JSONObject a partir del StringBuilder pasado a cadena
                //Accedemos al vector de resultados

                String resultJSON = respuestaJSON.getString("estado");   // estado es el nombre del campo en el JSON

                if (resultJSON.equals("1")){      // hay un alumno que mostrar

                    JSONArray rutaJSON = respuestaJSON.getJSONArray("colectivo");


                    for (int i = 0; i < rutaJSON.length(); i++) {

                        id = rutaJSON.getJSONObject(i).getInt("id");
                        num_econom = rutaJSON.getJSONObject(i).getString("num_econom");

                        placa = rutaJSON.getJSONObject(i).getString("placa");

                        descripcion = rutaJSON.getJSONObject(i).getString("descripcion");

                        agregado = rutaJSON.getJSONObject(i).getString("agregado");

                        modificado = rutaJSON.getJSONObject(i).getString("modificado");

                        ruta = rutaJSON.getJSONObject(i).getInt("ruta");

                        listacolectivo.add(new Colectivo(id,num_econom,placa,descripcion,agregado,modificado,ruta,""));

                    }
                    return listacolectivo;

                }
                else{
                    return listacolectivo;
                }

            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return listacolectivo;

    }

    public boolean Agregar(Colectivo colectivo) {

        try {

            url = new URL(agregar);//Teminamos de crear el oobjeto, mandando la ruta del servicio
            urlConnection = (HttpURLConnection) url.openConnection();//Abrimos la conexion.
            //Configuramos nuestro urlConnection con las caracteristicas necesarias
            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(true);
            urlConnection.setUseCaches(false);
            urlConnection.setRequestProperty("Content-Type", "application/json");
            urlConnection.setRequestProperty("Accept", "application/json");
            //finalmente hacemos la conexion
            urlConnection.connect();

            //Se contruye el objeto de tipo Json
            JSONObject jsonParam = new JSONObject();
            jsonParam.put("num_econom", colectivo.getNum_econom());
            jsonParam.put("placa", colectivo.getPlaca());
            jsonParam.put("descripcion", colectivo.getDescripcion());
            jsonParam.put("ruta", colectivo.getRuta());
            //Envío los parametros al post de PHP
            OutputStream outputStream = urlConnection.getOutputStream();//Solicitamos la salida de datos al servidor
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
            writer.write(jsonParam.toString());//Escribimos los parametros dentro del post
            writer.flush();
            writer.close();//Cierre del writer, recomendado

            int respuestaConexion = urlConnection.getResponseCode();//Obtenemos la respuesta

            StringBuilder result = new StringBuilder(); //almacenará el resultado

            if (respuestaConexion == HttpURLConnection.HTTP_OK) {

                String line;
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                while ((line = bufferedReader.readLine()) != null) {//Recorremos el buffer y verificamos si aun existen datos
                    result.append(line);//Almacenamos por linea de respuestas
                }

                System.out.println(":::::::::::::::::::"+result);
                //Creamos un objeto de tipo Json para poder leer la inf obtenida

                JSONObject jsonObject = new JSONObject(result.toString());
                String resultadoJson = jsonObject.getString("estado");//Estado es el nombre del campo en el Json de retorno

                if (resultadoJson.equals("1")) {
                    return true;
                } else {
                    return false;
                }

            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return false;

    }

    public boolean Editar(Colectivo colectivo){

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
            jsonParam.put("id", colectivo.getId());
            jsonParam.put("num_econom", colectivo.getNum_econom());
            jsonParam.put("placa", colectivo.getPlaca());
            jsonParam.put("descripcion", colectivo.getDescripcion());
            jsonParam.put("ruta", colectivo.getRuta());


            // Envio los parámetros post.
            OutputStream os = urlConnection.getOutputStream();
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(os, "UTF-8"));
            writer.write(jsonParam.toString());
            writer.flush();
            writer.close();

            int respuesta = urlConnection.getResponseCode();



            StringBuilder result = new StringBuilder();

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

    public boolean Eliminar(int id){

        try {

            HttpURLConnection urlConn;

            DataOutputStream printout;
            DataInputStream input;
            url = new URL(eliminar);
            urlConn = (HttpURLConnection) url.openConnection();
            urlConn.setDoInput(true);
            urlConn.setDoOutput(true);
            urlConn.setUseCaches(false);
            urlConn.setRequestProperty("Content-Type", "application/json");
            urlConn.setRequestProperty("Accept", "application/json");
            urlConn.connect();
            //Creo el Objeto JSON
            JSONObject jsonParam = new JSONObject();
            jsonParam.put("id", id);
            // Envio los parámetros post.
            OutputStream os = urlConn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(os, "UTF-8"));
            writer.write(jsonParam.toString());
            writer.flush();
            writer.close();

            int respuesta = urlConn.getResponseCode();


            StringBuilder result = new StringBuilder();

            if (respuesta == HttpURLConnection.HTTP_OK) {

                String line;
                BufferedReader br = new BufferedReader(new InputStreamReader(urlConn.getInputStream()));
                while ((line = br.readLine()) != null) {
                    result.append(line);
                    //response+=line;
                }

                System.out.println("::::::::::::::::::"+result);

                //Creamos un objeto JSONObject para poder acceder a los atributos (campos) del objeto.
                JSONObject respuestaJSON = new JSONObject(result.toString());   //Creo un JSONObject a partir del StringBuilder pasado a cadena
                //Accedemos al vector de resultados

                String resultJSON = respuestaJSON.getString("estado");   // estado es el nombre del campo en el JSON

                if (resultJSON.equals("1")) {      // hay un alumno que mostrar
                    return true;

                }
                else{
                    return false;
                }

            }
        }
        catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return false;

    }
}
