package com.example.hojat.androidsynthapp;

import android.app.Activity;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SeekBar;


public class MainActivity extends Activity {
    Thread t;
    int x;//Just testing GitHub
    int sr = 44100;
    boolean isRunning = true;
    SeekBar fSlider;
    double sliderval;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fSlider = (SeekBar) findViewById(R.id.frequency);

        // create a listener for the slider bar;
        SeekBar.OnSeekBarChangeListener listener = new SeekBar.OnSeekBarChangeListener() {
            public void onStopTrackingTouch(SeekBar seekBar) { }
            public void onStartTrackingTouch(SeekBar seekBar) { }
            public void onProgressChanged(SeekBar seekBar,
                                          int progress,
                                          boolean fromUser) {
                if(fromUser) sliderval = progress / (double)seekBar.getMax();
            }
        };

        // set the listener on the slider
        fSlider.setOnSeekBarChangeListener(listener);

        // start a new thread to synthesise audio
        t = new Thread() {
            public void run() {
                // set process priority
                setPriority(Thread.MAX_PRIORITY);
                // set the buffer size
                int buffsize = AudioTrack.getMinBufferSize(sr,
                        AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT);
                // create an audiotrack object
                AudioTrack audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC,
                        sr, AudioFormat.CHANNEL_OUT_MONO,
                        AudioFormat.ENCODING_PCM_16BIT, buffsize,
                        AudioTrack.MODE_STREAM);

                short samples[] = new short[buffsize];
                int amp = 10000;
                double twopi = 8.*Math.atan(1.);
                double fr = 440.f;
                double ph = 0.0;

                // start audio
                audioTrack.play();

                // synthesis loop
                while(isRunning){
                    fr =  440 + 440*sliderval;
                    for(int i=0; i < buffsize; i++){
                        samples[i] = (short) (amp*Math.sin(ph));
                        ph += twopi*fr/sr;
                    }
                    audioTrack.write(samples, 0, buffsize);
                }
                audioTrack.stop();
                audioTrack.release();
            }
        };
        t.start();
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
}
