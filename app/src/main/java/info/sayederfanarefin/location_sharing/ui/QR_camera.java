package info.sayederfanarefin.location_sharing.ui;

import android.content.Intent;
import android.graphics.PointF;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.dlazaro66.qrcodereaderview.QRCodeReaderView;

import info.sayederfanarefin.location_sharing.R;
import info.sayederfanarefin.location_sharing.utils.PointsOverlayView;

public class QR_camera extends AppCompatActivity {
    private QRCodeReaderView qrCodeReaderView;
    private PointsOverlayView pointsOverlayView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr);

        qrCodeReaderView = (QRCodeReaderView) findViewById(R.id.qrdecoderview);
        pointsOverlayView = (PointsOverlayView) findViewById(R.id.points_overlay_view);

        qrCodeReaderView.setOnQRCodeReadListener(new QRCodeReaderView.OnQRCodeReadListener() {
            @Override
            public void onQRCodeRead(String text, PointF[] points) {

                pointsOverlayView.setPoints(points);

                Intent data = new Intent();
                data.putExtra("friend_id",text);
                setResult(RESULT_OK,data);
                finish();

            }
        });

        // Use this function to enable/disable decoding
        qrCodeReaderView.setQRDecodingEnabled(true);

        // Use this function to change the autofocus interval (default is 5 secs)
        qrCodeReaderView.setAutofocusInterval(2000L);

        // Use this function to enable/disable Torch
        qrCodeReaderView.setTorchEnabled(true);

        // Use this function to set front camera preview
        qrCodeReaderView.setFrontCamera();

        // Use this function to set back camera preview
        qrCodeReaderView.setBackCamera();
    }

    @Override
    public void onResume() {
        super.onResume();
        qrCodeReaderView.startCamera();
       // // Log.v("====qr", "resume");
    }

    @Override
    public void onPause() {
        super.onPause();
        qrCodeReaderView.stopCamera();
      //  // Log.v("====qr", "pause");
    }
}
