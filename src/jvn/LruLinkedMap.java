package jvn;

import java.util.LinkedHashMap;

public class LruLinkedMap<K, V extends JvnObject> extends
		LinkedHashMap<K, V> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int max;

	public LruLinkedMap(int max) {
		super(10 * max, 1.0f, true);
		this.max = max;
	}

	@Override
	public V put(K k, V v) {
		if (cachesize() >= max) {
			System.out.println(cachesize() + " objects dans le cache");
			eldest().jvnRemoveSer();
		}
		return super.put(k, v);
	}

	public int cachesize() {
		int nb = 0;
		for (V v : this.values()) {
			nb += v.jvnIsCached() ? 1 : 0;
		}
		return nb;
	}

	public V eldest() {
		for (V v : this.values()) {
			if (v.jvnIsCached()) {
				System.out.println( v+ " décharge dans le cache");
				return v;
			}
		}
		return null;
	}

}