package mymajorproject.ginger;


import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.rojoxpress.slidebutton.SlideButton;

import org.json.JSONException;
import org.json.JSONObject;

public class EmotionScreen extends AppCompatActivity {

    String emotionData;
    SlideButton slideButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emotion_screen);

        emotionData = getIntent().getStringExtra("emotions");
        Log.e("WORK",emotionData);
        addemotion();


        slideButton = (SlideButton) findViewById(R.id.slide_button);

        slideButton.setSlideButtonListener(new SlideButton.SlideButtonListener() {
            @Override
            public void onSlide() {
                startActivity(new Intent(EmotionScreen.this,ChatActivity.class));

            }
        });

    }

    private void addemotion() {
        TextView emotionTextView;
        String text;

        emotionTextView = (TextView) findViewById(R.id.emotiondata1);
        text = "Sadness: "+extractEmotionFromJson(emotionData,"sadness");
        emotionTextView.setText(text);

        emotionTextView = (TextView) findViewById(R.id.emotiondata2);
        text = "Neutral: "+extractEmotionFromJson(emotionData,"neutral");
        emotionTextView.setText(text);

        emotionTextView = (TextView) findViewById(R.id.emotiondata3);
        text = "Contempt: "+extractEmotionFromJson(emotionData,"contempt");
        emotionTextView.setText(text);

        emotionTextView = (TextView) findViewById(R.id.emotiondata4);
        text = "Disgust: "+extractEmotionFromJson(emotionData,"disgust");
        emotionTextView.setText(text);

        emotionTextView = (TextView) findViewById(R.id.emotiondata5);
        text = "Anger: "+extractEmotionFromJson(emotionData,"anger");
        emotionTextView.setText(text);

        emotionTextView = (TextView) findViewById(R.id.emotiondata6);
        text = "Surprise: "+extractEmotionFromJson(emotionData,"surprise");
        emotionTextView.setText(text);

        emotionTextView = (TextView) findViewById(R.id.emotiondata7);
        text = "Fear: "+extractEmotionFromJson(emotionData,"fear");
        emotionTextView.setText(text);

        emotionTextView = (TextView) findViewById(R.id.emotiondata8);
        text = "Happiness: "+extractEmotionFromJson(emotionData,"happiness");
        emotionTextView.setText(text);

    }

    public static Float extractEmotionFromJson(String response,String emotionName) {
        Log.d("CPA",response);

        try {
            JSONObject baseJsonResponse = new JSONObject(response);
            Float emotion = new Float(baseJsonResponse.getDouble(emotionName));
            return emotion;

        } catch (JSONException e) {
            Log.e("CPA", "Problem in getting specific emotion", e);
        }
        return null;
    }



}
