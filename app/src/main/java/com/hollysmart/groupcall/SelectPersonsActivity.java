package com.hollysmart.groupcall;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;
import android.widget.TextView;
import android.widget.Toast;

import com.gqt.bean.GrpMember;
import com.gqt.bean.PttGroup;
import com.gqt.customgroup.CustomGroupResult;
import com.gqt.customgroup.CustomGroupType;
import com.gqt.customgroup.GroupInfoItem;
import com.gqt.helper.Constant;
import com.gqt.helper.GQTHelper;
import com.hollysmart.park.R;

import java.util.ArrayList;
import java.util.List;


@TargetApi(Build.VERSION_CODES.HONEYCOMB) public class SelectPersonsActivity extends Activity implements OnClickListener, OnItemClickListener, OnQueryTextListener {
	
	private TextView mOk, mCancel;
	private SearchView mSearchView;
	private ListView mMemberList;
	private List<GroupInfoItem> mGroupMembers = new ArrayList<GroupInfoItem>();;	//�б�����Դ
	private ArrayList<String> mSelectedList = new ArrayList<String>();	//��ѡ�еĳ�Ա�б�
	private ArrayList<String> mInviteList = new ArrayList<String>();	//������ĳ�Ա�б�
	private MemberListAdapter adapter;
	private String mTempGroupName,groupNum;	//��ʱ�Խ�����
	private boolean isInvite = false;
//	private List<Integer> unableSelectPositionList = new ArrayList<Integer>(); //������Աʱ���Ѿ��������еĳ�Ա���ɱ���ѡ
	private int groupType = -1;//0�Խ��� 1��ʱ�Խ���
	private CustomGroupType type;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_select_person_list);
		Intent intent = getIntent();
		if(intent.hasExtra("custom_grp_name")){
			groupType =0;
			type = (CustomGroupType)intent.getSerializableExtra("type");
			mTempGroupName = intent.getStringExtra("custom_grp_name");
			groupNum = intent.getStringExtra("custom_grp_num");
		}else{
		if(!intent.getBooleanExtra("isInvite", false)){
			mTempGroupName = intent.getStringExtra("tempGroupName");
		} else {
			isInvite = true;
			mTempGroupName = intent.getStringExtra("tempGroupName");
			mSelectedList = intent.getStringArrayListExtra("selectedList");
		}
		}
		Log.e("jiangkai", "type "+type+"  mTempGroupName "+mTempGroupName+" groupNum "+groupNum);
		if(groupType == 0 && type == CustomGroupType.DELETE){
				PttGroup pttGroup = new PttGroup(groupNum,mTempGroupName);
				Log.e("jiangkai", "name "+ GQTHelper.getInstance().getGroupEngine().getGrpMembers(pttGroup).size());
				for(GrpMember gp: GQTHelper.getInstance().getGroupEngine().getGrpMembers(pttGroup)){
					GroupInfoItem gifi = new GroupInfoItem();
					Log.e("jiangkai", "name "+gp.getMemberName()+"  num "+gp.getMemberNum());
					gifi.setGrp_uName(gp.getMemberName());
					gifi.setGrp_uNumber(gp.getMemberNum());
					mGroupMembers.add(gifi);
				}
				 
		}else{
			mGroupMembers = getMembersInfoFromDB();
		}
		init();
		IntentFilter filter = new IntentFilter();
		filter.addAction(GroupCallActivity.CUSTOM_GROUP_ACTION_RESULT_STATE);
		registerReceiver(receiver, filter);
	}
	
	public BroadcastReceiver receiver = new BroadcastReceiver(){

		@Override
		public void onReceive(Context arg0, Intent arg1) {
			// TODO Auto-generated method stub
			if(arg1.getAction().equals(GroupCallActivity.CUSTOM_GROUP_ACTION_RESULT_STATE)){
				
				CustomGroupResult result = (CustomGroupResult)arg1.getSerializableExtra("result");
				int code = arg1.getIntExtra("code", -1);
				String groupNum = arg1.getStringExtra("groupNum");
				switch (result) {
				case CREATE_SUCCESS:
				case ADD_SUCCESS:
				case DELETE_SUCCESS:
					PttGroup pttGroup = new PttGroup(groupNum,mTempGroupName);
				    GQTHelper.getInstance().getGroupEngine().getGrpMembers(pttGroup);
				case DESTROY_SUCCESS:
				case MODIFY_SUCCESS:
				case LEAVE_SUCCESS:
			Toast.makeText(SelectPersonsActivity.this, result+" �ɹ���", Toast.LENGTH_SHORT).show();
			       finish();
					break;
				case GET_GROUP_NUMBER_LIST_TIME_OUT:
				case GET_GROUP_MEMBER_INFO_TIME_OUT:
				case REQUEST_TIME_OUT:
			Toast.makeText(SelectPersonsActivity.this, result+" ��ʱ��", Toast.LENGTH_SHORT).show();
			
					break;
				case CREATE_FAILURE:
				case LEAVE_FAILURE:
				case MODIFY_FAILURE:
				case DESTROY_FAILURE:
				case DELETE_FAILURE:
				case ADD_FAILURE:
			Toast.makeText(SelectPersonsActivity.this,  result+" ʧ�ܣ�ԭ�� :"+showFailureReason(code), Toast.LENGTH_SHORT).show();
			
					break;

				default:
					break;
				}
				
			}
		}
		
	};
	/**
	 * ��ʾ����������ʧ����Ϣ
	 * 
	 * @param context
	 * @param code
	 *            ���������
	 */
	public String showFailureReason(int code) {
		String result = "δ֪����code��"+code;
		switch (code) {
		case 450:
			result = "�öԽ����Ѵ��ڣ��޷�����";
			break;
			
		case 451:
			result = "δѡ�񴴽����Լ�";
			break;

		case 452:
			result = "�����ӳ�Ա�Ѵ���";
			break;

		case 453:
			result = "���Ǵ����ߣ��޷����иò���";
			break;
			
		case 454:
			result = "����ɾ��������";
			break;
			
		case 455:
			result = "��Ա�����ڣ��޷�ɾ��";
			break;
			
		case 456:
			result = "�����˳�����̨�����ĶԽ���";
			break;
		}
		return result;
	}
	private void init() {
		mOk = (TextView) findViewById(R.id.tv_ok);
		mCancel = (TextView) findViewById(R.id.tv_cancel);
		mOk.setOnClickListener(this);
		mCancel.setOnClickListener(this);
		mSearchView = (SearchView) findViewById(R.id.sv_search_person);
		mSearchView.setOnQueryTextListener(this);
		mMemberList = (ListView) findViewById(R.id.lv_member_list);
		adapter = new MemberListAdapter(mGroupMembers);
		mMemberList.setAdapter(adapter);
		mMemberList.setOnItemClickListener(this);
	}
	
	class MemberListAdapter extends BaseAdapter {
		
		private ViewHolder holder;
		private List<GroupInfoItem> dataList;
		private String keyword = "";
		private boolean ischecked = false;
		public MemberListAdapter(List<GroupInfoItem> dataList) {
			this.dataList = dataList;
		}
		
		public List<GroupInfoItem> getDataList() {
			return dataList;
		}
		
		@Override
		public int getCount() {
			return dataList.size();
		}

		@Override
		public Object getItem(int arg0) {
			return dataList.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			return arg0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup viewgroup) {
			if(convertView == null) {
				convertView = LayoutInflater.from(SelectPersonsActivity.this).inflate(R.layout.contact_member_item, null);
				holder = new ViewHolder();
				holder.checkBox = (CheckBox) convertView.findViewById(R.id.grp_img);
				holder.name = (TextView) convertView.findViewById(R.id.grp_uName);
				holder.number = (TextView) convertView.findViewById(R.id.grp_uNumber);
				holder.department = (TextView) convertView.findViewById(R.id.grp_uDept);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			GroupInfoItem person = dataList.get(position);
			if(person != null) {
				holder.checkBox.setTag(person.getGrp_uNumber());
				if(mInviteList.contains(person.getGrp_uNumber())){
					holder.checkBox.setChecked(true);
				}else{
					if(mSelectedList.contains(person.getGrp_uNumber())){
						holder.checkBox.setChecked(true);
						if(isInvite){
							holder.checkBox.setEnabled(false);
//							unableSelectPositionList.add(position);
						}
					} else {
							holder.checkBox.setChecked(false);
							holder.checkBox.setEnabled(true);
					}
				}
				if (person.getGrp_uName() != null
						&& person.getGrp_uName().toLowerCase().contains(keyword.toLowerCase())) {
					holder.name.setText(getHighLightText(person.getGrp_uName(), keyword));
				} else {
					holder.name.setText(person.getGrp_uName());
				}
				if (person.getGrp_uNumber() != null
						&& person.getGrp_uNumber().toLowerCase().contains(keyword.toLowerCase())) {
					holder.number.setText(getHighLightText(person.getGrp_uNumber(), keyword));
				} else {
					holder.number.setText(person.getGrp_uNumber());
				}
				if (person.getGrp_uDept() != null
						&& person.getGrp_uDept().toLowerCase().contains(keyword.toLowerCase())) {
					holder.department.setText(getHighLightText(person.getGrp_uDept(), keyword));
				} else {
					holder.department.setText(person.getGrp_uDept());
				}
				holder.checkBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
					

					@Override
					public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
						if(!isInvite){
							if(isChecked){
								if(!mSelectedList.contains(compoundButton.getTag().toString())){
									mSelectedList.add(compoundButton.getTag().toString());
								}
							} else {
								mSelectedList.remove(compoundButton.getTag().toString());
							}
							Log.i("zdx", "selectlist size : " + mSelectedList.size());
						} else {
							if(isChecked){
								if(!(mSelectedList.contains(compoundButton.getTag().toString()) || mInviteList.contains(compoundButton.getTag().toString()))){
									mInviteList.add(compoundButton.getTag().toString());
								}
							} else {
								mInviteList.remove(compoundButton.getTag().toString());
							}
							Log.i("zdx", "mInviteList size : " + mInviteList.size());
						}
					}
				});
			}
			return convertView;
		}
		
	}
	
	public CharSequence getHighLightText(String str, String keyword) {
		int index = str.toLowerCase().indexOf(keyword.toLowerCase());
		int len = keyword.length();
		Spanned temp = Html.fromHtml(str.substring(0, index)
				+ "<u><font color=#FF0000>"
				+ str.substring(index, index + len) + "</font></u>"
				+ str.substring(index + len, str.length()));
		return temp;
	}
	
	class ViewHolder {
		CheckBox checkBox;
		TextView name;
		TextView number;
		TextView department;
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.tv_ok:
			if(groupType == 0){
				StringBuilder builder = new StringBuilder();
				if(type == CustomGroupType.CREATE){
				builder.append(Constant.userName);
					if(mSelectedList.size() > 0){
						builder.append(";");
					}
				}
				for (int i = 0; i < mSelectedList.size(); i++) {
					builder.append(mSelectedList.get(i));
					if(i != mSelectedList.size() - 1){
						builder.append(";");
					}
				}
				GQTHelper.getInstance().getGroupEngine().SendCustomGroupMessage(type, mTempGroupName,groupNum,builder.toString());
			}else{
			if(!isInvite){
				if(!mSelectedList.contains(Constant.userName)){
					Log.i("zdx", "username" + Constant.userName);
					mSelectedList.add(Constant.userName);
				}
				Intent intent = new Intent();
				intent.setClass(SelectPersonsActivity.this, TempGrpCallActivity.class);
				intent.putExtra("tempGroupName", mTempGroupName);
				intent.putExtra("isCreator", true);
				intent.putStringArrayListExtra("groupMemberList", mSelectedList);
				startActivity(intent);
				
				//������ʱ�Խ�
				GQTHelper.getInstance().getGroupEngine().makeTempGrpCall(mTempGroupName, mSelectedList);
			} else {
				StringBuilder builder = new StringBuilder();
				for (int i = 0; i < mInviteList.size(); i++) {
					builder.append(mInviteList.get(i));
					if(i != mInviteList.size() - 1){
						builder.append(",");
					}
				}
				GQTHelper.getInstance().getGroupEngine().JoinTmpGrpCall(GQTHelper.getInstance().getGroupEngine().getCurGrp(), builder.toString());
				setResult(RESULT_OK, new Intent().putStringArrayListExtra("inviteMembers", mInviteList));
			}
			this.finish();
		}
			break;
		case R.id.tv_cancel:
			this.finish();
		default:
			break;
		}
	}
	

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int positon, long id) {
		List<GroupInfoItem> dataList=adapter.getDataList();
		if(!isInvite){
			CheckBox checkBox = (CheckBox) view.findViewById(R.id.grp_img);
			checkBox.toggle();
		}else if(!mSelectedList.contains(dataList.get(positon).getGrp_uNumber())){
				CheckBox checkBox = (CheckBox) view.findViewById(R.id.grp_img);
				checkBox.toggle();
		}
//		if(!unableSelectPositionList.contains(positon)){
//			CheckBox checkBox = (CheckBox) view.findViewById(R.id.grp_img);
//			checkBox.toggle();
//		}
	}
	
	/**
	 * ��ȡ����Դ
	 * @return
	 */
	private List<GroupInfoItem> getMembersInfoFromDB() {
		List<GroupInfoItem> memberList = GQTHelper.getInstance().getGroupEngine().getAllMembers();
		GroupInfoItem tempItem = null;
		for (GroupInfoItem groupInfoItem : memberList) {
			if(Constant.userName.equals(groupInfoItem.getGrp_uNumber())){
				tempItem = groupInfoItem;
				break;
			}
		}
		if(tempItem != null) {
			memberList.remove(tempItem);
		}
		return memberList;
	}

	@Override
	public boolean onQueryTextChange(String newText) {
		List<GroupInfoItem> memberList;
		if (!TextUtils.isEmpty(newText)) {
			memberList = searchListBykeyWord(newText, mGroupMembers);
		} else {
			memberList = getMembersInfoFromDB();
		}
		adapter.keyword = newText;
		adapter.dataList = memberList;
		adapter.notifyDataSetChanged();
		return false;
	}

	/**
	 * ���ݹؼ��ֲ�ѯ���
	 * 
	 * @param keyWord
	 * @param grp_list
	 * @return
	 */
	public List<GroupInfoItem> searchListBykeyWord(String keyWord, List<GroupInfoItem> grp_list) {
		List<GroupInfoItem> searchResult = new ArrayList<GroupInfoItem>();
		for (int i = 0; i < grp_list.size(); i++) {
			GroupInfoItem item = grp_list.get(i);
			String name = item.getGrp_uName().toLowerCase();
			String number = item.getGrp_uNumber();
			String dept = item.getGrp_uDept().toLowerCase();
			String key = keyWord.toLowerCase();
			if (name.contains(key) || number.contains(keyWord) || dept.contains(key)) {
				searchResult.add(item);
			}
		}
		return searchResult;
	}
	
	@Override
	public boolean onQueryTextSubmit(String arg0) {
		return false;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		isInvite = false;
	}
}
