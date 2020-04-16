package com.example.pay_upi;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    TextInputEditText name,amount,upi,note,id,refid;
    MaterialButton pay;
    Uri uri;
    final int PAY_REQUEST=1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        name=findViewById(R.id.et_name);
        amount=findViewById(R.id.et_amount);
        upi=findViewById(R.id.et_upi);
        note=findViewById(R.id.et_note);
        pay=findViewById(R.id.bt_pay);
        id=findViewById(R.id.et_id);
        refid=findViewById(R.id.et_refid);

        pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(name.getText()) ||
                        TextUtils.isEmpty(amount.getText()) ||
                        TextUtils.isEmpty(note.getText()) ||
                        TextUtils.isEmpty(upi.getText()))
                {
                    Toast.makeText(MainActivity.this, "Plz fill all the details", Toast.LENGTH_SHORT).show();
                }
                else if(Integer.parseInt(amount.getText().toString().trim())<=0)
                {
                    Toast.makeText(MainActivity.this, "amount is incorrect", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    uri=new Uri.Builder()
                            .scheme("upi")
                            .authority("pay")
                            .appendQueryParameter("pa", upi.getText().toString().trim())
                            .appendQueryParameter("pn", name.getText().toString().trim())
                            .appendQueryParameter("tn", note.getText().toString().trim())
                            .appendQueryParameter("am", amount.getText().toString().trim())
                            .appendQueryParameter("tid",id.getText().toString().trim())
                            .appendQueryParameter("tr",refid.getText().toString().trim())
                            .appendQueryParameter("cu", "INR")
                            .build();

                    Intent upiIntent=new Intent(Intent.ACTION_VIEW);
                    upiIntent.setData(uri);
                    Intent chooser=Intent.createChooser(upiIntent,"Pay ");
//                    upiIntent.setPackage("com.google.android.apps.nbu.paisa.user");
//                    startActivityForResult(upiIntent, 123);
                    if(chooser.resolveActivity(getPackageManager())!=null)
                    {
                        startActivityForResult(chooser,PAY_REQUEST);
                    }
                    else
                    {
                        Toast.makeText(MainActivity.this, "No UPI app exist in your ", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==PAY_REQUEST)
        {

            if(isInternetAvailable(this))
            {

               // if ((RESULT_OK == resultCode) || (resultCode == 11)) {
                    if (data == null) {
                        ArrayList<String> dataList = new ArrayList<>();
                        dataList.add("nothing");
                        String temp="nothing";
                        Toast.makeText(this, "Transaction not completed", Toast.LENGTH_SHORT).show();
                    } else {

                        String text = data.getStringExtra("response");
                        Log.d("UPI", "onActivityResult: " + text);
                        ArrayList<String> dataList = new ArrayList<>();
                        dataList.add(text);
                        upiPaymentCheck(text);
                    }
            }
            else
            {
                Toast.makeText(this, "Plz check your internet connection and try again", Toast.LENGTH_SHORT).show();
            }


        }
    }

    void upiPaymentCheck(String data)
    {
        String str = data;
        //str = txnId=xxxxxxxxx&responseCode=xxx&Status=FAILURE&txnRef=xxxxxxxxxx
        String payment_cancel = "";
        String status = "";
        String response[] = str.split("&");
        for (int i = 0; i < response.length; i++)
        {
            String equalStr[] = response[i].split("=");
            if(equalStr.length >= 2)
            {
                if (equalStr[0].toLowerCase().equals("Status".toLowerCase()))
                {
                    status = equalStr[1].toLowerCase();
                }
            }
            else
            {
                payment_cancel = "Payment cancelled";
            }
        }

        if (status.equals("success")) {
            Toast.makeText(MainActivity.this, "Transaction successful.", Toast.LENGTH_SHORT).show();
        }
        else if("Payment cancelled".equals(payment_cancel)) {
            Toast.makeText(MainActivity.this, "Payment cancelled by user.", Toast.LENGTH_SHORT).show();
        }
        else {
            Toast.makeText(MainActivity.this, "Transaction failed ", Toast.LENGTH_SHORT).show();
        }
    }



    public static boolean isInternetAvailable(Context context) {
        ConnectivityManager connection_Check = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connection_Check != null) {
            NetworkInfo netInfo = connection_Check.getActiveNetworkInfo();
            if (netInfo.isConnected() && netInfo.isConnectedOrConnecting() && netInfo.isAvailable())
            {
                return true;
            }
        }
        return false;
    }
}
