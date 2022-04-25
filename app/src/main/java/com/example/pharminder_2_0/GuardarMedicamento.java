package com.example.pharminder_2_0;


import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


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

    }


    public void SetPrescriptionData(String data) {

        nombre = (TextView) findViewById(R.id.nombre_medicamento);
        p_activo = (TextView) findViewById(R.id.princ_Activo);
        c_presc = (TextView) findViewById(R.id.presc_med);
        //ImageView imagen = (ImageView) findViewById(R.id.imagen_medicamento) ;
        //url_prospecto = (TextView) findViewById(R.id.button2);
        via_admin = (TextView) findViewById(R.id.vias_administracion);
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

    public void saveNoteFromGuardar(View view) {
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
        finish();
    }

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

}