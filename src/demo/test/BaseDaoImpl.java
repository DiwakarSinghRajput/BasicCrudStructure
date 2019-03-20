package com.ued.news.dao.impl;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.hibernate.Session;

import com.ued.news.dao.BaseDao;

@SuppressWarnings({ "unchecked", "deprecation" })
public class BaseDaoImpl<E> implements BaseDao<E> {

	private final Class<E> entityClass;

	public Session session;

	@PersistenceContext
	private EntityManager entityManger;

	public BaseDaoImpl() {
		this.entityClass = (Class<E>) ((ParameterizedType) this.getClass().getGenericSuperclass())
				.getActualTypeArguments()[0];
	}

	public Session getSession() {
		session = this.entityManger.unwrap(Session.class);
		if (session.getTransaction().isActive()) {
			return session;
		}
		session.beginTransaction();
		return session;
	}

	@Override
	public Serializable save(E entity) {
		session = getSession();
		Serializable s = session.save(entity);
		return s;
	}

	@Override
	public void saveOrUpdate(E entity) {
		session = getSession();
		session.saveOrUpdate(entity);
	}

	@Override
	public void merge(E entity) {
		session = getSession();
		session.persist(entity);
	}

	@Override
	public boolean deleteById(Class<?> type, Serializable id) {
		session = getSession();
		Object persistentInstance = session.load(type, id);
		if (persistentInstance != null) {
			session.delete(persistentInstance);
			return true;
		}
		return false;
	}

	@Override
	public void deleteAll() {
		session = getSession();
		List<E> entities = findAll();
		for (E entity : entities) {
			session.delete(entity);
		}
	}

	@Override
	public List<E> findAll() {
		session = getSession();
		return session.createCriteria(this.entityClass).list();
	}

	@Override
	public E findById(Serializable id) {
		session = getSession();
		return session.get(this.entityClass, id);
	}

	@Override
	public void clear() {
		session.clear();
	}

	@Override
	public void flush() {
		if (session.getTransaction().isActive()) {
			session.flush();
		}
	}

	@Override
	public void close() {
		if (session.getTransaction().isActive()) {
			session.getTransaction().commit();
		}
	}

	@Override
	public void rollback() {
		if (session.getTransaction().isActive()) {
			session.getTransaction().rollback();
		}
	}

	@Override
	public void evict(E entity) {
		session = getSession();
		session.evict(entity);
	}

	@Override
	public List<E> find(List<String> Keys, List<String> Values, long fromDate, long toDate) {
		// TODO Auto-generated method stub
		return null;
	}
}