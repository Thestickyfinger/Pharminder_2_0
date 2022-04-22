package com.example.pharminder_2_0;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;

public class GuardarMedicamento extends AppCompatActivity                                      {
                                                           //implements View.OnClickListener

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.resumen_medicamento);

        // Recuperamos la informacion pasada en el intent
        Bundle bundle = this.getIntent().getExtras();
        String respuesta = bundle.getString("result");
        SetPrescriptionData(respuesta);
    }


    public void SetPrescriptionData(String data) {

        TextView nombre = (TextView) findViewById(R.id.nombre_medicamento);
        TextView p_activo = (TextView) findViewById(R.id.princ_Activo);
        TextView c_presc = (TextView) findViewById(R.id.presc_med);
        //ImageView imagen = (ImageView) findViewById(R.id.imagen_medicamento) ;
        TextView url_prospecto = (TextView) findViewById(R.id.button2);
        TextView via_admin = (TextView) findViewById(R.id.vias_administracion);
        EditText primera_toma = (EditText) findViewById(R.id.primeraToma);
        EditText frecuencia = (EditText) findViewById(R.id.frecuenciaToma);
        EditText ultima_toma = (EditText) findViewById(R.id.ultimaToma);

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
}
