package com.lingmo.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.lingmo.Utils.MyJson;
import com.lingmo.adapter.SearchMainAdapter;
import com.lingmo.adapter.SearchMoreAdapter;
import com.lingmo.adapter.ShopAdapter;
import com.lingmo.info.ShopInfo;
import com.lingmo.model.Model;
import com.lingmo.net.ThreadPoolUtils;
import com.lingmo.thread.HttpGetThread;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


/**
 * 店铺列表模块
 * */
public class ShopListActivity extends Activity implements OnScrollListener {

	protected static final int PAGE_SIZE = 6;
	private ListView mListView, mShoplist_toplist, mShoplist_threelist,
			mShoplist_twolist, mShoplist_onelist1,
			mShoplist_twolist1;
	private ImageView mShoplist_back;
	private LinearLayout mShoplist_shanghuleixing,
			mShoplist_mainlist1;
	private TextView mShoplist_title_textbtn1, mShoplist_title_textbtn2,
			mShoplist_title_textbtn3;
	
	private MyJson myJson = new MyJson();
	private List<ShopInfo> list = new ArrayList<ShopInfo>();
	private ShopAdapter mAdapter = null;
	private SearchMoreAdapter topadapter = null;
	private SearchMoreAdapter threeadapter = null;
	private SearchMoreAdapter twoadapter1 = null;
	private SearchMainAdapter oneadapter1 = null;
//	private SearchMoreAdapter twoadapter2 = null;
//	private SearchMainAdapter oneadapter2 = null;
	private SearchMoreAdapter twoadapter = null;

	private ImageView mSearch_city_img;
	private TextView mShoplist_title_txt;
	private int mDataPage = 0;

	private boolean toplistview = false;
	private boolean threelistview = false;
	private boolean twolistview = false;
	private boolean mainlistview1 = false;
	//private boolean mainlistview2 = false;
	private List<Map<String, Object>> mainList1;
	private List<Map<String, Object>> mainList2;
	
	private View loadView;
	private int lastItemIndex;
	private int mTotal = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_shoplist);
		initView();
	}
	
	private int getID(String str) {
		return getResources().getIdentifier(str, "id", getApplicationContext().getPackageName());
	}

	private int getDraw(String str) {
		return getResources().getIdentifier(str, "drawable", getApplicationContext().getPackageName());
	}
	
	private void initView() {
		mShoplist_back = (ImageView) findViewById(getID("Shoplist_back"));
		mShoplist_shanghuleixing = (LinearLayout) findViewById(getID("Shoplist_shanghuleixing"));
		mShoplist_title_txt = (TextView) findViewById(getID("Shoplist_title_txt"));
		mSearch_city_img = (ImageView) findViewById(getID("Search_city_img"));
		mShoplist_title_textbtn1 = (TextView) findViewById(getID("Shoplist_title_textbtn1"));
		mShoplist_title_textbtn2 = (TextView) findViewById(getID("Shoplist_title_textbtn2"));
		mShoplist_title_textbtn3 = (TextView) findViewById(getID("Shoplist_title_textbtn3"));
		mShoplist_toplist = (ListView) findViewById(getID("Shoplist_toplist"));
		mShoplist_mainlist1 = (LinearLayout) findViewById(getID("Shoplist_mainlist1"));
		mShoplist_onelist1 = (ListView) findViewById(getID("Shoplist_onelist1"));
		mShoplist_twolist1 = (ListView) findViewById(getID("Shoplist_twolist1"));
		mShoplist_twolist = (ListView) findViewById(getID("Shoplist_twolist"));
		mShoplist_threelist = (ListView) findViewById(getID("Shoplist_threelist"));
		mListView = (ListView) findViewById(getID("ShopListView"));

		MyOnclickListener mOnclickListener = new MyOnclickListener();
		mShoplist_back.setOnClickListener(mOnclickListener);
		mShoplist_shanghuleixing.setOnClickListener(mOnclickListener);
		mShoplist_title_textbtn1.setOnClickListener(mOnclickListener);
		mShoplist_title_textbtn2.setOnClickListener(mOnclickListener);
		mShoplist_title_textbtn3.setOnClickListener(mOnclickListener);
		// -----------------------------------------------------------------
		initModel1();
		initModel2();
		
		topadapter = new SearchMoreAdapter(ShopListActivity.this, Model.SHOPLIST_TOPLIST, R.layout.shop_list2_item);
		mShoplist_toplist.setAdapter(topadapter);
		
		oneadapter1 = new SearchMainAdapter(ShopListActivity.this, mainList1, R.layout.shop_list1_item, false);
		oneadapter1.setSelectItem(0);
		mShoplist_onelist1.setAdapter(oneadapter1);
		initAdapter1(Model.SHOPLIST_PLACESTREET[0]);
		
//		oneadapter2 = new SearchMainAdapter(ShopListActivity.this, mainList2,R.layout.shop_list1_item,true);
//		oneadapter2.setSelectItem(0);
//		mShoplist_onelist2.setAdapter(oneadapter2);
//		initAdapter2(Model.MORELISTTXT[0]);
		
		twoadapter = new SearchMoreAdapter(ShopListActivity.this, Model.SHOPLIST_TWOLIST, R.layout.shop_list2_item);
		mShoplist_twolist.setAdapter(twoadapter);
		
		threeadapter = new SearchMoreAdapter(ShopListActivity.this, Model.SHOPLIST_THREELIST, R.layout.shop_list2_item);
		mShoplist_threelist.setAdapter(threeadapter);

		TopListOnItemclick topListOnItemclick = new TopListOnItemclick();
		Onelistclick1 onelistclick1 = new Onelistclick1();
		Twolistclick1 twolistclick1 = new Twolistclick1();
//		Onelistclick2 onelistclick2 = new Onelistclick2();
//		Twolistclick2 twolistclick2 = new Twolistclick2();
		TwoListOnItemclick twoListOnItemClick = new TwoListOnItemclick();
		ThreeListOnItemclick threeListOnItemClick = new ThreeListOnItemclick();
		mShoplist_toplist.setOnItemClickListener(topListOnItemclick);
		mShoplist_onelist1.setOnItemClickListener(onelistclick1);
		mShoplist_twolist1.setOnItemClickListener(twolistclick1);
//		mShoplist_onelist2.setOnItemClickListener(onelistclick2);
//		mShoplist_twolist2.setOnItemClickListener(twolistclick2);
		mShoplist_twolist.setOnItemClickListener(twoListOnItemClick);
		mShoplist_threelist.setOnItemClickListener(threeListOnItemClick);
		// -----------------------------------------------------------------
		mAdapter = new ShopAdapter(list, ShopListActivity.this);
		loadView = getLayoutInflater().inflate(R.layout.load, null);
		mListView.addFooterView(loadView);
		loadView.setVisibility(View.GONE);
		mListView.setAdapter(mAdapter);
		mListView.setOnItemClickListener(new MainListOnItemClickListener());
		// 滑动获取更新，这个更适合一般的常见的操作方法,this是因为集成了相应的接口可以直接这样写，然后必须有其全部实现
		mListView.setOnScrollListener(this);
		// 拼接字符串操作
		sendReq();
	}

	private class MyOnclickListener implements View.OnClickListener {
		public void onClick(View v) {
			int mID = v.getId();

			if (mID == R.id.Shoplist_back) {
				ShopListActivity.this.finish();
			}
			if (mID == R.id.Shoplist_shanghuleixing) {
				if (!toplistview) {
					mSearch_city_img
							.setImageResource(getDraw("title_arrow_up"));
					mShoplist_toplist.setVisibility(View.VISIBLE);
					topadapter.notifyDataSetChanged();
					toplistview = true;
				} else {
					mSearch_city_img.setImageResource(getDraw("search_city"));
					mShoplist_toplist.setVisibility(View.GONE);
					toplistview = false;
				}
			} else {
				mSearch_city_img.setImageResource(getDraw("search_city"));
				mShoplist_toplist.setVisibility(View.GONE);
				toplistview = false;
			}
			if (mID == R.id.Shoplist_title_textbtn3) {
				Drawable drawable = null;
				if (!threelistview) {
					drawable = getResources().getDrawable(
							getDraw("ic_arrow_up_black"));
					mShoplist_threelist.setVisibility(View.VISIBLE);
					threeadapter.notifyDataSetChanged();
					threelistview = true;
				} else {
					drawable = getResources().getDrawable(
							getDraw("ic_arrow_down_black"));
					mShoplist_threelist.setVisibility(View.GONE);
					threelistview = false;
				}
				// 这一步必须要做,否则不会显示.
				drawable.setBounds(0, 0, drawable.getMinimumWidth(),
						drawable.getMinimumHeight());
				mShoplist_title_textbtn3.setCompoundDrawables(null, null,
						drawable, null);
			} else {
				Drawable drawable = getResources().getDrawable(
						getDraw("ic_arrow_down_black"));
				drawable.setBounds(0, 0, drawable.getMinimumWidth(),
						drawable.getMinimumHeight());
				mShoplist_title_textbtn3.setCompoundDrawables(null, null,
						drawable, null);
				mShoplist_threelist.setVisibility(View.GONE);
				threelistview = false;

			}
			if (mID == R.id.Shoplist_title_textbtn2) {
				Drawable drawable = null;
//				if (!mainlistview2) {
//					drawable = getResources().getDrawable(
//							getDraw("ic_arrow_up_black"));
//					mShoplist_mainlist2.setVisibility(View.VISIBLE);
//					twoadapter2.notifyDataSetChanged();
//					mainlistview2 = true;
//				} else {
//					drawable = getResources().getDrawable(
//							getDraw("ic_arrow_down_black"));
//					mShoplist_mainlist2.setVisibility(View.GONE);
//					mainlistview2 = false;
//				}
//				// 这一步必须要做,否则不会显示.
//				drawable.setBounds(0, 0, drawable.getMinimumWidth(),
//						drawable.getMinimumHeight());
//				mShoplist_title_textbtn2.setCompoundDrawables(null, null,
//						drawable, null);
				if (!twolistview) {
					drawable = getResources().getDrawable(
							getDraw("ic_arrow_up_black"));
					mShoplist_twolist.setVisibility(View.VISIBLE);
					twoadapter.notifyDataSetChanged();
					twolistview = true;
				} else {
					drawable = getResources().getDrawable(
							getDraw("ic_arrow_down_black"));
					mShoplist_twolist.setVisibility(View.GONE);
					twolistview = false;
				}
				// 这一步必须要做,否则不会显示.
				drawable.setBounds(0, 0, drawable.getMinimumWidth(),
						drawable.getMinimumHeight());
				mShoplist_title_textbtn2.setCompoundDrawables(null, null,
						drawable, null);
			} else {
				Drawable drawable = getResources().getDrawable(
						getDraw("ic_arrow_down_black"));
				drawable.setBounds(0, 0, drawable.getMinimumWidth(),
						drawable.getMinimumHeight());
				mShoplist_title_textbtn2.setCompoundDrawables(null, null,
						drawable, null);
//				mShoplist_mainlist2.setVisibility(View.GONE);
				mShoplist_twolist.setVisibility(View.GONE);
//				mainlistview2 = false;
				twolistview = false;
			}
			if (mID == R.id.Shoplist_title_textbtn1) {
				Drawable drawable = null;
				if (!mainlistview1) {
					drawable = getResources().getDrawable(
							getDraw("ic_arrow_up_black"));
					mShoplist_mainlist1.setVisibility(View.VISIBLE);
					twoadapter1.notifyDataSetChanged();
					mainlistview1 = true;
				} else {
					drawable = getResources().getDrawable(
							getDraw("ic_arrow_down_black"));
					mShoplist_mainlist1.setVisibility(View.GONE);
					mainlistview1 = false;
				}
				// 这一步必须要做,否则不会显示.
				drawable.setBounds(0, 0, drawable.getMinimumWidth(),
						drawable.getMinimumHeight());
				mShoplist_title_textbtn1.setCompoundDrawables(null, null,
						drawable, null);
			} else {
				Drawable drawable = getResources().getDrawable(
						getDraw("ic_arrow_down_black"));
				drawable.setBounds(0, 0, drawable.getMinimumWidth(),
						drawable.getMinimumHeight());
				mShoplist_title_textbtn1.setCompoundDrawables(null, null,
						drawable, null);
				mShoplist_mainlist1.setVisibility(View.GONE);
				mainlistview1 = false;
			}
		}
	}

	@SuppressLint("HandlerLeak") 
	Handler hand = new Handler() {
		@SuppressLint({ "HandlerLeak", "ShowToast" }) 
		public void handleMessage(android.os.Message msg) {
			super.handleMessage(msg);
			if (msg.what == 404) {
				Toast.makeText(ShopListActivity.this, "找不到地址", 1).show();
			} else if (msg.what == 100) {
				Toast.makeText(ShopListActivity.this, "传输失败", 1).show();
			} else if (msg.what == 200) {
				String result = (String) msg.obj;
				// 在activity当中获取网络交互的数据
				if (result != null) {
					// 1次网络请求返回的数据
					List<ShopInfo> newList = myJson.getShopList(result);
					if (mDataPage == 0) {
						mTotal = myJson.getTotal(result);
					}
					if (newList != null) {
						if (newList.size() == PAGE_SIZE) {
							++mDataPage;
						} 
						for (ShopInfo info : newList) {
							list.add(info);
						}
						if (mDataPage == 0 &&
						    list.size() == mTotal) {
							mListView.removeFooterView(loadView);
						}
						mAdapter.notifyDataSetChanged();
					}
				}
				mAdapter.notifyDataSetChanged();
			}
		};
	};

	private class MainListOnItemClickListener implements OnItemClickListener {
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			Intent intent = new Intent(ShopListActivity.this, ShopDetailsActivity.class);
			Bundle bund = new Bundle();
			bund.putSerializable("ShopInfo",list.get(arg2));
			intent.putExtra("value",bund);
			startActivity(intent);
		}
	}

	private class TopListOnItemclick implements OnItemClickListener {
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			topadapter.setSelectItem(arg2);
			mSearch_city_img.setImageResource(getDraw("search_city"));
			mShoplist_title_txt.setText(Model.SHOPLIST_TOPLIST[arg2]);
			mShoplist_toplist.setVisibility(View.GONE);
			toplistview = false;
			
			//TODO - update the shop list with the condition changing
		}
	}

	private class Onelistclick1 implements OnItemClickListener {
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			initAdapter1(Model.SHOPLIST_PLACESTREET[arg2]);
			oneadapter1.setSelectItem(arg2);
			oneadapter1.notifyDataSetChanged();
		}
	}

	private class Twolistclick1 implements OnItemClickListener {
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			twoadapter1.setSelectItem(arg2);
			Drawable drawable = getResources().getDrawable(
					getDraw("ic_arrow_down_black"));
			drawable.setBounds(0, 0, drawable.getMinimumWidth(),
					drawable.getMinimumHeight());
			mShoplist_title_textbtn1.setCompoundDrawables(null, null, drawable,
					null);
			int position = oneadapter1.getSelectItem();
			mShoplist_title_textbtn1
					.setText(Model.SHOPLIST_PLACESTREET[position][arg2]);
			mShoplist_mainlist1.setVisibility(View.GONE);
			mainlistview1 = false;
			
			//TODO - update the shop list with the condition changing
		}
	}

//	private class Onelistclick2 implements OnItemClickListener {
//		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
//				long arg3) {
//			initAdapter2(Model.MORELISTTXT[arg2]);
//			oneadapter2.setSelectItem(arg2);
//			oneadapter2.notifyDataSetChanged();
//		}
//	}
//
//	private class Twolistclick2 implements OnItemClickListener {
//		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
//				long arg3) {
//			twoadapter2.setSelectItem(arg2);
//			Drawable drawable = getResources().getDrawable(
//					getDraw("ic_arrow_down_black"));
//			drawable.setBounds(0, 0, drawable.getMinimumWidth(),
//					drawable.getMinimumHeight());
//			mShoplist_title_textbtn2.setCompoundDrawables(null, null, drawable,
//					null);
//			int position = oneadapter2.getSelectItem();
//			mShoplist_title_textbtn2.setText(Model.MORELISTTXT[position][arg2]);
//			mShoplist_mainlist2.setVisibility(View.GONE);
//			mainlistview2 = false;
//			
//			//TODO - update the shop list with the condition changing
//		}
//	}

	private void initModel1() {
		mainList1 = new ArrayList<Map<String, Object>>();
		for (int i = 0; i < Model.SHOPLIST_PLACE.length; i++) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("txt", Model.SHOPLIST_PLACE[i]);
			mainList1.add(map);
		}
	}

	private void initModel2() {
		mainList2 = new ArrayList<Map<String, Object>>();
		for (int i = 0; i < Model.LISTVIEWTXT.length; i++) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("img", Model.LISTVIEWIMG[i]);
			map.put("txt", Model.LISTVIEWTXT[i]);
			mainList2.add(map);
		}
	}

	private void initAdapter1(String[] array) {
		twoadapter1 = new SearchMoreAdapter(ShopListActivity.this, array, R.layout.shop_list2_item);
		mShoplist_twolist1.setAdapter(twoadapter1);
		twoadapter1.notifyDataSetChanged();
	}

//	private void initAdapter2(String[] array) {
//		twoadapter2 = new SearchMoreAdapter(ShopListActivity.this, array,R.layout.shop_list2_item);
//		mShoplist_twolist2.setAdapter(twoadapter2);
//		twoadapter2.notifyDataSetChanged();
//	}
	
	private class TwoListOnItemclick implements OnItemClickListener {
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			twoadapter.setSelectItem(arg2);
			Drawable drawable = getResources().getDrawable(
					getDraw("ic_arrow_down_black"));
			drawable.setBounds(0, 0, drawable.getMinimumWidth(),
					drawable.getMinimumHeight());
			mShoplist_title_textbtn2.setCompoundDrawables(null, null, drawable,
					null);
			mShoplist_title_textbtn2.setText(Model.SHOPLIST_TWOLIST[arg2]);
			mShoplist_twolist.setVisibility(View.GONE);
			twolistview = false;
			
			//TODO - update the shop list with the condition changing
		}
	}

	private class ThreeListOnItemclick implements OnItemClickListener {
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			threeadapter.setSelectItem(arg2);
			Drawable drawable = getResources().getDrawable(
					getDraw("ic_arrow_down_black"));
			drawable.setBounds(0, 0, drawable.getMinimumWidth(),
					drawable.getMinimumHeight());
			mShoplist_title_textbtn3.setCompoundDrawables(null, null, drawable,
					null);
			mShoplist_title_textbtn3.setText(Model.SHOPLIST_THREELIST[arg2]);
			mShoplist_threelist.setVisibility(View.GONE);
			threelistview = false;
			
			//TODO - update the shop list with the condition changing
		}
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (toplistview == true) {
				mSearch_city_img.setImageResource(getDraw("search_city"));
				mShoplist_toplist.setVisibility(View.GONE);
				toplistview = false;
			} else if (threelistview == true) {
				Drawable drawable = getResources().getDrawable(
						getDraw("ic_arrow_down_black"));
				drawable.setBounds(0, 0, drawable.getMinimumWidth(),
						drawable.getMinimumHeight());
				mShoplist_title_textbtn3.setCompoundDrawables(null, null,
						drawable, null);
				mShoplist_threelist.setVisibility(View.GONE);
				threelistview = false;
			} else if (mainlistview1 == true) {
				Drawable drawable = getResources().getDrawable(
						getDraw("ic_arrow_down_black"));
				drawable.setBounds(0, 0, drawable.getMinimumWidth(),
						drawable.getMinimumHeight());
				mShoplist_title_textbtn1.setCompoundDrawables(null, null,
						drawable, null);
				mShoplist_mainlist1.setVisibility(View.GONE);
				mainlistview1 = false;
			} else if (twolistview == true) {
				Drawable drawable = getResources().getDrawable(
						getDraw("ic_arrow_down_black"));
				drawable.setBounds(0, 0, drawable.getMinimumWidth(),
						drawable.getMinimumHeight());
				mShoplist_title_textbtn2.setCompoundDrawables(null, null,
						drawable, null);
//				mShoplist_mainlist2.setVisibility(View.GONE);
				mShoplist_twolist.setVisibility(View.GONE);
				twolistview = false;
			} else {
				ShopListActivity.this.finish();
			}
		}
		return false;
	}
	
	//滑动到底部
	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		Log.d("ShopList", "lastItemIndex: " + lastItemIndex);
		Log.d("ShopList", "mTotal: " + mTotal);
		if (lastItemIndex == mAdapter.getCount() 
				&& scrollState == OnScrollListener.SCROLL_STATE_IDLE 
				&& lastItemIndex < mTotal) {
			Log.d("ShopList", "拉到最底部");
			loadView.setVisibility(View.VISIBLE);
			sendReq();
		}
		if (lastItemIndex >= mTotal){
			Toast.makeText(getApplicationContext(), "没有更多了", Toast.LENGTH_SHORT).show();
			mListView.removeFooterView(loadView);
		}
	}
	
	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		Log.d("ShopList", "onScroll(firstVisibleItem="  
                + firstVisibleItem + ",visibleItemCount="  
                + visibleItemCount + ",totalItemCount=" + totalItemCount  
                + ")");  
		lastItemIndex = firstVisibleItem + visibleItemCount - 1;
	}
	
	private void sendReq() {
		String url = "http://api.map.baidu.com/geosearch/v3/nearby?ak=GiwfC8cmUBxyVyHAA8RTbaDD&geotable_id=99654&location=113.955933,22.555938&radius=4000&mcode=com.xuwei.locMap&page_size=" + PAGE_SIZE + "&page_index=" + mDataPage;
		ThreadPoolUtils.execute(new HttpGetThread(hand, url));
	}
}
