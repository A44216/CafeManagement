package com.sinhviencafemanagement.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import org.pytorch.IValue;
import org.pytorch.LiteModuleLoader;
import org.pytorch.Module;
import org.pytorch.Tensor;
import org.pytorch.torchvision.TensorImageUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;

public class FaceRecognitionHelper {
    private static final String TAG = "FaceRecognitionHelper";
    private static final String MODEL_NAME = "face_recognition_model.ptl";
    private static final int INPUT_SIZE = 112; // Kích thước ảnh đầu vào cho model (tùy chỉnh theo model thực tế)

    private Module module;
    private final Context context;
    private final Gson gson;

    public FaceRecognitionHelper(Context context) {
        this.context = context;
        this.gson = new Gson();
        try {
            module = LiteModuleLoader.load(assetFilePath(context, MODEL_NAME));
            Log.d(TAG, "Model loaded successfully");
        } catch (IOException e) {
            Log.e(TAG, "Error loading model", e);
            module = null;
        }
    }

    // Copy file từ assets ra storage để PyTorch load được
    private static String assetFilePath(Context context, String assetName) throws IOException {
        File file = new File(context.getFilesDir(), assetName);
        if (file.exists() && file.length() > 0) {
            return file.getAbsolutePath();
        }

        try (InputStream is = context.getAssets().open(assetName)) {
            try (OutputStream os = new FileOutputStream(file)) {
                byte[] buffer = new byte[4 * 1024];
                int read;
                while ((read = is.read(buffer)) != -1) {
                    os.write(buffer, 0, read);
                }
                os.flush();
            }
            return file.getAbsolutePath();
        }
    }

    // Chuyển đổi Bitmap khuôn mặt thành vector đặc trưng (Embedding)
    public float[] getFaceEmbedding(Bitmap faceBitmap) {
        if (module == null) {
            Log.e(TAG, "Model is not initialized");
            return null;
        }

        // Resize bitmap về kích thước input của model
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(faceBitmap, INPUT_SIZE, INPUT_SIZE, true);

        // Chuẩn hóa ảnh: mean và std thường là [0.5, 0.5, 0.5] cho các model face recognition phổ biến
        // Hoặc [0.485, 0.456, 0.406] và [0.229, 0.224, 0.225] cho ImageNet models
        // Ở đây giả định dùng chuẩn [0.5...]
        final float[] NO_MEAN_RGB = new float[] {0.5f, 0.5f, 0.5f};
        final float[] NO_STD_RGB = new float[] {0.5f, 0.5f, 0.5f};

        Tensor inputTensor = TensorImageUtils.bitmapToFloat32Tensor(
                resizedBitmap,
                TensorImageUtils.TORCHVISION_NORM_MEAN_RGB,
                TensorImageUtils.TORCHVISION_NORM_STD_RGB
        );

        // Run inference
        try {
            Tensor outputTensor = module.forward(IValue.from(inputTensor)).toTensor();
            float[] embedding = outputTensor.getDataAsFloatArray();
            return embedding;
        } catch (Exception e) {
            Log.e(TAG, "Error running inference", e);
            return null;
        }
    }

    // Tính độ tương đồng Cosine (Cosine Similarity) giữa 2 vector
    public double calculateCosineSimilarity(float[] vectorA, float[] vectorB) {
        if (vectorA == null || vectorB == null || vectorA.length != vectorB.length) {
            return -1.0;
        }

        double dotProduct = 0.0;
        double normA = 0.0;
        double normB = 0.0;

        for (int i = 0; i < vectorA.length; i++) {
            dotProduct += vectorA[i] * vectorB[i];
            normA += Math.pow(vectorA[i], 2);
            normB += Math.pow(vectorB[i], 2);
        }

        if (normA == 0 || normB == 0) {
            return 0.0;
        }

        return dotProduct / (Math.sqrt(normA) * Math.sqrt(normB));
    }

    // Chuyển đổi float array sang JSON String để lưu vào DB
    public String embeddingToString(float[] embedding) {
        if (embedding == null) return null;
        return gson.toJson(embedding);
    }

    // Chuyển đổi JSON String từ DB sang float array
    public float[] stringToEmbedding(String json) {
        if (json == null || json.isEmpty()) return null;
        Type type = new TypeToken<float[]>(){}.getType();
        return gson.fromJson(json, type);
    }
}
