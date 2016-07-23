package com.example.ahmeds.youtubeplayer;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements YTDataListener {
    List<YTVideo> videos=new ArrayList<>();
    ListView listview;
    myAdapter myadp;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listview = (ListView)findViewById(R.id.lv);
        videos= (new YTDBHelper(this,null,null,0)).getVideos();
        myadp = new myAdapter(this,videos);
        listview.setAdapter(myadp);

        //Overrding the list item click event, not used because there are buttons to be clicked inside the items in list
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapter, View view, int position, long arg) {
                /*listview.setEnabled(false);
                TextView v = (TextView) view.findViewById(R.id.item_title);
                Intent i = new Intent(MainActivity.this, VideoPlayerActivity.class);
                i.putExtra("embed", ((TextView) view.findViewById(R.id.item_title)).getHint().toString());
                i.putExtra("title", ((TextView) view.findViewById(R.id.item_title)).getText().toString());
                startActivity(i);*/
            }
        });
        //get GPS data to view City
        try {
            if (isLocationEnabled(getApplicationContext())){
                LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                Geocoder gcd = new Geocoder(getApplicationContext(), Locale.getDefault());

                List<Address> addresses = gcd.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                if (addresses.size() > 0)
                    Toast.makeText(getApplicationContext(), getString(R.string.CityLocation) + " " + addresses.get(0).getLocality() + "\n" + getRotation(getApplicationContext()), Toast.LENGTH_LONG).show();
                else Toast.makeText(getApplicationContext(), getString(R.string.NoAddressFound), Toast.LENGTH_LONG).show();
            }else{
                Toast.makeText(getApplicationContext(), getString(R.string.enableGPS), Toast.LENGTH_LONG).show();
            }
        }
        catch(SecurityException se){
            se.printStackTrace();
        }
        catch(Exception e){
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), getString(R.string.NoAddressFound), Toast.LENGTH_LONG).show();
        }
    }

    //Add a new video
    public void AddClick(View view){
        ((ImageButton)findViewById(R.id.add)).setEnabled(false);
        //get info to add
        DownloadYTDataTask task= new DownloadYTDataTask(this);
        String link=((EditText) findViewById(R.id.link)).getText().toString();
        task.execute("http://www.youtube.com/oembed?url=" + (link.toLowerCase().contains("youtube.com/watch?v=") ? link : "youtube.com/watch?v=" + link));
    }

    //Part of AddClick()
    @Override
    public void showData(YTVideo ytvideo) {
        if(ytvideo!=null) {
            YTDBHelper db= new YTDBHelper(this,null,null,0);
            db.addVideo(ytvideo);
            videos.add(ytvideo);
            myadp.notifyDataSetChanged();
            ((ImageButton)findViewById(R.id.add)).setEnabled(true);
            Toast.makeText(getApplicationContext(), R.string.VideoAddedSuccessfully, Toast.LENGTH_LONG).show();
        }
        //err
        else Toast.makeText(getApplicationContext(), R.string.ErrorInDownload, Toast.LENGTH_LONG).show();
        ((EditText) findViewById(R.id.link)).setText("");
        ((ImageButton)findViewById(R.id.add)).setEnabled(true);
    }

    //Delete Video Button
    public void DeleteClick(View view){
        final int position = listview.getPositionForView((View) view.getParent());
        YTDBHelper db= new YTDBHelper(this,null,null,0);
        db.deleteVideo(videos.get(position).get_title());
        videos.remove(position);
        myadp.notifyDataSetChanged();
        ((ImageButton)view).setEnabled(true);
        Toast.makeText(getApplicationContext(), R.string.VideoDeletedSuccessfully, Toast.LENGTH_LONG).show();
    }

    //Open video button
    public void OpenClick(View view){
        listview.setEnabled(false);
        Intent i = new Intent(MainActivity.this, VideoPlayerActivity.class);
        i.putExtra("embed", ((TextView) view.findViewById(R.id.item_title)).getHint().toString());
        i.putExtra("title", ((TextView) view.findViewById(R.id.item_title)).getText().toString());
        startActivity(i);
    }

    //Adapter class for listview
    class myAdapter extends ArrayAdapter<YTVideo>
    {
        View row;
        public myAdapter(Context context,List<YTVideo> arr)
        {
            super(context, R.layout.item, arr);
        }

        public View getView(final int position, View convertView, ViewGroup parent)
        {
            try{
                LayoutInflater inflater=getLayoutInflater();
                row = inflater.inflate(R.layout.item, parent, false);
                ((TextView)row.findViewById(R.id.item_title)).setText(videos.get(position).get_title());
                ((TextView)row.findViewById(R.id.item_title)).setHint(videos.get(position).get_embed());
                ((TextView)row.findViewById(R.id.item_author)).setText(videos.get(position).get_author());
                ((ImageView)row.findViewById(R.id.item_image)).setImageBitmap(BitmapFactory.decodeByteArray(videos.get(position).get_image(), 0, videos.get(position).get_image().length));

            }catch(Exception e){
            }
            return row;
        }
    }
    //results from activities started.
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
             if(resultCode == RESULT_OK && data.getStringExtra("finished")=="true"){
                listview.setEnabled(true);
             }
        }
    }

    public static boolean isLocationEnabled(Context context) {
        int locationMode = 0;
        String locationProviders;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
            try {
                locationMode = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE);

            } catch (Settings.SettingNotFoundException e) {
                e.printStackTrace();
            }

            return locationMode != Settings.Secure.LOCATION_MODE_OFF;

        }else{
            locationProviders = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
            return !TextUtils.isEmpty(locationProviders);
        }


    }
    //save an image to the memory
    private void SaveImage(Bitmap finalBitmap) {
        //<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root + "/saved_images");
        myDir.mkdirs();
        Random generator = new Random();
        int n = 10000;
        n = generator.nextInt(n);
        String fname = "Image-"+ n +".jpg";
        File file = new File (myDir, fname);
        if (file.exists ()) file.delete ();
        try {
            FileOutputStream out = new FileOutputStream(file);
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Detect the rotation of the device
    public String getRotation(Context context){
        final int rotation = getResources().getConfiguration().orientation;
        switch (rotation) {
            case Surface.ROTATION_0:
                return getString(R.string.portrait);
            case Surface.ROTATION_90:
                return  getString(R.string.landscape);
            case Surface.ROTATION_180:
                return  getString(R.string.revportrait);
            default:
                return  getString(R.string.revlandscape);
        }
    }

    //load image from it's memory bath
    public void loadImage(){
        Bitmap bmImg = BitmapFactory.decodeFile("path of your img1");
        (new ImageView(this)).setImageBitmap(bmImg);
    }

    //Play sound file
    public void PlaySound(Uri fileLocation){
        MediaPlayer mp;
        mp = MediaPlayer.create(getApplicationContext(), fileLocation /*R.raw.sound_one*/);
        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

            @Override
            public void onCompletion(MediaPlayer mp) {
                mp.reset();
                mp.release();
                mp=null;
            }

        });
        mp.start();
    }

    //Draw text on canvas
    public void DrawTextOnCanvas(){
        Canvas canvas=new Canvas()/*Or element*/;
        Paint paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawPaint(paint);

        paint.setColor(Color.BLACK);
        paint.setTextSize(20);
        canvas.drawText("Some Text", 10, 25, paint);
    }
}
