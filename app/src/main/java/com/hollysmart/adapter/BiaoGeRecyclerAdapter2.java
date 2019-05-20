package com.hollysmart.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.hollysmart.beans.DictionaryBean;
import com.hollysmart.beans.DongTaiFormBean;
import com.hollysmart.beans.cgformRuleBean;
import com.hollysmart.dialog.BsSelectDialog;
import com.hollysmart.dialog.TimePickerDialog;
import com.hollysmart.park.R;
import com.hollysmart.utils.Mlog;
import com.hollysmart.utils.Utils;

import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by cai on 2017/12/5  基础表格
 */
public class BiaoGeRecyclerAdapter2 extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private List<DongTaiFormBean> biaoGeBeanList;
    private TimePickerDialog timePickerDialog;

    private HashMap<String, List<DictionaryBean>> map = new HashMap<>();

    public BiaoGeRecyclerAdapter2(Context mContext, List<DongTaiFormBean> biaoGeBeanList) {
        this.mContext = mContext;
        mLayoutInflater = LayoutInflater.from(mContext);
        this.biaoGeBeanList = biaoGeBeanList;
    }


    public void setMap(HashMap<String, List<DictionaryBean>> map) {
        this.map = map;
    }

    private static int VIEWTYPE_DANHANG = 0;
    private static int VIEWTYPE_DANHANG_LIST = 1;
    private static int VIEWTYPE_DANHANG_TIME_SELECT = 2;

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
     */
    @Override
    public int getItemViewType(int position) {
        DongTaiFormBean bean = biaoGeBeanList.get(position);

        if (bean.getShowType() .equals("text") ) {
            return VIEWTYPE_DANHANG;
        }
        if (bean.getShowType() .equals("datetime") ) {

            return VIEWTYPE_DANHANG_TIME_SELECT;
        }
        if (bean.getShowType().equals("list") ) {

            return VIEWTYPE_DANHANG_LIST;
        }

        return 0;
    }


    @Override
    public int getItemCount() {
        return biaoGeBeanList.size();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEWTYPE_DANHANG) {
            return new DanhangViewHolder(mLayoutInflater.inflate(R.layout.item_biaoge_danhang, parent, false));
        } else if (viewType == VIEWTYPE_DANHANG_TIME_SELECT) {
            return new DanhangXuanZeViewHolder(mLayoutInflater.inflate(R.layout.item_biaoge_danhang_xuanze, parent, false));
        } else if (viewType == VIEWTYPE_DANHANG_LIST) {
            return new DanhangXuanZelistViewHolder(mLayoutInflater.inflate(R.layout.item_biaoge_danhang_list, parent, false));
        }

        return null;
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        DongTaiFormBean bean = biaoGeBeanList.get(position);

        if (holder instanceof DanhangViewHolder) {
            danhang((DanhangViewHolder) holder, bean);
        } else if (holder instanceof DanhangParentViewHolder) {
            danhangParent((DanhangParentViewHolder) holder, bean);
        } else if (holder instanceof DanhangXuanZeViewHolder) {
            danhangXuanze((DanhangXuanZeViewHolder) holder, bean);
        } else if (holder instanceof DanhangXuanZelistViewHolder) {
            danhangXuanzelist((DanhangXuanZelistViewHolder) holder, bean);
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
                biaoGeBeanList.get(getLayoutPosition()).setPropertyLabel(s.toString());
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

        public DanhangXuanZelistViewHolder(View itemView) {
            super(itemView);
            tv_bitian =  itemView.findViewById(R.id.tv_bitian);
            tv_name =  itemView.findViewById(R.id.tv_name);
            tv_tishi =  itemView.findViewById(R.id.tv_tishi);
            tv_value =  itemView.findViewById(R.id.tv_value);
        }
    }

    private void danhangXuanzelist(final DanhangXuanZelistViewHolder holder, final DongTaiFormBean bean) {
        if (bean.getFieldMustInput()) {
            holder.tv_bitian.setVisibility(View.VISIBLE);
        } else {
            holder.tv_bitian.setVisibility(View.GONE);
        }

        if (bean.getPropertyLabel() != null) {
            List<DictionaryBean> dictionaryBeans = map.get(bean.getDictText());

            for (DictionaryBean dictionaryBean : dictionaryBeans) {
                if (dictionaryBean.getValue().equals(bean.getPropertyLabel())) {
                    holder.tv_value.setText(dictionaryBean.getLabel());
                }
            }
        } else {
            holder.tv_value.setText("");
        }

        if (bean.isShowTiShi()) {
            holder.tv_tishi.setVisibility(View.VISIBLE);
        } else {
            holder.tv_tishi.setVisibility(View.GONE);
        }


        holder.tv_name.setText(bean.getContent());
        holder.tv_value.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final List<DictionaryBean> dictionaryBeans = map.get(bean.getDictText());
                if (dictionaryBeans != null && dictionaryBeans.size() > 0) {
                    new BsSelectDialog(new BsSelectDialog.BsSelectIF() {
                        @Override
                        public void onBsSelect(int type, int index) {
                            DictionaryBean dictionaryBean = dictionaryBeans.get(index);
                            holder.tv_value.setText(dictionaryBean.getLabel());

                            bean.setPropertyLabel(dictionaryBean.getValue());
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

        public DanhangXuanZeViewHolder(View itemView) {
            super(itemView);
            tv_bitian =  itemView.findViewById(R.id.tv_bitian);
            tv_name =  itemView.findViewById(R.id.tv_name);
            tv_value =  itemView.findViewById(R.id.tv_value);
            tv_tishi =  itemView.findViewById(R.id.tv_tishi);
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



}
