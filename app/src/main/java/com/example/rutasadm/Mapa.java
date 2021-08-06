package com.example.rutasadm;


import android.Manifest;
import android.app.AlertDialog;
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

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.rutasadm.Clases.Identificadores;
import com.example.rutasadm.Clases.Paradas;
import com.example.rutasadm.Clases.Recorrido;
import com.example.rutasadm.Clases.Solicitudes;
import com.example.rutasadm.Clases.Ubicacion;
import com.example.rutasadm.GestionesBD.GestionesParadas;
import com.example.rutasadm.GestionesBD.GestionesRecorrido;
import com.example.rutasadm.GestionesBD.GestionesSolicitudes;
import com.example.rutasadm.GestionesBD.GestionesUbicacion;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
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
public class Mapa extends Fragment implements OnMapReadyCallback {

    private View vista;

    private int tipo = 1;

    private int e = 1;

    private Double latitud;
    private Double longitud;

    private ArrayList<Recorrido> listaRecorridoConsulta = new ArrayList<>();
    private ArrayList<Paradas> listaParadasConsulta = new ArrayList<>();
    private ArrayList<Solicitudes> listaSolicitudesConsulta = new ArrayList<>();
    private RealizarGestiones realizarGestiones;//Clase intnerna tipo Asyntask
    private GestionesParadas gestionesParadas;
    private GestionesRecorrido gestionesRecorrido;
    private GestionesSolicitudes gestionesSolicitudes;
    private GestionesUbicacion gestionesUbicacion;

    private LocationManager locationManager;
    private LocationListener locationListener;
    private Location location;
    private GoogleMap mMap;
    private AlertDialog alert = null;
    private Polyline polyline;
    private Marker[] marcador;

    public Mapa() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        vista =  inflater.inflate(R.layout.fragment_mapa, container, false);

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.mapa);

        mapFragment.getMapAsync(this);

        locationManager = (LocationManager)getActivity().getSystemService(Context.LOCATION_SERVICE);

        //Alerta emergente al no tener el gps activado
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            AlertNoGPS();
        }

        //Verfico la version de android que tiene el dispositivo

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){

            if(ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION )
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

                latitud = location.getLatitude();
                longitud = location.getLongitude();

                LatLng myLocation = new LatLng(location.getLatitude(), location.getLongitude());

                if(e==1){

                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLocation,15));
                    mMap.animateCamera(CameraUpdateFactory.zoomIn());
                    mMap.animateCamera(CameraUpdateFactory.zoomTo(15), 2000, null);
                    e++;
                }
                else {
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(myLocation));
                }

                if(listaRecorridoConsulta.size()>0) {
                    tipo = 2;
                    realizarGestiones = new RealizarGestiones();
                    realizarGestiones.execute();
                }



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
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,5000,0,locationListener);

        BuscarRecorrido();
        return vista;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;

        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);

        ClicParadas();


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
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 0, locationListener);
            }
        } else {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 0, locationListener);
        }
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

    private void TrazarRecorrido(){

        marcador = new Marker[listaSolicitudesConsulta.size()];

        PolylineOptions recorrido = new PolylineOptions().width(5).color(Color.BLUE).geodesic(true);

        for (int i = 0; i<listaRecorridoConsulta.size();i++) {
            System.out.println("Herereererere:::::::::::::::::::::");
            recorrido.add(new LatLng(listaRecorridoConsulta.get(i).getLatitud(), listaRecorridoConsulta.get(i).getLongitud()));
            System.out.println(listaRecorridoConsulta.get(i).getLatitud()+""+ listaRecorridoConsulta.get(i).getLongitud());
        }

        polyline = mMap.addPolyline(recorrido);



        for(int i=0;i<listaParadasConsulta.size();i++) {

            LatLng mLatLng = new LatLng(listaParadasConsulta.get(i).getLatitud(),listaParadasConsulta.get(i).getLongitud());

            mMap.addMarker(new MarkerOptions().zIndex(1).position(mLatLng).title(getAddressFromLatLng(mLatLng)).snippet("Paradero").icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getResources(),R.drawable.paradero))));

        }

        for(int i = 0 ; i<listaSolicitudesConsulta.size();i++){
            marcador[i] = mMap.addMarker(new MarkerOptions().zIndex(2).icon(BitmapDescriptorFactory.fromResource(R.drawable.mantener))
                    .snippet(listaSolicitudesConsulta.get(i).getIdentificador()) .
                    position( new LatLng(listaSolicitudesConsulta.get(i).getLatitud(), listaSolicitudesConsulta.get(i).getLongitud())).visible(true));
        }



        LatLng myLocation = new LatLng(listaRecorridoConsulta.get(0).getLatitud(), listaRecorridoConsulta.get(0).getLongitud());

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLocation,10));
        mMap.animateCamera(CameraUpdateFactory.zoomIn());
        mMap.animateCamera(CameraUpdateFactory.zoomTo(13), 2000, null);

    }

    private void TrazarSolicitudes(){

        if(marcador != null){

            for(int i = 0; i<marcador.length;i++){
                marcador[i].remove();
            }
        }

        marcador = new Marker[listaSolicitudesConsulta.size()];

        for(int i = 0 ; i<listaSolicitudesConsulta.size();i++){
            marcador[i] = mMap.addMarker(new MarkerOptions().zIndex(2).icon(BitmapDescriptorFactory.fromResource(R.drawable.mantener))
                    .snippet(listaSolicitudesConsulta.get(i).getIdentificador()) .
                            position( new LatLng(listaSolicitudesConsulta.get(i).getLatitud(), listaSolicitudesConsulta.get(i).getLongitud())).visible(true));
        }

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

    private void ClicParadas(){
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(final Marker marker) {

                return false;
            }
        });
    }


    public class RealizarGestiones extends AsyncTask<String, Void, String> {
        @Override
        protected void onPostExecute(String s) {

            if(tipo ==1 ) {
                if (listaRecorridoConsulta.size() == 0) {
                    Toast.makeText(vista.getContext(),s,Toast.LENGTH_SHORT).show();
                } else {

                    TrazarRecorrido();

                    if(listaSolicitudesConsulta.size() ==0){
                        Toast.makeText(vista.getContext(), "Aun no tienes solicitudes de paradas", Toast.LENGTH_LONG).show();
                    }

                }
            }
            if(tipo == 2){

                if (s.equals("true")){
                    Toast.makeText(vista.getContext(),"Bien act",Toast.LENGTH_SHORT).show();
                    //TrazarSolicitudes();
                }
                else {
                    Toast.makeText(vista.getContext(),"Mal act",Toast.LENGTH_SHORT).show();
                }
            }


        }

        @Override
        protected String doInBackground(String... strings) {


            if(tipo==1) {//Obtener los valores para trazar el recorrido,paradas y solicitud de paradas.
                gestionesRecorrido = new GestionesRecorrido();
                listaRecorridoConsulta = gestionesRecorrido.ConsultaPorRuta(Identificadores.idRuta);

                gestionesParadas = new GestionesParadas();
                listaParadasConsulta = gestionesParadas.ConsultaPorRuta(Identificadores.idRuta);

                gestionesSolicitudes = new GestionesSolicitudes();
                listaSolicitudesConsulta = gestionesSolicitudes.ConsultaPorRuta(Identificadores.idRuta);

                if (listaRecorridoConsulta.size() > 0) {

                } else {
                    return "Aun no se ha trazado el recorrido para esta ruta";
                }
            }

            if(tipo == 2){//Actualizar ubicacion colectivo

                gestionesUbicacion = new GestionesUbicacion();

                gestionesSolicitudes = new GestionesSolicitudes();
                listaSolicitudesConsulta = gestionesSolicitudes.ConsultaPorRuta(Identificadores.idRuta);

                return gestionesUbicacion.Editar(new Ubicacion(0,latitud,longitud,Identificadores.idColectivo))+"";
            }
            return "";
        }
    }



}
