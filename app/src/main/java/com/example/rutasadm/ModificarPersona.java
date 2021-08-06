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
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.rutasadm.Clases.ColectivosLibres;
import com.example.rutasadm.Clases.Identificadores;
import com.example.rutasadm.Clases.Persona;
import com.example.rutasadm.GestionesBD.GestionesPersona;
import com.example.rutasadm.GestionesBD.GestionesRuta;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class ModificarPersona extends Fragment implements View.OnClickListener {

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
    private TextView txtAgregado;
    private TextView txtModificado;
    private FloatingActionButton btnGuardar;
    private FloatingActionButton btnEliminar;
    private ImageView btnEditar;

    ProgressBar progressBar;
    private View vista;


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
    private int idColectivoAnterior;

    private boolean isEdit = false;

    private GestionesPersona gestionesPersona;//Objeto para realizar las operaciones
    private GestionesRuta gestionesRuta;
    private Persona persona;

   private RealizarGestiones realizarGestiones;
   private ArrayList<Persona> listaPersona;

    private ArrayList<ColectivosLibres> colectivosLibresRef = new ArrayList<>();
    private ArrayList<String> colectivosLibres = new ArrayList<>();

    public ModificarPersona() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //Cargamos los id que mandamos del fragment anterior
        vista = inflater.inflate(R.layout.fragment_agregar_conductor, container, false);
        Bundle parametros = this.getArguments();
        id = parametros.getInt("id");


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
        txtAgregado = vista.findViewById(R.id.editAgregado);
        txtModificado = vista.findViewById(R.id.editModificado);
        rbConductor.setEnabled(false);
        rbAdministrador.setEnabled(false);

        progressBar = vista.findViewById(R.id.progreso);
        progressBar.setProgress(0);

        btnEditar = vista.findViewById(R.id.btnEditar);
        btnEliminar = vista.findViewById(R.id.btnEliminar);
        btnGuardar = vista.findViewById(R.id.btnGuardar);

        titleColectivo.setVisibility(View.GONE);
        spColectivo.setVisibility(View.GONE);

        btnEditar.setOnClickListener(this);
        btnGuardar.setOnClickListener(this);
        btnEliminar.setOnClickListener(this);
        btnGuardar.setOnClickListener(this);
        rbConductor.setOnClickListener(this);
        rbAdministrador.setOnClickListener(this);


        BuscarPersona();
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
                    txtNombre.setText(listaPersona.get(0).getNombre()+"");
                    txtApellidos.setText(listaPersona.get(0).getApellidos()+"");
                    txtTelefono.setText(listaPersona.get(0).getTelefono()+"");
                    txtDireccion.setText(listaPersona.get(0).getDireccion()+"");
                    txtUsuario.setText(listaPersona.get(0).getUsuario()+"");
                    txtContrasenia.setText(listaPersona.get(0).getContrasenia()+"");

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

                if(id != Identificadores.id) {
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
                }
                else {
                    Toast.makeText(vista.getContext(),"No puedes eliminar tu propio perfil",Toast.LENGTH_SHORT).show();
                }

                break;

        }


    }

    private void Guardar(){

        tipo_Accion=2;

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

            persona = new Persona(id,nombre,apellidos,telefono,direccion,
                    usuario,contrasenia,tipo,"","",colectivo);

            realizarGestiones = new RealizarGestiones();
            realizarGestiones.execute();

        }

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

    public void LlenarSpinner(){

        tipo_Accion = 4;
        realizarGestiones = new RealizarGestiones();
        realizarGestiones.execute();

    }

    private void LlenarDatos(){
        txtNombre.setText(listaPersona.get(0).getNombre());
        txtApellidos.setText(listaPersona.get(0).getApellidos());
        txtTelefono.setText(listaPersona.get(0).getTelefono());
        txtDireccion.setText(listaPersona.get(0).getDireccion());
        txtUsuario.setText(listaPersona.get(0).getUsuario());
        txtContrasenia.setText(listaPersona.get(0).getContrasenia());
        txtAgregado.setText(listaPersona.get(0).getAgregado());
        txtModificado.setText(listaPersona.get(0).getModificado());
        idColectivoAnterior = listaPersona.get(0).getColectivo();
        tipo = listaPersona.get(0).getTipo();
        if(listaPersona.get(0).getTipo() == 1){
            rbAdministrador.setChecked(true);
            spColectivo.setVisibility(View.GONE);
            titleColectivo.setVisibility(View.GONE);
        }
        else if(listaPersona.get(0).getTipo() == 2) {
            rbConductor.setChecked(true);

            LlenarSpinner();
            titleColectivo.setVisibility(View.VISIBLE);
            spColectivo.setVisibility(View.VISIBLE);
        }



    }

    private void BuscarPersona(){

        tipo_Accion = 1;
        realizarGestiones = new RealizarGestiones();
        realizarGestiones.execute();

    }

    public void Activar_Desactivar(){//Método para poder activas ov desactivar los editText o botones segun sea el caso
        if(isEdit){
            btnGuardar.show();
            txtNombre.setEnabled(true);
            txtApellidos.setEnabled(true);
            txtTelefono.setEnabled(true);
            txtDireccion.setEnabled(true);
            txtUsuario.setEnabled(true);
            txtContrasenia.setEnabled(true);

        }
        else {
            btnGuardar.hide();
            txtNombre.setEnabled(false);
            txtApellidos.setEnabled(false);
            txtTelefono.setEnabled(false);
            txtDireccion.setEnabled(false);
            txtUsuario.setEnabled(false);
            txtContrasenia.setEnabled(false);
        }

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
                if (listaPersona.size() == 0) {
                    Toast.makeText(vista.getContext(), "Sin resultados", Toast.LENGTH_LONG).show();
                } else {
                    LlenarDatos();
                }
            }
            else if (tipo_Accion == 2){
                if(s.equals("true")) {
                    BuscarPersona();
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

                if (colectivosLibresRef.size() == 0) {
                    colectivosLibres.clear();
                    colectivosLibres.add("No asignar unidad (No hay disponibles)");

                }
                spColectivo.setAdapter(new ArrayAdapter<String>(vista.getContext(), android.R.layout.simple_spinner_dropdown_item,colectivosLibres));
                if(idColectivoAnterior != 0){
                    System.out.println("Seleccion"+idColectivoAnterior);
                    spColectivo.setSelection(colectivosLibres.size()-1);
                }
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
                gestionesPersona = new GestionesPersona();
                listaPersona = gestionesPersona.ConsultaPorId(id);
            }

            else if (tipo_Accion == 2){

                gestionesPersona = new GestionesPersona();
                return gestionesPersona.Editar(persona)+"";
            }
            else if(tipo_Accion == 3){

                gestionesPersona = new GestionesPersona();
                return gestionesPersona.Eliminar(id)+"";
            }
            else if(tipo_Accion == 4){

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
                    if(idColectivoAnterior!=0){
                        colectivosLibres.add("Mantener la unidad Actual");
                    }

                    colectivosLibresRef.add(new ColectivosLibres(idColectivoAnterior,"",""));
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
