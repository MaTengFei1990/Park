package com.hollysmart.park;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hollysmart.interfaces.MyInterfaces;
import com.hollysmart.popuwindows.PopupXiangCe;
import com.hollysmart.style.StyleAnimActivity;
import com.hollysmart.utils.FileTool;
import com.hollysmart.utils.Mlog;
import com.hollysmart.utils.Utils;
import com.hollysmart.utils.loctionpic.AlbumHelper;
import com.hollysmart.utils.loctionpic.BitmapCache;
import com.hollysmart.utils.loctionpic.ImageBucket;
import com.hollysmart.utils.loctionpic.ImageItem;
import com.hollysmart.value.Values;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by cai on 16/7/28
 */
public class Cai_AddPicActivity extends StyleAnimActivity {

    public final int REQUEST_CODE_PHOTOHRAPH = 100;// 拍照

    @Override
    public int layoutResID() {
        return R.layout.activity_add_pic;
    }

    private GridView gv_loctionPic;
    private TextView tv_num;
    private LinearLayout ll_xiangce;
    private TextView tv_title;
    private ImageView iv_arrow;

    @Override
    public void findView() {
        findViewById(R.id.iv_back).setOnClickListener(this);
        tv_num =  findViewById(R.id.tv_num);
        tv_num.setOnClickListener(this);
        gv_loctionPic =  findViewById(R.id.gv_loctionPic);

        ll_xiangce =  findViewById(R.id.ll_xiangce);
        tv_title =  findViewById(R.id.tv_title);
        ll_xiangce.setOnClickListener(this);
        findViewById(R.id.rl_yulan).setOnClickListener(this);
        iv_arrow =  findViewById(R.id.iv_arrow);

    }


    private Context mContext;
    public static Bitmap bitmap;
    private List<ImageItem> selectedPics;
    private String cameraFile;
    private int num;
    private String ID;

    private PopupXiangCe popupXiangCe;
    private MyAdapter myAdapter;


    @Override
    public void init() {
        mContext = this;
        num = getIntent().getIntExtra("num", 9);
        ID = getIntent().getStringExtra("ID");
        tv_num.setText("0/" + num);
//        initData();
        showContacts();
        bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.a_v);
        selectedPics = new ArrayList<>();
        myAdapter = new MyAdapter(dataList.get(0).imageList);
        gv_loctionPic.setAdapter(myAdapter);
        gv_loctionPic.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    onCamera();
                } else {
                    Mlog.d("index = " + position);
                }
            }
        });

        popupXiangCe = new PopupXiangCe(this);
        popupXiangCe.setPopupListener(new MyInterfaces.PopupIF() {
            @Override
            public void onListener() {
            }

            @Override
            public void item(int position) {
                gv_loctionPic.setSelection(0);
                tv_title.setText(dataList.get(position).bucketName);
                for (ImageBucket imageBucket : dataList) {
                    if (imageBucket.isTag)
                        imageBucket.isTag = false;
                }
                dataList.get(position).isTag = true;
                myAdapter.setImageItems(dataList.get(position).imageList);
                myAdapter.notifyDataSetChanged();
            }
        });
    }

    final public static int REQUEST_CODE_ASK_CAMERA = 100;

    private void onCamera() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int checkCallPhonePermission = ContextCompat.checkSelfPermission(mContext, Manifest.permission.CAMERA);
            if (checkCallPhonePermission != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_ASK_CAMERA);
                return;
            } else {
                //上面已经写好的拨号方法
                camera();
            }
        } else {
            //上面已经写好的拨号方法
            camera();
        }
    }

    private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 100;

    /**
     * Show the contacts in the ListView.
     */
    private void showContacts() {
        // Check the SDK version and whether the permission is already granted or not.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSIONS_REQUEST_READ_CONTACTS);
            //After this point you wait for callback in onRequestPermissionsResult(int, String[], int[]) overriden method
        } else {
            // Android version is lesser than 6.0 or the permission is already granted.
            initData();
        }
    }


    private void camera() {
        try {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            cameraFile = Values.SDCARD_FILE(Values.SDCARD_PIC_WODE) + System.currentTimeMillis() + ".jpg";
            File f = FileTool.CreateFile(cameraFile);

            Uri mUri;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION); //添加这一句表示对目标应用临时授权该Uri所代表的文件
                mUri = FileProvider.getUriForFile(mContext, BuildConfig.APPLICATION_ID + ".fileprovider", f);
            } else {
                mUri = Uri.fromFile(f);
            }
            intent.putExtra(MediaStore.EXTRA_OUTPUT, mUri);
            startActivityForResult(intent, REQUEST_CODE_PHOTOHRAPH);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CODE_ASK_CAMERA) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission Granted
                camera();
            } else {
                // Permission Denied
                Toast.makeText(Cai_AddPicActivity.this, "请授权使用camera权限", Toast.LENGTH_SHORT)
                        .show();
            }
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    List<ImageBucket> dataList;

    private void initData() {
        AlbumHelper helper = AlbumHelper.getHelper();
        helper.init(getApplicationContext());
        dataList = helper.getImagesBucketList(false);


        List<ImageItem> xiangces = new ArrayList<>();
//        for (ImageBucket ibt : dataList) {
//            List<ImageItem> imageItems = ibt.imageList;
//            for (ImageItem item : imageItems) {
//                if (!Utils.isEmpty(item.imagePath))
//                    item.isSelected = false;
//                xiangces.add(item);
//            }
//        }

        for(int i=dataList.size()-1;i>=0;i--) {
            ImageBucket ibt = dataList.get(i);
            List<ImageItem> imageItems = ibt.imageList;
            for (ImageItem item : imageItems) {
                if (!Utils.isEmpty(item.imagePath))
                    item.isSelected = false;
                xiangces.add(item);
            }
        }

        ImageBucket imageBucket = new ImageBucket();
        imageBucket.isTag = true;
        imageBucket.count = xiangces.size();
        imageBucket.imageList = xiangces;
        imageBucket.bucketName = "相册胶卷";
        dataList.add(0, imageBucket);

    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        Mlog.d("-------------------- onSaveInstanceState ................");
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.ll_xiangce:
                popupXiangCe.showPopuWindow(ll_xiangce, dataList);
                break;
            case R.id.tv_num:
                Intent info = new Intent();
                info.putExtra("picPath", (Serializable) selectedPics);
                info.putExtra("ID", ID);
                setResult(2, info);
                finish();
                break;
        }
    }



    private class MyAdapter extends BaseAdapter {

        private LayoutInflater mInflater;
        private BitmapCache cache;
        private List<ImageItem> imageItems;

        public MyAdapter(List<ImageItem> imageItems) {
            this.imageItems = imageItems;
            mInflater = LayoutInflater.from(mContext);
            cache = new BitmapCache();
        }

        public void setImageItems(List<ImageItem> imageItems) {
            this.imageItems = imageItems;
        }

        @Override
        public int getCount() {
            return imageItems.size() + 1;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, final ViewGroup parent) {
            final ViewHolder holder;
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.item_pic, null);
                holder = new ViewHolder();
                holder.iv_loctionpic = (ImageView) convertView.findViewById(R.id.iv_loctionpic);
                holder.iv_select = (ImageView) convertView.findViewById(R.id.iv_select);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }


            if (position == 0) {
                holder.iv_select.setVisibility(View.GONE);
                holder.iv_loctionpic.setImageResource(R.mipmap.add_xiangji);
            } else {

                final ImageItem item = imageItems.get(position - 1);

                holder.iv_select.setVisibility(View.VISIBLE);

                if (item.isSelected) {
                    holder.iv_select.setImageResource(R.mipmap.anjianchuli_icon2);
                } else {
                    holder.iv_select.setImageResource(R.mipmap.anjianchuli_icon);
                }

                holder.iv_loctionpic.setTag(item.imagePath);
                cache.displayBmp(holder.iv_loctionpic, item.thumbnailPath, item.imagePath, callback);

                holder.iv_select.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (item.isSelected) {
                            item.isSelected = false;
                            holder.iv_select.setImageResource(R.mipmap.anjianchuli_icon);
                            selectedPics.remove(item);
                            if (selectedPics.size() == 0) {
                                tv_num.setText(selectedPics.size() + "/" + num);
                                tv_num.setEnabled(false);
                            } else {
                                tv_num.setText("完成" + selectedPics.size() + "/" + num);
                            }

                        } else {
                            tv_num.setEnabled(true);
                            if (selectedPics.size() < num) {
                                item.isSelected = true;
                                holder.iv_select.setImageResource(R.mipmap.anjianchuli_icon2);
                                selectedPics.add(item);
                                tv_num.setText("完成" + selectedPics.size() + "/" + num);
                            } else {
                                Utils.showToast(mContext, "最多可以选择" + num + "张");
                            }
                        }
                    }
                });
            }
            return convertView;
        }

        private class ViewHolder {
            ImageView iv_loctionpic;
            ImageView iv_select;
        }

        BitmapCache.ImageCallback callback = new BitmapCache.ImageCallback() {
            @Override
            public void imageLoad(ImageView imageView, Bitmap bitmap, Object... params) {
                if (imageView != null && bitmap != null) {
                    String url = (String) params[0];
                    if (url != null && url.equals((String) imageView.getTag())) {
                        imageView.setImageBitmap(bitmap);
                    } else {
                        Mlog.d("callback, bmp not match");
                    }
                } else {
                    Mlog.d("callback, bmp null");
                }
            }
        };
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_PHOTOHRAPH) {
            Mlog.d("requestCode = " + requestCode + "   resultCode = " + resultCode + "  RESULT_OK = " + RESULT_OK);
            if (resultCode == RESULT_OK) {
                Intent info = new Intent();
                info.putExtra("picPath", cameraFile);
                info.putExtra("ID", ID);
                setResult(1, info);
                finish();
            }
        }
    }
}





















