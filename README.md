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


4、本项目里只有四个权限，相机，读写，电话，位置，若想加入其它权限，只需要在base-permission/mipmap-xxhdpi加入一张图片，命名规则为：
例如：一个权限的名称为android.permission.READ_CONTACTS
   那么你的图片的全名为p_read_contacts.png全小写


