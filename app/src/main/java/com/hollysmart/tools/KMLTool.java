package com.hollysmart.tools;

import android.content.Context;
import android.util.Xml;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.CoordinateConverter;
import com.hollysmart.beans.GPS;
import com.hollysmart.beans.LatLngToJL;
import com.hollysmart.beans.LuXianInfo;
import com.hollysmart.beans.PointInfo;
import com.hollysmart.beans.ResDataBean;
import com.hollysmart.db.LuXianDao;
import com.hollysmart.db.ResDataDao;
import com.hollysmart.utils.CCM_DateTime;
import com.hollysmart.utils.Mlog;
import com.hollysmart.utils.Utils;
import com.hollysmart.value.Values;

import org.json.JSONArray;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlSerializer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;


public class KMLTool {

	private Context context;

	public static double pi = 3.1415926535897932384626;
	public static double a = 6378245.0;
	public static double ee = 0.00669342162296594323;

	public KMLTool(Context context) {
		this.context = context;
	}

	private List<String> picList; // 当前景点图片集
	private List<String> soundList; // 当前景点录音集
	private List<PointInfo> poiInfos = new ArrayList<PointInfo>();
	private static final int BUFF_SIZE = 1024 * 1024; // 1M Byte

	private LuXianInfo luXianInfo=new LuXianInfo();


	private List<ResDataBean> selectDB(String jqId) {
		Mlog.d("jqId = " + jqId);
		List<ResDataBean> resDataBeans = new ArrayList<ResDataBean>();


		ResDataDao resDataDao = new ResDataDao(context);

		List<ResDataBean> data = resDataDao.getData(jqId + "");

		resDataBeans.addAll(data);
		return resDataBeans;
	}

	public boolean createKML(String jqId, String localDir, String strTmpName, List<PointInfo> poiInfos) {
		List<ResDataBean> data = selectDB(jqId);
		boolean bFlag = false;
		FileOutputStream fileos = null;

		File newXmlFile = new File(localDir + "/" + strTmpName);
		try {
			if (newXmlFile.exists()) {
				bFlag = newXmlFile.delete();
			} else {
				bFlag = true;
			}
			if (bFlag) {
				if (newXmlFile.createNewFile()) {
					double juli;
					double juli2;
					LatLngToJL latLngToJL = new LatLngToJL();
					for (int i = 0; i < poiInfos.size()-2; i++) {
						juli = latLngToJL.gps2String(poiInfos.get(i).getLatitude(),
								poiInfos.get(i).getLongitude(),
								poiInfos.get(i + 1).getLatitude(),
								poiInfos.get(i + 1).getLongitude());

						juli2 = latLngToJL.gps2String(poiInfos.get(i).getLatitude(),
								poiInfos.get(i).getLongitude(),
								poiInfos.get(i + 2).getLatitude(),
								poiInfos.get(i + 2).getLongitude());
						if (juli2 > juli) {
							poiInfos.remove(i + 1);
						}

					}
					for (int i = 0; i < poiInfos.size()-2; i++) {
						juli = latLngToJL.gps2String(poiInfos.get(i).getLatitude(),
								poiInfos.get(i).getLongitude(),
								poiInfos.get(i + 1).getLatitude(),
								poiInfos.get(i + 1).getLongitude());

						juli2 = latLngToJL.gps2String(poiInfos.get(i).getLatitude(),
								poiInfos.get(i).getLongitude(),
								poiInfos.get(i + 2).getLatitude(),
								poiInfos.get(i + 2).getLongitude());
						if (juli2 > juli) {
							poiInfos.remove(i + 1);
						}

					}
					for (int i = 0; i < poiInfos.size()-2; i++) {
						juli = latLngToJL.gps2String(poiInfos.get(i).getLatitude(),
								poiInfos.get(i).getLongitude(),
								poiInfos.get(i + 1).getLatitude(),
								poiInfos.get(i + 1).getLongitude());

						juli2 = latLngToJL.gps2String(poiInfos.get(i).getLatitude(),
								poiInfos.get(i).getLongitude(),
								poiInfos.get(i + 2).getLatitude(),
								poiInfos.get(i + 2).getLongitude());
						if (juli2 > juli) {
							poiInfos.remove(i + 1);
						}

					}
					fileos = new FileOutputStream(newXmlFile);
					// we create a XmlSerializer in order to write xml data
					XmlSerializer serializer = Xml.newSerializer();
					// we set the FileOutputStream as output for the serializer,
					// using UTF-8 encoding
					serializer.setOutput(fileos, "UTF-8");
					// <?xml version=”1.0″ encoding=”UTF-8″>
					// Write <?xml declaration with encoding (if encoding not
					// null) and standalone flag (if stan dalone not null)
					// This method can only be called just after setOutput.
					serializer.startDocument("UTF-8", null);

					serializer.startTag("", "kml");
					serializer.attribute(null, "xmlns", "http://earth.google.com/kml/2.1");
					serializer.startTag("", "Document");
					//路线颜色、覆盖范围颜色
					serializer.startTag("", "Style");
					serializer.attribute(null, "id", "pinStyle");
					serializer.startTag("", "LineStyle");
					serializer.startTag("", "color");
					serializer.text("FF0000FF");
					serializer.endTag("", "color");
					serializer.startTag("", "width");
					serializer.text("4");
					serializer.endTag("", "width");
					serializer.endTag("", "LineStyle");
					//覆盖范围颜色
					serializer.startTag("", "PolyStyle");
					serializer.startTag("", "color");
					serializer.text("7f00ffff");
					serializer.endTag("", "color");
					serializer.endTag("", "PolyStyle");
					serializer.startTag("", "IconStyle");
					serializer.startTag("", "Icon");
					serializer.startTag("", "href");
					serializer.text("http://maps.google.com/mapfiles/kml/pushpin/ylw-pushpin.png");
					serializer.endTag("", "href");
					serializer.endTag("", "Icon");
					serializer.endTag("", "IconStyle");
					serializer.endTag("", "Style");

					//餐饮图标
					serializer.startTag("", "Style");
					serializer.attribute(null, "id", "sh_red-pushpin");
					serializer.startTag("", "IconStyle");
					serializer.startTag("", "scale");
					serializer.text("1.3");
					serializer.endTag("", "scale");
					serializer.startTag("", "Icon");
					serializer.startTag("", "href");
					serializer.text("http://maps.google.com/mapfiles/kml/shapes/dining.png");
					serializer.endTag("", "href");
					serializer.endTag("", "Icon");
					serializer.endTag("", "IconStyle");
					serializer.startTag("", "ListStyle");
					serializer.endTag("", "ListStyle");
					serializer.endTag("", "Style");

					serializer.startTag("", "StyleMap");
					serializer.attribute(null, "id", "msn_canyin");
					serializer.startTag("", "Pair");
					serializer.startTag("", "key");
					serializer.text("normal");
					serializer.endTag("", "key");
					serializer.startTag("", "styleUrl");
					serializer.text("#sh_red-pushpin");
					serializer.endTag("", "styleUrl");
					serializer.endTag("", "Pair");
					serializer.endTag("", "StyleMap");

					//景点图标
					serializer.startTag("", "Style");
					serializer.attribute(null, "id", "sn_blue-pushpin");
					serializer.startTag("", "IconStyle");
					serializer.startTag("", "scale");
					serializer.text("1.3");
					serializer.endTag("", "scale");
					serializer.startTag("", "Icon");
					serializer.startTag("", "href");
					serializer.text("http://maps.google.com/mapfiles/kml/shapes/parks.png");
					serializer.endTag("", "href");
					serializer.endTag("", "Icon");
					serializer.endTag("", "IconStyle");
					serializer.startTag("", "ListStyle");
					serializer.endTag("", "ListStyle");
					serializer.endTag("", "Style");

					serializer.startTag("", "StyleMap");
					serializer.attribute(null, "id", "msn_jindian");
					serializer.startTag("", "Pair");
					serializer.startTag("", "key");
					serializer.text("normal");
					serializer.endTag("", "key");
					serializer.startTag("", "styleUrl");
					serializer.text("#sn_blue-pushpin");
					serializer.endTag("", "styleUrl");
					serializer.endTag("", "Pair");
					serializer.endTag("", "StyleMap");

					//购物图标
					serializer.startTag("", "Style");
					serializer.attribute(null, "id", "sh_purple-pushpin");
					serializer.startTag("", "IconStyle");
					serializer.startTag("", "scale");
					serializer.text("1.3");
					serializer.endTag("", "scale");
					serializer.startTag("", "Icon");
					serializer.startTag("", "href");
					serializer.text("http://maps.google.com/mapfiles/kml/shapes/grocery.png");
					serializer.endTag("", "href");
					serializer.endTag("", "Icon");
					serializer.endTag("", "IconStyle");
					serializer.startTag("", "ListStyle");
					serializer.endTag("", "ListStyle");
					serializer.endTag("", "Style");

					serializer.startTag("", "StyleMap");
					serializer.attribute(null, "id", "msn_gouwu");
					serializer.startTag("", "Pair");
					serializer.startTag("", "key");
					serializer.text("normal");
					serializer.endTag("", "key");
					serializer.startTag("", "styleUrl");
					serializer.text("#sh_purple-pushpin");
					serializer.endTag("", "styleUrl");
					serializer.endTag("", "Pair");
					serializer.endTag("", "StyleMap");

					//住宿
					serializer.startTag("", "Style");
					serializer.attribute(null, "id", "sn_home");
					serializer.startTag("", "IconStyle");
					serializer.startTag("", "scale");
					serializer.text("1.3");
					serializer.endTag("", "scale");
					serializer.startTag("", "Icon");
					serializer.startTag("", "href");
					serializer.text("http://maps.google.com/mapfiles/kml/shapes/homegardenbusiness.png");
					serializer.endTag("", "href");
					serializer.endTag("", "Icon");
					serializer.endTag("", "IconStyle");
					serializer.startTag("", "ListStyle");
					serializer.endTag("", "ListStyle");
					serializer.endTag("", "Style");

					serializer.startTag("", "StyleMap");
					serializer.attribute(null, "id", "msn_zhusu");
					serializer.startTag("", "Pair");
					serializer.startTag("", "key");
					serializer.text("normal");
					serializer.endTag("", "key");
					serializer.startTag("", "styleUrl");
					serializer.text("#sn_home");
					serializer.endTag("", "styleUrl");
					serializer.endTag("", "Pair");
					serializer.endTag("", "StyleMap");

					//娱乐
					serializer.startTag("", "Style");
					serializer.attribute(null, "id", "sn_movies");
					serializer.startTag("", "IconStyle");
					serializer.startTag("", "scale");
					serializer.text("1.3");
					serializer.endTag("", "scale");
					serializer.startTag("", "Icon");
					serializer.startTag("", "href");
					serializer.text("http://maps.google.com/mapfiles/kml/shapes/movies.png");
					serializer.endTag("", "href");
					serializer.endTag("", "Icon");
					serializer.endTag("", "IconStyle");
					serializer.startTag("", "ListStyle");
					serializer.endTag("", "ListStyle");
					serializer.endTag("", "Style");

					serializer.startTag("", "StyleMap");
					serializer.attribute(null, "id", "msn_yule");
					serializer.startTag("", "Pair");
					serializer.startTag("", "key");
					serializer.text("normal");
					serializer.endTag("", "key");
					serializer.startTag("", "styleUrl");
					serializer.text("#sn_movies");
					serializer.endTag("", "styleUrl");
					serializer.endTag("", "Pair");
					serializer.endTag("", "StyleMap");

					//分类文件夹
					serializer.startTag("", "Folder");
					serializer.startTag("", "name");
					serializer.text("餐饮");
					serializer.endTag("", "name");
					serializer.startTag("", "open");
					serializer.text("1");
					serializer.endTag("", "open");
					for (int i = 0; i < data.size(); i++) {
						picFileList(localDir + "/" + Values.SDCARD_PIC, data.get(i).getFd_resname());
						soundFileList(localDir + "/" + Values.SDCARD_SOUNDS, data.get(i).getFd_resname());
						if (i == 1) {
							serializer.startTag("", "Placemark");

							//添加图片
							serializer.startTag("", "name");
							serializer.text(data.get(i).getFd_resname());
							serializer.endTag("", "name");
							for (int j = 0; j < picList.size(); j++) {
								serializer.startTag("", "description");
								serializer.text("<![CDATA[<img src=" + "\"" + Values.SDCARD_PIC + "/" + picList.get(j) + "\"" + "width=" + "\"" + "250" + "\"" + "/>");
								serializer.endTag("", "description");
							}
							serializer.startTag("", "styleUrl");
							serializer.endTag("", "styleUrl");
							serializer.startTag("", "Point");
							serializer.startTag("", "coordinates");
							serializer.text(baiduTogps(data.get(i).getFd_resposition()));
							serializer.endTag("", "coordinates");
							serializer.endTag("", "Point");
							serializer.endTag("", "Placemark");


							serializer.startTag("", "Placemark");
							serializer.startTag("", "name");
							serializer.text(data.get(i).getFd_resname());
							serializer.endTag("", "name");

							serializer.startTag("", "styleUrl");
							serializer.endTag("", "styleUrl");

							serializer.startTag("", "Polygon");
							serializer.startTag("", "outerBoundaryIs");
							serializer.startTag("", "LinearRing");
							serializer.startTag("", "coordinates");
							serializer.endTag("", "coordinates");
							serializer.endTag("", "LinearRing");
							serializer.endTag("", "outerBoundaryIs");
							serializer.endTag("", "Polygon");
							serializer.endTag("", "Placemark");

						}

					}
					serializer.endTag("", "Folder");


					serializer.startTag("", "Folder");
					serializer.startTag("", "name");
					serializer.text("购物");
					serializer.endTag("", "name");
					serializer.startTag("", "open");
					serializer.text("1");
					serializer.endTag("", "open");
					for (int i = 0; i < data.size(); i++) {
						picFileList(localDir + "/" + Values.SDCARD_PIC, data.get(i).getFd_resname());
						soundFileList(localDir + "/" + Values.SDCARD_SOUNDS, data.get(i).getFd_resname());
						if (i == 4) {
							serializer.startTag("", "Placemark");

							//添加图片
							serializer.startTag("", "name");
							serializer.text(data.get(i).getFd_resname());
							serializer.endTag("", "name");
							for (int j = 0; j < picList.size(); j++) {
								serializer.startTag("", "description");
								serializer.text("<![CDATA[<img src=" + "\"" + Values.SDCARD_PIC + "/" + picList.get(j) + "\"" + "width=" + "\"" + "250" + "\"" + "/>");
								serializer.endTag("", "description");
							}
							serializer.startTag("", "styleUrl");
							serializer.endTag("", "styleUrl");
							serializer.startTag("", "Point");
							serializer.startTag("", "coordinates");
							serializer.text(baiduTogps(data.get(i).getFd_resposition()));
							serializer.endTag("", "coordinates");
							serializer.endTag("", "Point");
							serializer.endTag("", "Placemark");


							serializer.startTag("", "Placemark");
							serializer.startTag("", "name");
							serializer.text(data.get(i).getFd_resname());
							serializer.endTag("", "name");

							serializer.startTag("", "styleUrl");
							serializer.endTag("", "styleUrl");

							serializer.startTag("", "Polygon");
							serializer.startTag("", "outerBoundaryIs");
							serializer.startTag("", "LinearRing");
							serializer.startTag("", "coordinates");
							serializer.endTag("", "coordinates");
							serializer.endTag("", "LinearRing");
							serializer.endTag("", "outerBoundaryIs");
							serializer.endTag("", "Polygon");
							serializer.endTag("", "Placemark");

						}

					}
					serializer.endTag("", "Folder");

					serializer.startTag("", "Folder");
					serializer.startTag("", "name");
					serializer.text("住宿");
					serializer.endTag("", "name");
					serializer.startTag("", "open");
					serializer.text("1");
					serializer.endTag("", "open");
					for (int i = 0; i < data.size(); i++) {
						picFileList(localDir + "/" + Values.SDCARD_PIC, data.get(i).getFd_resname());
						soundFileList(localDir + "/" + Values.SDCARD_SOUNDS, data.get(i).getFd_resname());
						if (i == 2) {
							serializer.startTag("", "Placemark");

							//添加图片
							serializer.startTag("", "name");
							serializer.text(data.get(i).getFd_resname());
							serializer.endTag("", "name");
							for (int j = 0; j < picList.size(); j++) {
								serializer.startTag("", "description");
								serializer.text("<![CDATA[<img src=" + "\"" + Values.SDCARD_PIC + "/" + picList.get(j) + "\"" + "width=" + "\"" + "250" + "\"" + "/>");
								serializer.endTag("", "description");
							}
							serializer.startTag("", "styleUrl");
							serializer.endTag("", "styleUrl");
							serializer.startTag("", "Point");
							serializer.startTag("", "coordinates");
							serializer.text(baiduTogps(data.get(i).getFd_resposition()));
							serializer.endTag("", "coordinates");
							serializer.endTag("", "Point");
							serializer.endTag("", "Placemark");


							serializer.startTag("", "Placemark");
							serializer.startTag("", "name");
							serializer.text(data.get(i).getFd_resname());
							serializer.endTag("", "name");

							serializer.startTag("", "styleUrl");
							serializer.endTag("", "styleUrl");

							serializer.startTag("", "Polygon");
							serializer.startTag("", "outerBoundaryIs");
							serializer.startTag("", "LinearRing");
							serializer.startTag("", "coordinates");
							serializer.endTag("", "coordinates");
							serializer.endTag("", "LinearRing");
							serializer.endTag("", "outerBoundaryIs");
							serializer.endTag("", "Polygon");
							serializer.endTag("", "Placemark");

						}

					}
					serializer.endTag("", "Folder");

					serializer.startTag("", "Folder");
					serializer.startTag("", "name");
					serializer.text("景点");
					serializer.endTag("", "name");
					serializer.startTag("", "open");
					serializer.text("1");
					serializer.endTag("", "open");
					for (int i = 0; i < data.size(); i++) {
						picFileList(localDir + "/" + Values.SDCARD_PIC, data.get(i).getFd_resname());
						soundFileList(localDir + "/" + Values.SDCARD_SOUNDS, data.get(i).getFd_resname());
						if (i == 0) {
							serializer.startTag("", "Placemark");

							//添加图片
							serializer.startTag("", "name");
							serializer.text(data.get(i).getFd_resname());
							serializer.endTag("", "name");
							for (int j = 0; j < picList.size(); j++) {
								serializer.startTag("", "description");
								serializer.text("<![CDATA[<img src=" + "\"" + Values.SDCARD_PIC + "/" + picList.get(j) + "\"" + "width=" + "\"" + "250" + "\"" + "/>");
								serializer.endTag("", "description");
							}
							serializer.startTag("", "styleUrl");
							serializer.endTag("", "styleUrl");
							serializer.startTag("", "Point");
							serializer.startTag("", "coordinates");
							serializer.text(baiduTogps(data.get(i).getFd_resposition()));
							serializer.endTag("", "coordinates");
							serializer.endTag("", "Point");
							serializer.endTag("", "Placemark");


							serializer.startTag("", "Placemark");
							serializer.startTag("", "name");
							serializer.text(data.get(i).getFd_resname());
							serializer.endTag("", "name");

							serializer.startTag("", "styleUrl");
							serializer.endTag("", "styleUrl");

							serializer.startTag("", "Polygon");
							serializer.startTag("", "outerBoundaryIs");
							serializer.startTag("", "LinearRing");
							serializer.startTag("", "coordinates");
							serializer.text(baiduTogps(data.get(i).getFd_resposition()));
							serializer.endTag("", "coordinates");
							serializer.endTag("", "LinearRing");
							serializer.endTag("", "outerBoundaryIs");
							serializer.endTag("", "Polygon");
							serializer.endTag("", "Placemark");

						}

					}
					serializer.endTag("", "Folder");

					serializer.startTag("", "Folder");
					serializer.startTag("", "name");
					serializer.text("娱乐");
					serializer.endTag("", "name");
					serializer.startTag("", "open");
					serializer.text("1");
					serializer.endTag("", "open");
					for (int i = 0; i < data.size(); i++) {
						picFileList(localDir + "/" + Values.SDCARD_PIC, data.get(i).getFd_resname());
						soundFileList(localDir + "/" + Values.SDCARD_SOUNDS, data.get(i).getFd_resname());
						if (i == 3) {
							serializer.startTag("", "Placemark");

							//添加图片
							serializer.startTag("", "name");
							serializer.text(data.get(i).getFd_resname());
							serializer.endTag("", "name");
							for (int j = 0; j < picList.size(); j++) {
								serializer.startTag("", "description");
								serializer.text("<![CDATA[<img src=" + "\"" + Values.SDCARD_PIC + "/" + picList.get(j) + "\"" + "width=" + "\"" + "250" + "\"" + "/>");
								serializer.endTag("", "description");
							}
							serializer.startTag("", "styleUrl");
							serializer.endTag("", "styleUrl");
							serializer.startTag("", "Point");
							serializer.startTag("", "coordinates");
							serializer.text(baiduTogps(data.get(i).getFd_resposition()));
							serializer.endTag("", "coordinates");
							serializer.endTag("", "Point");
							serializer.endTag("", "Placemark");


							serializer.startTag("", "Placemark");
							serializer.startTag("", "name");
							serializer.text(data.get(i).getFd_resname());
							serializer.endTag("", "name");

							serializer.startTag("", "styleUrl");
							serializer.endTag("", "styleUrl");

							serializer.startTag("", "Polygon");
							serializer.startTag("", "outerBoundaryIs");
							serializer.startTag("", "LinearRing");
							serializer.startTag("", "coordinates");
							serializer.text(baiduTogps(data.get(i).getFd_resposition()));
							serializer.endTag("", "coordinates");
							serializer.endTag("", "LinearRing");
							serializer.endTag("", "outerBoundaryIs");
							serializer.endTag("", "Polygon");
							serializer.endTag("", "Placemark");

						}

					}
					serializer.endTag("", "Folder");

					//导出路线
					if (poiInfos != null) {
						serializer.startTag("", "Placemark");
						//路线名称
						serializer.startTag("", "name");
						serializer.text("路线");
						serializer.endTag("", "name");
						serializer.startTag("", "visibility");
						serializer.text("1");
						serializer.endTag("", "visibility");
						serializer.startTag("", "description");
						serializer.endTag("", "description");
						serializer.startTag("", "styleUrl");
						serializer.text("#pinStyle");
						serializer.endTag("", "styleUrl");
						serializer.startTag("", "LineString");
						serializer.startTag("", "tessellate");
						serializer.text("1");
						serializer.endTag("", "tessellate");
						//路线值
						serializer.startTag("", "coordinates");
						for (int j = 0; j < poiInfos.size(); j++) {
							serializer.text(baiduTogps(poiInfos.get(j).getLongitude() + "," + poiInfos.get(j).getLatitude()));
						}
						serializer.endTag("", "coordinates");
						serializer.endTag("", "LineString");
						serializer.endTag("", "Placemark");
					}

					serializer.endTag("", "Document");
					serializer.endTag("", "kml");

					serializer.endDocument();
					// write xml data into the FileOutputStream
					serializer.flush();
					// finally we close the file stream
					fileos.close();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			bFlag = false;
		}
		return bFlag;
	}

	public boolean createRouteKML(String localDir, String xmlName, List<PointInfo> poiInfos) {
		boolean bFlag = false;
		String strTmpName = xmlName + new CCM_DateTime().Date_No() + ".kml";
		FileOutputStream fileos = null;

		File newXmlFile = new File(localDir + "/" + strTmpName);
		try {
			if (newXmlFile.exists()) {
				bFlag = newXmlFile.delete();
			} else {
				bFlag = true;
			}
			if (bFlag) {
				if (newXmlFile.createNewFile()) {
					fileos = new FileOutputStream(newXmlFile);
					// we create a XmlSerializer in order to write xml data
					XmlSerializer serializer = Xml.newSerializer();
					// we set the FileOutputStream as output for the serializer,
					// using UTF-8 encoding
					serializer.setOutput(fileos, "UTF-8");
					// <?xml version=”1.0″ encoding=”UTF-8″>
					// Write <?xml declaration with encoding (if encoding not
					// null) and standalone flag (if stan dalone not null)
					// This method can only be called just after setOutput.
					serializer.startDocument("UTF-8", null);

					serializer.startTag("", "kml");
					serializer.attribute(null, "xmlns", "http://earth.google.com/kml/2.1");
					serializer.startTag("", "Document");
					//路线颜色、覆盖范围颜色
					serializer.startTag("", "Style");
					serializer.attribute(null, "id", "pinStyle");
					serializer.startTag("", "LineStyle");
					serializer.startTag("", "color");
					serializer.text("FF0000FF");
					serializer.endTag("", "color");
					serializer.startTag("", "width");
					serializer.text("4");
					serializer.endTag("", "width");
					serializer.endTag("", "LineStyle");
					//覆盖范围颜色
					serializer.startTag("", "PolyStyle");
					serializer.startTag("", "color");
					serializer.text("7f00ffff");
					serializer.endTag("", "color");
					serializer.endTag("", "PolyStyle");
					serializer.startTag("", "IconStyle");
					serializer.startTag("", "Icon");
					serializer.startTag("", "href");
					serializer.text("http://maps.google.com/mapfiles/kml/pushpin/ylw-pushpin.png");
					serializer.endTag("", "href");
					serializer.endTag("", "Icon");
					serializer.endTag("", "IconStyle");
					serializer.endTag("", "Style");

					//导出路线
					if (poiInfos != null) {
						serializer.startTag("", "Placemark");
						//路线名称
						serializer.startTag("", "name");
						serializer.text("路线");
						serializer.endTag("", "name");
						serializer.startTag("", "visibility");
						serializer.text("1");
						serializer.endTag("", "visibility");
						serializer.startTag("", "description");
						serializer.endTag("", "description");
						serializer.startTag("", "styleUrl");
						serializer.text("#pinStyle");
						serializer.endTag("", "styleUrl");
						serializer.startTag("", "LineString");
						serializer.startTag("", "tessellate");
						serializer.text("1");
						serializer.endTag("", "tessellate");
						//路线值
						serializer.startTag("", "coordinates");
						for (int j = 0; j < poiInfos.size(); j++) {
							serializer.text(baiduTogps(poiInfos.get(j).getLongitude() + "," + poiInfos.get(j).getLatitude()));
						}
						serializer.endTag("", "coordinates");
						serializer.endTag("", "LineString");
						serializer.endTag("", "Placemark");
					}

					serializer.endTag("", "Document");
					serializer.endTag("", "kml");

					serializer.endDocument();
					// write xml data into the FileOutputStream
					serializer.flush();
					// finally we close the file stream
					fileos.close();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			bFlag = false;
		}
		return bFlag;
	}


	//百度坐标转为 谷歌坐标
	private String baiduTogps(String str) {
		if (Utils.isEmpty(str)) {
			return "";
		}
		StringBuffer sourceStr = new StringBuffer();
		String[] strs = str.split(",0");
		for (int i = 0; i < strs.length; i++) {
			String[] strs2 = strs[i].split(",");
			LatLng LatLng1 = new LatLng(Double.parseDouble(strs2[1]), Double.parseDouble(strs2[0]));
			// 将GPS设备采集的原始GPS坐标转换成百度坐标
			CoordinateConverter converter = new CoordinateConverter();
			converter.from(CoordinateConverter.CoordType.GPS);
			converter.coord(LatLng1);
			LatLng LatLng2 = converter.convert();

			double lng = 2 * LatLng1.longitude - LatLng2.longitude;
			double lat = 2 * LatLng1.latitude - LatLng2.latitude;

			sourceStr.append(lng).append(",").append(lat).append(",0 ");

		}
		return sourceStr.toString();
	}



	public static GPS gcj_To_Gps84(double lat, double lon) {
		GPS gps = transform(lat, lon);
		double lontitude = lon * 2 - gps.getLon();
		double latitude = lat * 2 - gps.getLat();
		return new GPS(latitude, lontitude);
	}

	public static GPS transform(double lat, double lon) {
		if (outOfChina(lat, lon)) {
			return new GPS(lat, lon);
		}
		double dLat = transformLat(lon - 105.0, lat - 35.0);
		double dLon = transformLon(lon - 105.0, lat - 35.0);
		double radLat = lat / 180.0 * pi;
		double magic = Math.sin(radLat);
		magic = 1 - ee * magic * magic;
		double sqrtMagic = Math.sqrt(magic);
		dLat = (dLat * 180.0) / ((a * (1 - ee)) / (magic * sqrtMagic) * pi);
		dLon = (dLon * 180.0) / (a / sqrtMagic * Math.cos(radLat) * pi);
		double mgLat = lat + dLat;
		double mgLon = lon + dLon;
		return new GPS(mgLat, mgLon);
	}

	/**
	 * is or not outOfChina
	 * @param lat
	 * @param lon
	 * @return
	 */
	public static boolean outOfChina(double lat, double lon) {
		if (lon < 72.004 || lon > 137.8347)
			return true;
		if (lat < 0.8293 || lat > 55.8271)
			return true;
		return false;
	}

	public static double transformLat(double x, double y) {
		double ret = -100.0 + 2.0 * x + 3.0 * y + 0.2 * y * y + 0.1 * x * y
				+ 0.2 * Math.sqrt(Math.abs(x));
		ret += (20.0 * Math.sin(6.0 * x * pi) + 20.0 * Math.sin(2.0 * x * pi)) * 2.0 / 3.0;
		ret += (20.0 * Math.sin(y * pi) + 40.0 * Math.sin(y / 3.0 * pi)) * 2.0 / 3.0;
		ret += (160.0 * Math.sin(y / 12.0 * pi) + 320 * Math.sin(y * pi / 30.0)) * 2.0 / 3.0;
		return ret;
	}


	public static double transformLon(double x, double y) {
		double ret = 300.0 + x + 2.0 * y + 0.1 * x * x + 0.1 * x * y + 0.1
				* Math.sqrt(Math.abs(x));
		ret += (20.0 * Math.sin(6.0 * x * pi) + 20.0 * Math.sin(2.0 * x * pi)) * 2.0 / 3.0;
		ret += (20.0 * Math.sin(x * pi) + 40.0 * Math.sin(x / 3.0 * pi)) * 2.0 / 3.0;
		ret += (150.0 * Math.sin(x / 12.0 * pi) + 300.0 * Math.sin(x / 30.0
				* pi)) * 2.0 / 3.0;
		return ret;
	}



	/**
	 * 火星坐标系 (GCJ-02) 与百度坐标系 (BD-09) 的转换算法 将 GCJ-02 坐标转换成 BD-09 坐标
	 *
	 * @param gg_lat
	 * @param gg_lon
	 */
	public static GPS gcj02_To_Bd09(double gg_lat, double gg_lon) {
		double x = gg_lon, y = gg_lat;
		double z = Math.sqrt(x * x + y * y) + 0.00002 * Math.sin(y * pi);
		double theta = Math.atan2(y, x) + 0.000003 * Math.cos(x * pi);
		double bd_lon = z * Math.cos(theta) + 0.0065;
		double bd_lat = z * Math.sin(theta) + 0.006;
		return new GPS(bd_lat, bd_lon);
	}

	//递归查找的所有图片文件
	private void picFileList(String strPath, String name) {

		picList = new ArrayList<String>();
		String filename;//文件名
		String suf;//文件后缀
		File dir = new File(strPath);//文件夹dir
		File[] files = dir.listFiles();//文件夹下的所有文件或文件夹

		if (files == null)
			return;

		for (int i = 0; i < files.length; i++) {

			if (files[i].isDirectory()) {
				picFileList(files[i].getAbsolutePath(), name);//递归文件夹！！！

			} else {
				filename = files[i].getName();
				int j = filename.lastIndexOf(".");
				int k = filename.lastIndexOf("-");
				suf = filename.substring(k + 1, j);//得到文件后缀

				if (suf.equalsIgnoreCase(name))//判断是不是后缀的文件
				{
					picList.add(files[i].getName());//对于文件才把它加到list中
				}
			}

		}
	}

	//递归查找的所有音频文件
	private void soundFileList(String strPath, String name) {
		soundList = new ArrayList<String>();
		String filename;//文件名
		String suf;//文件后缀
		File dir = new File(strPath);//文件夹dir
		File[] files = dir.listFiles();//文件夹下的所有文件或文件夹

		if (files == null)
			return;

		for (int i = 0; i < files.length; i++) {

			if (files[i].isDirectory()) {
				soundFileList(files[i].getAbsolutePath(), name);//递归文件夹！！！

			} else {
				filename = files[i].getName();
				int j = filename.lastIndexOf(".");
				int k = filename.lastIndexOf("-");
				suf = filename.substring(k + 1, j);//得到文件后缀


				if (suf.equalsIgnoreCase(name))//判断是不是后缀的文件
				{
//	                    String strFileName = files[i].getAbsolutePath().toLowerCase();
					soundList.add(files[i].getName());//对于文件才把它加到list中
				}
			}

		}
	}

	//icon匹配图标
	private String iconTo(int icon) {
		String style = null;
		switch (icon) {
			case 0:
				style = "#msn_jindian";
				break;
			case 1:
				style = "#msn_canyin";
				break;
			case 2:
				style = "#msn_zhusu";
				break;
			case 3:
				style = "#msn_yule";
				break;
			case 4:
				style = "#msn_gouwu";
				break;
			default:
				break;
		}
		return style;
	}


	/**
	 * 读取SD卡中的XML文件,使用pull解析
	 *
	 * @param path  /storage/emulated/0/external_files/smart_gatherer/luxian/2019-02-26 09:25:3520190226.kml
	 */
	public void readxml(File path) {

		try {
			FileInputStream fis = new FileInputStream(path);

			// 获得pull解析器对象
			XmlPullParser parser = Xml.newPullParser();
			// 指定解析的文件和编码格式
			parser.setInput(fis, "utf-8");

			int eventType = parser.getEventType(); // 获得事件类型

			String strCoordinates = null;
			String coordinates = null;
			String gender = null;
			String age = null;
			while (eventType != XmlPullParser.END_DOCUMENT) {
				String tagName = parser.getName(); // 获得当前节点的名称

				switch (eventType) {
					case XmlPullParser.START_TAG: // 当前等于开始节点 <person>
						if ("coordinates".equals(tagName)) { // <persons>
							coordinates = parser.nextText();


							if (!Utils.isEmpty(coordinates)) {


								String[] points = coordinates.split(",0");

								if (points.length > 0) {
									for (int i=0;i<points.length;i++) {

										String point = points[i];
										if (!Utils.isEmpty(point)) {

											String[] split = point.split(",");
											if (split.length==2) {

												String s = split[0];
												String s1 = split[1];

												GPS gps = gcj02_To_Bd09(Double.parseDouble(s1), Double.parseDouble(s));
												GPS gps1 = gcj_To_Gps84(gps.getLat(), gps.getLon());


												PointInfo pointInfo = new PointInfo();
												pointInfo.setLongitude(gps1.getLon());
												pointInfo.setLatitude(gps1.getLat());
												poiInfos.add(pointInfo);
											}

										}
									}

									luXianInfo.setPointInfos(poiInfos);

									luXianInfo.setSelect(false);
									luXianInfo.setCreatetime(new CCM_DateTime().Date());

									JSONArray array = new JSONArray();
									for (int i = 0; i < poiInfos.size(); i++) {
										array.put(poiInfos.get(i).getJsonObject());
									}
									luXianInfo.setLineCoordinates(array.toString());


									new LuXianDao(context).addOrUpdate(luXianInfo);

								}




							}







						}


						break;
					case XmlPullParser.END_TAG: // </persons>
						if ("person".equals(tagName)) {
//							Log.i(TAG, "id---" + id);
//							Log.i(TAG, "name---" + name);
//							Log.i(TAG, "gender---" + gender);
//							Log.i(TAG, "age---" + age);
						}
						break;
					default:
						break;
				}
				eventType = parser.next(); // 获得下一个事件类型
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {

		}

	}

}









