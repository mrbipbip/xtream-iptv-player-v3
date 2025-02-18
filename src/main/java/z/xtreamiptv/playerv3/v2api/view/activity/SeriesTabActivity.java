package z.xtreamiptv.playerv3.v2api.view.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build.VERSION;
import android.os.Bundle;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.navigation.NavigationView.OnNavigationItemSelectedListener;
import com.google.android.material.tabs.TabLayout;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager.widget.ViewPager.OnPageChangeListener;
import androidx.appcompat.widget.SearchView;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.widget.Toolbar.LayoutParams;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import z.xtreamiptv.playerv3.R;
import z.xtreamiptv.playerv3.miscelleneious.common.AppConst;
import z.xtreamiptv.playerv3.miscelleneious.common.Utils;
import z.xtreamiptv.playerv3.model.LiveStreamCategoryIdDBModel;
import z.xtreamiptv.playerv3.model.database.LiveStreamDBHandler;
import z.xtreamiptv.playerv3.model.database.PasswordStatusDBModel;
import z.xtreamiptv.playerv3.v2api.model.database.LiveStreamsDatabaseHandler;
import z.xtreamiptv.playerv3.v2api.model.database.SeriesStreamsDatabaseHandler;
import z.xtreamiptv.playerv3.view.activity.LoginActivity;
import z.xtreamiptv.playerv3.view.adapter.SeriesTabCategoryAdapter;
import java.util.ArrayList;

public class SeriesTabActivity extends AppCompatActivity implements OnNavigationItemSelectedListener, OnClickListener {
    private static final String JSON = "";
    int actionBarHeight;
    @BindView(R.id.appbar_toolbar)
    AppBarLayout appbarToolbar;
    private ArrayList<PasswordStatusDBModel> categoryWithPasword;
    private TextView clientNameTv;
    @BindView(R.id.content_drawer)
    RelativeLayout contentDrawer;
    private Context context;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawerLayout;
    private ArrayList<String> listPassword = new ArrayList();
    private ArrayList<LiveStreamCategoryIdDBModel> liveListDetail;
    private ArrayList<LiveStreamCategoryIdDBModel> liveListDetailAvailable;
    private ArrayList<LiveStreamCategoryIdDBModel> liveListDetailUnlcked;
    private ArrayList<LiveStreamCategoryIdDBModel> liveListDetailUnlckedDetail;
    private LiveStreamDBHandler liveStreamDBHandler;
    private LiveStreamsDatabaseHandler liveStreamsDatabaseHandler;
    private SharedPreferences loginPreferencesAfterLogin;
    @BindView(R.id.nav_view)
    NavigationView navView;
    @BindView(R.id.pb_loader)
    ProgressBar pbLoader;
    private ProgressDialog progressDialog;
    private SearchView searchView;
    private SeriesStreamsDatabaseHandler seriesStreamsDatabaseHandler;
    @BindView(R.id.sliding_tabs)
    TabLayout slidingTabs;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    private TypedValue tv;
    @BindView(R.id.tv_header_title)
    ImageView tvHeaderTitle;
    @BindView(R.id.viewpager)
    ViewPager viewpager;

    class C15071 implements OnPageChangeListener {
        C15071() {
        }

        public void onPageScrolled(int i, float v, int i2) {
        }

        public void onPageSelected(int i) {
        }

        public void onPageScrollStateChanged(int i) {
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_series_tab);
        ButterKnife.bind(this);
        changeStatusBarColor();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        setToggleIconPosition();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.getMenu().findItem(R.id.nav_series).setChecked(true);
        this.context = this;
        ((NavigationView) findViewById(R.id.nav_view)).getMenu().findItem(R.id.nav_series).setVisible(true);
        setUpDatabaseResults();
        if (this.tvHeaderTitle != null) {
            this.tvHeaderTitle.setOnClickListener(this);
        }
    }

    private void setUpDatabaseResults() {
        if (this.context != null) {
            this.seriesStreamsDatabaseHandler = new SeriesStreamsDatabaseHandler(this.context);
            this.liveStreamDBHandler = new LiveStreamDBHandler(this.context);
            this.categoryWithPasword = new ArrayList();
            this.liveListDetailAvailable = new ArrayList();
            this.liveListDetail = new ArrayList();
            this.liveListDetail = this.seriesStreamsDatabaseHandler.getAllSeriesCategories();
            LiveStreamCategoryIdDBModel liveStream = new LiveStreamCategoryIdDBModel(null, null, 0);
            LiveStreamCategoryIdDBModel liveStream1 = new LiveStreamCategoryIdDBModel(null, null, 0);
            LiveStreamCategoryIdDBModel liveStream2 = new LiveStreamCategoryIdDBModel(null, null, 0);
            liveStream.setLiveStreamCategoryID(AppConst.PASSWORD_UNSET);
            liveStream.setLiveStreamCategoryName(getResources().getString(R.string.all));
            liveStream1.setLiveStreamCategoryID("-1");
            liveStream1.setLiveStreamCategoryName(getResources().getString(R.string.favourites));
            this.liveListDetail.add(0, liveStream);
            this.liveListDetailAvailable = this.liveListDetail;
            if (this.liveListDetailAvailable != null && this.viewpager != null && this.slidingTabs != null) {
                this.viewpager.setAdapter(new SeriesTabCategoryAdapter(this.liveListDetailAvailable, getSupportFragmentManager(), this));
                this.slidingTabs.setupWithViewPager(this.viewpager);
                this.viewpager.setOnPageChangeListener(new C15071());
            }
        }
    }

    private void setToggleIconPosition() {
        this.tv = new TypedValue();
        if (getTheme().resolveAttribute(16843499, this.tv, true)) {
            this.actionBarHeight = TypedValue.complexToDimensionPixelSize(this.tv.data, getResources().getDisplayMetrics());
        }
        for (int i = 0; i < this.toolbar.getChildCount(); i++) {
            if (this.toolbar.getChildAt(i) instanceof ImageButton) {
                ((LayoutParams) this.toolbar.getChildAt(i).getLayoutParams()).gravity = 16;
            }
        }
    }

    private void changeStatusBarColor() {
        Window window = getWindow();
        if (VERSION.SDK_INT >= 19) {
            window.clearFlags(67108864);
        }
        if (VERSION.SDK_INT >= 21) {
            window.addFlags(Integer.MIN_VALUE);
        }
        if (VERSION.SDK_INT >= 21) {
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
        }
    }

    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(8388611)) {
            drawer.closeDrawer(8388611);
        } else {
            super.onBackPressed();
        }
    }

    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_home) {
            startActivity(new Intent(this, DashboardActivityV2.class));
        } else if (id == R.id.nav_live_tv) {
            Utils.set_layout_live(this.context);
        } else if (id == R.id.nav_vod) {
            Utils.set_layout_vod(this.context);

        } else if (id == R.id.nav_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
        } else if (id == R.id.nav_account_info) {
            startActivity(new Intent(this, AccountInfoActivity.class));
        } else if (id == R.id.nav_archive) {
            startActivity(new Intent(this, TVArchiveActivity.class));
        } else if (id == R.id.nav_live_tv_guide) {
            if (this.context != null) {
                Utils.startDashboardV2LoadTVGuid(this.context);
            }
        } else if (id == R.id.nav_logout) {
            Utils.logoutUser(this.context);
        }
        ((DrawerLayout) findViewById(R.id.drawer_layout)).closeDrawer(8388611);
        return true;
    }

    public void onResume() {
        super.onResume();
        this.loginPreferencesAfterLogin = getSharedPreferences(AppConst.LOGIN_SHARED_PREFERENCE, 0);
        if (this.loginPreferencesAfterLogin.getString("username", "").equals("") && this.loginPreferencesAfterLogin.getString("password", "").equals("")) {
            startActivity(new Intent(this, LoginActivity.class));
        } else if (this.context != null) {
            onFinish();
        }
        headerView();
    }

    private void headerView() {
        View header = this.navView.getHeaderView(0);
        ImageView closeDrawerIV = (ImageView) header.findViewById(R.id.iv_close_drawer);
        this.clientNameTv = (TextView) header.findViewById(R.id.tv_client_name);
        this.clientNameTv.setText(this.loginPreferencesAfterLogin.getString("username", ""));
        closeDrawerIV.setOnClickListener(this);
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_close_drawer:
                this.drawerLayout.closeDrawers();
                return;
            case R.id.tv_header_title:
                startActivity(new Intent(this, DashboardActivityV2.class));
                return;
            default:
                return;
        }
    }

    public void onFinish() {
        if (this.pbLoader != null) {
            this.pbLoader.setVisibility(4);
        }
    }
}
