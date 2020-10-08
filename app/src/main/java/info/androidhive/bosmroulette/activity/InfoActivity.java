package info.androidhive.bosmroulette.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import info.androidhive.bosmroulette.R;
import info.androidhive.bosmroulette.adapter.SimpleRecyclerAdapter;
import info.androidhive.bosmroulette.app.AppConfig;
import info.androidhive.bosmroulette.app.AppController;
import info.androidhive.bosmroulette.helper.SessionManager;

public class InfoActivity extends AppCompatActivity {
    CollapsingToolbarLayout collapsingToolbar;
    RecyclerView recyclerView;
    int mutedColor = R.attr.colorPrimary;
    SimpleRecyclerAdapter simpleRecyclerAdapter;
    private ProgressDialog pDialog;
    private JSONObject match;
    private String matchTitle,team1,team2;
    private int matchId,sportId;
    private ImageView img;
    private int radioSelected;
    private FloatingActionButton fab;
    final Context context = this;
    private RadioButton button1,button2;
    private SessionManager session;
    List<String> listData = new ArrayList<String>();
    private static String TAG = InfoActivity.class.getSimpleName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(true);
        setContentView(R.layout.activity_info);
        session=new SessionManager(getApplicationContext());
        final Toolbar toolbar = (Toolbar) findViewById(R.id.anim_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        Bundle extras=getIntent().getExtras();
        int reIds[]={(R.drawable.p1),(R.drawable.p2),(R.drawable.p3),(R.drawable.p4),(R.drawable.p5),(R.drawable.p6)};
        JSONArray arr = null;
        try {
            arr=new JSONArray(extras.getString("json"));
            match=arr.getJSONObject(extras.getInt("id"));
            matchTitle = match.getString("match_name");
            team1=match.getString("t1_name");
            team2=match.getString("t2_name");
            listData.add(match.getString("sport_name"));
            listData.add(team1+" Vs "+team2);
            listData.add(match.getString("time"));
            listData.add(match.getString("venue"));
            listData.add(match.getString("t1_score") + " <> " + match.getString("t1_score"));
            listData.add(match.getString("prevBet"));
            sportId=match.getInt("sport_id");
            matchId=match.getInt("id");
            if(!extras.isEmpty()) {
                getSupportActionBar().setTitle(matchTitle);
                collapsingToolbar.setTitle(matchTitle);
                int currPos=(sportId)%(7);
                img=(ImageView) findViewById(R.id.header);
                img.setImageResource(reIds[currPos-1]);
                ImageView header = (ImageView) findViewById(reIds[currPos-1]);

                recyclerView = (RecyclerView) findViewById(R.id.scrollableview);

                recyclerView.setHasFixedSize(true);
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
                recyclerView.setLayoutManager(linearLayoutManager);



                if (simpleRecyclerAdapter == null) {
                    simpleRecyclerAdapter = new SimpleRecyclerAdapter(listData);
                    simpleRecyclerAdapter.setInfoActivitiesList(this);
                    recyclerView.setAdapter(simpleRecyclerAdapter);
                }
                Bitmap bitmap = BitmapFactory.decodeResource(getResources(),reIds[currPos-1]);
                Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
                    @SuppressWarnings("ResourceType")
                    @Override
                    public void onGenerated(Palette palette) {

                        mutedColor = palette.getMutedColor(R.color.navigationBarColor);
                        collapsingToolbar.setContentScrimColor(mutedColor);
                        collapsingToolbar.setStatusBarScrimColor(R.color.cardview_shadow_end_color);
                    }
                });
            }
        } catch (JSONException e) {
            Log.d(TAG, e.getLocalizedMessage());
        }


        fab=(FloatingActionButton) findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                // get prompts.xml view
                LayoutInflater li = LayoutInflater.from(context);
                View promptsView = li.inflate(R.layout.prompt, null);

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                        context);

                // set prompts.xml to alertdialog builder
                alertDialogBuilder.setView(promptsView);
                try {
                    button1=(RadioButton)promptsView.findViewById(R.id.radioteam1);
                    button1.setText(team1);
                    button2=(RadioButton)promptsView.findViewById(R.id.radioteam2);
                    button2.setText(team2);
                } catch (Exception e) {
                    Log.d(TAG,e.getMessage());
                }
                final EditText userInput = (EditText) promptsView
                        .findViewById(R.id.editTextDialogUserInput);

                // set dialog message
                alertDialogBuilder
                        .setCancelable(false)
                        .setPositiveButton("OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,int id) {
                                        // get user input and set it to result
                                        // edit text
                                        placeBet(userInput.getText().toString(),radioSelected);
                                    }
                                })
                        .setNegativeButton("Cancel",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,int id) {
                                        dialog.cancel();
                                    }
                                });

                // create alert dialog
                AlertDialog alertDialog = alertDialogBuilder.create();

                // show it
                alertDialog.show();

            }
        });

    }
    public void onRadioButtonClicked(View view)
    {
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.radioteam1:
                if (checked)
                    radioSelected=1;
                    break;
            case R.id.radioteam2:
                if (checked)
                    radioSelected=2;
                    break;
            default:
                    radioSelected=0;
                    break;
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;

        }
        return super.onOptionsItemSelected(item);
    }
    private void placeBet(final String amount, final int team) {
        // Tag used to cancel the request
        String tag_string_req = "req_login";

        pDialog.setMessage("Please wait...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_LOGIN, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Login Response: " + response.toString());
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");

                    // Check for error node in json
                    if (!error) {
                        String message=jObj.getString("message");
                        Toast.makeText(getApplicationContext(),
                                message, Toast.LENGTH_LONG).show();

                    } else {
                        // Error in login. Get the error message
                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(getApplicationContext(),
                                errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Login Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("tag", "placeBet");
                params.put("amount", String.valueOf(amount));
                params.put("team", String.valueOf(team));
                params.put("match_id", String.valueOf(matchId));
                params.put("user_id", String.valueOf(session.getUserId()));

                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq);
        radioSelected=0;
    }

    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }
}

