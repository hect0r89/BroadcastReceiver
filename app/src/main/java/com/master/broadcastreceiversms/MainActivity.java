package com.master.broadcastreceiversms;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity {
    private TextView numLlamada;
    private TextView numSms;
    private TextView mensaje;
    private TextView bootText;
    private TextView btnLocal;
    private TextView txtLocal;
    private TextView crono;
    IntentFilter filterSms = new IntentFilter("android.provider.Telephony.SMS_RECEIVED");
    IntentFilter filterCall = new IntentFilter("android.intent.action.PHONE_STATE");
    IntentFilter filterCrono = new IntentFilter("com.master.CUSTOM_EVENT");
    IntentFilter filterLocal = new IntentFilter("com.master.LOCAL_EVENT");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        numLlamada = (TextView) findViewById(R.id.txtNumLlamada);
        numSms = (TextView) findViewById(R.id.txtNumSms);
        mensaje = (TextView) findViewById(R.id.txtMensaje);
        bootText = (TextView) findViewById(R.id.txt_boot);
        btnLocal = (Button) findViewById(R.id.btnLocal);
        txtLocal = (TextView) findViewById(R.id.txtLocal);
        crono = (TextView) findViewById(R.id.txtCrono);

        registerReceiver(receiverSms, filterSms);
        registerReceiver(receiverCall, filterCall);
        registerReceiver(receiverCrono, filterCrono);

        Intent intent = getIntent();
        String titleBoot = intent.getStringExtra("boot");
        if (titleBoot != null) {
            bootText.setText(titleBoot);
        }

        btnLocal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent("com.master.LOCAL_EVENT");
                intent.putExtra("mensaje", "Mensaje recibido a través de un LocalBroadcastManager");
                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiverSms);
        unregisterReceiver(receiverCall);
        unregisterReceiver(receiverCrono);
    }

    private BroadcastReceiver receiverSms = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle b = intent.getExtras();

            if (b != null) {
                Object[] pdus = (Object[]) b.get("pdus");

                SmsMessage[] mensajes = new SmsMessage[pdus.length];

                for (int i = 0; i < mensajes.length; i++) {
                    mensajes[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);

                    String idMensaje = mensajes[i].getOriginatingAddress();
                    String textoMensaje = mensajes[i].getMessageBody();
                    numSms.setText(idMensaje);
                    mensaje.setText(textoMensaje);
                }

            }
        }
    };

    private BroadcastReceiver receiverCall = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle extras = intent.getExtras();
            if (extras != null) {
                String state = extras.getString(TelephonyManager.EXTRA_STATE);
                if (state.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
                    String phoneNumber = extras
                            .getString(TelephonyManager.EXTRA_INCOMING_NUMBER);
                    numLlamada.setText(phoneNumber);
                }
            }
        }

    };

    private BroadcastReceiver receiverCrono = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            crono.setText("Sí");
        }

    };

    private BroadcastReceiver receiverLocal = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            String mensaje = intent.getStringExtra("mensaje");
            txtLocal.setText(mensaje);


        }

    };

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(receiverLocal, filterLocal);

    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiverLocal);
        super.onPause();
    }
}
