package examples.my.myfirstopenglapp;

import android.app.Activity;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.SeekBar;

import java.util.zip.Inflater;

public class MainActivity extends Activity {

    private GLSurfaceView mGLView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mGLView = new MyGLSurfaceView(this);
        LinearLayout rootLayout = (LinearLayout)findViewById(R.id.rootLayout);
        rootLayout.addView(mGLView);

        // Create a GLSurfaceView instance and set it
        // as the ContentView for this Activity.

        SeekBar myBar = (SeekBar)findViewById(R.id.seekBar);
        myBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                Log.i("SeekBar", Integer.toString(progress));

                MyGLSurfaceView obj = new MyGLSurfaceView(getApplicationContext());
                obj.setRenderer(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


    }
}