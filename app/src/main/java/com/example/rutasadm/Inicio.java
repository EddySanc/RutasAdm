package com.example.rutasadm;


import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.StrictMode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.rutasadm.Clases.Identificadores;
import com.example.rutasadm.Clases.Ruta;
import com.example.rutasadm.GestionesBD.GestionesColectivo;

import java.util.ArrayList;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.BLUETOOTH_ADMIN;
import static android.Manifest.permission.READ_PHONE_STATE;


/**
 * A simple {@link Fragment} subclass.
 */
public class Inicio extends Fragment implements View.OnClickListener {

    private CardView itemInformacion,itemTrazar,itemAgregar,itemMapa,itemDesarrolladores;
    private Dialog vtnEmergente;
    public View vista;

    private ArrayList<Ruta> rutasLibresRef = new ArrayList<>();
    private ArrayList<String> rutasLibres = new ArrayList<>();

    private GestionesColectivo gestionesColectivo;//Objeto para realizar las operaciones
    private RealizarGestiones realizarGestiones;

    //elementos de la ventana emergente
    TextView cerrar;
    Spinner spRuta;
    ProgressBar progressBar;
    Button btnContinuar;

    public Inicio() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        vista = inflater.inflate(R.layout.fragment_inicio, container, false);

        vtnEmergente = new Dialog(vista.getContext());

        itemInformacion = (CardView)vista.findViewById(R.id.itemInformacion);
        itemTrazar = (CardView)vista.findViewById(R.id.itemTrazar);
        itemAgregar = (CardView)vista.findViewById(R.id.itemAgregar);
        itemMapa = (CardView)vista.findViewById(R.id.itemMapa);
        itemDesarrolladores = (CardView)vista.findViewById(R.id.itemDesarrolladores);


        itemInformacion.setOnClickListener(this);
        itemTrazar.setOnClickListener(this);
        itemAgregar.setOnClickListener(this);
        itemMapa.setOnClickListener(this);
        itemDesarrolladores.setOnClickListener(this);
        //GestionesBD nuevo = new GestionesBD(this);
        //ArrayList<Ganado> ganado = nuevo.UbicacioGandado();

        //Verifico si aun no han sido concedidos los permisos de localizacion, si no es asi pregunto al usuario.
        validaPermisos();

        /*StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);*/

        //La funcion de arriba ayuda a evitar la restriccion de android a ejecutar tareas en el hilo principal. No es muy recomendable


        return vista;
    }


    @Override
    public void onClick(View v) {

        switch (v.getId()){

            case R.id.itemInformacion:

                CargarEditarPerfil();

                break;
            case R.id.itemTrazar:

                if(Identificadores.tipo == 1) {

                    if (validaPermisos()) {
                        mostrarVenta();
                    }
                }
                else {
                    Toast.makeText(vista.getContext(),"Esta función está solo disponible para los administradores",Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.itemAgregar:

                if(Identificadores.tipo == 1) {
                    AbrirFragments(new GestionDatos());
                }
                else {
                    Toast.makeText(vista.getContext(),"Esta función está solo disponible para los administradores",Toast.LENGTH_SHORT).show();
                }

                break;
            case R.id.itemMapa:
                if(Identificadores.tipo == 2) {
                    if (validaPermisos()) {
                        AbrirFragments(new Mapa());
                    }
                }
                else {
                    Toast.makeText(vista.getContext(),"Esta función está solo disponible para los conductores",Toast.LENGTH_SHORT).show();
                }

                break;
            case R.id.itemDesarrolladores:
                Uri url = Uri.parse("http://rutasbch.ml/manuales.php");
                Intent intent;
                intent = new Intent(Intent.ACTION_VIEW,url);
                startActivity(intent);
                break;
            default:
                break;
        }

    }

    private boolean validaPermisos() {

        if(Build.VERSION.SDK_INT<Build.VERSION_CODES.M){
            return true;
        }

        if((vista.getContext().getApplicationContext().checkSelfPermission(ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED) &&
                (vista.getContext().getApplicationContext().checkSelfPermission(ACCESS_COARSE_LOCATION)==PackageManager.PERMISSION_GRANTED)){
            return true;
        }

        if((shouldShowRequestPermissionRationale(ACCESS_FINE_LOCATION)||(shouldShowRequestPermissionRationale(ACCESS_COARSE_LOCATION)))){
            cargarDialogoRecomendacion();
        }else{
            requestPermissions(new String[]{ACCESS_FINE_LOCATION,ACCESS_COARSE_LOCATION},100);
        }

        return false;
    }

    private void cargarDialogoRecomendacion() {
        AlertDialog.Builder dialogo = new AlertDialog.Builder(vista.getContext());
        dialogo.setTitle("Permisos Desactivados");
        dialogo.setMessage("Debe aceptar los permisos para el correcto funcionamiento de la App");

        dialogo.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                requestPermissions(new String[]{ACCESS_FINE_LOCATION,ACCESS_COARSE_LOCATION}, 100);
            }
        });
        dialogo.show();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode==100){
            if(grantResults.length==2 && grantResults[0]==PackageManager.PERMISSION_GRANTED
                    && grantResults[1]==PackageManager.PERMISSION_GRANTED){


            }else{
                System.out.println("Permission Denied: ");
            }
        }

    }



    public void mostrarVenta() {

        vtnEmergente.setContentView(R.layout.popupmenu);

        cerrar = vtnEmergente.findViewById(R.id.txtCerrar);
        spRuta = vtnEmergente.findViewById(R.id.spRutaPop);
        progressBar = vtnEmergente.findViewById(R.id.progreso);
        btnContinuar = vtnEmergente.findViewById(R.id.btnContinuar);


        cerrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vtnEmergente.dismiss();
            }
        });
        btnContinuar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(rutasLibresRef.size()==0) {
                    Toast.makeText(vista.getContext(),"No hay rutas registradas",Toast.LENGTH_SHORT).show();
                }
                else {
                    CargarTrazarRuta(rutasLibresRef.get(spRuta.getSelectedItemPosition()).getId());
                    vtnEmergente.dismiss();
                }
            }
        });

        vtnEmergente.show();
        LlenarSpinner();

    }

    public void LlenarSpinner(){

        realizarGestiones = new RealizarGestiones();
        realizarGestiones.execute();

    }

    public class RealizarGestiones extends AsyncTask<String, Void, String> {
        @Override
        protected void onPostExecute(String s) {

            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    progressBar.setVisibility(vista.INVISIBLE);
                }
            });


            if (rutasLibresRef.size() == 0) {
                rutasLibres.clear();
                rutasLibres.add("No hay disponibles");

            }

            spRuta.setAdapter(new ArrayAdapter<String>(vista.getContext(), android.R.layout.simple_spinner_dropdown_item,rutasLibres));



        }

        @Override
        protected String doInBackground(String... strings) {

            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    progressBar.setVisibility(vista.VISIBLE);
                }
            });


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

            return "";
        }
    }




    public void AbrirFragments(Fragment fragment){

        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.contenedor,fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    void CargarTrazarRuta(int id) {


        Bundle args = new Bundle();
        args.putInt("id", id);

        Fragment trazarRuta = new TrazarRuta();
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        trazarRuta.setArguments(args);
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.contenedor, trazarRuta);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();


    }

    void CargarEditarPerfil() {


        Bundle args = new Bundle();
        args.putInt("id", Identificadores.id);

        Fragment editarPerfil = new ModificarPersona();
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        editarPerfil.setArguments(args);
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.contenedor, editarPerfil);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();


    }

    /*public void LlenarLista(){

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                gestionesColectivo = new GestionesColectivo();
                rutasLibresRef = gestionesColectivo.ConsultaRutaLibres();
            }
        });

        System.out.println("Size"+rutasLibresRef.size());
    }*/
}
