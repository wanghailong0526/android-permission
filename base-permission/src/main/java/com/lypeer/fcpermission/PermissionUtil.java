package com.lypeer.fcpermission;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import com.lypeer.fcpermission.impl.OnPermissionsDeniedListener;
import com.lypeer.fcpermission.impl.OnPermissionsGrantedListener;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Administrator on 2017/3/2/0002.
 */

public class PermissionUtil {
    public interface OnPermissionsGranted {
        void onPermissionsGranted(int requestCode, List<String> perms);
    }

    public interface OnPermissionsDenied {
        void onPermissionsDenied(int requestCode, List<String> perms);
    }

    private static PermissionUtil mPermissionUtil;
    private static OnPermissionsGranted onPermissionsGranted;
    private OnPermissionsDenied onPermissionsDenied;
    private static Activity activity;
    private static FcPermissionsB mFcPermissionsB;

    public static final int PM_CAMERA = 1111;
    public static final int PM_STORAGE = 1113;
    public static final int PM_PHONE = 1114;
    public static final int PM_CAMERA_STORAGE_PHONE = 1112;
    public static final int PM_CAMERA_STORAGE_PHONE_LOCATION = 1115;
    public static final int PM_CAMERA_STORAGE = 1116;
    public static final int PM_AUDIO_STORAGE = 1117;

    public static final String[] CAMERA = {Manifest.permission.CAMERA};
    public static final String[] PHONE = {Manifest.permission.CALL_PHONE};

    public static final String[] LOCATION = {Manifest.permission.ACCESS_FINE_LOCATION};

    public static final String[] STORAGE = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE
            , Manifest.permission.READ_EXTERNAL_STORAGE};

    public static final String[] CAMERA_STORAGE = {
            Manifest.permission.CAMERA
            , Manifest.permission.WRITE_EXTERNAL_STORAGE
            , Manifest.permission.READ_EXTERNAL_STORAGE};
    public static final String[] mPermissionList = new String[]{
            Manifest.permission.WRITE_EXTERNAL_STORAGE
            , Manifest.permission.READ_EXTERNAL_STORAGE
//            , Manifest.permission.ACCESS_FINE_LOCATION
            , Manifest.permission.CALL_PHONE
//            , Manifest.permission.CAMERA
    };
    public static final String[] AUDIO_STORAGE = {Manifest.permission.RECORD_AUDIO, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};

    public static void requestPermission(Activity activity1, int requestCodePre, String[] permissionName, OnPermissionsGranted onPermissionsGranted1) {
        onPermissionsGranted = onPermissionsGranted1;
        activity = activity1;
        getInstance(activity1).requestPermission(requestCodePre, permissionName);
    }

    //private 构造方法
    private PermissionUtil(Activity activity) {
        this.activity = activity;
    }

    //单例
    private static PermissionUtil getInstance(Activity activity) {
        if (null == mPermissionUtil) {
            synchronized (PermissionUtil.class) {
                if (null == mPermissionUtil) {
                    mPermissionUtil = new PermissionUtil(activity);
                }
            }
        }
        return mPermissionUtil;
    }


    /**
     * @param requestCodePre
     * @param permissionName
     */
    private void requestPermission(final int requestCodePre, final String... permissionName) {
        mFcPermissionsB = new FcPermissionsB.Builder(activity)
                .onGrantedListener(new OnPermissionsGrantedListener() {
                    @Override
                    public void onPermissionsGranted(int requestCode, List<String> perms) {
                        if (null != onPermissionsGranted) {
                            if (perms.size() == permissionName.length && requestCodePre == requestCode) {
                                onPermissionsGranted.onPermissionsGranted(requestCode, perms);
                            }
                        } else {
                            Toast.makeText(activity, "获取权限失败", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .onDeniedListener(new OnPermissionsDeniedListener() {
                    @Override
                    public void onPermissionsDenied(int requestCode, List<String> perms) {

                    }
                })
                .positiveBtn4ReqPer(android.R.string.ok)
                .negativeBtn4ReqPer(R.string.cancel)
                .positiveBtn4NeverAskAgain(R.string.setting)
                .negativeBtn4NeverAskAgain(R.string.cancel)
                .rationale4ReqPer(activity.getString(R.string.prompt_request_camara))//必需
                .rationale4NeverAskAgain(activity.getString(R.string.prompt_we_need_camera))//必需
                .requestCode(requestCodePre)//必需
                .build();
        if (!hasPermissions(activity, permissionName)) {
            mFcPermissionsB.requestPermissions(permissionName);
        } else {
            if (null != onPermissionsGranted) {
                onPermissionsGranted.onPermissionsGranted(requestCodePre, Arrays.asList(permissionName));
            }
        }
    }

    /**
     * 检查当前APP是否已有权限或者无需申请权限
     *
     * @param context 请求界面的context
     * @param perms   申请的权限列表
     * @return 如果为true, 表示不需要申请权限, 如果为false, 表示需要申请权限
     */
    private static boolean hasPermissions(Activity context, String... perms) {
        // Always return true for SDK < M, let the system deal with the permissions
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            Log.w("warning", "hasPermissions: API version < M, returning true by default");
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

    public static void onRequestPermissionsResult(int requestCode, String[] permissions,
                                                  int[] grantResults, Object object) {
        if (null != mFcPermissionsB) {
            mFcPermissionsB.onRequestPermissionsResult(requestCode, permissions, grantResults, activity);
        }

    }


    public static final class Builder {
        private FcPermissionsB.Builder builder;
        private Activity activity;
        private int requestCode = -1;
        private OnPermissionsGranted onPermissionsGranted;
        private OnPermissionsDenied onPermissionsDenied;

        public Builder(Activity activity) {
            this.activity = activity;
        }

        public Builder requestCode(int requestCode) {
            this.requestCode = requestCode;
            return this;
        }

        public Builder onPermissionsGranted(OnPermissionsGranted onPermissionsGranted) {
            this.onPermissionsGranted = onPermissionsGranted;
            return this;
        }

        public Builder onPermissionsDenied(OnPermissionsDenied onPermissionsDenied) {
            this.onPermissionsDenied = onPermissionsDenied;
            return this;
        }

        public Builder builder(FcPermissionsB.Builder builder) {
            this.builder = builder;
            return this;
        }
    }

}
