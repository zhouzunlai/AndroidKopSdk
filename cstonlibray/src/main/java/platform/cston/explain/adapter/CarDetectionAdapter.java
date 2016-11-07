package platform.cston.explain.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;

import cston.cstonlibray.R;
import platform.cston.explain.bean.CarDetectionEntity;


/**
 * Created by daifei on 2015/7/8.
 */
public class CarDetectionAdapter extends BaseAdapter {

    private Context mContext;

    private LayoutInflater mInflater;

    private ArrayList<CarDetectionEntity> mList;

    public CarDetectionAdapter(Context context,ArrayList<CarDetectionEntity> list) {
        mContext = context;
        mList = list;
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

    public void setList(ArrayList<CarDetectionEntity> list) {
        mList = list;
        notifyDataSetChanged();
    }

    public ArrayList<CarDetectionEntity> getList() {
        return mList;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.cst_platform_car_detection_list_item, null);
        }
        ImageView image = ViewHolder.get(convertView, R.id.image);
        TextView title = ViewHolder.get(convertView, R.id.title);
        ImageView state = ViewHolder.get(convertView, R.id.state);
        ProgressBar progress = ViewHolder.get(convertView, R.id.progress);
        View line = ViewHolder.get(convertView, R.id.line);
        View header = ViewHolder.get(convertView, R.id.header);
        View footer = ViewHolder.get(convertView, R.id.footer);

        if (position == 0) {
            header.setVisibility(View.VISIBLE);
        } else {
            header.setVisibility(View.GONE);
        }
        if (position == mList.size() - 1) {
            footer.setVisibility(View.VISIBLE);
        } else {
            footer.setVisibility(View.GONE);
        }
        if (position == mList.size() - 1) {
            line.setVisibility(View.GONE);
        } else {
            line.setVisibility(View.VISIBLE);
        }

        CarDetectionEntity entity = mList.get(position);

        image.setImageResource(entity.drawable);
        title.setText(entity.title);
        switch (entity.state) {
            case CarDetectionEntity.DETECTION_IDLE: {
                state.setVisibility(View.GONE);
                progress.setVisibility(View.GONE);
                break;
            }
            case CarDetectionEntity.DETECTION_ING: {
                state.setVisibility(View.GONE);
                progress.setVisibility(View.VISIBLE);
                break;
            }
            case CarDetectionEntity.DETECTION_NORMAL: {
                state.setVisibility(View.VISIBLE);
                progress.setVisibility(View.GONE);
                state.setImageResource(R.drawable.cst_platform_icon_detection_normal);
                break;
            }
            case CarDetectionEntity.DETECTION_WARNING: {
                state.setVisibility(View.VISIBLE);
                progress.setVisibility(View.GONE);
                state.setImageResource(R.drawable.cst_platform_icon_detection_warning);
                break;
            }
            case CarDetectionEntity.DETECTION_ERROR: {
                state.setVisibility(View.VISIBLE);
                progress.setVisibility(View.GONE);
                state.setImageResource(R.drawable.cst_platform_icon_detection_error);
                break;
            }
            default: {
                state.setVisibility(View.GONE);
                progress.setVisibility(View.GONE);
            }
        }

        return convertView;
    }
}
