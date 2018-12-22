package e.o.mcalcpro;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;

import ca.roumani.i2c.MPro;

public class MCalcPro_Activity extends AppCompatActivity implements TextToSpeech.OnInitListener , SensorEventListener {

    MPro mp;
    private TextToSpeech tts;
    private SensorManager mSensorManager;
    private Sensor mSensor;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mcalcpro_layout);
        // create a single instance
        mp = new MPro();
        // text to speach
        this.tts = new TextToSpeech(this,this);
        // motion sensor register listener for onSensorChanged()
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_FASTEST);
    }

    public void onInit(int initStatus){
        this.tts.setLanguage(Locale.US);
    }

    // 2 methods below is for the sensor: onAccuracyChanged and onSensorChanged
    // onAccuracyChanged is left blank bc we dont need to do anything if accuracy is changed
    public void onAccuracyChanged(Sensor arg0, int arg1){
    }

    //method will be invoked when device is shook
    public void onSensorChanged(SensorEvent event){
        //when device is moved causes sensor to invoke onSensorChanged method
        double ax = event.values[0];
        double ay = event.values[1];
        double az = event.values[2];
        double a = Math.sqrt(Math.pow(ax,2)+Math.pow(ay,2)+Math.pow(az,2));
        if(a>40){
            ((EditText)findViewById(R.id.pBox)).setText("");
            ((EditText)findViewById(R.id.aBox)).setText("");
            ((EditText)findViewById(R.id.iBox)).setText("");
            ((TextView)findViewById(R.id.output)).setText("");
        }
    }



    public void buttonClicked(View v) {
        try{
            mp.setPrinciple(((EditText)findViewById(R.id.pBox)).getText().toString());
            mp.setAmortization(((EditText)findViewById(R.id.aBox)).getText().toString());
            mp.setInterest(((EditText)findViewById(R.id.iBox)).getText().toString());
            System.out.println(mp.computePayment("%,.2f"));
            System.out.println(mp.outstandingAfter(2,"%,16.0f"));

            String s = "Monthly Payment : " + mp.computePayment("%,.2f");
            String n;
            n = s;
            n += "\n\n";
            n += " By making this payment monthly for 20 years, the mortgage will be paid in full." +
                    " If you terminate it on the nth anniversary";

            // does calc for 0,1,2,3,4,5
            for (int i = 0 ; i <=5 ; i++){
                n += "\n\n";
                n += String.format("%8d",i)+ mp.outstandingAfter(i,"%,16.0f");
            }
            // does calc for 10,15,20
            for (int j = 10 ; j <=20 ; j+=5){
                n += "\n\n";
                n += String.format("%8d",j)+ mp.outstandingAfter(j,"%,16.0f");
            }
            n+= "\n";

            // outputs the string value in the textview widget located in the scroll function
            ((TextView) findViewById(R.id.output)).setText(n);

            // text to speach output
            tts.speak(s,TextToSpeech.QUEUE_FLUSH,null);
        }

        // throws a toast saying exception
        catch (Exception e){
            Toast label = Toast.makeText(this,e.getMessage(), Toast.LENGTH_SHORT);
            label.show();
        }



    }


}
