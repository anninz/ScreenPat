package com.thq.pat;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
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
    int patAlpha = 255;

    SharedPreferences mSP;
    SharedPreferences.Editor mEditor;

    private EditText patNum;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store);
        mPermissionManager = new PermissionManager(this);
        mPermissionManager.requestLaunchPermissions();


        mSP = getSharedPreferences("data", Context.MODE_PRIVATE);
        mEditor = mSP.edit();

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
                String num = "1";//patNum.getText().toString();
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

//        patNum = (EditText) findViewById(R.id.edit_text_patnum);
//        patNum.setInputType(EditorInfo.TYPE_CLASS_PHONE);

        seekBarAlphe = (SeekBar) findViewById(R.id.seekBar1);
        seekBarAlphe.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {
                patAlpha = progress;
                setPrePatAlpha(progress);
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                prePatView.setVisibility(View.VISIBLE);
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                prePatView.setVisibility(View.GONE);
                setSPInt("alpha", patAlpha);
                Intent intent = new Intent();
                intent.setPackage(getPackageName());
//                intent.setClass(MainActivity.this, ActionListener.ActionBroadcastReceiver.class);//Warning:cant avaiable via the way,
                intent.setAction(ActionListener.MY_ACTION_CHANGE_PAT_ALPHA);
                sendBroadcast(intent);
            }
        });


        prePatView = (ImageView) findViewById(R.id.prePatView);
        seekBarSize = (SeekBar) findViewById(R.id.seekBar);
        seekBarSize.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {
                patSize = progress + 30;
                setPrePatSize(patSize);
//                Log.d(TAG,"1 onProgressChanged() strength="+patSize);
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                prePatView.setVisibility(View.VISIBLE);
//                Log.i(TAG, "user has started a touch gesture");
                //startTouch = true;
                //Log.i(TAG, "onStartTrackingTouch():startTouch = "+startTouch);
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                prePatView.setVisibility(View.GONE);
                setSPInt("size", patSize);
                Intent intent = new Intent();
                intent.setPackage(getPackageName());
//                intent.setClass(MainActivity.this, ActionListener.ActionBroadcastReceiver.class);//Warning:cant avaiable via the way,
                intent.setAction(ActionListener.MY_ACTION_CHANGE_PAT_SIZE);
                sendBroadcast(intent);
//                Log.i(TAG, "user has finished a touch gesture");
                //startTouch = false;
                //Log.i(TAG, "onStopTrackingTouch():startTouch = "+startTouch);
                //handler.removeCallbacks(mHideRunnable);
                //handler.postDelayed(mHideRunnable, 3000);
            }
        });

        findViewById(R.id.select_pat).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
//                intent.setClass(MainActivity.this, ChoosePatActivity.class);
                intent.setClass(MainActivity.this, PickPatFragment.class);
                startActivity(intent);
            }
        });

        patSize = mSP.getInt("size", 45);
        patAlpha = mSP.getInt("alpha", 255);
        setPrePatAlpha(patAlpha);
        setPrePatSize(patSize);
        seekBarAlphe.setProgress(patAlpha);
        seekBarSize.setProgress(patSize);
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
//                Toast.makeText(this, R.string.denied_required_permission,
//                        Toast.LENGTH_LONG).show();
                //finish();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions,
                    grantResults);
            return;
        }
    }

/*
    public static boolean isFirstTime(Context context) {
        SharedPreferences sp = context.getSharedPreferences("tips", Context.MODE_PRIVATE);
        return sp.getBoolean("firstTime", true);
    }*/

    private void setSPString(String key, String value) {
        SharedPreferences sp = getSharedPreferences("data", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(key, value);
        editor.commit();
    }

    private void setSPInt(String key, int value) {
//        SharedPreferences sp = getSharedPreferences("data", Context.MODE_PRIVATE);
//        SharedPreferences.Editor editor = sp.edit();
        mEditor.putInt(key, value);
        mEditor.commit();
    }

    void setPrePatSize(int size) {
        ViewGroup.LayoutParams layoutParams = prePatView.getLayoutParams();
        layoutParams.height = size;
        layoutParams.width  = size;
        prePatView.setLayoutParams(layoutParams);
    }
    void setPrePatAlpha(int alpha) {
        prePatView.setAlpha(alpha);
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent();
            intent.setClass(MainActivity.this, AddGestureActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}