package com.example.alexandr.megaquiz.quizstoragefragment.view;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.alexandr.megaquiz.R;
import com.example.alexandr.megaquiz.quizstoragefragment.QuizStorageItem;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Alexandr Mikhalev on 25.09.2018.
 *
 * @author Alexandr Mikhalev
 */
public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {

    private List<QuizStorageItem> mCategoriesNames = new ArrayList<>();
    private OnItemClickListener mOnItemClickListener;

    private final static int VIEW_ITEM_FIRST = 0;
    private final static int VIEW_ITEM_SECOND = 1;

    public RecyclerAdapter(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    public List<QuizStorageItem> getData() {
        return mCategoriesNames;
    }

    public void setData(List<QuizStorageItem> categoriesNames) {
        mCategoriesNames = categoriesNames;
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        return position % 2 == 0 ? VIEW_ITEM_FIRST : VIEW_ITEM_SECOND;
    }

    @NonNull
    @Override
    public RecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycler_storage_item_test, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerAdapter.ViewHolder holder, int position) {
        holder.mTVName.setText(mCategoriesNames.get(position).getNameOfItem());
        holder.mTVPosition.setText(String.valueOf(mCategoriesNames.get(position).getPosition()));
        holder.mTVQuantityQuestionOfQuiz.setText(String.valueOf(mCategoriesNames.get(position).getCategorySize()));
        holder.mCatName = mCategoriesNames.get(position).getNameOfItem();
    }

    @Override
    public int getItemCount() {
        return mCategoriesNames.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.name_item_recycler)
        TextView mTVName;

        @BindView(R.id.position_item_recycler)
        TextView mTVPosition;

        @BindView(R.id.number_quantity_item_recycler)
        TextView mTVQuantityQuestionOfQuiz;

        public String mCatName;

        public ViewHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (checkEmptyCategory(mCatName)) {
                mOnItemClickListener.onClick(mCatName);
            } else {
                String toastMessage = "КАТЕГОРИЯ ПУСТАЯ! СКОРО БУДЕТ ДОСТУПНА!";
                Toast.makeText(view.getContext(), toastMessage, Toast.LENGTH_SHORT).show();
            }
        }

        private boolean checkEmptyCategory(String categoryName) {
            int categorySize = 0;
            for (QuizStorageItem quizStorageItem : mCategoriesNames) {
                if (quizStorageItem.getNameOfItem().equals(categoryName))
                    categorySize = quizStorageItem.getCategorySize();
            }
            return categorySize > 0;
        }
    }

    interface OnItemClickListener {
        void onClick(String key);
    }
}
