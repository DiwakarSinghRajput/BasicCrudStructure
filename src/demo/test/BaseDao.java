package demo.test;

import java.io.Serializable;
import java.util.List;

public interface BaseDao<E> {

	Serializable save(E entity);

	public void saveOrUpdate(E entity);

	boolean deleteById(Class<?> type, Serializable id);

	void deleteAll();

	List<E> findAll();

	E findById(Serializable id);

	List<E> find(List<String> Keys, List<String> Values, long fromDate, long toDate);

	void clear();

	void flush();

	void close();

	void rollback();

	public void merge(E entity);

	void evict(E entity);

}
