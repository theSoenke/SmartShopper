package com.cyberland.felix.truerestclient;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements AsyncResponse
{
    Button buttonGetLists;
    Button buttonGetProducts;

    String serverResponse;
    EditText editTextServerResponse;
    String hash;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buttonGetLists = (Button) findViewById(R.id.buttonGetLists);
        buttonGetProducts = (Button) findViewById(R.id.buttonGetProducts);
        editTextServerResponse = (EditText) findViewById(R.id.edittext_serverresponse);


        buttonGetLists.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {

                try
                {

                    //hier bleibt er irgendwie haengen
                    // serverResponse = ProductSync.getLists();

                    hash = Base64.encodeToString("felix:test".getBytes(), Base64.NO_WRAP);
                    ListSync listsync = new ListSync();
                    listsync.delegate = MainActivity.this;
                    listsync.execute("http://api.tecfuture.de:3000/lists",hash);

                }
                catch (Exception e)
                {


                }

            }
        });

        buttonGetProducts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try
                {
                    hash = Base64.encodeToString("felix:test".getBytes(), Base64.NO_WRAP);
                    ProductSync productSync = new ProductSync();
                    productSync.delegate = MainActivity.this;
                    productSync.execute("http://api.tecfuture.de:3000/products",hash);

                }
                catch (Exception e)
                {

                }
            }
        });
    }

    public void processFinish(String output)
    {
        Toast.makeText(MainActivity.this,output,Toast.LENGTH_LONG).show();
        editTextServerResponse.setText(serverResponse);
    }
}
