package com.example.rajkumar.medione;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class login extends AppCompatActivity
{
    EditText email,pass;
    String username,password;
    public static final String JSON_ARRAY = "result";
    public static final String USER_NAME = "USER_NAME";
    public static final String PASSWORD = "PASSWORD";
    private  static  final String login_url = "http://medione.esy.es/retailer_login.php";


    public static final String MyPREFERENCES = "MyPrefs" ;


    JSONArray peoples = null;

    ArrayList<HashMap<String, String>> personList;
    private boolean loggedIn = false;

    SharedPreferences sharedpreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        email = (EditText) findViewById(R.id.email);
        pass = (EditText) findViewById(R.id.pass);

        //to set title ate center
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.actionbar);


        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences sharedPreferences = getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        String USER_NAME1 = sharedPreferences.getString(Config.EMAIL_SHARED_PREF, "Not Available");
        Log.e("login",USER_NAME1);
        Log.e("login cnt", String.valueOf(USER_NAME1.length()));
        if(!USER_NAME1.equals("Not Available"))
        {
            Log.e("inside",USER_NAME1);
            Intent intent=new Intent(login.this,MainActivity.class);
            startActivity(intent);
            finish();
        }

    }

    public void log(View v)
    {
         username= email.getText().toString().trim();
         password= pass.getText().toString().trim();

        if(validate())
        {
            userlogin(username, password);
        }

    }

    private boolean validate()
    {
        if(username.length()==0)
        {
            email.setError("email cannot be blank");
            return false;
        }
        if(password.length()==0)
        {
            pass.setError("password cannot be blank");
            return false;
        }
        return  true;
    }


    private void userlogin(final String username, final String password)
    {
        class UserLoginClass extends AsyncTask<String,Void,String>
        {
            ProgressDialog loading;
            @Override
            protected void onPreExecute()
            {
                super.onPreExecute();
                loading = ProgressDialog.show(login.this,"Please Wait",null,true,true);
                loading.setCanceledOnTouchOutside(false);
            }


            @Override
            protected String doInBackground(String... params)
            {
                HashMap<String,String> data = new HashMap<>();
                data.put("email",params[0]);
                data.put("pass",params[1]);

                RegisterUserClass ruc = new RegisterUserClass();

                String result = ruc.sendPostRequest(login_url,data);
                Log.e("result",result);
                return result.trim();
            }

            @Override
            protected void onPostExecute(String s)
            {
                super.onPostExecute(s);
                loading.dismiss();
                Log.e("error login",s);
                if(s.equals("upload your documents on the website"))
                {

                    Toast.makeText(login.this,"send your documents to  arialstarraj@gmail.com for verification ",Toast.LENGTH_LONG).show();

                }
                else if(s.length()==0)
                {
                    Toast.makeText(login.this,"netwrok is too slow",Toast.LENGTH_LONG).show();
                }
                else if(s.equals("Invalid Username or Password"))
                {
                    Toast.makeText(login.this,s,Toast.LENGTH_LONG).show();
                }
                else if(s.equals("error try again"))
                {
                    Toast.makeText(login.this,s,Toast.LENGTH_LONG).show();
                }
                else
                {
                    try
                    {
                        JSONObject jsonObj = new JSONObject(s);
                        peoples = jsonObj.getJSONArray(JSON_ARRAY);
                        String name="",PASSshar="",phone="",address="",email1="",status,tin_no;

                        for (int i = 0; i < peoples.length(); i++)
                        {
                            JSONObject c = peoples.getJSONObject(i);
                            name = c.getString("name");
                            PASSshar = c.getString("pass");
                            address = c.getString("address");
                            phone = c.getString("phone");
                            email1 = c.getString("email");
                            status = c.getString("status");

                        }


                        Log.e("name",name);
                        Log.e("name",PASSshar);
                        Log.e("name",address);
                        Log.e("name",phone);
                        Log.e("name",username);


                        SharedPreferences sharedPreferences = login.this.getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE);

                        //Creating editor to store values to shared preferences
                        SharedPreferences.Editor editor = sharedPreferences.edit();

                        //Adding values to editor
                       // editor.putBoolean(Config.LOGGEDIN_SHARED_PREF, true);
                        editor.putString(Config.EMAIL_SHARED_PREF, username);
                        editor.putString(Config.PASS_SHARED_PREF, PASSshar);
                        editor.putString(Config.NAME_SHARED_PREF, name);
                        editor.putString(Config.ADDRESS_SHARED_PREF, address);
                        editor.putString(Config.PHONE_SHARED_PREF, phone);

                        //Saving values to editor
                        editor.commit();


                        Intent intent = new Intent(login.this,MainActivity.class);
                        startActivity(intent);
                        finish();

                    }



                    catch (JSONException e)
                    {
                        e.printStackTrace();
                    }

                }
            }
        }
        UserLoginClass ulc = new UserLoginClass();
        ulc.execute(username,password);

    }






    public void register(View v)
    {
        Intent i=new Intent();
        i.setClass(this,register.class);
        startActivity(i);
        finish();
    }
}
