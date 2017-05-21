package com.whl.permission;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.lypeer.fcpermission.PermissionUtil;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private TextView btn_camera, btn_phone, btn_storage, btn_mul;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initListener();
    }

    private void initView() {
        btn_camera = (TextView) findViewById(R.id.btn_camera);
        btn_phone = (TextView) findViewById(R.id.btn_phone);
        btn_storage = (TextView) findViewById(R.id.btn_storage);
        btn_mul = (TextView) findViewById(R.id.btn_mul);


    }

    private void initListener() {
        btn_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PermissionUtil.requestPermission(MainActivity.this, PermissionUtil.PM_CAMERA, PermissionUtil.CAMERA, new PermissionUtil.OnPermissionsGranted() {
                    @Override
                    public void onPermissionsGranted(int requestCode, List<String> perms) {
                        Toast.makeText(MainActivity.this, "相机权限申请成功", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        btn_phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PermissionUtil.requestPermission(MainActivity.this, PermissionUtil.PM_PHONE, PermissionUtil.PHONE, new PermissionUtil.OnPermissionsGranted() {
                    @Override
                    public void onPermissionsGranted(int requestCode, List<String> perms) {
                        Toast.makeText(MainActivity.this, "电话权限申请成功", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        btn_storage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PermissionUtil.requestPermission(MainActivity.this, PermissionUtil.PM_STORAGE, PermissionUtil.STORAGE, new PermissionUtil.OnPermissionsGranted() {
                    @Override
                    public void onPermissionsGranted(int requestCode, List<String> perms) {
                        Toast.makeText(MainActivity.this, "存储权限申请成功", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        btn_mul.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PermissionUtil.requestPermission(MainActivity.this, PermissionUtil.PM_CAMERA_STORAGE_PHONE_LOCATION, PermissionUtil.mPermissionList, new PermissionUtil.OnPermissionsGranted() {
                    @Override
                    public void onPermissionsGranted(int requestCode, List<String> perms) {
                        Toast.makeText(MainActivity.this, "权限申请成功", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionUtil.onRequestPermissionsResult(requestCode, permissions, grantResults, MainActivity.this);
    }
}
