package com.zcorp.app.foodvision.Activities;

import android.graphics.Typeface;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.services.vision.v1.Vision;
import com.google.api.services.vision.v1.VisionRequestInitializer;
import com.google.api.services.vision.v1.model.AnnotateImageRequest;
import com.google.api.services.vision.v1.model.AnnotateImageResponse;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesRequest;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesResponse;
import com.google.api.services.vision.v1.model.EntityAnnotation;
import com.google.api.services.vision.v1.model.FaceAnnotation;
import com.google.api.services.vision.v1.model.Feature;
import com.google.api.services.vision.v1.model.Image;
import com.zcorp.app.foodvision.Adapters.Description_RV_Adatper;
import com.zcorp.app.foodvision.Data.Cloud_Data;
import com.zcorp.app.foodvision.R;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private Vision vision;
    ImageView imageView;
    RecyclerView description_rv;
    ArrayList<Cloud_Data> data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView=(ImageView) findViewById(R.id.imageView);
        description_rv=(RecyclerView) findViewById(R.id.description_rv);
        data=new ArrayList<>();


        imageView.setImageResource(R.drawable.pizza);


        Vision.Builder visionBuilder = new Vision.Builder(new NetHttpTransport(), new AndroidJsonFactory(),null);
        visionBuilder.setVisionRequestInitializer(new VisionRequestInitializer(getString(R.string.GOOGLE_CLOUD_API)));

        vision= visionBuilder.build();

        if(vision!=null){
            Log.e("vision","vision builded");
        }

        detection();


    }


    private void detection() {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    InputStream inputStream = getResources().openRawResource(R.raw.pizza);
                    byte[] photoData = IOUtils.toByteArray(inputStream);

                    Image inputImage = new Image();
                    inputImage.encodeContent(photoData);

                    Feature desiredFeature = new Feature();
                    desiredFeature.setType("LABEL_DETECTION");

                    final AnnotateImageRequest request = new AnnotateImageRequest();
                    request.setImage(inputImage);
                    request.setFeatures(Arrays.asList(desiredFeature));

                    BatchAnnotateImagesRequest batchRequest = new BatchAnnotateImagesRequest();
                    batchRequest.setRequests(Arrays.asList(request));

                    BatchAnnotateImagesResponse batchResponse =
                            vision.images().annotate(batchRequest).execute();

                    String message="";
                    final List<AnnotateImageResponse> responses = batchResponse.getResponses();

//                    for faces
//                    final List<FaceAnnotation> res = batchResponse.getResponses();


                    for(int i=0;i<responses.get(0).getLabelAnnotations().size();i++){
                        message+="Description:"+ responses.get(0).getLabelAnnotations().get(i).getDescription()+"\n";
                        message+="Score:"+ responses.get(0).getLabelAnnotations().get(i).getScore()+"\n\n";
                        Log.e("vision",i+message);

                        Cloud_Data temp=new Cloud_Data();
                        temp.setDescription(responses.get(0).getLabelAnnotations().get(i).getDescription());
                        temp.setScore(responses.get(0).getLabelAnnotations().get(i).getScore());

                        data.add(temp);
                    }



                    final String finalResult= message;
                    Log.e("vision",message);

////                    for faces
//                    int numberOfFaces = res.size();
//                    String likelihoods = "";
//                    for(int i=0; i<numberOfFaces; i++) {
//                        likelihoods += "\n It is " +
//                                res.get(i).toPrettyString() +
//                                " that face " + i + " is happy";
//                    }
//                    final String message =
//                            "This photo has " + numberOfFaces + " faces" + likelihoods;


                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
//                            result.setText(finalResult);

                            setRecyclerView();
                        }
                    });


                } catch(Exception e) {
                    Log.d("ERROR", e.getMessage());
                }
            }
        });
    }

    public void setRecyclerView(){
        LinearLayoutManager layoutManager=new LinearLayoutManager(this);
        layoutManager.setAutoMeasureEnabled(true);
        description_rv.setLayoutManager(layoutManager);
        description_rv.setNestedScrollingEnabled(false);
        Description_RV_Adatper adatper=new Description_RV_Adatper(data,this);
        description_rv.setAdapter(adatper);
    }
}
