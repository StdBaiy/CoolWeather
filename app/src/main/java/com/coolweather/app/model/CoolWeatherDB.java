package com.coolweather.app.model;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.coolweather.app.db.CoolWeatherOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class CoolWeatherDB {
    public static final String DB_NAME="cool_weather";

    public static final int VERSION=1;
    private static CoolWeatherDB coolWeatherDB;
    private SQLiteDatabase db;

    private CoolWeatherDB(Context context){
        CoolWeatherOpenHelper dbHelper=new CoolWeatherOpenHelper(context,DB_NAME,null,VERSION);
        db=dbHelper.getWritableDatabase();
    }
    /*
     *获取CoolWeatherDB实例
     */
    public synchronized static CoolWeatherDB getInstance(Context context){
        if(coolWeatherDB==null){
            coolWeatherDB=new CoolWeatherDB(context);
        }
        return coolWeatherDB;
    }

    public void saveProvince(Province province){
        if(province!=null)
            db.execSQL("insert into Province (name,code) values(?,?)",new String[]{province.getName(),province.getCode()});
    }

    public List<Province>loadProvinces(){
        List<Province>list=new ArrayList<Province>();
        @SuppressLint("Recycle") Cursor cursor=db.rawQuery("select*from Province",null);
        if(cursor.moveToFirst()){
            do{
                Province province=new Province();
                province.setId(cursor.getInt(cursor.getColumnIndex("id")));
                province.setName(cursor.getString(cursor.getColumnIndex("name")));
                province.setCode(cursor.getString(cursor.getColumnIndex("code")));
                list.add(province);
            }while(cursor.moveToNext());
        }
        return list;
    }

    public void saveCity(City city){
        if(city!=null){
//            db.execSQL("insert into City (name,code,province_id)values(?,?,?)",new String[]{city.getName(),city.getCode(),city.getProvinceId()});
            ContentValues values=new ContentValues();
            values.put("name",city.getName());
            values.put("code",city.getCode());
            values.put("province_id",city.getProvinceId());
            db.insert("City",null,values);
        }
    }

    public List<City> loadCities(int provinceId){
        List<City>list=new ArrayList<City>();
        Cursor cursor=db.query("City",null,"province_id=?",new String[]{String.valueOf(provinceId)},
                null,null,null);
        if(cursor.moveToFirst()){
            do{
                City city=new City();
                city.setId(cursor.getInt(cursor.getColumnIndex("id")));
                city.setName(cursor.getString(cursor.getColumnIndex("name")));
                city.setCode(cursor.getString(cursor.getColumnIndex("code")));
                city.setProvinceId(provinceId);
                list.add(city);
            }while(cursor.moveToNext());
        }
        return list;
    }

    public void saveCounty(County county){
        if(county!=null){
            ContentValues values=new ContentValues();
            values.put("name",county.getName());
            values.put("code",county.getCode());
            values.put("city_id",county.getCityId());
            db.insert("County",null,values);
        }
    }
    
    public List<County> loadCounties(int cityId){
        List<County>list=new ArrayList<County>();
        Cursor cursor=db.query("County",null,"city_id=?",new String[]{String.valueOf(cityId)},
                null,null,null);
        if(cursor.moveToFirst()){
            do{
                County county=new County();
                county.setId(cursor.getInt(cursor.getColumnIndex("id")));
                county.setName(cursor.getString(cursor.getColumnIndex("name")));
                county.setCode(cursor.getString(cursor.getColumnIndex("code")));
                county.setCityId(cityId);
                list.add(county);
            }while(cursor.moveToNext());
        }
        return list;
    }
}
