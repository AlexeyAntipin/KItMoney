package com.example.moneymanager.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.moneymanager.R;
import com.example.moneymanager.model.Category;
import java.util.List;

public class FragmentMainCategoryAdapter extends RecyclerView.Adapter<FragmentMainCategoryAdapter.ViewHolder> {

    private LayoutInflater inflater;
    private FragmentManager fm;
    private Context context;
    private List<Category> categories;
    private int[] colors;
    private boolean isEven = true;

    public FragmentMainCategoryAdapter(Context context, LayoutInflater inflater,
                               List<Category> categories, FragmentManager fm,
                                       int[] colors) throws InterruptedException {
        this.context = context;
        this.inflater = inflater;
        this.categories = categories;
        this.fm = fm;
        this.colors = colors;
    }

    @NonNull
    @Override
    public FragmentMainCategoryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.fragment_main_category_item, parent, false);
        return new FragmentMainCategoryAdapter.ViewHolder(view);
    }

    @SuppressLint("ResourceType")
    @Override
    public void onBindViewHolder(@NonNull FragmentMainCategoryAdapter.ViewHolder holder, int position) {
        if (position == getItemCount() - 1) {
            position *= 2;
            if (isEven) {
                holder.category1.setText(categories.get(position).title);
                holder.category2.setText(categories.get(position + 1).title);
                holder.color1.setBackgroundColor(colors[position]);
                holder.color2.setBackgroundColor(colors[position + 1]);
            } else {
                holder.category1.setText(categories.get(position).title);
                holder.color1.setBackgroundColor(colors[position]);
            }
        } else {
            position *= 2;
            holder.category1.setText(categories.get(position).title);
            holder.category2.setText(categories.get(position + 1).title);
            holder.color1.setBackgroundColor(colors[position]);
            holder.color2.setBackgroundColor(colors[position + 1]);
        }
    }

    @Override
    public int getItemCount() {
        if (categories.size() % 2 == 0) {
            isEven = true;
            return categories.size() / 2;
        }
        else {
            isEven = false;
            return (categories.size() / 2) + 1;
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        final TextView category1;
        final TextView category2;
        final ImageView color1;
        final ImageView color2;

        ViewHolder(View view) {
            super(view);
            category1 = view.findViewById(R.id.category_name1);
            category2 = view.findViewById(R.id.category_name2);
            color1 = view.findViewById(R.id.color1);
            color2 = view.findViewById(R.id.color2);
        }
    }
}
