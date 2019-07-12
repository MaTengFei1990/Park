package com.hollysmart.formlib.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Vibrator;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hollysmart.beans.DictionaryBean;
import com.hollysmart.formlib.beans.DongTaiFormBean;
import com.hollysmart.beans.JDPicInfo;
import com.hollysmart.beans.cgformRuleBean;
import com.hollysmart.dialog.BsSelectDialog;
import com.hollysmart.dialog.TimePickerDialog;
import com.hollysmart.formlib.activitys.MapRangeActivity;
import com.hollysmart.park.R;
import com.hollysmart.utils.Mlog;
import com.hollysmart.utils.Utils;
import com.hollysmart.views.linearlayoutforlistview.MyLinearLayoutForListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.content.Context.VIBRATOR_SERVICE;


/**
 * Created by cai on 2017/12/5  基础表格
 */
public class BiaoGeRecyclerAdapter2 extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private List<DongTaiFormBean> biaoGeBeanList;
    private TimePickerDialog timePickerDialog;

    private boolean isCheck=false; //是否是查看，true查看，不能编辑；


    JDPicInfo picBeannull = new JDPicInfo(0, null, null, null, 1, "false");

    private HashMap<String, List<DictionaryBean>> map = new HashMap<>();

    public BiaoGeRecyclerAdapter2(Context mContext, List<DongTaiFormBean> biaoGeBeanList ,boolean isCheck) {
        this.mContext = mContext;
        mLayoutInflater = LayoutInflater.from(mContext);
        this.biaoGeBeanList = biaoGeBeanList;
        this.isCheck = isCheck;
        for (DongTaiFormBean bean : biaoGeBeanList) {
            String propertyLabel = bean.getPropertyLabel();
            List<DongTaiFormBean> childlist = bean.getCgformFieldList();
            if (Utils.isEmpty(propertyLabel) && childlist != null && childlist.size() > 0) {
                bean.setPropertyLabel("0");
            }

            if (Utils.isEmpty(propertyLabel) && bean.getShowType().equals("switch")) {
                bean.setPropertyLabel("0");
            }
            if (childlist != null && childlist.size() > 0) {

                for (DongTaiFormBean childbean : childlist) {

                    String childpropertyLabel = childbean.getPropertyLabel();

                    List<DongTaiFormBean> childcgformFieldList = childbean.getCgformFieldList();
                    if (Utils.isEmpty(childpropertyLabel) && childcgformFieldList != null && childcgformFieldList.size() > 0) {
                        childbean.setPropertyLabel("0");
                    }

                    if (Utils.isEmpty(childpropertyLabel) && childbean.getShowType().equals("switch")) {
                        childbean.setPropertyLabel("0");
                    }
                }
            }


        }
    }


    public void setMap(HashMap<String, List<DictionaryBean>> map) {
        this.map = map;
    }

    private static int VIEWTYPE_DANHANG = 0;
    private static int VIEWTYPE_DANHANG_LIST = 1;
    private static int VIEWTYPE_DANHANG_TIME_SELECT = 2;
    private static int VIEWTYPE_CONTENT_CHILD_LIST = 3;//包含子表单
    private static int VIEWTYPE_CONTENT_IMAGE_CONTENT = 4;//包含图片
    private static int VIEWTYPE_CONTENT_MARKER_CONTENT = 5;//地图定位
    private static int VIEWTYPE_CONTENT_PLANE_CONTENT = 6;//面
    private static int VIEWTYPE_CONTENT_LINE_CONTENT = 8;//线
    private static int VIEWTYPE_CONTENT_SWITCH_CONTENT = 9;//开关

    /**
     * propertyType
     * 0   单文本
     * 1   多行文本
     * 2   字典
     * 3   子表单
     * 4   动态数组
     * expression
     * ""  无
     * "year"      年（YYYY）
     * "date"      日期（YYYY-MM-DD）
     * "email"     邮箱
     * "mobile"    手机
     * "num"       数字
     * "zipcode"   邮编
     * "idcard"    身份证
     * "url"       url
     *
     * @return -1         无
     * 0          单行
     * 1          单行 有父标签
     * 2          单行 选择
     * 3          单行 选择 有父标签
     * 4          多行
     * 5          子表单
     * 6          字典
     * 6          照片选择； image
     *
     * marker:地图定位 plane:面  line：线  image:图片
     */
    @Override
    public int getItemViewType(int position) {

        DongTaiFormBean bean = getItemStatusByPosition(position);

        if (bean == null) {
            return 0;
        }


        if (bean.getShowType() == null) {

            return VIEWTYPE_DANHANG;

        }




        if (bean.getShowType() .equals("text") ) {
            return VIEWTYPE_DANHANG;
        }
        if (bean.getShowType() .equals("datetime") ) {

            return VIEWTYPE_DANHANG_TIME_SELECT;
        }
        if (bean.getShowType().equals("list") ) {

            return VIEWTYPE_DANHANG_LIST;
        }
        if (bean.getShowType().equals("image") ) {

            return VIEWTYPE_CONTENT_IMAGE_CONTENT;
        }
        if (bean.getShowType().equals("marker") ) {

            return VIEWTYPE_CONTENT_MARKER_CONTENT;
        }
        if (bean.getShowType().equals("plane") ) {

            return VIEWTYPE_CONTENT_PLANE_CONTENT;
        }
        if (bean.getShowType().equals("line") ) {

            return VIEWTYPE_CONTENT_LINE_CONTENT;
        }
        if (bean.getShowType().equals("switch") ) {

            return VIEWTYPE_CONTENT_SWITCH_CONTENT;
        }

        return 0;
    }


    @Override
    public int getItemCount() {
        int ParentitemCount = 0;
        int childItemCount = 0;


        for (int i = 0; i < biaoGeBeanList.size(); i++) {

            DongTaiFormBean dongTaiFormBean = biaoGeBeanList.get(i);

            if (dongTaiFormBean.getCgformFieldList() != null && dongTaiFormBean.getCgformFieldList().size() > 0) {

                String propertyLabel = dongTaiFormBean.getPropertyLabel();

                if (propertyLabel!=null&&propertyLabel.equals("0")) {

                } else {

                    childItemCount = childItemCount + dongTaiFormBean.getCgformFieldList().size();
                }

            }


        }

        ParentitemCount = biaoGeBeanList.size();








        return ParentitemCount+childItemCount;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEWTYPE_DANHANG) {
            return new DanhangViewHolder(mLayoutInflater.inflate(R.layout.item_biaoge_danhang, parent, false));
        } else if (viewType == VIEWTYPE_DANHANG_TIME_SELECT) {
            return new DanhangXuanZeViewHolder(mLayoutInflater.inflate(R.layout.item_biaoge_danhang_xuanze, parent, false));
        } else if (viewType == VIEWTYPE_DANHANG_LIST) {
            return new DanhangXuanZelistViewHolder(mLayoutInflater.inflate(R.layout.item_biaoge_danhang_list, parent, false));
        }else if(viewType==VIEWTYPE_CONTENT_IMAGE_CONTENT){
            return new ImageContentViewHolder(mLayoutInflater.inflate(R.layout.item_biaoge_image_content, parent, false));
        }else if(viewType==VIEWTYPE_CONTENT_LINE_CONTENT){
            return new MapContentViewHolder(mLayoutInflater.inflate(R.layout.item_biaoge_danhang_xuanze, parent, false));
        }else if(viewType==VIEWTYPE_CONTENT_MARKER_CONTENT){
            return new MapContentViewHolder(mLayoutInflater.inflate(R.layout.item_biaoge_danhang_xuanze, parent, false));
        }else if(viewType==VIEWTYPE_CONTENT_PLANE_CONTENT){
            return new MapContentViewHolder(mLayoutInflater.inflate(R.layout.item_biaoge_danhang_xuanze, parent, false));
        }else if(viewType==VIEWTYPE_CONTENT_SWITCH_CONTENT){
            return new SwitchContentViewHolder(mLayoutInflater.inflate(R.layout.item_biaoge_switch_content, parent, false));
        }

        return null;
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        DongTaiFormBean bean = getItemStatusByPosition(position);

        if (bean == null) {
            position=position;
        }


        if (holder instanceof DanhangViewHolder) {
            danhang((DanhangViewHolder) holder, bean);
        } else if (holder instanceof DanhangParentViewHolder) {
            danhangParent((DanhangParentViewHolder) holder, bean);
        } else if (holder instanceof DanhangXuanZeViewHolder) {
            danhangXuanze((DanhangXuanZeViewHolder) holder, bean);
        } else if (holder instanceof DanhangXuanZelistViewHolder) {
            danhangXuanzelist((DanhangXuanZelistViewHolder) holder, bean);
        } else if (holder instanceof ImageContentViewHolder) {
            imageContent((ImageContentViewHolder) holder, bean,position);
        }else if (holder instanceof MapContentViewHolder) {
            mapContent((MapContentViewHolder) holder, bean);
        }else if (holder instanceof SwitchContentViewHolder) {
            switchContent((SwitchContentViewHolder) holder, bean);
        }


    }


    //0  单行
    private class DanhangViewHolder extends RecyclerView.ViewHolder {
        private TextView tv_bitian;
        private TextView tv_name;
        private EditText et_value;
        private TextView tv_tishi;

        public DanhangViewHolder(View itemView) {
            super(itemView);
            tv_bitian =  itemView.findViewById(R.id.tv_bitian);
            tv_name =  itemView.findViewById(R.id.tv_name);
            et_value =  itemView.findViewById(R.id.et_value);
            tv_tishi =  itemView.findViewById(R.id.tv_tishi);
            et_value.addTextChangedListener(tw);
        }

        private TextWatcher tw = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
//                biaoGeBeanList.get(getLayoutPosition()).setPropertyLabel(s.toString());
                getItemStatusByPosition(getLayoutPosition()).setPropertyLabel(s.toString());
            }
        };
    }

    private void danhang(final DanhangViewHolder holder, final DongTaiFormBean bean) {
        if (bean.getFieldMustInput()) {
            holder.tv_bitian.setVisibility(View.VISIBLE);
        } else {
            holder.tv_bitian.setVisibility(View.GONE);
        }

        if (bean.isShowTiShi()) {
            holder.tv_tishi.setVisibility(View.VISIBLE);
            holder.et_value.requestFocus();
        } else {
            holder.tv_tishi.setVisibility(View.GONE);
        }

        holder.tv_name.setText(bean.getContent());
        if (bean.getPropertyLabel() != null) {
            holder.et_value.setText(bean.getPropertyLabel().toString());
        } else {
            holder.et_value.setText("");
        }
        holder.et_value.clearFocus();

        if (isCheck) {
            holder.et_value.setEnabled(false);

        } else {
            holder.et_value.setEnabled(true);
        }

        holder.et_value.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                Mlog.d("hasFocus = " + hasFocus);

                List<cgformRuleBean> cgformRuleList = bean.getCgformRuleList();
                Matcher m=null;
                cgformRuleBean cgformRuleBean=null;
                if (cgformRuleList != null && cgformRuleList.size() > 0) {
                    cgformRuleBean = cgformRuleList.get(0);
                    String par = cgformRuleBean.getPattern();

                    Pattern p = Pattern.compile(par);
                    m = p.matcher(bean.getPropertyLabel());

                }


                if (!hasFocus&&!Utils.isEmpty(holder.et_value.getText().toString())&&m!=null) {

                    if (!m.matches()) {
                        holder.et_value.setText("");
                        holder.et_value.clearFocus();
                        Utils.showDialog(mContext, cgformRuleBean.getError());
                    }


                }
            }
        });

    }


    //1  单行 有父标签
    private class DanhangParentViewHolder extends RecyclerView.ViewHolder {
        private TextView tv_bitian;
        private TextView tv_name;
        private EditText et_value;
        private TextView tv_tishi;

        public DanhangParentViewHolder(View itemView) {
            super(itemView);
            tv_bitian = (TextView) itemView.findViewById(R.id.tv_bitian);
            tv_name = (TextView) itemView.findViewById(R.id.tv_name);
            et_value = (EditText) itemView.findViewById(R.id.et_value);
            tv_tishi = (TextView) itemView.findViewById(R.id.tv_tishi);
            et_value.addTextChangedListener(tw);
        }

        private TextWatcher tw = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                biaoGeBeanList.get(getLayoutPosition()).setPropertyLabel(s.toString());
            }
        };
    }

    private void danhangParent(final DanhangParentViewHolder holder, final DongTaiFormBean bean) {
        if (bean.getFieldMustInput()) {
            holder.tv_bitian.setVisibility(View.VISIBLE);
        } else {
            holder.tv_bitian.setVisibility(View.GONE);
        }

        if (bean.isShowTiShi()) {
            holder.tv_tishi.setVisibility(View.VISIBLE);
        } else {
            holder.tv_tishi.setVisibility(View.GONE);
        }


        holder.tv_name.setText(bean.getContent());

        if (bean.getPropertyLabel() != null) {
            holder.et_value.setText(bean.getPropertyLabel().toString());
        } else {
            holder.et_value.setText("");
        }

        if (isCheck) {
            holder.et_value.setEnabled(false);

        } else {
            holder.et_value.setEnabled(true);
        }

//        if ("email".equals(bean.getExpression())) {
//            holder.et_value.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_NORMAL);
//        } else if ("mobile".equals(bean.getExpression())) {
//            holder.et_value.setInputType(InputType.TYPE_CLASS_PHONE);
//        } else if ("num".equals(bean.getExpression())) {
//            holder.et_value.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_NORMAL);
//        } else if ("zipcode".equals(bean.getExpression())) {
//            holder.et_value.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_NORMAL);
//        } else if ("idcard".equals(bean.getExpression())) {
//            holder.et_value.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_NORMAL);
//        } else if ("url".equals(bean.getExpression())) {
//            holder.et_value.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_NORMAL);
//        } else {
//            holder.et_value.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_NORMAL);
//        }

        holder.et_value.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                Mlog.d("hasFocus = " + hasFocus);
//                if (!hasFocus) {
//                    if ("email".equals(bean.getExpression())) {
//                        if (!Utils.isEmail(bean.getPropertyLabel().toString())) {
//                            holder.tv_tishi.setText("邮箱格式不正确，请检查");
//                            holder.tv_tishi.setVisibility(View.VISIBLE);
//                        } else {
//                            holder.tv_tishi.setVisibility(View.GONE);
//                        }
//                    } else if ("mobile".equals(bean.getExpression())) {
//                        if (!Utils.checkMobilePhone(bean.getPropertyLabel().toString())) {
//                            holder.tv_tishi.setText("手机号不正确，请检查");
//                            holder.tv_tishi.setVisibility(View.VISIBLE);
//                        } else {
//                            holder.tv_tishi.setVisibility(View.GONE);
//                        }
//                    } else if ("zipcode".equals(bean.getExpression())) {
//                        if (!Utils.isPostCode(bean.getPropertyLabel().toString())) {
//                            holder.tv_tishi.setText("邮编不正确，请检查");
//                            holder.tv_tishi.setVisibility(View.VISIBLE);
//                        } else {
//                            holder.tv_tishi.setVisibility(View.GONE);
//                        }
//                    } else if ("idcard".equals(bean.getExpression())) {
//                        if (!Utils.checkIDCard(bean.getPropertyLabel().toString())) {
//                            holder.tv_tishi.setText("身份证不正确，请检查");
//                            holder.tv_tishi.setVisibility(View.VISIBLE);
//                        } else {
//                            holder.tv_tishi.setVisibility(View.GONE);
//                        }
//                    }
//                }
            }
        });

    }



    //2  单行 选择
    private class DanhangXuanZelistViewHolder extends RecyclerView.ViewHolder {
        private TextView tv_bitian;
        private TextView tv_name;
        private TextView tv_value;
        private TextView tv_tishi;
        private LinearLayout ll_value;
        private ImageView iv_arrorw;

        public DanhangXuanZelistViewHolder(View itemView) {
            super(itemView);
            tv_bitian =  itemView.findViewById(R.id.tv_bitian);
            tv_name =  itemView.findViewById(R.id.tv_name);
            tv_tishi =  itemView.findViewById(R.id.tv_tishi);
            tv_value =  itemView.findViewById(R.id.tv_value);
            ll_value =  itemView.findViewById(R.id.ll_value);
            iv_arrorw =  itemView.findViewById(R.id.iv_arrorw);
        }
    }

    private void danhangXuanzelist(final DanhangXuanZelistViewHolder holder, final DongTaiFormBean bean) {
        if (bean.getFieldMustInput()) {
            holder.tv_bitian.setVisibility(View.VISIBLE);
        } else {
            holder.tv_bitian.setVisibility(View.GONE);
        }

        if (bean.getPropertyLabel() != null) {

            holder.tv_value.setText(bean.getPropertyLabel());

        } else {
            holder.tv_value.setText("");
        }

        if (bean.isShowTiShi()) {
            holder.tv_tishi.setVisibility(View.VISIBLE);
        } else {
            holder.tv_tishi.setVisibility(View.GONE);
        }

        if (isCheck) {
            holder.ll_value.setEnabled(false);
            holder.iv_arrorw.setVisibility(View.INVISIBLE);

        } else {
            holder.ll_value.setEnabled(true);
            holder.iv_arrorw.setVisibility(View.VISIBLE);
        }


        holder.tv_name.setText(bean.getContent());
        holder.ll_value.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final List<DictionaryBean> dictionaryBeans = map.get(bean.getDictText());
                if (dictionaryBeans != null && dictionaryBeans.size() > 0) {
                    new BsSelectDialog(new BsSelectDialog.BsSelectIF() {
                        @Override
                        public void onBsSelect(int type, int index) {
                            DictionaryBean dictionaryBean = dictionaryBeans.get(index);
                            holder.tv_value.setText(dictionaryBean.getLabel());


                            String oldPropertylabel = bean.getPropertyLabel();


                            if (Utils.isEmpty(oldPropertylabel)) {

                                bean.setPropertyLabel(dictionaryBean.getLabel());

                                if (bean.getCgformFieldList() != null && bean.getPropertyLabel().equals("0")) {


                                    notifyItemRangeRemoved(bean.getPosition()+1,bean.getCgformFieldList().size());

                                }

                            }else if (!oldPropertylabel.equals(dictionaryBean.getLabel())) {

                                bean.setPropertyLabel(dictionaryBean.getLabel());

                                if (bean.getCgformFieldList() != null && bean.getPropertyLabel().equals("1")) {


                                    notifyItemRangeInserted(bean.getPosition()+1,bean.getCgformFieldList().size());

                                }

                                if (bean.getCgformFieldList() != null && bean.getPropertyLabel().equals("0")) {


                                    notifyItemRangeRemoved(bean.getPosition()+1,bean.getCgformFieldList().size());

                                }


                            }

                        }
                    }).showPopuWindow_DictListData(mContext,0, dictionaryBeans.get(0).getDescription(),map.get(bean.getDictText()));

                }

            }
        });

    }



    private class DanhangXuanZeViewHolder extends RecyclerView.ViewHolder {
        private TextView tv_bitian;
        private TextView tv_name;
        private TextView tv_value;
        private TextView tv_tishi;
        private ImageView iv_arrorw;
//        private LinearLayout ll_value;

        public DanhangXuanZeViewHolder(View itemView) {
            super(itemView);
            tv_bitian =  itemView.findViewById(R.id.tv_bitian);
            tv_name =  itemView.findViewById(R.id.tv_name);
            tv_value =  itemView.findViewById(R.id.tv_value);
            tv_tishi =  itemView.findViewById(R.id.tv_tishi);
            iv_arrorw =  itemView.findViewById(R.id.iv_arrorw);
//            ll_value =  itemView.findViewById(R.id.ll_value);
        }
    }

    private void danhangXuanze(final DanhangXuanZeViewHolder holder, final DongTaiFormBean bean) {
        if (bean.getFieldMustInput()) {
            holder.tv_bitian.setVisibility(View.VISIBLE);
        } else {
            holder.tv_bitian.setVisibility(View.GONE);
        }

        if (bean.getPropertyLabel() != null) {
            holder.tv_value.setText(bean.getPropertyLabel().toString());
        } else {
            holder.tv_value.setText("");
        }

        if (bean.isShowTiShi()) {
            holder.tv_tishi.setVisibility(View.VISIBLE);
        } else {
            holder.tv_tishi.setVisibility(View.GONE);
        }

        if (isCheck) {
            holder.tv_value.setEnabled(false);
            holder.iv_arrorw.setVisibility(View.INVISIBLE);

        } else {
            holder.tv_value.setEnabled(true);
            holder.iv_arrorw.setVisibility(View.VISIBLE);
        }


        holder.tv_name.setText(bean.getContent());
        holder.tv_value.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timePickerDialog = new TimePickerDialog(mContext, new TimePickerDialog.TimePickerDialogInterface() {
                    @Override
                    public void positiveListener() {
                        String date = timePickerDialog.getYear() + "-" + timePickerDialog.getMonth()
                                + "-" + timePickerDialog.getDay();
                        holder.tv_value.setText(date);
                        bean.setPropertyLabel(date);
                    }

                    @Override
                    public void negativeListener() {
                    }
                });
                timePickerDialog.showDatePickerDialog();
            }
        });

    }


    /***
     * image
     */

    private class ImageContentViewHolder extends RecyclerView.ViewHolder {
        private MyLinearLayoutForListView ll_jingdian_pic;
        private TextView tv_name;
        private TextView tv_tishi;
        private TextView tv_bitian;
        private TextView tv_value;

        public ImageContentViewHolder(View itemView) {
            super(itemView);
            ll_jingdian_pic =  itemView.findViewById(R.id.ll_jingdian_pic);
            tv_name =  itemView.findViewById(R.id.tv_name);

            tv_name =  itemView.findViewById(R.id.tv_name);
            tv_bitian =  itemView.findViewById(R.id.tv_bitian);

            tv_value =  itemView.findViewById(R.id.tv_value);
            tv_tishi =  itemView.findViewById(R.id.tv_tishi);
        }
    }


    private void imageContent(final ImageContentViewHolder holder, final DongTaiFormBean bean,int position) {

        List<JDPicInfo> list = new ArrayList<>();
        ItemPicAdater itemPicAdater = new ItemPicAdater(mContext, list, bean,picBeannull,isCheck);

        list.clear();
        List<JDPicInfo> picinfosList = bean.getPic();
        if (picinfosList != null && picinfosList.size() > 0) {
            for (int i = 0; i < picinfosList.size(); i++) {
                JDPicInfo jdPicInfo = picinfosList.get(i);

                if (!list.contains(jdPicInfo)) {
                    list.add(0, jdPicInfo);

                }
            }

        }
        if (listContainNull(list) == null) {
            if (isCheck) {
            } else {
                list.add(list.size(), picBeannull);

            }

        } else {
            JDPicInfo jdPicInfo = listContainNull(list);
            list.remove(jdPicInfo);

            if (isCheck) {
            } else {
                list.add(list.size(), picBeannull);

            }

        }
        holder.ll_jingdian_pic.removeAllViews();
        holder.ll_jingdian_pic.setAdapter(itemPicAdater);
        holder.tv_name.setText(bean.getContent());


        if (bean.getFieldMustInput()) {
            holder.tv_bitian.setVisibility(View.VISIBLE);
        } else {
            holder.tv_bitian.setVisibility(View.GONE);
        }

        if (bean.isShowTiShi()) {
            holder.tv_tishi.setVisibility(View.VISIBLE);
        } else {
            holder.tv_tishi.setVisibility(View.GONE);
        }

    }


    private JDPicInfo listContainNull(List<JDPicInfo> jdPicslist) {

        if (jdPicslist != null && jdPicslist.size() > 0) {

            for (int i = 0; i < jdPicslist.size(); i++) {

                JDPicInfo jdPicInfo = jdPicslist.get(i);

                if (jdPicInfo.getIsAddFlag() == 1) {
                    return jdPicInfo;
                }

            }

        }

        return null;


    }



    /***
     * map
     */

    private class MapContentViewHolder extends RecyclerView.ViewHolder {
        private TextView tv_bitian;
        private TextView tv_name;
        private TextView tv_value;
        private TextView tv_tishi;
        private LinearLayout ll_value;
        private ImageView iv_arrorw;

        public MapContentViewHolder(View itemView) {
            super(itemView);
            tv_bitian =  itemView.findViewById(R.id.tv_bitian);
            tv_name =  itemView.findViewById(R.id.tv_name);
            tv_value =  itemView.findViewById(R.id.tv_value);
            tv_tishi =  itemView.findViewById(R.id.tv_tishi);
            ll_value =  itemView.findViewById(R.id.ll_value);
            iv_arrorw =  itemView.findViewById(R.id.iv_arrorw);
        }
    }

    private void mapContent(final MapContentViewHolder holder, final DongTaiFormBean bean) {

        if (bean.getFieldMustInput()) {
            holder.tv_bitian.setVisibility(View.VISIBLE);
        } else {
            holder.tv_bitian.setVisibility(View.GONE);
        }

        if (bean.getPropertyLabel() != null) {
            holder.tv_value.setText(bean.getPropertyLabel().toString());
        } else {
            holder.tv_value.setText("");
        }

        if (bean.isShowTiShi()) {
            holder.tv_tishi.setVisibility(View.VISIBLE);
        } else {
            holder.tv_tishi.setVisibility(View.GONE);
        }


        holder.tv_name.setText(bean.getContent());

        holder.ll_value.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bean.getShowType().equals("marker")) {

                    Intent intent=new Intent(mContext, MapRangeActivity.class);
                    intent.putExtra("falg", bean.getShowType());
                    intent.putExtra("bean", bean);
                    intent.putExtra("isCheck", isCheck);
                    Activity activity = (Activity) mContext;
                    activity.startActivityForResult(intent,6);
                }
                if (bean.getShowType().equals("plane")) {
                    Intent intent=new Intent(mContext, MapRangeActivity.class);
                    intent.putExtra("falg", bean.getShowType());
                    intent.putExtra("bean", bean);
                    intent.putExtra("isCheck", isCheck);
                    Activity activity = (Activity) mContext;
                    activity.startActivityForResult(intent,6);
                }
                if (bean.getShowType().equals("line")) {
                    Intent intent=new Intent(mContext, MapRangeActivity.class);
                    intent.putExtra("falg", bean.getShowType());
                    intent.putExtra("bean", bean);
                    intent.putExtra("isCheck", isCheck);
                    Activity activity = (Activity) mContext;
                    activity.startActivityForResult(intent,6);
                }


            }
        });

    }




    /***
     * switch
     */

    private class SwitchContentViewHolder extends RecyclerView.ViewHolder {
        private TextView tv_bitian;
        private TextView tv_name;
        private TextView tv_tishi;
        private ImageView iv_switch;

        public SwitchContentViewHolder(View itemView) {
            super(itemView);
            tv_bitian =  itemView.findViewById(R.id.tv_bitian);
            tv_name =  itemView.findViewById(R.id.tv_name);
            tv_tishi =  itemView.findViewById(R.id.tv_tishi);
            iv_switch =  itemView.findViewById(R.id.iv_switch);
        }
    }

    private void switchContent(final SwitchContentViewHolder holder, final DongTaiFormBean bean) {

        if (bean.getFieldMustInput()) {
            holder.tv_bitian.setVisibility(View.VISIBLE);
        } else {
            holder.tv_bitian.setVisibility(View.GONE);
        }

        if (!Utils.isEmpty(bean.getPropertyLabel())) {
            if (bean.getPropertyLabel().equals("1")) {
                holder.iv_switch.setImageResource(R.mipmap.check_on);
            }

            if (bean.getPropertyLabel().equals("0")) {
                holder.iv_switch.setImageResource(R.mipmap.check_off);
            }


        } else {
            holder.iv_switch.setImageResource(R.mipmap.check_off);

        }

        if (bean.isShowTiShi()) {
            holder.tv_tishi.setVisibility(View.VISIBLE);
        } else {
            holder.tv_tishi.setVisibility(View.GONE);
        }


        holder.tv_name.setText(bean.getContent());

        if (isCheck) {
            holder.iv_switch.setEnabled(false);

        } else {
            holder.iv_switch.setEnabled(true);
        }
        holder.iv_switch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vibrate(mContext);
                String oldPropertylabel = bean.getPropertyLabel();


                if (Utils.isEmpty(oldPropertylabel)) {

                    bean.setPropertyLabel("1");

                    if (bean.getCgformFieldList() != null && bean.getPropertyLabel().equals("0")) {


                        notifyItemRangeRemoved(bean.getPosition()+1,bean.getCgformFieldList().size());

                    }

                }else {

                    if (oldPropertylabel.equals("0")) {

                        bean.setPropertyLabel("1");

                        if (bean.getCgformFieldList() != null && bean.getPropertyLabel().equals("1")) {


                            notifyItemRangeInserted(bean.getPosition() + 1, bean.getCgformFieldList().size());

                        }


                    } else  {

                        bean.setPropertyLabel("0");

                        if (bean.getCgformFieldList() != null && bean.getPropertyLabel().equals("0")) {


                            notifyItemRangeRemoved(bean.getPosition() + 1, bean.getCgformFieldList().size());

                        }


                    }
                }



                notifyDataSetChanged();






            }
        });

    }


    private void vibrate(Context context) {
        Vibrator vibrator = (Vibrator)context. getSystemService(VIBRATOR_SERVICE);
        vibrator.vibrate(200);
    }





    private DongTaiFormBean getItemStatusByPosition(int position) {

        int countss=0;


        for (int i = 0; i < biaoGeBeanList.size(); i++) {


            DongTaiFormBean dongTaiFormBean = biaoGeBeanList.get(i);

            String propertyLabel = dongTaiFormBean.getPropertyLabel();

            if (propertyLabel != null && propertyLabel.equals("0")) {

                dongTaiFormBean.setPosition(countss);
                dongTaiFormBean.setGroupindex(i);
                countss++;

            } else {

                List<DongTaiFormBean> propertys = dongTaiFormBean.getCgformFieldList();

                if (propertys != null && propertys.size() > 0) {
                    dongTaiFormBean.setPosition(countss);
                    countss++;

                    for (int j = 0; j < propertys.size(); j++) {

                        DongTaiFormBean dongTaiFormBean1 = propertys.get(j);

                        dongTaiFormBean1.setGroupindex(i);
                        dongTaiFormBean1.setPosition(countss);
                        countss++;

                    }


                } else {


                    dongTaiFormBean.setPosition(countss);
                    dongTaiFormBean.setGroupindex(i);
                    countss++;

                }

            }



        }


        for (int i = 0; i < biaoGeBeanList.size(); i++) {


            DongTaiFormBean dongTaiFormBean = biaoGeBeanList.get(i);

            List<DongTaiFormBean> propertys = dongTaiFormBean.getCgformFieldList();


            String propertyLabel = dongTaiFormBean.getPropertyLabel();

            if (propertyLabel != null && propertyLabel.equals("0")) {


                if (position == dongTaiFormBean.getPosition()) {
                    return dongTaiFormBean;
                }


            }else {

                if (propertys != null && propertys.size() > 0) {

                    if (position == dongTaiFormBean.getPosition()) {
                        return dongTaiFormBean;
                    }

                    for (int j = 0; j < propertys.size(); j++) {

                        DongTaiFormBean dongTaiFormBean1 = propertys.get(j);

                        if (position == dongTaiFormBean1.getPosition()) {
                            return dongTaiFormBean1;
                        }

                    }


                } else {

                    if (position == dongTaiFormBean.getPosition()) {
                        return dongTaiFormBean;
                    }


                }
            }




        }




        return null;




    }

}
