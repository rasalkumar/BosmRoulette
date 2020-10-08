package info.androidhive.bosmroulette.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import info.androidhive.bosmroulette.R;
import info.androidhive.bosmroulette.app.AppController;
import info.androidhive.bosmroulette.helper.SQLiteHandler;
import info.androidhive.bosmroulette.helper.SessionManager;

/**
 * Created by the master mind Mr.Shivam Gupta on 05/09/2015.
 */
public class LeaderBoard extends ActionBarActivity {
    private ProgressDialog pDialog;
    private Toolbar mtoolbar;
    private String urlJsonArry = "http://doctordhoondo.org/bosm/bosm/getLeaderBoard.php";
    private String jsonResponse;
    private SQLiteHandler db;
    private SessionManager session;
    private static String TAG = LeaderBoard.class.getSimpleName();
    private List<Information> data;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.score_board);

        mtoolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(mtoolbar);
        getSupportActionBar().setTitle("Score Board");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        db = new SQLiteHandler(getApplicationContext());
        pDialog=new ProgressDialog(this);
        pDialog.setMessage("Please wait...");
        pDialog.setCancelable(false);
        // session manager
        session = new SessionManager(getApplicationContext());
        if (!session.isLoggedIn()) {
            logoutUser();
        }
           showpDialog();
        mLayoutManager = new LinearLayoutManager(this);

        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(mLayoutManager);


        // specify an adapter (see also next example)
        mAdapter = new MyAdapter(LeaderBoard.this,makeJsonArrayRequest());

        mRecyclerView.setAdapter(mAdapter);

    }
    private void logoutUser() {
        session.setLogin(false);
        session.setUserId(null);
        db.deleteUsers();

        // Launching the login activity
        Intent intent = new Intent(LeaderBoard.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private List<Information> makeJsonArrayRequest() {
        final List<Information> dat = new ArrayList<>();
        pDialog.show();
        showpDialog();
        StringRequest req = new StringRequest(Request.Method.POST,urlJsonArry,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Information current ;
                        jsonResponse=response;

                        try {
                            // Parsing json array response
                            // loop through each json object
                            JSONArray jAr=new JSONArray(response);
                            for (int i = 0; i < jAr.length(); i++) {

                                JSONObject person =  jAr.getJSONObject(i);

                                String name = person.getString("name");

                                String points = person.getString("amount");
                                String total = person.getString("total");
                                boolean bet=false;

                                current=new Information();
                                current.match=name;
                                current.teams=("Total points: "+points);
                                current.date=("Total Bets: "+total);
                                current.mainActivity=false;
                                current.star=bet;
                                dat.add(current);

                            }
                            hidepDialog();
                            mAdapter.notifyDataSetChanged();

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(),
                                    "Error: " + "Connection Error",
                                    Toast.LENGTH_LONG).show();
                        }


                    }

                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                       "Connection Error", Toast.LENGTH_SHORT).show();

            }
        }){

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("userId", session.getUserId());
                return params;
            }

        };



        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(req);
        hidepDialog();
        return dat;

    }
    @Override
    protected void onStop()
    {
        //if(GCMRegistrar.isRegisteredOnServer(this))
        //unregisterReceiver(mHandleMessageReceiver);
        super.onStop();
    }
    private void showpDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hidepDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();

        if (id==android.R.id.home) {
            finish();
        }

        return true;

    }
}

