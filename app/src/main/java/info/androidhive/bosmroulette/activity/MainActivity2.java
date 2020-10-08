package info.androidhive.bosmroulette.activity;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;


import info.androidhive.bosmroulette.R;

public class MainActivity2 extends Activity {
    private final int SPLASH_DISPLAY_LENGTH = 4000;
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_activity2);
        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {
                /* Create an Intent that will start the Menu-Activity. */
                Intent mainIntent = new Intent(MainActivity2.this,LoginActivity.class);
                MainActivity2.this.startActivity(mainIntent);
                MainActivity2.this.finish();
            }
        }, SPLASH_DISPLAY_LENGTH);
    }
}
