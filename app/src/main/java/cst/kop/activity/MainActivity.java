package cst.kop.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import cst.kop.R;
import cst.kop.adapter.DialogListAdapter;
import cst.kop.fragment.NativeInterfaceFragment;
import cst.kop.tools.AppUtils;
import cst.kop.tools.DialogUtils;
import platform.cston.httplib.bean.AuthorizationInfo;
import platform.cston.httplib.bean.CarListResult;
import platform.cston.httplib.bean.OpenUserResult;
import platform.cston.httplib.search.AuthUser;
import platform.cston.httplib.search.CarInfoSearch;
import platform.cston.httplib.search.OnResultListener;
import platform.cston.httplib.search.OpenUserInfoSearch;

public class MainActivity extends AppCompatActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    TabLayout mTabLayout;

    Button mFab;

    public String mOpenCarId;
    public String mCarPlate;

    private List<CarListResult.CarInfo> data = new ArrayList<>();

    private AlertDialog carListDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("SDK功能列表");
        setSupportActionBar(toolbar);

        mTabLayout = (TabLayout) findViewById(R.id.tabs);
        mTabLayout.addTab(mTabLayout.newTab().setText("原生界面"));
        mTabLayout.addTab(mTabLayout.newTab().setText("API数据接口"));
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        mFab = (Button) findViewById(R.id.btn);
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showCarList();
            }
        });

        mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mViewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        TabLayout.TabLayoutOnPageChangeListener listener =
                new TabLayout.TabLayoutOnPageChangeListener(mTabLayout);
        mViewPager.addOnPageChangeListener(listener);
        showCarList();

    }


    /**
     * 显示车列表
     */
    private void showCarList() {
        if (null == data || data.size() == 0) {
            CarInfoSearch.newInstance().GetCarInfoResult(new OnResultListener.OnGetCarListResultListener() {
                @Override
                public void onGetCarListResult(CarListResult carListResult, boolean b, Throwable throwable) {
                    if (b) {
                        if (carListResult != null) {
                            Toast.makeText(MainActivity.this, "车列表请求失败：" + carListResult.getResult() + " code is:" + carListResult.getCode(), Toast.LENGTH_SHORT).show();
                        } else {
                            if (null != throwable)
                                Toast.makeText(MainActivity.this, "车列表请求异常：" + throwable.getCause(), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        if (null != carListResult && null != carListResult.getAllCars() && carListResult.getAllCars().size() > 0) {
                            data.addAll(carListResult.getAllCars());
                            if (null == mOpenCarId || mOpenCarId.isEmpty()) {
                                mOpenCarId = data.get(0).openCarId;
                                mCarPlate = data.get(0).plate;
                                mFab.setText(mCarPlate);
                            } else {
                                carListDialog = DialogUtils.showListItemChooseDialog(MainActivity.this, data, dialogCallBack);
                            }
                        } else
                            Toast.makeText(MainActivity.this, "请求成功，车列表数据为空", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            carListDialog = DialogUtils.showListItemChooseDialog(MainActivity.this, data, dialogCallBack);
        }
    }

    private DialogListAdapter.CallBack dialogCallBack = new DialogListAdapter.CallBack() {
        @Override
        public void onItemClick(int posion, CarListResult.CarInfo item) {
            mOpenCarId = data.get(posion).openCarId;
            mCarPlate = data.get(posion).plate;
            mFab.setText(mCarPlate);
            carListDialog.dismiss();
        }
    };


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            AuthUser.getInstance().CancelAuthorization(new OnResultListener.OnAuthorCancelListener() {
                @Override
                public void onAuthorCancelResult(boolean b, String s) {
                    if (b) {
                        startActivity(new Intent(MainActivity.this, LoginActivity.class));
                        MainActivity.this.finish();
                    } else {
                        if (null != s) {
                            Toast.makeText(MainActivity.this, s, Toast.LENGTH_SHORT).show();
                        }
                        AppUtils.setAuthFlag(MainActivity.this);
                        startActivity(new Intent(MainActivity.this, LoginActivity.class));
                        MainActivity.this.finish();
                    }
                }
            });
            return true;
        } else if (id == R.id.action_autho) {
            AuthorizationInfo info = AuthUser.getInstance().ResetOpenIdAndOpenCarId();
            String openId = "用户ID(openId):" + (info.openId != null ? info.openId : "");
            String carplate = "车牌:" + (mCarPlate != null ? mCarPlate : "");
            String openCarId = "车ID(openCarId):" + (mOpenCarId != null ? mOpenCarId : "");
            DialogUtils.showAuthInfoAlertDialog(MainActivity.this, "", carplate, openId, openCarId);
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * 请求用户基本资料
     */
    private void PostApiUserInfo() {
        OpenUserInfoSearch.getInstance().GetUserInfoResult(new OnResultListener.OpenUserResultListener() {
            @Override
            public void onOpenUserResult(OpenUserResult var1, boolean isError, Throwable ex) {
                if (!isError && var1 != null) {
                    if (var1.getCode().equals("0")) {
                        String phone = "手机号：" + "";
                        String openId = "用户ID（openId）：" + var1.getData().openId != null ? var1.getData().openId : "";
                        String carplate = "车牌：" + mCarPlate != null ? mCarPlate : "";
                        String openCarId = "车ID(openCarId)：" + mOpenCarId != null ? mOpenCarId : "";
                        DialogUtils.showAuthInfoAlertDialog(MainActivity.this, phone, carplate, openId, openCarId);
                    } else {
                        AuthorizationInfo info = AuthUser.getInstance().ResetOpenIdAndOpenCarId();
                        String phone = "手机号：" + "";
                        String openId = "用户ID（openId）：" + info.openId != null ? info.openId : "";
                        String carplate = "车牌：" + mCarPlate != null ? mCarPlate : "";
                        String openCarId = "车ID(openCarId)：" + mOpenCarId != null ? mOpenCarId : "";
                        DialogUtils.showAuthInfoAlertDialog(MainActivity.this, phone, carplate, openId, openCarId);
                    }
                } else {
                    AuthorizationInfo info = AuthUser.getInstance().ResetOpenIdAndOpenCarId();
                    String phone = "手机号：" + "";
                    String openId = "用户ID（openId）：" + info.openId != null ? info.openId : "";
                    String carplate = "车牌：" + mCarPlate != null ? mCarPlate : "";
                    String openCarId = "车ID(openCarId)：" + mOpenCarId != null ? mOpenCarId : "";
                    DialogUtils.showAuthInfoAlertDialog(MainActivity.this, phone, carplate, openId, openCarId);
                }
            }
        });
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return NativeInterfaceFragment.newInstance(position);
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "SECTION 1 1";
                case 1:
                    return "SECTION 2 2";
                case 2:
                    return "SECTION 3 3";
            }
            return null;
        }
    }
}
