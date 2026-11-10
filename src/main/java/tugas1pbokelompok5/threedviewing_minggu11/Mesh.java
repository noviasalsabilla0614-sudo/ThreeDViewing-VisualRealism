package tugas1pbokelompok5.threedviewing_minggu11;

import org.lwjgl.system.MemoryUtil;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

public class Mesh {
    private final int vaoID;
    private final int vboID;
    private final int eboID;
    private final int vertexCount;

    public Mesh(float[] vertices, int[] indices) {
        vertexCount = indices.length;
        vaoID = glGenVertexArrays();
        glBindVertexArray(vaoID);

        vboID = glGenBuffers();
        FloatBuffer vBuffer = MemoryUtil.memAllocFloat(vertices.length);
        vBuffer.put(vertices).flip();
        glBindBuffer(GL_ARRAY_BUFFER, vboID);
        glBufferData(GL_ARRAY_BUFFER, vBuffer, GL_STATIC_DRAW);
        MemoryUtil.memFree(vBuffer);

        eboID = glGenBuffers();
        IntBuffer iBuffer = MemoryUtil.memAllocInt(indices.length);
        iBuffer.put(indices).flip();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, eboID);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, iBuffer, GL_STATIC_DRAW);
        MemoryUtil.memFree(iBuffer);

        int stride = 8 * Float.BYTES;
        glVertexAttribPointer(0, 3, GL_FLOAT, false, stride, 0);                  // Position
        glEnableVertexAttribArray(0);
        glVertexAttribPointer(1, 3, GL_FLOAT, false, stride, 3 * Float.BYTES);    // Normal
        glEnableVertexAttribArray(1);
        glVertexAttribPointer(2, 2, GL_FLOAT, false, stride, 6 * Float.BYTES);    // UV
        glEnableVertexAttribArray(2);

        glBindVertexArray(0);
    }

    public void render() {
        glBindVertexArray(vaoID);
        glDrawElements(GL_TRIANGLES, vertexCount, GL_UNSIGNED_INT, 0);
        glBindVertexArray(0);
    }

    // Generator Bola 3D
    public static Mesh createSphere(float radius, int rings, int sectors) {
        List<Float> vList = new ArrayList<>();
        List<Integer> iList = new ArrayList<>();

        float R = 1.0f / (float)(rings - 1);
        float S = 1.0f / (float)(sectors - 1);

        for (int r = 0; r < rings; r++) {
            for (int s = 0; s < sectors; s++) {
                float y = (float) Math.sin(-Math.PI / 2 + Math.PI * r * R);
                float x = (float) (Math.cos(2 * Math.PI * s * S) * Math.sin(Math.PI * r * R));
                float z = (float) (Math.sin(2 * Math.PI * s * S) * Math.sin(Math.PI * r * R));

                vList.add(x * radius); vList.add(y * radius); vList.add(z * radius);
                vList.add(x); vList.add(y); vList.add(z);
                vList.add(s * S); vList.add(r * R);
            }
        }

        for (int r = 0; r < rings - 1; r++) {
            for (int s = 0; s < sectors - 1; s++) {
                iList.add(r * sectors + s);
                iList.add(r * sectors + (s + 1));
                iList.add((r + 1) * sectors + (s + 1));

                iList.add(r * sectors + s);
                iList.add((r + 1) * sectors + (s + 1));
                iList.add((r + 1) * sectors + s);
            }
        }

        float[] vArr = new float[vList.size()];
        for (int i = 0; i < vList.size(); i++) vArr[i] = vList.get(i);

        int[] iArr = new int[iList.size()];
        for (int i = 0; i < iList.size(); i++) iArr[i] = iList.get(i);

        return new Mesh(vArr, iArr);
    }

    // Generator Bidang Lantai (Plane Ground)
    public static Mesh createPlane(float size) {
        float h = size / 2.0f;
        float[] vertices = {
             h, 0.0f,  h,   0.0f, 1.0f, 0.0f,   1.0f, 1.0f,
            -h, 0.0f,  h,   0.0f, 1.0f, 0.0f,   0.0f, 1.0f,
            -h, 0.0f, -h,   0.0f, 1.0f, 0.0f,   0.0f, 0.0f,
             h, 0.0f, -h,   0.0f, 1.0f, 0.0f,   1.0f, 0.0f
        };
        int[] indices = { 0, 1, 2, 0, 2, 3 };
        return new Mesh(vertices, indices);
    }
}