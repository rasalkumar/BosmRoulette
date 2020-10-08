package info.androidhive.bosmroulette.activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.widget.ImageView;

import info.androidhive.bosmroulette.R;


/**
 * Created by the master mind Mr.Shivam Gupta on 16/09/2015.
 */
public class ContactUs extends ActionBarActivity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_us);

        // create bitmap from resource
        Bitmap bm1 = BitmapFactory.decodeResource(getResources(),
                R.drawable.teja_icon);
        Bitmap bm2 = BitmapFactory.decodeResource(getResources(),
                R.drawable.rohit_icon);
       Bitmap bm7 = BitmapFactory.decodeResource(getResources(),
                R.drawable.user_icon);
        Bitmap bm8 = BitmapFactory.decodeResource(getResources(),
                R.drawable.user_icon);
        Bitmap bm4 = BitmapFactory.decodeResource(getResources(),
                R.drawable.shivam_icon);
        Bitmap bm5 = BitmapFactory.decodeResource(getResources(),
                R.drawable.rasal_icon);
        Bitmap bm6 = BitmapFactory.decodeResource(getResources(),
                R.drawable.harish_icon);

        // set circle bitmap
        ImageView m1Image = (ImageView) findViewById(R.id.image1);
        ImageView m2Image = (ImageView) findViewById(R.id.image2);
        ImageView m7Image = (ImageView) findViewById(R.id.image7);
        ImageView m8Image = (ImageView) findViewById(R.id.image8);
        ImageView m4Image = (ImageView) findViewById(R.id.image4);
        ImageView m5Image = (ImageView) findViewById(R.id.image5);
        ImageView m6Image = (ImageView) findViewById(R.id.image6);
        m1Image.setImageBitmap(getCircleBitmap(bm1));
        m2Image.setImageBitmap(getCircleBitmap(bm2));
        //m3Image.setImageBitmap(getCircleBitmap(bm3));
        m4Image.setImageBitmap(getCircleBitmap(bm4));
        m5Image.setImageBitmap(getCircleBitmap(bm5));
        m6Image.setImageBitmap(getCircleBitmap(bm6));
        m7Image.setImageBitmap(getCircleBitmap(bm7));
        m8Image.setImageBitmap(getCircleBitmap(bm8));
    }

    private Bitmap getCircleBitmap(Bitmap bitmap) {
        final Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        final Canvas canvas = new Canvas(output);

        final int color = Color.RED;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawOval(rectF, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        bitmap.recycle();

        return output;
    }
}
