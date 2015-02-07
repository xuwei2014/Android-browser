package com.qualcomm.QCARUnityPlayer;

import java.lang.reflect.Field;

import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;

import com.qualcomm.Utils.FavoritesManager;

import com.unity3d.player.UnityPlayer;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
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

		progressBar = (ProgressBar) findViewById(R.id.web_progress_bar);

		mWebView = (WebView) findViewById(R.id.webview);
		preButton = (Button) findViewById(R.id.pre_button);
		nextButton = (Button) findViewById(R.id.next_button);
		windowButton = (Button) findViewById(R.id.window_button);
		toolsButton = (Button) findViewById(R.id.tools_button);
		homeButton = (Button) findViewById(R.id.home_button);

		progressBar.setVisibility(View.GONE);

		mWebView.getSettings().setJavaScriptEnabled(true);
		mWebView.getSettings().setUseWideViewPort(true);
		mWebView.getSettings().setLoadWithOverviewMode(true);
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
				Log.d(DEB_TAG, "add favorites: title: " + curTitle + ", url: "
						+ curUrl);
				if (favManager.addFavorite(curTitle, curUrl)) {
					Toast.makeText(WebActivity.this, "书签保存成功", Gravity.BOTTOM)
							.show();
				}
				break;

			case R.id.tools_button:
				Log.d(DEB_TAG, "open favorites activity");
				startActivityForResult(new Intent(WebActivity.this,
						FavActivity.class), 0);
				break;

			case R.id.home_button:
				UnityPlayer.UnitySendMessage("Main Camera", "ExitWebScene", "");
				finish();
				break;

			default:
				break;
			}
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (mWebView.canGoBack()) {
				mWebView.goBack();
			} else {
				UnityPlayer.UnitySendMessage("Main Camera", "ExitWebScene", "");
				finish();
			}
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
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.more_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_share:
			Log.d(DEB_TAG, "favoratiiiiiii");
			ShareSDK.initSDK(this);
			OnekeyShare oks = new OnekeyShare();
			oks.disableSSOWhenAuthorize();
			
			oks.setNotification(R.drawable.ic_launcher, getString(R.string.app_name));
			oks.setTitle(getString(R.string.share));
			oks.setTitleUrl("http://www.baidu.com");
			oks.setText("text content!!!");
			oks.setImageUrl("http://img.baidu.com/img/image/zhenrenmeinv0207.jpg");
			oks.setUrl("http://www.baidu.com");
			oks.setComment("comment!!!");
			oks.setSite(getString(R.string.app_name));
			oks.setSiteUrl("http://www.baidu.com");
			
			oks.show(this);
			
			break;
			
		case R.id.menu_info:
			Log.d(DEB_TAG, "twooooooooo");
			Toast.makeText(WebActivity.this, "灵墨视界", Gravity.BOTTOM).show();
			break;
			
		default:
			break;
		}
		return true;
	}
	
	public void setOverflowIconVisiable(Menu menu) {
		try {
			@SuppressWarnings("rawtypes")
			Class clazz = Class
					.forName("com.android.internal.view.menu.MenuBuilder");
			Field field = clazz.getDeclaredField("mOptionalIconsVisible");
			if (field != null) {
				field.setAccessible(true);
				field.set(menu, true);
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public boolean onMenuOpened(int featureId, Menu menu) {
		if (featureId == Window.FEATURE_ACTION_BAR && menu != null) {
			setOverflowIconVisiable(menu);
		}
		
		return super.onMenuOpened(featureId, menu);
	}
}
