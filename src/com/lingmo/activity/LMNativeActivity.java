package com.lingmo.activity;

import android.content.Intent;

import com.unity3d.player.UnityPlayerNativeActivity;

public class LMNativeActivity extends UnityPlayerNativeActivity {
	
	public void openWeb(String url, boolean straight) {
		Intent intent = new Intent(this, WebActivity.class);
		intent.putExtra("url", url);
		intent.putExtra("isStraight", straight);
		this.startActivity(intent);
	}

}
