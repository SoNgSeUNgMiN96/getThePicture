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
import java.util.zip.Inflater;


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

    @Override
    public View getView(int position, View converView, ViewGroup parent) {

        //뷰홀더를 사용해서 리스트뷰 최적화하기 필요!!

        if(converView ==null) //이전에 적용된게 없다면 밑에꺼.
        {
            converView = mLayoutInflater.inflate(R.layout.listview_custom, parent, false);
        }

        View view = mLayoutInflater.inflate(R.layout.listview_custom, null);

        ImageView imageView = (ImageView)view.findViewById(R.id.poster);
        TextView movieName = (TextView)view.findViewById(R.id.movieName);
        Button delete = (Button) view.findViewById(R.id.delete_button);


        try{
            File file = new File(sample.get(position).getImg());

            if(file.exists()) {      //이미지의 경로대로 파일을 읽어들여 이미지를 세팅한다.
                imageView.setImageBitmap(BitmapFactory.decodeFile(file.getAbsolutePath()));
            }
        }

        catch (Exception e){
            e.printStackTrace();
        }

        movieName.setText(sample.get(position).getImgName());

        delete.setOnClickListener(new View.OnClickListener() {      //리스트 요소 내의 삭제 버튼
            @Override
            public void onClick(View view) {
                try {
                    File file = new File(sample.get(position).getImg());        //sampe에 넣어둔 이미지의 path를 기준으로 파일을 읽어들인다.
                    sample.remove(position);        //해당 요소를 리스트에서 삭제한다.
                    notifyDataSetChanged(); //리스트뷰 갱신
                    if(file.exists()){      //파일이 존재하면
                        file.delete();      //파일을 삭제한다.
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        return view;
    }
}