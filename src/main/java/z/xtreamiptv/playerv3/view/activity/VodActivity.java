package z.xtreamiptv.playerv3.view.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build.VERSION;
import android.os.Bundle;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.navigation.NavigationView.OnNavigationItemSelectedListener;
import com.google.android.material.tabs.TabLayout;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager.widget.ViewPager.OnPageChangeListener;
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
import android.widget.Toast;
import butterknife.BindView;
import butterknife.ButterKnife;
import z.xtreamiptv.playerv3.R;
import z.xtreamiptv.playerv3.miscelleneious.common.AppConst;
import z.xtreamiptv.playerv3.miscelleneious.common.Utils;
import z.xtreamiptv.playerv3.model.LiveStreamCategoryIdDBModel;
import z.xtreamiptv.playerv3.model.LiveStreamsDBModel;
import z.xtreamiptv.playerv3.model.callback.XMLTVCallback;
import z.xtreamiptv.playerv3.model.database.DatabaseUpdatedStatusDBModel;
import z.xtreamiptv.playerv3.model.database.LiveStreamDBHandler;
import z.xtreamiptv.playerv3.model.database.PasswordStatusDBModel;
import z.xtreamiptv.playerv3.presenter.XMLTVPresenter;
import z.xtreamiptv.playerv3.view.adapter.VodCategoriesAdapter;
import z.xtreamiptv.playerv3.view.interfaces.XMLTVInterface;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;

public class VodActivity extends AppCompatActivity implements OnNavigationItemSelectedListener, OnClickListener, XMLTVInterface {
    private static final String JSON = "";
    int actionBarHeight;
    @BindView(R.id.appbar_toolbar)
    AppBarLayout appbarToolbar;
    ArrayList<LiveStreamCategoryIdDBModel> categoriesList;
    private ArrayList<PasswordStatusDBModel> categoryWithPasword;
    TextView clientNameTv;
    @BindView(R.id.content_drawer)
    RelativeLayout contentDrawer;
    private Context context;
    private DatabaseUpdatedStatusDBModel databaseUpdatedStatusDBModelEPG = new DatabaseUpdatedStatusDBModel(null, null, null, null);
    private DatabaseUpdatedStatusDBModel databaseUpdatedStatusDBModelLive = new DatabaseUpdatedStatusDBModel(null, null, null, null);
    @BindView(R.id.drawer_layout)
    DrawerLayout drawerLayout;
    private ArrayList<String> listPassword = new ArrayList();
    private ArrayList<LiveStreamCategoryIdDBModel> liveListDetail;
    private ArrayList<LiveStreamCategoryIdDBModel> liveListDetailAvailable;
    private ArrayList<LiveStreamCategoryIdDBModel> liveListDetailUnlcked;
    private ArrayList<LiveStreamCategoryIdDBModel> liveListDetailUnlckedDetail;
    private LiveStreamDBHandler liveStreamDBHandler;
    private SharedPreferences loginPreferencesAfterLogin;
    @BindView(R.id.nav_view)
    NavigationView navView;
    @BindView(R.id.pb_loader)
    ProgressBar pbLoader;
    @BindView(R.id.sliding_tabs)
    TabLayout slidingTabs;
    ArrayList<LiveStreamCategoryIdDBModel> subCategoryList;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    TypedValue tv;
    @BindView(R.id.tv_header_title)
    ImageView tvHeaderTitle;
    private String userName = "";
    private String userPassword = "";
    @BindView(R.id.viewpager)
    ViewPager viewpager;
    private XMLTVPresenter xmltvPresenter;

    class C19011 implements OnPageChangeListener {
        C19011() {
        }

        public void onPageScrolled(int i, float v, int i2) {
        }

        public void onPageSelected(int i) {
        }

        public void onPageScrollStateChanged(int i) {
        }
    }

    class AnonymousClass1NewAsyncTask extends AsyncTask<String, Integer, Boolean> {
        int ITERATIONS = this.val$xmltvCallback.programmePojos.size();
        Context mcontext = null;
         /* synthetic */ XMLTVCallback val$xmltvCallback;

        AnonymousClass1NewAsyncTask(Context context, XMLTVCallback xMLTVCallback) {
            this.val$xmltvCallback = xMLTVCallback;
            this.mcontext = context;
        }

        protected Boolean doInBackground(String... params) {
            publishProgress(new Integer[]{Integer.valueOf(0)});
            if (VodActivity.this.liveStreamDBHandler != null) {
                VodActivity.this.liveStreamDBHandler.addEPG(this.val$xmltvCallback.programmePojos);
            }
            return Boolean.valueOf(true);
        }

        protected void onProgressUpdate(Integer... progress) {
        }

        protected void onPreExecute() {
        }

        protected void onPostExecute(Boolean result) {
            VodActivity.this.liveStreamDBHandler.updateDBStatus(AppConst.DB_EPG, AppConst.DB_EPG_ID, AppConst.DB_UPDATED_STATUS_FINISH);
            if (VodActivity.this.context == null) {
            }
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vod);
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
        navigationView.getMenu().findItem(R.id.nav_vod).setChecked(true);
        this.context = this;
        Toast.makeText(this.context, getResources().getString(R.string.use_long_press), 1).show();
        intiliaze();
        setUpDatabaseResults();
        if (this.tvHeaderTitle != null) {
            this.tvHeaderTitle.setOnClickListener(this);
        }
    }

    private void intiliaze() {
        this.categoriesList = new ArrayList();
        this.subCategoryList = new ArrayList();
    }

    private void setUpDatabaseResults() {
        if (this.context != null) {
            this.liveStreamDBHandler = new LiveStreamDBHandler(this.context);
            this.liveListDetailUnlcked = new ArrayList();
            this.liveListDetailUnlckedDetail = new ArrayList();
            this.liveListDetailAvailable = new ArrayList();
            this.liveListDetail = new ArrayList();
            ArrayList<LiveStreamCategoryIdDBModel> moviesCategory = this.liveStreamDBHandler.getAllMovieCategoriesHavingParentIdZero();
            ArrayList<LiveStreamCategoryIdDBModel> moviesCategoryFinal = new ArrayList();
            Iterator it = moviesCategory.iterator();
            while (it.hasNext()) {
                LiveStreamCategoryIdDBModel list = (LiveStreamCategoryIdDBModel) it.next();
                String categoryID = list.getLiveStreamCategoryID();
                ArrayList<LiveStreamsDBModel> listChannels = this.liveStreamDBHandler.getAllLiveStreasWithCategoryId(categoryID, "movie");
                Iterator it2 = this.liveStreamDBHandler.getAllMovieCategoriesHavingParentIdNotZero(categoryID).iterator();
                while (it2.hasNext()) {
                    if (this.liveStreamDBHandler.getAllLiveStreasWithCategoryId(String.valueOf(((LiveStreamCategoryIdDBModel) it2.next()).getLiveStreamCategoryID()), "movie").size() > 0) {
                        moviesCategoryFinal.add(list);
                        break;
                    }
                }
                if (listChannels.size() > 0) {
                    moviesCategoryFinal.add(list);
                }
            }
            LiveStreamCategoryIdDBModel liveStream = new LiveStreamCategoryIdDBModel(null, null, 0);
            int size = moviesCategoryFinal.size();
            LiveStreamCategoryIdDBModel liveStream1 = new LiveStreamCategoryIdDBModel(null, null, 0);
            liveStream1.setLiveStreamCategoryID("-1");
            liveStream1.setLiveStreamCategoryName(getResources().getString(R.string.favourites));
            liveStream.setLiveStreamCategoryID(AppConst.PASSWORD_UNSET);
            liveStream.setLiveStreamCategoryName(getResources().getString(R.string.all));
            this.liveListDetail = moviesCategoryFinal;
            if (this.liveStreamDBHandler.getParentalStatusCount() <= 0 || this.liveListDetail == null) {
                this.liveListDetail.add(0, liveStream);
                this.liveListDetail.add(1, liveStream1);
                this.liveListDetailAvailable = this.liveListDetail;
            } else {
                this.listPassword = getPasswordSetCategories();
                this.liveListDetailUnlckedDetail = getUnlockedCategories(moviesCategoryFinal, this.listPassword);
                this.liveListDetailUnlcked.add(0, liveStream);
                this.liveListDetailUnlcked.add(1, liveStream1);
                this.liveListDetailAvailable = this.liveListDetailUnlckedDetail;
            }
            if (this.liveListDetailAvailable != null && this.viewpager != null && this.slidingTabs != null) {
                this.viewpager.setAdapter(new VodCategoriesAdapter(this.liveListDetailAvailable, getSupportFragmentManager(), this));
                this.slidingTabs.setupWithViewPager(this.viewpager);
                this.viewpager.setOnPageChangeListener(new C19011());
            }
        }
    }

    private ArrayList<LiveStreamCategoryIdDBModel> getUnlockedCategories(ArrayList<LiveStreamCategoryIdDBModel> liveListDetail, ArrayList<String> listPassword) {
        Iterator it = liveListDetail.iterator();
        while (it.hasNext()) {
            LiveStreamCategoryIdDBModel user1 = (LiveStreamCategoryIdDBModel) it.next();
            boolean flag = false;
            Iterator it2 = listPassword.iterator();
            while (it2.hasNext()) {
                if (user1.getLiveStreamCategoryID().equals((String) it2.next())) {
                    flag = true;
                    break;
                }
            }
            if (!flag) {
                this.liveListDetailUnlcked.add(user1);
            }
        }
        return this.liveListDetailUnlcked;
    }

    private ArrayList<String> getPasswordSetCategories() {
        this.categoryWithPasword = this.liveStreamDBHandler.getAllPasswordStatus();
        if (this.categoryWithPasword != null) {
            Iterator it = this.categoryWithPasword.iterator();
            while (it.hasNext()) {
                PasswordStatusDBModel listItemLocked = (PasswordStatusDBModel) it.next();
                if (listItemLocked.getPasswordStatus().equals("1")) {
                    this.listPassword.add(listItemLocked.getPasswordStatusCategoryId());
                }
            }
        }
        return this.listPassword;
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
            startActivity(new Intent(this, DashboardActivity.class));
        } else if (id == R.id.nav_live_tv) {
            Utils.set_layout_live(this.context);

        } else if (id == R.id.nav_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
        } else if (id == R.id.nav_account_info) {
            startActivity(new Intent(this, AccountInfoActivity.class));
        } else if (id == R.id.nav_archive) {
            startActivity(new Intent(this, TVArchiveActivity.class));
        } else if (id == R.id.nav_series) {
            startActivity(new Intent(this, SeriesTabActivity.class));
        } else if (id == R.id.nav_live_tv_guide) {
            if (!(this.context == null || this.databaseUpdatedStatusDBModelLive == null || this.liveStreamDBHandler == null)) {
                this.databaseUpdatedStatusDBModelLive = this.liveStreamDBHandler.getdateDBStatus(AppConst.DB_CHANNELS, "1");
                if (this.databaseUpdatedStatusDBModelLive == null || !this.databaseUpdatedStatusDBModelLive.getDbUpadatedStatusState().equals(AppConst.DB_UPDATED_STATUS_FINISH)) {
                    Utils.showToast(this.context, getResources().getString(R.string.udpating_channels_please_wait));
                } else {
                    startDashboardActivty();
                }
            }
        } else if (id == R.id.nav_logout && this.context != null) {
            Utils.logoutUser(this.context);
        }
        ((DrawerLayout) findViewById(R.id.drawer_layout)).closeDrawer(8388611);
        return true;
    }

    private void startDashboardActivty() {
        Intent dashboardActitvityIntent = new Intent(this, DashboardActivity.class);
        dashboardActitvityIntent.putExtra(AppConst.LAUNCH_TV_GUIDE, AppConst.LAUNCH_TV_GUIDE);
        startActivity(dashboardActitvityIntent);
    }

    public void startTvGuideActivity() {
        startActivity(new Intent(this, NewEPGActivity.class));
        finish();
    }

    public void startImportTvGuideActivity() {
        startActivity(new Intent(this, ImportEPGActivity.class));
        finish();
    }

    public static long getDateDiff(SimpleDateFormat format, String oldDate, String newDate) {
        try {
            return TimeUnit.DAYS.convert(format.parse(newDate).getTime() - format.parse(oldDate).getTime(), TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    private String currentDateValue() {
        return Utils.parseDateToddMMyyyy(Calendar.getInstance().getTime().toString());
    }

    public String getUserName() {
        if (this.context == null) {
            return this.userName;
        }
        this.loginPreferencesAfterLogin = this.context.getSharedPreferences(AppConst.LOGIN_SHARED_PREFERENCE, 0);
        return this.loginPreferencesAfterLogin.getString("username", "");
    }

    public String getUserPassword() {
        if (this.context == null) {
            return this.userPassword;
        }
        this.loginPreferencesAfterLogin = this.context.getSharedPreferences(AppConst.LOGIN_SHARED_PREFERENCE, 0);
        return this.loginPreferencesAfterLogin.getString("password", "");
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
                startActivity(new Intent(this, DashboardActivity.class));
                return;
            default:
                return;
        }
    }

    public void atStart() {
    }

    public void onFinish() {
        if (this.pbLoader != null) {
            this.pbLoader.setVisibility(4);
        }
    }

    public void onFailed(String errorMessage) {
    }

    public void epgXMLTV(XMLTVCallback xmltvCallback) {
        if (xmltvCallback != null && this.context != null && xmltvCallback.programmePojos != null) {
            this.liveStreamDBHandler.makeEmptyEPG();
            if (VERSION.SDK_INT >= 11) {
                new AnonymousClass1NewAsyncTask(this.context, xmltvCallback).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new String[0]);
            } else {
                new AnonymousClass1NewAsyncTask(this.context, xmltvCallback).execute(new String[0]);
            }
        }
    }

    public void epgXMLTVUpdateFailed(String failedUpdate) {
        if (failedUpdate.equals(AppConst.DB_UPDATED_STATUS_FAILED) && this.liveStreamDBHandler != null) {
            this.liveStreamDBHandler.updateDBStatus(AppConst.DB_EPG, AppConst.DB_EPG_ID, AppConst.DB_UPDATED_STATUS_FAILED);
        }
    }
}
