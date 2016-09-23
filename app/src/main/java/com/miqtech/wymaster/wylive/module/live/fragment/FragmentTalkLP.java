package com.miqtech.wymaster.wylive.module.live.fragment;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.miqtech.wymaster.wylive.R;
import com.miqtech.wymaster.wylive.annotations.LayoutId;
import com.miqtech.wymaster.wylive.base.BaseFragment;
import com.miqtech.wymaster.wylive.constants.API;
import com.miqtech.wymaster.wylive.entity.AnchorInfo;
import com.miqtech.wymaster.wylive.entity.FirstCommentDetail;
import com.miqtech.wymaster.wylive.entity.LiveRoomAnchorInfo;
import com.miqtech.wymaster.wylive.entity.User;
import com.miqtech.wymaster.wylive.module.comment.PersonalCommentDetail;
import com.miqtech.wymaster.wylive.module.comment.SubmitGradesActivity;
import com.miqtech.wymaster.wylive.module.live.LiveRoomActivity;
import com.miqtech.wymaster.wylive.module.live.adapter.RecreationCommentAdapter;
import com.miqtech.wymaster.wylive.module.login.LoginActivity;
import com.miqtech.wymaster.wylive.observer.Observerable;
import com.miqtech.wymaster.wylive.observer.ObserverableType;
import com.miqtech.wymaster.wylive.proxy.UserProxy;
import com.miqtech.wymaster.wylive.utils.DeviceUtils;
import com.miqtech.wymaster.wylive.utils.L;
import com.miqtech.wymaster.wylive.utils.Utils;
import com.miqtech.wymaster.wylive.utils.imageloader.AsyncImage;
import com.miqtech.wymaster.wylive.widget.CircleImageView;
import com.miqtech.wymaster.wylive.widget.pullToRefresh.PullToRefreshBase;
import com.miqtech.wymaster.wylive.widget.pullToRefresh.PullToRefreshListView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import butterknife.BindView;

/**
 * Created by admin on 2016/7/27.
 */
@LayoutId(R.layout.fragment_talk_lp)
public class FragmentTalkLP extends BaseFragment implements  RecreationCommentAdapter.ItemDataDealWith ,View.OnClickListener{
    @BindView(R.id.lvLivePlayComments)
    PullToRefreshListView lvLivePlayComments;
    @BindView(R.id.ivUpAndDownIcon)
    ImageView ivUpAndDownIcon;
    @BindView(R.id.anchorHeader)
    CircleImageView anchorHeader; //主播头像
    @BindView(R.id.anchorTitle)
    TextView anchorTitle;//主播标题
    @BindView(R.id.rlAnchorInformation)
    RelativeLayout rlAnchorInformation;
    @BindView(R.id.llComment)
    LinearLayout llComment; //评论输入框
    @BindView(R.id.tvAttention)
    TextView tvAttention; //关注按钮
    @BindView(R.id.tvErrorPage)
    TextView tvErrorPage; //异常页面
    @BindView(R.id.tvComment)
    TextView tvComment; //评论类容
    private ListView listView;
    private boolean isFirst=true;
    private RecreationCommentAdapter adapter;
    private int page = 1;
    private int pageSize = 10;
    private User user;
    private int replySize = 10;//   replySize	否	string	楼中楼回复数量（不传默认返回5条）
    private List<FirstCommentDetail> comments = new ArrayList<FirstCommentDetail>();
    private int listId;
    private Dialog mDialog;
    private String replyListPosition;
    private final int ISREFER = 1;//startActivityForResult(intent,)
    private final int type = 7;//评论类型：1-娱乐赛评论；2-官方赛事评论。（不传默认为1）7直播评论
    private String message; //输入框里面的消息
    private LiveRoomAnchorInfo info;
    private String id;//主播id
    private static final int COMMENT_REQUEST=2;
    private String imgName;//后台返回的图片名
    private int upId; //主播id

    @Override
    protected void initViews(View view, Bundle savedInstanceState) {
        initView();
        setOnClickListener();
    }
    private void setOnClickListener() {
        ivUpAndDownIcon.setOnClickListener(this);
        llComment.setOnClickListener(this);
        tvAttention.setOnClickListener(this);
    }
    public void setAnchorData(LiveRoomAnchorInfo info){
        setData(info);
    }
    private void setData(LiveRoomAnchorInfo info) {
        this.info=info;
        this.id=info.getId()+"";
        this.upId=info.getUpUserId();

        AsyncImage.displayImage(info.getIcon(),anchorHeader);
        anchorTitle.setText(info.getNickname());
        setSubscribeState(info.getIsSubscibe()==1?true:false);
        tvAttention.setOnClickListener(this);
        L.e(TAG, "订阅状态:::" + info.getIsSubscibe());
        if(info.getIsSubscibe()==1){
            rlAnchorInformation.setVisibility(View.GONE);
        }else{
            rlAnchorInformation.setVisibility(View.VISIBLE);
        }
        loadOfficalCommentList();
    }
    public void setSubscribeState(boolean isSubscribe){
        if (isSubscribe) {
            tvAttention.setText(mActivity.getResources().getString(R.string.live_room_attentioned));
            tvAttention.setCompoundDrawablesWithIntrinsicBounds(0,0,0,0);
            tvAttention.setTextColor(mActivity.getResources().getColor(R.color.search_edit_border));
            Utils.setShapeBackground(tvAttention,0,0,R.color.anchor_state_offline, DeviceUtils.dp2px(mActivity,5));
        } else {
            tvAttention.setText(mActivity.getResources().getString(R.string.live_room_attention));
            tvAttention.setTextColor(mActivity.getResources().getColor(R.color.white));
            tvAttention.setCompoundDrawablesWithIntrinsicBounds(R.drawable.live_attention,0,0,0);
            Utils.setShapeBackground(tvAttention,0,0,R.color.bar_text_selected, DeviceUtils.dp2px(mActivity,5));
        }
    }
    private void loadOfficalCommentList() {
        user = UserProxy.getUser();
        HashMap params = new HashMap();
        //TODO  战时写死
        params.put("amuseId", id);
        params.put("page", page+"");
        params.put("type", 7+ "");//	评论类型：1-娱乐赛评论；2-官方赛事评论 4自发赛评论。（不传默认为1） 7直播间评论
        params.put("replySize", replySize + "");
        if (user != null) {
            params.put("userId", user.getId() + "");
            params.put("token", user.getToken() + "");
        }
        sendHttpRequest(API.AMUSE_COMMENT_LIST,params);
    }
    private void initView() {
        listView=lvLivePlayComments.getRefreshableView();
        lvLivePlayComments.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
        lvLivePlayComments.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                loadOfficalCommentList();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {

            }

            @Override
            public void isHasNetWork(boolean isHasNetWork) {
                if (!isHasNetWork) {
                    showToast(getString(R.string.noNeteork));
                }
            }
        });

        adapter = new RecreationCommentAdapter(mActivity, comments);
        listView.setAdapter(adapter);
        adapter.setReport(this);
        tvComment.setText(getString(R.string.live_comment_hint,0+""));//设置默认评论数量为0
    }
    @Override
    public void onSuccess(JSONObject object, String method) {
        super.onSuccess(object, method);
        L.e(TAG,"onSuccess"+object.toString());
        lvLivePlayComments.onRefreshComplete();
        tvErrorPage.setVisibility(View.GONE);
     //   hideLoading();
        try {
            if (object == null) {
                return;
            }
        if (method.equals(API.AMUSE_COMMENT_LIST)) {
            initRecreationComment(object);
        } else if (method.equals(API.DEL_COMMENT)) {
            L.e("Delect", "删除成功2222");
            if (0 == object.getInt("code") && "success".equals(object.getString("result"))) {
                if (mDialog != null) {
                    mDialog.dismiss();
                }
                L.e("Delect", "删除成功" + replyListPosition);
                if (!TextUtils.isEmpty(replyListPosition)) {
                    int idd = Integer.parseInt(replyListPosition);
                    int replycount = comments.get(listId).getReplyCount();
                    if (replycount > 1) {
                        comments.get(listId).setReplyCount(replycount - 1);
                    }
                    comments.get(listId).getReplyList().remove(idd);
                    replyListPosition = "";
                } else {
                    comments.remove(listId);
                    if (comments.isEmpty()) {
                        setErrorPage("还没有留言哦，快来说两句吧");
                    }
                }
                adapter.notifyDataSetChanged();
                showToast("删除成功");
            }
        } else if (method.equals(API.V2_COMMENT_PRAISE)) {
            int praisrNum;
            //TODO 处理广播
           // BroadcastController.sendUserChangeBroadcase(context);
            if (comments.get(listId).getIsPraise() == 0) {
                praisrNum = comments.get(listId).getLikeCount();
                comments.get(listId).setIsPraise(1);
                comments.get(listId).setLikeCount(praisrNum + 1);
            } else if (comments.get(listId).getIsPraise() == 1) {
                praisrNum = comments.get(listId).getLikeCount();
                comments.get(listId).setIsPraise(0);
                comments.get(listId).setLikeCount(praisrNum - 1);
            }
            adapter.notifyDataSetChanged();
        } else if (method.equals(API.AMUSE_COMMENT)) {//提交评论
            L.e(TAG,"onSuccess 提交评论成功");
            page = 1;
            pageSize = 10;
            loadOfficalCommentList();
            showToast("发表成功");
        }else if(method.equals(API.LIVE_SUBSCRIBE)){
            try {
                if(object.getInt("code")==0 && "success".equals(object.getString("result"))){
                    info.setIsSubscibe(info.getIsSubscibe()==1?0:1);
                    if(info.getIsSubscibe()==1){
                        rlAnchorInformation.setVisibility(View.GONE);
                    }
                    ((LiveRoomActivity)getActivity()).updataSubscribeState(info.getIsSubscibe()==1?true:false);

                    AnchorInfo anchorInfo = new AnchorInfo();
                    anchorInfo.setState(info.getState());
                    anchorInfo.setFans(info.getFans());
                    anchorInfo.setRoom(info.getRoom());
                    anchorInfo.setId(info.getId());
                    anchorInfo.setName(info.getNickname());
                    anchorInfo.setIcon(info.getIcon());
                    Observerable.getInstance().notifyChange(ObserverableType.ATTENTION_ANCHOR,anchorInfo);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onFaild(JSONObject object, String method) {
        super.onFaild(object, method);
        L.e(TAG,"onFaild"+object.toString());
       // hideLoading();
        lvLivePlayComments.onRefreshComplete();
        try {
            int code=object.getInt("code");
            String result=object.getString("result");
           showToast(result);
        } catch (JSONException e) {
            e.printStackTrace();
        }
       // setErrorPage("还没有留言哦，快来说两句吧");
    }

    @Override
    public void onError(String errMsg, String method) {
        super.onError(errMsg, method);
        L.e(TAG,"onError"+errMsg);
        lvLivePlayComments.onRefreshComplete();
    //    hideLoading();
        setErrorPage("网络不给力，请检查网络再试试");
    }

    /**
     * 设置错误页面
     * @param errorHint
     */
    private void setErrorPage(String errorHint){
        tvErrorPage.setVisibility(View.VISIBLE);
        tvErrorPage.setText(errorHint);
    }
    private void initRecreationComment(JSONObject object) {
        try {
            if (object.getInt("code") == 0 && object.has("object")) {
                String strObj = object.getString("object");
                int totalComments=object.getJSONObject("object").getInt("total");
                ((LiveRoomActivity)mActivity).setData(totalComments);
                tvComment.setText(getString(R.string.live_comment_hint,totalComments+""));
                JSONObject jsonObj = new JSONObject(strObj);
                if (jsonObj.has("list")) {
                    Gson gs=new Gson();
                    List<FirstCommentDetail> newComments = gs.fromJson(jsonObj.getString("list").toString(), new TypeToken<List<FirstCommentDetail>>() {}.getType());
                    comments.clear();
                    if (newComments != null && !newComments.isEmpty()) {
                        comments.addAll(newComments);
                    }
                    adapter.notifyDataSetChanged();
                } else {
                    comments.clear();
                    adapter.notifyDataSetChanged();
               //     setErrorPage("还没有留言哦，快来说两句吧");
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delect(int position) {
        if (!comments.isEmpty() && position < comments.size()) {
            listId = position;
            creatDialogForDelect(comments.get(position).getId());
            Log.i(TAG, "删除的用户的id" + comments.get(position).getUserId());
        }
    }
    @Override
    public void praiseComment(int position) {
        user = UserProxy.getUser();
        if (user != null) {
            listId = position;
            Map<String, String> map = new HashMap<>();
            map.put("commentId", comments.get(position).getId() + "");
            map.put("userId", user.getId());
            map.put("token", user.getToken());
            sendHttpRequest(API.V2_COMMENT_PRAISE,map);
        } else {
            jumpToActivity(LoginActivity.class);
            //showToast(getResources().getString(R.string.pleaseLogin));
        }
    }

    @Override
    public void replyComment(int position) {
        Intent intent = new Intent(mActivity, PersonalCommentDetail.class);
        intent.putExtra("amuseId", id);
        intent.putExtra("type", type);
        intent.putExtra("parentId", comments.get(position).getId());
        intent.putExtra("isPopupKeyboard", 1);//判断是否弹出键盘   1  弹出  0不弹出   默认不弹
        startActivityForResult(intent, ISREFER);
    }

    @Override
    public void lookComment(int position) {
        Intent intent = new Intent(mActivity, PersonalCommentDetail.class);
        intent.putExtra("amuseId", id);
        intent.putExtra("type", type);
        intent.putExtra("parentId", comments.get(position).getId());
        intent.putExtra("isPopupKeyboard", 0);//判断是否弹出键盘   1  弹出  0不弹出   默认不弹
        startActivityForResult(intent, ISREFER);
    }

    @Override
    public void lookCommentSection() {
    }

    @Override
    public void delectReplyReply(int position, int replyListid) {
        user = UserProxy.getUser();
        if (user != null) {
            if (!comments.isEmpty() && !comments.get(position).getReplyList().isEmpty()) {
                listId = position;
                replyListPosition = replyListid + "";
                creatDialogForDelect(comments.get(position).getReplyList().get(replyListid).getId());
            }
        } else {
           jumpToActivity(LoginActivity.class);
        }
    }
    private void creatDialogForDelect(final String id) {
        mDialog = new Dialog(mActivity, R.style.register_style);
        mDialog.setContentView(R.layout.dialog_register_marked_words);
        TextView title = (TextView) mDialog.findViewById(R.id.dialog_title_register);
        TextView yes = (TextView) mDialog.findViewById(R.id.dialog_register_yes_pact);
        TextView no = (TextView) mDialog.findViewById(R.id.dialog_register_no_pact);
        View line = mDialog.findViewById(R.id.dialog_line_no_pact);
        line.setVisibility(View.VISIBLE);
        no.setVisibility(View.VISIBLE);
        title.setVisibility(View.VISIBLE);
        title.setText("是否删除评论");
        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                delectcomment(id);
                L.e("Delect", "删除" + id);
                mDialog.dismiss();
            }
        });
        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
            }
        });
        mDialog.show();
    }

    private void delectcomment(String id) {
        user = UserProxy.getUser();
        if (user != null) {
            Map<String, String> map = new HashMap<>();
            map.put("commentId", id);
            map.put("userId", user.getId());
            map.put("token", user.getToken());
            sendHttpRequest(API.DEL_COMMENT,map);
            L.e("Delect", "删除q赢球" + id + "::" + user.getId() + ":::" + user.getToken());
        } else {
            jumpToActivity(LoginActivity.class);
           // showToast(getResources().getString(R.string.pleaseLogin));
        }
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ivUpAndDownIcon:
                rlAnchorInformation.setVisibility(View.GONE);
                break;
            case R.id.llComment:
                Intent intent=new Intent(getActivity(), SubmitGradesActivity.class);
                intent.putExtra("fromType",1);
                startActivityForResult(intent,COMMENT_REQUEST);
                break;
            case  R.id.tvAttention:
                getAttentionRequest();
                break;
        }
    }
    private void getAttentionRequest(){
      //  showLoading();
        Map<String, String> params = new HashMap();
        if(UserProxy.getUser()!=null){
            params.put("userId",UserProxy.getUser().getId()+"");
            params.put("token",UserProxy.getUser().getToken());
            params.put("upUserId",upId+"");
            //TODO 传递数据
            sendHttpRequest(API.LIVE_SUBSCRIBE,params);
        }else{
            jumpToActivity(LoginActivity.class);
        }

    }

    /**
     * 提交评论
     */
    private void submitComment() {
        Map<String, String> map = new HashMap();
        user = UserProxy.getUser();
        if (user != null) {
            map.put("amuseId", id);
            map.put("userId", user.getId() + "");
            map.put("token", user.getToken() + "");
            map.put("content", Utils.replaceBlank(message));
            if(!TextUtils.isEmpty(imgName)){
                map.put("img",imgName);
            }
            map.put("type", 7 + "");// type否	string	评论类型：1-娱乐赛评论；2-官方赛事评论。（不传默认为1） 4自发赛 6 悬赏令 7 直播间 8 视频
            sendHttpRequest(API.AMUSE_COMMENT,map);
        } else {
            jumpToActivity(LoginActivity.class);
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ISREFER && data != null) {
            int myIsRefre = data.getIntExtra("isRefre", -1);
            if (myIsRefre == 1) {
                loadOfficalCommentList();
            }
        } else if(requestCode==COMMENT_REQUEST && resultCode==SubmitGradesActivity.RESULT_OK && data!=null){
            imgName =data.getStringExtra("imgName");
            message=data.getStringExtra("remark");
            L.e(TAG,"imgName"+imgName+":::message"+message);
            submitComment();
        }
    }
}
