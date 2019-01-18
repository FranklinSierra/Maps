package com.example.franklinsierra.maps.fragments;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.franklinsierra.maps.R;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

// Le implemento el OnMapReadyCallback para completar el ciclo de vida del mapa
//Tambien el GoogleMap.OnMarkerDragListener para cuando se arrastre el marcador
public class MapFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMarkerDragListener {

    private View rootView;
    private MapView mapView;
    private GoogleMap gMap;

    //para obtener la direccion e info de un sitio cuando se suelte el marcador (arrastre)
    private List<Address> addresses;
    private Geocoder geocoder;  //se encarga de recoger la informacion dependiendo de la latitud,longitud

    //para personalizar el marcador
    private MarkerOptions marker;

    //FAB que me lleva a la configuracion del GPS
    FloatingActionButton fab;


    public MapFragment() {
    }


    //  ++++    hace parte del ciclo de vida    ++++    //
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_map, container, false);
        fab = (FloatingActionButton) rootView.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkifGPSEnabled();
            }
        });
        return rootView;
    }

    //  ++++    hace parte del ciclo de vida (cuando se tiene cargado el view    ++++    //
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //aca va el otro 30% de la implementacion del mapa
        mapView = (MapView) rootView.findViewById(R.id.map);
        if (mapView != null) {
            //Ciclo de vida
            mapView.onCreate(null);
            mapView.onResume();
            mapView.getMapAsync(this);
        }


    }

    // metodo que crea un loop que no deja salir al usuario hasta que no habilite el GPS
    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        //configuracion del mapa y el marcador
        gMap = googleMap;
        LatLng place = new LatLng(7.058971, -73.085757);

        //personalizo el marcador
        marker = new MarkerOptions();
        marker.title("Mi marcador");
        //caja de contenido
        marker.snippet("esto es una caja de texto donde se pueden modificar los datos");
        marker.position(place);
        marker.draggable(true);
        marker.icon(BitmapDescriptorFactory.fromResource(android.R.drawable.ic_menu_compass));

        //nivel de zoom
        CameraUpdate zoom = CameraUpdateFactory.zoomTo(15);

        //le paso al mapa el marcador
        gMap.addMarker(marker);
        gMap.moveCamera(CameraUpdateFactory.newLatLng(place));
        gMap.animateCamera(zoom);

        //llamo al metodo cuando arrastren al marcador
        gMap.setOnMarkerDragListener(this);

        //instancio el encargado de retornar la info
        geocoder = new Geocoder(getContext(), Locale.getDefault());
    }

    //  ++++    Metodo para configurar el GPS   ++++    //
    private void checkifGPSEnabled() {
        try {
            int gpsSignal = Settings.Secure.getInt(getActivity().getContentResolver(),
                    Settings.Secure.LOCATION_MODE);

            if (gpsSignal == 0) {
                //quiere decir que no esta habilitado el GPS
                showAlertGps();
            }
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void showAlertGps() {

        new AlertDialog.Builder(getContext())
                .setTitle("GPS signal")
                .setMessage("you don't have signal GPS, Would you like setting it now?")
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        final Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(intent);
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();



       /* AlertDialog.Builder dialog= new AlertDialog.Builder(getActivity());
        dialog.setTitle("GPS signal")
                .setMessage("you don't have signal GPS, Would you like setting it now?")
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        final Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(intent);
                    }
                }).setNegativeButton("Cancel", null);
        dialog.create();*/
    }

    @Override
    public void onMarkerDragStart(Marker marker) {

        //cuando se comience a arrastrar
        marker.hideInfoWindow();    //oculta el snippet
    }

    @Override
    public void onMarkerDrag(Marker marker) {

    }

    @Override
    public void onMarkerDragEnd(Marker marker) {
        double latitude = marker.getPosition().latitude;
        double longitude = marker.getPosition().longitude;

        //Recojo solo un item de la lista de info con el 1
        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }

        //Recojo la informacion que desee
        String address = addresses.get(0).getAddressLine(0);
        String country = addresses.get(0).getCountryName();
        String state = addresses.get(0).getAdminArea();
        String codePostal = addresses.get(0).getPostalCode();
        String city = addresses.get(0).getLocality();

        //Puedo usar el getSnippet
        marker.setSnippet(city + " " + country);
        //muestro el snippet
        marker.showInfoWindow();

        /*Toast.makeText(getContext(), "address" + address + "\n" +
                "country: " + country + "\n" +
                "state: " + state + "\n" +
                "codePostal: " + codePostal + "\n" +
                "city: " + city + "\n", Toast.LENGTH_LONG).show();

*/
    }


}
