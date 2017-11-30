package mx.happylittlebox.repartidor.bluetoothcontroller;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.jmedeisis.bugstick.Joystick;
import com.jmedeisis.bugstick.JoystickListener;

import java.io.Console;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {
    Button buttonFire;
    Joystick joystick;
    boolean fireState = false;
    ProgressDialog progress;
    BluetoothAdapter myBluetooth = null;
    BluetoothSocket btSocket = null;
    boolean isBtConnected = false;
    static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    String address = "00:21:13:00:D0:FC";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        joystick = (Joystick) findViewById(R.id.joystick);
        buttonFire = (Button) findViewById(R.id.button_fire);

        checkBTState();


        joystick.setJoystickListener(new JoystickListener() {
            @Override
            public void onDown() {

            }

            @Override
            public void onDrag(float degrees, float offset) {
                double grados = degrees;

                if (grados < 45 && grados > -45) {
                    try {
                        btSocket.getOutputStream().write(3);
                    } catch (IOException e) {

                    }
                    Log.d("Derecha", Double.toString(grados));
                } else if (grados > 45 && grados < 135) {
                    try {
                        btSocket.getOutputStream().write(12);
                    } catch (IOException e) {

                    }
                    Log.d("Arriba", Double.toString(grados));
                } else if (grados < -45 && grados > -135) {
                    try {
                        btSocket.getOutputStream().write(6);
                    } catch (IOException e) {

                    }
                    Log.d("Abajo", Double.toString(grados));
                } else {
                    try {
                        btSocket.getOutputStream().write(9);
                    } catch (IOException e) {

                    }
                    Log.d("Izquierda", Double.toString(grados));
                }
            }

            @Override
            public void onUp() {
                try {
                    btSocket.getOutputStream().write(1);
                } catch (IOException e) {

                }
                Log.d("Unpressed", "unpresed");
            }

        });

        buttonFire.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!fireState) {
                    buttonFire.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.colorBlack, null));
                    fireState = true;
                    try {
                        btSocket.getOutputStream().write(2);
                        btSocket.getOutputStream().write(1);

                    } catch (IOException e) {

                    }
                    Log.d("Fire", "fire");
                } else {
                    buttonFire.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.colorPrimary, null));
                    fireState = false;
                    try {
                        btSocket.getOutputStream().write(2);
                        btSocket.getOutputStream().write(1);
                    } catch (IOException e) {

                    }
                    Log.d("Unfire", "Unfire");
                }

            }
        });
    }

    private class ConnectBT extends AsyncTask<Void, Void, Void>  // UI thread
    {
        private boolean ConnectSuccess = true;

        @Override
        protected void onPreExecute() {
            progress = ProgressDialog.show(MainActivity.this, "Conectando con KIIKO...", "Espere un momento");
        }

        @Override
        protected Void doInBackground(Void... devices) {
            try {
                if (btSocket == null || !isBtConnected) {
                    myBluetooth = BluetoothAdapter.getDefaultAdapter();
                    BluetoothDevice dispositivo = myBluetooth.getRemoteDevice(address);//conectamos al dispositivo y chequeamos si esta disponible
                    btSocket = dispositivo.createInsecureRfcommSocketToServiceRecord(myUUID);
                    BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
                    btSocket.connect();
                }
            } catch (IOException e) {
                ConnectSuccess = false;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            if (!ConnectSuccess) {
                msg("Conexión Fallida");
                finish();
            } else {
                msg("Conectado a KIIKO");
                isBtConnected = true;
            }
            progress.dismiss();
        }
    }

    private void msg(String s) {
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
    }

    private void checkBTState() {
        myBluetooth=BluetoothAdapter.getDefaultAdapter();
        if(myBluetooth==null) {
            Toast.makeText(getBaseContext(), "Device does not support Bluetooth", Toast.LENGTH_SHORT).show();
        } else {
            if (myBluetooth.isEnabled()) {
                ConnectBT connection = new ConnectBT();
                connection.execute();
                Log.d("Bluetooth", "Blueetooth on");
            } else {
                //Prompt user to turn on Bluetooth
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, 1);

            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        System.out.println(resultCode);
        if (resultCode == 0) {
            Toast.makeText(this, "Necesitas Bluetooth para usar esta aplicación", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Log.d("Bluetooth", "Aceptado");
            ConnectBT connection = new ConnectBT();
            connection.execute();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        try {
            if (btSocket != null) {
                btSocket.close();
                Toast.makeText(this, "KIIKO Desconectado", Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        ConnectBT connection = new ConnectBT();
        connection.execute();

    }

}
