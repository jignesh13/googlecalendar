package com.example.GoogleCalendar;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.joda.time.LocalDate;

import java.util.ArrayList;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class FirstFragment extends Fragment {
    private String title;
    private ArrayList<DayModel> dayModels;
    private RecyclerView gridView;
    private int firstday;
    private int month, year;

    // newInstance constructor for creating fragment with arguments
    public static FirstFragment newInstance(int month, int year, int page, ArrayList<DayModel> dayModels) {
        FirstFragment fragmentFirst = new FirstFragment();
        Bundle args = new Bundle();
        args.putInt("firstday", page);
        args.putInt("month", month);
        args.putInt("year", year);
        fragmentFirst.dayModels = dayModels;
        fragmentFirst.setArguments(args);
        return fragmentFirst;
    }

    // Store instance variables based on arguments passed
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firstday = getArguments().getInt("firstday");
        month = getArguments().getInt("month");
        year = getArguments().getInt("year");
    }

    // Inflate the view for the fragment based on layout XML
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fraglay, container, false);
        gridView = view.findViewById(R.id.recyclerview);
        gridView.setLayoutManager(new GridLayoutManager(getActivity(), 7));
        gridView.setAdapter(new Myadapter());
        updategrid(dayModels);
        return view;
    }


    private void updategrid(ArrayList<DayModel> dayModelArrayList) {
        this.dayModels = dayModelArrayList;
        gridView.getAdapter().notifyDataSetChanged();
    }

    public void updategrid() {
        gridView.getAdapter().notifyDataSetChanged();
    }


    class Myadapter extends RecyclerView.Adapter<Myadapter.ViewHolder> {

        @Override
        public Myadapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View view = getActivity().getLayoutInflater().inflate(R.layout.gridlay, parent, false);
            return new ViewHolder(view);

        }


        @Override
        public void onBindViewHolder(Myadapter.ViewHolder holder, int position) {


            if (position >= firstday) {
                position = position - firstday;
                DayModel dayModel = dayModels.get(position);
                boolean selected = dayModel.getDay() == MainActivity.lastdate.getDayOfMonth() && dayModel.getMonth() == MainActivity.lastdate.getMonthOfYear() && dayModel.getYear() == MainActivity.lastdate.getYear() ? true : false;

                if (dayModel.isToday()) {
                    holder.textView.setBackgroundResource(R.drawable.circle);
                    holder.textView.setTextColor(Color.WHITE);

                } else if (selected) {
                    holder.textView.setBackgroundResource(R.drawable.selectedback);
                    holder.textView.setTextColor(Color.rgb(91, 128, 231));

                } else {
                    holder.textView.setBackgroundColor(Color.TRANSPARENT);
                    holder.textView.setTextColor(Color.rgb(80, 80, 80));

                }
                holder.textView.setText(dayModels.get(position).getDay() + "");

                if (dayModel.getEventlist() && !selected) {
                    holder.eventview.setVisibility(View.VISIBLE);
                } else {
                    holder.eventview.setVisibility(View.GONE);
                }
            } else {
                holder.textView.setBackgroundColor(Color.TRANSPARENT);
                holder.textView.setText("");
                holder.eventview.setVisibility(View.GONE);
            }


        }

        @Override
        public int getItemCount() {

            return dayModels.size() + firstday;
        }

        class ViewHolder extends RecyclerView.ViewHolder {

            private TextView textView;
            private View eventview;

            public ViewHolder(View itemView) {
                super(itemView);
                textView = itemView.findViewById(R.id.textView8);
                eventview = itemView.findViewById(R.id.eventview);
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (getAdapterPosition() >= firstday) {
                            for (DayModel dayModel : dayModels) {
                                dayModel.setSelected(false);
                            }
                            DayModel dayModel = dayModels.get(getAdapterPosition() - firstday);
                            MainActivity.lastdate = new LocalDate(year, month, dayModels.get(getAdapterPosition() - firstday).getDay());

                            EventBus.getDefault().post(new MessageEvent(new LocalDate(year, month, dayModels.get(getAdapterPosition() - firstday).getDay())));
                            // dayModels.get(getAdapterPosition()-firstday).setSelected(true);
                            notifyDataSetChanged();
                        }

                    }
                });
            }
        }
    }
}
