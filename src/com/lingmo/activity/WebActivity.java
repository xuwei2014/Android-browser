package com.lingmo.activity;

import java.lang.reflect.Field;

import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;

import com.lingmo.Utils.FavoritesManager;

import com.unity3d.player.UnityPlayer;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewConfiguration;
import android.view.Window;
import android.view.View.OnClickListener;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

public class WebActivity extends Activity {
	private final String DBG_TAG = "WebActivity_debug";
	private final String INFO_URL = "http://huituzhixin.com:6688/jianpai/About/index.html";
	private final String INFO_TITLE = "灵墨视界";
	
	String curUrl = "";
	String curTitle = "";
	Bitmap curIcon = null;
	WebView mWebView;
	GestureDetector mGestureDetector;
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
		
		mGestureDetector = new GestureDetector(this, new GestureListener());
		mWebView.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (v.getId() == R.id.webview) {
					return mGestureDetector.onTouchEvent(event);
				}
				return false;
			}
		});

		favManager = new FavoritesManager(getApplicationContext());
		favManager.addFavorite(INFO_TITLE, INFO_URL, null);

		buttonListener = new ButtonClickedListener();

		preButton.setOnClickListener(buttonListener);
		nextButton.setOnClickListener(buttonListener);
		windowButton.setOnClickListener(buttonListener);
		toolsButton.setOnClickListener(buttonListener);
		homeButton.setOnClickListener(buttonListener);
		

		preButton.setEnabled(false);
		nextButton.setEnabled(false);
		
		setOverflowShowingAlways();
		
		// go straight to the bookmarks activity
		if (this.getIntent().getBooleanExtra("isStraight", false)) {
			Log.d(DBG_TAG, "open favorites activity straight!!!");
			startActivityForResult(new Intent(WebActivity.this,
					FavActivity.class), 0);
		}
	}

	private void setOverflowShowingAlways() {
		try {
			ViewConfiguration config = ViewConfiguration.get(this);
			Field menuKeyField = ViewConfiguration.class.getDeclaredField("sHasPermanentMenuKey");
			menuKeyField.setAccessible(true);
			menuKeyField.setBoolean(config, false);
		} catch (Exception e) {
			e.printStackTrace();
		}
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
		public void onReceivedIcon(WebView view, Bitmap icon) {
			super.onReceivedIcon(view, icon);
			curIcon = icon;
			if (curIcon != null) {
				Log.d(DBG_TAG, "Get web icon!!!");
			}
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
				Log.d(DBG_TAG, "add favorites: title: " + curTitle + ", url: "
						+ curUrl);
				if (favManager.addFavorite(curTitle, curUrl, curIcon)) {
					Toast.makeText(WebActivity.this, "书签保存成功", Gravity.BOTTOM)
							.show();
				}
				break;

			case R.id.tools_button:
				Log.d(DBG_TAG, "open favorites activity");
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
			Log.d(DBG_TAG, "favorites get url: " + data.getStringExtra("url"));
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
			Log.d(DBG_TAG, "favoratiiiiiii");
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
			
		case R.id.menu_fav:
			Log.d(DBG_TAG, "add favorites: title: " + curTitle + ", url: "
					+ curUrl);
			if (favManager.addFavorite(curTitle, curUrl, curIcon)) {
				Toast.makeText(WebActivity.this, "书签保存成功", Gravity.BOTTOM)
						.show();
			}
			break;
			
		case R.id.menu_fav_dir:
			Log.d(DBG_TAG, "open favorites activity");
			startActivityForResult(new Intent(WebActivity.this,
					FavActivity.class), 0);
			break;
			
		case R.id.menu_info:
			Log.d(DBG_TAG, "twooooooooo");
			mWebView.loadUrl(INFO_URL);
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
	
	private class GestureListener implements GestureDetector.OnGestureListener {

		@Override
		public boolean onDown(MotionEvent arg0) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean onFling(MotionEvent arg0, MotionEvent arg1, float velocityX,
				float velocityY) {
			Log.d(DBG_TAG, "Y is: " + mWebView.getScrollY());
			if (mWebView.getScrollY() > 0) {
				getActionBar().hide();
			} else {
				getActionBar().show();
			}
			
			return false;
		}

		@Override
		public void onLongPress(MotionEvent arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public boolean onScroll(MotionEvent arg0, MotionEvent arg1, float arg2,
				float arg3) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public void onShowPress(MotionEvent arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public boolean onSingleTapUp(MotionEvent arg0) {
			// TODO Auto-generated method stub
			return false;
		}		
	}
}
