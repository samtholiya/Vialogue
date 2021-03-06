package com.comp.iitb.vialogue.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.comp.iitb.vialogue.Network.LokavidyaSso.Apis.SignUp;
import com.comp.iitb.vialogue.R;
import com.comp.iitb.vialogue.coordinators.OnDoneSignIn;

public class CreateYourAccount2 extends AppCompatActivity {

    private static final String TAG = "Create Your Account2";

    //UI elements
    private EditText mEnterNameEditText;
    private EditText mEnterPasswordEditText;
    private EditText mConfirmPasswordEditText;
    private ImageView mShowEnterPasswordImageView;
    private ImageView mShowConfirmPasswordImageView;
    private Button mNextButton;
    private TextView mEnterNameTextView;
    private TextView mEnterPasswordTextView;
    private TextView mConfirmPasswordTextView;

    //variables
    private String mUserName;
    private String mPassword;
    private String mConfirmPassword;
    private boolean isDotPass = true;
    private boolean isDotConPass = true;
    private Toast mToast;
    private Context mContext;
    String responseString;

    SignUp.RegistrationType mRegistrationType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_your_account2);

        mContext = CreateYourAccount2.this;

        Bundle args = getIntent().getExtras();
        String registrationData = args.getString(getResources().getString(R.string.registrationData));
        String registrationType = args.getString(getResources().getString(R.string.registrationType));
        System.out.println("args: " +args);
        System.out.println("data: " +registrationData);
        System.out.println("type: " +registrationType);
        //String registrationdata = "9892264573"; //getResources().getString(R.string.email/phoneNo) can be use as key
        //String registrationType = getResources().getString(R.string.phoneNo);

        System.out.println("type: " +getResources().getString(R.string.email));
        mRegistrationType = (registrationType.equals(getResources().getString(R.string.email))) ?
                SignUp.RegistrationType.EMAIL_ID : SignUp.RegistrationType.PHONE_NUMBER;

        System.out.println("mRegistrationType: " + mRegistrationType);

        //Initialize ui elements
        mEnterNameEditText = (EditText) findViewById(R.id.et_enter_name);
        mEnterPasswordEditText = (EditText) findViewById(R.id.et_enter_password);
        mConfirmPasswordEditText = (EditText) findViewById(R.id.et_confirm_password);
        mShowEnterPasswordImageView = (ImageView) findViewById(R.id.iv_show_enter_password);
        mShowConfirmPasswordImageView = (ImageView) findViewById(R.id.iv_show_confirm_password);
        mNextButton = (Button) findViewById(R.id.btn_next);
        mEnterNameTextView = (TextView) findViewById(R.id.tv_enter_name);
        mEnterPasswordTextView = (TextView) findViewById(R.id.tv_enter_password);
        mConfirmPasswordTextView = (TextView) findViewById(R.id.tv_confirm_password);

        mShowEnterPasswordImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPassword = mEnterPasswordEditText.getText().toString();
                if(mPassword.length() == 0) return;
                if(isDotPass) {
                    mEnterPasswordEditText.setTransformationMethod(null);//changes to char
                    mEnterPasswordEditText.setSelection(mEnterPasswordEditText.getText().length());
                    mShowEnterPasswordImageView.setColorFilter(ContextCompat.getColor(mContext, R.color.colorPrimary));
                    isDotPass = false;
                } else {
                    mEnterPasswordEditText.setTransformationMethod(new PasswordTransformationMethod());//changes to dots
                    mEnterPasswordEditText.setSelection(mEnterPasswordEditText.getText().length());
                    mShowEnterPasswordImageView.setColorFilter(ContextCompat.getColor(mContext, android.R.color.darker_gray));
                    isDotPass = true;
                }

            }
        });

        mShowConfirmPasswordImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mConfirmPassword = mConfirmPasswordEditText.getText().toString();
                if(mConfirmPassword.length() == 0) return;
                if(isDotConPass) {
                    mConfirmPasswordEditText.setTransformationMethod(null);//changes to char
                    mConfirmPasswordEditText.setSelection(mConfirmPasswordEditText.getText().length());
                    mShowConfirmPasswordImageView.setColorFilter(ContextCompat.getColor(mContext, R.color.colorPrimary));
                    isDotConPass = false;
                } else {
                    mConfirmPasswordEditText.setTransformationMethod(new PasswordTransformationMethod());//changes to dots
                    mConfirmPasswordEditText.setSelection(mConfirmPasswordEditText.getText().length());
                    mShowConfirmPasswordImageView.setColorFilter(ContextCompat.getColor(mContext, android.R.color.darker_gray));
                    isDotConPass = true;
                }

            }
        });

        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mUserName = mEnterNameEditText.getText().toString();
                if(mUserName.length() == 0) {
                    mEnterNameEditText.requestFocus();
                    mEnterNameTextView.setTextColor(ContextCompat.getColor(mContext, android.R.color.holo_red_light));
                    return;
                } else {
                    mEnterNameTextView.setTextColor(ContextCompat.getColor(mContext, android.R.color.tab_indicator_text));
                }
                mPassword = mEnterPasswordEditText.getText().toString();
                if(mPassword.length() == 0) {
                    mEnterPasswordEditText.requestFocus();
                    mEnterPasswordTextView.setTextColor(ContextCompat.getColor(mContext, android.R.color.holo_red_light));
                    return;
                } else if(mPassword.length() < 8) {
                    Toast.makeText(mContext, "Passwords should contain minimum 8 characters", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    mEnterPasswordTextView.setTextColor(ContextCompat.getColor(mContext, android.R.color.tab_indicator_text));
                }
                mConfirmPassword = mConfirmPasswordEditText.getText().toString();
                if(mConfirmPassword.length() == 0) {
                    mConfirmPasswordEditText.requestFocus();
                    mConfirmPasswordTextView.setTextColor(ContextCompat.getColor(mContext, android.R.color.holo_red_light));
                    return;
                } else if(mConfirmPassword.length() < 8) {
                    Toast.makeText(mContext, "Passwords should contain minimum 8 characters", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    mConfirmPasswordTextView.setTextColor(ContextCompat.getColor(mContext, android.R.color.tab_indicator_text));
                }

                if(mPassword.equals(mConfirmPassword)) {
                    Log.d(TAG, "passwords match");
                    Intent intent = new Intent(mContext, LoginActivity.class);
                    SignUp.signUpInBackground(
                            mContext,
                            mUserName,
                            mRegistrationType,
                            registrationData,
                            mPassword,
                            new OnDoneSignIn() {
                                @Override
                                public void done(SignUp.SignUpResponse signUpResponse) {
                                    switch (signUpResponse.getResponseType()) {
                                        case USER_SIGNED_UP:
                                            responseString = signUpResponse.getResponseString();
                                            startActivity(intent);
                                        case USER_ALREADY_EXISTS:
                                            responseString = signUpResponse.getResponseString();
                                            startActivity(intent);
                                        case COULD_NOT_SIGN_UP:
                                            responseString = signUpResponse.getResponseString();
                                        case NETWORK_ERROR:
                                            responseString = signUpResponse.getResponseString();
                                    }
                                    Toast.makeText(mContext, responseString, Toast.LENGTH_SHORT).show();
                                    System.out.println("signUpResponse:" + signUpResponse);
                                }
                            });

                } else {
                    Toast.makeText(mContext, "Passwords do not match", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }
}
