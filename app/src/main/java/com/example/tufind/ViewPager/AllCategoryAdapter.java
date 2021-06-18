package com.example.tufind.ViewPager;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tufind.R;
import com.squareup.picasso.Picasso;

//import com.bumptech.glide.Glide;

//AllCardsAdapter
public class AllCategoryAdapter extends RecyclerView.ViewHolder {
    View aview;

    public AllCategoryAdapter(@NonNull View itemView) {
        super(itemView);
        aview = itemView;

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
    public void setDetails(Context ctx,String image,String title,String diatance){
        ImageView mImage = aview.findViewById(R.id.rImage);
        TextView mTiltle  = aview.findViewById(R.id.rTitleTv);
        TextView mdes = aview.findViewById(R.id.rdes);


        mTiltle.setText(title);
        mdes.setText(diatance);

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
