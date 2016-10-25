package com.zafodB.PhoToDo;

import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.firebase.client.Firebase;

/**
 * Created by filip on 07/04/2016.
 */
public class Feedback extends AppCompatActivity {
    private Firebase localRef;

    private EditText feedbackText;
    private Button sendFeedback;

    //Sets up feedback activity with simple method sending feedback.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.send_feedback_activity);

        localRef = ApplicationMain.getFirebaseRef();

        feedbackText = (EditText) findViewById(R.id.feedback_edit);

        sendFeedback = (Button) findViewById(R.id.send_feedback);
        sendFeedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                localRef.child("Feedback").child(ApplicationMain.getUserAuthData().getUid() + " " + String.valueOf
                        (SystemClock.currentThreadTimeMillis())).setValue(feedbackText.getText().toString());
            }
        });
    }
}
