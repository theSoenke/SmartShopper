package com.cyberland.felix.truerestclient;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity
{
    Button button;
    String serverResponse;
    EditText editTextServerResponse;
    String hash;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button = (Button) findViewById(R.id.button);
        editTextServerResponse = (EditText) findViewById(R.id.edittext_serverresponse);


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {

                try
                {

                    //hier bleibt er irgendwie haengen
                    // serverResponse = ProductSync.getLists();

                    hash = Base64.encodeToString("vsis:password".getBytes(), Base64.NO_WRAP);
                    new ListSync().execute("http://api.tecfuture.de:3000/lists",hash);

                    Toast.makeText(MainActivity.this,"Hier komme ich nicht hin",Toast.LENGTH_LONG).show();

                    Toast.makeText(MainActivity.this,serverResponse,Toast.LENGTH_LONG).show();
                        editTextServerResponse.setText(serverResponse);


                }
                catch (Exception e)
                {


                }

            }
        });
    }
}
