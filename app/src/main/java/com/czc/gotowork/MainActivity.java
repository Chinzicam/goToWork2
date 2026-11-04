package com.czc.gotowork;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.czc.gotowork.databinding.ActivityMainBinding;
import com.czc.gotowork.ui.SettingsActivity;
import com.czc.gotowork.util.Prefs;
import com.czc.gotowork.util.SalaryCalc;

import java.util.Locale;

/**
 * 主界面：显示倒计时、已赚金额、进度条等，每秒刷新
 */
public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding mBinding;
    private Prefs mPrefs;
    private SalaryCalc mCalc;
    private final Handler mHandler = new Handler(Looper.getMainLooper());

    // 设置页跳转回调
    private final ActivityResultLauncher<Intent> mSettingsLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                    result -> loadConfig());

    private final Runnable mTick = new Runnable() {
        @Override
        public void run() {
            refresh();
            mHandler.postDelayed(this, 1000);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        mPrefs = new Prefs(this);

        // 首次启动或未配置时，直接进设置页
        if (!mPrefs.isConfigured()) {
            startActivity(new Intent(this, SettingsActivity.class));
        }

        mBinding.btnSettings.setOnClickListener(v ->
                mSettingsLauncher.launch(new Intent(this, SettingsActivity.class)));

        loadConfig();
    }

    private void loadConfig() {
        if (!mPrefs.isConfigured()) {
            mCalc = null;
            return;
        }
        mCalc = new SalaryCalc(
                mPrefs.getDouble("salary_month", 8000),
                mPrefs.getInt("work_days_month", 22),
                mPrefs.getDouble("work_start_hour", 9.0),
                mPrefs.getDouble("lunch_start_hour", 12.0),
                mPrefs.getDouble("lunch_end_hour", 13.0),
                mPrefs.getDouble("work_end_hour", 18.5),
                mPrefs.getInt("pay_day", 15)
        );
    }

    private void refresh() {
        if (mCalc == null) return;

        long remain = mCalc.remainSeconds();
        long worked = mCalc.workedSeconds();
        long total = mCalc.totalWorkSeconds();
        double earned = mCalc.earnedToday();

        // 倒计时
        if (remain > 0) {
            long h = remain / 3600;
            long m = (remain % 3600) / 60;
            long s = remain % 60;
            mBinding.tvCountdown.setText(String.format(Locale.getDefault(),
                    "%02d:%02d:%02d", h, m, s));
        } else {
            mBinding.tvCountdown.setText("已下班！");
        }

        // 已赚
        mBinding.tvEarned.setText(String.format(Locale.getDefault(), "¥ %.2f", earned));
        mBinding.tvRate.setText(String.format(Locale.getDefault(),
                "赚钱速度：%.4f 元/秒", mCalc.perSecondWage()));

        // 进度条
        int pct = total > 0 ? (int) (worked * 100 / total) : 0;
        mBinding.progressBar.setProgress(pct);

        double workedH = worked / 3600.0;
        double totalH = total / 3600.0;
        mBinding.tvProgress.setText(String.format(Locale.getDefault(),
                "已工作 %.2f / %.2f 小时", workedH, totalH));

        // 其他信息
        mBinding.tvMonth.setText(String.format(Locale.getDefault(),
                "本月累计  ¥ %.2f", mCalc.monthEarned()));
        mBinding.tvPayday.setText(String.format(Locale.getDefault(),
                "距发薪日（%d号）  还有 %d 天", mCalc.payDay, mCalc.daysToPayday()));
        mBinding.tvHour.setText(String.format(Locale.getDefault(),
                "时薪  ¥ %.2f", mCalc.hourWage()));
        mBinding.tvDaypay.setText(String.format(Locale.getDefault(),
                "今日满勤  ¥ %.2f", mCalc.fullDayPay()));

        // 鼓励语
        mBinding.tvTip.setText(mCalc.tipText(earned));
    }

    @Override
    protected void onResume() {
        super.onResume();
        mHandler.post(mTick);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mHandler.removeCallbacks(mTick);
    }
}
