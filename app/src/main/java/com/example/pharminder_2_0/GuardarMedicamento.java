package com.example.pharminder_2_0;


import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.Random;


public class GuardarMedicamento extends AppCompatActivity                                      {
    //implements View.OnClickListener

    private TextView nombre;
    private TextView p_activo;
    private TextView c_presc;
    private String url_prospecto;
    private TextView via_admin;

    private TextView mTitleText;
    private TextView mBodyText;
    private Long mRowId;
    private NotesDbAdapter dbAdapter;
    //variables para las alarmas
    Calendar c = Calendar.getInstance();
    int cyear = c.get(Calendar.YEAR);
    int cmonth = c.get(Calendar.MONTH);
    int cday = c.get(Calendar.DAY_OF_MONTH);
    int hour = c.get(Calendar.HOUR_OF_DAY);
    int minute = c.get(Calendar.MINUTE);
    int year, month, day, year1, month1, day1;
    Button timeButton, dateButton, dateButton1, numberButton;
    private NumberPicker picker1;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.resumen_medicamento);

        //creamos el adaptador de la BD y la abrimos
        dbAdapter = new NotesDbAdapter(this);
        dbAdapter.open();


        Bundle bundle = this.getIntent().getExtras();
        String respuesta = bundle.getString("Result");
        SetPrescriptionData(respuesta);

        //Para las alarmas
        timeButton = findViewById(R.id.timeButton);
        dateButton = findViewById(R.id.dateButton);
        dateButton1 = findViewById(R.id.dateButton1);
        picker1 = findViewById(R.id.np);
        picker1.setMaxValue(24);
        picker1.setMinValue(1);
        picker1.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int i, int i1) {
                int valuePicker1 = picker1.getValue();
                Log.d("picker value", String.valueOf(valuePicker1));
            }
        });
    }


    public void SetPrescriptionData(String data) {

        nombre = (TextView) findViewById(R.id.nombre_medicamento);
        p_activo = (TextView) findViewById(R.id.princ_Activo);
        c_presc = (TextView) findViewById(R.id.presc_med);
        //ImageView imagen = (ImageView) findViewById(R.id.imagen_medicamento) ;
        //url_prospecto = (TextView) findViewById(R.id.button2);
        via_admin = (TextView) findViewById(R.id.vias_administracion);


        JSONObject obj = null;
        String jsonString = data;
        String nombreMedicamento = null;
        String pActivo = null;
        String cPresc = null;
        String urlProspecto = null;
        String viasAdministracion = null;
        //ArrayList<String> viasAdministracion = new ArrayList<String>();
        //String urlImagenMedicamento = null;
        if (data == null){

        }


        try {
            obj = new JSONObject(jsonString);
            nombreMedicamento = obj.getString("nombre");
            pActivo = obj.getString("pactivos");
            cPresc = obj.getString("cpresc");
            JSONArray documentosArray = obj.getJSONArray("docs");
            JSONArray viasAdminArray = obj.getJSONArray("viasAdministracion");

            //Loop para buscar PROSPECTO
            for (int i = 0; i < documentosArray.length(); i++)
            {
                if(documentosArray.getJSONObject(i).getInt("tipo")==2){
                    urlProspecto = documentosArray.getJSONObject(i).getString("url");
                }
            }
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
        url_prospecto = urlProspecto;
        //Loop para escribir lista de VIAS DE ADMINISTRACION
        /*for(int i = 0;i < viasAdministracion.size(); i++){
            vias_administracion = viasAdministracion.get(i);
        }*/

        //imagen.setImage();



    }

    //----------------------------------Para hacer las barras de seleccion de 1ªToma, Frecuencia y ultima toma ----------------------

   /* EditText primera_toma = (EditText) findViewById(R.id.primeraToma);
    EditText frecuencia = (EditText) findViewById(R.id.frecuenciaToma);
    EditText ultima_toma = (EditText) findViewById(R.id.ultimaToma);
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.primeraToma:
                showDatePickerDialog();
                break;
            case R.id.frecuenciaToma:
                break;
            case R.id.ultimaToma:
                break;
        }
    }
    public static class DatePickerFragment extends DialogFragment {
        private DatePickerDialog.OnDateSetListener listener;
        public static DatePickerFragment newInstance(DatePickerDialog.OnDateSetListener listener) {
            DatePickerFragment fragment = new DatePickerFragment();
            fragment.setListener(listener);
            return fragment;
        }
        public void setListener(DatePickerDialog.OnDateSetListener listener) {
            this.listener = listener;
        }
        @Override
        @NonNull
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);
            return new DatePickerDialog(getActivity(), listener, year, month, day);
        }
    }*/

    public void  abrirurl(View view){


        Uri uri = Uri.parse(url_prospecto); // missing 'http://' will cause crashed
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }

    //-----------------------------------Guardar Medicamento en la Base de Datos----------------------------------



    //--------------------Crea Menu de opciones dentro del medicamento------------------------

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Se recrea el menu que aparece en ActionBar de la actividad.
        getMenuInflater().inflate(R.menu.menu_edit, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Gestiona la seleccion de opciones en el menú
        int id = item.getItemId();
        if (id == R.id.action_delete) {
            if (mRowId != null) {
                dbAdapter.deleteNote(mRowId);
            }
            setResult(RESULT_OK);
            dbAdapter.close();
            finish();
        }

        if (id == R.id.action_about) {
            System.out.println("APPMOV: About action...");
        }

        return super.onOptionsItemSelected(item);
    }


//Codigo para las alarmas
//  Metodo que procesa la pulsacion (onClick) del boton
//  se indica en el atributo "android:onClick" del elemento Button definido en XML
public void sendName(View view) {


    String title = nombre.getText().toString();
    Log.i("----------------------RECEIVED", title);
    String pactivo = p_activo.getText().toString();
    Log.i("----------------------RECEIVED", pactivo);
    String prescripcion = c_presc.getText().toString();
    Log.i("----------------------RECEIVED", prescripcion);
    String viadministracion = via_admin.getText().toString();
    Log.i("----------------------RECEIVED", viadministracion);


    if (mRowId == null) {
        long id = dbAdapter.createNote(title, pactivo, prescripcion, viadministracion);
        if (id > 0) {
            mRowId = id;
        }
    } else {
        dbAdapter.updateNote(mRowId, title, pactivo, prescripcion, viadministracion);
    }
    setResult(RESULT_OK);
    dbAdapter.close();

    //empieza el codigo de las alarmas
    double diferenciadias1 = (year - cyear) * 365 + (month - cmonth) * 12 + (day - cday);
    double diferenciadias2 = (year1 - year) * 365 + (month1 - month) * 12 + (day1 - day);
    double diferenciadias3 = (year1 - cyear) * 365 + (month1 - cmonth) * 12 + (day1 - cday);

    int mHour = c.get(Calendar.HOUR_OF_DAY);
    int mMinute = c.get(Calendar.MINUTE);
    Context context = getApplicationContext();
    ArrayList<PendingIntent> intentArray = new ArrayList<PendingIntent>();
    AlarmManager alarms =
            (AlarmManager) getSystemService(Context.ALARM_SERVICE);
    //hago un loop para cada alarma
    long frecuencia = picker1.getValue() * 3600000;
    long timeOrLengthofWait = (long) ((((hour - mHour) * 60 + minute - mMinute) * 60000)+ diferenciadias1*86400000);
    if(timeOrLengthofWait<0){
        timeOrLengthofWait+=24*3600000;
    }

    //Creamos notificación
    NotificationManager notificationManager;

    // crea canal de notificaciones
    NotificationCompat.Builder mBuilder =
            new NotificationCompat.Builder(this.getApplicationContext(), "com.uc3m.it.helloallarmappmov.notify_001");

    //pendingIntent para abrir la actividad cuando se pulse la notificación
    //pendingIntent para abrir la actividad cuando se pulse la notificación
    Intent ii = new Intent(this.getApplicationContext(), MainActivity.class);
    PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, ii, PendingIntent.FLAG_IMMUTABLE);

    mBuilder.setContentIntent(pendingIntent);
    mBuilder.setSmallIcon(R.mipmap.ic_launcher_round);
    mBuilder.setContentTitle("Alarma activada");
    if (timeOrLengthofWait <= 0) {
    } else if (timeOrLengthofWait <= 60000) {
        mBuilder.setContentText("Alarma activada para dentro de " + timeOrLengthofWait / 1000 + " segundos");
    } else if (timeOrLengthofWait <= 3600000) {
        mBuilder.setContentText("Alarma activada para dentro de " + timeOrLengthofWait / 60000 + " minutos");
    } else if  (timeOrLengthofWait <= 86400000){
        mBuilder.setContentText("Alarma activada para dentro de " + timeOrLengthofWait / 3600000 + " horas");
    }else{
        mBuilder.setContentText("Alarma activada para dentro de " + timeOrLengthofWait / (3600000*24) + " dias");
    }
    notificationManager =
            (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        String channelId = "YOUR_CHANNEL_ID";
        NotificationChannel channel = new NotificationChannel(channelId,
                "Canal de HelloAlarmAppMov",
                NotificationManager.IMPORTANCE_DEFAULT);
        notificationManager.createNotificationChannel(channel);
        mBuilder.setChannelId(channelId);
    }

    notificationManager.notify(1, mBuilder.build());

    // Programamos la alarma
    Random random = new Random();

    int m = random.nextInt(9999 - 1000) + 1000;

    int alarmType = AlarmManager.ELAPSED_REALTIME_WAKEUP;


    Intent intentToFire = new Intent(this, MyBroadcastReceiver.class);
    intentToFire.putExtra("NAME", nombre.getText().toString());
    PendingIntent AlarmPendingIntent = PendingIntent.getBroadcast(this, m, intentToFire, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);
    intentArray.add(AlarmPendingIntent);
    alarms.setRepeating(alarmType, timeOrLengthofWait, frecuencia, AlarmPendingIntent);
    if (diferenciadias1 < 0) {
        alarms.cancel(AlarmPendingIntent);
        Snackbar mySnackbar = Snackbar.make(findViewById(R.id.snack), "Establezca una fecha de inicio posterior a la actual", 5000);
        mySnackbar.show();
    }
    if (diferenciadias2 < 0) {
        alarms.cancel(AlarmPendingIntent);
        Snackbar mySnackbar = Snackbar.make(findViewById(R.id.snack), "Establezca una fecha de fin posterior a la de inicio", 5000);
        mySnackbar.show();
    }
    if (diferenciadias3 < 0) {
        alarms.cancel(AlarmPendingIntent);
        Snackbar mySnackbar = Snackbar.make(findViewById(R.id.snack), "Establezca una fecha de fin posterior a la actual", 5000);
        mySnackbar.show();
    }
    finish();
}


    @Override
    public void onDestroy() {
        super.onDestroy();
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(0);
    }

    public void PopTimeTicker(View view) {
        TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override

            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                hour = selectedHour;
                minute = selectedMinute;
                timeButton.setText(String.format(Locale.getDefault(), "%02d:%02d", hour, minute));
            }
        };
        int mHour = c.get(Calendar.HOUR_OF_DAY);
        int mMinute = c.get(Calendar.MINUTE);
        TimePickerDialog timePickerDialog = new TimePickerDialog(this, /*style,*/ onTimeSetListener, mHour, mMinute, true);
        timePickerDialog.setTitle("Select Time");
        timePickerDialog.show();
    }

    public void DateTicker(View view) {
        DatePickerDialog.OnDateSetListener onDateSetListener = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker datePicker, int selecteyear, int selectemonth, int selecteday) {
                day = selecteday;
                month = selectemonth;
                year = selecteyear;
                dateButton.setText(String.format(Locale.getDefault(), "%02d/%02d/%02d", day, month, year));
            }
        };
        DatePickerDialog DatePickerDialog = new DatePickerDialog(this, /*style,*/ onDateSetListener, cyear, cmonth, cday);

        DatePickerDialog.setTitle("Select Date");
        DatePickerDialog.show();
    }

    public void DateTicker2(View view) {
        DatePickerDialog.OnDateSetListener onDateSetListener = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker datePicker, int selecteyear1, int selectemonth1, int selecteday1) {
                day1 = selecteday1;
                month1 = selectemonth1;
                year1 = selecteyear1;
                dateButton1.setText(String.format(Locale.getDefault(), "%02d/%02d/%02d", day1, month1, year1));
            }
        };
        DatePickerDialog DatePickerDialog = new DatePickerDialog(this, /*style,*/ onDateSetListener, cyear, cmonth, cday);

        DatePickerDialog.setTitle("Select Date");
        DatePickerDialog.show();
    }
}






