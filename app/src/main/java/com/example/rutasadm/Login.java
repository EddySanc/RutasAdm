package com.example.rutasadm;


import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.rutasadm.Clases.Colectivo;
import com.example.rutasadm.Clases.ColectivosLibres;
import com.example.rutasadm.Clases.Identificadores;
import com.example.rutasadm.Clases.Persona;
import com.example.rutasadm.GestionesBD.GestionesColectivo;
import com.example.rutasadm.GestionesBD.GestionesPersona;
import com.example.rutasadm.GestionesBD.GestionesRuta;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class Login extends Fragment implements View.OnClickListener {


    private View vista;
    private Button btnLogin;
    private EditText txtUser,txtContrasenia;
    private Boolean satisfactorio = true;
    private ProgressBar progressBar;

    private int tipo;

    private String usuario;
    private String contrasenia;


    private GestionesPersona gestionesPersona;//Objeto para realizar las operaciones
    private GestionesColectivo gestionesColectivo;
    private Persona persona;

    private RealizarGestiones realizarGestiones;
    private ArrayList<Persona> listaPersona = new ArrayList<>();
    private ArrayList<Colectivo> listaColectivo = new ArrayList<>();


    public Login() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        vista = inflater.inflate(R.layout.fragment_login,container,false);

        btnLogin = vista.findViewById(R.id.btnIniciar);
        txtUser = vista.findViewById(R.id.etNombreUser);
        txtContrasenia = vista.findViewById(R.id.etContrasenia);
        progressBar = vista.findViewById(R.id.progreso);
        progressBar.setProgress(0);


        btnLogin.setOnClickListener(this);

        return vista;
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.btnIniciar:


                if(ValidarCampos()) {
                    usuario = txtUser.getText().toString();
                    contrasenia = txtContrasenia.getText().toString();
                    tipo = 1;
                    realizarGestiones = new RealizarGestiones();
                    realizarGestiones.execute();
                }
                break;
        }


    }

    private boolean ValidarCampos(){

        if(txtUser.getText().toString().isEmpty()){
            txtUser.requestFocus();
            Toast.makeText(vista.getContext(),"El campo \"Usuario\" esta vacío",Toast.LENGTH_SHORT).show();
            //txtNombre.getBackground().setColorFilter(Color.BLUE, PorterDuff.Mode.SRC_ATOP); Para cambiar de color del contorno a rojo
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
                    progressBar.setVisibility(vista.INVISIBLE);
                    progressBar.setProgress(0);
                }
            });

            if(tipo==1) {//Busqueda por id
                if (listaPersona.size() == 0) {
                    Toast.makeText(vista.getContext(), "Usuario y/o contraseña incorrectos", Toast.LENGTH_LONG).show();
                } else {

                    Identificadores.id = listaPersona.get(0).getId();
                    Identificadores.idColectivo = listaPersona.get(0).getColectivo();
                    Identificadores.tipo = listaPersona.get(0).getTipo();

                    if(Identificadores.idColectivo!=0) {
                        Buscarcolectivo();
                    }
                    else {
                        AbrirInicio();
                    }
                }
            }
            if(tipo == 2){
                if(listaColectivo.size() >0) {
                    Identificadores.idRuta = listaColectivo.get(0).getRuta();
                    System.out.println(Identificadores.idRuta);
                    AbrirInicio();
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

            if(tipo == 1 ) {//Login
                gestionesPersona = new GestionesPersona();
                listaPersona = gestionesPersona.Login(usuario,contrasenia);
            }
            if(tipo == 2){
                gestionesColectivo = new GestionesColectivo();
                listaColectivo = gestionesColectivo.ConsultaPorId(Identificadores.idColectivo);
            }


            return "";
        }

    }

    private void Buscarcolectivo() {

        tipo = 2;
        realizarGestiones = new RealizarGestiones();
        realizarGestiones.execute();
    }


    public void AbrirInicio(){

        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Fragment fInicio  = new Inicio();
        fragmentTransaction.replace(R.id.contenedor,fInicio);
        fragmentTransaction.commit();
    }


}
