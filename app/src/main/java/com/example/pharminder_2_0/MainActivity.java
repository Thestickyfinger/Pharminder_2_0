package com.example.pharminder_2_0;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import org.json.*;
import org.w3c.dom.Text;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Build;
import android.widget.TextView;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener, CalendarAdapter.OnItemListener {

    private static final String API_URL  = "https://cima.aemps.es/cima/rest/medicamento";

    private NotesDbAdapter dbAdapter;
    private ListView m_listview;
    private TextView monthYearText;
    private RecyclerView calendarRecyclerView;
    private LocalDate selectedDate;
    private TextView tvBarCode;



    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //inflamos el layout
        setContentView(R.layout.activity_notepad);

        //creamos el adaptador de la BD y la abrimos
        dbAdapter = new NotesDbAdapter(this);
        dbAdapter.open();

        //Codigo del CalendarActivity
        initWidgets();
        selectedDate = LocalDate.now();
        setMonthView();

        // Creamos un listview que va a contener el título de todas las notas y
        // en el que cuando pulsemos sobre un título lancemos una actividad de editar
        // la nota con el id correspondiente
        m_listview = (ListView) findViewById(R.id.id_list_view);
        m_listview.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> arg0, View view, int position, long id) {
                        Intent i = new Intent(view.getContext(), com.example.pharminder_2_0.EditActivity.class);
                        i.putExtra(NotesDbAdapter.KEY_ROWID, id);
                        startActivityForResult(i, 1);
                    }
                }
        );

        // rellenamos el listview con los títulos de todas las notas en la BD
        fillData();
    }

    private void fillData() {
        Cursor notesCursor = dbAdapter.fetchAllNotes();

        // Creamos un array con los campos que queremos mostrar en el listview (sólo el título de la nota)
        String[] from = new String[]{NotesDbAdapter.KEY_TITLE};

        // array con los campos que queremos ligar a los campos del array de la línea anterior (en este caso sólo text1)
        int[] to = new int[]{R.id.text1};

        // Creamos un SimpleCursorAdapter y lo asignamos al listview para mostrarlo
        SimpleCursorAdapter notes =
                new SimpleCursorAdapter(this, R.layout.notes_row, notesCursor, from, to, 0);
        m_listview.setAdapter(notes);
    }

    //-------------------Escaneo Cod-Barras y Busqueda en Api------------------------------

    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        fillData();

        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent );
        if(result != null)
            if (result.getContents() != null){
                String resultindex = result.getContents().substring(6,12);
                tvBarCode = findViewById(R.id.resultado);

                APIFromCIMATask api = new APIFromCIMATask();
                api.cn = resultindex;
                api.execute();

                tvBarCode.setText("El código de barras es:\n" + resultindex);
            }else{
                Toast.makeText(this, "Scanning cancelled", Toast.LENGTH_LONG).show();
            }
    }

    public void SetPrescriptionData(String data) {

        setContentView(R.layout.resumen_medicamento);
        TextView nombre = (TextView) findViewById(R.id.nombre_medicamento);
        TextView p_activo = (TextView) findViewById(R.id.princ_Activo);
        TextView c_presc = (TextView) findViewById(R.id.presc_med);
        //ImageView imagen = (ImageView) findViewById(R.id.imagen_medicamento) ;
        TextView url_prospecto = (TextView) findViewById(R.id.button2);
        TextView via_admin = (TextView) findViewById(R.id.vias_administracion);

        JSONObject obj = null;
        String jsonString = data;
        String nombreMedicamento = null;
        String pActivo = null;
        String cPresc = null;
        String urlProspecto = null;
        String viasAdministracion = null;
        //ArrayList<String> viasAdministracion = new ArrayList<String>();
        //String urlImagenMedicamento = null;



        try {
            obj = new JSONObject(jsonString);
            nombreMedicamento = obj.getString("nombre");
            pActivo = obj.getString("pactivos");
            cPresc = obj.getString("cpresc");
            //JSONArray documentosArray = obj.getJSONArray("docs");
            JSONArray viasAdminArray = obj.getJSONArray("viasAdministracion");

            //Loop para buscar PROSPECTO
            /*for (int i = 0; i < documentosArray.length(); i++)
            {
                if(documentosArray.getJSONObject(i).getInt("tipo")==2){
                    urlProspecto = documentosArray.getJSONObject(i).getString("url");
                }
            }*/
            //Loop para buscar VIAS DE ADMINISTRACION
            for (int i = 0; i < viasAdminArray.length(); i++)
            {
                //viasAdministracion.add(viasAdminArray.getJSONObject(i).getString("nombre"));
                viasAdministracion = viasAdminArray.getJSONObject(i).getString("nombre");
            }


        } catch (JSONException e) {
            e.printStackTrace();
            nombreMedicamento    = "ERROR: " + e.getLocalizedMessage();
        }

        nombre.setText(nombreMedicamento);
        p_activo.setText(pActivo);
        c_presc.setText(cPresc);
        via_admin.setText(viasAdministracion);
        //Loop para escribir lista de VIAS DE ADMINISTRACION
        /*for(int i = 0;i < viasAdministracion.size(); i++){
            vias_administracion = viasAdministracion.get(i);
        }*/

        //imagen.setImage();



    }

    private class APIFromCIMATask extends AsyncTask<String, String, String> {

        String cn;
        String response;

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        protected String doInBackground(String... urls) {
            // We make the connection
            try {
                // Creamos la conexión
                URL url = new URL(API_URL + "?cn=" + cn);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                conn.setRequestMethod("GET");
                conn.setRequestProperty("Content-Type", "application/json");
//                conn.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));
//                conn.setDoOutput(true);

                Reader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
                response = "";

                for (int c; (c = in.read()) >= 0; )
                    response += (char) c;

            } catch (IOException e) {
                e.printStackTrace();
                response = "ERROR: " + e.getLocalizedMessage();
            }

//            SetPrescriptionData(response);
            Log.i("RECEIVED", response);

            return response;
        }

        protected void onPostExecute(String result){
            SetPrescriptionData(result);
        }
    }


    //---------------------Creamos menu con las tres opciones de añadir------------------

    public void showPopup(View view) {
        PopupMenu popup = new PopupMenu(this, view);
        popup.setOnMenuItemClickListener(this);
        popup.inflate(R.menu.popupmenu);
        popup.show();
        LinearLayout dim_layout = (LinearLayout) findViewById(R.id.dim_layout);
        dim_layout.setVisibility(View.VISIBLE);

    }


    @Override
    public boolean onMenuItemClick(MenuItem item) {

        /* ESto no funciona del todo porque cuando pulsas fuera no vuelve a iluminarse*/

        LinearLayout dim_layout = (LinearLayout) findViewById(R.id.dim_layout);
        dim_layout.setVisibility(View.INVISIBLE);

        switch (item.getItemId()) {
            case R.id.item1:
                Toast.makeText(this, "Codigo de barras", Toast.LENGTH_SHORT);
               escanear();
                return true;

            case R.id.item2:
                Toast.makeText(this, "Codigo nacional", Toast.LENGTH_SHORT);
                switchMaintocalendar();
                 return true;

            case R.id.item3:
                Toast.makeText(this, "Nombre del medicamento", Toast.LENGTH_SHORT);
                createNote();

                return true;
            case R.id.action_settings:
                Toast.makeText(this, "setiings", Toast.LENGTH_SHORT);
                switchMaintoSettings();
                return true;
            default:

                return false;
        }
    }
    //menu para setting


    public void  escanear(){
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.initiateScan();
    }


    public void openMaps(View view) {
        // Do something in response to button
        switchMaintoMaps();

    }

    private void switchMaintoMaps() {

        startActivity(new Intent(MainActivity.this, MapsActivity.class));

    }



    private void switchMaintocalendar() {

        startActivity(new Intent(MainActivity.this, CalendarActivity.class));

    }
    private void switchMaintoSettings() {

        startActivity(new Intent(MainActivity.this, SettingsActivity.class));

    }


    private void createNote() {
        Intent i = new Intent(this, com.example.pharminder_2_0.EditActivity.class);
        startActivityForResult(i, 1);
    }
    //Creamos menu settings con las tres opciones de añadir

    public void showsettings(View view) {
        PopupMenu popupsettings = new PopupMenu(this, view);
        popupsettings.setOnMenuItemClickListener(this);
        popupsettings.inflate(R.menu.menu_settings);
        popupsettings.show();

    }


    //----------------Codigo del CalendarActivity---------------

    private void initWidgets()
    {
        calendarRecyclerView = findViewById(R.id.calendarRecycleView);
        monthYearText = findViewById(R.id.monthYearTV);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setMonthView()
    {
        monthYearText.setText(monthYearFromDate(selectedDate));
        ArrayList<String> daysInMonth = daysInMonthArray(selectedDate);

        CalendarAdapter calendarAdapter = new CalendarAdapter(daysInMonth, this);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getApplicationContext(), 7);
        calendarRecyclerView.setLayoutManager(layoutManager);
        calendarRecyclerView.setAdapter(calendarAdapter);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private ArrayList<String> daysInMonthArray(LocalDate date)
    {
        ArrayList<String> daysInMonthArray = new ArrayList<>();
        YearMonth yearMonth = YearMonth.from(date);

        int daysInMonth = yearMonth.lengthOfMonth();

        LocalDate firstOfMonth = selectedDate.withDayOfMonth(1);
        int dayOfWeek = firstOfMonth.getDayOfWeek().getValue();

        for(int i = 1; i <= 42; i++)
        {   //Antes había:
            //if(i <= dayOfWeek || i > daysInMonth + dayOfWeek)
            if(i < dayOfWeek || i > daysInMonth + dayOfWeek - 1)
            {
                daysInMonthArray.add("");
            }
            else
            {   //daysInMonthArray.add(String.valueOf(i - dayOfWeek));
                daysInMonthArray.add(String.valueOf(i - dayOfWeek + 1));
            }
        }
        return  daysInMonthArray;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private String monthYearFromDate(LocalDate date)
    {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM yyyy");
        return date.format(formatter);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void previousMonthAction(View view)
    {
        selectedDate = selectedDate.minusMonths(1);
        setMonthView();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void nextMonthAction(View view)
    {
        selectedDate = selectedDate.plusMonths(1);
        setMonthView();
    }

    @Override
    public void onItemClick(int position, String daytex) {
        /*if(!dayText.equals(""))
        {
            String message = "Selected Date " + dayText + " " + monthYearFromDate(selectedDate);
            Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        }*/

    }
}

