package com.example.projectmodule.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projectmodule.R;
import com.example.projectmodule.data.model.Category;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {

	public interface OnCategoryClickListener {
		void onCategoryClick(Category category);
	}

	private final OnCategoryClickListener listener;
	private final List<Category> categories = new ArrayList<>();

	public CategoryAdapter(OnCategoryClickListener listener) {
		this.listener = listener;
	}

	public void submitCategories(List<Category> newCategories) {
		categories.clear();
		if (newCategories != null) {
			categories.addAll(newCategories);
		}
		notifyDataSetChanged();
	}

	@NonNull
	@Override
	public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category, parent, false);
		return new CategoryViewHolder(view);
	}

	@Override
	public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
		holder.bind(categories.get(position), listener);
	}

	@Override
	public int getItemCount() {
		return categories.size();
	}

	public static class CategoryViewHolder extends RecyclerView.ViewHolder {

		private final MaterialCardView cardView;
		private final ImageView categoryImage;
		private final TextView categoryTitle;

		CategoryViewHolder(@NonNull View itemView) {
			super(itemView);
			cardView = itemView.findViewById(R.id.categoryCard);
			categoryImage = itemView.findViewById(R.id.categoryImage);
			categoryTitle = itemView.findViewById(R.id.categoryTitle);
		}

		void bind(final Category category, final OnCategoryClickListener listener) {
			categoryTitle.setText(category.getTitle());
			categoryImage.setImageResource(category.getImageResId());

			cardView.setOnClickListener(v -> {
				animateClick(v);
				if (listener != null) {
					listener.onCategoryClick(category);
				}
			});
		}

		private void animateClick(View view) {
			view.animate()
					.scaleX(0.95f)
					.scaleY(0.95f)
					.setDuration(80)
					.withEndAction(() -> view.animate()
							.scaleX(1f)
							.scaleY(1f)
							.setDuration(80)
							.start())
					.start();
		}
	}
}
