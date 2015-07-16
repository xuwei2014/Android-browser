package com.lingmo.activity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.BaiduMap.OnMapClickListener;
import com.baidu.mapapi.map.BaiduMap.OnMarkerClickListener;
import com.baidu.mapapi.map.InfoWindow.OnInfoWindowClickListener;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
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
	BitmapDescriptor[] bds;
	
    LatLng mLoc = null;
    LatLng mDest = null;
    String mDestName = null;
    
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_location);
		
		buildMarks();
		
		mMarkerInfoLy = (RelativeLayout)findViewById(R.id.detail_info);
		t1 = (TextView)findViewById(R.id.info_name);
		t2 = (TextView)findViewById(R.id.info_phoneno);
		t3 = (TextView)findViewById(R.id.info_distance);

		// 地图初始化
		mMapView = (MapView) findViewById(R.id.bmapView);
		mBaiduMap = mMapView.getMap();
		MapStatusUpdate msu = MapStatusUpdateFactory.zoomTo(14.0f);
		
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
		
		mBaiduMap.setMapStatus(msu);

		initMarkerClickEvent();
		initMapClickEvent();
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
			MyLocationData locData = new MyLocationData.Builder()
					.accuracy(location.getRadius())
					// 此处设置开发者获取到的方向信息，顺时针0-360
					.direction(100).latitude(location.getLatitude())
					.longitude(location.getLongitude()).build();
			mBaiduMap.setMyLocationData(locData);
			if (isFirstLoc) {
				isFirstLoc = false;
				LatLng ll = new LatLng(location.getLatitude(),
						location.getLongitude());
				mLoc = ll;
				MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);
				mBaiduMap.animateMapStatus(u);
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
    
	private void initMapClickEvent()
	{
		mBaiduMap.setOnMapClickListener(new OnMapClickListener()
		{

			@Override
			public boolean onMapPoiClick(MapPoi arg0)
			{
				return false;
			}

			@Override
			public void onMapClick(LatLng arg0)
			{
				mMarkerInfoLy.setVisibility(View.GONE);
				mBaiduMap.hideInfoWindow();

			}
		});
	}

	private void initMarkerClickEvent()
	{
		// 对Marker的点击
		mBaiduMap.setOnMarkerClickListener(new OnMarkerClickListener()
		{
			@Override
			public boolean onMarkerClick(final Marker marker)
			{
				if (marker == null || marker.getExtraInfo() == null) {
					return false;
				}
				// 获得marker中的数据
				Info info = (Info) marker.getExtraInfo().get("info");
				if (info == null) {
					return false;
				}

				InfoWindow mInfoWindow;
				// 生成一个TextView用户在地图中显示InfoWindow
				TextView location = new TextView(getApplicationContext());
				int imgPopup = getApplicationContext().getResources().getIdentifier("popup", "drawable", getApplicationContext().getPackageName());
				location.setBackgroundResource(imgPopup);
				location.setPadding(30, 20, 30, 50);
				location.setText(info.getName());
				// 将marker所在的经纬度的信息转化成屏幕上的坐标
				final LatLng ll = marker.getPosition();
				Point p = mBaiduMap.getProjection().toScreenLocation(ll);
				p.y -= 47;
				LatLng llInfo = mBaiduMap.getProjection().fromScreenLocation(p);
				// 为弹出的InfoWindow添加点击事件
				mInfoWindow = new InfoWindow(BitmapDescriptorFactory.fromView(location), llInfo, -47,
						new OnInfoWindowClickListener()
						{

							@Override
							public void onInfoWindowClick()
							{
								// 隐藏InfoWindow
								mBaiduMap.hideInfoWindow();
								mMarkerInfoLy.setVisibility(View.GONE);
							}
						});
				// 显示InfoWindow
				mBaiduMap.showInfoWindow(mInfoWindow);
				// 设置详细信息布局为可见
				mMarkerInfoLy.setVisibility(View.VISIBLE);
				// 根据商家信息为详细信息布局设置信息			
				t1.setText(info.getName());
				t2.setText(info.getTel());
				t3.setText("距离" + info.getDistance() + "米");
				
				mDest = new LatLng(info.getLatitude(), info.getLongitude());
				mDestName = info.getName();
				
				return true;
			}
		});
	}
	
	private void buildMarks() {
		bds = new BitmapDescriptor[10];
		String makers[] = {
				"icon_marka",
				"icon_markb",
				"icon_markc",
				"icon_markd",
				"icon_marke",
				"icon_markf",
				"icon_markg",
				"icon_markh",
				"icon_marki",
				"icon_markj"
		};
		
		int imgMark;
		Context context = getApplicationContext();
		for (int i = 0; i < makers.length; ++i) {
			imgMark = context.getResources().getIdentifier(makers[i], "drawable", context.getPackageName());
			bds[i] = BitmapDescriptorFactory.fromResource(imgMark);
		}	
	}
	
	private void addMark() {
		Intent intent = getIntent();
		Bundle bund = intent.getBundleExtra("value");
		ShopInfo shopinfo = (ShopInfo) bund.getSerializable("ShopInfo");
		
		LatLng ll = new LatLng(Double.valueOf(shopinfo.getLatitude()), Double.valueOf(shopinfo.getLongitude()));
		OverlayOptions oo = new MarkerOptions().icon(bds[0]).position(ll);
		Marker marker = (Marker)mBaiduMap.addOverlay(oo);
		Info info = new Info(Double.valueOf(shopinfo.getLatitude()), Double.valueOf(shopinfo.getLongitude()), 
				shopinfo.getSname(),
				shopinfo.getStel(),
				Integer.valueOf(shopinfo.getSnear()));
		Bundle bundle = new Bundle();
		bundle.putSerializable("info", info);
		marker.setExtraInfo(bundle);
	}
	
	public void infoButtonProcess(View v) {	
//        if (v.getId() == R.id.virtualshop) {
//        	Log.d("LBS", "shopping!!!");
//        } else 
		if (v.getId() == R.id.navigation) {
			Log.d("LBS", "other map!!!");
			navi();
		}
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
