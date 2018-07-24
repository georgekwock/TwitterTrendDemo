package task.twittercodingtask.view;

import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.Collections;
import java.util.List;

import task.twittercodingtask.R;
import task.twittercodingtask.databinding.LayoutItemBinding;
import task.twittercodingtask.module.TrendBean;

/**
 * @Author Qing Guo
 * @Date 2018/7/18
 * @Description Recyclerview Adapter using DataBinding.
 */
public class MainAdapter extends RecyclerView.Adapter<MainAdapter.TypeViewHolder> {
    private List<TrendBean> mData;

    public MainAdapter() {
        this.mData = Collections.emptyList();
    }

    public MainAdapter(List<TrendBean> mData) {
        this.mData = mData;
    }

    @NonNull
    @Override
    public TypeViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutItemBinding binding = DataBindingUtil.inflate(LayoutInflater.from(viewGroup
                .getContext()), R.layout.layout_item, viewGroup, false);
        return new TypeViewHolder(binding);
    }


    @Override
    public void onBindViewHolder(@NonNull TypeViewHolder typeViewHolder, int position) {
        typeViewHolder.setTrendBean(mData.get(position));
        LayoutItemBinding binding = typeViewHolder.getBinding();
        binding.trendName.setText(mData.get(position).getName());
        if (mData.get(position).getVolume().equals("null") || mData.get(position).getVolume() ==
                null) {
            binding.volume.setText("0");
        } else {
            binding.volume.setText(mData.get(position).getVolume());
        }
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class TypeViewHolder extends RecyclerView.ViewHolder {
        final LayoutItemBinding binding;
        private TrendBean trendBean;

        public TypeViewHolder(LayoutItemBinding binding) {
            super(binding.cardView);
            this.binding = binding;
        }

        public LayoutItemBinding getBinding() {
            return binding;
        }

        public TrendBean getTrendBean() {
            return trendBean;
        }

        public void setTrendBean(TrendBean trendBean) {
            this.trendBean = trendBean;
        }
    }

    public void clearItems() {
        mData.clear();
        notifyDataSetChanged();
    }

    /*
     * method used to update the list of the data.
     * */
    public void updateList(List<TrendBean> data) {
        mData = data;
        notifyDataSetChanged();
    }
}
