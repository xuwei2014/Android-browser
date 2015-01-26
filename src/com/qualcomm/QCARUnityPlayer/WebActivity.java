package com.qualcomm.QCARUnityPlayer;

import com.qualcomm.Utils.FavoritesManager;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

public class WebActivity extends Activity {
	private final String DEB_TAG = "WebActivity_debug";
	
	String curUrl = "";
	String curTitle = "";
	WebView mWebView;
	FavoritesManager favManager;
	
	// Progress Bar
	private ProgressBar progressBar;
	
	// Buttons
	private Button preButton;
	private Button nextButton;
	private Button windowButton;
	private Button toolsButton;	
	private Button homeButton;
	
	// Listeners
	private ButtonClickedListener buttonListener;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_PROGRESS);
        setContentView(R.layout.webview);
        
        progressBar = (ProgressBar)findViewById(R.id.web_progress_bar);
        
        mWebView = (WebView)findViewById(R.id.webview);
        preButton = (Button)findViewById(R.id.pre_button);
        nextButton = (Button)findViewById(R.id.next_button);
        windowButton = (Button)findViewById(R.id.window_button);
        toolsButton = (Button)findViewById(R.id.tools_button);
        homeButton = (Button)findViewById(R.id.home_button);
        
        progressBar.setVisibility(View.GONE);
        
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.loadUrl(this.getIntent().getStringExtra("url"));
        mWebView.setWebViewClient(new MyWebViewClient());
        mWebView.setWebChromeClient(new MyChromeClient());
        
        favManager = new FavoritesManager(getApplicationContext());
        
        buttonListener = new ButtonClickedListener();
        
        preButton.setOnClickListener(buttonListener);
        nextButton.setOnClickListener(buttonListener);
        windowButton.setOnClickListener(buttonListener);
        toolsButton.setOnClickListener(buttonListener);
        homeButton.setOnClickListener(buttonListener);
        
        preButton.setEnabled(false);
        nextButton.setEnabled(false);
    }
      
    private class MyWebViewClient extends WebViewClient {
    	@Override
    	public boolean shouldOverrideUrlLoading(WebView view, String url) {
    		view.loadUrl(url);
    		return true;
    	}
    	
    	@Override
    	public void onPageFinished(WebView view, String url) {
    		super.onPageFinished(view, url);
    		WebActivity.this.curUrl = url;
    		preButton.setEnabled(mWebView.canGoBack());
    		nextButton.setEnabled(mWebView.canGoForward());
    	}    	
    }
    
    private class MyChromeClient extends WebChromeClient {
    	@Override
    	public void onReceivedTitle(WebView view, String title) {
    		super.onReceivedTitle(view, title);
    		curTitle = title;
    	}
    	
    	@Override
    	public void onProgressChanged(WebView view, int newProgress) {
    		super.onProgressChanged(view, newProgress);
    		if (newProgress == 100) {
    			progressBar.setVisibility(View.GONE);
    		} else {
    			progressBar.setVisibility(View.VISIBLE);
    			progressBar.setProgress(newProgress);
    		}
    	}
    }
    
    private class ButtonClickedListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.pre_button:
				if (mWebView.canGoBack()) {
					mWebView.goBack();
				}
				break;
				
			case R.id.next_button:
				if (mWebView.canGoForward()) {
					mWebView.goForward();
				}
				break;
				
			case R.id.window_button:
				Log.d(DEB_TAG, "add favorites: title: " + curTitle + ", url: " + curUrl);
				if (favManager.addFavorite(curTitle, curUrl)) {
					Toast.makeText(WebActivity.this, "书签保存成功", Gravity.BOTTOM).show();
				}
				break;
				
			case R.id.tools_button:
				Log.d(DEB_TAG, "open favorites activity");
				startActivityForResult(new Intent(WebActivity.this, FavActivity.class), 0);
				break;
				
			case R.id.home_button:
				finish();
				break;

			default:
				break;
			}
		}
    }
       
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
    	if ((keyCode == KeyEvent.KEYCODE_BACK) && mWebView.canGoBack()) {
    		mWebView.goBack();
    		return true;
    	}
    	return super.onKeyDown(keyCode, event);
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	switch (resultCode) {
		case 0:
			Log.d(DEB_TAG, "favorites get url: " + data.getStringExtra("url"));
			mWebView.loadUrl(data.getStringExtra("url"));
			break;

		default:
			break;
		}
    	super.onActivityResult(requestCode, resultCode, data);
    }

}
