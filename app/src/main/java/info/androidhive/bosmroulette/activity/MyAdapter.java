package info.androidhive.bosmroulette.activity;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Collections;
import java.util.List;

import info.androidhive.bosmroulette.R;

/**
 * Created by the master mind Mr.Shivam Gupta on 04/09/2015.
 */
public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {

    //create class information contain all info
    List<Information> data = Collections.emptyList();
    private LayoutInflater inflater;

    public MyAdapter(Context context,List<Information> data) {
        inflater = LayoutInflater.from(context);
        this.data = data;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView match;
        TextView teams;
        TextView circle;
        TextView date;
        ImageView star;
        public ViewHolder(View itemView) {
            super(itemView);
            match = (TextView)itemView.findViewById(R.id.match_name);
            teams = (TextView)itemView.findViewById(R.id.teams);
            date = (TextView)itemView.findViewById(R.id.date);
            circle = (TextView)itemView.findViewById(R.id.circletv);
            star = (ImageView)itemView.findViewById(R.id.star);
            //icon = (ImageView)itemView.findViewById(R.id.parentimage);

        }
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent,int viewType)
    {
        View view;

        view = inflater.inflate(R.layout.recycler_view,parent,false);

        Log.d("VIVZ","onCreateHolder called ");
        ViewHolder vh = new ViewHolder(view);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Information current = data.get(position);
        Log.d("VIVZ", "onBindViewHolder called " + position + current.star);
        holder.match.setText(current.match);
        holder.teams.setText(current.teams);
        holder.date.setText(current.date);
        holder.circle.setText(current.Text);
        if(!current.mainActivity)
            holder.circle.setVisibility(View.GONE);
        else holder.circle.setVisibility(View.VISIBLE);
        if(!current.star)
            holder.star.setVisibility(View.GONE);
        else holder.star.setVisibility(View.VISIBLE);
        if(current.Text.equals("F"))
        {
            holder.circle.setBackgroundResource(R.drawable.bg_red);
        }
        else if(current.Text.equals("C"))
        {
            holder.circle.setBackgroundResource(R.drawable.bg_blue);
        }
        else if(current.Text.equals("B") && current.match.charAt(2)=='s')
        {
            holder.circle.setBackgroundResource(R.drawable.bg_yellow);
        }
        else if(current.Text.equals("V"))
        {
            holder.circle.setBackgroundResource(R.drawable.bg_teal);
        }
        else if(current.Text.equals("L"))
        {
            holder.circle.setBackgroundResource(R.drawable.bg_purple);
        }
        else if(current.Text.equals("B"))
        {
            holder.circle.setBackgroundResource(R.drawable.bg_grey);
        }
        else if(current.Text.equals("H"))
        {
            holder.circle.setBackgroundResource(R.drawable.bg_green);
        }
        else
        {
            holder.circle.setBackgroundResource(R.drawable.bg_green);
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return data.size();
    }
}

