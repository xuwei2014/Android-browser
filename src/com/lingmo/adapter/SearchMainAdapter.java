package com.lingmo.adapter;

import java.util.List;
import java.util.Map;

import com.lingmo.activity.R;



import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;

import android.widget.TextView;

/**
 * �����еĸ���Ľ��������listview��������
 * @author ��ɬ
 *</BR> </BR> By����ɬ </BR> ��ϵ���ߣ�QQ 534429149
 */

public class SearchMainAdapter extends BaseAdapter {

	private Context ctx;
	private List<Map<String, Object>> list;
	private int position = 0;
	private boolean islodingimg = true;
	private int layout = R.layout.search_more_mainlist_item;

	public SearchMainAdapter(Context ctx, List<Map<String, Object>> list) {
		this.ctx = ctx;
		this.list = list;
	}

	public SearchMainAdapter(Context ctx, List<Map<String, Object>> list,
			int layout, boolean islodingimg) {
		this.ctx = ctx;
		this.list = list;
		this.layout = layout;
		this.islodingimg = islodingimg;
	}

	public int getCount() {
		return list.size();
	}

	public Object getItem(int arg0) {
		return list.get(arg0);
	}

	public long getItemId(int arg0) {
		return arg0;
	}

	public View getView(int arg0, View arg1, ViewGroup arg2) {
		Holder hold;
		if (arg1 == null) {
			hold = new Holder();
			arg1 = View.inflate(ctx, layout, null);
			hold.txt = (TextView) arg1
					.findViewById(R.id.Search_more_mainitem_txt);
			hold.img = (ImageView) arg1
					.findViewById(R.id.Search_more_mainitem_img);
			hold.layout = (LinearLayout) arg1
					.findViewById(R.id.Search_more_mainitem_layout);
			arg1.setTag(hold);
		} else {
			hold = (Holder) arg1.getTag();
		}
		if(islodingimg == true){
			hold.img.setImageResource(Integer.parseInt(list.get(arg0).get("img")
					.toString()));
		}
		hold.txt.setText(list.get(arg0).get("txt").toString());
		hold.layout.setBackgroundResource(ctx.getResources().getIdentifier("search_more_mainlistselect", "drawable", ctx.getApplicationContext().getPackageName()));
		if (arg0 == position) {
			hold.layout.setBackgroundResource(ctx.getResources().getIdentifier("list_bkg_line_u", "drawable", ctx.getApplicationContext().getPackageName()));
		}
		return arg1;
	}

	public void setSelectItem(int i) {
		position = i;
	}

	public int getSelectItem() {
		return position;
	}

	private static class Holder {
		LinearLayout layout;
		ImageView img;
		TextView txt;
	}

}
