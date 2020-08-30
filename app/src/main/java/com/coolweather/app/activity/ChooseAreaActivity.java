package com.coolweather.app.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.coolweather.app.R;
import com.coolweather.app.model.City;
import com.coolweather.app.model.CoolWeatherDB;
import com.coolweather.app.model.County;
import com.coolweather.app.model.Province;
import com.coolweather.app.util.HttpUtil;
import com.coolweather.app.util.Utility;

import java.util.ArrayList;
import java.util.List;

public class ChooseAreaActivity extends Activity {
    public static final int LEVEL_PROVINCE=0;
    public static final int LEVEL_CITY=1;
    public static final int LEVEL_COUNTY=2;

    private ProgressDialog progressDialog;
    private TextView titleText;
    private ListView listView;
    private ArrayAdapter<String>adapter;
    private CoolWeatherDB coolWeatherDB;
    private List<String>dataList =new ArrayList<String>();

    private List<Province>provinceList;
    private List<City>cityList;
    private List<County>countyList;

    private Province selectedProvince;
    private City selectedCity;
    private County selectedCounty;

    private int currentLevel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.choose_area);
        listView=(ListView)findViewById(R.id.list_view);
        titleText=(TextView)findViewById(R.id.title_text);
        adapter=new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,dataList);
        listView.setAdapter(adapter);
        coolWeatherDB=CoolWeatherDB.getInstance(this);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(currentLevel==LEVEL_PROVINCE){
                    selectedProvince=provinceList.get(i);
                    queryCity();
                }else if(currentLevel==LEVEL_CITY){
                    selectedCity=cityList.get(i);
                    queryCounty();
                }
            }
        });
        queryProvince();

//        queryFromServer(null,"province");
    }

    private void queryProvince(){
        provinceList=coolWeatherDB.loadProvinces();
        if(provinceList.size()>0){
            dataList.clear();
            for(Province p:provinceList){
                dataList.add(p.getName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            titleText.setText("中国");
            currentLevel=LEVEL_PROVINCE;
        }else {
            queryFromServer(null,"province");
        }
    }

    private void queryCity(){
        cityList=coolWeatherDB.loadCities(selectedProvince.getId());
        if(cityList.size()>0){
            dataList.clear();
            for(City c:cityList){
                dataList.add(c.getName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            titleText.setText(selectedProvince.getName());
            currentLevel=LEVEL_CITY;
        }else {
            queryFromServer(selectedProvince.getCode(),"city");
        }
    }

    private void queryCounty(){
        countyList=coolWeatherDB.loadCounties(selectedCity.getId());
        if(countyList.size()>0){
            dataList.clear();
            for(County c:countyList){
                dataList.add(c.getName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            titleText.setText(selectedCity.getName());
            currentLevel=LEVEL_COUNTY;
        }else {
            queryFromServer(selectedCity.getCode(),"county");
        }
    }

    private void queryFromServer(final String code ,final String type){
//        Log.d("进入","queryFromServer");
        String address;
        if(!TextUtils.isEmpty(code)){
            address="http://www.weather.com.cn/data/list3/city"+code+".xml";
        }else{
            address="http://www.weather.com.cn/data/list3/city.xml";
        }
        showProgressDialog();
        HttpUtil.sendHttpRequest(address, new HttpUtil.HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
//                Log.d("进入","onFinish");
                boolean success=false;
                if("province".equals(type)){
                    success= Utility.handleProvinceResponse(coolWeatherDB,response);
                }else if("city".equals(type)){
                    success=Utility.handleCitiesResponse(coolWeatherDB,response,selectedProvince.getId());
                }else if("county".equals(type)){
                    success=Utility.handleCountiesResponse(coolWeatherDB,response,selectedCity.getId());
                }
                
                if(success){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeProgressDialog();
                            switch (type) {
                                case "province":
                                    queryProvince();
                                    break;
                                case "city":
                                    queryCity();
                                    break;
                                case "county":
                                    queryCounty();
                                    break;
                            }
                        }
                    });
                }
                
            }

            @Override
            public void onError(Exception e) {
//                Log.d("进入","onError");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeProgressDialog();
                        Toast.makeText(ChooseAreaActivity.this,"加载失败",Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void showProgressDialog(){
//        Log.d("进入","showProgressDialog");
        if(progressDialog==null){
            progressDialog=new ProgressDialog(this);
            progressDialog.setMessage("正在加载...");
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }

    private void closeProgressDialog(){
        if(progressDialog!=null)
            progressDialog.dismiss();
    }

    @Override
    public void onBackPressed() {
        if(currentLevel==LEVEL_COUNTY){
            queryCity();
        }else if(currentLevel==LEVEL_CITY){
            queryProvince();
        }else {
            finish();
        }
    }
}
