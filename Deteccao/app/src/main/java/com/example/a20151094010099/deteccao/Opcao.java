package com.example.a20151094010099.deteccao;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import org.eclipse.paho.client.mqttv3.MqttException;

import AppBD.BD;
import MQTT.MqTTOptions;
import Placas.Placa;

public class Opcao extends AppCompatActivity {

    MqTTOptions mqtt = new MqTTOptions();
    final int RequestCameraPermissionsID = 1001;

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case RequestCameraPermissionsID:
                for (int i = 0; i < permissions.length; i++) {
                    if (permissions[i].equalsIgnoreCase(Manifest.permission.ACCESS_FINE_LOCATION)
                            && grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    }
                }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_opcao);
        try {
            mqtt.conect(getApplicationContext());
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public void detectorDePlacas(View view) {
        Intent intent = new Intent(getApplicationContext(), DeteccaoDePlacas.class);
        startActivity(intent);
    }

    public void detectorDePessoas(View view) {
        Intent intent = new Intent(getApplicationContext(), DeteccaoDePessoas.class);
        startActivity(intent);
    }
}
