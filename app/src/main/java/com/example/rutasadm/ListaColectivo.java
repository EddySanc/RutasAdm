package com.example.rutasadm;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.rutasadm.Clases.Colectivo;
import com.example.rutasadm.GestionesBD.GestionesColectivo;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class ListaColectivo extends Fragment implements View.OnClickListener {

    private ListView lvColectivo;//ListView
    private View vista;
    private EditText txtBusqueda;
    private ImageView btnBuscar;
    private ArrayList<Colectivo> listacolectivoRef = new ArrayList<>();//Refencia a la list view segun el id
    private ArrayAdapter<String> adapter;
    private ArrayList<String> listRuta = new ArrayList<>(); // Con esta variable se llenará los datos en el ListView
    private RealizarGestiones realizarGestiones;//Clase intnerna tipo Asyntask
    private GestionesColectivo gestionesColectivo;//Clase de donde se realizan las operaciones a la BD
    private int tipo = 1;
    private int id;
    private String descripcion;

    AlertDialog alert = null;
    ProgressBar progressBar;

    public ListaColectivo() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        vista = inflater.inflate(R.layout.fragment_lista_colectivo, container, false);


        lvColectivo = vista.findViewById(R.id.lista_colectivo);
        btnBuscar = vista.findViewById(R.id.btnBuscar);
        txtBusqueda = vista.findViewById(R.id.txtBuscarDescripcion);
        progressBar = vista.findViewById(R.id.progreso);
        progressBar.setProgress(0);

        registerForContextMenu(lvColectivo);//Método que vincula el menu contextual con la listView
        btnBuscar.setOnClickListener(this);

        CargarHilo();

        return vista;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        MenuInflater inflater = getActivity().getMenuInflater();
        //AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;//Nos serivira para obtener la informacion de la vista de la listView
        inflater.inflate(R.menu.menuopciones, menu);//Creamos el menu y mostramos



    }

    //Obtenemos la opcion que el usuario eligió
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();//Obtenemos la infromacion del item eleccionado para obtener su posicion en la listVuew
        switch (item.getItemId()){
            case R.id.editar:
                CargarModificarColectivo(listacolectivoRef.get(info.position).getId());
                break;
            case R.id.eliminar:
                id = listacolectivoRef.get(info.position).getId();
                tipo = 2;

                //Cargamos el msj de confirm
                AlertDialog.Builder dialogo1 = new AlertDialog.Builder(getContext());
                dialogo1.setTitle("Importante");
                dialogo1.setMessage("¿ Desea eliminar toda la información de esta ruta ?");
                dialogo1.setCancelable(false);
                dialogo1.setPositiveButton("Si", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogo1, int id) {
                        CargarHilo();
                    }
                });
                dialogo1.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogo1, int id) {
                        dialogo1.cancel();
                    }
                });
                dialogo1.show();
                break;
            default:
                return super.onContextItemSelected(item);
        }
        return true;
    }

    public void LlenarLista() {

        //Llenado del ListView
        adapter = new ArrayAdapter<String>(vista.getContext(), android.R.layout.simple_list_item_1, listRuta);
        lvColectivo.setAdapter(adapter);

    }

    public void CargarHilo() {

        realizarGestiones = new RealizarGestiones();
        realizarGestiones.execute();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnBuscar:

                descripcion = "";
                descripcion = txtBusqueda.getText().toString();
                tipo = 3;

                if(!(adapter==null)) {
                    adapter.clear();
                    listRuta.clear();
                }
                CargarHilo();
                break;
        }
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
            if(tipo ==1) {
                if (listacolectivoRef.size() == 0) {
                    Toast.makeText(vista.getContext(), s, Toast.LENGTH_LONG).show();
                } else {
                    LlenarLista();
                    System.out.println("Longitud::::"+listacolectivoRef.size());
                }
            }
            else if(tipo ==2){

                if(s.equals("true")) {
                    tipo = 1;
                    Toast.makeText(vista.getContext(),"Elemento eliminado con exito",Toast.LENGTH_LONG).show();
                    adapter.clear();
                    listRuta.clear();
                    CargarHilo();
                    txtBusqueda.setText("");
                }
                else {
                    Toast.makeText(vista.getContext(),"Ocurrió un error durante el proceso, verifica tu conexión",Toast.LENGTH_LONG).show();
                }

            }
            else if(tipo ==3) {
                if (listacolectivoRef.size() == 0) {
                    Toast.makeText(vista.getContext(), s, Toast.LENGTH_LONG).show();
                    tipo=1;
                    CargarHilo();
                } else {

                    LlenarLista();
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

            if(tipo==1) {
                gestionesColectivo = new GestionesColectivo();
                listacolectivoRef = gestionesColectivo.ConsultaGral();
                if (listacolectivoRef.size() > 0) {
                    for (int i = 0; i < listacolectivoRef.size(); i++) {

                        listRuta.add("Numero Económico: " + listacolectivoRef.get(i).getNum_econom() + "\n" +
                                "Descripcion: " + listacolectivoRef.get(i).getDescripcion() + "\n");

                    }
                } else {
                    return "No hay ningun resultado";
                }
            }
            else if(tipo == 2){
                gestionesColectivo = new GestionesColectivo();
                return gestionesColectivo.Eliminar(id)+"";
            }
            else if(tipo==3){

                gestionesColectivo = new GestionesColectivo();
                listacolectivoRef = gestionesColectivo.ConsultaPorDescripcion(descripcion);

                if (listacolectivoRef.size() > 0) {
                    for (int i = 0; i < listacolectivoRef.size(); i++) {

                        listRuta.add("Numero Económico: " + listacolectivoRef.get(i).getNum_econom() + "\n" +
                                "Descripcion: " + listacolectivoRef.get(i).getDescripcion() + "\n");
                        System.out.println(listRuta);
                    }

                } else {
                    return "No hay ningun resultado";
                }
            }
            return "";
        }
    }

    void CargarModificarColectivo(int id) {


        Bundle args = new Bundle();
        args.putInt("id", id);

        Fragment modificarColectivo = new ModificarColectivo();
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        modificarColectivo.setArguments(args);
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.contenedor, modificarColectivo);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();


    }

}
