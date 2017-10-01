package com.example.rajkumar.medione;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import static android.R.id.list;
import static com.example.rajkumar.medione.login.USER_NAME;

public class searchm extends Fragment
{

    private ListView listView;
    private ProgressDialog loading;
    private ProgressDialog loading_add;
    private ProgressDialog loading_remove;
    String username;
    private  static  final String retailer_search_url = "http://medione.esy.es/retailer_search.php";
    private  static  final String retailer_add_med = "http://medione.esy.es/retailer_add_med.php";
    private  static  final String retailer_remove_med = "http://medione.esy.es/retailer_remove_med.php";
    public static final String KEY_NAME = "name";
    public static final String KEY_MED = "med";
    public static final String KEY_EMAIL = "email";
    public static final String KEY_PRICE = "price";
    public static final String KEY_ID = "medid";
    public static final String KEY_AVAIL = "avail";
    public static final String KEY_NOTAVAIL = "notavail";
    public static final String KEY_BOOK = "book";
    public static final String JSON_ARRAY = "result";
    public static final String[] li={"NO MEDICINES FOUND PLEASE GO BACK AND SEARCH AGAIN"};
    String USER_NAME;
    String medidd;

    JSONArray peoples = null;

    ArrayList<HashMap<String, String>> personList;



    View view;
    String medname;
    private Boolean exit = false;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        view=inflater.inflate(R.layout.activity_searchm,container,false);
        listView=(ListView)view.findViewById(R.id.listView);
        personList = new ArrayList<HashMap<String,String>>();

        getData();

        return view;

    }




    private void getData()
    {

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        USER_NAME  = sharedPreferences.getString(Config.EMAIL_SHARED_PREF,"Not Available");
        Log.e("USERNAME",USER_NAME);
        loading = ProgressDialog.show(getActivity(), "Please Wait..Medicines are loading", null, true, true);
        loading.setCanceledOnTouchOutside(false);


        medname = getArguments().getString("medname");
        username = getArguments().getString("USER_NAME");
        Log.e("username",username);





        StringRequest stringRequest = new StringRequest(Request.Method.POST, retailer_search_url,new Response.Listener<String>()
        {
            @Override
            public void onResponse(String response)
            {
                //loading.dismiss();

                showJSON(response);
            }
        },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error)
                    {
                        String message = null;
                        if (error instanceof NetworkError)
                        {
                            message = "Cannot connect to Internet...Please check your connection!";
                            Toast.makeText(getActivity().getApplicationContext(),message,Toast.LENGTH_LONG).show();
                        }
                        else if (error instanceof ServerError)
                        {
                            message = "The server could not be found. Please try again after some time!!";
                            Toast.makeText(getActivity().getApplicationContext(),message,Toast.LENGTH_LONG).show();
                        }
                        else if (error instanceof AuthFailureError)
                        {
                            message = "Cannot connect to Internet...Please check your connection!";
                            Toast.makeText(getActivity().getApplicationContext(),message,Toast.LENGTH_LONG).show();
                        }
                        else if (error instanceof ParseError)
                        {
                            message = "Parsing error! Please try again after some time!!";
                            Toast.makeText(getActivity().getApplicationContext(),message,Toast.LENGTH_LONG).show();
                        }
                        else if (error instanceof NoConnectionError)
                        {
                            message = "Cannot connect to Internet...Please check your connection!";
                            Toast.makeText(getActivity().getApplicationContext(),message,Toast.LENGTH_LONG).show();
                        }
                        else if (error instanceof TimeoutError)
                        {
                            message = "Connection TimeOut! Please check your internet connection.";
                            Toast.makeText(getActivity().getApplicationContext(),message,Toast.LENGTH_LONG).show();
                        }
                        else
                        {
                            message = "Connection TimeOut! Please check your internet connection.";
                            Toast.makeText(getActivity().getApplicationContext(),message,Toast.LENGTH_LONG).show();
                        }
                    }
                }){
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            protected Map<String,String> getParams()
            {
                Map<String,String> params = new HashMap<String, String>();
                params.put(KEY_MED,medname);
                params.put(KEY_EMAIL,username);
                return params;
            }

        };

        RequestQueue requestQueue = Volley.newRequestQueue(getActivity().getApplicationContext());
        requestQueue.add(stringRequest);
    }

    private void showJSON(String response)
    {
        try
        {
            loading.dismiss();
            Log.e("response from serch",response);
            if(response.equals("error"))
            {

                mo_med_found ldf = new mo_med_found ();
                Bundle args = new Bundle();
                args.putString("retailer", "1");
                args.putString("medname", medname);
                ldf.setArguments(args);

                android.app.FragmentManager fragmentManager=getFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.content_frame,ldf).addToBackStack("fragBack").commit();

            }
            else
            {
                JSONObject jsonObj = new JSONObject(response);
                peoples = jsonObj.getJSONArray(JSON_ARRAY);

                for (int i = 0; i < peoples.length(); i++)
                {
                    JSONObject c = peoples.getJSONObject(i);
                    String name = c.getString(KEY_NAME);
                    String price = c.getString(KEY_PRICE);
                    String medid = c.getString(KEY_ID);
                    price="₹"+price;
                    //cntr+alt+4 for indian rupee
                    HashMap<String, String> persons = new HashMap<String, String>();



                        persons.put(KEY_NAME, name);
                        persons.put(KEY_PRICE, price);
                        persons.put(KEY_ID, medid);
                        personList.add(persons);

                }

                ListAdapter adapter = new SimpleAdapter(getActivity().getApplicationContext(), personList, R.layout.list_view_layout,
                        new String[]{KEY_ID,KEY_NAME,KEY_PRICE},
                        new int[]{R.id.medid,R.id.textViewname, R.id.textViewprice})
                {
                    public View getView(int position, View convertView, ViewGroup parent)
                    {


                        // get filled view from SimpleAdapter
                        View itemView=super.getView(position, convertView, parent);
                        // find our button there
                        View avail=itemView.findViewById(R.id.textView_avail);

                        View notavail=itemView.findViewById(R.id.textView_notavail);



                        final TextView textView = ((TextView) itemView.findViewById(R.id.medid));


                        avail.setOnClickListener(new View.OnClickListener()
                        {
                            @Override
                            public void onClick(View v)
                            {

//                                View parentView = (View) view.getParent();
//                                //TextView textView = ((TextView) v.findViewById(R.id.textVieworderid));
//                                String oid=textView.getText().toString();
//                                Log.e("text",oid);
//
//                                ImageButton imageButton=(ImageButton)v.findViewById(R.id.cancle);
//                                imageButton.getResources();
//                                if(imageButton.getDrawable().getConstantState().equals(getActivity().getApplicationContext().getResources().getDrawable(R.drawable.cancel).getConstantState()))
//                                {
//                                    String c="cancle";
//                                    Log.e("ca",c);
//                                    cnc(c,USER_NAME,oid);
//
//                                }
                                medidd=textView.getText().toString();
                                Log.e("response remove medid",medidd);
                                add_med();



                            }
                        });

                        notavail.setOnClickListener(new View.OnClickListener()
                        {
                            @Override
                            public void onClick(View v)
                            {
                                medidd=textView.getText().toString();
                                Log.e("medid",medidd);
                                remove_med();
                            }
                        });


                        return itemView;
                    }

                };


                listView.setAdapter(adapter);

            }

        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }


    }

    private void remove_med()
    {
        loading_remove = ProgressDialog.show(getActivity(), "Please Wait..Removing Medicine from list", null, true, true);
        loading_remove.setCanceledOnTouchOutside(false);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, retailer_remove_med,new Response.Listener<String>()
        {
            @Override
            public void onResponse(String response)
            {
                //loading.dismiss();

                Log.e("response from remove ",response);
                Toast.makeText(getActivity().getApplicationContext(),response,Toast.LENGTH_LONG).show();
                loading_remove.dismiss();
            }
        },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error)
                    {
                        String message = null;
                        if (error instanceof NetworkError)
                        {
                            message = "Cannot connect to Internet...Please check your connection!";
                            Toast.makeText(getActivity().getApplicationContext(),message,Toast.LENGTH_LONG).show();
                        }
                        else if (error instanceof ServerError)
                        {
                            message = "The server could not be found. Please try again after some time!!";
                            Toast.makeText(getActivity().getApplicationContext(),message,Toast.LENGTH_LONG).show();
                        }
                        else if (error instanceof AuthFailureError)
                        {
                            message = "Cannot connect to Internet...Please check your connection!";
                            Toast.makeText(getActivity().getApplicationContext(),message,Toast.LENGTH_LONG).show();
                        }
                        else if (error instanceof ParseError)
                        {
                            message = "Parsing error! Please try again after some time!!";
                            Toast.makeText(getActivity().getApplicationContext(),message,Toast.LENGTH_LONG).show();
                        }
                        else if (error instanceof NoConnectionError)
                        {
                            message = "Cannot connect to Internet...Please check your connection!";
                            Toast.makeText(getActivity().getApplicationContext(),message,Toast.LENGTH_LONG).show();
                        }
                        else if (error instanceof TimeoutError)
                        {
                            message = "Connection TimeOut! Please check your internet connection.";
                            Toast.makeText(getActivity().getApplicationContext(),message,Toast.LENGTH_LONG).show();
                        }
                        else
                        {
                            message = "Connection TimeOut! Please check your internet connection.";
                            Toast.makeText(getActivity().getApplicationContext(),message,Toast.LENGTH_LONG).show();
                        }
                    }
                }){
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            protected Map<String,String> getParams()
            {
                Map<String,String> params = new HashMap<String, String>();
                params.put("medid",medidd);
                params.put(KEY_EMAIL,username);
                return params;
            }

        };

        RequestQueue requestQueue = Volley.newRequestQueue(getActivity().getApplicationContext());
        requestQueue.add(stringRequest);
    }

    private void add_med()
    {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        USER_NAME  = sharedPreferences.getString(Config.EMAIL_SHARED_PREF,"Not Available");
        Log.e("USERNAME",USER_NAME);
        loading = ProgressDialog.show(getActivity(), "Please Wait..Adding medicine to list", null, true, true);
        loading.setCanceledOnTouchOutside(false);



        medname = getArguments().getString("medname");
        username = getArguments().getString("USER_NAME");
        Log.e("username",username);





        StringRequest stringRequest = new StringRequest(Request.Method.POST, retailer_add_med,new Response.Listener<String>()
        {
            @Override
            public void onResponse(String response)
            {
                //loading.dismiss();

                Log.e("response from add",response);
                Toast.makeText(getActivity().getApplicationContext(),response,Toast.LENGTH_LONG).show();
                loading.dismiss();
            }
        },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error)
                    {
                        String message = null;
                        if (error instanceof NetworkError)
                        {
                            message = "Cannot connect to Internet...Please check your connection!";
                            Toast.makeText(getActivity().getApplicationContext(),message,Toast.LENGTH_LONG).show();
                        }
                        else if (error instanceof ServerError)
                        {
                            message = "The server could not be found. Please try again after some time!!";
                            Toast.makeText(getActivity().getApplicationContext(),message,Toast.LENGTH_LONG).show();
                        }
                        else if (error instanceof AuthFailureError)
                        {
                            message = "Cannot connect to Internet...Please check your connection!";
                            Toast.makeText(getActivity().getApplicationContext(),message,Toast.LENGTH_LONG).show();
                        }
                        else if (error instanceof ParseError)
                        {
                            message = "Parsing error! Please try again after some time!!";
                            Toast.makeText(getActivity().getApplicationContext(),message,Toast.LENGTH_LONG).show();
                        }
                        else if (error instanceof NoConnectionError)
                        {
                            message = "Cannot connect to Internet...Please check your connection!";
                            Toast.makeText(getActivity().getApplicationContext(),message,Toast.LENGTH_LONG).show();
                        }
                        else if (error instanceof TimeoutError)
                        {
                            message = "Connection TimeOut! Please check your internet connection.";
                            Toast.makeText(getActivity().getApplicationContext(),message,Toast.LENGTH_LONG).show();
                        }
                        else
                        {
                            message = "Connection TimeOut! Please check your internet connection.";
                            Toast.makeText(getActivity().getApplicationContext(),message,Toast.LENGTH_LONG).show();
                        }
                    }
                }){
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            protected Map<String,String> getParams()
            {
                Map<String,String> params = new HashMap<String, String>();
                params.put("medid",medidd);
                params.put(KEY_EMAIL,username);
                return params;
            }

        };

        RequestQueue requestQueue = Volley.newRequestQueue(getActivity().getApplicationContext());
        requestQueue.add(stringRequest);

    }


    public void onBackPressed()
    {
        if (exit)
        {
            onBackPressed();
            return;
        }
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();


    }
}
