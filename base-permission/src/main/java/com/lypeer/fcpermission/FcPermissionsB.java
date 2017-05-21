package com.lypeer.fcpermission;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.GridView;

import com.lypeer.fcpermission.impl.OnPermissionsDeniedListener;
import com.lypeer.fcpermission.impl.OnPermissionsGrantedListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;

import com.lypeer.fcpermission.adapter.*;
import com.lypeer.fcpermission.view.CustomDialog;

/**
 * Copyright Google Inc. All Rights Reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
public class FcPermissionsB {

    private static final String TAG = "FcPermissionsB";

    private final Object mObject;

    private final OnPermissionsDeniedListener mDeniedListener;
    private final OnPermissionsGrantedListener mGrantedListener;

    private final String mRationale4ReqPer;
    private final String mRationale4NeverAskAgain;

    private final int mPositiveBtn4ReqPer;
    private final int mNegativeBtn4ReqPer;
    private final int mPositiveBtn4NeverAskAgain;
    private final int mNegativeBtn4NeverAskAgain;

    private final int mRequestCode;


    /**
     * 构造方法,返回一个可用的FcPermissionB
     *
     * @param object                    请求界面的Context,有可能是Activity,Fragment,android.app.Fragment
     * @param grantedListener           监视用户拒绝提供权限的事件
     * @param deniedListener            监视用户同意提供权限的事件
     * @param rationale4ReqPer          询问是否前往请求权限的提示语
     * @param rationale4NeverAskAgain   询问在之前选择了"不再询问"之后是否选择去设置里修改权限的提示语
     * @param positiveBtn4ReqPer        询问是否前往请求权限时Positive Button上的String的Res id
     * @param negativeBtn4ReqPer        询问是否前往请求权限时Negative Button上的String的Res id
     * @param positiveBtn4NeverAskAgain 询问在之前选择了"不再询问"之后是否选择去设置里修改权限时
     *                                  Positive Button上的String的Res id
     * @param negativeBtn4NeverAskAgain 询问在之前选择了"不再询问"之后是否选择去设置里修改权限时
     *                                  Negative Button上的String的Res id
     * @param requestCode               请求码
     */
    FcPermissionsB(
            Object object,
            OnPermissionsGrantedListener grantedListener,
            OnPermissionsDeniedListener deniedListener,
            String rationale4ReqPer,
            String rationale4NeverAskAgain,
            @StringRes int positiveBtn4ReqPer,
            @StringRes int negativeBtn4ReqPer,
            @StringRes int positiveBtn4NeverAskAgain,
            @StringRes int negativeBtn4NeverAskAgain,
            int requestCode) {
        this.mObject = object;
        this.mGrantedListener = grantedListener;
        this.mDeniedListener = deniedListener;
        this.mRationale4ReqPer = rationale4ReqPer;
        this.mRationale4NeverAskAgain = rationale4NeverAskAgain;
        this.mPositiveBtn4ReqPer = positiveBtn4ReqPer;
        this.mNegativeBtn4ReqPer = negativeBtn4ReqPer;
        this.mPositiveBtn4NeverAskAgain = positiveBtn4NeverAskAgain;
        this.mNegativeBtn4NeverAskAgain = negativeBtn4NeverAskAgain;
        this.mRequestCode = requestCode;
    }

    /**
     * 开始进行请求权限相关的工作
     *
     * @param perms 要请求的权限的列表
     */
    public void requestPermissions(String... perms) {
        if (mObject == null || TextUtils.isEmpty(mRationale4ReqPer) ||
                TextUtils.isEmpty(mRationale4NeverAskAgain) || mRequestCode == -1) {
            throw new IllegalArgumentException("You should init these arguments .");
        }
        requestPermissions(mObject, mRationale4ReqPer,
                mPositiveBtn4ReqPer == -1 ? android.R.string.ok : mPositiveBtn4ReqPer,
                mNegativeBtn4ReqPer == -1 ? android.R.string.cancel : mNegativeBtn4ReqPer,
                mRequestCode, perms);
    }

    /**
     * 检查当前APP是否已有权限或者无需申请权限
     *
     * @param context 请求界面的context
     * @param perms   申请的权限列表
     * @return 如果为true, 表示不需要申请权限, 如果为false, 表示需要申请权限
     */
    private boolean hasPermissions(Context context, String... perms) {
        // Always return true for SDK < M, let the system deal with the permissions
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            Log.w(TAG, "hasPermissions: API version < M, returning true by default");
            return true;
        }

        for (String perm : perms) {
            boolean hasPerm = (ContextCompat.checkSelfPermission(context, perm) ==
                    PackageManager.PERMISSION_GRANTED);
            if (!hasPerm) {
                return false;
            }
        }
        return true;
    }

    /**
     * 开始进行申请权限相关的工作
     *
     * @param object         请求界面的Context,有可能是Activity,Fragment,android.app.Fragment
     * @param rationale      询问是否前往请求权限的提示语
     * @param positiveButton 询问是否前往请求权限时Positive Button上的String的Res id
     * @param negativeButton 询问是否前往请求权限时Negative Button上的String的Res id
     * @param requestCode    请求码
     * @param perms          请求的权限的列表
     */
    private void requestPermissions(final Object object, String rationale,
                                    @StringRes int positiveButton,
                                    @StringRes int negativeButton,
                                    final int requestCode, final String... perms) {
        //检查object合法性
        checkCallingObjectSuitability(object);

        if (!hasPermissions(getActivity(object), perms)) {

            boolean shouldShowRationale = true;
//            for (String perm : perms) {
//                shouldShowRationale =
//                        shouldShowRationale || shouldShowRequestPermissionRationale(object, perm);
//            }

            if (shouldShowRationale) {
                Activity activity = getActivity(object);
                if (null == activity) {
                    return;
                }
                //弹出dialog提示用户要授权的权限
                showDialog3(object, rationale, positiveButton, negativeButton, requestCode, activity, perms);
            } else {
                executePermissionsRequest(object, perms, requestCode);
            }
            return;
        }

        if (mGrantedListener != null) {
            mGrantedListener.onPermissionsGranted(requestCode, Arrays.asList(perms));
        }
    }

    private void showDialog(final Object object, String rationale, @StringRes int positiveButton, @StringRes int negativeButton, final int requestCode, Activity activity, final String[] perms) {
        AlertDialog dialog = new AlertDialog.Builder(activity)
                .setMessage(rationale)
                .setPositiveButton(positiveButton, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        executePermissionsRequest(object, perms, requestCode);
                    }
                })
                .setNegativeButton(negativeButton, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // act as if the permissions were denied
                        checkDeniedPermissionsNeverAskAgain(
                                object,
                                mRationale4NeverAskAgain,
                                mPositiveBtn4NeverAskAgain == -1 ? R.string.setting : mPositiveBtn4NeverAskAgain,
                                mNegativeBtn4NeverAskAgain == -1 ? android.R.string.cancel : mNegativeBtn4NeverAskAgain,
                                Arrays.asList(perms)
                        );
                        if (mDeniedListener != null) {
                            mDeniedListener.onPermissionsDenied(requestCode, Arrays.asList(perms));
                        }

                    }
                }).create();
        dialog.show();
    }

    /**
     * 开始进行申请权限相关的工作首先弹出的Dialog，显示权限的图片，名字，
     *
     * @param object
     * @param rationale
     * @param positiveButton
     * @param negativeButton
     * @param requestCode
     * @param activity
     * @param perms
     */
    private void showDialog2(final Object object, String rationale, @StringRes int positiveButton, @StringRes int negativeButton, final int requestCode, Activity activity, final String[] perms) {
        int num = 2;
        int resId = 0;
        String stringId = "";
        View view = LayoutInflater.from(activity).inflate(R.layout.show_permission_view, null);
        GridView gridView = (GridView) view.findViewById(R.id.GridView1);
        List<Integer> pictures = new ArrayList<>();
        List<String> strings = new ArrayList<>();

        //下面这个for循环用于动态获得权限对应的图片
        for (String s : perms) {
            if (s.substring(s.lastIndexOf(".") + 1, s.length()).equalsIgnoreCase("WRITE_EXTERNAL_STORAGE") || s.substring(s.lastIndexOf(".") + 1, s.length()).equalsIgnoreCase("READ_EXTERNAL_STORAGE")) {
                resId = activity.getResources().getIdentifier("p_" + s.substring(s.indexOf("_") + 1, s.length()).toLowerCase(), "mipmap", activity.getPackageName());
                stringId = activity.getResources().getString(activity.getResources().getIdentifier("p_" + s.substring(s.indexOf("_") + 1, s.length()).toLowerCase(), "string", activity.getPackageName()));
            } else {
                resId = activity.getResources().getIdentifier("p_" + s.substring(s.lastIndexOf(".") + 1, s.length()).toLowerCase(), "mipmap", activity.getPackageName());
                stringId = activity.getResources().getString(activity.getResources().getIdentifier("p_" + s.substring(s.lastIndexOf(".") + 1, s.length()).toLowerCase(), "string", activity.getPackageName()));
            }
            pictures.add(resId);
            strings.add(stringId);

        }
        pictures = duplicateRemova(pictures);
        strings = duplicateRemovaString(strings);
        //下面这个方法判断grideview有几列
//        num = numColumns(perms, pictures);
        num = pictures.size();
        gridView.setNumColumns(num);
        gridView.setAdapter(new PermissionAdapter(activity, pictures, strings));
        AlertDialog dialog = new AlertDialog.Builder(activity)
                .setMessage(rationale)
                .setView(view)
                .setCancelable(false)
                .setPositiveButton(positiveButton, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        executePermissionsRequest(object, perms, requestCode);
                    }
                })
                .setNegativeButton(negativeButton, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // act as if the permissions were denied
//                        checkDeniedPermissionsNeverAskAgain(
//                                object,
//                                mRationale4NeverAskAgain,
//                                mPositiveBtn4NeverAskAgain == -1 ? R.string.setting : mPositiveBtn4NeverAskAgain,
//                                mNegativeBtn4NeverAskAgain == -1 ? android.R.string.cancel : mNegativeBtn4NeverAskAgain,
//                                Arrays.asList(perms)
//                        );
                                if (mDeniedListener != null) {
                                    mDeniedListener.onPermissionsDenied(requestCode, Arrays.asList(perms));
                                }

                            }
                        }
                ).create();

        dialog.show();
    }

    private void showDialog3(final Object object, String rationale, @StringRes int positiveButton, @StringRes int negativeButton, final int requestCode, Activity activity, final String[] perms) {
        int resId = 0;
        String stringId = "";
        List<Integer> pictures = new ArrayList<>();
        List<String> strings = new ArrayList<>();
        int p_call_phone = R.mipmap.p_call_phone;
        //下面这个for循环用于动态获得权限对应的图片
        for (String s : perms) {
            if (s.substring(s.lastIndexOf(".") + 1, s.length()).equalsIgnoreCase("WRITE_EXTERNAL_STORAGE") || s.substring(s.lastIndexOf(".") + 1, s.length()).equalsIgnoreCase("READ_EXTERNAL_STORAGE")) {
                String str = "p_" + s.substring(s.indexOf("_") + 1, s.length()).toLowerCase();
                resId = activity.getResources().getIdentifier("p_" + s.substring(s.indexOf("_") + 1, s.length()).toLowerCase(), "mipmap", activity.getPackageName());
                stringId = activity.getResources().getString(activity.getResources().getIdentifier("p_" + s.substring(s.indexOf("_") + 1, s.length()).toLowerCase(), "string", activity.getPackageName()));
            } else {
                resId = activity.getResources().getIdentifier("p_" + s.substring(s.lastIndexOf(".") + 1, s.length()).toLowerCase(), "mipmap", activity.getPackageName());
                stringId = activity.getResources().getString(activity.getResources().getIdentifier("p_" + s.substring(s.lastIndexOf(".") + 1, s.length()).toLowerCase(), "string", activity.getPackageName()));
            }
            pictures.add(resId);
            strings.add(stringId);

        }
        pictures = duplicateRemova(pictures);
        strings = duplicateRemovaString(strings);
        CustomDialog.Builder builder = new CustomDialog.Builder(activity);
        builder.setPictures(pictures);
        builder.setStrings(strings);
        builder.setMessage(R.string.prompt_we_need_camera);
        builder.setPositiveButton(new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                executePermissionsRequest(object, perms, requestCode);
                dialog.dismiss();
            }
        });
        builder.setCancelable(false);
        builder.create().show();
    }


    //去除重复图片(有序)对应的ResId
    public static List<Integer> duplicateRemova(List<Integer> list) {
        List<Integer> listRes = new ArrayList<Integer>();
        LinkedHashSet<Integer> set = new LinkedHashSet<Integer>(list);
        listRes.addAll(set);
        return listRes;
    }

    //去除重复图片(有序)对应的ResId
    public static List<String> duplicateRemovaString(List<String> list) {
        List<String> listRes = new ArrayList<String>();
        LinkedHashSet<String> set = new LinkedHashSet<String>(list);
        listRes.addAll(set);
        return listRes;
    }

    private void checkCallingObjectSuitability(Object object) {
        // 确保object是一个Activity或者Fragment
        boolean isActivity = object instanceof Activity;
        boolean isSupportFragment = object instanceof Fragment;
        boolean isAppFragment = object instanceof android.app.Fragment;
        boolean isMinSdkM = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;

        if (!(isSupportFragment || isActivity || (isAppFragment && isMinSdkM))) {
            if (isAppFragment) {
                throw new IllegalArgumentException(
                        "Target SDK needs to be greater than 23 if caller is android.app.Fragment");
            } else {
                throw new IllegalArgumentException("Caller must be an Activity or a Fragment.");
            }
        }
    }

    @TargetApi(23)
    private boolean shouldShowRequestPermissionRationale(Object object, String perm) {
        if (object instanceof Activity) {
            return ActivityCompat.shouldShowRequestPermissionRationale((Activity) object, perm);
        } else if (object instanceof Fragment) {
            return ((Fragment) object).shouldShowRequestPermissionRationale(perm);
        } else if (object instanceof android.app.Fragment) {
            return ((android.app.Fragment) object).shouldShowRequestPermissionRationale(perm);
        } else {
            return false;
        }
    }

    @TargetApi(23)
    private void executePermissionsRequest(Object object, String[] perms, int requestCode) {
        checkCallingObjectSuitability(object);

        if (object instanceof Activity) {
            ActivityCompat.requestPermissions((Activity) object, perms, requestCode);
        } else if (object instanceof Fragment) {
            ((Fragment) object).requestPermissions(perms, requestCode);
        } else if (object instanceof android.app.Fragment) {
            ((android.app.Fragment) object).requestPermissions(perms, requestCode);
        }
    }

    @TargetApi(11)
    private Activity getActivity(Object object) {
        if (object instanceof Activity) {
            return ((Activity) object);
        } else if (object instanceof Fragment) {
            return ((Fragment) object).getActivity();
        } else if (object instanceof android.app.Fragment) {
            return ((android.app.Fragment) object).getActivity();
        } else {
            return null;
        }
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults, Object object) {

        checkCallingObjectSuitability(object);
        String mNevrAskAgain = "需要您手动设置权限";
        ArrayList<String> granted = new ArrayList<>();
        ArrayList<String> denied = new ArrayList<>();
        for (int i = 0; i < permissions.length; i++) {
            String perm = permissions[i];
            if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                granted.add(perm);
            } else {
                denied.add(perm);
            }
        }

        // Report granted permissions, if any.
        if (!granted.isEmpty()) {
            // Notify callbacks
            if (mGrantedListener != null) {
                mGrantedListener.onPermissionsGranted(requestCode, granted);
            }
        }

        // Report denied permissions, if any.
        if (!denied.isEmpty()) {
            checkDeniedPermissionsNeverAskAgain(
                    object, mNevrAskAgain
                    /*mRationale4NeverAskAgain*/,
                    mPositiveBtn4NeverAskAgain == -1 ? R.string.setting : mPositiveBtn4NeverAskAgain,
                    mNegativeBtn4NeverAskAgain == -1 ? android.R.string.cancel : mNegativeBtn4NeverAskAgain,
                    denied
            );
            if (mDeniedListener != null) {
                mDeniedListener.onPermissionsDenied(requestCode, denied);
            }
        }
    }

    @TargetApi(11)
    private void startAppSettingsScreen(Object object,
                                        Intent intent) {
        if (object instanceof Activity) {
            ((Activity) object).startActivityForResult(intent, mRequestCode);
        } else if (object instanceof Fragment) {
            ((Fragment) object).startActivityForResult(intent, mRequestCode);
        } else if (object instanceof android.app.Fragment) {
            ((android.app.Fragment) object).startActivityForResult(intent, mRequestCode);
        }
    }

    private boolean checkDeniedPermissionsNeverAskAgain(final Object object,
                                                        String rationale,
                                                        @StringRes int positiveButton,
                                                        @StringRes int negativeButton,
                                                        List<String> deniedPerms) {
        return checkDeniedPermissionsNeverAskAgain(object, rationale,
                positiveButton, negativeButton, null, deniedPerms);
    }

    /**
     * 检查被拒绝提供的权限是否选中了不再询问
     */
    private boolean checkDeniedPermissionsNeverAskAgain(final Object object,
                                                        String rationale,
                                                        @StringRes int positiveButton,
                                                        @StringRes int negativeButton,
                                                        @Nullable DialogInterface.OnClickListener negativeButtonOnClickListener,
                                                        List<String> deniedPerms) {
        List<Integer> pictures = new ArrayList<>();
        List<String> strings = new ArrayList<>();
        Activity activity = getActivity(object);
        boolean shouldShowRationale;
        for (String s : deniedPerms) {
            shouldShowRationale = shouldShowRequestPermissionRationale(object, s);
            if (!shouldShowRationale) {
                if (null == activity) {
                    return true;
                }
                int resId;
                String stringId;
                if (s.substring(s.lastIndexOf(".") + 1, s.length()).equalsIgnoreCase("WRITE_EXTERNAL_STORAGE") || s.substring(s.lastIndexOf(".") + 1, s.length()).equalsIgnoreCase("READ_EXTERNAL_STORAGE")) {
                    resId = activity.getResources().getIdentifier("p_" + s.substring(s.indexOf("_") + 1, s.length()).toLowerCase(), "mipmap", activity.getPackageName());
                    stringId = activity.getResources().getString(activity.getResources().getIdentifier("p_" + s.substring(s.indexOf("_") + 1, s.length()).toLowerCase(), "string", activity.getPackageName()));
                } else {
                    resId = activity.getResources().getIdentifier("p_" + s.substring(s.lastIndexOf(".") + 1, s.length()).toLowerCase(), "mipmap", activity.getPackageName());
                    stringId = activity.getResources().getString(activity.getResources().getIdentifier("p_" + s.substring(s.lastIndexOf(".") + 1, s.length()).toLowerCase(), "string", activity.getPackageName()));
                }
                pictures.add(resId);
                strings.add(stringId);
                pictures = duplicateRemova(pictures);
                strings = duplicateRemovaString(strings);
                CustomDialog.Builder builder = new CustomDialog.Builder(activity);
                builder.setPictures(pictures);
                builder.setStrings(strings);
                builder.setMessage(R.string.prompt_request_never_ask);
                final Activity finalActivity = activity;
                builder.setPositiveButton(new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", finalActivity.getPackageName(), null);
                        intent.setData(uri);
                        startAppSettingsScreen(object, intent);
                        dialog.dismiss();
                    }
                });
                builder.setCancelable(true);
                builder.create().show();
            }

//                AlertDialog dialog = new AlertDialog.Builder(activity)
//                        .setMessage(rationale)
//                        .setPositiveButton(positiveButton, new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
//                                Uri uri = Uri.fromParts("package", activity.getPackageName(), null);
//                                intent.setData(uri);
//                                startAppSettingsScreen(object, intent);
//                            }
//                        })
//                        .setNegativeButton(negativeButton, negativeButtonOnClickListener)
//                        .create();
//                dialog.show();


            return true;
        }


        return false;
    }

    public static final class Builder {
        private Context mContext;
        private OnPermissionsDeniedListener mDeniedListener;
        private OnPermissionsGrantedListener mGrantedListener;
        private String mRationale4ReqPer;
        private String mRationale4NeverAskAgain;
        private int mPositiveBtn4ReqPer = -1;
        private int mNegativeBtn4ReqPer = -1;
        private int mPositiveBtn4NeverAskAgain = -1;
        private int mNegativeBtn4NeverAskAgain = -1;
        private int mRequestCode = -1;

        public Builder(Context context) {
            this.mContext = context;
        }

        public Builder onDeniedListener(OnPermissionsDeniedListener listener) {
            this.mDeniedListener = listener;
            return this;
        }

        public Builder onGrantedListener(OnPermissionsGrantedListener listener) {
            this.mGrantedListener = listener;
            return this;
        }

        public Builder rationale4ReqPer(String rationale4ReqPer) {
            this.mRationale4ReqPer = rationale4ReqPer;
            return this;
        }

        public Builder positiveBtn4ReqPer(int positiveBtn4ReqPer) {
            this.mPositiveBtn4ReqPer = positiveBtn4ReqPer;
            return this;
        }

        public Builder positiveBtn4NeverAskAgain(int positiveBtn4NeverAskAgain) {
            this.mPositiveBtn4NeverAskAgain = positiveBtn4NeverAskAgain;
            return this;
        }

        public Builder negativeBtn4ReqPer(int negativeBtn4ReqPer) {
            this.mNegativeBtn4ReqPer = negativeBtn4ReqPer;
            return this;
        }

        public Builder negativeBtn4NeverAskAgain(int negativeBtn4NeverAskAgain) {
            this.mNegativeBtn4NeverAskAgain = negativeBtn4NeverAskAgain;
            return this;
        }

        public Builder rationale4NeverAskAgain(String rationale4NeverAskAgain) {
            this.mRationale4NeverAskAgain = rationale4NeverAskAgain;
            return this;
        }

        public Builder requestCode(int requestCode) {
            this.mRequestCode = requestCode;
            return this;
        }

        public FcPermissionsB build() {
            return new FcPermissionsB(
                    mContext,
                    mGrantedListener,
                    mDeniedListener,
                    mRationale4ReqPer,
                    mRationale4NeverAskAgain,
                    mPositiveBtn4ReqPer,
                    mNegativeBtn4ReqPer,
                    mPositiveBtn4NeverAskAgain,
                    mNegativeBtn4NeverAskAgain,
                    mRequestCode
            );
        }
    }
}
