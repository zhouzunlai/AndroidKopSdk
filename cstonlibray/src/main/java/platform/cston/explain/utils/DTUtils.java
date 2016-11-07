package platform.cston.explain.utils;

import android.util.Log;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by daifei on 2016/9/3.
 */
public class DTUtils {

    //四舍五入操作,并且保留一位数的操作
    public  static double halfUp(double num,int precision)
    {
        String strNum=Double.toString(num);
        BigDecimal bd=new BigDecimal(strNum);
        return bd.setScale(precision,BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    //对开销进行处理
    public static String getExpense(double expenses)
    {
        if(Double.compare(expenses,0)<0||Double.compare(expenses,0)==0)
        {
            return  "0";
        }
        else if(Double.compare(expenses ,0.1)<0&&Double.compare(expenses,0.0)>0)
        {
            return  "0.1";
        }
        else if(Double.compare(expenses,0.1)>0&& Double.compare(expenses,100)<0)
        {

            if(Double.compare(halfUp(expenses,1),100)>=0)
            {
                String strExpenses=Double.toString(halfUp(expenses,1));
                Log.i("strExpenses","strExpenses====>"+strExpenses);
                if(strExpenses.contains(".0"))
                {
                    Log.i("strExpenses","strExpensesINT====>"+Integer.toString((int)expenses));
                    return Integer.toString((int)halfUp(expenses,1));
                }
            }
            else
            {
                return Double.toString(halfUp(expenses,1));
            }
        }
        else if(Double.compare(expenses,100)>0||Double.compare(expenses,100)==0)
        {//对小数点后一位的数据进行判断
            String strExpenses=Double.toString(halfUp(expenses,1));
            //如果包含小数点后一位是0,则只显示整数
            if(strExpenses.contains(".0"))
            {
                return Integer.toString((int)halfUp(expenses,1));
            }
        }

        return Double.toString(halfUp(expenses,1));

    }

    //对耗油量进行处理
    public static String getOilMass(double oilMass)
    {
        if(Double.compare(oilMass,0)<0||Double.compare(oilMass,0)==0)
        {
            return  "0";
        }
        else if(Double.compare(oilMass ,0.1)<0&&Double.compare(oilMass,0.0)>0)
        {
            return  Double.toString(halfUp(0.1d,1));
        }
        else if(Double.compare(oilMass,0.1)>0&& Double.compare(oilMass,100)<0)
        {
            return Double.toString(halfUp(oilMass,1));
        }
        else if(Double.compare(oilMass,100)>=0)
        {//对小数点后一位的数据进行判断
            String strExpenses=Double.toString(oilMass);
            //如果包含小数点后一位是0,则只显示整数
            if(strExpenses.contains(".0"))
            {
                return Integer.toString((int)oilMass);
            }
        }
        return Double.toString(halfUp(oilMass,1));

    }

    //对行驶里程进行计算
    public static String  getMileage(double mileage)
    {
        if(Double.compare(mileage,0)<0||Double.compare(mileage,0)==0)
        {
            return "0";
        }
        else if(Double.compare(mileage,0)>0&&Double.compare(mileage,0.1)<=0)
        {
            return "0.1";
        }
        else if(Double.compare(mileage,0.1)>0&&Double.compare(mileage,100.0)<0)
        {
            //如果大于10
            if(Double.compare(mileage,10.0)>=0)
            {
                String strMileage=Double.toString(mileage);
                if(strMileage.contains(".0"))
                {
                    return Integer.toString((int)mileage);
                }
            }

            //如果四舍五入之后的格式为10.0
            if(Double.compare(halfUp(mileage,1),10.0)>=0&&Double.compare(halfUp(mileage,1),100.0)<0)
            {
                String strMileage=Double.toString(halfUp(mileage,1));
                if(strMileage.contains(".0"))
                {
                    return Integer.toString((int)halfUp(mileage,1));
                }
            }
//            Log.i("strMileage","mid====>"+Double.toString(halfUp(mileage,1)));

            if(Double.compare(halfUp(mileage,1),10)==0)
            {
                return "10";
            }
            if(Double.compare(halfUp(mileage,1),100)==0)
            {
                return "100";
            }
            return Double.toString(halfUp(mileage,1));
        }
        else if(Double.compare(mileage,100.0)>=0)
        {
            String strMileage=Double.toString(mileage);
            if(strMileage.contains(".0")||strMileage.contains(".1")
                    ||strMileage.contains(".2")||strMileage.contains(".3")||strMileage.contains(".4"))
            {
                return Integer.toString((int)mileage);
            }
            else
            {
                return Integer.toString((int)(mileage+1));
            }
        }
        return Double.toString(halfUp(mileage,1));
    }


    //驾驶时间,这里传入的参数为秒
    public static String getDriveTime(double driveTime)
    {
        if(Double.compare(driveTime,0)<0||Double.compare(driveTime,0)==0)
        {
            return "0";
        }
        else if(Double.compare(driveTime,0.0d)>0&&Double.compare(driveTime,1.0d)<=0)
        {
            return "1";
        }
        else if(Double.compare(driveTime,1.0d)>0&&Double.compare(driveTime,90.0d)<=0)
        {
            return Integer.toString((int)halfUp(driveTime,0));
        }
        else if(Double.compare(driveTime,90)>0)
        {
            driveTime/=60;
            String strDriveTime=Double.toString(driveTime);
            if(strDriveTime.contains(".0"))
            {
                return Integer.toString((int)driveTime);
            }
            else
            {
                strDriveTime=Double.toString(halfUp(driveTime,1));
                if(strDriveTime.contains(".0"))
                {
                    return Integer.toString((int)halfUp(driveTime,1));
                }
            }
        }


        return Double.toString(halfUp(driveTime,1));
    }


    //平局速度
    public static String getAveSpeed(double averageSpeed)
    {
        if(Double.compare(averageSpeed,0)<=0)
        {
            return "0";
        }
        else if(Double.compare(averageSpeed,0)>0&&Double.compare(averageSpeed,0.1)<=0)
        {
            return "0.1";
        }
        else if(Double.compare(averageSpeed,0.1)>0&&Double.compare(averageSpeed,100)<0)
        {
            if(Double.compare(averageSpeed,10)>0)
            {
                String strAveSpeed=Double.toString(averageSpeed);
                if(strAveSpeed.contains(".0"))
                {
                    return Integer.toString((int)averageSpeed);
                }
            }
            return Double.toString(halfUp(averageSpeed,1));
        }
        return Integer.toString((int)halfUp(averageSpeed,0));
    }

    //平均油耗
    public static String getAveOilMass(double aveOilMass)
    {
        if(Double.compare(aveOilMass,0.0)<=0)
        {
            return "0";
        }
        else if(Double.compare(aveOilMass,0.0)>0&&Double.compare(aveOilMass,0.1)<=0)
        {
            return "0.1";
        }
        else if(Double.compare(aveOilMass,0.1)>0&&Double.compare(aveOilMass,99)<0)
        {
            if(Double.compare(aveOilMass,10.0d)>0)
            {
                String strAveOilMass=Double.toString(aveOilMass);
                if(strAveOilMass.contains(".0"))
                {
                    return Integer.toString((int)aveOilMass);
                }
            }
            return Double.toString(halfUp(aveOilMass,1));
        }
        return "99";
    }


    public static String getTotalMileage(double totalMileage)
    {
        if(Double.compare(totalMileage,0)<=0)
        {
            return "0";
        }
        else if(Double.compare(totalMileage,0)>0&&Double.compare(totalMileage,0.1)<=0)
        {
            return "0.1";
        }
        else
        {
            String strTotalMileage=Double.toString(totalMileage);
            if(strTotalMileage.contains(".0"))
            {
                return Integer.toString((int)totalMileage);
            }
        }
        return Double.toString(halfUp(totalMileage,1));
    }


    public static double StrToDouble(String str)
    {
        return  Double.parseDouble(str);
    }

    public static int StrToInt(String str)
    {
        return  Integer.parseInt(str);
    }

    //将字符串转换为日期
    public static Date StrToDate(String str)
    {
//        String time="2010-11-20 11:10:10";

        Date date=null;
        SimpleDateFormat formatter=new SimpleDateFormat("yyyyMMdd");
        try{
            date=formatter.parse(str);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return date;
    }

    //转化为20160822这样的字符串
    public static String DateToStr(Date date)
    {
        SimpleDateFormat formatter=new SimpleDateFormat("yyyyMMdd");
        String time=formatter.format(date);
        return time;
    }

    public static String DateToStrMMDD(Date date)
    {
        SimpleDateFormat formatter=new SimpleDateFormat("MM月dd日");
        String time=formatter.format(date);
        return time;
    }

    public static String DateToStrYear(Date date)
    {
        SimpleDateFormat formatter=new SimpleDateFormat("yyyy");
        String time=formatter.format(date);
        return time;
    }

    public static String LongToStrTimeDay(long time)
    {
        Date date=new Date(time);
        SimpleDateFormat formatter=new SimpleDateFormat("yyyyMMdd");
        return formatter.format(date);
    }

    public static String LongToStrTimeDayC(long time)
    {
        Date date=new Date(time);
        SimpleDateFormat formatter=new SimpleDateFormat("MM月dd");
        return formatter.format(date);
    }


    public static String LongToStrTimeDayLine(long time)
    {
        Date date=new Date(time);
        SimpleDateFormat formatter=new SimpleDateFormat("yyyy-MM-dd");
        return formatter.format(date);
    }


    public static String LongToStrTimeMonth(long time)
    {
        Date date=new Date(time);
        SimpleDateFormat formatter=new SimpleDateFormat("yyyyMM");
        return formatter.format(date);
    }

    public static String LongToStrTimeYear(long time)
    {
        Date date=new Date(time);
        SimpleDateFormat formatter=new SimpleDateFormat("yyyy");
        return formatter.format(date);
    }




    public static String LongToStrYMDHMS(long time)
    {
        Date date=new Date(time);
        SimpleDateFormat formatter=new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
        return formatter.format(date);
    }


    public static String LongToStrYMDHMSL(long time)
    {
        Date date=new Date(time);
        SimpleDateFormat formatter=new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        return formatter.format(date);
    }

    public static long StrTimeToLong(String time)
    {
         long longTime=0;
        SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMdd");
        try {
            longTime=sdf.parse(time).getTime();
        }catch (ParseException e){
            e.printStackTrace();;
        }
        return longTime;
    }

    public static long StrTimeMonthToLong(String time)
    {
        long longTime=0;
        SimpleDateFormat sdf=new SimpleDateFormat("yyyyMM");
        try {
            longTime=sdf.parse(time).getTime();
        }catch (ParseException e){
            e.printStackTrace();;
        }
        return longTime;
    }

    public static long StrTimeToLongLine(String time)
    {
        long longTime=0;
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            longTime=sdf.parse(time).getTime();
        }catch (ParseException e)
        {
            e.printStackTrace();;
        }
        return longTime;
    }

    public static long StrTimeToLongYear(String time)
    {
        long longTime=0;
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy");
        try {
            longTime=sdf.parse(time).getTime();
        }
        catch (ParseException e)
        {
            e.printStackTrace();;
        }
        return longTime;
    }

    /**
     *将时间转换为MM月dd日
     * @return
     */
    public static String StrTimeToMMDD(String[] time)
    {
        if(time.length!=3)
            return "";
        String month=time[1];
        String day=time[2];
        if(month.startsWith("0"))
        {
            month=month.substring(1);
        }
        if(day.startsWith("0"))
        {
            day=day.substring(1);
        }
        return  month+"月"+day+"日";
    }

    //10:23格式
    public static String StrToHHmm(String time)
    {
       time=time.substring(8);
        return time.substring(0,2)+":"+time.substring(2,4);
    }

    public static String StrToDayDate(String time)
    {
        String year=time.substring(0,4);
        String month=time.substring(4,6);
        String day=time.substring(6,8);
        if(month.startsWith("0"))
        {
            month=month.substring(1,2);
        }

        if(day.startsWith("0"))
        {
            day=day.substring(1,2);
        }
        return year+"/"+month+"/"+day;

    }


    public static String CheckEventName(String category,String type)
    {
        if(category.equals("security"))
        {
            if(type.equals("crash"))
            {
                return "碰撞";
            }
            else if(type.equals("drag"))
            {
                return "拖吊";
            }
            else if(type.equals("open"))
            {
                return "非法开门";
            }
            else if(type.equals("ignition"))
            {
                return "非法点火";
            }
            else if(type.equals("turnOver"))
            {
                return "翻车";
            }
            else if(type.equals("onGuard"))
            {
                return "汽车设防";
            }
            else if(type.equals("offGuard"))
            {
                return "汽车撤防";
            }
            else if(type.equals("lockFailed"))
            {
                return "锁车失败";
            }
            else if(type.equals("guardOvertime"))
            {
                return "超时未设防";
            }
            else if(type.equals("glassUnclosed"))
            {
                return "设防玻璃未关";
            }
        }
        else if(category.equals("drivingBehavior"))
        {
            if(type.equals("accelerate"))
            {
                return "急加速";
            }
            else if(type.equals("dccelerate"))
            {
                return "急减速";
            }
            else if(type.equals("sharpTurn"))
            {
                return "急转弯";
            }
            else if(type.equals("fatigue"))
            {
                return "疲劳驾驶";
            }
            else if(type.equals("overSpeed"))
            {
                return "超速";
            }
            else if(type.equals("slide"))
            {
                return "高速空挡滑行";
            }
            else if(type.equals("idleSpeed"))
            {
                return "怠速过长";
            }
        }
        return "null";
    }


    /**
     *获取某某年某月的最后一天
     * @param year
     * @param month
     * @return
     */
    public static String getLastDayOfMonth(int year,int month)
    {
        Calendar cal = Calendar.getInstance();
        //设置年份
        cal.set(Calendar.YEAR,year);
        //设置月份
        cal.set(Calendar.MONTH, month-1);
        //获取某月最大天数
        int lastDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
        //设置日历中月份的最大天数
        cal.set(Calendar.DAY_OF_MONTH, lastDay);
        //格式化日期
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String lastDayOfMonth = sdf.format(cal.getTime());

        return lastDayOfMonth;
    }

    public static String getToday(String formate)
    {


        Date date=new Date();
        SimpleDateFormat formatter=new SimpleDateFormat(formate);
        return formatter.format(date);
    }
}
