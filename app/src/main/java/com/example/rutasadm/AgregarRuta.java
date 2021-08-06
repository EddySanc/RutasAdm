package com.example.rutasadm;


import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.rutasadm.Clases.Ruta;
import com.example.rutasadm.GestionesBD.GestionesRuta;
import com.google.android.material.floatingactionbutton.FloatingActionButton;


/**
 * A simple {@link Fragment} subclass.
 */
public class AgregarRuta extends Fragment implements View.OnClickListener {

    private EditText txtNum_ruta;
    private EditText txtDescripcion;
    private TextView txtAgregado;
    private TextView txtModificado;
    private TextView aqregado;
    private TextView modificado;

    private FloatingActionButton btnGuardar;
    private FloatingActionButton btnEliminar;
    private ImageView btnEditar;

    ProgressBar progressBar;

    private View vista;

    private int tipo;//Tipo de accion a reaalizar 1:Agregar 2:Modifiucar 3: Eliminar
    private int id;
    private int num_ruta;
    private String descripcion;

    private  String devuelve;//mensaje que devolvera al momento de realizar la operación



    private GestionesRuta gestionesRuta;//Objeto para realizar las operaciones
    private Ruta ruta;

    private RealizarGestiones realizarGestiones;

    public AgregarRuta() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        vista = inflater.inflate(R.layout.fragment_agregar_ruta, container, false);


        progressBar = vista.findViewById(R.id.progreso);
        progressBar.setProgress(0);

        txtNum_ruta = vista.findViewById(R.id.editNumRuta);
        txtDescripcion = vista.findViewById(R.id.editDescripcion);
        txtAgregado = vista.findViewById(R.id.editAgregado);
        txtModificado = vista.findViewById(R.id.editModificado);
        aqregado = vista.findViewById(R.id.Agregado);
        modificado = vista.findViewById(R.id.Modificado);
        btnEditar = vista.findViewById(R.id.btnEditar);
        btnEliminar = vista.findViewById(R.id.btnEliminar);
        btnGuardar = vista.findViewById(R.id.btnGuardar);

        //El view.GONE Ayuda a ocultar elementos del layaout y tambien quita el espacio que éste ocupa
        //A diferenciaq del view:INVSIBLE que solo lo oculta y el espacio se mantiene
        txtAgregado.setVisibility(View.GONE);
        txtModificado.setVisibility(View.GONE);
        aqregado.setVisibility(View.GONE);
        modificado.setVisibility(View.GONE);
        btnEliminar.hide();
        btnEditar.setVisibility(View.INVISIBLE);

        btnGuardar.setOnClickListener(this);




        return vista;
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){

            case R.id.btnGuardar:
                tipo = 1;
                Agregar();
                break;
        }
    }

    private void Agregar(){

        if(ValidarCampos()) {
            num_ruta = Integer.parseInt(txtNum_ruta.getText().toString());
            descripcion = txtDescripcion.getText().toString();
            ruta = new Ruta(0, num_ruta, descripcion, "", "");

            realizarGestiones = new RealizarGestiones();
            realizarGestiones.execute();
        }

    }

    private void LimpiarCajas(){
        txtDescripcion.setText("");
        txtNum_ruta.setText("");

    }

    private boolean ValidarCampos(){

        if(txtNum_ruta.getText().toString().isEmpty()){
            txtNum_ruta.requestFocus();
            Toast.makeText(vista.getContext(),"El campo \"Número de Ruta\" esta vacío",Toast.LENGTH_SHORT).show();
            //txtNombre.getBackground().setColorFilter(Color.BLUE, PorterDuff.Mode.SRC_ATOP); Para cambiar de color del contorno a rojo
            return false;
        }
        if(txtDescripcion.getText().toString().isEmpty()){
            txtDescripcion.requestFocus();
            Toast.makeText(vista.getContext(),"El campo \"Descripción\" esta vacío",Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    public class RealizarGestiones extends AsyncTask<String, Void, String>{
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

        }

        @Override
        protected String doInBackground(String... strings) {

            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    progressBar.setVisibility(vista.VISIBLE);
                }
            });
            if(tipo == 1){
                gestionesRuta = new GestionesRuta();

                if(gestionesRuta.Agregar(ruta)){
                    return  "Ruta Agregada Correctamente";
                }
                else {
                    return  "Ocurrio un error, intentelo de nuevo";
                }
            }
            return "Error inesperado";
        }
    }

}
