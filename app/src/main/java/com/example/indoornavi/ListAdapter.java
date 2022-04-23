package com.example.indoornavi;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ViewHolder> {

    private static final String TAG = "ListAdapter";
    ArrayList<ListNote> items = new ArrayList<>();

    @NonNull
    @Override
    public ListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.item_list, parent, false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ListAdapter.ViewHolder holder, int position) {
        ListNote item = items.get(position);
        holder.setItem(item);
        holder.setLayout();
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        LinearLayout layout;
        CheckBox checkBox;
        Button deleteBtn;

        public ViewHolder(View itemView) {
            super(itemView);

            layout = itemView.findViewById(R.id.layoutList);
            checkBox = itemView.findViewById(R.id.listCheckBox);
            deleteBtn = itemView.findViewById(R.id.deleteListBtn);

            deleteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String list = (String) checkBox.getText();
                    deleteItem(list);
                    Toast.makeText(v.getContext(), "삭제되었습니다.",Toast.LENGTH_SHORT).show();
                }
            private void deleteItem(String item) {        }
            });
        }
        public void setItem(ListNote item) {
            checkBox.setText(item.getShoppingList());
        }
        public void setLayout() {
            layout.setVisibility(View.VISIBLE);
        }
    }

    public void setItems(ArrayList<ListNote> items) {
        this.items = items;
    }
}
