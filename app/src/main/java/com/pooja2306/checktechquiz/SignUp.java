package com.pooja2306.checktechquiz;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class SignUp extends Activity {

    String line=null;
    String result=null;
    InputStream is=null;
    EditText etFirst, etLast, etPass, etCPass, etEmail, etContact;
    Button btnReg;
    String f_name, l_name, pass, c_pass, email, c_no;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        etFirst = (EditText) findViewById(R.id.etFirst);
        etLast = (EditText) findViewById(R.id.etLast);
        etEmail = (EditText) findViewById(R.id.etEmail);
        etPass = (EditText) findViewById(R.id.etPass);
        etCPass = (EditText) findViewById(R.id.etCPass);
        etContact = (EditText) findViewById(R.id.etContact);
        btnReg = (Button) findViewById(R.id.btnReg);

        btnReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                f_name = etFirst.getText().toString();
                l_name = etLast.getText().toString();
                pass = etPass.getText().toString();
                c_pass = etCPass.getText().toString();
                email = etEmail.getText().toString();
                c_no = etContact.getText().toString();

                if(f_name.length()>0)
                {
                    etFirst.setError(null);
                    if(l_name.length()>0)
                    {
                        etLast.setError(null);
                        if(pass.length()>0)
                        {
                            etPass.setError(null);
                            if(c_pass.length()>0)
                            {
                                etCPass.setError(null);
                                if(pass.equals(c_pass))
                                {
                                    if(c_no.length()!=0)
                                    {
                                        etContact.setError(null);
                                        if(c_no.length()==10)
                                        {
                                            etContact.setError(null);
                                            new Register().execute("");
                                        } else
                                        {
                                            etContact.setFocusable(true);
                                            etContact.setError("Invalid Number");
                                        }

                                    }else
                                    {
                                        etContact.setFocusable(true);
                                        etContact.setError("Required Field");
                                    }
                                }else
                                {
                                    etCPass.setFocusable(true);
                                    etCPass.setError("Passwords do not match");
                                }
                            }else
                            {
                                etCPass.setFocusable(true);
                                etCPass.setError("Required Field");
                            }
                        }else
                        {
                            etPass.setFocusable(true);
                            etPass.setError("Required Field");
                        }
                    }else
                    {
                        etLast.setFocusable(true);
                        etLast.setError("Required Field");
                    }

                }else
                {
                    etFirst.setFocusable(true);
                    etFirst.setError("Required Field");
                }
            }
        });
    }

    class Register extends AsyncTask<String, String, String>
    {
        @Override
        protected String doInBackground(String... params) {
            ArrayList<NameValuePair> values = new ArrayList<NameValuePair>();
            values.add(new BasicNameValuePair("f_name", f_name));
            values.add(new BasicNameValuePair("l_name", l_name));
            values.add(new BasicNameValuePair("email", email));
            values.add(new BasicNameValuePair("pass", pass));
            values.add(new BasicNameValuePair("c_no", c_no));

            try
            {
                DefaultHttpClient httpClient = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost(Config.ip+"Register.php");
                httpPost.setEntity(new UrlEncodedFormEntity(values));
                HttpResponse Response = httpClient.execute(httpPost);
                HttpEntity entity = Response.getEntity();
                is = entity.getContent();
                Log.i("Tag", "Connection successful");
            }
            catch(Exception e)
            {
                Log.i("Tag", "Connection Failed");
            }
            try
            {
                BufferedReader reader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"), 8);
                StringBuilder sb = new StringBuilder();
                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }
                is.close();
                result = sb.toString();
                Log.i("TAG", "result retrieved " + result);

            }
            catch(Exception e)
            {
                Log.i("Tag", "result not retrieved" + e.toString());
            }
            try
            {
                JSONObject obj = new JSONObject(result);
                if(obj.getString("status").equalsIgnoreCase("true"))
                {
                    Intent i = new Intent(getApplication(),MainActivity.class);
                    startActivity(i);
                }
            }
            catch(Exception e)
            {
                Log.i("Tag", e.toString());
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
        }
    }
}
