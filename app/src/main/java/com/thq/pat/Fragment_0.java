package com.thq.pat;


import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.thq.pat.widget.DividerItemDecoration;
import com.thq.pat.widget.RecyclerItemClickListener;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import dalvik.system.DexClassLoader;

public class Fragment_0 extends Fragment {

    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private List<Pat> myDataset;
    private MyAdapter mAdapter;
    private Set<String> mSet;
    List<ViewHolder> mHolder;
  
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View mView=inflater.inflate(R.layout.activity_recycler_view, container, false);
        initView(mView);
        return mView;  
    }

    private void initView(View view) {
        //RecyclerView
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST));
        mRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), onItemClickListener));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        checkIfHaveNewPat();

        mSet = new HashSet<>();
        // specify an adapter (see also next example)
        myDataset = new ArrayList<>();
//        LoadClass();
//        isHost = true;
//        ((FragmentInteraction)getActivity()).process("change");
        mAdapter = new MyAdapter(getActivity(), myDataset);
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onStop() {
        super.onStop();
        for (ViewHolder viewHolder:mHolder) {
            if (viewHolder.patNum > 0) {
                StringBuffer stringBuffer = new StringBuffer();
                stringBuffer.append(viewHolder.patNum + "#");
                stringBuffer.append(viewHolder.apkPath + "#");
                stringBuffer.append(viewHolder.mTextView.getText().toString());
                mSet.add(stringBuffer.toString());
            }
/*
            if (viewHolder.mCheckBox.isChecked()) {
                StringBuffer stringBuffer = new StringBuffer();
                stringBuffer.append(viewHolder.mEditText.getText().toString() + "#");
                stringBuffer.append(viewHolder.apkPath + "#");
                stringBuffer.append(viewHolder.mTextView.getText().toString());
                mSet.add(stringBuffer.toString());
            }
*/
        }
        setSPSet("PatSet", mSet);
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("onResume", "onResume");
        myDataset.clear();
        LoadClass();
        ((FragmentInteraction)getActivity()).process("change");
        mAdapter.notifyDataSetChanged();
    }

    /**
     * 用来与外部activity交互的
     */
    private FragmentInteraction listterner;

    /**
     * 当FRagmen被加载到activity的时候会被回调
     * @param activity
     */
//    @Override
    public void onAttach(AppCompatActivity activity) {
        super.onAttach(activity);

        if(activity instanceof FragmentInteraction)
        {
            listterner = (FragmentInteraction)activity;
        }
        else{
            throw new IllegalArgumentException("activity must implements FragmentInteraction");
        }

    }

    @Override
    public void onDetach() {
        super.onDetach();

        listterner = null;
    }

    /**
     * 定义了所有activity必须实现的接口
     */
    public interface FragmentInteraction
    {
        /**
         * Fragment 向Activity传递指令，这个方法可以根据需求来定义
         * @param str
         */
        void process(String str);


    }


    DexClassLoader classLoader = null;
    private void LoadClass() {

        Pat pat1 = new Pat();
        String appName1 = "蟑螂";
        Bitmap appIcon1 = BitmapFactory.decodeResource(getResources(), R.drawable.pic8);
        pat1.patIcon = appIcon1;
        pat1.patName = appName1;
        pat1.apkPath = "Host";
        pat1.defaultNum = 1;
        myDataset.add(pat1);

//        String dexOutputDir = getApplicationInfo().dataDir;
        String apkdir = "/data/data/" + getActivity().getPackageName() + "/dynamicapk/" ;
//        String DynamicApkPath = apkdir + File.separator + "plugapk-release-unsigned.apk";
        final File tmpDir = getActivity().getDir("dex", 0);

        File dir = new File(apkdir);
        if (!dir.exists()) dir.mkdir();
        File[] allFiles = dir.listFiles();
        Log.d("LoadClass", "getFiles  success! allFiles = " + allFiles);
        if (allFiles == null) return;
        for (int i = 0; i < allFiles.length; i++) {
            File file = allFiles[i];
            //Log.d("LoadClass", "file.isFile() = " + file.isFile());
            if (file.isFile()) {

//                Log.d("THQ", "getFiles  success! file = " + file.getPath() + " " + file.getAbsolutePath());
                classLoader = new DexClassLoader(file.getAbsolutePath(),tmpDir.getAbsolutePath(),null,getActivity().getClassLoader());
                ((FragmentInteraction)(getActivity())).process(file.getPath());

//                Log.d("LoadClass", "classLoader = " + classLoader);
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

                        String appName = (String)getAppName.invoke(null, getActivity());
                        Method getAppIcon = clazz.getMethod("getAppIcon", Context.class);
                        Bitmap appIcon = (Bitmap) getAppIcon.invoke(null, getActivity());
                        pat.patIcon = appIcon;
                        pat.patName = appName;
                        pat.apkPath = file.getPath();
//                        Log.d("LoadClass", "getAppName = " + appName + appIcon + file.getPath());
                        myDataset.add(pat);
//                        myDataset[i] = appName;

                    }
                }catch(Exception e){
                    Log.i("Loader", "error:"+Log.getStackTraceString(e));
                }
            }
        }
    }

    private void setSPString(String key, String value) {
        SharedPreferences sp = getActivity().getSharedPreferences("data", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(key, value);
        editor.commit();
    }

    private void setSPInt(String key, int value) {
        SharedPreferences sp = getActivity().getSharedPreferences("data", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt(key, value);
        editor.commit();
    }

    private void setSPSet(String key, Set<String> value) {
        SharedPreferences sp = getActivity().getSharedPreferences("data", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putStringSet(key, value);
        editor.commit();
    }

    class Pat {
        String patName;
        Bitmap patIcon;
        String apkPath;
        int defaultNum;
    }


    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView mTextView;

        public ImageView mImageView;

        public SeekBar mSeekBar;

        public TextView mPatNum;

        int patNum;

//        public EditText mEditText;
//        Spinner mSpinner;

//        public CheckBox mCheckBox;

        public String apkPath;
//        public int position;

        public ViewHolder(View v) {
            super(v);

            mTextView = (TextView) v.findViewById(R.id.news_title);
            mImageView = (ImageView) v.findViewById(R.id.pat_image);
            mSeekBar = (SeekBar) v.findViewById(R.id.pat_num);
            mPatNum = (TextView) v.findViewById(R.id.pat_num_hint);


//            mEditText = (EditText) v.findViewById(R.id.pat_num);
//            mCheckBox = (CheckBox) v.findViewById(R.id.checkbox);
//            mSpinner = (Spinner) v.findViewById(R.id.spinner1);
//            mSpinner.setAdapter(adapter);

            mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    patNum = progress;
                    mPatNum.setText(patNum + "只");
                }
                @Override
                public void onStartTrackingTouch(SeekBar seekBar) { }
                @Override
                public void onStopTrackingTouch(SeekBar seekBar) { }
            });
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
            holder.mSeekBar.setProgress(pat.defaultNum);
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

    private boolean checkIfHaveNewPat() {
        String myApkDir = "/sdcard/thqpat/apk";
        File dir = new File(myApkDir);
        if (!dir.exists()) dir.mkdir();
        File[] allFiles = dir.listFiles();
//        Log.d("THQ", "getFiles  success! allFiles = " + allFiles);
        if (allFiles == null) return false;
        for (int i = 0; i < allFiles.length; i++) {
            File file = allFiles[i];
            if (file.isFile() && file.getName().endsWith(".apk")) {
                Log.i("THQ", "checkIfHaveNewPat " + file.getName());
                copyApk2DataDir(file);
            }
        }
        return false;
    }

    private void copyApk2DataDir(File apkFile) {
        InputStream in = null;
        FileOutputStream out = null;
//        String path = this.getApplicationContext().getFilesDir()
//                .getAbsolutePath() + "/dynamicapk/" + apkFile.getName(); // data/data目录
        String path = "/data/data/" + getActivity().getPackageName() + "/dynamicapk/" + apkFile.getName(); // data/data目录
        File file = new File(path);
        if (!file.exists()) {
            try {
                Log.i("THQ", "start copy Apk 2 DataDir " + file.getAbsolutePath());
//                in = this.getAssets().open("db/mydb.db3"); // 从assets目录下复制
                in = new FileInputStream(apkFile); // 从sdcard目录下复制
                out = new FileOutputStream(file);
                int length = -1;
                byte[] buf = new byte[1024];
                while ((length = in.read(buf)) != -1) {
                    out.write(buf, 0, length);
                }
                out.flush();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (in != null) {
                    try {

                        in.close();

                    } catch (IOException e1) {

                        // TODO Auto-generated catch block

                        e1.printStackTrace();
                    }
                }
                if (out != null) {
                    try {
                        out.close();
                    } catch (IOException e1) {
                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                    }
                }
            }
        }
    }
      
} 