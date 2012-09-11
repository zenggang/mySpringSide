package org.myspringside.dao.imp.jdbc.tools;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 分页对象. 包含当前页数据及分页信息.
 * 
 * @author ajax
 * @author calvin
 */
@SuppressWarnings("serial")
public class Page<T> implements Serializable {

	static private int DEFAULT_PAGE_SIZE = 20;

	/**
	 * 每页的记录数
	 */
	private int pageSize = DEFAULT_PAGE_SIZE;

	/**
	 * 当前页第丄1�7条数据在List中的位置,仄1�7�1�7姄1�7
	 */
	private long start;

	/**
	 * 当前页中存放的记彄1�7,类型丄1�7般为List
	 */
	private List<T> data;

	/**
	 * 总记录数
	 */
	private long totalCount;

	/**
	 * 构�1�7�方法，只构造空顄1�7
	 */
	public Page() {
		this(0, 0, DEFAULT_PAGE_SIZE, new ArrayList<T>());
	}

	/**
	 * 默认构�1�7�方泄1�7
	 * 
	 * @param start
	 *            本页数据在数据库中的起始位置
	 * @param totalSize
	 *            数据库中总记录条敄1�7
	 * @param pageSize
	 *            本页容量
	 * @param data
	 *            本页包含的数捄1�7
	 */
	public Page(long start, long totalSize, int pageSize, List<T> data) {
		this.pageSize = pageSize;
		this.start = start;
		this.totalCount = totalSize;
		this.data = data;
	}
	
	public Boolean getHasNext(){
		return hasNextPage();
	}
	public Boolean getHasPre(){
		return hasPreviousPage();
	}
	/**
	 * 取数据库中包含的总记录数
	 */
	public long getTotalCount() {
		return this.totalCount;
	}

	/**
	 * 取�1�7�页敄1�7
	 */
	public long getTotalPageCount() {
		if (totalCount % pageSize == 0)
			return totalCount / pageSize;
		else
			return totalCount / pageSize + 1;
	}

	/**
	 * 取每页数据容釄1�7
	 */
	public int getPageSize() {
		return pageSize;
	}

	/**
	 * 当前页中的记彄1�7
	 */
	public List<T> getResult() {
		return data;
	}
	public void setResult(List<T> res){
		this.data=res;
	}
	/**
	 * 取当前页砄1�7,页码仄1�7�1�7姄1�7
	 */
	public long getCurrentPageNo() {
		return start / pageSize + 1;
	}

	/**
	 * 是否有下丄1�7顄1�7
	 */
	public boolean hasNextPage() {
		return this.getCurrentPageNo() < this.getTotalPageCount();
	}

	/**
	 * 是否有上丄1�7顄1�7
	 */
	public boolean hasPreviousPage() {
		return this.getCurrentPageNo() > 1;
	}

	/**
	 * 获取任一页第丄1�7条数据的位置，每页条数使用默认�1�7�1�7
	 */
	protected static int getStartOfPage(int pageNo) {
		return getStartOfPage(pageNo, DEFAULT_PAGE_SIZE);
	}

	/**
	 * 获取任一页第丄1�7条数据的位置,startIndex仄1�7�1�7姄1�7
	 */
	public static int getStartOfPage(int pageNo, int pageSize) {
		return (pageNo - 1) * pageSize;
	}

	public long getStart() {
		return start;
	}

	public void setStart(long start) {
		this.start = start;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public void setTotalCount(long totalCount) {
		this.totalCount = totalCount;
	}
}
