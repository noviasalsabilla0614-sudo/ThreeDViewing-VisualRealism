# 3D Visual Realism Suite - Minggu 11

Proyek praktikum Grafika Komputer 3D yang mengimplementasikan 5 indikator utama *Visual Realism* menggunakan Java, LWJGL 3, OpenGL 3.3 Core Profile, dan JOML.

## 🌟 Fitur Interaktif Keyboard

| Key | Fitur / Indikator | Deskripsi Pengujian |
| :---: | :--- | :--- |
| **`B`** | **Shading Models** | Beralih antara model specular **Phong** dan **Blinn-Phong**. |
| **`F`** | **Flat vs Smooth Shading** | Beralih antara **Flat Shading** dan **Smooth Shading**. |
| **`Z`** | **Hidden Surface Removal** | Menghidupkan / mematikan **Z-Buffer Depth Test** (`GL_DEPTH_TEST`). |
| **`C`** | **Back-Face Culling** | Menghidupkan / mematikan **Back-Face Culling** (`GL_CULL_FACE`). |
| **`T`** | **Texture Filtering** | Pemetaan tekstur koordinat UV dengan *Bilinear Mipmapping*. |

## 🛠️ Real-Time Shadow Mapping
- **2-Pass Depth FBO**: Pass 1 render kedalaman dari sudut pandang cahaya, Pass 2 render bayangan real-time dengan *Dynamic Bias* dan PCF.
