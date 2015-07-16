package com.lingmo.Utils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Info implements Serializable
{
	private static final long serialVersionUID = -758459502806858414L;
	/**
	 * ����
	 */
	private double latitude;
	/**
	 * γ��
	 */
	private double longitude;
	/**
	 * �̼�����
	 */
	private String name;
	
	private String tel;
	/**
	 * ���릻
	 */
	private int distance;
	
	public static List<Info> infos = new ArrayList<Info>();

	public Info()
	{
	}

	public Info(double latitude, double longitude, String name,	String tel, int distance)
	{
		super();
		this.latitude = latitude;
		this.longitude = longitude;
	
		this.name = name;
		this.tel = tel;
		this.distance = distance;
	}

	public double getLatitude()
	{
		return latitude;
	}

	public void setLatitude(double latitude)
	{
		this.latitude = latitude;
	}

	public double getLongitude()
	{
		return longitude;
	}

	public void setLongitude(double longitude)
	{
		this.longitude = longitude;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String getTel()
	{
		return tel;
	}

	public void setTel(String tel)
	{
		this.tel = tel;
	}
	
	public int getDistance()
	{
		return distance;
	}

	public void setDistance(int distance)
	{
		this.distance = distance;
	}
}
