package com.netforceinfotech.myinapptest;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.TransactionDetails;
import com.netforceinfotech.myinapptest.Debugger.Debugger;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, BillingProcessor.IBillingHandler {

    Button buttonBuyActive, buttonSubscribe;
    TextView textViewState, textViewScribed;
    BillingProcessor bp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initview();
    }

    private void initview() {
        buttonBuyActive = (Button) findViewById(R.id.buttonBuy);
        buttonSubscribe = (Button) findViewById(R.id.buttonSubscrib);
        textViewScribed = (TextView) findViewById(R.id.textViewScribed);
        textViewState = (TextView) findViewById(R.id.textViewState);
        buttonBuyActive.setOnClickListener(this);
        buttonSubscribe.setOnClickListener(this);
        boolean isAvailable = BillingProcessor.isIabServiceAvailable(getApplicationContext());
        if (!isAvailable) {
            // continue
            showMessage("Billing process not availalbe");
            return;
        }
        bp = new BillingProcessor(this, getString(R.string.in_app_key), this);
    }

    private void showMessage(String s) {
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.buttonBuy:
                if (buttonBuyActive.getText().toString().equalsIgnoreCase("consume")) {
                    buttonBuyActive.setText("Buy Active");
                } else {
                    bp.purchase(MainActivity.this, getString(R.string.in_app_key));
                    bp.consumePurchase(getString(R.string.in_app_key));
                }
                break;
            case R.id.buttonSubscrib:
                break;
        }
    }

    @Override
    public void onProductPurchased(String productId, TransactionDetails details) {
        Debugger.i("kresult", details.toString());
      if (productId.equalsIgnoreCase("buy_active")) {
            buttonBuyActive.setText("Consume");
        } else if (productId.equalsIgnoreCase("subscribed")) {
            textViewState.setText("lazy");
            textViewScribed.setText("Yes");
        }
    }

    @Override
    public void onPurchaseHistoryRestored() {

    }

    @Override
    public void onBillingError(int errorCode, Throwable error) {
        showMessage("error code " + errorCode);
    }

    @Override
    public void onBillingInitialized() {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!bp.handleActivityResult(requestCode, resultCode, data))
            super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onDestroy() {
        if (bp != null)
            bp.release();

        super.onDestroy();
    }
}
