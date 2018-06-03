package com.dev.hari.trackme.utility;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.dev.hari.trackme.R;

/**
 * Created by hariom.gupta on 5/22/2018.
 */

public class Utilities {

    public static void initToolBar(final AppCompatActivity activity, String titleName, int statusColor, int bgColor, boolean showBack) {
        Toolbar toolbar = activity.findViewById(R.id.toolbar);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            activity.getWindow().setStatusBarColor(activity.getResources().getColor(statusColor));
        }
        toolbar.setBackgroundColor(activity.getResources().getColor(bgColor));
        toolbar.setTitle(titleName);

        activity.setSupportActionBar(toolbar);
        activity.getSupportActionBar().setDisplayShowTitleEnabled(false);
        activity.getSupportActionBar().setDisplayShowHomeEnabled(true);

        activity.getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM | ActionBar.DISPLAY_SHOW_HOME);
        LayoutInflater inflater = LayoutInflater.from(activity);
        View view = inflater.inflate(R.layout.layout_toolbar_common, null);
        TextView tv_ToolbarTitle = view.findViewById(R.id.tv_ToolbarTitle);
        tv_ToolbarTitle.setText(titleName);

        activity.getSupportActionBar().setCustomView(view);
        activity.getSupportActionBar().setDisplayShowCustomEnabled(true);
        final ActionBar ab = activity.getSupportActionBar();
        toolbar.setNavigationIcon(R.drawable.ic_keyboard_backspace_black_24dp);
        if(!showBack){
            activity.getSupportActionBar().setDisplayShowHomeEnabled(false);
            activity.getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        }
        toolbar.setNavigationOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        activity.finish();
                    }
                }
        );
    }

    public static void hideKeyBoard(Activity activity) {
        try {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), InputMethodManager.RESULT_UNCHANGED_SHOWN);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void showKeyBoard(Activity activity) {
        try {
            InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            if (inputMethodManager != null) {
                inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void showMessage(Activity activity, String message) {
        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
//        Common.snackMessage((CoordinatorLayout)findViewById(R.id.coordinatorLayout), message);
    }

    public static boolean validatePhoneNumber(EditText mPhoneNumberField) {
        String phoneNumber = mPhoneNumberField.getText().toString();
        if (TextUtils.isEmpty(phoneNumber)) {
            mPhoneNumberField.setError("Invalid phone number.");
            return false;
        } else if (phoneNumber.length() < 10) {
            mPhoneNumberField.setError("Invalid phone number.");
            return false;
        } else if (phoneNumber.startsWith("1") || phoneNumber.startsWith("2")
                || phoneNumber.startsWith("3") || phoneNumber.startsWith("4")) {
            mPhoneNumberField.setError("Invalid phone number.");
            return false;
        }

        return true;
    }

    public static void enableViews(View... views) {
        for (View v : views) {
            v.setEnabled(true);
        }
    }

    public static void disableViews(View... views) {
        for (View v : views) {
            v.setEnabled(false);
        }
    }

    public static void enableVisible(View... views) {
        for (View v : views) {
            v.setVisibility(View.VISIBLE);
        }
    }

    public static void disableVisible(View... views) {
        for (View v : views) {
            v.setVisibility(View.GONE);
        }
    }
}
