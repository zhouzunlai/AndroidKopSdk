package cst.kop.activity;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import cst.kop.R;
import platform.cston.httplib.bean.CarBrandResult;
import platform.cston.httplib.bean.CarFaultResult;
import platform.cston.httplib.bean.CarListResult;
import platform.cston.httplib.bean.CarModelResult;
import platform.cston.httplib.bean.CarTypeResult;
import platform.cston.httplib.bean.DrivingBehaviorResult;
import platform.cston.httplib.bean.OpenUserResult;
import platform.cston.httplib.bean.TravelStatisticsResult;

/**
 * Created by zhou-pc on 2016/9/22.
 */
public class ApiActivity extends AppCompatActivity {

    private TextView tvApi_title;

    private TextView tvApi_content;

    private String mTitle;

    private int mSelectId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        mTitle = intent.getStringExtra("APITITLE");
        mSelectId = intent.getIntExtra("SELECTID", 0);
        setContentView(R.layout.activity_api);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        toolbar.setNavigationIcon(R.mipmap.cst_head_back_btn_white_normal);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        tvApi_title = (TextView) findViewById(R.id.api_title);
        tvApi_content = (TextView) findViewById(R.id.api_content);
        tvApi_title.setText(mTitle);
        ApiDateTrans(mSelectId, intent);
    }


    @TargetApi(Build.VERSION_CODES.N)
    private void ApiDateTrans(int withId, Intent intent) {
        switch (withId) {
            case 0:
                CarListResult carListResult = intent.getParcelableExtra("ParcelaInfo");
                tvApi_content.setText(carListResult.getAllCars().get(0).toString());
                break;
            case 1:
                OpenUserResult openUserResult = intent.getParcelableExtra("ParcelaInfo");
                tvApi_content.setText(openUserResult.getData().toString());
                break;
            case 2:
                TravelStatisticsResult travelStatisticsResult = intent.getParcelableExtra("ParcelaInfo");
                tvApi_content.setText(travelStatisticsResult.getData().toString());
                break;
            case 3:
                DrivingBehaviorResult drivingBehaviorResult = intent.getParcelableExtra("ParcelaInfo");
                tvApi_content.setText(drivingBehaviorResult.getData().toString());
                break;
            case 4:
                CarFaultResult carFaultResult = intent.getParcelableExtra("ParcelaInfo");
                tvApi_content.setText(carFaultResult.getData().toString());
                break;
            case 5:
                CarBrandResult carBrandResult = intent.getParcelableExtra("ParcelaInfo");
                String brand = "";
                for (int i = 0; i < carBrandResult.getData().size(); i++) {
                    brand += carBrandResult.getData().get(i).toString();
                }
                tvApi_content.setText(brand);
                break;
            case 6:
                CarTypeResult carTypeResult = intent.getParcelableExtra("ParcelaInfo");
                String type = "";
                for (int i = 0; i < carTypeResult.getData().size(); i++) {
                    type += carTypeResult.getData().get(i).toString();
                }
                tvApi_content.setText(type);
                break;
            case 7:
                CarModelResult carModelResult = intent.getParcelableExtra("ParcelaInfo");
                String model = "";
                for (int i = 0; i < carModelResult.getData().size(); i++) {
                    model += carModelResult.getData().get(i).toString();
                }
                tvApi_content.setText(model);
                break;
        }
    }
}
