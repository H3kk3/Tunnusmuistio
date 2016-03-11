package io.h3kk3.tunnusmuistio.app;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.math.BigInteger;
import java.sql.SQLException;


public class AddNew extends AppCompatActivity {

    private TunnusDbAdapter mDbAdapter;
    private EditText mNickView;
    private EditText mIbanView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addnew);
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
    }


    private void trySaveNew() {

        // Reset error.
        mIbanView.setError(null);

        // Store values at the time of the login attempt.
        String nick = mNickView.getText().toString();
        String iban = mIbanView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Checks the IBAN
        if (!TextUtils.isEmpty(iban) && !isIbanValid(iban)) {
            mIbanView.setError("Antamasi IBAN-tunnus on virheellinen");
            focusView = mIbanView;
            cancel = true;
        }

        // makes sure user gave name
        if (TextUtils.isEmpty(nick)) {
            mNickView.setError("Anna kaverin nimi tai lempinimi");
            focusView = mNickView;
            cancel = true;
        }
        if (cancel) {
            focusView.requestFocus();
        } else {
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
        for (int i = 0; i < iban.length(); i++) {
            numberIban.append(Character.getNumericValue(iban.charAt(i)));
        }
        BigInteger ninetyseven = new BigInteger("97");
        BigInteger ibanNumber = new BigInteger(numberIban.toString());
        return ibanNumber.mod(ninetyseven).intValue() == 1;
    }


    private void saveToDatabase(String nick, String iban) {
        try {
            mDbAdapter = new TunnusDbAdapter(AddNew.this);
            mDbAdapter.open();
            mDbAdapter.createMemo(nick, iban);
            mDbAdapter.close();
            finish();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

