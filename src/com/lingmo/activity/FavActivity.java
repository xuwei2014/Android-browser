package com.lingmo.activity;

import com.lingmo.Utils.FavoritesManager;
import com.lingmo.Utils.ItemLongClickedPopWindow;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class FavActivity extends Activity{

	private static final String DEG_TAG = "webbrowser_FavActivity";
	
	//收藏历史的内容
	private ListView favoritesContent;
	
	//popupwindow弹窗
	private ItemLongClickedPopWindow itemLongClickedPopWindow;
	
	//书签管理
	private FavoritesManager favoritesManager;
	
	//Cursor
	private Cursor favoritesCursor;
	
	//Adapter
	private ListAdapter favorietesAdapter;
	
	//监听
	private ListViewOnItemLongListener itemLongListener;
	private ListViewOnItemClickedListener itemClickListener;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.view_favorites);
		
		//初始化
		this.favoritesContent = (ListView) this.findViewById(R.id.favorites_content_favorite);
		
		this.itemLongListener = new ListViewOnItemLongListener();
		this.itemClickListener = new ListViewOnItemClickedListener();		
		
		this.favoritesContent.setOnItemLongClickListener(this.itemLongListener);
		this.favoritesContent.setOnItemClickListener(this.itemClickListener);
		
		//初始化数据
		this.initData();
		
		setResult(-1);
	}
	
	/**
	 * 初始化ListView中的数据
	 * */
	private void initData() {
		//获取书签管理
		this.favoritesManager = new FavoritesManager(this);
		this.favoritesCursor = this.favoritesManager.getAllFavorites();
		/*
		this.favorietesAdapter = new SimpleCursorAdapter(getApplicationContext(), 
				R.layout.list_item, this.favoritesCursor, 
				new String[]{"_id","name","url"}, 
				new int[]{R.id.item_id, R.id.item_name,R.id.item_url});
				*/
		this.favorietesAdapter = new MyCursorAdapter(getApplicationContext(), this.favoritesCursor);
		this.favoritesContent.setAdapter(this.favorietesAdapter);
	}
	
	class MyCursorAdapter extends SimpleCursorAdapter {
		
		@SuppressWarnings("deprecation")
		public MyCursorAdapter(Context context, Cursor c) {
			super(context, R.layout.list_item, c,
					new String[]{"_id","name","url"},
					new int[]{R.id.item_id, R.id.item_name, R.id.item_url});
		}
		
		@Override
		public void bindView(View view, Context context, Cursor cursor) {
			super.bindView(view, context, cursor);

			ImageView iconImg = (ImageView)view.findViewById(R.id.item_icon);
			
			byte[] icon = cursor.getBlob(cursor.getColumnIndex("icon"));
			if (icon != null) {
				Log.d(DEG_TAG, "bindView set image!!!!");
				iconImg.setImageBitmap(BitmapFactory.decodeByteArray(icon, 0, icon.length));								
			}
		}
	}

	/**
	 * 长按单项事件
	 * 覆盖如下方法
	 * 1.	onItemLongClick
	 * */
	private class ListViewOnItemLongListener implements OnItemLongClickListener{

		@Override
		public boolean onItemLongClick(AdapterView<?> parent, View view,
				int position, long id) {
			Log.d(DEG_TAG, "long item cliced");
			if(parent.getId()==R.id.favorites_content_favorite){
				itemLongClickedPopWindow = new ItemLongClickedPopWindow(FavActivity.this, 500, 500);
				itemLongClickedPopWindow.setBackgroundDrawable(getResources().getDrawable(R.drawable.favandhis_activity));
				itemLongClickedPopWindow.showAsDropDown(view, view.getWidth()/2, -view.getHeight()/2);
				TextView modifyFavorite = (TextView) itemLongClickedPopWindow.getView(R.id.item_longclicked_modifyFavorites);
				TextView deleteFavorite = (TextView) itemLongClickedPopWindow.getView(R.id.item_longclicked_deleteFavorites);
				ItemClickedListener itemClickedListener = new ItemClickedListener(view);
				modifyFavorite.setOnClickListener(itemClickedListener);
				deleteFavorite.setOnClickListener(itemClickedListener);			
			}
			return true;
		}
		
	}
	
	/**
	 * ListView单击单项事件
	 * 覆盖如下方法
	 * 1.	onClick
	 * */
	private class ListViewOnItemClickedListener implements OnItemClickListener{

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			if(arg0.getId()==R.id.favorites_content_favorite){
				Intent intent = new Intent();
				Log.d(DEG_TAG, "favorites url: " + ((TextView) arg1.findViewById(R.id.item_url)).getText().toString());
				intent.putExtra("url", ((TextView) arg1.findViewById(R.id.item_url)).getText().toString());
				
				setResult(0, intent);
				finish();
			}
		}
		
	}	

	/**
	 * popupwindow按钮事件处理类
	 * @param	view	传入的ListView条目
	 * 		用来获取其中的id、name、url这三个值
	 * 覆盖如下方法：
	 * 1.	onClick
	 * */
	private class ItemClickedListener implements OnClickListener{
		
		private String item_id;
		private String item_name;
		private String item_url;
		
		public ItemClickedListener(View item){
			this.item_id = ((TextView) item.findViewById(R.id.item_id)).getText().toString();
			this.item_name = ((TextView) item.findViewById(R.id.item_name)).getText().toString();
			this.item_url = ((TextView) item.findViewById(R.id.item_url)).getText().toString();
		}

		@Override
		public void onClick(View view) {
			//取消弹窗
			itemLongClickedPopWindow.dismiss();
			if(view.getId()==R.id.item_longclicked_modifyFavorites){
				//弹出修改窗口
				LayoutInflater modifyFavoritesInflater = LayoutInflater.from(FavActivity.this);
				View modifyFavoritesView = modifyFavoritesInflater.inflate(R.layout.dialog_modify, null);
				final TextView item_name_input = (TextView) modifyFavoritesView.findViewById(R.id.dialog_name_input);
				final TextView item_url_input = (TextView) modifyFavoritesView.findViewById(R.id.dialog_url_input);
				item_name_input.setText(item_name);
				item_url_input.setText(item_url);
				new AlertDialog.Builder(FavActivity.this)
					.setTitle("编辑书签")
					.setView(modifyFavoritesView)
					.setPositiveButton("确定", new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							Log.d(DEG_TAG, "id:"+item_id+",name:"+item_name+",url:"+item_url);
							if(favoritesManager.modifyFavorite(item_id, item_name_input.getText().toString(), 
									item_url_input.getText().toString())){
								Toast.makeText(FavActivity.this, "修改成功", Gravity.BOTTOM).show();
								initData();
								favoritesContent.invalidate();
							}else{
								Toast.makeText(FavActivity.this, "修改失败", Gravity.BOTTOM).show();
							}
						}
						
					}).setNegativeButton("取消", null)
					.create()
					.show();
			}else if(view.getId()==R.id.item_longclicked_deleteFavorites){
				new AlertDialog.Builder(FavActivity.this)
					.setTitle("删除书签")
					.setMessage("是否要删除\""+item_name+"\"这个书签？")
					.setPositiveButton("删除", new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							if(favoritesManager.deleteFavorite(item_id)){
								//删除成功
								Toast.makeText(FavActivity.this, "删除成功", Gravity.BOTTOM).show();
								initData();
								favoritesContent.invalidate();
							}else{
								Toast.makeText(FavActivity.this, "删除失败", Gravity.BOTTOM).show();
							}
						}
					})
					.setNegativeButton("取消", null)
					.create()
					.show();
			}
				
		}
		
	}

	@Override
	protected void onDestroy() {
		if (this.favoritesCursor != null) {    
			this.favoritesCursor.close();    
	    } 
		super.onDestroy();
	}
	
}
