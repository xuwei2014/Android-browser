package com.lingmo.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import cn.sharesdk.unity3d.ShareSDKUtils;

import com.lingmo.ad.ADImageView;
import com.unity3d.player.UnityPlayerNativeActivity;

public class LMNativeActivity extends UnityPlayerNativeActivity {

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ShareSDKUtils.prepare(this.getApplicationContext());
	}

	public void openWeb(String url, boolean straight) {
		Intent intent = new Intent(this, WebActivity.class);
		intent.putExtra("url", url);
		intent.putExtra("isStraight", straight);
		this.startActivity(intent);
	}

	public void createADImage(final String name, final int width,
			final int height, final int sec, final String url) {
		Log.d("LMNativeActivty", "createADImage!!!");
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				new ADImageView(LMNativeActivity.this, name, width, height,
						sec, url);
			}
		});
	}
}
