package com.example.ahmeds.youtubeplayer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.VideoView;

public class VideoPlayerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player);
        Intent intent = getIntent();
        ((TextView)findViewById(R.id.title)).setText(intent.getStringExtra("title"));

        WebView wv=(WebView)findViewById(R.id.wv);
        //Override for video view
        WebChromeClient chromeClient = new WebChromeClient() {
            @Override
            public void onShowCustomView(View view, CustomViewCallback callback) {
                super.onShowCustomView(view, callback);
                if (view instanceof FrameLayout) {
                    FrameLayout frame = (FrameLayout) view;
                    if (frame.getFocusedChild() instanceof VideoView) {
                        VideoView video = (VideoView) frame.getFocusedChild();
                        frame.removeView(video);
                        video.start();
                    }
                }

            }
        };
        wv.setWebChromeClient(chromeClient);
        wv.getSettings().setJavaScriptEnabled(true);
        try {
            String result= "<html><body><iframe class=\"youtube-player\" type=\"text/html5\" width=\""
                    + "100%"//(w1 - 20)
                    + "\" height=\""
                    + "100%"
                    + "\" src=\""
                    + intent.getStringExtra("embed")
                    + "\" frameborder=\"0\"\"allowfullscreen\"></iframe></body></html>";

            wv.loadDataWithBaseURL(null, result, "text/html", "utf-8", "about:blank");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void CloseClick (View view){
        Intent intent = new Intent();
        intent.putExtra("finished","true");
        setResult(RESULT_OK, intent);
        finish();
    }
}

