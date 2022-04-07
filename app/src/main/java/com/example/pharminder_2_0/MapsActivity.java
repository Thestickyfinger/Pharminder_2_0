package com.example.pharminder_2_0;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.location.Location;
import android.os.Bundle;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    // Nuestro mapa
    GoogleMap mMap;

    // para perdir permiso de localización
    private final int REQUEST_PERMISSION_ACCESS_FINE_LOCATION=1;


    // Definimos nuestro escuchador de clicks en el mapa.
    private final GoogleMap.OnMapClickListener mClickListener = new GoogleMap.OnMapClickListener() {

        @Override
        public void onMapClick(LatLng position) {
            // Recibimos las coordenadas del punto del mapa que pulsó el usuario.
            // Para realizar la concatenación de texto forma eficiente, usaremos un objeto de la clase StringBuffer:
            StringBuffer text = new StringBuffer();
            text.append("GPS: ");
            text.append(position.latitude).append(", ").append(position.longitude);
            String coordText = text.toString();

            // Ponemos un marcador en dicha posición.
            // Para esto creamos un objeto MarkerOptions, al que le damos los detalles del nuevo marcador.
            // Después lo añadimos al mapa.
            // Más información sobre marcadores en:
            // https://developers.google.com/maps/documentation/android/marker
            MarkerOptions markerOpts = new MarkerOptions();
            markerOpts.position(position); // ubicación en el mapa (único requisito imprescindible)
            markerOpts.draggable(true); // se le permite ser arrastrado (¡preconfiguración!
            // para hacerlo a posteriori, utilizar Marker.setDraggable(boolean))
            // Se arrastra con una pulsación larga + movimiento sin levantar el dedo
            markerOpts.title("Marker"); // título
            markerOpts.snippet(coordText); // texto complementario al título

            Marker marker = mMap.addMarker(markerOpts);

        }
    }; //Fin de la definición e inicialización del atributo mClickListener

    // Ya que hemos hecho arrastrables los marcadores, creamos también un escuchador que
    // reaccione ante eventos de este tipo
    private final GoogleMap.OnMarkerDragListener mDragListener = new GoogleMap.OnMarkerDragListener(){

        @Override
        public void onMarkerDragStart(Marker marker) {
        }

        @Override
        public void onMarkerDrag(Marker marker) {
        }

        // Sólo sobreescribimos este, que es el que nos interesa para conocer
        // la ubicación _final_ del marcador
        @Override
        public void onMarkerDragEnd(Marker marker) {

            LatLng position = marker.getPosition();

            // De nuevo, pasamos a texto las coordenadas del punto final
            // de forma eficiente, usando un objeto de la clase StringBuffer:
            StringBuffer text = new StringBuffer();
            text.append("GPS: ");
            text.append(position.latitude).append(", ").append(position.longitude);
            String coordText = text.toString();

            // Actualizamos el snippet del marcador, aunque OJO:
            // sólo se va a ver la modificación al volver a pulsar el marcador.
            marker.setSnippet(coordText);
        }
    }; //Fin de la definición e inicialización del atributo mDragListener

    //Resto de código de la clase ActivityWithXMLMapFragment

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Pedimos el objeto GoogleMap de forma asíncrona:
        // lo recibiremos en OnMapReadyCallback.onMapReady()
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }
    /*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }*/

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);

    }

    // Único método de la interfaz OnMapReadyCallback, que implementa nuestra Activity,
    // que debemos rellenar para hacer uso del mapa una vez que está listo
    @Override
    public void onMapReady(GoogleMap googleMap) {

        // Obtenemos el mapa, que nunca será NULL,
        // y ya podemos hacer lo que sea con él
        mMap = googleMap;

        // En primer lugar, registramos los escuchadores de eventos del mapa que hemos creado
        mMap.setOnMapClickListener(mClickListener); // para clicks sobre el mapa
        mMap.setOnMarkerDragListener(mDragListener); // para eventos de arrastre de marcadores

        // Activamos algunos controles en el mapa:
        // (https://developers.google.com/maps/documentation/android/interactivity)

        // Primero pedimos el objeto que nos permite modificar la UI.
        // Todos los cambios efectuados sobre este objeto se reflejan inmediatamente en el mapa
        UiSettings settings = mMap.getUiSettings();

        settings.setZoomControlsEnabled(true); // botones para hacer zoom

        settings.setCompassEnabled(true); // brújula (sólo se muestra el icono si se rota el mapa con los dedos)

        // Comprobamos si tenemos permiso para acceder a la localización
        if (checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true); // botón "My Location"
        } else {
            // no tiene permiso, solicitarlo
            requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_PERMISSION_ACCESS_FINE_LOCATION);
            // cuando se nos conceda el permiso se llamará a onRequestPermissionsResult()
        }


        // Dadas unas coordenadas, centramos el mapa en ellas
        double latitude = 40.416959;
        double longitude = -3.703797;
        centerMap(latitude, longitude);

    }

    public void centerMap(double latitude, double longitude){

        // A partir de una pareja de coordenadas (tipo double) creamos un objeto LatLng,
        // que es el tipo de dato que debemos usar al tratar con mapas
        LatLng position = new LatLng(latitude, longitude);

        // Obtenemos un objeto CameraUpdate que indique el movimiento de cámara que queremos;
        // en este caso, centrar el mapa en unas coordenadas con el método newLatLng()
        CameraUpdate update = CameraUpdateFactory.newLatLng(position);

        // Alternativamente, se puede hacer lo mismo a la vez que se cambia el nivel de zoom
        // (comentar si se desea evitar el zoom)
        float zoom = 18;
        update = CameraUpdateFactory.newLatLngZoom(position, zoom);

        // Más información sobre distintos movimientos de cámara aquí:
        // http://developer.android.com/reference/com/google/android/gms/maps/CameraUpdateFactory.html

        // Pasamos el tipo de actualización configurada al método del mapa que mueve la cámara
        mMap.moveCamera(update);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_PERMISSION_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    finish();
                    startActivity(new Intent(this, this.getClass()));

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }


}