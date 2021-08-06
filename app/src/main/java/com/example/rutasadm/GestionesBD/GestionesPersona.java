package com.example.rutasadm.GestionesBD;

import com.example.rutasadm.Clases.ColectivosLibres;
import com.example.rutasadm.Clases.Persona;
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

public class GestionesPersona {
    private String IP = "http://sigegapp.000webhostapp.com/";//Direccion del hosting donde se alojó nuestro web service
    private String agregar = IP + "persona_insertar.php";//Noormbre del archivo que hace la función de insertar
    private String obtener = IP + "persona_obtener.php";
    private String obtener_id = IP + "persona_obtener_por_id.php";
    private String login = IP + "persona_login.php";
    private String obtener_libres = IP + "colectivo_libres_obtener.php";
    private String obtener_nombre = IP + "persona_obtener_por_nombre.php";
    private String editar = IP + "persona_actualizar.php";
    private String eliminar = IP + "persona_borrar.php";
    private URL url = null;//Direccion a donde mandaremos la peticion
    private HttpURLConnection urlConnection;

    private ArrayList<Persona> listaPersona = new ArrayList<>();

    private int id;
    private String nombre;
    private String apellidos;
    private String telefono;
    private String direccion;
    private String usuario;
    private String contrasenia;
    private int tipo;
    private String agregado;
    private String modificado;
    private int colectivo;

    public GestionesPersona() {

    }

    public ArrayList<Persona> ConsultaGral(){

        try {
            url = new URL(obtener);
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


                    //Creamos un objeto JSONObject para poder acceder a los atributos del objeto
                    JSONObject respuestaJSON = new JSONObject(result.toString());
                    //Acceder al vector de resultados
                    String resultJSON = respuestaJSON.getString("estado");


                    if (resultJSON.equals("1")) {

                        JSONArray rutaJSON = respuestaJSON.getJSONArray("persona");

                        for (int i = 0; i < rutaJSON.length(); i++) {


                            id = rutaJSON.getJSONObject(i).getInt("id");
                            nombre = rutaJSON.getJSONObject(i).getString("nombre");
                            apellidos = rutaJSON.getJSONObject(i).getString("apellidos");
                            telefono = rutaJSON.getJSONObject(i).getString("telefono");
                            direccion = rutaJSON.getJSONObject(i).getString("direccion");
                            usuario = rutaJSON.getJSONObject(i).getString("usuario");
                            contrasenia = rutaJSON.getJSONObject(i).getString("contrasenia");
                            tipo = rutaJSON.getJSONObject(i).getInt("tipo");
                            agregado = rutaJSON.getJSONObject(i).getString("agregado");
                            modificado = rutaJSON.getJSONObject(i).getString("modificado");

                            listaPersona.add(new Persona(id,nombre,apellidos,telefono,direccion,usuario,contrasenia,tipo,agregado,modificado,colectivo));

                        }

                        return listaPersona;
                    } else {

                        return listaPersona;
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

        return listaPersona;
    }

    public ArrayList<Persona> ConsultaPorId(int idPersona){

        try {
            url = new URL(obtener_id+"?id="+idPersona);
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

                    id = respuestaJSON.getJSONObject("persona").getInt("id");
                    nombre = respuestaJSON.getJSONObject("persona").getString("nombre");
                    apellidos = respuestaJSON.getJSONObject("persona").getString("apellidos");
                    telefono = respuestaJSON.getJSONObject("persona").getString("telefono");
                    direccion = respuestaJSON.getJSONObject("persona").getString("direccion");
                    usuario = respuestaJSON.getJSONObject("persona").getString("usuario");
                    contrasenia = respuestaJSON.getJSONObject("persona").getString("contrasenia");
                    tipo = respuestaJSON.getJSONObject("persona").getInt("tipo");
                    agregado = respuestaJSON.getJSONObject("persona").getString("agregado");
                    modificado = respuestaJSON.getJSONObject("persona").getString("modificado");

                    if( respuestaJSON.getJSONObject("persona").getString("colectivo").equals("null")) {
                        colectivo = 0;
                    }
                    else{
                        colectivo = respuestaJSON.getJSONObject("persona").getInt("colectivo");
                    }


                    listaPersona.add(new Persona(id,nombre,apellidos,telefono,direccion,usuario,contrasenia,tipo,agregado,modificado,colectivo));

                    return listaPersona;

                }
                else{
                    return listaPersona;
                }

            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return listaPersona;

    }

    public ArrayList<Persona> Login(String usuario, String contrasenia){

        try {
            url = new URL(login+"?usuario="+usuario+"&contrasenia="+contrasenia);
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

                System.out.println(result);
                //Creamos un objeto JSONObject para poder acceder a los atributos (campos) del objeto.
                JSONObject respuestaJSON = new JSONObject(result.toString());   //Creo un JSONObject a partir del StringBuilder pasado a cadena
                //Accedemos al vector de resultados

                String resultJSON = respuestaJSON.getString("estado");   // estado es el nombre del campo en el JSON

                if (resultJSON.equals("1")){      // hay un alumno que mostrar

                    id = respuestaJSON.getJSONObject("persona").getInt("id");
                    nombre = respuestaJSON.getJSONObject("persona").getString("nombre");
                    apellidos = respuestaJSON.getJSONObject("persona").getString("apellidos");
                    telefono = respuestaJSON.getJSONObject("persona").getString("telefono");
                    direccion = respuestaJSON.getJSONObject("persona").getString("direccion");
                    usuario = respuestaJSON.getJSONObject("persona").getString("usuario");
                    contrasenia = respuestaJSON.getJSONObject("persona").getString("contrasenia");
                    tipo = respuestaJSON.getJSONObject("persona").getInt("tipo");
                    agregado = respuestaJSON.getJSONObject("persona").getString("agregado");
                    modificado = respuestaJSON.getJSONObject("persona").getString("modificado");

                    if( respuestaJSON.getJSONObject("persona").getString("colectivo").equals("null")) {
                        colectivo = 0;
                    }
                    else{
                        colectivo = respuestaJSON.getJSONObject("persona").getInt("colectivo");
                    }


                    listaPersona.add(new Persona(id,nombre,apellidos,telefono,direccion,usuario,contrasenia,tipo,agregado,modificado,colectivo));

                    return listaPersona;

                }
                else{
                    return listaPersona;
                }

            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return listaPersona;

    }
    public ArrayList<Persona> ConsultaPorNombre(String nombre){

        try {
            url = new URL(obtener_nombre+"?nombre="+nombre);
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

                    JSONArray rutaJSON = respuestaJSON.getJSONArray("persona");


                    for (int i = 0; i < rutaJSON.length(); i++) {

                        id = rutaJSON.getJSONObject(i).getInt("id");
                        nombre = rutaJSON.getJSONObject(i).getString("nombre");
                        apellidos = rutaJSON.getJSONObject(i).getString("apellidos");
                        telefono = rutaJSON.getJSONObject(i).getString("telefono");
                        direccion = rutaJSON.getJSONObject(i).getString("direccion");
                        usuario = rutaJSON.getJSONObject(i).getString("usuario");
                        contrasenia = rutaJSON.getJSONObject(i).getString("contrasenia");
                        tipo = rutaJSON.getJSONObject(i).getInt("tipo");
                        agregado = rutaJSON.getJSONObject(i).getString("agregado");
                        modificado = rutaJSON.getJSONObject(i).getString("modificado");

                        listaPersona.add(new Persona(id,nombre,apellidos,telefono,direccion,usuario,contrasenia,tipo,agregado,modificado,colectivo));


                    }
                    return listaPersona;

                }
                else{
                    return listaPersona;
                }

            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return listaPersona;

    }

    public ArrayList<ColectivosLibres> ConsultaColectivosLibres(){

        ArrayList<ColectivosLibres> colectivosLibres = new ArrayList<>();

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


                        JSONArray rutaJSON = respuestaJSON.getJSONArray("colectivo");

                        for (int i = 0; i < rutaJSON.length(); i++) {

                            id = rutaJSON.getJSONObject(i).getInt("id");
                            String num_ruta = rutaJSON.getJSONObject(i).getString("num_ruta");

                            String num_econom = rutaJSON.getJSONObject(i).getString("num_econom");



                            colectivosLibres.add(new ColectivosLibres(id,num_ruta,num_econom));

                        }

                        return colectivosLibres;
                    } else {

                        return colectivosLibres;
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

        return colectivosLibres;
    }

    public boolean Agregar(Persona persona) {

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
            jsonParam.put("nombre", persona.getNombre());
            jsonParam.put("apellidos", persona.getApellidos());
            jsonParam.put("telefono", persona.getTelefono());
            jsonParam.put("direccion", persona.getDireccion());
            jsonParam.put("usuario", persona.getUsuario());
            jsonParam.put("contrasenia", persona.getContrasenia());
            jsonParam.put("tipo", persona.getTipo());
            jsonParam.put("colectivo", persona.getColectivo());


            //Envío los parametros al post de PHP
            OutputStream outputStream = urlConnection.getOutputStream();//Solicitamos la salida de datos al servidor
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
            writer.write(jsonParam.toString());//Escribimos los parametros dentro del post
            writer.flush();
            writer.close();//Cierre del writer, recomendado

            int respuestaConexion = urlConnection.getResponseCode();//Obtenemos la respuesta

            StringBuilder result = new StringBuilder(); //almacenará el resultado
            System.out.println(urlConnection.getResponseMessage());
            if (respuestaConexion == HttpURLConnection.HTTP_OK) {

                String line;
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                while ((line = bufferedReader.readLine()) != null) {//Recorremos el buffer y verificamos si aun existen datos
                    result.append(line);//Almacenamos por linea de respuestas
                }

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

    public boolean Editar(Persona persona){

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
            jsonParam.put("id", persona.getId());
            jsonParam.put("nombre", persona.getNombre());
            jsonParam.put("apellidos", persona.getApellidos());
            jsonParam.put("telefono", persona.getTelefono());
            jsonParam.put("direccion", persona.getDireccion());
            jsonParam.put("usuario", persona.getUsuario());
            jsonParam.put("contrasenia", persona.getContrasenia());
            jsonParam.put("tipo", persona.getTipo());
            jsonParam.put("colectivo", persona.getColectivo());

            System.out.println("::::::::::::::::::::::"+persona.getColectivo());

            // Envio los parámetros post.
            OutputStream os = urlConnection.getOutputStream();
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(os, "UTF-8"));
            writer.write(jsonParam.toString());
            writer.flush();
            writer.close();

            int respuesta = urlConnection.getResponseCode();


            System.out.println(urlConnection.getResponseMessage()+"SDSFDSFGDS");
            StringBuilder result = new StringBuilder();

            if (respuesta == HttpURLConnection.HTTP_OK) {

                String line;
                BufferedReader br=new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                while ((line=br.readLine()) != null) {
                    result.append(line);
                    //response+=line;
                }


                System.out.println("result::::::::::"+result);
                System.out.println("result::::::::::"+persona.getNombre());
                System.out.println("result::::::::::"+persona.getApellidos());
                System.out.println("result::::::::::"+persona.getId());
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
