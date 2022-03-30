package com.example.fitnesstracker.util;

import android.content.Context;
import android.content.pm.PackageManager;
import android.text.TextUtils;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import java.util.Map;

public final class RequestPermissionContract implements ActivityResultCallback<Map<String, Boolean>> {
    private final RequestPermissionCallback callback;
    private int currentRequestCode;
    private final ActivityResultLauncher<String[]> permissionLauncher;
    private FragmentActivity activity;
    private Fragment fragment;

    public RequestPermissionContract(Fragment fragment, RequestPermissionCallback callback) {
        this.callback = callback;
        this.fragment = fragment;
        permissionLauncher = fragment.registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), this);
    }

    public RequestPermissionContract(FragmentActivity activity, RequestPermissionCallback callback) {
        this.callback = callback;
        this.activity = activity;
        permissionLauncher = activity.registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), this);
    }

    /**
     * launch the dialog to request the permissions.
     * You can pass as many permissions as required.
     *
     * @param rationale is required of you want to show the dialog before requesting the permission and permission was already denied previously.
     */
    public void launch(int requestCode, @Nullable String rationale, String... permissions) {
        if (checkSelfPermission(permissions)) {
            // permissions are already granted, so pass the callback
            callback.onActivityResult(requestCode, PermissionResult.GRANTED);
            return;
        }
        if (shouldShowRequestPermissionRationale(permissions)) {
            if (!TextUtils.isEmpty(rationale)) {
                // user want to show the rationale dialog
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                if (rationale != null)
                    builder.setMessage(rationale);
                builder.setPositiveButton(android.R.string.ok, (dialog, which) -> launchPermissionLauncher(requestCode, permissions));
                builder.setNegativeButton(android.R.string.cancel, (dialog, which) -> callback.onActivityResult(requestCode, PermissionResult.DENIED));
                builder.create().show();
                return;
            }
        }

        // launch the permissions
        launchPermissionLauncher(requestCode, permissions);
    }

    private void launchPermissionLauncher(int requestCode, String... permissions) {
        this.currentRequestCode = requestCode;
        // request the permissions
        permissionLauncher.launch(permissions);
    }

    @Override
    public void onActivityResult(Map<String, Boolean> results) {
        if (results.isEmpty()) {
            callback.onActivityResult(currentRequestCode, PermissionResult.DENIED);
            return;
        }
        String[] permissions = results.keySet().toArray(new String[0]);
        if (checkSelfPermission(permissions)) {
            // all permissions are granted
            callback.onActivityResult(currentRequestCode, PermissionResult.GRANTED);
        } else if (shouldShowRequestPermissionRationale(permissions)) {
            callback.onActivityResult(currentRequestCode, PermissionResult.DENIED);
        } else {
            // permissions are permanently disabled
            callback.onActivityResult(currentRequestCode, PermissionResult.PERMANENTLY_DENIED);
        }
    }

    private FragmentActivity getActivity() {
        if (activity != null)
            return activity;
        else
            return fragment.requireActivity();
    }

    private boolean shouldShowRequestPermissionRationale(String... permissions) {
        for (String perm : permissions)
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), perm))
                return true;
        return false;
    }

    /**
     * check if all the permissions are granted
     */
    private boolean checkSelfPermission(String... permissions) {
        return checkSelfPermission(getActivity(), permissions);
    }

    public static boolean checkSelfPermission(Context context, String... permissions) {
        for (String perm : permissions)
            if (ContextCompat.checkSelfPermission(context, perm) != PackageManager.PERMISSION_GRANTED)
                return false;
        return true;
    }

    public interface RequestPermissionCallback {
        /**
         * function to pass the result with request code.
         * Will return the aggregated result of all permissions i.e. if all the permissions are granted then it will be true.
         */
        void onActivityResult(int requestCode, PermissionResult result);
    }

    public enum PermissionResult {GRANTED, DENIED, PERMANENTLY_DENIED}
}