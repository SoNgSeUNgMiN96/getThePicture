package org.tensorflow.lite.examples.detection;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

public class MyAdapter extends BaseAdapter {

    Context mContext = null;
    LayoutInflater mLayoutInflater = null;
    ArrayList<SampleData> sample;

    public MyAdapter(Context context, ArrayList<SampleData> data) {
        mContext = context;
        sample = data;
        mLayoutInflater = LayoutInflater.from(mContext);
    }

    @Override
    public int getCount() {
        return sample.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public SampleData getItem(int position) {
        return sample.get(position);
    }

    public SampleData removeItem(int position) {
        return sample.remove(position);
    }

    @Override
    public View getView(int position, View converView, ViewGroup parent) {
        View view = mLayoutInflater.inflate(R.layout.listview_custom, null);

        ImageView imageView = (ImageView)view.findViewById(R.id.poster);
        TextView movieName = (TextView)view.findViewById(R.id.movieName);
        Button delete = (Button) view.findViewById(R.id.delete_button);


        try{
            File file = new File(sample.get(position).getImg());

            if(file.exists()){
                imageView.setImageBitmap(BitmapFactory.decodeFile(file.getAbsolutePath()));
            }

        }
        catch (Exception e){
            e.printStackTrace();
        }

        movieName.setText(sample.get(position).getImgName());

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                File file = new File(sample.get(position).getImg());
                sample.remove(position);
                notifyDataSetChanged();
                if(file.exists()){
                    file.delete();
                }


            }


        });

        return view;
    }
}