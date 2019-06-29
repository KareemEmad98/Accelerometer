package com.example.last;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
//import android.widget.Toast;
//import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.StringJoiner;
import java.util.Timer;
import java.util.TimerTask;

class Reading {
    long millisec;
    long nanoTime;
    long TimeStamp;
    float x;
    float y;
    float z;
    Reading(){

    }
    Reading( float x, float y, float z) {
        this.millisec = System.currentTimeMillis();
        this.nanoTime = System.nanoTime();
//        this.TimeStamp = Event.timestamp;
        this.x = x;
        this.y = y;
        this.z = z;
    }
    void setStamp(long x){
        this.TimeStamp=x;
    }
}
class newReading extends TimerTask {
    Reading read;
    SensorEvent sensorEvent;
     static ArrayList<Reading> stream;
    newReading(SensorEvent Event){
        this.sensorEvent=Event;
    }
    public void run() {
        read=new Reading();
//        z=new ArrayList<Reading>();
//        read=new Reading(x.values[0],x.values[1],x.values[2]);
//        read.setStamp(x.timestamp);
//        z.add(read);
        read.x=sensorEvent.values[0];
        read.y=sensorEvent.values[1];
        read.z=sensorEvent.values[2];
        read.nanoTime=System.nanoTime();
        read.TimeStamp=sensorEvent.timestamp;
        read.millisec=System.currentTimeMillis();
        stream.add(read);
    }
    ArrayList<Reading> getArray(){
        return stream;
    }
}
public class MainActivity extends AppCompatActivity implements SensorEventListener {
    ArrayList<Reading> list=new ArrayList<Reading>();
    EditText feild;
    private static Socket socket = null;
    //    private static ServerSocket serversocket;
    private static InputStreamReader is;
    private static BufferedReader br;
    private static PrintWriter printwriter;
    private TextView xText, yText, zText;
    private Sensor sense;
    private SensorManager manager;
    float message;
    float messagex;
    float messagey;
    float messagez;
    long timeStamp;
    long milliSec;
    long nanoTime;
    private static String ip = "192.168.1.3";
    private static int port = 8012;
    int i=0;
    static int framesSent=10;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        feild = (EditText) findViewById(R.id.msg);
        manager = (SensorManager) getSystemService(SENSOR_SERVICE);
        sense = manager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
//        manager.registerListener(this, sense, SensorManager.SENSOR_DELAY_NORMAL);
        xText = (TextView) findViewById(R.id.xtext);
        yText = (TextView) findViewById(R.id.ytext);
        zText = (TextView) findViewById(R.id.ztext);
    }
    public void onResume(View v) {
        i=0;
        super.onResume();

        manager.registerListener(this, sense, SensorManager.SENSOR_DELAY_NORMAL);

    }

    public void onPause() {
        super.onPause();
        manager.unregisterListener(this);
//        Toast.makeText(getApplicationContext(), "Data Sent", Toast.LENGTH_LONG).show();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void sendText() {
//        message=feild.getText().toString();
//        message=event.ti();
        Connection con = new Connection();
        con.execute();
//        Toast.makeText(getApplicationContext(), "Data Sent", Toast.LENGTH_LONG).show();
    }

    public void onStartSensing(SensorEvent Event){
        list=new ArrayList<Reading>();
        Timer timer = new Timer();
        TimerTask tsk=new newReading(Event);
        timer.scheduleAtFixedRate(tsk,0,100);
        list=((newReading) tsk).getArray();
    }
    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        i++;
        xText.setText("X"+sensorEvent.values[0] +"\n");
        yText.setText("Y"+sensorEvent.values[1] +"\n");
        zText.setText("Z"+sensorEvent.values[2]+"\n ");
//        message=sensorEvent.values[0];
        messagex=sensorEvent.values[0];
        messagey=sensorEvent.values[1];
        messagez=sensorEvent.values[2];
        nanoTime=System.nanoTime();
        timeStamp=sensorEvent.timestamp;
        milliSec=System.currentTimeMillis();



        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        sendText();
        if(i==framesSent){
            onPause();
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    class Connection extends AsyncTask<Void,Void,Void>{
        @Override
        protected Void doInBackground(Void... voids) {
            try {

                socket=new Socket(ip,port);


                printwriter=new PrintWriter(socket.getOutputStream());
//                printwriter.write("Gesture :");
//                printwriter.write(Long.toString(milliSec)+",");
//                printwriter.write(Long.toString(nanoTime)+",");
//                printwriter.write(Long.toString(timeStamp)+",");

                printwriter.write(Float.toString(messagex)+",");
                printwriter.write(Float.toString(messagey)+",");
                printwriter.write(Float.toString(messagez)+",");



//                printwriter.write("Gesture Sent");
                printwriter.flush();
                printwriter.close();
                socket.close();
//                Toast.makeText(getApplicationContext(), "done", Toast.LENGTH_LONG).show();


            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }



    }}
