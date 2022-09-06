package z.xtreamiptv.playerv3.miscelleneious.pagination;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.OnScrollListener;

public abstract class PaginationScrollListener extends OnScrollListener {
    LinearLayoutManager layoutManager;

    public abstract int getTotalPageCount();

    public abstract boolean isLastPage();

    public abstract boolean isLoading();

    protected abstract void loadMoreItems();

    public PaginationScrollListener(LinearLayoutManager layoutManager) {
        this.layoutManager = layoutManager;
    }

    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);
        int visibleItemCount = this.layoutManager.getChildCount();
        int totalItemCount = this.layoutManager.getItemCount();
        int firstVisibleItemPosition = this.layoutManager.findFirstVisibleItemPosition();
        if (dy > 0) {
        }
        if (!isLoading() && !isLastPage() && visibleItemCount + firstVisibleItemPosition >= totalItemCount && firstVisibleItemPosition >= 0) {
            loadMoreItems();
        }
    }
}
