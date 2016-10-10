package com.leontran.themobilevn.utils;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.Signature;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.text.format.DateUtils;
import android.util.Base64;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Transformation;

import com.google.gson.Gson;
import com.leontran.themobilevn.model.CitiProvinceData;
import com.leontran.themobilevn.model.GenericLoadListProductCustomize;
import com.leontran.themobilevn.model.ListCitiProvinceData;
import com.leontran.themobilevn.model.ListSimpleObjectData;
import com.leontran.themobilevn.model.SimpleObjectType;
import com.leontran.themobilevn.model.UserProfileData;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;


public class MPreferenceManager {

    public static final String KEY_ACCESS = "451846b553156450940c03157a4f61e5";

    private static Utils mUtils;
    private static Context mContext;

    public static final String PREF_KEYNAME_ACCESS_TOKEN = "accessTK";
    public static final String PREF_KEYNAME_PRODUCT_ID = "procId";
    public static final String PREF_KEYNAME_CITIES_PROVINCES = "tinh_thanh_pho_";
    public static final String PREF_KEYNAME_PRICE_RANGE = "khoang_gia";
    public static final String PREF_KEYNAME_BRAND = "nha_san_suat";
    public static final String PREF_KEYNAME_DEVICE_TYPE = "loai_thiet_bi";
    public static final String PREF_KEYNAME_CUSTOMIZE_SEARCH = "tim_kiem_nang_cao";
    public static final String PREF_KEYNAME_USER_PROFILE = "nguoi_dung_dang_nhap";

    private MPreferenceManager() {

    }

    public static Context getApplicationContext() {
        return mContext.getApplicationContext();
    }

    public static Context getContext() {
        return mContext;
    }

    public static void setContext(Context context) {

        // applicationContext = context.getApplicationContext();
        mContext = context;
        if (mUtils == null) {
            mUtils = new Utils(context);
        }
        ImageLoaderConfiguration.Builder config = new ImageLoaderConfiguration.Builder(
                context);
        config.threadPriority(Thread.NORM_PRIORITY - 2);
        config.denyCacheImageMultipleSizesInMemory();
        config.diskCacheFileNameGenerator(new Md5FileNameGenerator());
        config.diskCacheSize(50 * 1024 * 1024); // 50 MiB
        config.tasksProcessingOrder(QueueProcessingType.FIFO);
        ImageLoader.getInstance().init(config.build());
    }

    static int clearCacheFolder(final File dir, final int numDays) {

        int deletedFiles = 0;
        if (dir != null && dir.isDirectory()) {
            try {
                for (File child : dir.listFiles()) {

                    // first delete subdirectories recursively
                    if (child.isDirectory()) {
                        deletedFiles += clearCacheFolder(child, numDays);
                    }

                    // then delete the files and subdirectories in this dir
                    // only empty directories can be deleted, so subdirs have
                    // been done first
                    if (child.lastModified() < new Date().getTime() - numDays
                            * DateUtils.DAY_IN_MILLIS) {
                        if (child.delete()) {
                            deletedFiles++;
                        }
                    }
                }
            } catch (Exception e) {
            }
        }
        return deletedFiles;
    }

    public static void clearCache(final Context context, final int numDays) {
        int numDeletedFiles = clearCacheFolder(context.getCacheDir(), numDays);
    }

    public static boolean isConnectionAvailable() {
        return mUtils.isConnectionAvailable();
    }

    public static boolean is3G() {
        ConnectivityManager connectivityManager = (ConnectivityManager) mContext
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mobileInfo = connectivityManager
                .getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (mobileInfo != null && mobileInfo.isConnected()) {
            return true;
        }
        return false;
    }

    public static void setAccessToken(String accessToken) {
        if (mUtils != null) {
            mUtils.setStringPref(PREF_KEYNAME_ACCESS_TOKEN, accessToken);
        }
    }

    public static String getAccessToken() {
        if (mUtils != null && mUtils.hasKey(PREF_KEYNAME_ACCESS_TOKEN)) {
            return mUtils.getStringPref(PREF_KEYNAME_ACCESS_TOKEN);
        }
        return null;
    }

    public static void setProductId(String accessToken) {
        if (mUtils != null) {
            mUtils.setStringPref(PREF_KEYNAME_PRODUCT_ID, accessToken);
        }
    }

    public static String getProductId() {
        if (mUtils != null && mUtils.hasKey(PREF_KEYNAME_PRODUCT_ID)) {
            return mUtils.getStringPref(PREF_KEYNAME_PRODUCT_ID);
        }
        return null;
    }

    public static void setCitiesProvinceList(String dataString) {
        if (mUtils != null) {
            mUtils.setStringPref(PREF_KEYNAME_CITIES_PROVINCES, dataString);
        }
    }

    public static ListCitiProvinceData getCitiesProvinceList() {
        if (mUtils != null && mUtils.hasKey(PREF_KEYNAME_CITIES_PROVINCES)) {
            String strJSON = mUtils.getStringPref(PREF_KEYNAME_CITIES_PROVINCES);
            return ((new Gson()).fromJson(strJSON, ListCitiProvinceData.class));
        }
        return null;
    }

    public static void setPriceRange(String dataString) {
        if (mUtils != null) {
            mUtils.setStringPref(PREF_KEYNAME_PRICE_RANGE, dataString);
        }
    }

    public static ListSimpleObjectData getPriceRange() {
        if (mUtils != null && mUtils.hasKey(PREF_KEYNAME_PRICE_RANGE)) {
            String strJSON = mUtils.getStringPref(PREF_KEYNAME_PRICE_RANGE);
            return ((new Gson()).fromJson(strJSON, ListSimpleObjectData.class));
        }
        return null;
    }

    public static void setBrand(String dataString) {
        if (mUtils != null) {
            mUtils.setStringPref(PREF_KEYNAME_BRAND, dataString);
        }
    }

    public static ListSimpleObjectData getBrand() {
        if (mUtils != null && mUtils.hasKey(PREF_KEYNAME_BRAND)) {
            String strJSON = mUtils.getStringPref(PREF_KEYNAME_BRAND);
            return ((new Gson()).fromJson(strJSON, ListSimpleObjectData.class));
        }
        return null;
    }

    public static void setDeviceType(String dataString) {
        if (mUtils != null) {
            mUtils.setStringPref(PREF_KEYNAME_DEVICE_TYPE, dataString);
        }
    }

    public static ListSimpleObjectData getDeviceType() {
        if (mUtils != null && mUtils.hasKey(PREF_KEYNAME_DEVICE_TYPE)) {
            String strJSON = mUtils.getStringPref(PREF_KEYNAME_DEVICE_TYPE);
            return ((new Gson()).fromJson(strJSON, ListSimpleObjectData.class));
        }
        return null;
    }

    public static void setUserProfile(String dataString) {
        if (mUtils != null) {
            mUtils.setStringPref(PREF_KEYNAME_USER_PROFILE, dataString);
        }
    }

    public static UserProfileData getUserProfile() {
        if (mUtils != null && mUtils.hasKey(PREF_KEYNAME_USER_PROFILE)) {
            String strJSON = mUtils.getStringPref(PREF_KEYNAME_USER_PROFILE);
            return ((new Gson()).fromJson(strJSON, UserProfileData.class));
        }
        return null;
    }

    public static void setCustomizeSearch(String dataString) {
        if (mUtils != null) {
            mUtils.setStringPref(PREF_KEYNAME_CUSTOMIZE_SEARCH, dataString);
        }
    }

    public static GenericLoadListProductCustomize getCustomizeSearch() {
        if (mUtils != null && mUtils.hasKey(PREF_KEYNAME_CUSTOMIZE_SEARCH)) {
            String strJSON = mUtils.getStringPref(PREF_KEYNAME_CUSTOMIZE_SEARCH);
            return ((new Gson()).fromJson(strJSON, GenericLoadListProductCustomize.class));
        }
        return null;
    }


    public static String getStringDataByKey(String key) {
        if (mUtils != null && mUtils.hasKey(key)) {
            return mUtils.getStringPref(key);
        }
        return null;
    }

    public static void setIntDataByKey(int data, String key) {
        if (mUtils != null) {
            mUtils.setIntPref(key, data);
        }
    }

    public static int getIntDataByKey(String key) {
        if (mUtils != null && mUtils.hasKey(key)) {
            return mUtils.getIntPref(key);
        }
        return -1;
    }

    public static void setFloatDataByKey(float data, String key) {
        if (mUtils != null) {
            mUtils.setFloatPref(key, data);
        }
    }

    public static float getFloatDataByKey(String key) {
        if (mUtils != null && mUtils.hasKey(key)) {
            return mUtils.getFloatPref(key);
        }
        return -1;
    }

    public static void setBoolDataByKey(boolean data, String key) {
        if (mUtils != null) {
            mUtils.setBoolPref(key, data);
        }
    }

    public static boolean getBoolDataByKey(String key) {
        if (mUtils != null && mUtils.hasKey(key)) {
            return mUtils.getBooleanPref(key);
        }
        return false;
    }



    public static <T> T getClassFromJson(String json, Class<T> cl) {
        return (new Gson()).fromJson(json, cl);
    }

    public static String getJsonFromObject(Object obj) {
        return (new Gson()).toJson(obj);
    }

    public static final int CACH_TIMEOUT_ONEMIN = 60 * 1000;
    public static final int CACH_TIMEOUT_ONEHOUR = 60 * CACH_TIMEOUT_ONEMIN;
    public static final int CACHE_TIMEOUT_ONEDAY = 24 * CACH_TIMEOUT_ONEHOUR;
    public static final String VN_DATEFORMAT = "EEEE, dd.MM.yyyy";

    public static final int MIN_COUNT_BOX_SECTION_HOME = 60;
    public static final int MIN_COUNT_BOX_SECTION_OTHER = 20;

    private final static long MILLISECS_PER_DAY = 24 * 60 * 60 * 1000;

    @SuppressWarnings({"deprecation"})
    private static long getDateToLong(Date date) {
        return Date.UTC(date.getYear(), date.getMonth(), date.getDate(), 0, 0,
                0);
    }

    public static int getSignedDiffInDays(Date beginDate, Date endDate) {
        long beginMS = getDateToLong(beginDate);
        long endMS = getDateToLong(endDate);
        long diff = (endMS - beginMS) / (MILLISECS_PER_DAY);
        return (int) diff;
    }

    public static int getUnsignedDiffInDays(Date beginDate, Date endDate) {
        return Math.abs(getSignedDiffInDays(beginDate, endDate));
    }

    public static void trimCache(Context context) {
        try {
            File dir = context.getCacheDir();
            if (dir != null && dir.isDirectory()) {
                if (deleteDir(dir)) {
                }
            }
            dir = null;
        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    public static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }

        // The directory is now empty so delete it
        return dir.delete();
    }

    public static int roundUp(float num) {
        int resultNum = 0;
        num = num / 2;
        if (num - ((int) num) >= 0.5) {
            resultNum = ((int) num) + 1;
        } else {
            resultNum = ((int) num);
        }
        return resultNum;
    }


    public static byte[] encrypt(byte[] raw, byte[] clear) throws Exception {
        SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
        byte[] encrypted = cipher.doFinal(clear);
        return encrypted;
    }

    public static byte[] decrypt(byte[] raw, byte[] encrypted) throws Exception {
        SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, skeySpec);
        byte[] decrypted = cipher.doFinal(encrypted);
        return decrypted;
    }

    public static void startAnimation(Context context, View v, int animId) {
        final Animation inAnim = AnimationUtils.loadAnimation(context, animId);
        v.startAnimation(inAnim);
    }

    public static boolean verificaInstagram(Context context) {
        boolean instalado = false;
        try {
            context.getPackageManager().getApplicationInfo(
                    "com.instagram.android", 0);
            instalado = true;
        } catch (NameNotFoundException e) {
            instalado = false;
        }
        return instalado;
    }

    /**
     * @param name
     * @param path : /Duy/DuyLocalPicture
     * @return
     */
    public static URI getPathImageFromSdcard(String name, String path) {
        String root = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DCIM + path).toString();
        File myDir = new File(root);
        myDir.mkdirs();
        String fname = name + ".png";
        File file = new File(myDir, fname);
        if (file.exists()) {
            return file.toURI();
        } else {
            return null;
        }
    }

    /**
     * @param name
     * @param path : /Duy/DuyLocalPicture
     * @return
     */
    public static Uri getPathImageFromSdcardUri(String name, String path) {
        String root = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DCIM + path).toString();
        File myDir = new File(root);
        myDir.mkdirs();
        String fname = name + ".png";
        File file = new File(myDir, fname);
        if (file.exists()) {
            Uri uri = Uri.fromFile(file);
            return uri;
        } else {
            return null;
        }
    }

    /**
     * @param bmp
     * @param name
     * @param path : /Duy/DuyLocalPicture
     */
    public static void saveBitmapToSdcard(Bitmap bmp, String name, String path) {
        if (name != null) {
            String root = Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DCIM + path).toString();
            File myDir = new File(root);
            myDir.mkdirs();
            String fname = name + ".png";
            File file = new File(myDir, fname);
            if (file.exists())
                file.delete();
            try {
                FileOutputStream out = new FileOutputStream(file);
                bmp.compress(Bitmap.CompressFormat.PNG, 100, out);
                out.flush();
                out.close();

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            String root = Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DCIM + path).toString();
            File myDir = new File(root);
            myDir.mkdirs();
            String fname = "Image_Intagram.png";
            File file = new File(myDir, fname);
            if (file.exists())
                file.delete();
            try {
                FileOutputStream out = new FileOutputStream(file);
                bmp.compress(Bitmap.CompressFormat.PNG, 100, out);
                out.flush();
                out.close();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static Bitmap convertSringBase64ToBitMap(String encodedString) {
        try {
            byte[] encodeByte = Base64.decode(encodedString, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0,
                    encodeByte.length);
            return bitmap;
        } catch (Exception e) {
            e.getMessage();
            return null;
        }
    }

    public static String convertBitmapToBase64(Bitmap bmp) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 70, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        String encoded = Base64.encodeToString(byteArray, Base64.DEFAULT);
        return encoded;
    }

    public static String convertBitmapToBase64LowQuality(Bitmap bmp) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 30, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        String encoded = Base64.encodeToString(byteArray, Base64.DEFAULT);
        return encoded;
    }

    public static void expand(final View v) {
        v.measure(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        final int targtetHeight = v.getMeasuredHeight();

        v.getLayoutParams().height = 0;
        v.setVisibility(View.VISIBLE);
        Animation a = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime,
                                               Transformation t) {
                v.getLayoutParams().height = interpolatedTime == 1 ? LayoutParams.WRAP_CONTENT
                        : (int) (targtetHeight * interpolatedTime);
                v.requestLayout();
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // 1dp/ms
        a.setDuration((int) (targtetHeight / v.getContext().getResources()
                .getDisplayMetrics().density));
        v.startAnimation(a);
    }

    public static void collapse(final View v) {
        final int initialHeight = v.getMeasuredHeight();

        Animation a = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime,
                                               Transformation t) {
                if (interpolatedTime == 1) {
                    v.setVisibility(View.GONE);
                } else {
                    v.getLayoutParams().height = initialHeight
                            - (int) (initialHeight * interpolatedTime);
                    v.requestLayout();
                }
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // 1dp/ms
        a.setDuration((int) (initialHeight / v.getContext().getResources()
                .getDisplayMetrics().density));
        v.startAnimation(a);
    }

    public static String getTimeByTimeInMillis(long data) {
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        String formattedDate = df.format(data);
        return formattedDate;
    }

    public static String getTimeByTimeInMillisYYYY_MM_DD(long data) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        String formattedDate = df.format(data);
        return formattedDate;
    }

    public static String getCurrentDateTime() {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        String formattedDate = df.format(c.getTime());
        return formattedDate;
    }

    public static int parseToDecFromHex(int hex) {
        int result1 = hex % 16;
        int result2 = hex / 16;
        int result = (result2 * 10) + result1;
        return result;
    }

    public static int parseToHexFromDec(int hex) {
        int result1 = hex % 10;
        int result2 = hex / 10;
        int result = (result2 * 16) + result1;
        return result;
    }

    public static String formatHour(int mins) {
        if (mins < 60) {
            if (mins < 10) {
                return "0:0" + mins;
            } else {
                return "0:" + mins;
            }
        } else {
            return ((mins / 60) < 10 ? "0" + (mins / 60) : (mins / 60) + "")
                    + ":"
                    + ((mins % 60) < 10 ? "0" + (mins % 60) : (mins % 60) + "");
        }
    }

    public static String encodeUTF(String str) {
        if (str == null) {
            return "Unknow";
        }

        try {
            byte[] utf8Bytes = str.getBytes("UTF-8");

            String encodedStr = new String(utf8Bytes, "UTF-8");

            return encodedStr;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return str;
    }

    public static String printKeyHash(Activity context) {
        PackageInfo packageInfo;
        String key = null;
        try {

            // getting application package name, as defined in manifest
            String packageName = context.getApplicationContext()
                    .getPackageName();

            // Retriving package info
            packageInfo = context.getPackageManager().getPackageInfo(
                    packageName, PackageManager.GET_SIGNATURES);


            for (Signature signature : packageInfo.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                key = new String(Base64.encode(md.digest(), 0));
                key = key.replace("+", "_");
                key = key.replace("-", "_");
                key = key.replace("/", "_");
                // String key = new String(Base64.encodeBytes(md.digest()));

            }
        } catch (NameNotFoundException e1) {
        } catch (NoSuchAlgorithmException e) {
        } catch (Exception e) {
        }

        return key;
    }

    public static String getFromatThounsandInt(int d) {
        String s = "";
        try {
            // The comma in the format specifier does the trick
            s = String.format("%,d", d);
        } catch (NumberFormatException e) {
        }
        return s;
    }

    public static String getFromatThounsandLong(long d) {
        String s = "";
        try {
            // The comma in the format specifier does the trick
            s = String.format("%,d", d);
        } catch (NumberFormatException e) {
        }
        return s;
    }

    public static String getFromatThounsandFloat(float f) {
        String s = "";
        try {
            // The comma in the format specifier does the trick
            s = String.format("%,.2f", f);
        } catch (NumberFormatException e) {
        }
        return s;
    }

    public static Drawable getDrawale(Resources res, int id) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return res.getDrawable(id, mContext.getTheme());
        } else {
            return res.getDrawable(id);
        }
    }

    public static int getColor(Resources res, int id) {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            return res.getColor(id, mContext.getTheme());
//        } else {
        return res.getColor(id);
//        }
    }

    public static int getCountDatBetWeekTwoDat(String pastDay, String nowDay) {
        SimpleDateFormat myFormat = new SimpleDateFormat("dd/MM/yyyy");
        int day = 5;
        try {
            Date date1 = myFormat.parse(pastDay);
            Date date2 = myFormat.parse(nowDay);
            long diff = date2.getTime() - date1.getTime();
            day = (int) TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return day;
    }

    public static String getAccessToken(String email) {
        String s = email + KEY_ACCESS;
        try {
            // Create MD5 Hash
            MessageDigest digest = MessageDigest.getInstance("MD5");
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuilder hexString = new StringBuilder();
            for (byte aMessageDigest : messageDigest) {
                String h = Integer.toHexString(0xFF & aMessageDigest);
                while (h.length() < 2)
                    h = "0" + h;
                hexString.append(h);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String getAccessTokenByUser(String userToken) {
        String s = userToken + KEY_ACCESS;
        try {
            // Create MD5 Hash
            MessageDigest digest = MessageDigest.getInstance("MD5");
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuilder hexString = new StringBuilder();
            for (byte aMessageDigest : messageDigest) {
                String h = Integer.toHexString(0xFF & aMessageDigest);
                while (h.length() < 2)
                    h = "0" + h;
                hexString.append(h);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static boolean isConnectionAvailable(Activity act) {
        if (act == null && mContext == null) {
            return false;
        }
        ConnectivityManager cm = (ConnectivityManager) act.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        // if no network is available networkInfo will be null
        // otherwise check if we are connected
        if (networkInfo != null && networkInfo.isConnected()) {
            return true;
        }
        UIUtils.showToast(act, "Oops! We were not able to connect to the Internet…");
        return false;
    }


    public static String parseImageToFormatBase64(Resources res, int resource) {
        Bitmap bm = BitmapFactory.decodeResource(res, resource);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 100, baos); // bm is the bitmap
        // object
        byte[] b = baos.toByteArray();

        String encodedImage = Base64.encodeToString(b, Base64.DEFAULT);

        return encodedImage;
    }

    public static String parseImageToFormatBase64(Bitmap image) {
        Bitmap immagex = image;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        immagex.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] b = baos.toByteArray();
        String imageEncoded = Base64.encodeToString(b, Base64.DEFAULT);

        return imageEncoded;
    }

    public static int getAgeFromFormat_yyyyMMdd(String dateFormat) {
        int age = 0;
        Date selectedDate = null;
        String expectedPattern = "yyyyMMdd";
        SimpleDateFormat formatter = new SimpleDateFormat(expectedPattern);
        try {
            selectedDate = formatter.parse(dateFormat);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            return age;
        }
        if (dateFormat.equals("")) {
            return age;
        }
        Calendar timeNow = Calendar.getInstance();
        int nowYear = timeNow.get(Calendar.YEAR);
        timeNow.setTime(selectedDate);
        int birhtDayYear = timeNow.get(Calendar.YEAR);
        age = nowYear - birhtDayYear;
        return age;
    }

    public static float getStrideLength(int gender, float height) {
        // 1 : male ; 2 : female
        float strideLength = 50.0f;
        if (gender == 1) {
            strideLength = (float) (0.415 * height);
        } else {
            strideLength = (float) (0.413 * height);
        }
        return strideLength;
    }

    public static int getRecomandStepByGender(String gender) {
        // 1 : male ; 2 : female
        if (gender.equals("1")) {
            return 12000;
        } else {
            return 10000;
        }

    }

    public static boolean compareTwoDate(Date date1, Date date2) {

        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");

        String stringDate1 = dateFormat.format(date1);
        String stringDate2 = dateFormat.format(date2);

        if (stringDate1.equals(stringDate2))
            return true;
        else
            return false;
    }

    public static void clearAllCache() {
        mUtils.clear();
    }

    public static Shader getShaderText() {
        Shader mTextShader = new LinearGradient(0, 0, 20, 50, new int[]{0xfffff000, 0xffe8a91a},
                new float[]{0, 1}, TileMode.CLAMP);
        return mTextShader;
    }

    public static Date getBeginOfDate(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    public static Date getEndOfDate(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        return calendar.getTime();
    }

    public static boolean isValidAge(Date dobirth) {
        Calendar now = Calendar.getInstance();
        now.add(Calendar.YEAR, -7);
        long height = getEndOfDate(now.getTime()).getTime();

        now.add(Calendar.YEAR, -5);
        long low = getBeginOfDate(now.getTime()).getTime();
        long dob = getBeginOfDate(dobirth).getTime();

        boolean valid = low <= dob && dob <= height;
        return valid;
    }


}


