package com.hollysmart.park;

import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;

import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.hollysmart.adapter.ResDataManageAdapter;
import com.hollysmart.apis.GetNetResListAPI;
import com.hollysmart.beans.ProjectBean;
import com.hollysmart.beans.ResDataBean;
import com.hollysmart.beans.JDPicInfo;
import com.hollysmart.db.JDPicDao;
import com.hollysmart.db.ProjectDao;
import com.hollysmart.db.ResDataDao;
import com.hollysmart.db.UserInfo;
import com.hollysmart.style.StyleAnimActivity;
import com.hollysmart.utils.ACache;
import com.hollysmart.utils.Mlog;
import com.hollysmart.utils.Utils;
import com.hollysmart.value.Values;

import java.io.File;
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
		isLogin();
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





		new GetNetResListAPI(userInfo, projectBean, new GetNetResListAPI.DatadicListIF() {
			@Override
			public void datadicListResult(boolean isOk, List<ResDataBean> netDataList) {


				List<String> idList = new ArrayList<>();

				for (ResDataBean resDataBean : mJingDians) {

					idList.add(resDataBean.getId());
				}


				if (isOk) {
					if (netDataList != null && netDataList.size() > 0) {
						int j=0;

						for (int i = 0; i < netDataList.size(); i++) {

							ResDataBean resDataBean = netDataList.get(i);

							if (!idList.contains(resDataBean.getId())) {
								String fd_resposition = resDataBean.getFd_resposition();

								if (!Utils.isEmpty(fd_resposition)) {

									String[] split = fd_resposition.split(",");
									resDataBean.setLatitude(split[0]);
									resDataBean.setLongitude(split[1]);

								}


								mJingDians.add(resDataBean);

								j = j + 1;

								projectBean.setNetCount(10);
							}
						}

						new ProjectDao(mContext).addOrUpdate(projectBean);
						ProjectBean dataByID = new ProjectDao(mContext).getDataByID(projectBean.getId());

						dataByID.getNetCount();
					}
				}

//				for (int i = 0; i < mJingDians.size(); i++) {
//
//					LatLng llA = new LatLng(Double.parseDouble(mJingDians.get(i).getLatitude()),
//							Double.parseDouble(mJingDians.get(i).getLongitude()));
//					OverlayOptions ooA = new MarkerOptions().position(llA)
//							.icon(bdA).zIndex(i);
//					Marker marker = (Marker) (mBaiduMap.addOverlay(ooA));
//					mMarkers.put(i, marker);
//					int fanwei = resDatalist.get(i).getScope();
//					mainPresenter.getCoordinates(fanwei, i);
//				}

				resDataManageAdapter.notifyDataSetChanged();



			}
		}).request();














	}







	/**
	 * 判断用户登录状态，登录获取用户信息
	 */
	private UserInfo userInfo;

	public boolean isLogin() {
		if (userInfo != null)
			return true;
		try {
			String userPath = Values.SDCARD_FILE(Values.SDCARD_CACHE) + Values.CACHE_USER;
			Object obj = ACache.get(new File(userPath)).getAsObject(Values.CACHE_USERINFO);
			if (obj != null) {
				userInfo = (UserInfo) obj;
				return true;
			}
		}catch (Exception e){
			e.printStackTrace();
		}
		return false;
	}


}
