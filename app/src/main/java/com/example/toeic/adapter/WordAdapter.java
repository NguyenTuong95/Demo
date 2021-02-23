
package com.example.toeic.adapter;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.toeic.R;
import com.example.toeic.databinding.ItemWordBinding;
import com.example.toeic.vocabulary.EnglishWord;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

public class WordAdapter extends RecyclerView.Adapter<WordAdapter.WordViewHolder> {

    private List<EnglishWord> mWord;
    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private IOnWordItemClickListener mListener;

    public WordAdapter(Context context, List<EnglishWord> data, IOnWordItemClickListener listener){
        mContext = context;
        mWord = data;
        mLayoutInflater = LayoutInflater.from(context);
        mListener = listener;
    }

    @NonNull
    @Override
    public WordAdapter.WordViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = mLayoutInflater.inflate(R.layout.item_word, parent,false);

        final WordViewHolder wordViewHolder = new WordViewHolder(itemView);
        wordViewHolder.binding.container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onItemClick(wordViewHolder
                        .getAdapterPosition());
            }
        });

        return wordViewHolder;
    }

    @Override
    public void onBindViewHolder(WordViewHolder holder, int position) {
        EnglishWord word = mWord.get(position);
        holder.binding.tvSubject.setText(word.getSubject());
        holder.binding.tvTitle.setText(word.getWord());
        holder.binding.tvVnDescription.setText(word.getNote());
        holder.binding.tvWordType.setText(word.getType());
    }

    @Override
    public int getItemCount() {
        return mWord != null ? mWord.size() : 0;
    }


    public class WordViewHolder extends RecyclerView.ViewHolder {
        ItemWordBinding binding;

        public WordViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = DataBindingUtil.bind(itemView);

        }
    }

    public interface IOnWordItemClickListener{
        void onItemClick(int index);

    }
}
