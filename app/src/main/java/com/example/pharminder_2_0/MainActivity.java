package com.example.pharminder_2_0;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener {

    private NotesDbAdapter dbAdapter;
    private ListView m_listview;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //inflamos el layout
        setContentView(R.layout.activity_notepad);

        //creamos el adaptador de la BD y la abrimos
        dbAdapter = new NotesDbAdapter(this);
        dbAdapter.open();

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        fillData();
    }

    public void showPopup(View view) {
        PopupMenu popup = new PopupMenu(this, view);
        popup.setOnMenuItemClickListener(this);
        popup.inflate(R.menu.popupmenu);
        popup.show();

    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.item1:
                Toast.makeText(this, "Codigo de barras", Toast.LENGTH_SHORT);
                return true;

            case R.id.item2:
                Toast.makeText(this, "Codigo nacional", Toast.LENGTH_SHORT);
                return true;
            case R.id.item3:
                Toast.makeText(this, "Nombre del medicamento", Toast.LENGTH_SHORT);
                createNote();

                return true;
            default:
                return false;
        }
    }
    //menu para setting

    public void openMaps(View view) {
        // Do something in response to button
        switchMaintoMaps();

    }

    private void switchMaintoMaps() {

        startActivity(new Intent(MainActivity.this, MapsActivity.class));

    }

    public void openSettings(View view) {
        // Do something in response to button
        switchMaintoSettings();

    }

    private void switchMaintoSettings() {

        startActivity(new Intent(MainActivity.this, SettingsActivity.class));

    }


    private void createNote() {
        Intent i = new Intent(this, com.example.pharminder_2_0.EditActivity.class);
        startActivityForResult(i, 1);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_settings:
                switchMaintoSettings();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}

