package com.miqtech.wymaster.wylive.module.mine.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.miqtech.wymaster.wylive.R;
import com.miqtech.wymaster.wylive.annotations.LayoutId;
import com.miqtech.wymaster.wylive.base.BaseAppCompatActivity;
import com.miqtech.wymaster.wylive.constants.API;
import com.miqtech.wymaster.wylive.entity.User;
import com.miqtech.wymaster.wylive.http.FileUploadUtil;
import com.miqtech.wymaster.wylive.proxy.UserProxy;
import com.miqtech.wymaster.wylive.utils.imageloader.AsyncImage;
import com.miqtech.wymaster.wylive.widget.ActionSheetDialog;

import org.feezu.liuli.timeselector.TimeSelector;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;

/**
 * Created by 编辑资料 on 2016/8/24.
 */
@LayoutId(R.layout.activity_edituserinfo)
public class EditUserInfoActviity extends BaseAppCompatActivity implements View.OnClickListener {
    @BindView(R.id.ivHeader)
    ImageView ivHeader;
    @BindView(R.id.rlNickName)
    RelativeLayout rlNickName;
    @BindView(R.id.rlGender)
    RelativeLayout rlGender;
    @BindView(R.id.rlBornDay)
    RelativeLayout rlBornDay;
    @BindView(R.id.rlPhoneNum)
    RelativeLayout rlPhoneNum;
    @BindView(R.id.tvNickName)
    TextView tvNickName;
    @BindView(R.id.tvGender)
    TextView tvGender;
    @BindView(R.id.tvBornDay)
    TextView tvBornDay;
    @BindView(R.id.tvPhoneNum)
    TextView tvPhoneNum;

    private Context context;

    private Uri imageUri; // 图片路径
    private String filename; // 图片名称
    private File outputImage;

    private ActionSheetDialog genderDialog;
    private TimeSelector timeSelector;


    public static final int REQUEST_NICKNAME = 1;
    public static final int REQUEST_PHONENUM = 2;
    private static final int REQUEST_TAKE_PHOTO = 3;
    private static final int REQUEST_CROP_PHOTO = 4;

    private HashMap<String, String> params = new HashMap<>();

    private String changedNickname;

    private String changedGender;

    private String changedHeader;


    @Override
    protected void initViews(Bundle savedInstanceState) {
        setTitle("个人资料");
        context = this;
        ivHeader.setOnClickListener(this);
        rlNickName.setOnClickListener(this);
        rlGender.setOnClickListener(this);
        rlBornDay.setOnClickListener(this);
        rlPhoneNum.setOnClickListener(this);

        initMineViews();
    }

    private void initMineViews() {
        User user = UserProxy.getUser();
        if (user != null) {
            tvNickName.setText(user.getNickname());
            AsyncImage.displayImage(API.IMAGE_HOST + user.getIcon(), ivHeader);
            //sex 性别0男1女
            int sex = user.getSex();
            if (sex == 0) {
                tvGender.setText("男");
            } else if (sex == 1) {
                tvGender.setText("女");
            }
            if (TextUtils.isEmpty(user.getTelephone())) {
                tvPhoneNum.setText("马上绑定");
                tvPhoneNum.setTextColor(context.getResources().getColor(R.color.bar_text_selected));
            } else {
                tvPhoneNum.setText(user.getTelephone());
                tvPhoneNum.setTextColor(context.getResources().getColor(R.color.attention_item_count));
            }
        }
    }


    @Override
    public void onSuccess(JSONObject object, String method) {
        super.onSuccess(object, method);
        if (method.equals(API.EDIT_INFO)) {
            User user = UserProxy.getUser();
            if (!TextUtils.isEmpty(changedNickname)) {
                user.setNickname(changedNickname);
                changedNickname = "";
            } else if (!TextUtils.isEmpty(changedHeader)) {
                user.setIcon(changedHeader);
                changedHeader = "";
                AsyncImage.displayImage(API.IMAGE_HOST+user.getIcon(),ivHeader);
            } else if (!TextUtils.isEmpty(changedGender)) {
                user.setSex(Integer.parseInt(changedGender));
                changedGender = "";
            }
            UserProxy.setUser(user);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ivHeader:
                chooseUploadPic();
                break;
            case R.id.rlGender:
                chooseGender();
                break;
            case R.id.rlBornDay:
                chooseBornDay();
                break;
            case R.id.rlPhoneNum:
                if (!tvPhoneNum.getText().equals("马上绑定")) {
                    Intent intent = new Intent();
                    intent.setClass(context, BoundUserPhoneActivity.class);
                    startActivityForResult(intent, REQUEST_PHONENUM);
                }
                break;
            case R.id.rlNickName:
                Intent intent = new Intent();
                intent.setClass(context, EditNickNameActivity.class);
                intent.putExtra("nickName", tvNickName.getText().toString());
                startActivityForResult(intent, REQUEST_NICKNAME);
                break;
        }
    }

    private void uploadUserInfo(String nickName, String gender, String avatar) {
        User user = UserProxy.getUser();
        if (user != null) {
            params.put("userId", user.getId());
            params.put("token", user.getToken());
            if (!TextUtils.isEmpty(nickName)) {
                params.put("nickname", nickName);
                changedNickname = nickName;
            }
            if (!TextUtils.isEmpty(gender)) {
                if (gender.equals("男")) {
                    params.put("sex", 0 + "");
                    changedGender = "0";
                } else if (gender.equals("女")) {
                    params.put("sex", 1 + "");
                    changedGender = "1";
                }
            }
            if (!TextUtils.isEmpty(avatar)) {
                params.put("icon", avatar);
                changedHeader = avatar;
            }
            sendHttpRequest(API.EDIT_INFO, params);
        }
    }

    private void uploadUserHead() {
        params.clear();
        final Map<String, String> fileParams = new HashMap<>();
        fileParams.put("pic", outputImage.getAbsolutePath());
        new Thread(new Runnable() {
            @Override
            public void run() {
                FileUploadUtil.uploadImageFile(API.HOST + API.UPLOAD_PIC, params, fileParams, mHandler);
            }
        }).start();
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {

                case FileUploadUtil.SUCCESS:
                    Bundle data = msg.getData();
                    String result = data.getString("result");
                    try {
                        JSONObject jsonObj = new JSONObject(result);
                        String imgName = jsonObj.getString("object");
                        uploadUserInfo(null, null, imgName);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                case FileUploadUtil.FAILED:
                    break;
            }
        }
    };

    private void chooseBornDay() {
        timeSelector = new TimeSelector(this, new TimeSelector.ResultHandler() {
            @Override
            public void handle(String time) {
                tvBornDay.setText(time);
            }
        }, "1970-01-01 12:00", "2016-08-24 12:00");
        timeSelector.show();
    }

    private void chooseUploadPic() {
        ActionSheetDialog choosePicDialog = new ActionSheetDialog(context)
                .builder()
                .setCancelable(false)
                .setCanceledOnTouchOutside(true)
                .addSheetItem("拍照", ActionSheetDialog.SheetItemColor.Black,
                        new ActionSheetDialog.OnSheetItemClickListener() {
                            @Override
                            public void onClick(int which) {

                                takePhoto();
                            }
                        })
                .addSheetItem("相册", ActionSheetDialog.SheetItemColor.Black, new ActionSheetDialog.OnSheetItemClickListener() {
                    @Override
                    public void onClick(int which) {
                        takeAlbum();
                    }
                });
        choosePicDialog.show();
    }

    private void takeAlbum() {
        // TODO Auto-generated method stub
        // takePhoto();
        // 图片名称 时间命名
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
        Date date = new Date(System.currentTimeMillis());
        filename = format.format(date);
        // 创建File对象用于存储拍照的图片 SD卡根目录
        // File outputImage = new
        // File(Environment.getExternalStorageDirectory(),"test.jpg");
        // 存储至DCIM文件夹
        File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
        outputImage = new File(path, filename + ".jpg");
        try {
            if (outputImage.exists()) {
                outputImage.delete();
            }
            outputImage.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 将File对象转换为Uri并启动照相程序
        imageUri = Uri.fromFile(outputImage);
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setDataAndType(MediaStore.Images.Media.INTERNAL_CONTENT_URI, "image/*");
        intent.putExtra("output", imageUri);
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);// 裁剪框比例
        intent.putExtra("aspectY", 1);
//        intent.putExtra("outputX", 800);// 输出图片大小
//        intent.putExtra("outputY", 800);
        startActivityForResult(intent, REQUEST_CROP_PHOTO);
    }

    private void takePhoto() {
        // 图片名称 时间命名
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
        Date date = new Date(System.currentTimeMillis());
        filename = format.format(date);
        // 创建File对象用于存储拍照的图片 SD卡根目录
        // File outputImage = new
        // File(Environment.getExternalStorageDirectory(),"test.jpg");
        // 存储至DCIM文件夹
        File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
        outputImage = new File(path, filename + ".jpg");
        try {
            if (outputImage.exists()) {
                outputImage.delete();
            }
            outputImage.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 将File对象转换为Uri并启动照相程序
        imageUri = Uri.fromFile(outputImage);
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE"); // 照相
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri); // 指定图片输出地址
        startActivityForResult(intent, REQUEST_TAKE_PHOTO); // 启动照相
        // 拍完照startActivityForResult() 结果返回onActivityResult()函数
    }

    private void chooseGender() {
        genderDialog = new ActionSheetDialog(context)
                .builder()
                .setCancelable(false)
                .setCanceledOnTouchOutside(true)
                .addSheetItem("男", ActionSheetDialog.SheetItemColor.Black,
                        new ActionSheetDialog.OnSheetItemClickListener() {
                            @Override
                            public void onClick(int which) {
                                tvGender.setText("男");
                                uploadUserInfo(null, "男", null);
                            }
                        })
                .addSheetItem("女", ActionSheetDialog.SheetItemColor.Black,
                        new ActionSheetDialog.OnSheetItemClickListener() {
                            @Override
                            public void onClick(int which) {
                                tvGender.setText("女");
                                uploadUserInfo(null, "女", null);
                            }
                        });
        genderDialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_NICKNAME && resultCode == RESULT_OK && data != null) {
            String nickName = data.getStringExtra("nickName");
            if (!TextUtils.isEmpty(nickName)) {
                uploadUserInfo(nickName, null, null);
                tvNickName.setText(nickName);
            }
        } else if (requestCode == REQUEST_PHONENUM && resultCode == RESULT_OK && data != null) {
            tvPhoneNum.setText(data.getStringExtra("phoneNum"));
        } else if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            Intent intent = new Intent("com.android.camera.action.CROP"); // 剪裁
            intent.setDataAndType(imageUri, "image/*");
            intent.putExtra("scale", true);
            // 设置宽高比例
            intent.putExtra("aspectX", 1);
            intent.putExtra("aspectY", 1);
            // 设置裁剪图片宽高
//            intent.putExtra("outputX", 800);
//            intent.putExtra("outputY", 800);
            intent.putExtra("return-data", true);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            Toast.makeText(this, "剪裁图片", Toast.LENGTH_SHORT).show();
            // 广播刷新相册
            Intent intentBc = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            intentBc.setData(imageUri);
            this.sendBroadcast(intentBc);
            startActivityForResult(intent, REQUEST_CROP_PHOTO); // 设置裁剪参数显示图片至ImageView
        } else if (requestCode == REQUEST_CROP_PHOTO && resultCode == RESULT_OK) {
            uploadUserHead();
        }
    }
}
