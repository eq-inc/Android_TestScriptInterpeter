package jp.eq_inc.testuiautomator;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;

import jp.co.thcomp.util.RuntimePermissionUtil;

public class RuntimePermissionActivity extends AppCompatActivity implements RuntimePermissionUtil.OnRequestPermissionsResultListener {
    public static final String INTENT_STRING_ARRAY_PARAM_PERMISSIONS = "INTENT_STRING_ARRAY_PARAM_PERMISSIONS";
    public static final String INTENT_INT_ARRAY_PARAM_GRANTS = "INTENT_INT_ARRAY_PARAM_GRANTS";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String[] permissions = getIntent().getStringArrayExtra(INTENT_STRING_ARRAY_PARAM_PERMISSIONS);
        if (permissions != null && permissions.length > 0) {
            RuntimePermissionUtil.requestPermissions(this, permissions, this);
        } else {
            finish();
        }
    }

    @Override
    public void onRequestPermissionsResult(@NonNull String[] permissions, @NonNull int[] grants) {
        Intent resultIntent = new Intent();
        resultIntent.putExtra(INTENT_STRING_ARRAY_PARAM_PERMISSIONS, permissions);
        resultIntent.putExtra(INTENT_INT_ARRAY_PARAM_GRANTS, grants);
        setResult(Activity.RESULT_OK, resultIntent);
        finish();
    }
}
