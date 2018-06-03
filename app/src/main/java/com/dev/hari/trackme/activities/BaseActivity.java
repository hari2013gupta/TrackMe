package com.dev.hari.trackme.activities;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;

import com.dev.hari.trackme.interfaces.MyConstants;
import com.google.firebase.auth.FirebaseAuth;



public class BaseActivity extends AppCompatActivity implements MyConstants {

    private ProgressDialog mProgressDialog;

    public void showProgressDialog(String message) {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setCancelable(false);
            mProgressDialog.setMessage(message);
        }

        mProgressDialog.show();
    }

    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    public String getUid() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }


}
