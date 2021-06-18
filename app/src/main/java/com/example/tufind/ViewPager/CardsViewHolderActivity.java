package com.example.tufind.ViewPager;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tufind.R;
import com.squareup.picasso.Picasso;

public class CardsViewHolderActivity extends RecyclerView.ViewHolder {
    View mview;

    public CardsViewHolderActivity(@NonNull View itemView) {
        super(itemView);
        mview = itemView;

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mClickListner.onItemClick(v,getAdapterPosition());
            }
        });

        itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                mClickListner.onItemLongCivk(v,getAdapterPosition());
                return true;
            }
        });
    }
    public void setDetails(Context ctx, String title, String image,String diatance){
        TextView mTiltle  = mview.findViewById(R.id.rTitleTv);
        ImageView mImage = mview.findViewById(R.id.rImage);
        TextView mdistan  = mview.findViewById(R.id.rdes);

        mTiltle.setText(title);
        mdistan.setText(diatance);

        Picasso.get().load(image).into(mImage);
    }

    private ClickListner mClickListner;

    public interface ClickListner{
        void onItemClick(View view, int position);
        void onItemLongCivk(View view, int position);
    }

    public void setOnClickLickListener(ClickListner clickListner){
        mClickListner = clickListner;
    }
}
