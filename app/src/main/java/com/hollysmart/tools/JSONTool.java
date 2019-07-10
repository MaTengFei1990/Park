package com.hollysmart.tools;

import android.content.Context;

import com.hollysmart.beans.JDPicInfo;
import com.hollysmart.formlib.beans.ResDataBean;
import com.hollysmart.db.JDPicDao;
import com.hollysmart.db.ResDataDao;
import com.hollysmart.utils.CCM_DateTime;
import com.hollysmart.utils.Mlog;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

public class JSONTool {
	
	private Context context;
	
	public JSONTool(Context context) {
		this.context = context;
	}
	
	private List<ResDataBean> selectDB(String projectId) {

		Mlog.d("projectId = " + projectId);
		List<ResDataBean> resDataBeans = new ArrayList<ResDataBean>();

		ResDataDao resDataDao = new ResDataDao(context);

		JDPicDao jdPicDao = new JDPicDao(context);


		List<ResDataBean> datalist = resDataDao.getData(projectId + "");


		for (ResDataBean resDataBean : datalist) {

			List<JDPicInfo> dataByJDId = jdPicDao.getDataByJDId(resDataBean.getId() + "");
			resDataBean.setJdPicInfos(dataByJDId);

		}
		return resDataBeans;
	}
	
	public boolean createJSON(String projectId, String jqName, String createTime, String localDir) {
		List<ResDataBean> data = selectDB(projectId);
		boolean bFlag = false;
		String strTmpName = jqName + new CCM_DateTime().Date_No() + ".json";
		FileOutputStream fileos = null;

		File newXmlFile = new File(localDir +"/"+strTmpName);
		try {
			if (newXmlFile.exists()) {
				bFlag = newXmlFile.delete();
			} else {
				bFlag = true;
			}
			if (bFlag) {
				if (newXmlFile.createNewFile()) {
					JSONArray units = new JSONArray();
					for (int i = 0; i < data.size(); i++) {
						JSONObject jd = new JSONObject();
						jd.put("longitude", data.get(i).getFd_resposition().split(",")[0]);
						jd.put("latitude", data.get(i).getFd_resposition().split(",")[1]);
						jd.put("scope", "");
						jd.put("unitName", data.get(i).getFd_resname());
						jd.put("createdAt", data.get(i).getCreatedAt());
						JSONArray photos =  new JSONArray();
						List<JDPicInfo> picInfos = data.get(i).getJdPicInfos();
						for (int j = 0; j < picInfos.size(); j++) {
							JSONObject pic = new JSONObject();
							pic.put("createdAt", picInfos.get(j).getCreatetime());
							pic.put("filename", picInfos.get(j).getFilename());
							photos.put(pic);
						}
						jd.put("photo", photos);
						units.put(jd);
					}
					
					JSONObject jsonObject = new JSONObject();
					jsonObject.put("siteName", jqName);
					jsonObject.put("createdAt", createTime);
					jsonObject.put("unit", units);
					fileos = new FileOutputStream(newXmlFile);
					fileos.write(jsonObject.toString().getBytes());
					fileos.flush();  
					fileos.close();  
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			bFlag = false;
		}
		return bFlag;
	}
}









