package com.jam00.www.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.menu.MenuBuilder;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.jam00.www.R;
import com.jam00.www.util.ActivityCollector;
import com.jam00.www.util.BaseApplication;
import com.jam00.www.util.GlideCircleTransform;
import com.jam00.www.util.LogUtil;

import java.lang.reflect.Method;

public class NavBaseActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public static final String TAG = "NavBaseActivity";
    public String toolBarTitle;
    public NavigationView navigationView;
    //登录信息
    public TextView userName;
    public TextView userDes;
    public TextView loginOut;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }
    //初始化侧边栏导航栏等数据
    public void initNav(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //设置 toolbar 的图标
//        toolbar.setLogo(R.mipmap.ic_launcher);
        //顶部导航中间的文字（为使标题居中，要加一个textview）
        toolbar.setTitle("");
//        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white);
        TextView textView = (TextView) findViewById(R.id.toolbar_title);
        textView.setText(toolBarTitle);
        setSupportActionBar(toolbar);//执行设定

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close){
            // 当抽屉完全关闭时被调用
            @Override
            public void onDrawerClosed(View drawerView){

            }
            // 当抽屉完全打开时被调用
            @Override
            public void onDrawerOpened(View drawerView){
                userName = (TextView)drawerView.findViewById(R.id.head_username);
                userDes = (TextView)drawerView.findViewById(R.id.head_des);
                loginOut = (TextView)drawerView.findViewById(R.id.login_out);
                //用户头像
                ImageView userHead = (ImageView)drawerView.findViewById(R.id.head_image);
                //判断是否登录
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(NavBaseActivity.this);
                String authkey = prefs.getString("authkey",null);
                String username = prefs.getString("username",null);
                String head = prefs.getString("head",null);
                if(authkey!=null){
                    if(head!=null){
                        //设置用户头像
                        Glide.with(BaseApplication.getContext()).load(head).transform(new GlideCircleTransform(BaseApplication.getContext())).into(userHead);
                    }else{
                        Glide.with(BaseApplication.getContext()).load(R.drawable.default_head).transform(new GlideCircleTransform(BaseApplication.getContext())).into(userHead);
                    }
                    userName.setText(username);
                    userDes.setText(prefs.getString("des",""));
                    //退出登录
                    loginOut.setVisibility(View.VISIBLE);
                    loginOut.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(NavBaseActivity.this).edit();
                            editor.putString("userid",null);
//                            editor.putString("username",null);
                            editor.putString("authkey",null);
//                            editor.putString("head",null);
                            editor.putString("des",null);
                            editor.apply();
                            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                            drawer.closeDrawer(GravityCompat.START);
                            ActivityCollector.finishAll();
                            HomeActivity.actionStart(NavBaseActivity.this);
                        }
                    });
                }else{
                    //设置用户头像
                    Glide.with(BaseApplication.getContext()).load(R.drawable.default_head).transform(new GlideCircleTransform(BaseApplication.getContext())).into(userHead);
                    //点击登录前往登录页面
                    userName.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                            drawer.closeDrawer(GravityCompat.START);
                            if(!"LoginActivity".equals(activityName)){
                                LoginActivity.actionStart(NavBaseActivity.this);
                            }
                        }
                    });
                }
            }
        };
//        toggle.setDrawerIndicatorEnabled(false);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        // 配置SearchView的属性
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        MenuItemCompat.setOnActionExpandListener(searchItem,new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                mToast("打开 - 对应的 UI 操作");
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                mToast("收起 - 对应的 UI 操作");
                return true;
            }
        });
        return true;
    }
    //Overflow 列表显示图标
    @Override
    public boolean onMenuOpened(int featureId, Menu menu) {
        setOverflowIconVisible(featureId, menu);
        return super.onMenuOpened(featureId, menu);
    }
    /**
     * 显示OverflowMenu的Icon
     *
     * @param featureId
     * @param menu
     */
    private void setOverflowIconVisible(int featureId, Menu menu) {
        if (featureId == Window.FEATURE_ACTION_BAR && menu != null) {
            if (menu.getClass().getSimpleName().equals("MenuBuilder")) {
                try {
                    Method m = menu.getClass().getDeclaredMethod("setOptionalIconsVisible", Boolean.TYPE);
                    m.setAccessible(true);
                    m.invoke(menu, true);
                } catch (Exception e) {
                    LogUtil.d(TAG, "OverflowIconVisible - "+e.getMessage());
                }
            }
        }
    }

    @Override
    protected boolean onPrepareOptionsPanel(View view, Menu menu) {
        //显示OverflowMenu的Icon (这个起作用)
        if (menu != null) {
            if (menu.getClass() == MenuBuilder.class) {
                try {
                    Method m = menu.getClass().getDeclaredMethod("setOptionalIconsVisible", Boolean.TYPE);
                    m.setAccessible(true);
                    m.invoke(menu, true);
                } catch (Exception e) {
                    LogUtil.d(TAG, "OverflowIconVisible - "+e.getMessage());
                }
            }
        }
        return super.onPrepareOptionsPanel(view, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button_left, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_search) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            if(!"HomeActivity".equals(activityName)){
                HomeActivity.actionStart(this);
            }
        } else if (id == R.id.nav_weather) {
            if(!"ChooseAreaActivity".equals(activityName)){
                Intent intent = new Intent(this, ChooseAreaActivity.class);
                startActivity(intent);
            }
        } else if (id == R.id.nav_record) {
            if(!"RecordActivity".equals(activityName)){
                RecordActivity.actionStart(this);
            }
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

}
