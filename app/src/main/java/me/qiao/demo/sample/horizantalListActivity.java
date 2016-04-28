package me.qiao.demo.sample;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Checkable;
import android.widget.ImageView;
import android.widget.TextView;

import me.qiao.demo.R;
import me.qiao.demo.footer.PacmanLoadingView;
import me.qiao.demo.header.HorizantalRefreshHeader;
import me.qiao.wrapper.RecyclerWrapper;


public class horizantalListActivity extends AppCompatActivity implements RecyclerWrapper.onRefreshListener,
        RecyclerWrapper.onLoadMoreListener{

    RecyclerView mRecyclerView;
    PacmanLoadingView loadingView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mRecyclerView = new RecyclerView(this);
        setContentView(mRecyclerView);

        RecyclerWrapper.bindOn(mRecyclerView)
                .layoutBy(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false))
                .withRefreshHeader(new HorizantalRefreshHeader(this),this)
                .withLoadMoreFooter(loadingView = new PacmanLoadingView(this),this)
                .setAdapter(mAdapter)
                .build();
    }

    @Override
    public void onRefresh(final RecyclerWrapper.Callbaclk callback){
        mRecyclerView.postDelayed(new Runnable() {
            @Override
            public void run() {
                mItemCount=30;
                mAdapter.notifyDataSetChanged();
                callback.onComplete(null);
            }
        },2000);
    }

    @Override
    public void onLoadMore(final RecyclerWrapper.Callbaclk callbaclk) {
        loadingView.startAnim();
        mRecyclerView.postDelayed(new Runnable() {
            @Override
            public void run() {
                mItemCount+=10;
                mAdapter.notifyItemRangeInserted(mItemCount-10,10);
                callbaclk.onComplete(null);
                loadingView.stopAnim();
            }
        },3000);
    }

    private int mItemCount = 30;

    RecyclerView.Adapter mAdapter = new RecyclerView.Adapter() {

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                ImageView imageView = new ImageView(horizantalListActivity.this);
                imageView.setImageResource(R.mipmap.ic_launcher);
//                return new ImageHolder(LayoutInflater.from(ListActivity.this).inflate(R.layout.list_item_image,parent,false));
            return new RecyclerView.ViewHolder(imageView){

            };
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
//                ((ImageHolder)holder).bindData("item"+position);
        }

        @Override
        public int getItemCount() {
            return mItemCount;
        }
    };

    class ImageHolder extends RecyclerView.ViewHolder implements View.OnClickListener,Checkable {
        private ImageView imageView;
        private TextView textView;
        private boolean isChecked;
        private String name;

        public ImageHolder(View view){
            super(view);
            imageView = (ImageView)view.findViewById(R.id.image);
            textView = (TextView)view.findViewById(R.id.text);
            view.setOnClickListener(this);
        }

        public void bindData(String name){
            this.name = name;

            textView.setText(name);
        }

        @Override
        public void onClick(View v) {
            toggle();
        }

        @Override
        public void setChecked(boolean checked) {
            if(isChecked!=checked){
                isChecked = checked;
            }
        }

        @Override
        public boolean isChecked() {
            return isChecked;
        }

        @Override
        public void toggle() {
            setChecked(!isChecked);
        }
    }
}
