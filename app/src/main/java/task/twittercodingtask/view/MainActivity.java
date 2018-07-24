package task.twittercodingtask.view;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.List;

import task.twittercodingtask.R;
import task.twittercodingtask.databinding.ActivityMainBinding;
import task.twittercodingtask.module.TrendBean;
import task.twittercodingtask.viewmodel.MainViewModel;

public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout
        .OnRefreshListener, MainViewModel.TwitterListener {
    private ActivityMainBinding binding;
    private MainViewModel viewModel;
    private MainAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        viewModel = new MainViewModel(this, this);
        binding.setMainViewModel(viewModel);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void setupRecyclerView(RecyclerView recyclerView, List<TrendBean> data) {
        adapter = new MainAdapter(data);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });
    }

    @Override
    public void onRefresh() {
        viewModel.onResume();
    }

    @Override
    public void onUpdate(List<TrendBean> data) {
        if (adapter != null) {
            adapter.updateList(data);
        }
    }

    @Override
    public void onInit(List<TrendBean> data) {
        setupRecyclerView(binding.recyclerView, data);
    }

    @Override
    public void onError(String header, String message) {
        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle(header);
        alertDialog.setMessage(message);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, getString(R.string.xml_confirm),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }
}
