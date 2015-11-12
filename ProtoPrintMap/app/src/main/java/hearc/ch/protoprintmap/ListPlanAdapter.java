package hearc.ch.protoprintmap;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.zip.Inflater;

/**
 * Created by leonardo.distasio on 10.11.2015.
 */
public class ListPlanAdapter extends BaseAdapter
{
    private List<Plan> mListPlan;
    private LayoutInflater mInflater;
    private Context mContext;
    private List<ListPlanListener> mListPlanListener = new ArrayList<ListPlanListener>();

    public ListPlanAdapter(Context context, List<Plan> list)
    {
        mContext = context;
        mListPlan = list;
        mInflater = LayoutInflater.from(mContext);
    }

    public void refresh(List<Plan> list)
    {
        mListPlan.clear();
        mListPlan.addAll(list);
    }

    @Override
    public int getCount() {
        return mListPlan.size();
    }

    @Override
    public Object getItem(int position)
    {
        return mListPlan.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent)
    {
        LinearLayout linearLayout;

        if(convertView == null)
        {
            linearLayout = (LinearLayout) mInflater.inflate(R.layout.adapter_plan_layout, parent, false);
        }
        else
        {
            linearLayout = (LinearLayout) convertView;
        }

        TextView tvPlan = (TextView) linearLayout.findViewById(R.id.tvPlanName);

        tvPlan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendListener(mListPlan.get(position), position);
            }
        });

        tvPlan.setText(mListPlan.get(position).getImg());

        return linearLayout;
    }

    public void addListener(ListPlanListener aListener)
    {
        mListPlanListener.add(aListener);
    }

    private void sendListener(Plan plan, int postion)
    {
        for(ListPlanListener list : mListPlanListener)
        {
            list.OnClickPlan(plan, postion);
        }
    }
}
