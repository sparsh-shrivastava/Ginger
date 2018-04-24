package mymajorproject.ginger;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class ChatActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        WebView browser = (WebView) findViewById(R.id.chatwebview);

        WebSettings webSettings = browser.getSettings();
        webSettings.setJavaScriptEnabled(true);

        browser.setWebViewClient(new MyBrowser());
//        browser.loadUrl("https://www.google.com");
        browser.loadUrl("http://rebot.me/bubblechat/chat?username=723940");


    }


    private class MyBrowser extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }

}
