package com.lingmo.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import cn.sharesdk.unity3d.ShareSDKUtils;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.lingmo.ad.ADImageView;
import com.unity3d.player.UnityPlayer;
import com.unity3d.player.UnityPlayerNativeActivity;

public class LMNativeActivity extends UnityPlayerNativeActivity {
	
	LocationClient mLocationClient;
	MyLocationListener mMyLocationListener;	
	String mLocName;	
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ShareSDKUtils.prepare(this.getApplicationContext());
		
        mLocationClient = new LocationClient(this.getApplicationContext());
        mMyLocationListener = new MyLocationListener();
		mLocationClient.registerLocationListener(mMyLocationListener);
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
	
	public void startLocation(String name) {
		Log.d("LMNative", "inside startLocation");
		initLocation();
		mLocationClient.start();
		Log.d("LMNative", "started");
		mLocName = name;
	}
	
	private void initLocation(){
		LocationClientOption option = new LocationClientOption();
		option.setLocationMode(LocationMode.Hight_Accuracy);
		option.setCoorType("gcj02");
		int span=500;
		option.setScanSpan(span);
		option.setIsNeedAddress(true);
		mLocationClient.setLocOption(option);
	}
	
	public class MyLocationListener implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation location) {
			//Receive Location 
			Log.d("LMNative", "onReceive");
			StringBuffer sb = new StringBuffer(256);
			sb.append("time : ");
			sb.append(location.getTime());
			sb.append("\nerror code : ");
			sb.append(location.getLocType());
			sb.append("\nlatitude : ");
			sb.append(location.getLatitude());
			sb.append("\nlontitude : ");
			sb.append(location.getLongitude());
			sb.append("\nradius : ");
			sb.append(location.getRadius());
			if (location.getLocType() == BDLocation.TypeGpsLocation){
				sb.append("\nspeed : ");
				sb.append(location.getSpeed());
				sb.append("\nsatellite : ");
				sb.append(location.getSatelliteNumber());
				sb.append("\ndirection : ");
				sb.append("\naddr : ");
				sb.append(location.getAddrStr());
				sb.append(location.getDirection());
			} else if (location.getLocType() == BDLocation.TypeNetWorkLocation){
				sb.append("\naddr : ");
				sb.append(location.getAddrStr());
				sb.append("\noperationers : ");
				sb.append(location.getOperators());
			}
			sb.append("\nprovince : ");
			sb.append(location.getProvince());
			sb.append("\ncity : ");
			sb.append(location.getCity());
			sb.append("\ndistrict : ");
			sb.append(location.getDistrict());
			Log.i("BaiduLocationApiDem", sb.toString());
			Log.d("LMNative", mLocName);
			UnityPlayer.UnitySendMessage(mLocName, "OnReceiveBaidu", sb.toString());
			mLocationClient.stop();
		}
	}
}
