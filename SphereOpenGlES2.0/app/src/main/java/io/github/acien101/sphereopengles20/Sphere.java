package io.github.acien101.sphereopengles20;

import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * Created by amil101 on 6/02/16.
 */
public class Sphere {

    private final String vertexShaderCode =
            "uniform mat4 uMVPMatrix;" +
                    "attribute vec4 vPosition;" +
                    "void main() {" +
                    // The matrix must be included as a modifier of gl_Position.
                    // Note that the uMVPMatrix factor *must be first* in order
                    // for the matrix multiplication product to be correct.
                    "  gl_Position = uMVPMatrix * vPosition;" +
                    "}";

    private final String fragmentShaderCode =
            "precision mediump float;" +
                    "uniform vec4 vColor;" +
                    "void main() {" +
                    "  gl_FragColor = vColor;" +
                    "}";

    private FloatBuffer vertexBuffer;

    // number of coordinates per vertex in this array
    static final int COORDS_PER_VERTEX = 3;

    // Set color with red, green, blue and alpha (opacity) values
    float color[] = { 0.63671875f, 0.76953125f, 0.22265625f, 1.0f };

    private final int mProgram;

    public Sphere(int stacks, int slices, float radius, float squash) {

        this.m_Stacks = stacks;
        this.m_Slices = slices;
        this.m_Radius = radius;
        this.m_Squash = squash;

        init(m_Stacks, m_Slices, radius, squash, "dummy");

        int vertexShader = MyGLRenderer.loadShader(GLES20.GL_VERTEX_SHADER,
                vertexShaderCode);
        int fragmentShader = MyGLRenderer.loadShader(GLES20.GL_FRAGMENT_SHADER,
                fragmentShaderCode);

        // create empty OpenGL ES Program
        mProgram = GLES20.glCreateProgram();

        // add the vertex shader to program
        GLES20.glAttachShader(mProgram, vertexShader);

        // add the fragment shader to program
        GLES20.glAttachShader(mProgram, fragmentShader);

        // creates OpenGL ES program executables
        GLES20.glLinkProgram(mProgram);
    }

    public static int loadShader(int type, String shaderCode){

        // create a vertex shader type (GLES20.GL_VERTEX_SHADER)
        // or a fragment shader type (GLES20.GL_FRAGMENT_SHADER)
        int shader = GLES20.glCreateShader(type);

        // add the source code to the shader and compile it
        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);

        return shader;
    }

    private int mPositionHandle;
    private int mColorHandle;
    private int mMVPMatrixHandle;

    private final int vertexStride = COORDS_PER_VERTEX * 4; // 4 bytes per vertex

    public void draw(float[] mvpMatrix) {

        // Add program to OpenGL ES environment
        GLES20.glUseProgram(mProgram);

        // get handle to vertex shader's vPosition member
        mPositionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");

        // Enable a handle to the triangle vertices
        GLES20.glEnableVertexAttribArray(mPositionHandle);

        // Prepare the triangle coordinate data
        GLES20.glVertexAttribPointer(mPositionHandle, COORDS_PER_VERTEX,
                GLES20.GL_FLOAT, false,
                vertexStride, vertexBuffer);

        // get handle to fragment shader's vColor member
        mColorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");

        // Set color for drawing the triangle
        GLES20.glUniform4fv(mColorHandle, 1, color, 0);

        // get handle to shape's transformation matrix
        mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");

        // Apply the projection and view transformation
        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mvpMatrix, 0);

        // Draw the triangle
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, (m_Slices+1)*2*(m_Stacks-1)+2);

        // Disable vertex array
        GLES20.glDisableVertexAttribArray(mPositionHandle);
    }


    float m_Scale;
    float m_Squash;
    float m_Radius;
    int m_Stacks, m_Slices;

    private void init(int stacks,int slices, float radius, float squash, String textureFile) {
        float[] vertexData;
        float[] colorData;
        float colorIncrement=0f;

        float blue=0f;
        float red=1.0f;
        int vIndex=0;				//vertex index
        int cIndex=0;				//color index

        m_Scale=radius;
        m_Squash=squash;

        colorIncrement=1.0f/(float)stacks;

        {
            m_Stacks = stacks;
            m_Slices = slices;

            //vertices

            vertexData = new float[ 3*((m_Slices*2+2) * m_Stacks)];

            //color data

            colorData = new float[ (4*(m_Slices*2+2) * m_Stacks)];

            int phiIdx, thetaIdx;

            //latitude

            for(phiIdx=0; phiIdx < m_Stacks; phiIdx++)
            {
                //starts at -90 degrees (-1.57 radians) goes up to +90 degrees (or +1.57 radians)

                //the first circle

                float phi0 = (float) Math.PI * ((float)(phiIdx+0) * (1.0f/(float)(m_Stacks)) - 0.5f);

                //the next, or second one.

                float phi1 = (float) Math.PI * ((float)(phiIdx+1) * (1.0f/(float)(m_Stacks)) - 0.5f);

                float cosPhi0 = (float) Math.cos(phi0);
                float sinPhi0 = (float) Math.sin(phi0);
                float cosPhi1 = (float) Math.cos(phi1);
                float sinPhi1 = (float) Math.sin(phi1);

                float cosTheta, sinTheta;

                //longitude

                for(thetaIdx=0; thetaIdx < m_Slices; thetaIdx++)
                {
                    //increment along the longitude circle each "slice"

                    float theta = (float) (-2.0f*(float) Math.PI * ((float)thetaIdx) * (1.0/(float)(m_Slices-1)));
                    cosTheta = (float) Math.cos(theta);
                    sinTheta = (float) Math.sin(theta);

                    //we're generating a vertical pair of points, such
                    //as the first point of stack 0 and the first point of stack 1
                    //above it. This is how TRIANGLE_STRIPS work,
                    //taking a set of 4 vertices and essentially drawing two triangles
                    //at a time. The first is v0-v1-v2 and the next is v2-v1-v3. Etc.

                    //get x-y-z for the first vertex of stack

                    vertexData[vIndex+0] = m_Scale*cosPhi0*cosTheta;
                    vertexData[vIndex+1] = m_Scale*(sinPhi0*m_Squash);
                    vertexData[vIndex+2] = m_Scale*(cosPhi0*sinTheta);

                    vertexData[vIndex+3] = m_Scale*cosPhi1*cosTheta;
                    vertexData[vIndex+4] = m_Scale*(sinPhi1*m_Squash);
                    vertexData[vIndex+5] = m_Scale*(cosPhi1*sinTheta);

                    colorData[cIndex+0] = (float)red;
                    colorData[cIndex+1] = (float)0f;
                    colorData[cIndex+2] = (float)blue;
                    colorData[cIndex+4] = (float)red;
                    colorData[cIndex+5] = (float)0f;
                    colorData[cIndex+6] = (float)blue;
                    colorData[cIndex+3] = (float)1.0;
                    colorData[cIndex+7] = (float)1.0;

                    cIndex+=2*4;
                    vIndex+=2*3;
                }

                blue+=colorIncrement;
                red-=colorIncrement;

                // create a degenerate triangle to connect stacks and maintain winding order

                vertexData[vIndex+0] = vertexData[vIndex+3] = vertexData[vIndex-3];
                vertexData[vIndex+1] = vertexData[vIndex+4] = vertexData[vIndex-2];
                vertexData[vIndex+2] = vertexData[vIndex+5] = vertexData[vIndex-1];
            }

        }

        makeFloatBuffer(vertexData);
    }

    public void  makeFloatBuffer(float[] arr)
    {
        ByteBuffer bb = ByteBuffer.allocateDirect(arr.length * 4);
        bb.order(ByteOrder.nativeOrder());
        vertexBuffer = bb.asFloatBuffer();
        vertexBuffer.put(arr);
        vertexBuffer.position(0);

    }
}