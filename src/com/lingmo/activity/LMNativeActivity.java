package com.lingmo.activity;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;

import cn.sharesdk.unity3d.ShareSDKUtils;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.baidu.mapapi.SDKInitializer;
import com.lingmo.Utils.UpdateManager;
import com.lingmo.ad.ADImageView;
import com.unity3d.player.UnityPlayer;
import com.unity3d.player.UnityPlayerNativeActivity;


public class LMNativeActivity extends UnityPlayerNativeActivity {
	
	/**
	 * 构造广播监听类，监听 SDK key 验证以及网络异常广播
	 */
	public class SDKReceiver extends BroadcastReceiver {
		public void onReceive(Context context, Intent intent) {
			String s = intent.getAction();
			Log.d("SDKReceiver", "action: " + s);
			if (s.equals(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_ERROR)) {
				Log.d("SDKReceiver", "key 验证出错! 请在 AndroidManifest.xml 文件中检查 key 设置");
			} else if (s
					.equals(SDKInitializer.SDK_BROADCAST_ACTION_STRING_NETWORK_ERROR)) {
				Log.d("SDKReceiver", "网络出错");
			}
		}
	}
	private SDKReceiver mReceiver;
	
	LocationClient mLocationClient;
	MyLocationListener mMyLocationListener;	
	String mLocName;	
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ShareSDKUtils.prepare(this.getApplicationContext());
		SDKInitializer.initialize(getApplicationContext());
		
		// 注册 SDK 广播监听者
		IntentFilter iFilter = new IntentFilter();
		iFilter.addAction(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_ERROR);
		iFilter.addAction(SDKInitializer.SDK_BROADCAST_ACTION_STRING_NETWORK_ERROR);
		mReceiver = new SDKReceiver();
		registerReceiver(mReceiver, iFilter);
		
        mLocationClient = new LocationClient(this.getApplicationContext());
        mMyLocationListener = new MyLocationListener();
		mLocationClient.registerLocationListener(mMyLocationListener);
	}
	
    public void recommand(String name) {
    	Intent intent = new Intent(this, ShopListActivity.class);
    	this.startActivity(intent);
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
	
    public void downloadAPK(final String url) {
    	runOnUiThread(new Runnable() {
			@Override
			public void run() {
				UpdateManager updateManager = new UpdateManager(LMNativeActivity.this, url);
				updateManager.showDownloadDialog();
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

			JSONObject jObj = new JSONObject();
			try {
				jObj.put("time", location.getTime());
				jObj.put("error", location.getLocType());
				jObj.put("latitude", location.getLatitude());
				jObj.put("longitude", location.getLongitude());
				jObj.put("radius", location.getRadius());
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}			
			
			if (location.getLocType() == BDLocation.TypeGpsLocation){				
				try {
					jObj.put("speed", location.getSpeed());
					jObj.put("satellite", location.getSatelliteNumber());
					jObj.put("direction", location.getDirection());
					jObj.put("addr", location.getAddrStr());
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else if (location.getLocType() == BDLocation.TypeNetWorkLocation){
				try {
					jObj.put("addr", location.getAddrStr());
					jObj.put("operationers", location.getOperators());
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			try {
				jObj.put("province", location.getProvince());
				jObj.put("city", location.getCity());
				jObj.put("district", location.getDistrict());
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Log.i("BaiduLocationApiDem", jObj.toString());
			Log.d("LMNative", mLocName);
			UnityPlayer.UnitySendMessage(mLocName, "OnReceiveBaidu", jObj.toString());
			mLocationClient.stop();
		}
	}
}
