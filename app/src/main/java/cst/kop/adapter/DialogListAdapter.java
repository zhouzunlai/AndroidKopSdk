package cst.kop.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;

import java.util.List;

import cst.kop.R;
import cst.kop.tools.ViewHolder;
import platform.cston.httplib.bean.CarListResult;

/**
 * Created by liuc on 2014/5/26.
 */
public class DialogListAdapter extends BaseAdapter {

    private CallBack mCallBack;

    private Context mContext;

    public interface CallBack {

        void onItemClick(int posion, CarListResult.CarInfo item);
    }


    public void setCallBack(CallBack callBack) {
        mCallBack = callBack;
    }

    private List<CarListResult.CarInfo> mItemList;

    private LayoutInflater inflater = null;

    public DialogListAdapter(Context context, List<CarListResult.CarInfo> itemList) {
        mContext = context;
        this.mItemList = itemList;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return mItemList == null ? 0 : mItemList.size();
    }

    @Override
    public CarListResult.CarInfo getItem(int position) {
        return mItemList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {

            convertView = inflater.inflate(R.layout.dialog_list_item, null);
        }

        Button button = (Button) ViewHolder.get(convertView, R.id.btn);

        final CarListResult.CarInfo item = getItem(position);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCallBack != null) {
                    mCallBack.onItemClick(position, item);
                }
            }
        });

        if (!TextUtils.isEmpty(item.plate)) {
            button.setText(item.plate);
        } else {
            button.setText("");
        }
        return convertView;
    }


}
