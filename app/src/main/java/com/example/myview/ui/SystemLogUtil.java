package com.example.myview.ui;

import android.util.Log;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class SystemLogUtil {

    public static void updateDebugLog() {
        try {
            Class cls = Reflect28Util.forName("android.view.ViewRootImpl");

//            try {
//                Method declaredMethod = Reflect28Util.getDeclaredMethod(cls, "currentActivityThread");
//                declaredMethod.setAccessible(true);
//                Object activityThread = declaredMethod.invoke(null);
//                Field mHiddenApiWarningShown = Reflect28Util.getDeclaredField(cls, "mHiddenApiWarningShown");
//                mHiddenApiWarningShown.setAccessible(true);
//                mHiddenApiWarningShown.setBoolean(activityThread, true);
//                Log.e("SystemLogUtil", "reflection dark greylist is success");
//            } catch (Exception e) {
//                e.printStackTrace();
//            }

            String fieldName = "";
            Field[] fields = null;
            fields = cls.getDeclaredFields();
            for (int i = 0; i < fields.length; ++i) {
                fieldName = fields[i].getName();
                Log.i("SystemLogUtil", "fieldName:" + fieldName);
                if (fieldName == null) continue;//按照原有顺序一个个设置进去
                if (fieldName.startsWith("DEBUG") || fieldName.equals("LOCAL_LOGV")) {
                    fields[i].setAccessible(true);
                    fields[i].setBoolean(null, true);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            Log.e("SystemLogUtil", "reflection dark greylist is fail");
        }
    }
}
