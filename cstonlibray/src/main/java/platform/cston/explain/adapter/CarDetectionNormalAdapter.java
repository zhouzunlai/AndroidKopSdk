package platform.cston.explain.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import cston.cstonlibray.R;
import platform.cston.explain.bean.CarDetectionSubEntity;

public class CarDetectionNormalAdapter extends BaseAdapter {

    private Context mContext;

    private LayoutInflater mInflater;

    private ArrayList<CarDetectionSubEntity> mList;

    public CarDetectionNormalAdapter(Context context) {
        mContext = context;
        mInflater = LayoutInflater.from(context);
    }

    public int getCount() {
        return mList == null ? 0 : mList.size();
    }

    public Object getItem(int position) {
        return mList == null ? null : mList.get(position);
    }

    public long getItemId(int position) {
        return mList == null ? 0 : position;
    }

    public void setList(ArrayList<CarDetectionSubEntity> list) {
        mList = list;
        notifyDataSetChanged();
    }

    public ArrayList<CarDetectionSubEntity> getList() {
        return mList;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.cst_platform_car_detection_result_list_item, null);
        }
        ImageView image = ViewHolder.get(convertView, R.id.image);
        TextView title = ViewHolder.get(convertView, R.id.title);
        ImageView state = ViewHolder.get(convertView, R.id.state);
        View line = ViewHolder.get(convertView, R.id.line);

        if (position == mList.size() - 1) {
            line.setVisibility(View.GONE);
        } else {
            line.setVisibility(View.VISIBLE);
        }

        CarDetectionSubEntity entity = mList.get(position);

        image.setImageResource(entity.drawable);
        title.setText(entity.title);
        state.setImageResource(R.drawable.cst_platform_icon_detection_normal);

        return convertView;
    }
}
