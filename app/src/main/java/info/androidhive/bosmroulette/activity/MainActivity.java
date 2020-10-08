package info.androidhive.bosmroulette.activity;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gcm.GCMRegistrar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import info.androidhive.bosmroulette.R;
import info.androidhive.bosmroulette.app.AppConfig;
import info.androidhive.bosmroulette.app.AppController;
import info.androidhive.bosmroulette.helper.SQLiteHandler;
import info.androidhive.bosmroulette.helper.SessionManager;
public class MainActivity extends AppCompatActivity implements FragmentDrawer.FragmentDrawerListener {

    private Context context=this;
    private Toolbar mToolbar;
    private FragmentDrawer drawerFragment;
    private List<Information> data;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private SQLiteHandler db;
    private SessionManager session;
    private String jsonResponse;
    private String urlJsonArry = "http://doctordhoondo.org/bosm/bosm/getMatches.php";
    private static String TAG = MainActivity.class.getSimpleName();
    private ImageButton FAB;
    public String[] cc = {""};
    TextView Credits;
    // label to display gcm messages
    TextView lblMessage;

    // Asyntask
    AsyncTask<Void, Void, Void> mRegisterTask;

    // Alert dialog manager
    AlertDialogManager alert = new AlertDialogManager();

    // Connection detector
    ConnectionDetector cd;
    private ProgressDialog pDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        pDialog=new ProgressDialog(this);
        pDialog.setMessage("Please wait...");
        pDialog.setCancelable(false);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        db = new SQLiteHandler(getApplicationContext());


        // session manager
        session = new SessionManager(getApplicationContext());
        if (!session.isLoggedIn()) {
            logoutUser();
        }


        showpDialog();
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        data=this.makeJsonArrayRequest();
       setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        drawerFragment = (FragmentDrawer)
                getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        drawerFragment.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), mToolbar);
        drawerFragment.setDrawerListener(this);

        //*************************************

        registerGCM();
        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);

        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(mLayoutManager);


        // specify an adapter (see also next example)
        mAdapter = new MyAdapter(MainActivity.this,makeJsonArrayRequest());

       mRecyclerView.addOnItemTouchListener(
               new RecyclerItemClickListener(MainActivity.this, new RecyclerItemClickListener.OnItemClickListener() {
                   @Override
                   public void onItemClick(View view, final int position) {
                       // TODO Handle item click
                       Intent i;
                       new Handler().postDelayed(new Runnable() {
                           @Override
                           public void run() {

                               Intent i = new Intent(MainActivity.this, InfoActivity.class);
                               i.putExtra("json", jsonResponse);
                               i.putExtra("id", position);
                               startActivity(i);
                           }
                       }, 50);

                   }
               })
       );

       // mAdapter = new MyAdapter(MainActivity.this,data);
        mRecyclerView.setAdapter(mAdapter);

        FAB = (ImageButton) findViewById(R.id.imageButton);
        FAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getPoints();

            }
        });
        //displayView(0);


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main_activity2, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id)
        {
            case R.id.action_refresh:
                mAdapter = new MyAdapter(MainActivity.this,makeJsonArrayRequest());
                mRecyclerView.setAdapter(mAdapter);
                break;
            case R.id.action_share:
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                String shareBody = "Download BOSM ROU LETTE app from play" +
                        " store and predict which team is going to win the next match " +
                        "in BOSM 2015.I tried and it's amazing."+"Use my promo code "+"'"+session.getUserId().substring(15,20)+"'"+" " +
                        "and get additional 5 points.Download this app from play store. https://play.google.com/store/apps/details?id=com.codingclub.bosm.roulette";
                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "BOSM ROULETTE");
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                startActivity(Intent.createChooser(sharingIntent, "Share via"));
                break;

            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    private void logoutUser() {
        session.setLogin(false);
        session.setUserId(null);
        db.deleteUsers();

        // Launching the login activity
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     * Method to make json array request where response starts with [
     * */
    private void registerGCM()
    {
        // Make sure the device has the proper dependencies.
        GCMRegistrar.checkDevice(this);

        // Make sure the manifest was properly set - comment out this line
        // while developing the app, then uncomment it when it's ready.
        GCMRegistrar.checkManifest(this);

       //lblMessage = (TextView) findViewById(R.id.lblMessage);

        registerReceiver(mHandleMessageReceiver, new IntentFilter(
                CommonUtilities.DISPLAY_MESSAGE_ACTION));

        // Get GCM registration id
        final String regId = GCMRegistrar.getRegistrationId(this);
        Log.d(TAG, regId);
        // Check if regid already presents
        if (regId.equals("")) {
            // Registration is not present, register now with GCM
            GCMRegistrar.register(this, CommonUtilities.SENDER_ID);

            if(GCMRegistrar.isRegisteredOnServer(this))
            {Toast.makeText(getApplicationContext(), "Notifications Enabled", Toast.LENGTH_LONG).show();}
        } else {
            // Device is already registered on GCM
            if (GCMRegistrar.isRegisteredOnServer(this)) {
                // Skips registration.


            } else {
                // Try to register again, but not in the UI thread.
                // It's also necessary to cancel the thread onDestroy(),
                // hence the use of AsyncTask instead of a raw thread.
                final Context context = this;
                mRegisterTask = new AsyncTask<Void, Void, Void>() {

                    @Override
                    protected Void doInBackground(Void... params) {
                        // Register on our server
                        // On server creates a new user
                        ServerUtilities.register(context, session.getUserId(), regId);
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void result) {
                        mRegisterTask = null;
                    }

                };
                mRegisterTask.execute(null, null, null);
            }
        }
    }
    private final BroadcastReceiver mHandleMessageReceiver = new BroadcastReceiver() {
        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
        @Override
        public void onReceive(Context context, Intent intent) {
            String newMessage = intent.getExtras().getString(CommonUtilities.EXTRA_MESSAGE);
            // Waking up mobile if it is sleeping
            WakeLocker.acquire(getApplicationContext());

            WakeLocker.release();
        }
    };

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

                                String match = person.getString("match_name");

                                String teamA = person.getString("t1_name");
                                String teamB = person.getString("t2_name");
                                String date = person.getString("time");
                                boolean bet=person.getBoolean("bet");
                                current=new Information();
                                current.match=match;
                                current.teams=(teamA+" Vs "+teamB);
                                current.date=date;
                                current.Text=assignText(match);
                                current.star=bet;
                                dat.add(current);

                            }

                            mAdapter.notifyDataSetChanged();
                            hidepDialog();
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(),
                                    "Error: " + e.getLocalizedMessage(),
                                    Toast.LENGTH_LONG).show();
                        }

                    hidepDialog();
                    }


                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getLocalizedMessage());
                Toast.makeText(getApplicationContext(),
                        error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();

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

        return dat;

    }
    @Override
    protected void onStop()
    {
      // if(GCMRegistrar.isRegisteredOnServer(this))
      // unregisterReceiver(mHandleMessageReceiver);
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

    private void displayView(int position) {

        Intent i;
        switch (position) {
            case 0:
                break;
            case 1:
                i=new Intent(MainActivity.this,InviteFriends.class);
                startActivity(i);
                break;
            case 2:
                i = new Intent(MainActivity.this,LeaderBoard.class);
                startActivity(i);
                break;
            case 3:
                i = new Intent(MainActivity.this,Instructions.class);
                startActivity(i);
                break;
            case 4:
                i = new Intent(MainActivity.this,ContactUs.class);
                startActivity(i);
                break;
            case 5:
                logoutUser();
                break;
            default:
                break;
        }
        // set the toolbar title
        //getSupportActionBar().setTitle(title);
    }
    public String assignText(String match)
    {
        String text;
        if (match.charAt(0)=='F')
        {
            text="F";
        }
        else if(match.charAt(0)=='B' && match.charAt(2)=='s')
        {
            text="B";
        }
        else if(match.charAt(0)=='B' && match.charAt(2)=='d')
        {
            text="B";
        }
        else if(match.charAt(0)=='H')
        {
            text="H";
        }
        else if(match.charAt(0)=='C')
        {
            text="C";
        }
        else if(match.charAt(0)=='V')
        {
            text="V";
        }
        else if(match.charAt(0)=='T')
        {
            text="T";
        }
        else
        {
            text="M";
        }
        return text;
    }


    @Override
    public void onDrawerItemSelected(View view, int position) {
        displayView(position);
    }
    private void getPoints() {
        // Tag used to cancel the request
       showpDialog();


        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_REGISTER, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                try {
                    JSONObject jObj = new JSONObject(response);
                    String points = jObj.getString("points");

                    LayoutInflater l = LayoutInflater.from(context);
                    View promptsView = l.inflate(R.layout.cart_promt, null);

                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

                    // set prompts.xml to alertdialog builder
                    alertDialogBuilder.setView(promptsView);


                    Credits = (TextView)promptsView.findViewById(R.id.credits);
                    // create alert dialog
                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();

                    try {
                        //Credits.setText();
                        Credits.setText(points);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                }
                hidepDialog();
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Lost Internet Connection." + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        "Connection Error", Toast.LENGTH_LONG).show();

            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("tag", "getPoints");
                params.put("userId", session.getUserId());
                return params;
            }

        };
        hidepDialog();
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, "new String Request Tag");

    }


}
