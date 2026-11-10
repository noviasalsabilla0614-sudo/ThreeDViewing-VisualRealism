package tugas1pbokelompok5.threedviewing_minggu11;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;

public class ThreeDViewing_Minggu11 {
    private static boolean useBlinnPhong = true;     // Key 'B'
    private static boolean useFlatShading = false;   // Key 'F'
    private static boolean enableDepthTest = true;   // Key 'Z'
    private static boolean enableCulling = true;     // Key 'C'

    public static void main(String[] args) {
        if (!glfwInit()) throw new IllegalStateException("Gagal inisialisasi GLFW");

        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);

        long window = glfwCreateWindow(800, 600, "3D Visual Realism Suite - Minggu 11", 0, 0);
        if (window == 0) throw new RuntimeException("Gagal membuat window GLFW");

        glfwMakeContextCurrent(window);
        org.lwjgl.opengl.GL.createCapabilities();

        Shader mainShader = new Shader("shaders/phong.vert", "shaders/phong.frag");
        Shader shadowShader = new Shader("shaders/shadowDepth.vert", "shaders/shadowDepth.frag");
        ShadowMapFBO shadowFBO = new ShadowMapFBO();

        Mesh sphereMesh = Mesh.createSphere(1.0f, 32, 32);
        Mesh groundMesh = Mesh.createPlane(10.0f);

        glfwSetKeyCallback(window, (w, key, scancode, action, mods) -> {
            if (action == GLFW_PRESS) {
                if (key == GLFW_KEY_B) { useBlinnPhong = !useBlinnPhong; System.out.println("Blinn-Phong Mode: " + useBlinnPhong); }
                if (key == GLFW_KEY_F) { useFlatShading = !useFlatShading; System.out.println("Flat Shading Mode: " + useFlatShading); }
                if (key == GLFW_KEY_Z) { enableDepthTest = !enableDepthTest; System.out.println("Depth Test (Z-Buffer): " + enableDepthTest); }
                if (key == GLFW_KEY_C) { enableCulling = !enableCulling; System.out.println("Back-Face Culling: " + enableCulling); }
            }
        });

        while (!glfwWindowShouldClose(window)) {
            Vector3f lightPos = new Vector3f(2.0f, 5.0f, 2.0f);
            Matrix4f lightProjection = new Matrix4f().ortho(-10f, 10f, -10f, 10f, 1.0f, 15.0f);
            Matrix4f lightView = new Matrix4f().lookAt(lightPos, new Vector3f(0, 0, 0), new Vector3f(0, 1, 0));
            Matrix4f lightSpaceMatrix = new Matrix4f(lightProjection).mul(lightView);

            // PASS 1: Render Depth Map ke Shadow FBO
            glEnable(GL_DEPTH_TEST);
            shadowFBO.bindForWriting();
            shadowShader.use();
            shadowShader.setMat4("lightSpaceMatrix", lightSpaceMatrix);
            Matrix4f modelSphere = new Matrix4f().translate(0.0f, 1.5f, 0.0f);
            shadowShader.setMat4("model", modelSphere);
            sphereMesh.render();
            shadowFBO.unbind(800, 600);

            // PASS 2: Render Utama Ke Layar
            glViewport(0, 0, 800, 600);
            glClearColor(0.15f, 0.15f, 0.15f, 1.0f);
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

            if (enableCulling) {
                glEnable(GL_CULL_FACE);
            } else {
                glDisable(GL_CULL_FACE);
            }

            mainShader.use();
            Matrix4f projection = new Matrix4f().perspective((float) Math.toRadians(45.0), 800f/600f, 0.1f, 100.0f);
            Matrix4f view = new Matrix4f().lookAt(new Vector3f(0.0f, 3.5f, 6.0f), new Vector3f(0.0f, 0.5f, 0.0f), new Vector3f(0.0f, 1.0f, 0.0f));

            mainShader.setMat4("projection", projection);
            mainShader.setMat4("view", view);
            mainShader.setMat4("lightSpaceMatrix", lightSpaceMatrix);
            mainShader.setVec3("lightPos", lightPos);
            mainShader.setVec3("viewPos", new Vector3f(0.0f, 3.5f, 6.0f));
            mainShader.setVec3("lightColor", new Vector3f(1.0f, 1.0f, 1.0f));

            mainShader.setBool("useBlinnPhong", useBlinnPhong);
            mainShader.setBool("useFlatShading", useFlatShading);
            mainShader.setBool("useTexture", false);

            shadowFBO.bindForReading(1);
            mainShader.setInt("shadowMap", 1);

            // 1. Render Ground Plane (Lantai)
            glEnable(GL_DEPTH_TEST);
            mainShader.setMat4("model", new Matrix4f());
            mainShader.setVec3("objectColor", new Vector3f(0.7f, 0.7f, 0.7f));
            groundMesh.render();

            // 2. Render Bola 3D Merah (Tergantung Tombol Z)
            if (enableDepthTest) {
                glEnable(GL_DEPTH_TEST);
            } else {
                glDisable(GL_DEPTH_TEST);
            }
            mainShader.setMat4("model", modelSphere);
            mainShader.setVec3("objectColor", new Vector3f(0.8f, 0.1f, 0.1f));
            sphereMesh.render();

            glfwSwapBuffers(window);
            glfwPollEvents();
        }

        glfwDestroyWindow(window);
        glfwTerminate();
    }
}