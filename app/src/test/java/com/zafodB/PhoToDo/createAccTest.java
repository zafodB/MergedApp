package com.zafodB.PhoToDo;

import android.content.Context;
import android.content.Intent;
import android.test.ActivityInstrumentationTestCase2;
import android.test.TouchUtils;
import android.test.ViewAsserts;
import android.test.suitebuilder.annotation.SmallTest;
import android.widget.Button;
import android.widget.EditText;

import com.zafodB.PhoToDo.CreateAccountActivity;

/**
 * Created by filip on 15/05/2016.
 */
public class createAccTest extends ActivityInstrumentationTestCase2<CreateAccountActivity> {

    private EditText email;
    private Button button;
    private CreateAccountActivity myActivity;


    public createAccTest() {
        super(CreateAccountActivity.class);

    }

    @Override
    protected void setUp() throws Exception {


//        myActivity.startActivity(new Intent(Intent));

//        myActivity = getActivity();



        email =  (EditText) myActivity.findViewById(com.zafodB.PhoToDo.R.id.sing_up_email);
        button = (Button) myActivity.findViewById(com.zafodB.PhoToDo.R.id.create_acc_button);

        super.setUp();
    }

    @SmallTest
    public void testEmail(){
        assertNotNull(email);
        ViewAsserts.assertOnScreen(email.getRootView(), email);

        testDriver();

    }

    public void testDriver(){
        for (int i = 0 ; i < 255; i++) {
            email.setText(i);

            TouchUtils.tapView(this, button);

            assertNotNull(email);

        }
    }
}
