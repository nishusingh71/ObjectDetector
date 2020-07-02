package com.example.objectdetector;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.label.FirebaseVisionImageLabel;
import com.google.firebase.ml.vision.label.FirebaseVisionImageLabeler;

import java.io.IOException;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    ImageView detectedImage;
    TextView result;
    Button DetectorButton;
    public static final int OBJ=126;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        detectedImage=findViewById(R.id.imageViewDetect);
        result=findViewById(R.id.textViewRes);
        DetectorButton=findViewById(R.id.buttonDetect);

        DetectorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent =new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,"Choose object images"),OBJ);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==OBJ){
            result.setText(" ");
            detectedImage.setImageURI(data.getData());
        }
        FirebaseVisionImage image;
        try {
            image=FirebaseVisionImage.fromFilePath(getApplicationContext(),data.getData());
            FirebaseVisionImageLabeler imageLabeler= FirebaseVision.getInstance().getOnDeviceImageLabeler();

            imageLabeler.processImage(image).addOnSuccessListener(new OnSuccessListener<List<FirebaseVisionImageLabel>>() {
                @Override
                public void onSuccess(List<FirebaseVisionImageLabel> firebaseVisionImageLabels) {
                    for (FirebaseVisionImageLabel label:firebaseVisionImageLabels){
                        String text=label.getText();
                        float confidence=label.getConfidence();
                        result.append(text+"   "+confidence+"\n");
                    }

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(MainActivity.this,e.getMessage(), Toast.LENGTH_SHORT).show();

                }
            });

        }catch (IOException e) {
            e.printStackTrace();
        }

    }
}