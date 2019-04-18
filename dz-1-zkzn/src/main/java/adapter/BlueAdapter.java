package adapter;

/**
 * Created by user on 2019/3/20.
 */

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.zkzn.dz_1.R;

import java.util.ArrayList;

import model.BlueInfo;

import static android.R.id.list;

public class BlueAdapter extends RecyclerView.Adapter<BlueAdapter.MyViewHolder>
        {
private ArrayList<BlueInfo> list = null;

public BlueAdapter(ArrayList<BlueInfo> list){
        this.list = list;
        }

public interface OnItemClickLitener
{
    void onItemClick(View view, int position);
    //void onItemLongClick(View view , int position);
}

    private OnItemClickLitener mOnItemClickLitener;

    public void setOnItemClickLitener(OnItemClickLitener mOnItemClickLitener)
    {
        this.mOnItemClickLitener = mOnItemClickLitener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.blue_item,parent,false);
        MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }


    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position)
    {
        holder.tv_name.setText(list.get(position).getName());
        holder.tv_address.setText(list.get(position).getAddress());
        holder.btn_connect.setText(list.get(position).getState());
        // 如果设置了回调，则设置点击事件
        if (mOnItemClickLitener != null)
        {
            holder.btn_connect.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    int pos = holder.getLayoutPosition();
                    mOnItemClickLitener.onItemClick(holder.btn_connect, pos);
                }
            });
        }
    }

    @Override
    public int getItemCount()
    {
        return list.size();
    }

class MyViewHolder extends RecyclerView.ViewHolder
{

    TextView tv_name;
    TextView tv_address;
    Button btn_connect;


    public MyViewHolder(View view)
    {
        super(view);
        tv_name = (TextView) view.findViewById(R.id.name);
        tv_address = (TextView)view.findViewById(R.id.address);
        btn_connect = (Button)view.findViewById(R.id.connect);

    }
}
}
