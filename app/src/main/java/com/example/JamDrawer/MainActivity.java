package com.example.JamDrawer;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import java.util.Set;

public class MainActivity extends AppCompatActivity {
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothDevice mDevice;
    private final static String TAG = "MainActivity";
    private FloatingActionButton fab;
    private DrawingView drawingV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawingV = findViewById(R.id.view2);
        fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("NewApi")
            @Override
            public void onClick(View view) {
                initBluetooth();
                findRaspberry();
                onSend();
                Snackbar.make(view, "Sending Image", Snackbar.LENGTH_SHORT)
                        .setAction("Action", null).show();
            }
        });

        final ImageView iv1 = findViewById(R.id.img1);
        final ImageView iv3 = findViewById(R.id.img3);
        final ImageView iv4 = findViewById(R.id.img4);
        ImageView[] ivls = {iv1, iv3, iv4};
        for (int i = 0; i < ivls.length; i++) {
            final ImageView iv = ivls[i];
            iv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    drawingV.setBackgroundView(iv);
                }
            });
        }

        FloatingActionButton fab2 = findViewById(R.id.fab2);
        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawingV.clearView();
            }
        });
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    public void onSend() {
        String message = getSVG();
        new MessageThread(mDevice, message).start();

    }

    private void findRaspberry() {
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter
                .getBondedDevices();
        for (BluetoothDevice device : pairedDevices) {
            if (device.getName().equals("Jam Plotter"))
                this.mDevice = device;
        }
    }

    private void initBluetooth() {
        Log.d(TAG, "Checking Bluetooth...");
        if (mBluetoothAdapter == null) {
            Log.d(TAG, "Device does not support Bluetooth");
        } else {
            Log.d(TAG, "Bluetooth supported");
        }
        if (!mBluetoothAdapter.isEnabled()) {
            Log.d(TAG, "Bluetooth not enabled");
        } else {
            Log.d(TAG, "Bluetooth enabled");
        }
    }

    private String getSVG() {
        Bitmap bitmap = drawingV.getExportedBitmap();
        try {

            String output = ImageTracerAndroid.imageToSVG(bitmap, null, ImageTracerAndroid.generatepalette(2));
            return output;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "-1";
    }
}
