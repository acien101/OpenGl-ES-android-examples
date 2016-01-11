package examples.my.myfirstopenglapp;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.Log;
import android.widget.SeekBar;

/**
 * Created by amil101 on 10/01/16.
 */
class MyGLSurfaceView extends GLSurfaceView {

    private final MyGLRenderer mRenderer;

    public MyGLSurfaceView(Context context){
        super(context);

        // Create an OpenGL ES 2.0 context
        setEGLContextClientVersion(2);

        mRenderer = new MyGLRenderer();

        // Set the Renderer for drawing on the GLSurfaceView
        setRenderer(mRenderer);
        // Render the view only when there is a change in the drawing data
        setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);

    }

    public void setRenderer(int progress){

        mRenderer.setZoom(progress);
        requestRender();
    }


}