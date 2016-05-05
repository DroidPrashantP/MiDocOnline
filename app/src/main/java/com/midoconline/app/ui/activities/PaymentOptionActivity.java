package com.midoconline.app.ui.activities;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.midoconline.app.R;
import com.midoconline.app.Util.Constants;
import com.midoconline.app.Util.SharePreferences;
import com.midoconline.app.Util.StringUtils;
import com.midoconline.app.Util.Utils;
import com.midoconline.app.services.IncomeCallListenerService;
import com.midoconline.app.ui.activities.util.ErrorDialogFragment;
import com.stripe.android.Stripe;
import com.stripe.android.TokenCallback;
import com.stripe.android.model.Card;
import com.stripe.android.model.Token;

import java.util.Arrays;

public class PaymentOptionActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String TAG = PaymentOptionActivity.class.getName();
    private EditText mEdtCardNumber;
    private EditText mEdtCVV;
    private Button mBtnPay;
    private RelativeLayout mainContainer;
    public SharePreferences mSharePreferences;
    private String stripTokenID;
    private Spinner mMonthSpinner;
    private Spinner mYearSpinner;
    private boolean isFromHistory = false;
    private String opponantHistoryEmail;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );
        setContentView(R.layout.activity_payment_option);
        mSharePreferences = new SharePreferences(this);
        Bundle bundle = getIntent().getExtras();
        if (bundle!=null) {
            isFromHistory = bundle.getBoolean("isFromHistory");
            opponantHistoryEmail = bundle.getString("opponentEmail");
        }
        initiView();
        setToolbar();
    }

    private void initiView() {
        mainContainer = (RelativeLayout) findViewById(R.id.mainContainer);
        mEdtCardNumber = (EditText) findViewById(R.id.edt_cardnumber);
        mEdtCVV = (EditText) findViewById(R.id.edt_cvv);
        mMonthSpinner = (Spinner) findViewById(R.id.expMonth);
        mYearSpinner = (Spinner) findViewById(R.id.expYear);

        mMonthSpinner.setAdapter(new MyAdapter(this, R.layout.custom_spinner_view, getResources().getStringArray(R.array.month_array)));
        mYearSpinner.setAdapter(new MyAdapter(this, R.layout.custom_spinner_view, getResources().getStringArray(R.array.year_array)));

        mBtnPay = (Button) findViewById(R.id.btn_pay);
        mBtnPay.setOnClickListener(this);
        mainContainer.setBackgroundResource(R.drawable.bg);

        if (StringUtils.isNotEmpty(mSharePreferences.getCardNumber())){
            mEdtCardNumber.setText(""+mSharePreferences.getCardNumber());
        }
        if (mSharePreferences.getCardExpiryMonth()!= 0){
            mMonthSpinner.setSelection(Arrays.asList(getResources().getStringArray(R.array.month_array)).indexOf(mSharePreferences.getCardExpiryMonth()));
        }
        if (mSharePreferences.getcardExpiryYear()!= 0 ){
            mYearSpinner.setSelection(Arrays.asList(getResources().getStringArray(R.array.year_array)).indexOf(mSharePreferences.getcardExpiryYear()));
        }
        if (mSharePreferences.getcardExpiryCvv() != 0){
            mEdtCVV.setText(""+mSharePreferences.getcardExpiryCvv());
        }

    }

    public void saveCreditCard() {
        if (!mEdtCardNumber.getText().toString().substring(0,2).equalsIgnoreCase("37")) {

        final int ExpMonth = getInteger(mMonthSpinner);
        final int ExpYear = getInteger(mYearSpinner);


        Card card = new Card(
                mEdtCardNumber.getText().toString(),
                ExpMonth,
                ExpYear,
                mEdtCVV.getText().toString());

        boolean validation = card.validateCard();
        if (validation) {
                startProgress();
                new Stripe().createToken(
                        card,
                        Constants.PUBLISHABLE_KEY,
                        new TokenCallback() {
                            public void onSuccess(Token token) {
                                String endingIn = getResources().getString(R.string.endingIn);
                                stripTokenID = token.getId();
                                Log.d("Card number", stripTokenID);
                                mSharePreferences.setStripKey(stripTokenID);
                                mSharePreferences.setCardExpiryMonth(ExpMonth);
                                mSharePreferences.setcardExpiryYear(ExpYear);
                                mSharePreferences.setCardExpiryMonth(Integer.parseInt(mEdtCVV.getText().toString()));
                                mSharePreferences.setCardNumber(mEdtCardNumber.getText().toString());

                                ExecutePaymentRequest();
                            }

                            public void onError(Exception error) {
                                handleError(error.getLocalizedMessage());
                                finishProgress();
                            }
                        });
        } else if (!card.validateNumber()) {
            handleError("The card number that you entered is invalid");
        } else if (!card.validateExpiryDate()) {
            handleError("The expiration date that you entered is invalid");
        } else if (!card.validateCVC()) {
            handleError("The CVC code that you entered is invalid");
        } else {
            handleError("The card details that you entered are invalid");
        }
        }else {
            handleError("Sorry, we do not accept American Express Cards.");
        }
    }

    @Override
    public void onClick(View v) {

        if (v == mBtnPay) {
            saveCreditCard();
        }

    }

    public void startIncomeCallListenerService(String login, String password, int startServiceVariant) {
        Intent tempIntent = new Intent(this, IncomeCallListenerService.class);
        PendingIntent pendingIntent = createPendingResult(Constants.LOGIN_TASK_CODE, tempIntent, 0);
        Intent intent = new Intent(this, IncomeCallListenerService.class);
        intent.putExtra(Constants.USER_LOGIN, login);
        intent.putExtra(Constants.USER_PASSWORD, password);
        intent.putExtra(Constants.START_SERVICE_VARIANT, startServiceVariant);
        intent.putExtra(Constants.PARAM_PINTENT, pendingIntent);
        intent.putExtra(Constants.BundleKeys.CALL_TYPE, Constants.BundleKeys.NORMAL_CALL);
        startService(intent);
    }

    public void ExecutePaymentRequest() {
        Utils.closeProgress();
        if (isFromHistory){
            Intent intent = new Intent(PaymentOptionActivity.this, OpponentsActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("opponentEmail", opponantHistoryEmail);
            intent.putExtra("isFromHistory", true);
            startActivity(intent);
            finish();
        }else {
            String login = mSharePreferences.getQBEmail();
            String password = "password";
            startIncomeCallListenerService(login, password, Constants.LOGIN);
            finish();
        }
    }

    private void startProgress() {
        Utils.showProgress(this);
    }

    private void finishProgress() {
        Utils.closeProgress();
    }

    private void handleError(String error) {
        DialogFragment fragment = ErrorDialogFragment.newInstance(R.string.validationErrors, error);
        fragment.show(getSupportFragmentManager(), "error");
    }

    private Integer getInteger(Spinner spinner) {
        try {
            return Integer.parseInt(spinner.getSelectedItem().toString());
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    public void setToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        setSupportActionBar(toolbar);
        final ActionBar ab = getSupportActionBar();
        ImageView nav_drawer = (ImageView) findViewById(R.id.nav_drawer);
        nav_drawer.setImageResource(R.drawable.back_arrow);
        nav_drawer.setColorFilter(getResources().getColor(R.color.white), android.graphics.PorterDuff.Mode.MULTIPLY);
        nav_drawer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    public class MyAdapter extends ArrayAdapter<String> {
        private String[] spinnervalue;

        public MyAdapter(Context ctx, int txtViewResourceId, String[] objects) {
            super(ctx, txtViewResourceId, objects);
            this.spinnervalue = objects;
        }

        @Override
        public View getDropDownView(int position, View cnvtView, ViewGroup prnt) {
            return getCustomView(position, cnvtView, prnt);
        }

        @Override
        public View getView(int pos, View cnvtView, ViewGroup prnt) {
            return getCustomView(pos, cnvtView, prnt);
        }

        public View getCustomView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = getLayoutInflater();
            View mySpinner = inflater.inflate(R.layout.custom_spinner_view, parent, false);
            TextView main_text = (TextView) mySpinner.findViewById(R.id.speciality_textView);
            main_text.setText(spinnervalue[position]);
            return mySpinner;
        }
    }
}
