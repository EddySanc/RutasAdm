package com.example.rutasadm;


import android.graphics.Color;
import android.graphics.PorterDuff;
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
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.rutasadm.Clases.ColectivosLibres;
import com.example.rutasadm.Clases.Persona;
import com.example.rutasadm.GestionesBD.GestionesPersona;
import com.example.rutasadm.GestionesBD.GestionesRuta;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.logging.Level;


/**
 * A simple {@link Fragment} subclass.
 */
public class AgregarConductor extends Fragment implements View.OnClickListener {

    private EditText txtNombre;
    private EditText txtApellidos;
    private EditText txtTelefono;
    private EditText txtDireccion;
    private EditText txtUsuario;
    private EditText txtContrasenia;
    private EditText txtTipo;
    private RadioButton rbConductor;
    private RadioButton rbAdministrador;
    private TextView titleColectivo;
    private Spinner spColectivo;
    private TextView aqregado;
    private TextView modificado;
    private TextView txtAgregado;
    private TextView txtModificado;
    private FloatingActionButton btnGuardar;
    private FloatingActionButton btnEliminar;
    private ImageView btnEditar;

    ProgressBar progressBar;

    private int tipo_Accion;//Tipo de accion a reaalizar 1:Agregar 2:Modifiucar 3: Eliminar
    private int id;
    private String nombre;
    private String apellidos;
    private String telefono;
    private String direccion;
    private String usuario;
    private String contrasenia;
    private int tipo;
    private int colectivo;
    private ArrayList<ColectivosLibres> colectivosLibresRef = new ArrayList<>();
    private ArrayList<String> colectivosLibres = new ArrayList<>();

    private View vista;

    private GestionesPersona gestionesPersona;//Objeto para realizar las operaciones
    private GestionesRuta gestionesRuta;
    private Persona persona;

   private RealizarGestiones realizarGestiones;


    public AgregarConductor() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        vista = inflater.inflate(R.layout.fragment_agregar_conductor, container, false);

        progressBar = vista.findViewById(R.id.progreso);
        progressBar.setProgress(0);

        txtNombre = vista.findViewById(R.id.editNombre);
        txtApellidos = vista.findViewById(R.id.editApellidos);
        txtTelefono = vista.findViewById(R.id.editTelefono);
        txtDireccion = vista.findViewById(R.id.editDireccion);
        txtUsuario = vista.findViewById(R.id.editUsuario);
        txtContrasenia = vista.findViewById(R.id.editContrasenia);
        rbConductor = vista.findViewById(R.id.rbConductor);
        rbAdministrador = vista.findViewById(R.id.rbAdmin);
        titleColectivo = vista.findViewById(R.id.Colectivo);
        spColectivo = vista.findViewById(R.id.spColectivo);
        spColectivo.setPrompt("Selecciona una opción");
        aqregado = vista.findViewById(R.id.Agregado);
        modificado = vista.findViewById(R.id.Modificado);
        txtAgregado = vista.findViewById(R.id.editAgregado);
        txtModificado = vista.findViewById(R.id.editModificado);

        btnEditar = vista.findViewById(R.id.btnEditar);
        btnEliminar = vista.findViewById(R.id.btnEliminar);
        btnGuardar = vista.findViewById(R.id.btnGuardar);

        //Oculto elementos que no deberán ocultarse al pricipio

        titleColectivo.setVisibility(View.GONE);
        spColectivo.setVisibility(View.GONE);
        txtAgregado.setVisibility(View.GONE);
        txtModificado.setVisibility(View.GONE);
        aqregado.setVisibility(View.GONE);
        modificado.setVisibility(View.GONE);
        btnEliminar.hide();
        btnEditar.setVisibility(View.INVISIBLE);


        btnGuardar.setOnClickListener(this);
        rbConductor.setOnClickListener(this);
        rbAdministrador.setOnClickListener(this);

        return vista;
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){

            case R.id.btnGuardar:
                tipo_Accion = 1;
                Guardar();
                break;
            case R.id.rbAdmin:
                tipo = 1;
                titleColectivo.setVisibility(View.GONE);
                spColectivo.setVisibility(View.GONE);
                break;
            case R.id.rbConductor:
                LlenarSpinner();
                tipo = 2;
                titleColectivo.setVisibility(View.VISIBLE);
                spColectivo.setVisibility(View.VISIBLE);
                break;

        }
    }

    public void LlenarSpinner(){

        tipo_Accion = 2;
        realizarGestiones = new RealizarGestiones();
        realizarGestiones.execute();

    }
    private void Guardar(){

        if(ValidarCampos()){
            nombre = txtNombre.getText().toString();
            apellidos = txtApellidos.getText().toString();
            telefono = txtTelefono.getText().toString();
            direccion = txtDireccion.getText().toString();
            usuario = txtUsuario.getText().toString();
            contrasenia = txtContrasenia.getText().toString();

            if(tipo == 2) {
                colectivo = spColectivo.getSelectedItemPosition();
                if (!(colectivo == 0)) {
                    colectivo = colectivosLibresRef.get(colectivo - 1).getId();
                }
            }
            else if(tipo == 1){
                colectivo = 0;
            }

            persona = new Persona(0,nombre,apellidos,telefono,direccion,
                    usuario,contrasenia,tipo,"","",colectivo);

            realizarGestiones = new RealizarGestiones();
            realizarGestiones.execute();
        }

    }

    private void LimpiarCajas(){

        txtNombre.setText("");
        txtApellidos.setText("");
        txtTelefono.setText("");
        txtDireccion.setText("");
        txtUsuario.setText("");
        txtContrasenia.setText("");
        rbAdministrador.setChecked(true);
        tipo = 1;
        rbConductor.setChecked(false);
        titleColectivo.setVisibility(View.GONE);
        spColectivo.setVisibility(View.GONE);
    }

    private boolean ValidarCampos(){

        if(txtNombre.getText().toString().isEmpty()){
            txtNombre.requestFocus();
            Toast.makeText(vista.getContext(),"El campo \"Nombre\" esta vacío",Toast.LENGTH_SHORT).show();
            //txtNombre.getBackground().setColorFilter(Color.BLUE, PorterDuff.Mode.SRC_ATOP); Para cambiar de color del contorno a rojo
            return false;
        }
        if(txtApellidos.getText().toString().isEmpty()){
            txtApellidos.requestFocus();
            Toast.makeText(vista.getContext(),"El campo \"Apellidos\" esta vacío",Toast.LENGTH_SHORT).show();
            return false;
        }
        if(txtTelefono.getText().toString().isEmpty()){
            txtTelefono.requestFocus();
            Toast.makeText(vista.getContext(),"El campo \"Telefono\" esta vacío",Toast.LENGTH_SHORT).show();
            return false;
        }
        if(txtDireccion.getText().toString().isEmpty()){
            txtDireccion.requestFocus();
            Toast.makeText(vista.getContext(),"El campo \"Dirección\" esta vacío",Toast.LENGTH_SHORT).show();
            return false;
        }
        if(txtUsuario.getText().toString().isEmpty()){
            txtUsuario.requestFocus();
            Toast.makeText(vista.getContext(),"El campo \"Usuario\" esta vacío",Toast.LENGTH_SHORT).show();
            return false;
        }
        if(txtContrasenia.getText().toString().isEmpty()){
            txtContrasenia.requestFocus();
            Toast.makeText(vista.getContext(),"El campo \"Contraseña\" esta vacío",Toast.LENGTH_SHORT).show();
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

            if(tipo_Accion== 1){
                Toast.makeText(vista.getContext(),s,Toast.LENGTH_LONG).show();
                LimpiarCajas();

            }
            else if(tipo_Accion ==2){

                if (colectivosLibresRef.size() == 0) {
                    colectivosLibres.clear();
                    colectivosLibres.add("No asignar unidad (No hay disponibles)");

                }

                spColectivo.setAdapter(new ArrayAdapter<String>(vista.getContext(), android.R.layout.simple_spinner_dropdown_item,colectivosLibres));

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
            if(tipo_Accion == 1){
                gestionesPersona = new GestionesPersona();

                if(gestionesPersona.Agregar(persona)){
                   return  "Persona Agregada Correctamente";
                }
                else {
                    return  "Ocurrio un error, intentelo de nuevo";
                }
            }
            else if(tipo_Accion == 2){

                if (!colectivosLibres.isEmpty()){
                    colectivosLibres.clear();
                }
                colectivosLibres.add("No asignar unidad");
                gestionesPersona = new GestionesPersona();
                colectivosLibresRef = gestionesPersona.ConsultaColectivosLibres();
                if (colectivosLibresRef.size() > 0) {
                    for (int i = 0; i < colectivosLibresRef.size(); i++) {

                        colectivosLibres.add("Ruta: " + colectivosLibresRef.get(i).getNum_ruta()+"----"+
                                "Num Econom: " + colectivosLibresRef.get(i).getNum_econom());
                    }
                } else {
                    return "No hay ningun resultado";
                }
            }
            return "";
        }
    }

}
