package com.example.pharminder_2_0;

import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

// ----------------------------------YA NO SE USA------------------------------------------------------

public class EditActivity extends AppCompatActivity {

    private TextView titleText;
    private TextView bodyText;
    private Long mRowId;
    private NotesDbAdapter dbAdapter;

    private TextView c_presc;
    private TextView via_admin;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // infla el layout
        setContentView(R.layout.activity_edit);

       /* // Recuperamos la informacion pasada en el intent
        Bundle bundle = this.getIntent().getExtras();
        String respuesta = bundle.getString("result");
        SetPrescriptionData(respuesta);*/

        // obtiene referencia a los tres views que componen el layout
        titleText = (TextView) findViewById(R.id.title);
        bodyText = (TextView) findViewById(R.id.body);
        c_presc = (TextView) findViewById(R.id.prescripcionmedica);
        via_admin = (TextView) findViewById(R.id.viasdadministracion);

        //creamos el adaptador de la BD y la abrimos
        dbAdapter = new NotesDbAdapter(this);
        dbAdapter.open();

        // obtiene id de fila de la tabla si se le ha pasado (hemos pulsado una nota para editarla)
        mRowId = (savedInstanceState == null) ? null :
                (Long) savedInstanceState.getSerializable(NotesDbAdapter.KEY_ROWID);
        if (mRowId == null) {
            Bundle extras = getIntent().getExtras();
            mRowId = extras != null ? extras.getLong(NotesDbAdapter.KEY_ROWID) : null;
        }
        Throwable msg = null;
        Log.i(String.valueOf(msg), "---------------------------------mRowId: " + mRowId);
        // Si se le ha pasado un id (no era null) rellena el título y el cuerpo con los campos guardados en la BD
        // en caso contrario se dejan en blanco (editamos una nota nueva)
        if (mRowId != null) {
            Cursor note = dbAdapter.fetchNote(mRowId);
            titleText.setText(note.getString(
                    note.getColumnIndexOrThrow(NotesDbAdapter.KEY_TITLE)));
            bodyText.setText(note.getString(
                    note.getColumnIndexOrThrow(NotesDbAdapter.KEY_BODY)));
            c_presc.setText(note.getString(
                    note.getColumnIndexOrThrow(NotesDbAdapter.KEY_PRESCRIPCION)));
            via_admin.setText(note.getString(
                    note.getColumnIndexOrThrow(NotesDbAdapter.KEY_VIADMIN)));
        }
    }

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


    public void saveNote(View view) {
        String title = titleText.getText().toString();
        String body = bodyText.getText().toString();
        String prescripcion = c_presc.getText().toString();
        String viadministracion = via_admin.getText().toString();

        if (mRowId == null) {
            long id = dbAdapter.createNote(title, body, prescripcion, viadministracion);
            if (id > 0) {
                mRowId = id;
            }
        } else {
            dbAdapter.updateNote(mRowId, title, body, prescripcion, viadministracion);
        }
        setResult(RESULT_OK);
        dbAdapter.close();
        finish();
    }

    //------------------Para juntar GuardarMedicamento con EditActivity, pero no lo hemos conseguido-------------------

    /*public void SetPrescriptionData(String data) {
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
            *//*for (int i = 0; i < documentosArray.length(); i++)
            {
                if(documentosArray.getJSONObject(i).getInt("tipo")==2){
                    urlProspecto = documentosArray.getJSONObject(i).getString("url");
                }
            }*//*
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
        *//*for(int i = 0;i < viasAdministracion.size(); i++){
            vias_administracion = viasAdministracion.get(i);
        }*//*
        //imagen.setImage();
    }*/

}