package com.example.franklinsierra.maps.activities;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.example.franklinsierra.maps.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        //obtiene el mapa de manera asincrona
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        //LatLng significa Latitud y longitud
        //draggable es para que se puede arrastrar
        LatLng pesa = new LatLng(7.058971  , -73.085756);
        mMap.addMarker(new MarkerOptions().position(pesa).title("Saludos desde la pesa").draggable(true));


        //establecer limites al zoom (cuando no se quiere que se salga de alguna zona en particular)
        mMap.setMinZoomPreference(10);
        mMap.setMaxZoomPreference(15);

        //juego de la camara
        // el 15 en el zoom es para nivel de calles,
        // el 90 son los grados
        //tilt tambien son grados
        CameraPosition cameraPosition=new CameraPosition.Builder()
                .target(pesa)
                .zoom(5)       //limnite->21
                .bearing(90)    //0-365 grados
                .tilt(30)       //efecto de 3D de 0-90
                .build();

        //hago la animacion de la camara
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));


        //  ++++    Tomar la latitud y longitud de donde se toque en el mapa    ++++    //
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                Toast.makeText(MapsActivity.this,"Clicked in"+"\n"+"Lat:"+latLng.latitude+
                        "\n"+"Long:"+"\n"+latLng.longitude,Toast.LENGTH_SHORT).show();
            }
        });

        //  ++++    Cuando se prolongue el click    ++++    //
        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                Toast.makeText(MapsActivity.this,"Long click in:"+"\n"+"Lat:"+latLng
                        .latitude+"\n"+"Long:"+latLng.longitude+"\n",Toast.LENGTH_SHORT).show();
            }
        });

        //  ++++    Cuando se arrastra un marcador  ++++    //
        mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {

            //Donde comienza el arrastre
            @Override
            public void onMarkerDragStart(Marker marker) {

            }

            //durante el arrastre
            @Override
            public void onMarkerDrag(Marker marker) {

            }

            //Donde finaliza el arrastre
            @Override
            public void onMarkerDragEnd(Marker marker) {
                Toast.makeText(MapsActivity.this,"Drop in:"+"\n"+"Lat:"+marker.getPosition().latitude
                        +"\n"+"Long:"+marker.getPosition().longitude+"\n",Toast.LENGTH_SHORT).show();
            }
        });


        //ubico donde enfoca por defecto
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(pesa));
    }
}
