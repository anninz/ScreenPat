package com.thq.pat;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Toast;

import com.thq.pat.permission.PermissionManager;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private PermissionManager mPermissionManager;
    private boolean mIsNoGrantResults = false;
    private Toolbar mToolbar;

    private ImageView prePatView;
    private SeekBar seekBarSize;
    private SeekBar seekBarAlphe;
    int patSize = 60;

    private EditText patNum;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store);
        mPermissionManager = new PermissionManager(this);
        mPermissionManager.requestLaunchPermissions();


        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        mToolbar.setTitle(R.string.app_name);


        //获取启动按钮
        Button start = (Button)findViewById(R.id.start_id);
        //获取移除按钮
        Button remove = (Button)findViewById(R.id.remove_id);
        //绑定监听
        start.setOnClickListener(new OnClickListener()
        {

            @Override
            public void onClick(View v)
            {
                // TODO Auto-generated method stub
                Intent intent = new Intent(MainActivity.this, FxService.class);
                String num = patNum.getText().toString();
                setSPInt("patnum",Integer.parseInt("".equals(num)?"1":num));
                //启动FxService
                startService(intent);
//                finish();
            }
        });

        remove.setOnClickListener(new OnClickListener()
        {

            @Override
            public void onClick(View v)
            {
                //uninstallApp("com.phicomm.hu");
                Intent intent = new Intent(MainActivity.this, FxService.class);
                //终止FxService
                stopService(intent);
            }
        });

        patNum = (EditText) findViewById(R.id.edit_text_patnum);
//        patNum.setInputType(EditorInfo.TYPE_CLASS_PHONE);

        seekBarAlphe = (SeekBar) findViewById(R.id.seekBar1);
        seekBarAlphe.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {
                int strength = 0;
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                Log.i(TAG, "user has started a touch gesture");
                //startTouch = true;
                //Log.i(TAG, "onStartTrackingTouch():startTouch = "+startTouch);
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Log.i(TAG, "user has finished a touch gesture");
                //startTouch = false;
                //Log.i(TAG, "onStopTrackingTouch():startTouch = "+startTouch);
                //handler.removeCallbacks(mHideRunnable);
                //handler.postDelayed(mHideRunnable, 3000);
            }
        });


        prePatView = (ImageView) findViewById(R.id.prePatView);
        seekBarSize = (SeekBar) findViewById(R.id.seekBar);
        seekBarSize.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {
                patSize = progress + 30;
/*                switch (progress)
                {
                    case 1:  strength = 10; break;
                    case 2:  strength = 15; break;
                    case 3:  strength = 20; break;
                    case 4:  strength = 25; break;
                    case 5:  strength = 30; break;
                    case 6:  strength = 35; break;
                    case 7:  strength = 40; break;
                    case 8:  strength = 45; break;
                    case 9:  strength = 50; break;
                    case 10: strength = 55;break;
                    default: strength = 60;
                }*/
                setPrePatSize(patSize);
                Log.d(TAG,"1 onProgressChanged() strength="+patSize);
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                prePatView.setVisibility(View.VISIBLE);
                Log.i(TAG, "user has started a touch gesture");
                //startTouch = true;
                //Log.i(TAG, "onStartTrackingTouch():startTouch = "+startTouch);
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                prePatView.setVisibility(View.GONE);
                setSPInt("size", patSize);
                Log.i(TAG, "user has finished a touch gesture");
                //startTouch = false;
                //Log.i(TAG, "onStopTrackingTouch():startTouch = "+startTouch);
                //handler.removeCallbacks(mHideRunnable);
                //handler.postDelayed(mHideRunnable, 3000);
            }
        });

    }

    /**
     * the result if CameraActivity permission check.
     * there is four permissions that must be all on, the camera can be launch normally,
     * otherwise exit the camera app.
     *
     * @param requestCode  camera permission check code, used when requested permissions and
     *                     the code will be back in the permissions requested result.
     * @param permissions  the dangerous permissions that the activity defined in manifest.
     * @param grantResults the permission result for every permission.
     */
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        Log.i(TAG, "onRequestPermissionsResult(), requestCode = " + requestCode);
        if (grantResults.length <= 0) {
            mIsNoGrantResults = true;
            Log.e(TAG, "onRequestPermissionsResult(), if grantResults.length <= 0 may be home launcher");
            //finish();
            return;
        }

        mIsNoGrantResults = false;
        if (mPermissionManager.getCameraLaunchPermissionRequestCode()
                == requestCode) {
            if (mPermissionManager.isCameraLaunchPermissionsResultReady(
                    permissions, grantResults)) {
            } else {
                // more than one critical permission was denied
                // activity finish, exit and destroy
                Toast.makeText(this, R.string.denied_required_permission,
                        Toast.LENGTH_LONG).show();
                finish();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions,
                    grantResults);
            return;
        }
    }


    public static boolean isFirstTime(Context context) {
        SharedPreferences sp = context.getSharedPreferences("tips", Context.MODE_PRIVATE);
        return sp.getBoolean("firstTime", true);
    }

    private void setSPString(String key, String value) {
        SharedPreferences sp = getSharedPreferences("data", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(key, value);
        editor.commit();
    }

    private void setSPInt(String key, int value) {
        SharedPreferences sp = getSharedPreferences("data", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt(key, value);
        editor.commit();
    }

    void setPrePatSize(int size) {
        ViewGroup.LayoutParams layoutParams = prePatView.getLayoutParams();
        layoutParams.height = size;
        layoutParams.width  = size;
        prePatView.setLayoutParams(layoutParams);
    }
}