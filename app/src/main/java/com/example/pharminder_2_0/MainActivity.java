package com.example.pharminder_2_0;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.Toast;
import android.widget.PopupMenu;

public class MainActivity extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.app_bar_main);
    }

    public void showPopup(View view){
        PopupMenu popup=new PopupMenu(this, view);
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
                switchMaintoNotepad();

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


    //o una o la otra tenemo que utilizar
    private void switchMaintoNotepad() {

        startActivity(new Intent(MainActivity.this, NotepadActivity.class));

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
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
    }}

