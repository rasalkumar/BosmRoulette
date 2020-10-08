package info.androidhive.bosmroulette.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import info.androidhive.bosmroulette.R;
import info.androidhive.bosmroulette.helper.SessionManager;

/**
 * Created by the master mind Mr.Shivam Gupta on 12/09/2015.
 */
public class InviteFriends extends ActionBarActivity implements View.OnClickListener {

    private Toolbar mtoolbar;
    private Button invite;
    SessionManager session;
    TextView promo_code;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite_friends);
        session=new SessionManager(this);
        mtoolbar = (Toolbar)findViewById(R.id.toolbar);
        invite = (Button)findViewById(R.id.invite_friends);
        promo_code = (TextView)findViewById(R.id.invite_code);
        promo_code.setText(session.getUserId().substring(15,20));
        invite.setOnClickListener(this);
        setSupportActionBar(mtoolbar);
        getSupportActionBar().setTitle("INVITE FRIENDS");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

    }

    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();

        if (id==android.R.id.home) {
            finish();
        }

        return true;

    }

    @Override
    public void onClick(View view) {
        String s = promo_code.getText().toString();

        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        String shareBody = "Use my promo code "+"'"+session.getUserId().substring(15,20)+"'"+" and get" +
                " additional 5 points for BOSM ROULETTE. Download this app from play store. " +
                "https://play.google.com/store/apps/details?id=com.codingclub.bosm.roulette";
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "BOSM ROULETTE");
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
        startActivity(Intent.createChooser(sharingIntent, "Share via"));
    }

}
