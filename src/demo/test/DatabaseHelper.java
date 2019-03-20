package demo.test;

public class DatabaseHelper {

	private String search = "";
	private int currentPage = 0;
	private int itemPerPage = 0;
	private String sortBy;
	private String sortOrder;

	public DatabaseHelper(String search, int currentPage, int itemPerPage, String sortBy, String sortOrder) {
		this.search = search;
		this.currentPage = currentPage;
		this.itemPerPage = itemPerPage;
		this.sortBy = sortBy;
		this.sortOrder = sortOrder;

	}

	// Copy Constructor for Pagination
	public DatabaseHelper(DatabaseHelper databaseHelper) {
		this.search = databaseHelper.search;
		this.currentPage = 0;
		this.itemPerPage = 0;
		this.sortBy = databaseHelper.sortBy;
		this.sortOrder = databaseHelper.sortOrder;
	}

	public String getSearch() {
		return search;
	}

	public void setSearch(String search) {
		this.search = search;
	}

	public int getCurrentPage() {
		return currentPage;
	}

	public void setCurrentPage(int currentPage) {
		this.currentPage = currentPage;
	}

	public int getItemPerPage() {
		return itemPerPage;
	}

	public void setItemPerPage(int itemPerPage) {
		this.itemPerPage = itemPerPage;
	}

	public String getSortBy() {
		return sortBy;
	}

	public void setSortBy(String sortBy) {
		this.sortBy = sortBy;
	}

	public String getSortOrder() {
		return sortOrder;
	}

	public void setSortOrder(String sortOrder) {
		this.sortOrder = sortOrder;
	}

	@Override
	public String toString() {
		return "DatabaseHelper [search=" + search + ", currentPage=" + currentPage + ", itemPerPage=" + itemPerPage
				+ ", sortBy=" + sortBy + ", sortOrder=" + sortOrder + "]";
	}

}