# android-permission
android permission android 权限管理

使用方法

1、依赖base-permission
2、在需要申请权限的地方添加以下代码
PermissionUtil.requestPermission(MainActivity.this, PermissionUtil.PM_CAMERA_STORAGE_PHONE_LOCATION, PermissionUtil.mPermissionList, new PermissionUtil.OnPermissionsGranted() {
                    @Override
                    public void onPermissionsGranted(int requestCode, List<String> perms) {
                    //这里为申请成功的回调
                        Toast.makeText(MainActivity.this, "权限申请成功", Toast.LENGTH_SHORT).show();
                    }
                });
3、在主Activity里回调，在某个Activity里的所有Fragment，回调只写一个，写到Activity里方便。注意每个权限的请求码要不同，都封装到了PermissionUtil里
 @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionUtil.onRequestPermissionsResult(requestCode, permissions, grantResults, MainActivity.this);
    }


