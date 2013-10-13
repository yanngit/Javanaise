package jvn;

import java.util.LinkedHashMap;

public class LruLinkedMap<K, V extends JvnObject> extends
		LinkedHashMap<K, V> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int max;

	/**
	 * Default constructor of the LRU list
	 * @param max
	 */
	public LruLinkedMap(int max) {
		super(10 * max, 1.0f, true);
		this.max = max;
	}

	@Override
	/**
	 * Add an element at the top of the list
	 */
	public V put(K k, V v) {
		if (cachesize() >= max) {
			eldest().jvnUnloadObject();
		}
		return super.put(k, v);
	}

	/**
	 * Return the number of the shared objects in the list
	 * 
	 * @return nb
	 * 			  the number of objects in the list
	 */
	public int cachesize() {
		int nb = 0;
		for (V v : this.values()) {
			nb += v.jvnIsCached() ? 1 : 0;
		}
		return nb;
	}

	/**
	 * Return the last element of the list
	 * 
	 * @return last element of the list
	 */
	public V eldest() {
		for (V v : this.values()) {
			if (v.jvnIsCached()) {
				return v;
			}
		}
		return null;
	}

}