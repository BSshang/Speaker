package com.chogge.speaker.utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.widget.Toast;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Date;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author xuehao.jin
 * @time 2016年2月16日 下午5:29:14
 */

public class MyUtils {
    private static Toast mToast;

    public static void showToast(Context mContext, String msg) {
        if (mToast == null) {
            mToast = Toast.makeText(mContext.getApplicationContext(), "", Toast.LENGTH_SHORT);
        }
        mToast.setText(msg);
        mToast.show();
    }

    public static void showToastLong(Context mContext, String msg) {
        if (mToast == null) {
            mToast = Toast.makeText(mContext.getApplicationContext(), "", Toast.LENGTH_LONG);
        }
        mToast.setText(msg);
        mToast.show();
    }

    /**
     * 打印log，tag名是Altria
     * @param content 内容
     */
    public static void printLog(String content) {
        Log.e("Altria", content);
    }

    /**
     * 打印log，tag名是Altria
     * 不受4K限制
     * @param str
     */
    public static void printLogUnlimited(String str) {
        str = str.trim();
        int index = 0;
        int maxLength = 4000;
        String sub;
        while (index < str.length()) {
            // java的字符不允许指定超过总的长度end
            if (str.length() <= index + maxLength) {
                sub = str.substring(index);
            } else {
                sub = str.substring(index, index + maxLength);
            }

            index += maxLength;
            Log.e("Altria", sub.trim());
        }
    }

    public static boolean deleteFile(File file) {
        boolean res = false;
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (int i = 0; i < files.length; i++) {
                File f = files[i];
                deleteFile(f);
            }
            res = file.delete();//如要保留文件夹，只删除文件，请注释这行
        } else if (file.exists()) {
            res = file.delete();
        }
        return res;
    }

    /**
     * 获取版本名称
     * @return
     */
    public static String getVersionName(Context context) {
        PackageManager manager = context.getApplicationContext().getPackageManager();//获取包管理器
        try {
            //通过当前的包名获取包的信息
            PackageInfo info = manager.getPackageInfo(context.getApplicationContext().getPackageName(),0);//获取包对象信息
            return  info.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 获取版本号
     * @return
     */
    public static int getVersionCode(Context context) {
        PackageManager manager = context.getApplicationContext().getPackageManager();//获取包管理器
        try {
            //通过当前的包名获取包的信息
            PackageInfo info = manager.getPackageInfo(context.getApplicationContext().getPackageName(), 0);//获取包对象信息
            return  info.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return 1;
    }

    /***
     * Base64 解码
     */
    public static byte[] decodeBase64(String input) throws Exception {
        return Base64.decode(input, Base64.DEFAULT);
    }

    /**
     * md5加密
     *
     * @param string
     * @return
     */
    public static String md5(String string) {
        byte[] hash;
        try {
            hash = MessageDigest.getInstance("MD5").digest(
                    string.getBytes("UTF-8"));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Huh, MD5 should be supported?", e);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Huh, UTF-8 should be supported?", e);
        }

        StringBuilder hex = new StringBuilder(hash.length * 2);
        for (byte b : hash) {
            if ((b & 0xFF) < 0x10)
                hex.append("0");
            hex.append(Integer.toHexString(b & 0xFF));
        }
        return hex.toString();
    }

    /**
     * 手机是否链接WIFI网络
     *
     * @return boolean true 手机是否链接的是WIFI网络
     */
    public static boolean isWifiConnect(Context context) {
        ConnectivityManager connManager = (ConnectivityManager) context.getApplicationContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mWifi = connManager
                .getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        return mWifi.isConnected();
    }

    /**
     * 手机是否的移动网络
     *
     * @return boolean true 手机是否链接的是移动网络
     */
    public static boolean isMobileConnect(Context context) {
        ConnectivityManager connManager = (ConnectivityManager) context.getApplicationContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo m3G = connManager
                .getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        return m3G.isConnected();
    }

    /**
     * 网络是否可用
     *
     * @param context
     * @return
     */
    public static boolean networkIsActive(Context context) {
        // 获取手机所有连接管理对象（包括对wi-fi,net等连接的管理）
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getApplicationContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager == null) {
            return false;
        } else {
            // 获取NetworkInfo对象
            NetworkInfo[] networkInfo = connectivityManager.getAllNetworkInfo();

            if (networkInfo != null && networkInfo.length > 0) {
                for (int i = 0; i < networkInfo.length; i++) {
                    // 判断当前网络状态是否为连接状态
                    if (networkInfo[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * dip 转换成 px
     *
     * @param dip
     * @param context
     * @return
     */
    public static float dp2Px(float dip, Context context) {
        DisplayMetrics displayMetrics = context.getApplicationContext().getResources()
                .getDisplayMetrics();
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip,
                displayMetrics);
    }

    /**
     * 将sp值转换为px值，保证文字大小不变
     *
     * @param spValue
     * @return
     */
    public static float sp2px(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return spValue * fontScale + 0.5f;
    }

    /**
     * 将float转换成double
     *
     * @return
     */
    public static float double2float(double d) {
        BigDecimal bigDecimal = new BigDecimal(String.valueOf(d));
        return bigDecimal.floatValue();
    }

    /**
     * 将double转换成float
     *
     * @return
     */
    public static double float2double(Float f) {
        BigDecimal bigDecimal = new BigDecimal(String.valueOf(f));
        return bigDecimal.doubleValue();
    }

    /**
     * 获取手机语言
     *
     * @return <br>
     * zh_Hant 中文繁体 <br>
     * zh_Hans 中文简体 <br>
     * en 英文
     */
    public static String getLanguage() {
        Locale locale = Locale.getDefault();
        String country = locale.getCountry();

        StringBuffer language = new StringBuffer(locale.getLanguage());
        language.append("-");
        language.append(country);
        return language.toString().toLowerCase();
    }

    /**
     * 将byte数组转为十六进制字符串
     *
     * @param byteArray byte[]
     * @return String 十六进制字符串
     */
    public static String bytesToHexString(byte[] byteArray) {
        StringBuffer re = new StringBuffer();
        for (int i = 0; i < byteArray.length; i++) {
            re.append(to16(byteArray[i]));
        }
        return re.toString();
    }

    /**
     * 将byte数组转为十六进制字符串 制定长度
     *
     * @param byteArray byte[]
     * @return String 十六进制字符串
     */
    public static String bytesToHexStringLimited(byte[] byteArray, int len) {
        StringBuffer re = new StringBuffer();
        for (int i = 0; i < len; i++) {
            re.append(to16(byteArray[i]));
        }
        return re.toString();
    }

    /**
     * 十六进制字符串转为 btye 数组
     * @return byte数组
     */
    public static byte[] hexStringToByte(String dataString) {
        int subPosition = 0;
        int byteLenght = dataString.length() / 2;
        byte[] result = new byte[byteLenght];
        try {
            for (int i = 0; i < byteLenght; i++) {
                String s = dataString.substring(subPosition, subPosition + 2);
                result[i] = (byte) Integer.parseInt(s, 16);
                subPosition = subPosition + 2;
            }
        } catch (Exception e) {
            return null;
        }
        return result;
    }

    /**
     * int型转换成16进制数
     *
     * @param b
     * @return
     */
    public static String to16(int b) {
        String hexString = Integer.toHexString(b);
        int lenth = hexString.length();
        if (lenth == 1) {
            hexString = "0" + hexString;
        }
        if (lenth > 2) {
            hexString = hexString.substring(lenth - 2, lenth);
        }
        return hexString;
    }

    /**
     * 是否是email
     *
     * @return boolean正确的邮箱格式
     */
    public static boolean isEmail(String str) {
        String regex = "^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$";
        return match(regex, str);
    }

    /**
     * @param regex 正则表达式字符串
     * @param str   要匹配的字符串
     * @return 如果str 符合 regex的正则表达式格式,返回true, 否则返回 false;
     */
    private static boolean match(String regex, String str) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(str);
        return matcher.matches();
    }

    public static String getToday(){
        Calendar c = Calendar.getInstance();
        String day = String.valueOf(c.get(Calendar.DAY_OF_WEEK));
        if("1".equals(day)){
            day ="日";
        }else if("2".equals(day)){
            day ="一";
        }else if("3".equals(day)){
            day ="二";
        }else if("4".equals(day)){
            day ="三";
        }else if("5".equals(day)){
            day ="四";
        }else if("6".equals(day)){
            day ="五";
        }else if("7".equals(day)){
            day ="六";
        }
        return formatDate(c.getTimeInMillis(), "yyyy-MM-dd HH:mm") + " 星期" + day;
    }


    /**
     * 时间戳 时间格式化 yyyy-MM-dd HH:mm:ss
     */
    public static String formatDate(long oldString) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        String dateString = dateFormat.format(oldString);
        return dateString;
    }

    /**
     * 时间格式化 yyyy-MM-dd HH:mm:ss
     */
    public static String formatDate(String oldString) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss");
        String dateString = dateFormat.format(Date.valueOf(oldString));
        return dateString;
    }

    /**
     * 时间格式化
     *
     * @param oldString
     * @param how 制定格式
     * @return
     */
    public static String formatDate(String oldString, String how) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(how);
        String dateString = dateFormat.format(Date.valueOf(oldString));
        return dateString;
    }

    /**
     * 时间格式化
     *
     * @param oldString
     * @param how 制定格式
     * @return
     */
    public static String formatDate(long oldString, String how) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                how, Locale.getDefault());
        String dateString = dateFormat.format(oldString);
        return dateString;
    }

    public static String secToTime(int time) {
        String timeStr = null;
        int hour = 0;
        int minute = 0;
        int second = 0;
        if (time <= 0)
            return "00:00";
        else {
            minute = time / 60;
            if (minute < 60) {
                second = time % 60;
                timeStr = unitFormat(minute) + ":" + unitFormat(second);
            } else {
                hour = minute / 60;
                if (hour > 99)
                    return "99:59:59";
                minute = minute % 60;
                second = time - hour * 3600 - minute * 60;
                timeStr = unitFormat(hour) + ":" + unitFormat(minute) + ":" + unitFormat(second);
            }
        }
        return timeStr;
    }

    public static String unitFormat(int i) {
        String retStr = null;
        if (i >= 0 && i < 10)
            retStr = "0" + Integer.toString(i);
        else
            retStr = "" + i;
        return retStr;
    }

    public static String convertFileSize(long size) {
        long kb = 1024;
        long mb = kb * 1024;
        long gb = mb * 1024;

        if (size >= gb) {
            return String.format("%.1f GB", (float) size / gb);
        } else if (size >= mb) {
            float f = (float) size / mb;
            return String.format(f > 100 ? "%.0f MB" : "%.1f MB", f);
        } else if (size >= kb) {
            float f = (float) size / kb;
            return String.format(f > 100 ? "%.0f KB" : "%.1f KB", f);
        } else
            return String.format("%d bfile", size);
    }

    public static boolean isDebugVersion(Context context) {
        try {
            ApplicationInfo info = context.getApplicationInfo();
            return (info.flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static String clearWebService(String body){
        return body.replace("<string xmlns=\"http://tempuri.org/\">", "")
                .replace("<?xml version=\"1.0\" encoding=\"utf-8\"?>", "")
                .replace("</string>", "");
    }

    /**
     * 字符串是否为数字
     * @param str
     * @return
     */
    public static boolean isNumeric(String str) {
        boolean isNumber = true;
        try {
            Float.parseFloat(str);
        }catch (Exception e){
            isNumber = false;
            e.printStackTrace();
        }
        return isNumber;
    }

    /**
     * 格式化数字，保留2位小数
     * @param data
     * @return
     */
    public static String formateDecimal(float data){
        DecimalFormat decimalFormat=new DecimalFormat(".00");
        String p = decimalFormat.format(data);
        return p;
    }
}
