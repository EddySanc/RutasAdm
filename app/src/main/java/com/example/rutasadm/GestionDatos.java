package com.example.rutasadm;


import android.app.ActionBar;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;


/**
 * A simple {@link Fragment} subclass.
 */
public class GestionDatos extends Fragment implements View.OnClickListener{


    private View vista;
    private FloatingActionMenu menuFloating;
    private FloatingActionButton itemColectivo;
    private FloatingActionButton itemRuta;
    private FloatingActionButton itemConductor;

    private RadioButton rbColectivo;
    private RadioButton rbConductor;
    private RadioButton rbRuta;



    public GestionDatos() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        vista = inflater.inflate(R.layout.fragment_gestion_datos, container, false);



        //Enlazo la vista e indico que al dar clic en cualquer lado de la pantalla automaticamente se cierre el menu
        menuFloating = vista.findViewById(R.id.menuFlotante);
        menuFloating.setClosedOnTouchOutside(true);

        //
        itemColectivo = vista.findViewById(R.id.itemColectivo);
        itemRuta = vista.findViewById(R.id.itemRuta);
        itemConductor = vista.findViewById(R.id.itemConductor);

        itemColectivo.setOnClickListener(this);
        itemRuta.setOnClickListener(this);
        itemConductor.setOnClickListener(this);
        /***************************************/
        rbColectivo = vista.findViewById(R.id.rbColectivo);
        rbConductor = vista.findViewById(R.id.rbConductor);
        rbRuta = vista.findViewById(R.id.rbRuta);

        rbColectivo.setOnClickListener(this);
        rbConductor.setOnClickListener(this);
        rbRuta.setOnClickListener(this);





        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.contenedorLista,new ListaColectivo()).commit();
        rbColectivo.setChecked(true);

        return vista;
    }



    @Override
    public void onClick(View v) {

        Fragment accion;
        switch (v.getId()){

            case R.id.itemColectivo:
                accion = new AgregarColectivo();
                AbrirFragments(R.id.contenedor,accion);

                break;
            case R.id.itemRuta:
                accion = new AgregarRuta();
                AbrirFragments(R.id.contenedor,accion);
                break;
            case R.id.itemConductor:
                accion = new AgregarConductor();
                AbrirFragments(R.id.contenedor,accion);
                break;
            case R.id.rbColectivo:
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.contenedorLista, new ListaColectivo()).commit();
                break;
            case R.id.rbConductor:
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.contenedorLista, new ListaConductor()).commit();
                break;
            case R.id.rbRuta:
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.contenedorLista, new ListaRuta()).commit();
                break;
        }

    }



    private void AbrirFragments(int id,Fragment fragment){

        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(id,fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();

    }
}
