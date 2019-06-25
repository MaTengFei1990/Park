package com.hollysmart.park;

import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;

import com.hollysmart.adapter.ResDataManageAdapter;
import com.hollysmart.beans.JDPicInfo;
import com.hollysmart.beans.ProjectBean;
import com.hollysmart.beans.ResDataBean;
import com.hollysmart.db.JDPicDao;
import com.hollysmart.db.ResDataDao;
import com.hollysmart.style.StyleAnimActivity;
import com.hollysmart.utils.Mlog;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


/***
 * 资源管理界面
 */

public class ResDataManageActivity extends StyleAnimActivity {
	@Override
	public int layoutResID() {
		return R.layout.activity_jingdian;
	}

	@BindView(R.id.ib_back)
	ImageView ib_back;

	@BindView(R.id.ib_add)
	ImageView ib_add;

	@BindView(R.id.lv_jingdian)
	ListView lv_jingdian;


	private List<JDPicInfo> picList; // 当前景点图片集
	private List<String> soundList; // 当前景点录音集

	private ProjectBean projectBean;

	@Override
	public void findView() {
		ButterKnife.bind(this);
		ib_back.setOnClickListener(this);
		ib_add.setOnClickListener(this);
	}

	private List<ResDataBean> mJingDians;
	private ResDataManageAdapter resDataManageAdapter;

	
	@Override
	public void init() {
		picList=new ArrayList<>();
		soundList=new ArrayList<>();
		mJingDians = new ArrayList<>();
		projectBean = (ProjectBean) getIntent().getSerializableExtra("projectBean");

		resDataManageAdapter = new ResDataManageAdapter(mContext, mJingDians, picList, soundList, projectBean);
		lv_jingdian.setAdapter(resDataManageAdapter);
		selectDB(projectBean.getId());
	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ib_back:
			finish();
			break;
		case R.id.ib_add:
			setResult(2); // 2代表新增
			finish();
			break;
		}
	}


	// 查询
	private void selectDB(String jqId) {
		Mlog.d("jqId = " + jqId);
		mJingDians.clear();

		ResDataDao resDataDao = new ResDataDao(getApplication());
		List<ResDataBean> resDataBeans = resDataDao.getData(jqId + "");
		if (resDataBeans != null && resDataBeans.size() > 0) {

			mJingDians.addAll(resDataBeans);
		}


		JDPicDao jdPicDao = new JDPicDao(mContext);
		for(int i=0;i<mJingDians.size();i++) {
			ResDataBean resDataBean = mJingDians.get(i);

			List<JDPicInfo> jdPicInfoList = jdPicDao.getDataByJDId(resDataBean.getId() + "");

			resDataBean.setJdPicInfos(jdPicInfoList);

		}

		resDataManageAdapter.notifyDataSetChanged();


	}


}
