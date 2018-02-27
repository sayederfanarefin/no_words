package info.sayederfanarefin.location_sharing.utils;

/**
 * Created by erfan on 9/18/17.
 */

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.microsoft.projectoxford.emotion.EmotionServiceClient;
import com.microsoft.projectoxford.emotion.contract.RecognizeResult;
import com.microsoft.projectoxford.emotion.rest.EmotionServiceException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class doRequest extends AsyncTask<String, String, List<RecognizeResult>> {

    private Exception e = null;
    private Bitmap mBitmap;
    Handler myHandler;

    private EmotionServiceClient client;

    public doRequest(Bitmap mBitmap, EmotionServiceClient client, Handler myHandler ) {

        this.mBitmap = mBitmap;
        this.client = client;
        this.myHandler = myHandler;

        Log.v("-x-", "---" + "do req");
    }

    @Override
    protected List<RecognizeResult> doInBackground(String... args) {

        try {
            return processWithAutoFaceDetection();
        } catch (Exception e) {
            this.e = e;    // Store error
            Log.v("-x-", "---" + "doInBackground exception"+ e.toString());
        }

        return null;
    }

    @Override
    protected void onPostExecute(List<RecognizeResult> result) {
        super.onPostExecute(result);

        if (e != null) {
            this.e = null;
            Log.v("-x-", "---" + "null");
        } else {
            if (result.size() == 0) {
            } else {
                for (RecognizeResult r : result) {

                    double results_array[] = new double[]{
                            r.scores.anger,
                            r.scores.contempt,
                            r.scores.disgust,
                            r.scores.fear,
                            r.scores.happiness,
                            r.scores.neutral,
                            r.scores.sadness,
                            r.scores.surprise
                    };

                    double results_backup_array[] = new double[]{
                            r.scores.anger,
                            r.scores.contempt,
                            r.scores.disgust,
                            r.scores.fear,
                            r.scores.happiness,
                            r.scores.neutral,
                            r.scores.sadness,
                            r.scores.surprise
                    };

                    Arrays.sort(results_array);
                    double the_one = results_array[results_array.length-1];
                    int the_one_index=-1;
                    for(int x =0; x < 8; x++){
                        if(the_one == results_backup_array[x]){
                            the_one_index = x;
                            break;
                        }
                    }

                    String xx = "";
                    if(the_one_index == -1){
                        xx = "no emotion";
                    }else if (the_one_index == 0){
                        xx = "anger";
                    }else if (the_one_index == 1){
                        xx = "contempt";
                    }else if (the_one_index == 2){
                        xx = "disgust";
                    }else if (the_one_index == 3){
                        xx = "fear";
                    }else if (the_one_index == 4){
                        xx = "happpiness";
                    }else if (the_one_index == 5){
                        xx = "neutral";
                    }else if (the_one_index == 6){
                        xx = "sadness";
                    }else if (the_one_index == 7){
                        xx = "surprise";
                    }

                    Log.v("-x-", "Emotion: "+ xx);

                    Message m = new Message();
                    Bundle b = new Bundle();
                    b.putString("emotion", xx);
                    m.setData(b);
                    myHandler.sendMessage(m);
                }
            }
        }
    }

    private List<RecognizeResult> processWithAutoFaceDetection() throws EmotionServiceException, IOException {

        ByteArrayOutputStream output = new ByteArrayOutputStream();
        mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, output);
        ByteArrayInputStream inputStream = new ByteArrayInputStream(output.toByteArray());
        List<RecognizeResult> result = null;
        result = this.client.recognizeImage(inputStream);
        return result;
    }
}