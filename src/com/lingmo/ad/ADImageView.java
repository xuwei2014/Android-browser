package com.lingmo.ad;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import com.lingmo.activity.R;
import com.unity3d.player.UnityPlayer;

import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.ImageView;

public class ADImageView {

	private Dialog mDialog;
	private MyCount mc;
	private ImageView mImageView;
	Bitmap bitmap;
	private int mSeconds;

	public ADImageView(Context context, final String name, int width, int height, int sec, String url) {
		mDialog = new Dialog(context, R.style.CommonDialog);
		mDialog.setContentView(R.layout.image_view);
		mDialog.setCancelable(false);
		
		WindowManager.LayoutParams lp = mDialog.getWindow().getAttributes();
		lp.width = width;
		lp.height = height;
		mDialog.getWindow().setAttributes(lp);		

		mImageView = (ImageView) mDialog.findViewById(R.id.img);
		mImageView.setImageResource(R.drawable.home_button);
		mImageView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Log.d("ADImage", "on the clicking!");
				mDialog.dismiss();
				// Callback to Unity
				UnityPlayer.UnitySendMessage(name, "OnClickADImage", "");
			}

		});
		
		mSeconds = sec;

		Log.d("ADImage", "It's Okay!");
		new Task().execute(url);
	}

	class MyCount extends CountDownTimer {
		public MyCount(long millisInFuture, long countDownInterval) {
			super(millisInFuture, countDownInterval);
		}

		@Override
		public void onFinish() {
			mDialog.dismiss();
		}

		@Override
		public void onTick(long millisUntilFinished) {

		}
	}

	/**
	 * 获取网落图片资源
	 * 
	 * @param url
	 * @return
	 */
	public static Bitmap getHttpBitmap(String url) {
		URL myFileURL;
		Bitmap bitmap = null;
		try {
			myFileURL = new URL(url);
			// 获得连接
			HttpURLConnection conn = (HttpURLConnection) myFileURL
					.openConnection();
			// 设置超时时间为6000毫秒，conn.setConnectionTiem(0);表示没有时间限制
			conn.setConnectTimeout(6000);
			// 连接设置获得数据流
			conn.setDoInput(true);
			// 不使用缓存
			conn.setUseCaches(false);
			// 这句可有可无，没有影响
			conn.connect();
			// 得到数据流
			InputStream is = conn.getInputStream();
			// 解析得到图片
			bitmap = BitmapFactory.decodeStream(is);
			// 关闭数据流
			is.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return bitmap;
	}

	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case 0:
				mImageView.setImageBitmap(bitmap);
				mDialog.show();
				mc = new MyCount(mSeconds * 1000, 1000);
				mc.start();
				break;

			default:
				break;
			}
		}
	};

	class Task extends AsyncTask<String, Integer, String> {

		@Override
		protected String doInBackground(String... arg0) {
			//String url = "http://lmsj-assets.oss-cn-qingdao.aliyuncs.com/image/Demo_HBTR.jpg";
			Log.d("ADImage", "url:" + arg0[0]);
			bitmap = getHttpBitmap(arg0[0]);
			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			Message msg = new Message();
			msg.what = 0;
			handler.sendMessage(msg);
		}

	}

}

