package com.example.mobile_project.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobile_project.R;
import com.example.mobile_project.model.Place;

import java.util.ArrayList;
import java.util.List;

/**
 * 주변 장소 어댑터 (홈 화면용)
 * 가로 스크롤 RecyclerView에 표시
 */
public class NearbyPlaceAdapter extends RecyclerView.Adapter<NearbyPlaceAdapter.ViewHolder> {

    private List<Place> places = new ArrayList<>();
    private OnPlaceClickListener listener;

    public interface OnPlaceClickListener {
        void onPlaceClick(Place place);
    }

    public void setOnPlaceClickListener(OnPlaceClickListener listener) {
        this.listener = listener;
    }

    public void setPlaces(List<Place> places) {
        this.places = places;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_nearby_place, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Place place = places.get(position);
        holder.bind(place);
    }

    @Override
    public int getItemCount() {
        return places.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView ivCategoryIcon;
        private TextView tvPlaceName;
        private TextView tvCategory;
        private TextView tvDistance;
        private TextView tvRating;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivCategoryIcon = itemView.findViewById(R.id.iv_category_icon);
            tvPlaceName = itemView.findViewById(R.id.tv_place_name);
            tvCategory = itemView.findViewById(R.id.tv_category);
            tvDistance = itemView.findViewById(R.id.tv_distance);
            tvRating = itemView.findViewById(R.id.tv_rating);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onPlaceClick(places.get(position));
                }
            });
        }

        public void bind(Place place) {
            tvPlaceName.setText(place.getName());
            tvCategory.setText(place.getCategoryInKorean());
            tvDistance.setText(place.getFormattedDistance());
            tvRating.setText(String.valueOf(place.getRating()));

            // 카테고리별 아이콘 설정
            if ("cafe".equals(place.getCategory())) {
                ivCategoryIcon.setImageResource(android.R.drawable.ic_menu_info_details);
            } else {
                ivCategoryIcon.setImageResource(android.R.drawable.ic_menu_compass);
            }
        }
    }
}