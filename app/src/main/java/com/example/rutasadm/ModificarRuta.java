package com.example.rutasadm;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.rutasadm.AgregarRuta;
import com.example.rutasadm.Clases.Ruta;
import com.example.rutasadm.GestionesBD.GestionesRuta;
import com.example.rutasadm.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class ModificarRuta extends Fragment implements View.OnClickListener {

    private EditText txtNum_ruta;
    private EditText txtDescripcion;
    private TextView txtAgregado;
    private TextView txtModificado;

    private FloatingActionButton btnGuardar;
    private FloatingActionButton btnEliminar;
    private ImageView btnEditar;

    ProgressBar  progressBar;

    private View vista;

    private int tipo = 1;//Tipo de accion a reaalizar 1:Consulta 2:Modifiucar 3: Eliminar
    private int id;
    private int num_ruta;
    private String descripcion;

    private boolean isEdit = false;
    private GestionesRuta gestionesRuta;//Objeto para realizar las operaciones
    private Ruta ruta;

    private RealizarGestiones realizarGestiones;
    private ArrayList<Ruta> listaRuta;


    public ModificarRuta() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        vista = inflater.inflate(R.layout.fragment_agregar_ruta, container, false);

        //Cargamos los id que mandamos del fragment anterior
        Bundle parametros = this.getArguments();
        id = parametros.getInt("id");

        txtNum_ruta = vista.findViewById(R.id.editNumRuta);
        txtDescripcion = vista.findViewById(R.id.editDescripcion);
        txtAgregado = vista.findViewById(R.id.editAgregado);
        txtModificado = vista.findViewById(R.id.editModificado);
        btnEditar = vista.findViewById(R.id.btnEditar);
        btnGuardar = vista.findViewById(R.id.btnGuardar);
        btnEliminar = vista.findViewById(R.id.btnEliminar);

        progressBar = vista.findViewById(R.id.progreso);
        progressBar.setProgress(0);

        btnEditar.setOnClickListener(this);
        btnGuardar.setOnClickListener(this);
        btnEliminar.setOnClickListener(this);

        LlenarDatos();
        Activar_Desactivar();


        return vista;
    }

    public void Activar_Desactivar(){//Método para poder activas ov desactivar los editText o botones segun sea el caso
        if(isEdit){
            btnGuardar.show();
            txtNum_ruta.setEnabled(true);
            txtDescripcion.setEnabled(true);

        }
        else {
            btnGuardar.hide();
            txtNum_ruta.setEnabled(false);
            txtDescripcion.setEnabled(false);
        }


    }
    private void LlenarDatos(){
        tipo = 1;
        realizarGestiones = new RealizarGestiones();
        realizarGestiones.execute();

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){

            case R.id.btnEditar:

                if(isEdit){
                    isEdit = false;
                    Activar_Desactivar();
                    txtNum_ruta.setText(listaRuta.get(0).getNum_ruta()+"");
                    txtDescripcion.setText(listaRuta.get(0).getDescripcion()+"");

                }
                else {
                    isEdit = true;
                    Activar_Desactivar();
                }

                break;
            case R.id.btnGuardar:
                tipo = 2;
                ruta = new Ruta(id,Integer.parseInt(txtNum_ruta.getText().toString()),txtDescripcion.getText().toString(),"","");
                realizarGestiones = new RealizarGestiones();
                realizarGestiones.execute();
                break;
            case R.id.btnEliminar:
                tipo = 3;
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

    public class RealizarGestiones extends AsyncTask<String, Void, String> {


        @Override
        protected void onPostExecute(String s) {


            if(tipo==1) {//Busqueda por id
                if (listaRuta.size() == 0) {
                    Toast.makeText(vista.getContext(), "Sin resultados", Toast.LENGTH_LONG).show();
                } else {
                    txtNum_ruta.setText(listaRuta.get(0).getNum_ruta()+"");
                    txtDescripcion.setText(listaRuta.get(0).getDescripcion());
                    txtAgregado.setText(listaRuta.get(0).getAgregado());
                    txtModificado.setText(listaRuta.get(0).getModificado());

                }
            }
            else if (tipo == 2){
                if(s.equals("true")) {
                    LlenarDatos();
                    isEdit = false;
                    Activar_Desactivar();
                    Toast.makeText(vista.getContext(),"Cambios guardados con exito",Toast.LENGTH_LONG).show();
                }
                else{
                    Toast.makeText(vista.getContext(),"Ocurrió un error durante el proceso, verifica tu conexión",Toast.LENGTH_LONG).show();
                }

            }
            else if(tipo ==3){

                if(s.equals("true")) {
                    AbrirFragmentLista();
                    Toast.makeText(vista.getContext(),"Elemento eliminado con exito",Toast.LENGTH_LONG).show();
                }
                else {
                    Toast.makeText(vista.getContext(),"Ocurrió un error durante el proceso, verifica tu conexión",Toast.LENGTH_LONG).show();
                }

            }
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    progressBar.setVisibility(vista.GONE);
                    progressBar.setProgress(0);
                }
            });


        }

        @Override
        protected String doInBackground(String... strings) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    progressBar.setVisibility(vista.VISIBLE);
                }
            });

            if(tipo == 1 ) {//Busqueda por id
                gestionesRuta = new GestionesRuta();
                listaRuta = gestionesRuta.ConsultaPorId(id);
            }

            else if (tipo == 2){
                gestionesRuta = new GestionesRuta();

                return gestionesRuta.Editar(ruta)+"";
            }
            else if(tipo == 3){

                gestionesRuta = new GestionesRuta();

                return gestionesRuta.Eliminar(id)+"";
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
