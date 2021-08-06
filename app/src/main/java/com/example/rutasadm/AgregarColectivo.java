package com.example.rutasadm;


import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

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
public class AgregarColectivo extends Fragment  implements View.OnClickListener {

    private EditText txtNum_Econom;
    private EditText txtPlaca;
    private EditText txtDescripcion;
    private Spinner spRuta;
    private TextView txtAgregado;
    private TextView txtModificado;
    private TextView agregado;
    private TextView modificado;


    private FloatingActionButton btnGuardar;
    private FloatingActionButton btnEliminar;
    private ImageView btnEditar;

    ProgressBar progressBar;

    private View vista;

    private int tipo;//Tipo de accion a reaalizar 1:Agregar 2:Modifiucar 3: Eliminar
    private int id;
    private String num_econom;
    private String placa;
    private String descripcion;
    private int  ruta;

    private ArrayList<Ruta> rutasLibresRef = new ArrayList<>();
    private ArrayList<String> rutasLibres = new ArrayList<>();


    private GestionesColectivo gestionesColectivo;//Objeto para realizar las operaciones
    private Colectivo colectivo;

    private RealizarGestiones realizarGestiones;

    public AgregarColectivo() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        vista =  inflater.inflate(R.layout.fragment_agregar_colectivo, container,  false);

        progressBar = vista.findViewById(R.id.progreso);
        progressBar.setProgress(0);

        txtNum_Econom = vista.findViewById(R.id.editNumero);
        txtPlaca = vista.findViewById(R.id.editPlacas);
        txtDescripcion = vista.findViewById(R.id.editDescripcion);
        spRuta = vista.findViewById(R.id.spRuta);
        agregado = vista.findViewById(R.id.Agregado);
        modificado = vista.findViewById(R.id.Modificado);
        txtAgregado = vista.findViewById(R.id.editAgregado);
        txtModificado = vista.findViewById(R.id.editModificado);

        btnEditar = vista.findViewById(R.id.btnEditar);
        btnEliminar = vista.findViewById(R.id.btnEliminar);
        btnGuardar = vista.findViewById(R.id.btnGuardar);

        btnEditar.setVisibility(View.INVISIBLE);
        btnEliminar.hide();
        txtAgregado.setVisibility(View.GONE);
        txtModificado.setVisibility(View.GONE);
        agregado.setVisibility(View.GONE);
        modificado.setVisibility(View.GONE);

        btnGuardar.setOnClickListener(this);


        LlenarSpinner();

        return vista;
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){

            case R.id.btnGuardar:
                tipo = 1;
                Guardar();
                break;

        }

    }

    public void LlenarSpinner(){

        tipo = 2;
        realizarGestiones = new RealizarGestiones();
        realizarGestiones.execute();

    }

    private void Guardar(){

        if(ValidarCampos()){
            num_econom = txtNum_Econom.getText().toString();
            placa = txtPlaca.getText().toString();
            descripcion = txtDescripcion.getText().toString();

            ruta = rutasLibresRef.get(spRuta.getSelectedItemPosition()).getId();


            System.out.println("::::::::::"+ruta);

            colectivo = new Colectivo(0,num_econom,placa,descripcion,"","",ruta,"");

            realizarGestiones = new RealizarGestiones();
            realizarGestiones.execute();
        }

    }

    private void LimpiarCajas(){

        txtNum_Econom.setText("");
        txtPlaca.setText("");
        txtDescripcion.setText("");
        spRuta.setSelection(0);

    }

    private boolean ValidarCampos(){

        if(txtNum_Econom.getText().toString().isEmpty()){
            txtNum_Econom.requestFocus();
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

        if(rutasLibresRef.size()==0){
            Toast.makeText(vista.getContext(),"Se ha detectado que no hay rutas disponibles, es necesario agregar una  ruta antes de guardar un colectivo",Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    public class RealizarGestiones extends AsyncTask<String, Void, String> {
        @Override
        protected void onPostExecute(String s) {

            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    progressBar.setVisibility(vista.GONE);
                }
            });

            if(tipo == 1){
                Toast.makeText(vista.getContext(),s,Toast.LENGTH_LONG).show();
                LimpiarCajas();

            }
            else if(tipo  ==2){

                if (rutasLibresRef.size() == 0) {
                    rutasLibres.clear();
                    rutasLibres.add("No hay disponibles");

                }

                spRuta.setAdapter(new ArrayAdapter<String>(vista.getContext(), android.R.layout.simple_spinner_dropdown_item,rutasLibres));

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
            if(tipo  == 1){
                gestionesColectivo = new GestionesColectivo();

                if(gestionesColectivo.Agregar(colectivo)){
                    return  "Colectivo Agregado Correctamente";
                }
                else {
                    return  "Ocurrio un error, intentelo de nuevo";
                }
            }
            else if(tipo == 2){

                if (!rutasLibres.isEmpty()){
                    rutasLibres.clear();
                }

                gestionesColectivo = new GestionesColectivo();
                rutasLibresRef = gestionesColectivo.ConsultaRutaLibres();
                if (rutasLibresRef.size() > 0) {
                    for (int i = 0; i < rutasLibresRef.size(); i++) {

                        rutasLibres.add("Ruta: " + rutasLibresRef.get(i).getNum_ruta());
                    }
                } else {
                    return "No hay ningun resultado";
                }
            }
            return "";
        }
    }

}
