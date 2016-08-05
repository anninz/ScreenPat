package com.thq.pat;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.thq.pat.permission.PermissionManager;
import com.thq.pat.plugapilib.IHatchProvider;
import com.thq.pat.widget.DividerItemDecoration;
import com.thq.pat.widget.RecyclerItemClickListener;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import dalvik.system.DexClassLoader;

public class ChoosePatActivity extends BaseActivity {
    private static final String TAG = "MainActivity";

    private PermissionManager mPermissionManager;
    private boolean mIsNoGrantResults = false;
    private Toolbar mToolbar;

    private ImageView prePatView;
    private SeekBar seekBarSize;
    private SeekBar seekBarAlphe;
    int patSize = 60;

    private EditText patNum;

    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private List<Pat> myDataset;
    private MyAdapter mAdapter;
    private Set<String> mSet;
    List<ViewHolder> mHolder;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycler_view);


        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        mToolbar.setTitle(R.string.app_name);

        //RecyclerView
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));
        mRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(this, onItemClickListener));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        mSet = new HashSet<>();
        // specify an adapter (see also next example)
        myDataset = new ArrayList<>();
        LoadClass();
        isHost = true;
        mAdapter = new MyAdapter(this, myDataset);
        mRecyclerView.setAdapter(mAdapter);

    }

    @Override
    public void onStop() {
        super.onStop();
        for (ViewHolder viewHolder:mHolder) {
            if (viewHolder.mCheckBox.isChecked()) {
                StringBuffer stringBuffer = new StringBuffer();
                stringBuffer.append(viewHolder.mEditText.getText().toString() + "#");
                stringBuffer.append(viewHolder.apkPath + "#");
                stringBuffer.append(viewHolder.mTextView.getText().toString());
                mSet.add(stringBuffer.toString());
            }
        }
        setSPSet("PatSet", mSet);
    }

    DexClassLoader classLoader = null;
    private void LoadClass() {

        Pat pat1 = new Pat();
        String appName1 = "蟑螂";
        Bitmap appIcon1 = BitmapFactory.decodeResource(getResources(), R.drawable.pic8);
        pat1.patIcon = appIcon1;
        pat1.patName = appName1;
        pat1.apkPath = "Host";
        myDataset.add(pat1);

//        String dexOutputDir = getApplicationInfo().dataDir;
        String apkdir = "/data/data/" + getPackageName() + "/dynamicapk/" ;
//        String DynamicApkPath = apkdir + File.separator + "plugapk-release-unsigned.apk";
        final File tmpDir = getDir("dex", 0);

        File dir = new File(apkdir);
        if (!dir.exists()) dir.mkdir();
        File[] allFiles = dir.listFiles();
        Log.d("THQ", "getFiles  success! allFiles = " + allFiles);
        if (allFiles == null) return;
        for (int i = 0; i < allFiles.length; i++) {
            File file = allFiles[i];
            if (file.isFile()) {

//                Log.d("THQ", "getFiles  success! file = " + file.getPath() + " " + file.getAbsolutePath());
                classLoader = new DexClassLoader(file.getAbsolutePath(),tmpDir.getAbsolutePath(),null,getClassLoader());
                loadResources(file.getPath());

                try{
                    if (classLoader != null) {
                        Class clazz = classLoader.loadClass("com.thq.pat.plug.HatchFactory");
//                Method method = clazz.getMethod("getTextString", Context.class);
//                String str = (String) method.invoke(null, this);
                /*IHatchProvider FlyProvider = (IHatchProvider)clazz.newInstance();
                for (int i = 0; i < 2; i++) {
                    pats.add(FlyProvider.doHatch(this));
                }*/

                        Pat pat = new Pat();
                        Method getAppName = clazz.getMethod("getAppName", Context.class);

                        String appName = (String)getAppName.invoke(null, this);
                        Method getAppIcon = clazz.getMethod("getAppIcon", Context.class);
                        Bitmap appIcon = (Bitmap) getAppIcon.invoke(null, this);
                        pat.patIcon = appIcon;
                        pat.patName = appName;
                        pat.apkPath = file.getPath();
                        myDataset.add(pat);
//                        myDataset[i] = appName;

                    }
                }catch(Exception e){
                    Log.i("Loader", "error:"+Log.getStackTraceString(e));
                }
            }
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

    private void setSPSet(String key, Set<String> value) {
        SharedPreferences sp = getSharedPreferences("data", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putStringSet(key, value);
        editor.commit();
    }

    void setPrePatSize(int size) {
        ViewGroup.LayoutParams layoutParams = prePatView.getLayoutParams();
        layoutParams.height = size;
        layoutParams.width  = size;
        prePatView.setLayoutParams(layoutParams);
    }

    class Pat {
        String patName;
        Bitmap patIcon;
        String apkPath;
    }


    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView mTextView;

        public ImageView mImageView;

        public EditText mEditText;
//        Spinner mSpinner;

        public CheckBox mCheckBox;

        public String apkPath;
//        public int position;

        public ViewHolder(View v) {
            super(v);
//
//            List<String> list = new ArrayList<String>();
//            list.add("1");
//            list.add("2");
//            list.add("3");
//            list.add("4");

//            ArrayAdapter<String> adapter=new ArrayAdapter<String>(ChoosePatActivity.this, android.R.layout.simple_spinner_item,list);
//            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

            mTextView = (TextView) v.findViewById(R.id.news_title);
            mImageView = (ImageView) v.findViewById(R.id.pat_image);
            mEditText = (EditText) v.findViewById(R.id.pat_num);
            mCheckBox = (CheckBox) v.findViewById(R.id.checkbox);
//            mSpinner = (Spinner) v.findViewById(R.id.spinner1);
//            mSpinner.setAdapter(adapter);
        }
    }

    public class MyAdapter extends RecyclerView.Adapter<ViewHolder> {
        private final int mBackground;
        private List<Pat> mDataset;
        private final TypedValue mTypedValue = new TypedValue();

        // Provide a suitable constructor (depends on the kind of dataset)
        public MyAdapter(Context context, List<Pat> myDataset) {
            mDataset = myDataset;
            mHolder = new ArrayList<>();
            context.getTheme().resolveAttribute(R.attr.selectableItemBackground, mTypedValue, true);
            mBackground = mTypedValue.resourceId;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent,
                                                       int viewType) {
            // create a new view
//            isHost = true;
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_view, parent, false);
//            isHost = false;
            v.setBackgroundResource(mBackground);
            // set the view's size, margins, paddings and layout parameters
            ViewHolder vh = new ViewHolder(v);
            mHolder.add(vh);
            return vh;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            // - get element from your dataset at this position
            // - replace the contents of the view with that element
            Pat pat = mDataset.get(position);
            holder.mTextView.setText(pat.patName);
            BitmapDrawable bd = new BitmapDrawable(getResources(), pat.patIcon);
            holder.mImageView.setImageDrawable(bd);
            holder.apkPath = pat.apkPath;
        }

        // Return the size of your dataset (invoked by the layout manager)
        @Override
        public int getItemCount() {
            return mDataset.size();
        }
    }

    private RecyclerItemClickListener.OnItemClickListener onItemClickListener = new RecyclerItemClickListener.OnItemClickListener() {
        @Override
        public void onItemClick(View view, int position) {
        }
    };
}