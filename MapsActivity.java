package com.example.trazasrutas_2;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mi_mapa;
    private Button btn_seguir, btn_reset;
    private PolylineOptions ruta=new PolylineOptions();
    private boolean mapa_centrado=false;
    LocationListener oyente;
    LocationManager lm;
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 99) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                btn_seguir.setEnabled(true);
                 }
        }
    }

    public void pedirActualizaciones() {
        lm = (LocationManager) getSystemService(LOCATION_SERVICE);
        oyente = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                meterNuevoPuntoEnRuta(location);
            }
        };
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 100, 0, oyente);

    }

    private void meterNuevoPuntoEnRuta(Location location) {
        LatLng punto=new LatLng(location.getLatitude(), location.getLongitude());
        ruta.add(punto);
        mi_mapa.addPolyline(ruta);
        if (mapa_centrado==false)
        {
            mi_mapa.moveCamera(CameraUpdateFactory.newLatLngZoom(punto, 8));
            mapa_centrado=true;
        }
        //Centro el mapa en el punto que me ha llegado
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        btn_seguir=findViewById(R.id.btn_seguir);
        btn_reset=findViewById(R.id.btn_reset);
        btn_reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetearRuta();
            }
        });
        btn_seguir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pedirActualizaciones();
            }
        });
        chekearPermiso();
    }

    private void resetearRuta() {
        lm.removeUpdates(oyente);
        ruta=new PolylineOptions();
        mi_mapa.clear();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void chekearPermiso() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {//Si no tengo permiso, lo pido
       //Si no tengo permiso lo pido
            String[] permisos={Manifest.permission.ACCESS_FINE_LOCATION};
            requestPermissions(permisos, 99);
        }
        else {
            //Si ya tengo permiso me pongo a geolocalizar (bueno, después de clicar).
            //Habilito botón
            btn_seguir.setEnabled(true);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mi_mapa = googleMap;

    }
}