package com.pooja2306.checktechquiz;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.message.BufferedHeader;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends Activity {

    TextView signup;
    EditText etUser, etPwd;
    Button btnLogin;
    String uname, pwd;
    String line=null;
    String result=null;
    InputStream is=null;
    String id,w_money,emi,status,msg;
    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        signup = (TextView) findViewById(R.id.txtSignup);
        etUser = (EditText) findViewById(R.id.etUser);
        etPwd = (EditText) findViewById(R.id.etPwd);
        btnLogin = (Button) findViewById(R.id.btnLogin);
        dialog = new ProgressDialog(MainActivity.this);

        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
        String restoredText = pref.getString("login", null);

        if (restoredText != null) {
            if (restoredText.equals("yes")) {
                Intent myIntent = new Intent(MainActivity.this, Home.class);
                MainActivity.this.startActivity(myIntent);
                finish();
            }
        }

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uname = etUser.getText().toString();
                pwd = etPwd.getText().toString();

                if(uname.length()>0)
                {
                   etUser.setError(null);
                    if(pwd.length()>0)
                    {
                        etPwd.setError(null);
                        if(emailValidator(uname))
                        {
                            etUser.setError(null);
                            new Login().execute("");
                        }
                        else
                        {
                            etUser.setFocusable(true);
                            etUser.setError("Invalid Email.");
                        }
                    }
                    else
                    {
                        etPwd.setFocusable(true);
                        etPwd.setError("Required Field");
                    }
                }
                else
                {
                    etUser.setFocusable(true);
                    etUser.setError("Required Field");
                }
              //  Toast.makeText(MainActivity.this, "email"+uname+" "+pwd, Toast.LENGTH_LONG).show();
            }

        });

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplication(), SignUp.class);
                startActivity(i);
            }
        });
    }

        public boolean emailValidator(String email)
    {
        Pattern pattern;
        Matcher matcher;
        final String EMAIL_PATTERN = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        pattern = Pattern.compile(EMAIL_PATTERN);
        matcher = pattern.matcher(email);
        return matcher.matches();
    }

    class Login extends AsyncTask<String, String, String>{
        @Override
        protected String doInBackground(String... params) {
            ArrayList<NameValuePair> values = new ArrayList<NameValuePair>();
            values.add(new BasicNameValuePair("email", uname));
            values.add(new BasicNameValuePair("pass", pwd));

            try
            {
                DefaultHttpClient httpClient = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost(Config.ip+"Login.php");
                httpPost.setEntity(new UrlEncodedFormEntity(values));
                HttpResponse Response = httpClient.execute(httpPost);
                HttpEntity entity = Response.getEntity();
                is = entity.getContent();
                Log.i("Tag", "Connection successful");

            }
            catch(Exception e)
            {
                Log.i("MyTag", "Connection Failed");
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
                Log.i("MyTag", "Result not retrieved" + e.toString());
            }

            try
            {
                JSONObject obj = new JSONObject(result);

            if(obj.getString("status").equalsIgnoreCase("true"))
                {

                    id = obj.getString("id");
                    emi= obj.getString("email");
                    w_money = obj.getString("wallet");


                    SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
                    SharedPreferences.Editor editor = pref.edit();

                    editor.putString("login","yes");
                    editor.putString("id",id);
                    editor.putString("email", emi);
                    editor.putString("wallet",w_money);
                    editor.commit();

                    Intent i = new Intent(MainActivity.this, Home.class);
                    startActivity(i);
                    finish();
                }
                else
                {
                    status = obj.getString("status");
                    msg = obj.getString("msg");

                    Log.i("MyTag", status +", "+msg);
                }

            }
            catch(Exception e)
            {
                Log.i("MyTag", id+" "+emi+" "+w_money+e.toString());
            }

            return null;
        }

        @Override
        protected void onPreExecute() {
            dialog.setMessage("Please wait...");
            dialog.show();
        }

        @Override
        protected void onPostExecute(String s) {
            if(dialog.isShowing()){
                dialog.dismiss();
            }
        }
    }
}
