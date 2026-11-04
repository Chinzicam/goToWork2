package com.czc.gotowork.util;

import java.util.Calendar;

/**
 * 薪资计算工具：根据当前时间和配置计算工时、已赚金额等
 */
public class SalaryCalc {

    public final double salaryMonth;
    public final int workDaysMonth;
    public final double startHour;
    public final double lunchStart;
    public final double lunchEnd;
    public final double endHour;
    public final int payDay;

    public SalaryCalc(double salaryMonth, int workDaysMonth,
                      double startHour, double lunchStart, double lunchEnd,
                      double endHour, int payDay) {
        this.salaryMonth = salaryMonth;
        this.workDaysMonth = workDaysMonth;
        this.startHour = startHour;
        this.lunchStart = lunchStart;
        this.lunchEnd = lunchEnd;
        this.endHour = endHour;
        this.payDay = payDay;
    }

    /** 一天总工作秒数（扣除午休） */
    public long totalWorkSeconds() {
        double workStart = startHour;
        double workEnd = endHour;
        double lunchLen = lunchEnd - lunchStart;
        double hours = (workEnd - workStart) - lunchLen;
        return (long) (hours * 3600);
    }

    /** 已工作秒数 */
    public long workedSeconds() {
        Calendar now = Calendar.getInstance();
        double nowH = now.get(Calendar.HOUR_OF_DAY) + now.get(Calendar.MINUTE) / 60.0
                + now.get(Calendar.SECOND) / 3600.0;

        double lunchLen = lunchEnd - lunchStart;
        double totalHours = (endHour - startHour) - lunchLen;

        if (nowH <= startHour) return 0;
        if (nowH >= endHour) return (long) (totalHours * 3600);

        // 上午工时
        double morning = Math.min(nowH, lunchStart) - startHour;
        // 下午工时
        double afternoon = 0;
        if (nowH > lunchEnd) {
            afternoon = Math.min(nowH, endHour) - lunchEnd;
        } else if (nowH > lunchStart) {
            // 午休中，不计
        }
        return (long) ((morning + afternoon) * 3600);
    }

    /** 距离下班秒数 */
    public long remainSeconds() {
        return Math.max(0, totalWorkSeconds() - workedSeconds());
    }

    /** 时薪 */
    public double hourWage() {
        double dayHours = totalWorkSeconds() / 3600.0;
        return salaryMonth / (workDaysMonth * dayHours);
    }

    /** 每秒工资 */
    public double perSecondWage() {
        return hourWage() / 3600.0;
    }

    /** 今日已赚 */
    public double earnedToday() {
        return workedSeconds() * perSecondWage();
    }

    /** 今日满勤日薪 */
    public double fullDayPay() {
        return hourWage() * (totalWorkSeconds() / 3600.0);
    }

    /** 本月累计（按已过天数估算） */
    public double monthEarned() {
        Calendar now = Calendar.getInstance();
        int dayOfMonth = now.get(Calendar.DAY_OF_MONTH);
        return Math.max(0, dayOfMonth - 1) * fullDayPay();
    }

    /** 距离发薪日天数 */
    public int daysToPayday() {
        Calendar now = Calendar.getInstance();
        int day = now.get(Calendar.DAY_OF_MONTH);
        if (day < payDay) {
            return payDay - day;
        }
        // 下个月发薪
        Calendar next = (Calendar) now.clone();
        next.add(Calendar.MONTH, 1);
        next.set(Calendar.DAY_OF_MONTH, 1);
        int daysInNext = next.getActualMaximum(Calendar.DAY_OF_MONTH);
        int target = Math.min(payDay, daysInNext);
        // 粗略计算：剩余本月天数 + 目标日
        Calendar thisMonthEnd = (Calendar) now.clone();
        thisMonthEnd.set(Calendar.DAY_OF_MONTH, thisMonthEnd.getActualMaximum(Calendar.DAY_OF_MONTH));
        int restThisMonth = thisMonthEnd.get(Calendar.DAY_OF_MONTH) - day;
        return restThisMonth + target;
    }

    /** 根据已赚金额返回鼓励语 */
    public String tipText(double earned) {
        if (earned < 2) return "刚上班，钱还在赶来的路上～";
        if (earned < 5) return "两块钱到手，先买瓶水！";
        if (earned < 10) return "早餐钱有了，冲！";
        if (earned < 15) return "一杯奶茶钱到手了！";
        if (earned < 25) return "午餐钱稳了，今天不亏！";
        if (earned < 40) return "一天的饭钱都赚够了！";
        if (earned < 60) return "半天工资到手了，继续！";
        if (earned < 100) return "今天目标快达成了，稳住！";
        if (earned < 150) return "今天赚够了，美滋滋！";
        if (earned < 250) return "日薪快到手了，加油！";
        if (earned < 400) return "今天工资到位了，下班近在眼前！";
        return "今天这班上得值！";
    }
}
