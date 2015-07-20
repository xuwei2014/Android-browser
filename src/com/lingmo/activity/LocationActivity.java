package com.lingmo.activity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.MyLocationConfiguration.LocationMode;
import com.baidu.mapapi.model.LatLng;
import com.lingmo.Utils.Coordinate2D;
import com.lingmo.Utils.Info;
import com.lingmo.Utils.LocConverter;
import com.lingmo.info.ShopInfo;



/**
 * 此demo用来展示如何结合定位SDK实现定位，并使用MyLocationOverlay绘制定位位置 同时展示如何使用自定义图标绘制并点击时弹出泡泡
 * 
 */
public class LocationActivity extends Activity {

	// 定位相关
	LocationClient mLocClient;
	public MyLocationListenner myListener = new MyLocationListenner();
	BitmapDescriptor mCurrentMarker;

	MapView mMapView;
	BaiduMap mBaiduMap;
	
	RelativeLayout mMarkerInfoLy;
	TextView t1;
	TextView t2;
	TextView t3;

	// UI相关
	OnCheckedChangeListener radioButtonListener;
	Button requestLocButton;
	boolean isFirstLoc = true;// 是否首次定位
	
	// 初始化全局 bitmap 信息，不用时及时 recycle
	BitmapDescriptor bd_start;
	BitmapDescriptor bd_end;
	
    LatLng mLoc = null;
    LatLng mDest = null;
    String mDestName = null;
    
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_location);
		
		bd_start = BitmapDescriptorFactory.fromAsset("Icon_start.png");
		bd_end = BitmapDescriptorFactory.fromAsset("Icon_end.png");
		
		mMarkerInfoLy = (RelativeLayout)findViewById(R.id.detail_info);
		t1 = (TextView)findViewById(R.id.info_name);
		t2 = (TextView)findViewById(R.id.info_phoneno);
		t3 = (TextView)findViewById(R.id.info_distance);

		// 地图初始化
		mMapView = (MapView) findViewById(R.id.bmapView);
		mBaiduMap = mMapView.getMap();
		MapStatusUpdate msu = MapStatusUpdateFactory.zoomTo(14.0f);
		mBaiduMap.setMapStatus(msu);
		
		// 开启定位图层
		mBaiduMap.setMyLocationEnabled(true);
		// 定位初始化
		mLocClient = new LocationClient(this);
		mLocClient.registerLocationListener(myListener);
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true);// 打开gps
		option.setCoorType("bd09ll"); // 设置坐标类型
		option.setScanSpan(1000);
		mLocClient.setLocOption(option);
		mLocClient.start();
				
		addMark();
	}

	/**
	 * 定位SDK监听函数
	 */
	public class MyLocationListenner implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation location) {
			// map view 销毁后不在处理新接收的位置
			if (location == null || mMapView == null)
				return;
//			MyLocationData locData = new MyLocationData.Builder()
//					.accuracy(location.getRadius())
//					// 此处设置开发者获取到的方向信息，顺时针0-360
//					.direction(100).latitude(location.getLatitude())
//					.longitude(location.getLongitude()).build();
//			mBaiduMap.setMyLocationData(locData);
			if (isFirstLoc) {
				isFirstLoc = false;
				LatLng ll = new LatLng(location.getLatitude(),
						location.getLongitude());
				mLoc = ll;
				MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);
				mBaiduMap.animateMapStatus(u);
				mBaiduMap.addOverlay(new MarkerOptions().icon(bd_start).position(ll));
			}
		}

		public void onReceivePoi(BDLocation poiLocation) {
		}
	}
	
	@Override
	protected void onPause() {
		mMapView.onPause();
		super.onPause();
	}

	@Override
	protected void onResume() {
		mMapView.onResume();
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		// 退出时销毁定位
		mLocClient.stop();

		// 关闭定位图层
		mBaiduMap.setMyLocationEnabled(false);
		mMapView.onDestroy();
		super.onDestroy();
	}
	
    private static String convertInputStreamToString(InputStream inputStream) throws IOException{
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while((line = bufferedReader.readLine()) != null)
            result += line;
 
        inputStream.close();
        return result;
 
    }
	 
    public static String GET(String url){
        InputStream inputStream = null;
        String result = "";
        try {
 
            // create HttpClient
            HttpClient httpclient = new DefaultHttpClient();
 
            // make GET request to the given URL
            HttpResponse httpResponse = httpclient.execute(new HttpGet(url));
 
            // receive response as inputStream
            inputStream = httpResponse.getEntity().getContent();
 
            // convert inputstream to string
            if(inputStream != null)
                result = convertInputStreamToString(inputStream);
            else
                result = "Did not work!";
 
        } catch (Exception e) {
            Log.d("InputStream", e.getLocalizedMessage());
        }
 
        return result;
    }
    
	
	private void addMark() {
		Intent intent = getIntent();
		Bundle bund = intent.getBundleExtra("value");
		ShopInfo shopinfo = (ShopInfo) bund.getSerializable("ShopInfo");
						
		LatLng ll = new LatLng(Double.valueOf(shopinfo.getLatitude()), Double.valueOf(shopinfo.getLongitude()));
		OverlayOptions oo = new MarkerOptions().icon(bd_end).position(ll);
		Marker marker = (Marker)mBaiduMap.addOverlay(oo);
		
		Info info = new Info(Double.valueOf(shopinfo.getLatitude()), Double.valueOf(shopinfo.getLongitude()), 
				shopinfo.getSname(),
				shopinfo.getStel(),
				Integer.valueOf(shopinfo.getSnear()));
		showInfoWindow(info, marker);
	}
	
	private void showInfoWindow(Info info, Marker marker) {
		InfoWindow mInfoWindow;
		
		LinearLayout location = new LinearLayout(getApplicationContext());
		LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		location.setLayoutParams(param);
		
		location.setOrientation(LinearLayout.HORIZONTAL);
		int imgPopup = getApplicationContext().getResources().getIdentifier("popup", "drawable", getApplicationContext().getPackageName());
					
		TextView textname = new TextView(getApplicationContext());
		textname.setGravity(Gravity.CENTER);
		textname.setPadding(10, 10, 10, 10);
		location.addView(textname, param);
		ImageView button = new ImageView(getApplicationContext());
		button.setPadding(10, 10, 10, 10);
		location.addView(button, param);
				
		// 将marker所在的经纬度的信息转化成屏幕上的坐标
		final LatLng ll = marker.getPosition();
		// 为弹出的InfoWindow添加点击事件
		mInfoWindow = new InfoWindow(location, ll, -47);
		// 显示InfoWindow
		mBaiduMap.showInfoWindow(mInfoWindow);
		// 设置详细信息布局为可见
		mMarkerInfoLy.setVisibility(View.VISIBLE);
		// 根据商家信息为详细信息布局设置信息	
		textname.setText(info.getName());
		button.setImageResource(getApplicationContext().getResources().getIdentifier("navi", "drawable", getApplicationContext().getPackageName()));
		location.setBackgroundResource(imgPopup);
		button.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				navi();
			}
		});

		t1.setText(info.getName());
		t2.setText(info.getTel());
		t3.setText("距离" + info.getDistance() + "米");
		
		mDest = new LatLng(info.getLatitude(), info.getLongitude());
		mDestName = info.getName();		
	}
   
    private void navi() {
    	Coordinate2D loc = new Coordinate2D();
    	loc.lat = mDest.latitude;
    	loc.lng = mDest.longitude;
    	Coordinate2D wgs = LocConverter.bd09ToWgs84(loc);
    	Uri myUri = Uri.parse("geo:0,0?q=" + wgs.lat + "," + wgs.lng + "(" + mDestName + ")");
    	Intent intent = new Intent(Intent.ACTION_VIEW, myUri);
    	this.startActivity(intent);    	
    }
}
