package cst.kop.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import cst.kop.R;
import cst.kop.beans.NativeBeans;

/**
 * Created by zhou-pc on 2016/9/12.
 */
public class RecyclerViewListAdapter extends RecyclerView.Adapter<RecyclerViewListAdapter.MyViewHolder> {

    private Context mContext;
    private List<NativeBeans> mNativeList;

    public RecyclerViewListAdapter(Context context, List<NativeBeans> nativeList) {
        mContext = context;
        mNativeList = nativeList;
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        MyViewHolder holder = new MyViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_recycler, parent,
                false));
        return holder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        holder.title.setText(mNativeList.get(position).title);
        holder.content.setText(mNativeList.get(position).content);
        // 如果设置了回调，则设置点击事件
        if (mOnItemClickLitener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = holder.getLayoutPosition();
                    mOnItemClickLitener.onItemClick(holder.itemView, pos);
                }
            });
        }

    }

    @Override
    public int getItemCount() {
        return mNativeList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView title;
        TextView content;

        public MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.item_recycler_title);
            content = (TextView) view.findViewById(R.id.item_recycler_content);
        }
    }

    public interface OnItemClickLitener {
        void onItemClick(View view, int position);
    }

    private OnItemClickLitener mOnItemClickLitener;

    public void setOnItemClickLitener(OnItemClickLitener mOnItemClickLitener) {
        this.mOnItemClickLitener = mOnItemClickLitener;
    }
}
