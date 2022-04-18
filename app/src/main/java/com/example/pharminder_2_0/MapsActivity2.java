package com.example.pharminder_2_0;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.location.Location;
import android.os.AsyncTask;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;

public class MapsActivity2 extends FragmentActivity{

    // Nuestro mapa
    GoogleMap mMap;
    //Para NearbyPlaces
    Button btFind;
    Spinner spType;
    FusedLocationProviderClient fusedLocationProviderClient;
    SupportMapFragment supportMapFragment;
    //Iniciamos latitud y longitud
    double latitude = 40.416959;
    double longitude = -3.703797;

    // para perdir permiso de localización
    private final int REQUEST_PERMISSION_ACCESS_FINE_LOCATION = 1;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        //Para el NearbyPlaces
        spType = findViewById(R.id.sp_type);
        btFind = findViewById(R.id.bt_find);
        supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        String[] placeTypeList = {"atm", "bank"};
        String[] placeNameList = {"ATM", "Bank"};
        //Hacemos el adaptador para el spinner
        spType.setAdapter(new ArrayAdapter<>(MapsActivity2.this, android.R.layout.simple_spinner_dropdown_item, placeNameList));
        //Inicializamos fused
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        // Comprobamos si tenemos permiso para acceder a la localización
        if (ActivityCompat.checkSelfPermission(MapsActivity2.this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
            getCurrentLocation();
            // botón "My Location"
        } else {
            // no tiene permiso, solicitarlo
            ActivityCompat.requestPermissions(MapsActivity2.this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 44);
            // cuando se nos conceda el permiso se llamará a onRequestPermissionsResult()
        }

        btFind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //get selected position of spinner
                int i = spType.getSelectedItemPosition();
                //Inicializamos URL
                String url = "https://maps.googleapis.com/maps/api/..." //URL
                        + "?location=" + latitude + "," + longitude //posicion en la que nos encontramos
                        + "&radius=5000"  //radio en el que queremos buscar
                        + "&type=" + placeTypeList[i] //Tipo de sitio que queremos buscar
                        + "&sensor=true" + "&key=" + getResources().getString(R.string.google_map_key);
                new PlaceTask().execute(url);
            }
        });

    }
    private void getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if(location!=null){
                    latitude = location.getLatitude();
                    longitude = location.getLongitude();

                    supportMapFragment.getMapAsync(new OnMapReadyCallback(){
                        @Override
                        public void onMapReady(@NonNull GoogleMap googleMap) {
                            // Obtenemos el mapa, que nunca será NULL,
                            // y ya podemos hacer lo que sea con él
                            mMap = googleMap;

                            // En primer lugar, registramos los escuchadores de eventos del mapa que hemos creado
                            //mMap.setOnMapClickListener(mClickListener); // para clicks sobre el mapa
                            //mMap.setOnMarkerDragListener(mDragListener); // para eventos de arrastre de marcadores

                            // Activamos algunos controles en el mapa:
                            // (https://developers.google.com/maps/documentation/android/interactivity)

                            // Primero pedimos el objeto que nos permite modificar la UI.
                            // Todos los cambios efectuados sobre este objeto se reflejan inmediatamente en el mapa
                            UiSettings settings = mMap.getUiSettings();

                            //Zoom to current location
                            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude,longitude), 10));

                            settings.setZoomControlsEnabled(true); // botones para hacer zoom

                            settings.setCompassEnabled(true); // brújula (sólo se muestra el icono si se rota el mapa con los dedos)
                        }
                    });
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 44) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //Cuaando tengamos permiso
                //LLamamos al meetodo
                getCurrentLocation();
            }

        }
    }

    private class PlaceTask extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String... strings) {
            String data = null;
            try {
                //Inicializamos los datos
                data = downloadUrl(strings[0]);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return data;
        }

        @Override
        protected void onPostExecute(String s) {
            //Execute parser task
            new ParserTask().execute(s);
        }
    }
    private String downloadUrl(String string) throws IOException{
        //Inicializamos URL
        URL url = new URL(string);
        //Inicializamos la conexcion
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        //Conexión
        connection.connect();
        //Inicializamos input stream
        InputStream stream = connection.getInputStream();
        //Initialize Buffer Reader
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        //Initialize string builder
        StringBuilder builder = new StringBuilder();
        //Initialize String Variable
        String line = "";

        while((line = reader.readLine()) != null){
            builder.append(line);
        }
        //Get append data
        String data = builder.toString();
        //Close reader
        reader.close();
        //Returnn data
        return data;
    }

    private class ParserTask extends AsyncTask<String,Integer, List<HashMap<String,String>>> {
        @Override
        protected List<HashMap<String, String>> doInBackground(String... strings) {
            //Create json parser class
            JSonParser jSonParser = new JSonParser();
            //initialize Hash Map List
            List<HashMap<String,String>> mapList = null;
            JSONObject object = null;
            try {
                //initialize json object
                object = new JSONObject(strings[0]);
                //parse json object
                mapList = jSonParser.parseResult(object);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            //Return maplist
            return mapList;
        }

        @Override
        protected void onPostExecute(List<HashMap<String, String>> hashMaps) {
            //Clear Map
            mMap.clear();
            //Use for Loop
            for(int i = 0; i<hashMaps.size();i++){
                //initialize HashMap
                HashMap<String,String > hashMapList = hashMaps.get(i);
                //get latitude
                double lat = Double.parseDouble(hashMapList.get("lat"));
                //get longitude
                double lng = Double.parseDouble(hashMapList.get("lng"));
                // get name
                String name = hashMapList.get("name");
                //concat latitude and longitude
                LatLng latLng = new LatLng(lat, lng);
                //Initialize marker options
                MarkerOptions options = new MarkerOptions();
                //set position
                options.position(latLng);
                //set title
                options.title(name);
                //add marker to map
                mMap.addMarker(options);
            }
        }
    }

}









