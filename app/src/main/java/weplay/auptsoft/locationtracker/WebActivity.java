package weplay.auptsoft.locationtracker;

import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.webkit.ClientCertRequest;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.wang.avi.AVLoadingIndicatorView;

/**
 * Created by Administrator on 12/30/2018.
 */

public class WebActivity extends AppCompatActivity {
    SwipeRefreshLayout swipeRefreshLayout;
    WebView mainWebView;
    Toolbar toolbar;
    AVLoadingIndicatorView loadingIndicatorView;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_web);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        toolbar = (Toolbar)findViewById(R.id.toolbar_id);
        swipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.swipe_refresh_view_id);
        loadingIndicatorView = (AVLoadingIndicatorView)findViewById(R.id.web_progress_indicator_id);
        mainWebView = (WebView)findViewById(R.id.web_view_id);
        mainWebView.getSettings().setJavaScriptEnabled(true);

        setSupportActionBar(toolbar);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finishAfterTransition();
            }
        });


        String url = getIntent().getStringExtra("url");
        String data = getIntent().getStringExtra("data");
        String method = getIntent().getStringExtra("method");

        if(method.toLowerCase().equals("get")) {
            mainWebView.loadUrl(url);
        } else if (method.toLowerCase().equals("post")){
            mainWebView.postUrl(url, data.getBytes());
        }

        getSupportActionBar().hide();


        mainWebView.setWebViewClient(new WebViewClient(){
            @Override
            public void onReceivedClientCertRequest(WebView view, ClientCertRequest request) {
                //super.onReceivedClientCertRequest(view, request);
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                swipeRefreshLayout.setRefreshing(true);
                loadingIndicatorView.setVisibility(View.VISIBLE);
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                swipeRefreshLayout.setRefreshing(false);
                loadingIndicatorView.setVisibility(View.GONE);
                super.onPageFinished(view, url);
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                loadingIndicatorView.setVisibility(View.GONE);
                super.onReceivedError(view, request, error);
            }

        });


        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mainWebView.reload();
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (mainWebView.canGoBack()) {
            mainWebView.goBack();
        } else {
            supportFinishAfterTransition();
        }
    }
}
