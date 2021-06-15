package com.example.financy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.textfield.TextInputEditText;

import static com.example.financy.Constants.*;

public class ActivityAdd extends AppCompatActivity {

    private BottomNavigationView mBottomNavigationView;
    private TextInputEditText mTextInputEditTextAmount;
    private TextInputEditText mTextInputEditTextTitle;
    private TextInputEditText mTextInputEditTextIncome;
    private TextView mTextViewSaveEntry;
    private TextView mTextViewSaveIncome;
    private SharedPreferences mSharedPreferences;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        setup();
    }

    private void saveIncome() {
        String income = mTextInputEditTextIncome.getText().toString();
        if (income.length() > 0) {
            SharedPreferences.Editor editor = mSharedPreferences.edit();
            editor.putString(SAVE_NAME_INCOME, mTextInputEditTextIncome.getText().toString());
            editor.commit();
            mTextInputEditTextIncome.setText("");
            mTextInputEditTextIncome.clearFocus();
            makeToast("Nettoeinkommen wurde gespeichert!");
        }
        else {
            mTextInputEditTextIncome.requestFocus();
            makeToast("Nettoeinkommen muss angegeben werden!");
        }
    }

    private void saveNewEntry() {
        String title = mTextInputEditTextTitle.getText().toString();
        String amount = mTextInputEditTextAmount.getText().toString();

        if (title.length() > 0 && amount.length() > 0) {
            saveTitle(title);
            saveAmount(amount);
            mTextInputEditTextTitle.setText("");
            mTextInputEditTextAmount.setText("");
            mTextInputEditTextAmount.clearFocus();
            mTextInputEditTextTitle.clearFocus();
            hideKeyboard(this);
            makeToast("Eintrag wurde gespeichert!");
        }
        else {
            if (title.length() == 0 && amount.length() == 0) {
                mTextInputEditTextTitle.requestFocus();
                makeToast("Titel/Monatlicher Betrag ausfüllen!");
            }
            else if (title.length() == 0) {
                mTextInputEditTextTitle.requestFocus();
                makeToast("Titel muss angegeben werden!");
            }
            else {
                mTextInputEditTextAmount.requestFocus();
                makeToast("Monatlicher Beitrag muss angegeben werden!");
            }
        }

    }

    private void saveAmount(String amount) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        String allAmounts = mSharedPreferences.getString(SAVE_NAME_AMOUNTS, "");
        if (allAmounts.length() == 0) {
            allAmounts = amount.trim();
        }
        else {
            allAmounts = allAmounts + SHARED_PREFERENCES_DELIMITER + amount.trim();
        }
        editor.putString(SAVE_NAME_AMOUNTS, allAmounts);
        editor.commit();
    }

    private void saveTitle(String title) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        String allTitles = mSharedPreferences.getString(SAVE_NAME_TITLES, "");
        if (allTitles.length() == 0) {
            allTitles = title.trim();
        }
        else {
            allTitles = allTitles + SHARED_PREFERENCES_DELIMITER + title.trim();
        }
        editor.putString(SAVE_NAME_TITLES, allTitles);
        editor.commit();
    }

    private void setup() {
        mSharedPreferences = getSharedPreferences(SAVE_NAME_SHARED_PREFERENCES, Context.MODE_PRIVATE);
        mContext = getApplicationContext();
        setupBottomNavigationBar();
//        resetSharedPreferences();
        mTextInputEditTextAmount = findViewById(R.id.amount_input);
        mTextInputEditTextTitle = findViewById(R.id.title_input);
        mTextInputEditTextIncome = findViewById(R.id.income_input);
        mTextViewSaveEntry = findViewById(R.id.save_entry_button);
        mTextViewSaveIncome = findViewById(R.id.save_income_button);

        mTextViewSaveEntry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveNewEntry();
            }
        });

        mTextViewSaveIncome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveIncome();
            }
        });
    }

    private void setupBottomNavigationBar() {
        mBottomNavigationView = findViewById(R.id.bottom_navigation);
        mBottomNavigationView.setSelectedItemId(R.id.add_entry);
        mBottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch(menuItem.getItemId()) {
                    case R.id.show_statistics:
                        startActivity(new Intent(getApplicationContext(), ActivityOverview.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.add_entry:
                        return true;
                }
                return false;
            }
        });
    }

    private void makeToast(String text) {
        Toast toast = Toast.makeText(mContext, text, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    private void resetSharedPreferences() {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.clear();
        editor.commit();
    }

    private void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = activity.getCurrentFocus();
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}