package com.thq.pat;


import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.thq.pat.widget.DividerItemDecoration;
import com.thq.pat.widget.RecyclerItemClickListener;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Fragment_1 extends Fragment {

    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private List<Pat> myDataset;
    private MyAdapter mAdapter;
    private Set<String> mSet;
    List<ViewHolder> mHolder;
    Handler mHandle;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View mView=inflater.inflate(R.layout.activity_recycler_view1, container, false);
        initView(mView);
        return mView;
    }

    private void initView(View view) {
        //RecyclerView
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerView1);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST));
        mRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), onItemClickListener));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

//        checkIfHaveNewPat();

        mSet = new HashSet<>();
        // specify an adapter (see also next example)
        myDataset = new ArrayList<>();

        mHandle = new MyHandle();

        new Thread(new Runnable() {
            @Override
            public void run() {
                downloadTxt();
                mHandle.sendEmptyMessage(DONE_INFO);
            }
        }).start();

    }

    final static int DONE_INFO = 1;
    final static int DONE_APK = 2;
     class MyHandle extends Handler {
         @Override
         public void handleMessage(Message msg) {
             super.handleMessage(msg);
             switch (msg.what){
                 case DONE_INFO:
                     //        LoadClass();
//        isHost = true;
//                ((FragmentInteraction)getActivity()).process("change");
                     mAdapter = new MyAdapter(getActivity(), myDataset);
                     mRecyclerView.setAdapter(mAdapter);
                     break;
                 case DONE_APK:
                     Toast.makeText(getActivity(),"Download Done!",Toast.LENGTH_LONG).show();
                     break;
                 default:
                     break;
             }
         }
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


/*    *//**
     * 用来与外部activity交互的
     *//*
    private FragmentInteraction listterner;

    *//**
     * 当FRagmen被加载到activity的时候会被回调
     * @param activity
     *//*
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
    }*/

    /**
     * 定义了所有activity必须实现的接口
     */
    public interface FragmentInteraction {
        /**
         * Fragment 向Activity传递指令，这个方法可以根据需求来定义
         * @param str
         */
        void process(String str);
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
    }


    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView mTextView;

        public ImageView mImageView;

//        public SeekBar mSeekBar;

        public Button mDownload;

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
            mDownload = (Button) v.findViewById(R.id.download);



//            mEditText = (EditText) v.findViewById(R.id.pat_num);
//            mCheckBox = (CheckBox) v.findViewById(R.id.checkbox);
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
                    .inflate(R.layout.item_view1, parent, false);
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
            final Pat pat = mDataset.get(position);
            holder.mTextView.setText(pat.patName);
            BitmapDrawable bd = new BitmapDrawable(getResources(), pat.patIcon);
            holder.mImageView.setImageDrawable(bd);
            holder.apkPath = pat.apkPath;
            holder.mDownload.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            //downloadPlugApk(pat.patName);
                            downloadPlugApk(pat.patName,null);
                            mHandle.sendEmptyMessage(DONE_APK);
                        }
                    }).start();
                }
            });
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

    /**
     *
     * @Project: Android_MyDownload
     * @Desciption: 只能读取文本文件，读取mp3文件会出现内存溢出现象
     * @Author: LinYiSong
     * @Date: 2011-3-25~2011-3-25
     */
    public void downloadTxt() {
        String urlStr="http://igee.xin:8080/screenpat/info.txt";
        Log.i("THQ", "downloadTxt begin");
        try {
                /*
                 * 通过URL取得HttpURLConnection
                 * 要网络连接成功，需在AndroidMainfest.xml中进行权限配置
                 * <uses-permission android:name="android.permission.INTERNET" />
                 */
            URL url=new URL(urlStr);
            HttpURLConnection conn=(HttpURLConnection)url.openConnection();
            //取得inputStream，并进行读取
            InputStream input=conn.getInputStream();
            BufferedReader in=new BufferedReader(new InputStreamReader(input));
            String line=null;
            StringBuffer sb=new StringBuffer();
            while((line=in.readLine())!=null){
                sb.append(line);
                Pat pat1 = new Pat();
                String appName1 = "蟑螂";
                Bitmap appIcon1 = BitmapFactory.decodeResource(getResources(), R.drawable.pic8);
                pat1.patIcon = appIcon1;
                pat1.patName = line;
                pat1.apkPath = "Host";
                myDataset.add(pat1);
            }
            Log.i("THQ", sb.toString());

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /**
     *
     * @Project: Android_MyDownload
     * @Desciption: 读取任意文件，并将文件保存到手机SDCard
     * @Author: LinYiSong
     * @Date: 2011-3-25~2011-3-25
     *
     *
     * 该方法会导致下载下来的apk有问题，使用新的下载方案：downloadPlugApk(String patName, String destfile)
     */

    public void downloadPlugApk(String patName) {
        String urlStr="http://igee.xin:8080/screenpat/plugapk/"+patName;
        String path="thqpat/apk";
        String fileName= patName;
        OutputStream output=null;
        try {
                /*
                 * 通过URL取得HttpURLConnection
                 * 要网络连接成功，需在AndroidMainfest.xml中进行权限配置
                 * <uses-permission android:name="android.permission.INTERNET" />
                 */
            URL url=new URL(urlStr);
            HttpURLConnection conn=(HttpURLConnection)url.openConnection();
            //取得inputStream，并将流中的信息写入SDCard

                /*
                 * 写前准备
                 * 1.在AndroidMainfest.xml中进行权限配置
                 * <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
                 * 取得写入SDCard的权限
                 * 2.取得SDCard的路径： Environment.getExternalStorageDirectory()
                 * 3.检查要保存的文件上是否已经存在
                 * 4.不存在，新建文件夹，新建文件
                 * 5.将input流中的信息写入SDCard
                 * 6.关闭流
                 */
            String SDCard= Environment.getExternalStorageDirectory()+"";
            String pathName=SDCard+"/"+path+"/"+fileName;//文件存储路径

            File file=new File(pathName);
            InputStream input=conn.getInputStream();
            if(file.exists()){
                System.out.println("exits");
                return;
            }else{
//                String dir=SDCard+"/"+path;
//                new File(dir).mkdir();//新建文件夹
                file.createNewFile();//新建文件
                output=new FileOutputStream(file);
                //读取大文件
                byte[] buffer=new byte[4*1024];
                while(input.read(buffer)!=-1){
                    output.write(buffer);
                }
                output.flush();
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally{
            try {
                if (output != null) {
                    output.close();
                }
                System.out.println("success");
            } catch (IOException e) {
                System.out.println("fail");
                e.printStackTrace();
            }
        }
    }

    public void downloadPlugApk(String patName, String destfile) {
        String urlStr="http://igee.xin:8080/screenpat/plugapk/"+patName;
        String path=Environment.getExternalStorageDirectory().getAbsolutePath()+"/thqpat/apk/"+patName;
        try{
            //下载路径，如果路径无效了，可换成你的下载路径
            //String url = "http://c.qijingonline.com/test.mkv";
            //String path = Environment.getExternalStorageDirectory().getAbsolutePath();

            final long startTime = System.currentTimeMillis();
            Log.i("DOWNLOAD","startTime="+startTime);
            //下载函数
            String filename=urlStr.substring(urlStr.lastIndexOf("/") + 1);
            //获取文件名
            URL myURL = new URL(urlStr);
            URLConnection conn = myURL.openConnection();
            conn.connect();
            InputStream is = conn.getInputStream();
            int fileSize = conn.getContentLength();//根据响应获取文件大小
            if (fileSize <= 0) throw new RuntimeException("无法获知文件大小 ");
            if (is == null) throw new RuntimeException("stream is null");
            //File file1 = new File(path);
            //if(!file1.exists()){
            //    file1.mkdirs();
            //}
            //把数据存入路径+文件名
            FileOutputStream fos = new FileOutputStream(path);
            byte buf[] = new byte[1024];
            int downLoadFileSize = 0;
            do{
                //循环读取
                int numread = is.read(buf);
                if (numread == -1)
                {
                    break;
                }
                fos.write(buf, 0, numread);
                downLoadFileSize += numread;
                //更新进度条
            } while (true);
            Log.i("DOWNLOAD","download success");
            Log.i("DOWNLOAD","totalTime="+ (System.currentTimeMillis() - startTime));
            is.close();
        } catch (Exception ex) {
            Log.e("DOWNLOAD", "error: " + ex.getMessage(), ex);
        }
    }

} 