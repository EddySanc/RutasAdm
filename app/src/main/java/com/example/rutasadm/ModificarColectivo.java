package com.example.rutasadm;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.rutasadm.Clases.Colectivo;
import com.example.rutasadm.Clases.Ruta;
import com.example.rutasadm.GestionesBD.GestionesColectivo;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class ModificarColectivo extends Fragment implements View.OnClickListener {

    private EditText txtNum_econom;
    private EditText txtPlaca;
    private EditText txtDescripcion;
    private Spinner spRutas;
    private TextView txtAgregado;
    private TextView txtModificado;
    private FloatingActionButton btnGuardar;
    private FloatingActionButton btnEliminar;
    private ImageView btnEditar;

    ProgressBar progressBar;
    private View vista;

    private int tipo_Accion;//Tipo de accion a reaalizar 1:Agregar 2:Modifiucar 3: Eliminar
    private int id;
    private String num_econom;
    private String placa;
    private String descripcion;
    private int ruta;
    private String descrip_Ruta;

    private boolean isEdit = false;

    private GestionesColectivo gestionesColectivo;//Objeto para realizar las operaciones
    private Colectivo colectivo;

    private RealizarGestiones realizarGestiones;
    private ArrayList<Colectivo> listaColectivo;

    private ArrayList<Ruta> rutasLibresRef = new ArrayList<>();
    private ArrayList<String> rutasLibres = new ArrayList<>();

    public ModificarColectivo() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        vista = inflater.inflate(R.layout.fragment_agregar_colectivo, container,false);


        Bundle parametros = this.getArguments();
        id = parametros.getInt("id");
        System.out.println("id"+id);

        txtNum_econom = vista.findViewById(R.id.editNumero);
        txtPlaca = vista.findViewById(R.id.editPlacas);
        txtDescripcion = vista.findViewById(R.id.editDescripcion);
        spRutas = vista.findViewById(R.id.spRuta);
        spRutas.setPrompt("Selecciona una opción");
        txtAgregado = vista.findViewById(R.id.editAgregado);
        txtModificado = vista.findViewById(R.id.editModificado);

        progressBar = vista.findViewById(R.id.progreso);
        progressBar.setProgress(0);

        btnEditar = vista.findViewById(R.id.btnEditar);
        btnEliminar = vista.findViewById(R.id.btnEliminar);
        btnGuardar = vista.findViewById(R.id.btnGuardar);


        btnEditar.setOnClickListener(this);
        btnGuardar.setOnClickListener(this);
        btnEliminar.setOnClickListener(this);
        btnGuardar.setOnClickListener(this);

        BuscarColectivo();
        Activar_Desactivar();

        return vista;
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){

            case R.id.btnEditar:

                if(isEdit){
                    isEdit = false;
                    Activar_Desactivar();
                    txtNum_econom.setText(listaColectivo.get(0).getNum_econom()+"");
                    txtPlaca.setText(listaColectivo.get(0).getPlaca()+"");
                    txtDescripcion.setText(listaColectivo.get(0).getDescripcion()+"");

                }
                else {
                    isEdit = true;
                    Activar_Desactivar();
                }

                break;
            case R.id.btnGuardar:
                Guardar();
                break;
            case R.id.btnEliminar:
                tipo_Accion = 3;
                AlertDialog.Builder dialogo1 = new AlertDialog.Builder(getContext());
                dialogo1.setTitle("Importante");
                dialogo1.setMessage("¿ Desea eliminar toda la información de esta ruta ?");
                dialogo1.setCancelable(false);
                dialogo1.setPositiveButton("Si", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogo1, int id) {
                        realizarGestiones = new RealizarGestiones();
                        realizarGestiones.execute();
                    }
                });
                dialogo1.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogo1, int id) {
                        dialogo1.cancel();
                    }
                });
                dialogo1.show();

                break;

        }

    }

    private void Guardar(){

        tipo_Accion=2;

        if(ValidarCampos()){
            num_econom = txtNum_econom.getText().toString();
            placa = txtPlaca.getText().toString();
            descripcion = txtDescripcion.getText().toString();


            ruta = spRutas.getSelectedItemPosition();

            ruta = rutasLibresRef.get(ruta).getId();



            colectivo = new Colectivo(id,num_econom,placa,descripcion,"","",ruta,"");

            realizarGestiones = new RealizarGestiones();
            realizarGestiones.execute();

        }

    }

    private boolean ValidarCampos(){

        if(txtNum_econom.getText().toString().isEmpty()){
            txtNum_econom.requestFocus();
            Toast.makeText(vista.getContext(),"El campo \"Número Económico\" esta vacío",Toast.LENGTH_SHORT).show();
            //txtNombre.getBackground().setColorFilter(Color.BLUE, PorterDuff.Mode.SRC_ATOP); Para cambiar de color del contorno a rojo
            return false;
        }
        if(txtPlaca.getText().toString().isEmpty()){
            txtPlaca.requestFocus();
            Toast.makeText(vista.getContext(),"El campo \"Placa\" esta vacío",Toast.LENGTH_SHORT).show();
            return false;
        }
        if(txtDescripcion.getText().toString().isEmpty()){
            txtDescripcion.requestFocus();
            Toast.makeText(vista.getContext(),"El campo \"Descripción\" esta vacío",Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    public void LlenarSpinner(){

        tipo_Accion = 4;
        realizarGestiones = new RealizarGestiones();
        realizarGestiones.execute();

    }

    private void LlenarDatos(){
        txtNum_econom.setText(listaColectivo.get(0).getNum_econom());
        txtPlaca.setText(listaColectivo.get(0).getPlaca());
        txtDescripcion.setText(listaColectivo.get(0).getDescripcion());
        txtAgregado.setText(listaColectivo.get(0).getAgregado());
        txtModificado.setText(listaColectivo.get(0).getModificado());

        LlenarSpinner();


    }

    private void BuscarColectivo(){

        tipo_Accion = 1;
        realizarGestiones = new RealizarGestiones();
        realizarGestiones.execute();

    }

    public void Activar_Desactivar(){//Método para poder activas ov desactivar los editText o botones segun sea el caso
        if(isEdit){
            btnGuardar.show();
            txtNum_econom.setEnabled(true);
            txtPlaca.setEnabled(true);
            txtDescripcion.setEnabled(true);
        }
        else {
            btnGuardar.hide();
            txtNum_econom.setEnabled(false);
            txtPlaca.setEnabled(false);
            txtDescripcion.setEnabled(false);
        }

    }

    //Método para obtener la posición de un ítem del spinner
    public static int obtenerPosicionItem(Spinner spinner, String objeto) {
        //Creamos la variable posicion y lo inicializamos en 0
        int posicion = 0;
        //Recorre el spinner en busca del ítem que coincida con el parametro `String fruta`
        //que lo pasaremos posteriormente
        for (int i = 0; i < spinner.getCount(); i++) {
            //Almacena la posición del ítem que coincida con la búsqueda
            if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(objeto)) {
                posicion = i;
            }
        }
        //Devuelve un valor entero (si encontro una coincidencia devuelve la
        // posición 0 o N, de lo contrario devuelve 0 = posición inicial)
        return posicion;
    }


    public class RealizarGestiones extends AsyncTask<String, Void, String> {


        @Override
        protected void onPostExecute(String s) {


            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    progressBar.setVisibility(vista.GONE);
                    progressBar.setProgress(0);
                }
            });

            if(tipo_Accion==1) {//Busqueda por id
                if (listaColectivo.size() == 0) {
                    Toast.makeText(vista.getContext(), "Sin resultados", Toast.LENGTH_LONG).show();
                } else {
                    LlenarDatos();
                }
            }
            else if (tipo_Accion == 2){
                if(s.equals("true")) {
                    BuscarColectivo();
                    isEdit = false;
                    Activar_Desactivar();
                    Toast.makeText(vista.getContext(),"Cambios guardados con exito",Toast.LENGTH_LONG).show();

                }
                else{
                    Toast.makeText(vista.getContext(),"Ocurrió un error durante el proceso, verifica tu conexión",Toast.LENGTH_LONG).show();
                }

            }
            else if(tipo_Accion ==3){

                if(s.equals("true")) {
                    AbrirFragmentLista();
                    Toast.makeText(vista.getContext(),"Elemento eliminado con exito",Toast.LENGTH_LONG).show();
                }
                else {
                    Toast.makeText(vista.getContext(),"Ocurrió un error durante el proceso, verifica tu conexión",Toast.LENGTH_LONG).show();
                }

            }
            else if(tipo_Accion ==4){

                if (rutasLibresRef.size() == 0) {
                    rutasLibres.clear();
                    rutasLibres.add("(No hay disponibles)");

                }
                spRutas.setAdapter(new ArrayAdapter<String>(vista.getContext(), android.R.layout.simple_spinner_dropdown_item,rutasLibres));

                spRutas.setSelection(obtenerPosicionItem(spRutas, listaColectivo.get(0).getRuta_descripcion()));

            }



        }

        @Override
        protected String doInBackground(String... strings) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    progressBar.setVisibility(vista.VISIBLE);
                }
            });

            if(tipo_Accion == 1 ) {//Busqueda por id
                gestionesColectivo = new GestionesColectivo();
                listaColectivo = gestionesColectivo.ConsultaPorId(id);
            }

            else if (tipo_Accion == 2){
                gestionesColectivo = new GestionesColectivo();
                return gestionesColectivo.Editar(colectivo)+"";
            }
            else if(tipo_Accion == 3){

                gestionesColectivo = new GestionesColectivo();
                return gestionesColectivo.Eliminar(id)+"";
            }
            else if(tipo_Accion == 4){

                if (!rutasLibres.isEmpty()){
                    rutasLibres.clear();
                }

                gestionesColectivo = new GestionesColectivo();
                rutasLibresRef = gestionesColectivo.ConsultaRutaLibres();
                if (rutasLibresRef.size() > 0) {
                    for (int i = 0; i < rutasLibresRef.size(); i++) {

                        rutasLibres.add( rutasLibresRef.get(i).getDescripcion());
                    }

                } else {
                    return "No hay ningun resultado";
                }
            }



            return "";
        }

    }

    public void AbrirFragmentLista(){

        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.contenedor,new GestionDatos());
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

}
