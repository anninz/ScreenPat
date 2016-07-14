/*
 *  Copyright Statement:
 *          This software/firmware and related documentation ("TCT Software") are
 *  protected under relevant copyright laws. The information contained herein
 *  is confidential and proprietary to TCT Inc. and/or its licensors.
 *  Without the prior written permission of TCT inc. and/or its licensors,
 *  any reproduction, modification, use or disclosure of TCT Software,
 *  and information contained herein, in whole or in part, shall be strictly prohibited.
 *
 *  TCT Inc. (C) 2015. All rights reserved.
 *
 * Class Name: PermissionManager
 *
 * Description:
 * use for get premission of camera
 *
 * Modify history:
 * |--------Owner------|------Time------|-----Bug ID-----|---------Bug Description-----------------------------|
 * ------------------------------------------------------------------------------------------------------------
 * |      fengke       |    2016.01.13  |       /        |              Create class                           |
 * Copyright (c) 2015. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */
package com.thq.pat.permission;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.thq.pat.MainActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * the class is for the activity permission check.
 */
public class PermissionManager {
    private static final String TAG = "PermissionManager";
    private static final int CAM_REQUEST_CODE_ASK_LAUNCH_PERMISSIONS = 100;
    private static final int CAM_REQUEST_CODE_ASK_LOCATION_PERMISSIONS = 101;
    private final MainActivity mActivity;
    private List<String> mLauchPermissionList = new ArrayList<String>();

    /**
     * it need keep the activity context, because permission checking need it.
     *
     * @param activity the activity which need permission check.
     */
    public PermissionManager(MainActivity activity) {
        mActivity = activity;
        initCameraLaunchPermissionList();
    }

    private void initCameraLaunchPermissionList() {
        mLauchPermissionList.add(Manifest.permission.SYSTEM_ALERT_WINDOW);
        mLauchPermissionList.add(Manifest.permission.INTERNET);
        mLauchPermissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        mLauchPermissionList.add(Manifest.permission.ACCESS_NETWORK_STATE);
    }

    /**
     * get the need check permission list.
     * some permissions may be already on.
     *
     * @param permissionList all the dangerous permission for the activity.
     * @return the need check permission list.
     */
    private List<String> getNeedCheckPermissionList(List<String> permissionList) {
        // all needed permissions, may be on or off
        if (permissionList.size() <= 0) {
            return permissionList;
        }
        List<String> needCheckPermissionsList = new ArrayList<String>();
        for (String permission : permissionList) {
            if (ContextCompat.checkSelfPermission(mActivity, permission)
                    != PackageManager.PERMISSION_GRANTED) {
                Log.i(TAG, "getNeedCheckPermissionList() permission ="
                        + permission);
                needCheckPermissionsList.add(permission);
            }
        }
        Log.i(TAG, "getNeedCheckPermissionList() listSize ="
                + needCheckPermissionsList.size());
        return needCheckPermissionsList;
    }

    /**
     * check camera launch permissions, the permission list is defined in the function.
     *
     * @return true if all the launch permissions are already on.
     * false if more than one launch permission is off.
     */
    public boolean checkCameraLaunchPermissions() {
        List<String> needCheckPermissionsList = getNeedCheckPermissionList(mLauchPermissionList);
        if (needCheckPermissionsList.size() > 0) {
            return false;
        }
        Log.i(TAG, "CheckCameraPermissions(), all on");
        return true;
    }

    /**
     * request for the activity launch permission.
     * if it need check some permissions, it will show a dialog to user, and
     * the user will decide whether the permission should be granted to the activity.
     * if user denied, the feature for the activity can not be used. and the request result
     * will be sent to the activity
     * which should implements ActivityCompat.OnRequestPermissionsResultCallback.
     *
     * @return true if the activity already had all the permissions in permissionList.
     * false if the activity had not all the permissions in permissionList.
     */
    public boolean requestLaunchPermissions() {

        List<String> needCheckPermissionsList = getNeedCheckPermissionList(mLauchPermissionList);
        if (needCheckPermissionsList.size() > 0) {
            // should show dialog
            Log.i(TAG, "requestLaunchPermissions(), user check");
            ActivityCompat.requestPermissions(
                    mActivity,
                    needCheckPermissionsList
                            .toArray(new String[needCheckPermissionsList
                                    .size()]), CAM_REQUEST_CODE_ASK_LAUNCH_PERMISSIONS);
            return false;
        }
        Log.i(TAG, "requestLaunchPermissions(), all on");
        return true;
    }

    /**
     * used in the activity permission request result, onRequestPermissionsResult.
     * it is used by the activity to check the request type.
     *
     * @return the request code
     */
    public int getCameraLaunchPermissionRequestCode() {
        return CAM_REQUEST_CODE_ASK_LAUNCH_PERMISSIONS;
    }

    /**
     * used in the activity permission request result, onRequestPermissionsResult.
     * it is used by the activity to check the request type.
     *
     * @return the request code
     */
    public int getCameraLocationPermissionRequestCode() {
        return CAM_REQUEST_CODE_ASK_LOCATION_PERMISSIONS;
    }

    /**
     * it should be called in the activity request callback, onRequestPermissionsResult.
     * it is to check whether all the permission request results are on.
     *
     * @param permissions  the permission list get from onRequestPermissionsResult.
     * @param grantResults the results for requested permissions
     * @return true if all the request permissions are allowed.
     * false if more than one request permission is denied.
     */
    public boolean isCameraLaunchPermissionsResultReady(
            String permissions[], int[] grantResults) {
        Map<String, Integer> perms = new HashMap<String, Integer>();
        perms.put(Manifest.permission.SYSTEM_ALERT_WINDOW,
                PackageManager.PERMISSION_GRANTED);
        perms.put(Manifest.permission.INTERNET,
                PackageManager.PERMISSION_GRANTED);
        perms.put(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                PackageManager.PERMISSION_GRANTED);
        perms.put(Manifest.permission.ACCESS_NETWORK_STATE,
                PackageManager.PERMISSION_GRANTED);

        for (int i = 0; i < permissions.length; i++) {
            perms.put(permissions[i], grantResults[i]);
        }

        if (perms.get(Manifest.permission.SYSTEM_ALERT_WINDOW)
                == PackageManager.PERMISSION_GRANTED
                && perms.get(Manifest.permission.INTERNET)
                == PackageManager.PERMISSION_GRANTED
                && perms.get(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED
                && perms.get(Manifest.permission.ACCESS_NETWORK_STATE)
                == PackageManager.PERMISSION_GRANTED
                ) {
            return true;
        }
        return false;
    }

}
