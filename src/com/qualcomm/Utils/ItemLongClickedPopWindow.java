package com.qualcomm.Utils;

import com.qualcomm.QCARUnityPlayer.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.PopupWindow;

public class ItemLongClickedPopWindow extends PopupWindow{
    private LayoutInflater favAndHisInflater;
    private View favAndHisView;
    private Context context;
    
    /**
     * 构造函数
     * @param	context Context
     * @param	width	int
     * @param	height	int
     * */
    public ItemLongClickedPopWindow(Context context, int width, int height){
    	super(context);
    	this.context = context;
    	
    	//创建
    	this.initTab();
    	
    	//设置默认选项
    	setWidth(width);
    	setHeight(height);
    	setContentView(this.favAndHisView);
    	setOutsideTouchable(true);
    	setFocusable(true);
    }
    
    
    //实例化
    private void initTab(){
    	this.favAndHisInflater = LayoutInflater.from(this.context);
    	this.favAndHisView = this.favAndHisInflater.inflate(R.layout.list_item_longclicked, null);
    }
    
    public View getView(int id){
    	return this.favAndHisView.findViewById(id);
    }
}
