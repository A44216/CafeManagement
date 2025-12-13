package com.sinhviencafemanagement.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.OptIn;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ExperimentalGetImage;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.face.Face;
import com.google.mlkit.vision.face.FaceDetection;
import com.google.mlkit.vision.face.FaceDetector;
import com.google.mlkit.vision.face.FaceDetectorOptions;
import com.sinhviencafemanagement.R;
import com.sinhviencafemanagement.utils.FaceRecognitionHelper;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FaceCaptureActivity extends AppCompatActivity {
    private static final String TAG = "FaceCaptureActivity";
    private static final int PERMISSION_REQUEST_CAMERA = 1001;
    public static final String EXTRA_EMBEDDING = "extra_embedding"; // Key để trả về kết quả

    private PreviewView previewView;
    private TextView tvStatus;
    private FaceRecognitionHelper faceHelper;
    private ExecutorService cameraExecutor;
    private FaceDetector faceDetector;

    private boolean isProcessing = false; // Flag để tránh xử lý nhiều frame cùng lúc

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_face_capture);

        previewView = findViewById(R.id.previewView);
        tvStatus = findViewById(R.id.tvStatus);
        Button btnBack = findViewById(R.id.btnBack);

        btnBack.setOnClickListener(v -> finish());

        faceHelper = new FaceRecognitionHelper(this);
        cameraExecutor = Executors.newSingleThreadExecutor();

        // Cấu hình ML Kit Face Detector
        FaceDetectorOptions options =
                new FaceDetectorOptions.Builder()
                        .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
                        .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_NONE)
                        .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_NONE)
                        .build();
        faceDetector = FaceDetection.getClient(options);

        // Kiểm tra quyền Camera
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            startCamera();
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA}, PERMISSION_REQUEST_CAMERA);
        }
    }

    private void startCamera() {
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture =
                ProcessCameraProvider.getInstance(this);

        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();

                // Preview
                Preview preview = new Preview.Builder().build();
                preview.setSurfaceProvider(previewView.getSurfaceProvider());

                // Image Analysis (Xử lý frame)
                ImageAnalysis imageAnalysis = new ImageAnalysis.Builder()
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        .build();

                imageAnalysis.setAnalyzer(cameraExecutor, this::analyzeImage);

                // Chọn camera trước
                CameraSelector cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA;

                try {
                    cameraProvider.unbindAll();
                    cameraProvider.bindToLifecycle(
                            this, cameraSelector, preview, imageAnalysis);
                    runOnUiThread(() -> tvStatus.setText("Vui lòng nhìn thẳng vào camera"));
                } catch (Exception exc) {
                    Log.e(TAG, "Use case binding failed", exc);
                }

            } catch (ExecutionException | InterruptedException e) {
                Log.e(TAG, "Camera provider error", e);
            }
        }, ContextCompat.getMainExecutor(this));
    }

    @OptIn(markerClass = ExperimentalGetImage.class)
    private void analyzeImage(@NonNull ImageProxy imageProxy) {
        if (isProcessing) {
            imageProxy.close();
            return;
        }

        Image mediaImage = imageProxy.getImage();
        if (mediaImage != null) {
            InputImage image = InputImage.fromMediaImage(mediaImage, imageProxy.getImageInfo().getRotationDegrees());

            faceDetector.process(image)
                    .addOnSuccessListener(faces -> {
                        if (!faces.isEmpty()) {
                            processDetectedFaces(faces, imageProxy);
                        } else {
                            runOnUiThread(() -> tvStatus.setText("Không tìm thấy khuôn mặt..."));
                            imageProxy.close();
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Face detection failed", e);
                        imageProxy.close();
                    });
        } else {
            imageProxy.close();
        }
    }

    private void processDetectedFaces(List<Face> faces, ImageProxy imageProxy) {
        // Lấy khuôn mặt to nhất (giả sử chỉ có 1 người thao tác)
        Face face = faces.get(0);
        Rect bounds = face.getBoundingBox();

        // Kiểm tra kích thước khuôn mặt có đủ lớn không (để đảm bảo chất lượng)
        // Ví dụ: Chiều rộng khuôn mặt phải > 30% chiều rộng ảnh
        if (bounds.width() < imageProxy.getWidth() * 0.3) {
            runOnUiThread(() -> tvStatus.setText("Hãy đưa mặt lại gần hơn"));
            imageProxy.close();
            return;
        }

        // Bắt đầu xử lý lấy embedding
        isProcessing = true;
        runOnUiThread(() -> tvStatus.setText("Đang xử lý... Giữ nguyên!"));

        // Convert ImageProxy to Bitmap
        Bitmap bitmap = imageProxyToBitmap(imageProxy);
        if (bitmap == null) {
            isProcessing = false;
            imageProxy.close();
            return;
        }

        // Cần xoay bitmap cho đúng hướng (Front camera thường bị xoay)
        int rotationDegrees = imageProxy.getImageInfo().getRotationDegrees();
        if (rotationDegrees != 0) {
            android.graphics.Matrix matrix = new android.graphics.Matrix();
            matrix.postRotate(rotationDegrees);
            // Flip horizontal cho giống gương (nếu cần thiết cho UX, nhưng model AI không quan trọng flip)
             matrix.postScale(-1, 1, bitmap.getWidth() / 2f, bitmap.getHeight() / 2f); 
            bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        }

        // Crop khuôn mặt từ Bitmap gốc
        // Cần mapping coordinates từ ML Kit (dựa trên ảnh gốc) sang Bitmap
        // Vì ta đã lấy bitmap từ chính imageProxy nên coordinates là khớp (sau khi xoay nếu cần)
        // Tuy nhiên, bounds có thể bị ra ngoài viền, cần fix
        int x = Math.max(0, bounds.left);
        int y = Math.max(0, bounds.top);
        int w = Math.min(bitmap.getWidth() - x, bounds.width());
        int h = Math.min(bitmap.getHeight() - y, bounds.height());

        if (w > 0 && h > 0) {
            Bitmap faceBitmap = Bitmap.createBitmap(bitmap, x, y, w, h);
            
            // Get Embedding
            float[] embedding = faceHelper.getFaceEmbedding(faceBitmap);

            if (embedding != null) {
                // Trả kết quả về
                String embeddingJson = faceHelper.embeddingToString(embedding);
                Intent resultIntent = new Intent();
                resultIntent.putExtra(EXTRA_EMBEDDING, embeddingJson);
                setResult(RESULT_OK, resultIntent);
                finish();
            } else {
                runOnUiThread(() -> {
                    tvStatus.setText("Lỗi nhận diện. Thử lại.");
                    isProcessing = false;
                });
            }
        } else {
            isProcessing = false;
        }
        
        imageProxy.close();
    }

    // Helper: Chuyển ImageProxy (YUV) sang Bitmap
    private Bitmap imageProxyToBitmap(ImageProxy image) {
        ImageProxy.PlaneProxy[] planes = image.getPlanes();
        ByteBuffer yBuffer = planes[0].getBuffer();
        ByteBuffer uBuffer = planes[1].getBuffer();
        ByteBuffer vBuffer = planes[2].getBuffer();

        int ySize = yBuffer.remaining();
        int uSize = uBuffer.remaining();
        int vSize = vBuffer.remaining();

        byte[] nv21 = new byte[ySize + uSize + vSize];
        // U and V are swapped
        yBuffer.get(nv21, 0, ySize);
        vBuffer.get(nv21, ySize, vSize);
        uBuffer.get(nv21, ySize + vSize, uSize);

        YuvImage yuvImage = new YuvImage(nv21, ImageFormat.NV21, image.getWidth(), image.getHeight(), null);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        yuvImage.compressToJpeg(new Rect(0, 0, yuvImage.getWidth(), yuvImage.getHeight()), 100, out);

        byte[] imageBytes = out.toByteArray();
        return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CAMERA) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startCamera();
            } else {
                Toast.makeText(this, "Cần quyền Camera để sử dụng tính năng này", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cameraExecutor.shutdown();
    }
}
