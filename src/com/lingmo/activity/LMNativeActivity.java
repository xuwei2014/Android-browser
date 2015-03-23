package com.lingmo.activity;

import android.content.Intent;
import android.os.Bundle;

import cn.sharesdk.unity3d.ShareSDKUtils;

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

}
