package com.czc.gotowork.ui;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.czc.gotowork.databinding.ActivitySettingsBinding;
import com.czc.gotowork.util.Prefs;

/**
 * 设置页：填写薪资参数，保存到 SharedPreferences
 */
public class SettingsActivity extends AppCompatActivity {

    private ActivitySettingsBinding mBinding;
    private Prefs mPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivitySettingsBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        mPrefs = new Prefs(this);

        // 回显已有配置
        mBinding.etSalaryMonth.setText(fmt(mPrefs.getDouble("salary_month", 8000)));
        mBinding.etWorkDays.setText(String.valueOf(mPrefs.getInt("work_days_month", 22)));
        mBinding.etStartHour.setText(fmt(mPrefs.getDouble("work_start_hour", 9.0)));
        mBinding.etLunchStart.setText(fmt(mPrefs.getDouble("lunch_start_hour", 12.0)));
        mBinding.etLunchEnd.setText(fmt(mPrefs.getDouble("lunch_end_hour", 13.0)));
        mBinding.etEndHour.setText(fmt(mPrefs.getDouble("work_end_hour", 18.5)));
        mBinding.etPayDay.setText(String.valueOf(mPrefs.getInt("pay_day", 15)));

        mBinding.btnSave.setOnClickListener(v -> save());
    }

    private String fmt(double v) {
        if (v == (long) v) return String.valueOf((long) v);
        return String.valueOf(v);
    }

    private void save() {
        try {
            double salary = Double.parseDouble(mBinding.etSalaryMonth.getText().toString().trim());
            int days = Integer.parseInt(mBinding.etWorkDays.getText().toString().trim());
            double start = Double.parseDouble(mBinding.etStartHour.getText().toString().trim());
            double ls = Double.parseDouble(mBinding.etLunchStart.getText().toString().trim());
            double le = Double.parseDouble(mBinding.etLunchEnd.getText().toString().trim());
            double end = Double.parseDouble(mBinding.etEndHour.getText().toString().trim());
            int payDay = Integer.parseInt(mBinding.etPayDay.getText().toString().trim());

            // 校验时间顺序
            if (!(start < ls && ls < le && le < end && end <= 24)) {
                Toast.makeText(this, "请检查时间顺序：上班 < 午休开始 < 午休结束 < 下班", Toast.LENGTH_LONG).show();
                return;
            }

            mPrefs.setDouble("salary_month", salary);
            mPrefs.setInt("work_days_month", days);
            mPrefs.setDouble("work_start_hour", start);
            mPrefs.setDouble("lunch_start_hour", ls);
            mPrefs.setDouble("lunch_end_hour", le);
            mPrefs.setDouble("work_end_hour", end);
            mPrefs.setInt("pay_day", payDay);

            setResult(RESULT_OK);
            finish();
        } catch (Exception e) {
            Toast.makeText(this, "请填写有效数字", Toast.LENGTH_SHORT).show();
        }
    }
}
