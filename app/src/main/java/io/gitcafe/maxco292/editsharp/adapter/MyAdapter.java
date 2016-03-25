package io.gitcafe.maxco292.editsharp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;



import java.util.List;

import io.gitcafe.maxco292.editsharp.R;

/**
 * ListView适配器
 * 
 * @author zihao
 * 
 */
public class MyAdapter extends BaseAdapter {

	private List<String> mTitleArray;// 标题列表
	private LayoutInflater inflater = null;
	private Context mContext;

	/**
	 * Adapter构造方法
	 * 
	 * @param titleArray
	 */
	public MyAdapter(Context context, List<String> titleArray) {
		// TODO Auto-generated constructor stub
		this.mTitleArray = titleArray;
		this.mContext = context;
		inflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	/**
	 * 获取总数
	 */
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mTitleArray.size();
	}

	/**
	 * 获取Item内容
	 */
	@Override
	public String getItem(int position) {
		// TODO Auto-generated method stub
		return mTitleArray.get(position);
	}

	/**
	 * 获取Item的ID
	 */
	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub

		ViewHolder holder;

		if (convertView == null) {
			holder = new ViewHolder();
			convertView = inflater.inflate(R.layout.list_item_layout, null);
			holder.titleTv = (TextView) convertView.findViewById(R.id.title_tv);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		// 设置
		holder.titleTv.setText(mTitleArray.get(position));

		return convertView;
	}

	static class ViewHolder {
		TextView titleTv;
	}

	/**
	 * 刷新数据
	 * 
	 * @param array
	 */
	public void refreshData(List<String> array) {
		this.mTitleArray = array;
		notifyDataSetChanged();
	}

}