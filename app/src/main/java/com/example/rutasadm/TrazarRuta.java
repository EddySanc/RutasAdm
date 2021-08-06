package com.example.rutasadm;


import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.os.RecoverySystem;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.app.FragmentTransaction;
import android.widget.Button;
import android.widget.Toast;

import com.example.rutasadm.Clases.Paradas;
import com.example.rutasadm.Clases.Recorrido;
import com.example.rutasadm.GestionesBD.GestionesColectivo;
import com.example.rutasadm.GestionesBD.GestionesParadas;
import com.example.rutasadm.GestionesBD.GestionesPersona;
import com.example.rutasadm.GestionesBD.GestionesRecorrido;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.IOException;
import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class TrazarRuta extends Fragment implements OnMapReadyCallback, View.OnClickListener {

    private Button btnTrazar;
    private Button btnAgregarParada;
    private Button btnEliminar;

    private Dialog vtnEmergente;
    private int id;
    private int tipo;
    private GoogleMap mMap;
    LocationManager locationManager;
    LocationListener locationListener;
    Location location;
    AlertDialog alert = null;
    int e = 1;
    private View vista;
    private  Polyline polyline;
    private boolean isActivate = false;

    private GestionesParadas gestionesParadas;
    private GestionesRecorrido gestionesRecorrido;
    private Recorrido recorrido;
    private RealizarGestiones realizarGestiones;



    private ArrayList<Recorrido> listaRecorrido = new ArrayList<>();
    private ArrayList<Recorrido> listaRecorridoConsulta = new ArrayList<>();
    private ArrayList<Paradas> listaParadasConsulta = new ArrayList<>();
    private ArrayList<Paradas> listaParadas = new ArrayList<>();

    public TrazarRuta() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        vista = inflater.inflate(R.layout.fragment_trazar_ruta, container, false);

        vtnEmergente = new Dialog(vista.getContext());

        Bundle parametros = this.getArguments();
        id = parametros.getInt("id");

        btnTrazar = vista.findViewById(R.id.btnTrazar);
        btnAgregarParada = vista.findViewById(R.id.btnAgregarParada);
        btnEliminar = vista.findViewById(R.id.btnEliminar);

        btnTrazar.setOnClickListener(this);
        btnAgregarParada.setOnClickListener(this);
        btnEliminar.setOnClickListener(this);



        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.mapa);

        mapFragment.getMapAsync(this);

        BuscarRecorrido();

        System.out.println("Aqui"+id);

        locationManager = (LocationManager)getActivity().getSystemService(Context.LOCATION_SERVICE);

        //Alerta emergente al no tener el gps activado
        if (!locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER) && !locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            AlertNoGPS();
        }

        //Verfico la version de android que tiene el dispositivo

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){

            if(ContextCompat.checkSelfPermission(getContext(),Manifest.permission.ACCESS_FINE_LOCATION )
                    != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(getContext(),Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){

                return null;

            }
            else{
                location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            }

        }
        else{

            location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        }


        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location locatio) {
                mMap.setMyLocationEnabled(true);

                location = locatio;



                if(e==1){
                    LatLng myLocation = new LatLng(location.getLatitude(), location.getLongitude());

                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLocation,15));
                    mMap.animateCamera(CameraUpdateFactory.zoomIn());
                    mMap.animateCamera(CameraUpdateFactory.zoomTo(15), 2000, null);
                    e++;
                }

                if(isActivate) {


                    LatLng myLocation = new LatLng(location.getLatitude(), location.getLongitude());
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(myLocation));

                    listaRecorrido.add(new Recorrido(0, 0, location.getLatitude(), location.getLongitude(), id));
                    System.out.println(listaRecorrido.size());



                    PolylineOptions options = new PolylineOptions().width(5).color(Color.BLUE).geodesic(true);
                    for (int i = 0; i < listaRecorrido.size(); i++)
                    {
                        options.add(new LatLng(listaRecorrido.get(i).getLatitud(), listaRecorrido.get(i).getLongitud()));
                        System.out.println(listaRecorrido.get(i).getLatitud()+"___"+listaRecorrido.get(i).getLongitud());
                    }
                    polyline = mMap.addPolyline(options);
                }



                /*for(int i = 0; i<listaRecorrido.size();i++) {

                    polyline = mMap.addPolyline(new PolylineOptions()
                            .add(
                                    new LatLng(listaRecorrido.get(i).getLatitud(), listaRecorrido.get(i).getLongitud())  )
                            .width(5)
                            .color(Color.RED));
                }
              */







            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,20,locationListener);


        return vista;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);
    }

    private void AlertNoGPS() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage("Los  sistemas de localización estan desactivados, ¿Desea activar el GPS?")
                .setCancelable(false)
                .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                    }
                });
        alert = builder.create();
        alert.show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(alert != null)
        {
            alert.dismiss ();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (ContextCompat.checkSelfPermission(getContext(),Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(getContext(),Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                return;
            } else {
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
            }
        } else {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
        }
        if(!(listaRecorridoConsulta.size()==0)) {
            TrazarRecorrido();
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnTrazar:

                if(!(listaRecorridoConsulta.size()>0)) {
                    if (isActivate) {
                        isActivate = false;
                        btnTrazar.setText("Trazar");

                        tipo = 2;
                        realizarGestiones = new RealizarGestiones();
                        realizarGestiones.execute();

                    } else {
                        btnTrazar.setText("Listo");
                        isActivate = true;
                    }
                }
            else {
                Toast.makeText(vista.getContext(),"Ya existe un recorrido para estaruta. Si desea actualizarlo, por favor borre el actual y vuela a trazarlo",Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btnAgregarParada:

                if(isActivate) {
                    LatLng mLatLng = new LatLng(location.getLatitude(), location.getLongitude());

                    mMap.addMarker(new MarkerOptions().position(mLatLng).title(getAddressFromLatLng(mLatLng)).snippet("Paradero").icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getResources(),R.drawable.paradero))));

                    listaParadas.add(new Paradas(0, "", location.getLatitude(), location.getLongitude(), id));
                }
                else {
                    Toast.makeText(vista.getContext(),"Para agregar un paradero es necesario comenzar el trazado de la ruta",Toast.LENGTH_SHORT).show();
                }

                break;
            case R.id.btnEliminar:

                if(listaRecorridoConsulta.size()>0){

                    AlertDialog.Builder dialogo1 = new AlertDialog.Builder(getContext());
                    dialogo1.setTitle("Importante");
                    dialogo1.setMessage("¿ Desea eliminar toda la información de esta ruta ?");
                    dialogo1.setCancelable(false);
                    dialogo1.setPositiveButton("Si", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialogo1, int id) {
                            tipo = 3;
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
                    Toast.makeText(vista.getContext(),"No exiten registros para borrar",Toast.LENGTH_SHORT).show();
                }

                break;
        }
    }

    public void mostrarVentana() {

        vtnEmergente.setContentView(R.layout.cargando);
        vtnEmergente.show();
    }
    private void TrazarRecorrido(){


            PolylineOptions recorrido = new PolylineOptions().width(5).color(Color.BLUE).geodesic(true);
            for (int i = 0; i<listaRecorridoConsulta.size();i++) {
                recorrido.add(new LatLng(listaRecorridoConsulta.get(i).getLatitud(), listaRecorridoConsulta.get(i).getLongitud()));
                System.out.println(listaRecorridoConsulta.get(i).getLatitud()+""+ listaRecorridoConsulta.get(i).getLongitud());
            }
            polyline = mMap.addPolyline(recorrido);


            for(int i=0;i<listaParadasConsulta.size();i++) {

                LatLng mLatLng = new LatLng(listaParadasConsulta.get(i).getLatitud(),listaParadasConsulta.get(i).getLongitud());

                mMap.addMarker(new MarkerOptions().position(mLatLng).title(getAddressFromLatLng(mLatLng)).snippet("Paradero").icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getResources(),R.drawable.paradero))));

            }

            LatLng myLocation = new LatLng(listaRecorridoConsulta.get(0).getLatitud(), listaRecorridoConsulta.get(0).getLongitud());

            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLocation,15));
            mMap.animateCamera(CameraUpdateFactory.zoomIn());
            mMap.animateCamera(CameraUpdateFactory.zoomTo(15), 2000, null);


    }

    private String getAddressFromLatLng( LatLng latLng ) {
        Geocoder geocoder = new Geocoder( getActivity() );

        String address = "";
        try {
            address = geocoder
                    .getFromLocation( latLng.latitude, latLng.longitude, 1 )
                    .get( 0 ).getAddressLine( 0 );
        } catch (IOException e ) {
        }

        return address;
    }

    private void BuscarRecorrido(){
        tipo = 1;
        realizarGestiones = new RealizarGestiones();
        realizarGestiones.execute();
    }

    public class RealizarGestiones extends AsyncTask<String, Void, String> {
        @Override
        protected void onPostExecute(String s) {

            if(tipo ==1) {
                if (listaRecorridoConsulta.size() == 0) {
                    Toast.makeText(vista.getContext(),s,Toast.LENGTH_SHORT).show();
                } else {


                    TrazarRecorrido();

                }
            }
            if (tipo == 2){
                if(s.equals("true")){
                    Toast.makeText(vista.getContext(),"Recorrido trazado con exito",Toast.LENGTH_SHORT).show();
                    mMap.clear();
                    BuscarRecorrido();
                    listaRecorrido.clear();
                    listaParadas.clear();
                    vtnEmergente.dismiss();
                }
                else {
                    vtnEmergente.dismiss();
                    Toast.makeText(vista.getContext(),"Ocurrio un error al trazar el recorrido",Toast.LENGTH_SHORT).show();
                }
            }
            if(tipo == 3){
                if(s.equals("true")){
                    vtnEmergente.dismiss();
                    Toast.makeText(vista.getContext(),"Recorrido eliminado con exito",Toast.LENGTH_SHORT).show();
                    tipo = 1;
                    mMap.clear();
                    BuscarRecorrido();
                    listaParadas.clear();
                    listaRecorrido.clear();
                }
                else {
                    vtnEmergente.dismiss();
                    Toast.makeText(vista.getContext(),"Ocurrio un error al eliminar el recorrido",Toast.LENGTH_SHORT).show();
                }
            }

        }

        @Override
        protected String doInBackground(String... strings) {

            if(tipo==1) {
                gestionesRecorrido = new GestionesRecorrido();
                listaRecorridoConsulta = gestionesRecorrido.ConsultaPorRuta(id);

                gestionesParadas = new GestionesParadas();
                listaParadasConsulta = gestionesParadas.ConsultaPorRuta(id);

                if (listaRecorridoConsulta.size() > 0) {

                } else {
                    return "Aun no se ha trazado el recorrido para esta ruta";
                }
            }
                if(tipo == 2){
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mostrarVentana();
                        }
                    });

                    gestionesRecorrido = new GestionesRecorrido();
                    gestionesParadas = new GestionesParadas();
                    for(int i = 0; i<listaRecorrido.size();i++){
                        if(!gestionesRecorrido.Agregar(listaRecorrido.get(i).getLatitud(),listaRecorrido.get(i).getLongitud(),listaRecorrido.get(i).getRuta())) {
                            return "false";
                        }
                    }

                        for(int i = 0; i<listaParadas.size();i++){
                            if(!gestionesParadas.Agregar(listaParadas.get(i).getLatitud(),listaParadas.get(i).getLongitud(),listaParadas.get(i).getRuta())) {
                                return "false";
                            }
                        }
                    return "true";
            }
                if(tipo == 3){
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mostrarVentana();
                        }
                    });
                    gestionesRecorrido = new GestionesRecorrido();
                    if(!gestionesRecorrido.Eliminar(id)){
                        return "false";
                    }
                    if (!gestionesParadas.Eliminar(id)){
                        return "false";
                    }

                    return "true";
                }


            return "";
        }
    }
}
