package com.lingmo.adapter;

import java.util.List;

import com.lingmo.Utils.LoadImg;
import com.lingmo.Utils.LoadImg.ImageDownloadCallBack;
import com.lingmo.activity.R;
import com.lingmo.info.ShopInfo;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;


public class ShopAdapter extends BaseAdapter {

	private List<ShopInfo> list;
	private Context ctx;
	private LoadImg loadImg;

	public ShopAdapter(List<ShopInfo> list, Context ctx) {
		this.list = list;
		this.ctx = ctx;
		// ʵ������ȡͼƬ����
		loadImg = new LoadImg(ctx);
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int arg0) {
		return list.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public View getView(final int arg0, View arg1, ViewGroup arg2) {
		final Holder hold;
		if (arg1 == null) {
			hold = new Holder();
			arg1 = View.inflate(ctx, R.layout.item_shop, null);
			hold.mTitle = (TextView) arg1.findViewById(R.id.ShopItemTextView);
			hold.mImage = (ImageView) arg1.findViewById(R.id.ShopItemImage);
			hold.mMoney = (TextView) arg1.findViewById(R.id.ShopItemMoney);
			hold.mAddress = (TextView) arg1.findViewById(R.id.ShopItemAddress);
			hold.mStytle = (TextView) arg1.findViewById(R.id.ShopItemStytle);
			hold.mDist = (TextView) arg1.findViewById(R.id.ShopItemJuli);
			hold.mStar = (ImageView) arg1.findViewById(R.id.ShopItemStar);
			hold.mTuan = (ImageView) arg1.findViewById(R.id.ShopItemTuan);
			hold.mQuan = (ImageView) arg1.findViewById(R.id.ShopItemQuan);
			hold.mDing = (ImageView) arg1.findViewById(R.id.ShopItemDing);
			hold.mCard = (ImageView) arg1.findViewById(R.id.ShopItemCard);

			arg1.setTag(hold);
		} else {
			hold = (Holder) arg1.getTag();
		}
		hold.mTitle.setText(list.get(arg0).getSname());
		hold.mImage.setTag("http://lmsj-assets.oss-cn-qingdao.aliyuncs.com/ADimage/m.jpg");
		hold.mMoney.setText(list.get(arg0).getSmoney());
		hold.mAddress.setText(list.get(arg0).getSdistrict());
		hold.mStytle.setText(list.get(arg0).getStype());
		hold.mDist.setText(list.get(arg0).getSnear() + "m");
		hold.mTuan.setVisibility(View.GONE);
		hold.mQuan.setVisibility(View.GONE);
		hold.mDing.setVisibility(View.GONE);
		hold.mCard.setVisibility(View.GONE);
		if (list.get(arg0).getSflag_tuan().equals("1")) {
			hold.mTuan.setVisibility(View.VISIBLE);
		}
		if (list.get(arg0).getSflag_quan().equals("1")) {
			hold.mQuan.setVisibility(View.VISIBLE);
		}
		if (list.get(arg0).getSflag_ding().equals("1")) {
			hold.mDing.setVisibility(View.VISIBLE);
		}
		if (list.get(arg0).getSflag_ka().equals("1")) {
			hold.mCard.setVisibility(View.VISIBLE);
		}

		int slevel = Integer.valueOf(list.get(arg0).getSlevel());
		switch (slevel) {
		case 0:
			hold.mStar.setImageResource(ctx.getResources().getIdentifier("star0", "drawable", ctx.getApplicationContext().getPackageName()));
			break;
		case 1:
			hold.mStar.setImageResource(ctx.getResources().getIdentifier("star1", "drawable", ctx.getApplicationContext().getPackageName()));
			break;
		case 2:
			hold.mStar.setImageResource(ctx.getResources().getIdentifier("star2", "drawable", ctx.getApplicationContext().getPackageName()));
			break;
		case 3:
			hold.mStar.setImageResource(ctx.getResources().getIdentifier("star3", "drawable", ctx.getApplicationContext().getPackageName()));
			break;
		case 4:
			hold.mStar.setImageResource(ctx.getResources().getIdentifier("star4", "drawable", ctx.getApplicationContext().getPackageName()));
			break;
		case 5:
			hold.mStar.setImageResource(ctx.getResources().getIdentifier("star5", "drawable", ctx.getApplicationContext().getPackageName()));
			break;
		}

		// ����Ĭ����ʾ��ͼƬ
		hold.mImage.setImageResource(ctx.getResources().getIdentifier("shop_photo_frame", "drawable", ctx.getApplicationContext().getPackageName()));
		// �����ȡͼƬ
		Bitmap bit = loadImg.loadImage(hold.mImage, "http://lmsj-assets.oss-cn-qingdao.aliyuncs.com/ADimage/m.jpg", 
				new ImageDownloadCallBack() {
			@Override
			public void onImageDownload(ImageView imageView, Bitmap bitmap) {
				// ���罻��ʱ�ص�������ֹ��λ
				if (hold.mImage.getTag().equals(
						"http://lmsj-assets.oss-cn-qingdao.aliyuncs.com/ADimage/m.jpg")) {
					// �����������ػ���ͼƬ��ʾ
					hold.mImage.setImageBitmap(bitmap);
				}
			}
		});
		// �ӱ��ػ�ȡ��
		if (bit != null) {
			// ���û���ͼƬ��ʾ
			hold.mImage.setImageBitmap(bit);
		}

		return arg1;
	}

	static class Holder {
		TextView mTitle, mMoney, mAddress, mStytle, mDist;
		ImageView mImage, mStar, mTuan, mQuan, mDing, mCard;
	}

}
