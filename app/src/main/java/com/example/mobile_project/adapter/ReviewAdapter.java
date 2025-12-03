package com.example.mobile_project.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobile_project.R;
import com.example.mobile_project.model.Review;

import java.util.ArrayList;
import java.util.List;

/**
 * 리뷰 RecyclerView Adapter
 */
public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder> {

    private List<Review> reviewList;
    private OnItemClickListener onItemClickListener;

    public ReviewAdapter() {
        this.reviewList = new ArrayList<>();
    }

    public ReviewAdapter(List<Review> reviewList) {
        this.reviewList = reviewList != null ? reviewList : new ArrayList<>();
    }

    @NonNull
    @Override
    public ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_review_card, parent, false);
        return new ReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewViewHolder holder, int position) {
        Review review = reviewList.get(position);
        holder.bind(review);
    }

    @Override
    public int getItemCount() {
        return reviewList.size();
    }

    // 데이터 업데이트
    public void setReviewList(List<Review> reviewList) {
        this.reviewList = reviewList != null ? reviewList : new ArrayList<>();
        notifyDataSetChanged();
    }

    // 클릭 리스너 인터페이스
    public interface OnItemClickListener {
        void onItemClick(Review review, int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    // ViewHolder
    class ReviewViewHolder extends RecyclerView.ViewHolder {
        private TextView tvStudentName;
        private TextView tvCreatedAt;
        private RatingBar ratingBar;
        private TextView tvRatingValue;
        private TextView tvComment;

        public ReviewViewHolder(@NonNull View itemView) {
            super(itemView);

            tvStudentName = itemView.findViewById(R.id.tv_student_name);
            tvCreatedAt = itemView.findViewById(R.id.tv_created_at);
            ratingBar = itemView.findViewById(R.id.rating_bar);
            tvRatingValue = itemView.findViewById(R.id.tv_rating_value);
            tvComment = itemView.findViewById(R.id.tv_comment);
        }

        public void bind(Review review) {
            // TODO: 학생 이름 가져오기 (현재는 review에 없음)
            // 실제로는 ReviewDAO에서 JOIN하여 User 정보도 함께 가져와야 함
            tvStudentName.setText("학생 #" + review.getStudentId());

            // 작성 날짜
            tvCreatedAt.setText(review.getCreatedAt());

            // 평점
            ratingBar.setRating(review.getRating());
            tvRatingValue.setText(review.getRatingDisplay());

            // 리뷰 내용
            tvComment.setText(review.getComment());

            // 카드 클릭 이벤트
            itemView.setOnClickListener(v -> {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(review, getAdapterPosition());
                }
            });
        }
    }
}