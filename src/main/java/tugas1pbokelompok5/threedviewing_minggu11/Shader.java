package tugas1pbokelompok5.threedviewing_minggu11;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.system.MemoryStack;
import java.io.InputStream;
import java.nio.FloatBuffer;
import java.nio.charset.StandardCharsets;
import static org.lwjgl.opengl.GL20.*;

public class Shader {
    private final int programID;

    public Shader(String vertPath, String fragPath) {
        int vertID = loadShader(vertPath, GL_VERTEX_SHADER);
        int fragID = loadShader(fragPath, GL_FRAGMENT_SHADER);

        programID = glCreateProgram();
        glAttachShader(programID, vertID);
        glAttachShader(programID, fragID);
        glLinkProgram(programID);

        if (glGetProgrami(programID, GL_LINK_STATUS) == GL_FALSE) {
            throw new RuntimeException("Gagal link Shader: " + glGetProgramInfoLog(programID));
        }

        glDeleteShader(vertID);
        glDeleteShader(fragID);
    }

    private int loadShader(String path, int type) {
        try (InputStream in = getClass().getClassLoader().getResourceAsStream(path)) {
            if (in == null) throw new RuntimeException("File shader tidak ditemukan: " + path);
            String source = new String(in.readAllBytes(), StandardCharsets.UTF_8);
            int shaderID = glCreateShader(type);
            glShaderSource(shaderID, source);
            glCompileShader(shaderID);

            if (glGetShaderi(shaderID, GL_COMPILE_STATUS) == GL_FALSE) {
                throw new RuntimeException("Gagal kompilasi shader (" + path + "): " + glGetShaderInfoLog(shaderID));
            }
            return shaderID;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void use() { glUseProgram(programID); }
    public void setBool(String name, boolean value) { glUniform1i(glGetUniformLocation(programID, name), value ? 1 : 0); }
    public void setInt(String name, int value) { glUniform1i(glGetUniformLocation(programID, name), value); }
    public void setVec3(String name, Vector3f value) { glUniform3f(glGetUniformLocation(programID, name), value.x, value.y, value.z); }
    
    public void setMat4(String name, Matrix4f value) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            FloatBuffer buffer = stack.mallocFloat(16);
            value.get(buffer);
            glUniformMatrix4fv(glGetUniformLocation(programID, name), false, buffer);
        }
    }
}