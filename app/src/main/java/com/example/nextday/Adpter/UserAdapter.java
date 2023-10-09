package com.example.nextday.Adpter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.example.nextday.Model.User;
import com.example.nextday.R;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    private List<User> userList;
    private List<User> favoriteList;
    private OnFavoriteClickListener onFavoriteClickListener;

    public UserAdapter(List<User> userList, List<User> favoriteList, OnFavoriteClickListener onFavoriteClickListener) {
        this.userList = userList;
        this.favoriteList = favoriteList;
        this.onFavoriteClickListener = onFavoriteClickListener;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = userList.get(position);

        holder.tvName.setText(user.getFirstName() + " " + user.getLastName());

        RequestOptions requestOptions = new RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.ALL);

        Glide.with(holder.itemView.getContext())
                .load(user.getAvatar())
                .apply(requestOptions)
                .into(holder.ivAvatar);

        if (favoriteList.contains(user)) {
            holder.ivFavorite.setImageResource(R.drawable.ic_favorite);
        } else {
            holder.ivFavorite.setImageResource(R.drawable.ic_unfavorite);
        }
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public class UserViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView tvName;
        private ImageView ivAvatar;
        private ImageView ivFavorite;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            ivAvatar = itemView.findViewById(R.id.ivAvatar);
            ivFavorite = itemView.findViewById(R.id.ivFavorite);
            ivFavorite.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                User user = userList.get(position);
                onFavoriteClickListener.onFavoriteClick(user);
            }
        }
    }

    public interface OnFavoriteClickListener {
        void onFavoriteClick(User user);
    }
}
