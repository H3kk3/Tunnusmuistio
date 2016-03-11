package io.h3kk3.tunnusmuistio.app;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.app.LoaderManager.LoaderCallbacks;

import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.math.BigInteger;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static android.Manifest.permission.READ_CONTACTS;

/**
 * A login screen that offers login via email/password.
 */
public class AddNew extends AppCompatActivity  {

    private TunnusDbAdapter mDbAdapter;
    private EditText mNickView;
    private EditText mIbanView;
    private View mProgressView;
    private View mLoginFormView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addnew);
        setupActionBar();
        // Set up the login form.
        mNickView = (EditText) findViewById(R.id.nick);

        mIbanView = (EditText) findViewById(R.id.iban);
        mIbanView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.savenew_button || id == EditorInfo.IME_NULL) {
                    trySaveNew();
                    return true;
                }
                return false;
            }
        });

        Button mSaveNewButton = (Button) findViewById(R.id.savenew_button);
        mSaveNewButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                trySaveNew();
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
    }


    /**
     * Set up the {@link android.app.ActionBar}, if the API is available.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void setupActionBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            // Show the Up button in the action bar.
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void trySaveNew() {

        // Reset error.
        mIbanView.setError(null);

        // Store values at the time of the login attempt.
        String nick = mNickView.getText().toString();
        String iban = mIbanView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(iban) && !isIbanValid(iban)) {
            mIbanView.setError("Antamasi IBAN-tunnus on virheellinen");
            focusView = mIbanView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(nick)) {
            mNickView.setError("Anna kaverin nimi tai lempinimi");
            focusView = mNickView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            saveToDatabase(nick, iban);
        }
    }


    private boolean isIbanValid(String iban) {
        // TO DO: create optional check/transformation for BBAN-numbers
        iban = iban.replace(" ", "");
        if (iban.length() < 15 || iban.length() > 18) {
            return false;
        }
        iban = iban.substring(4) + iban.substring(0, 4);
        StringBuilder numberIban = new StringBuilder();
        for (int i = 0;i < iban.length();i++) {
            numberIban.append(Character.getNumericValue(iban.charAt(i)));
        }
        BigInteger ninetyseven = new BigInteger("97");
        BigInteger ibanNumber = new BigInteger(numberIban.toString());
        return ibanNumber.mod(ninetyseven).intValue() == 1;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    private void saveToDatabase(String nick, String iban){
        try {
            mDbAdapter = new TunnusDbAdapter(AddNew.this);
            mDbAdapter.open();
            mDbAdapter.createMemo(nick, iban);
            mDbAdapter.close();
            finish();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

}

