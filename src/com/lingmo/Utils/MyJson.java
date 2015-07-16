package com.lingmo.Utils;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.lingmo.info.CommentsInfo;
import com.lingmo.info.FoodInfo;
import com.lingmo.info.ShopInfo;
import com.lingmo.info.SignInfo;

import android.util.Log;



/**
 * Json�ַ�������������
 * @author ��ɬ
 * </BR> </BR> By����ɬ </BR> ��ϵ���ߣ�QQ 534429149
 */

public class MyJson {
	private ArrayList<SignInfo> SignList = new ArrayList<SignInfo>();
	private ArrayList<CommentsInfo> CommentsList = new ArrayList<CommentsInfo>();
	private ArrayList<FoodInfo> FoodList = new ArrayList<FoodInfo>();

	public int getTotal(String value) {
		int sum = 0;
		try {
			JSONObject jObj = new JSONObject(value);
			sum = jObj.getInt("total");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return sum;
	}
	
	// ���������б�
	public List<ShopInfo> getShopList(String value) {
		List<ShopInfo> list = null;
		Log.d("MyJson", value);
		try {
			JSONObject jObj = new JSONObject(value);
			JSONArray jay = jObj.getJSONArray("contents");
			list = new ArrayList<ShopInfo>();
			for (int i = 0; i < jay.length(); i++) {
				JSONObject job = jay.getJSONObject(i);
				ShopInfo info = new ShopInfo();
				info.setSid(String.valueOf(i));
				info.setSname(job.getString("title"));
				info.setStype(job.getString("tags"));
				info.setSdistrict(job.getString("district"));
				info.setSaddress(job.getString("address"));
				info.setSnear(job.getString("distance"));
				info.setStel(job.getString("telephone"));
//				info.setStime(job.getString("stime"));
//				info.setSzhekou(job.getString("szhekou"));
//				info.setSmembercard(job.getString("smembercard"));
//				info.setSper(job.getString("sper"));
				info.setSmoney(job.getString("money"));
//				info.setSnum(job.getString("snum"));
				info.setSlevel(job.getString("level"));
				info.setSflag_tuan("0");
				info.setSflag_quan("0");
				info.setSflag_ding("0");
				info.setSflag_ka("0");
				info.setLongitude(String.valueOf(job.getJSONArray("location").getDouble(0)));
				info.setLatitude(String.valueOf(job.getJSONArray("location").getDouble(1)));
//				info.setSintroduction(job.getString("sintroduction"));
//				info.setSdetails(job.getString("sdetails"));
//				info.setStips(job.getString("stips"));
//				info.setSflag_promise(job.getString("sflag_promise"));
				info.setIname("abcd" + i);
				list.add(info);
			}
		} catch (JSONException e) {
		}
		return list;
	}

	// ��������json�Լ��ص��ӿ�ʵ��    
	public void getShopDetail(String mJson, DetailCallBack callback) {
		try {
			JSONObject job = new JSONObject(mJson);
			String result = job.getString("result");
			Log.e("result", "result:" + result);
			if (result.equalsIgnoreCase("ok")) {
				Log.e("result", "result:" + result);
				String signValue = job.getString("sign");
				String commentsValue = job.getString("comments");
				String foodValue = job.getString("food");
				JSONArray signArray = new JSONArray(signValue);
				JSONArray commentsArray = new JSONArray(commentsValue);
				JSONArray foodArray = new JSONArray(foodValue);
				for (int i = 0; i < foodArray.length(); i++) {
					JSONObject sJob = foodArray.getJSONObject(i);
					FoodInfo info = new FoodInfo();
					info.setFoodid(sJob.getString("foodid"));
					info.setSid(sJob.getString("sid"));
					info.setFoodname(sJob.getString("foodname"));
					info.setFoodphotoid(sJob.getString("foodphotoid"));
					FoodList.add(info);
				}
				for (int i = 0; i < commentsArray.length(); i++) {
					JSONObject sJob = commentsArray.getJSONObject(i);
					CommentsInfo info = new CommentsInfo();
					info.setCid(sJob.getString("cid"));
					info.setSid(sJob.getString("sid"));
					info.setPid(sJob.getString("pid"));
					info.setName(sJob.getString("name"));
					info.setTime(sJob.getString("time"));
					info.setComments(sJob.getString("comments"));
					info.setClevel(sJob.getString("clevel"));
					info.setKouweilevel(sJob.getString("kouweilevel"));
					info.setHuanjinglevel(sJob.getString("huanjinglevel"));
					info.setFuwulevel(sJob.getString("fuwulevel"));
					info.setCpermoney(sJob.getString("cpermoney"));
					CommentsList.add(info);
				}
				for (int i = 0; i < signArray.length(); i++) {
					JSONObject sJob = signArray.getJSONObject(i);
					SignInfo info = new SignInfo();
					info.setSignid(sJob.getString("signid"));
					info.setSid(sJob.getString("sid"));
					info.setPid(sJob.getString("pid"));
					info.setName(sJob.getString("name"));
					info.setSigncontent(sJob.getString("signcontent"));
					info.setSignlevel(sJob.getString("signlevel"));
					info.setSignimage(sJob.getString("signimage"));
					info.setSigntime(sJob.getString("signtime"));
					SignList.add(info);
				}
				Log.e("result", "SignList:" + SignList.size() + " CommentsList"
						+ CommentsList.size() + " FoodList" + FoodList.size());
				callback.getList(SignList, CommentsList, FoodList);
			} else {
				callback.getList(SignList, CommentsList, FoodList);
			}
		} catch (JSONException e) {
			callback.getList(SignList, CommentsList, FoodList);
		}
	}

	//ǩ����Ϣ�Ľ���
	public List<SignInfo> getSignList(String value) {
		List<SignInfo> list = new ArrayList<SignInfo>();
		try {
			JSONArray jay = new JSONArray(value);
			for (int i = 0; i < jay.length(); i++) {
				JSONObject job = jay.getJSONObject(i);
				SignInfo info = new SignInfo();
				info.setName(job.getString("name"));
				info.setSigncontent(job.getString("signcontent"));
				info.setSignimage(job.getString("signimage"));
				info.setSignlevel(job.getString("signlevel"));
				info.setSigntime(job.getString("signtime"));
				list.add(info);
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return list;
	}

	public interface DetailCallBack {
		public void getList(ArrayList<SignInfo> SignList,
				ArrayList<CommentsInfo> CommentsList,
				ArrayList<FoodInfo> FoodList);
	}
}
