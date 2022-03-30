package com.example.agrosmart.ml;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.agrosmart.R;

import java.util.List;

public class CustomAdapterWatered extends RecyclerView.Adapter<CustomAdapterWatered.MyViewHolder> {

    Context context;
    List<ModelClassWatered> mList;



    public CustomAdapterWatered(Context context, List<ModelClassWatered> mList) {
        this.context = context;
        this.mList = mList;
    }


    @NonNull
    @Override
    public CustomAdapterWatered.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.recycle_image, parent, false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomAdapterWatered.MyViewHolder holder, int position) {


        holder.textView.setText(mList.get(position).getImagename());
        Glide.with(context).load(mList.get(position).getImage()).centerCrop().placeholder(R.drawable.app_logo).into(holder.imageView);



    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView textView;
        ImageView imageView;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            textView = itemView.findViewById(R.id.filename);
            imageView=itemView.findViewById(R.id.icon);
        }
    }
}
