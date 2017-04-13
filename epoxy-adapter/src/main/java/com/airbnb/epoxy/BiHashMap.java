package com.airbnb.epoxy;

import android.support.annotation.NonNull;

import java.util.AbstractCollection;
import java.util.AbstractMap;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;

/**
 * simple bimap implementation using two maps
 *
 * @param <K> key type
 * @param <V> value type
 */
class BiHashMap<K, V> extends AbstractMap<K, V> {
  private Map<K, V> mMap;
  private Map<V, K> mInverseMap;

  public BiHashMap() {
    mMap = new HashMap<>();
    mInverseMap = new HashMap<>();
  }

  public BiHashMap(int initialCapacity) {
    mMap = new HashMap<>(initialCapacity);
    mInverseMap = new HashMap<>(initialCapacity);
  }

  private BiHashMap(Map<K, V> map, Map<V, K> inverseMap) {
    mMap = map;
    mInverseMap = inverseMap;
  }

  public BiHashMap<V, K> inverse() {
    return new BiHashMap<>(mInverseMap, mMap);
  }

  public void clear() {
    mMap.clear();
    mInverseMap.clear();
  }

  public boolean containsKey(Object key) {
    return mMap.containsKey(key);
  }

  public boolean containsValue(Object value) {
    return mMap.containsValue(value);
  }

  public int size() {
    return mMap.size();
  }

  public boolean isEmpty() {
    return mMap.isEmpty();
  }

  public synchronized V put(K key, V value) {
    mInverseMap.put(value, key);
    return mMap.put(key, value);
  }

  public V get(Object key) {
    return mMap.get(key);
  }

  public synchronized V remove(Object key) {
    V value = mMap.remove(key);
    mInverseMap.remove(value);
    return value;
  }

  public synchronized void putAll(@NonNull Map<? extends K, ? extends V> m) {
    super.putAll(m);
  }

  @Override
  public synchronized V merge(K key, V value,
      BiFunction<? super V, ? super V, ? extends V> remappingFunction) {
    return super.merge(key, value, remappingFunction);
  }

  @NonNull
  @Override
  public Set<K> keySet() {
    return mMap.keySet();
  }

  @NonNull
  @Override
  public Collection<V> values() {
    return new AbstractCollection<V>() {
      @Override
      public int size() {
        return mMap.size();
      }

      @NonNull
      @Override
      public Iterator<V> iterator() {
        return new IteratorDecorator<V>(mMap.values().iterator()) {
          @Override
          public void remove() {
            super.remove();
            mInverseMap.remove(current);
          }
        };
      }

      @Override
      public boolean add(V v) {
        throw new UnsupportedOperationException(
            "adding to the value collection of a map is not supported");
      }

      @Override
      public boolean addAll(@NonNull Collection<? extends V> c) {
        throw new UnsupportedOperationException(
            "adding to the value collection of a map is not supported");
      }
    };
  }

  @NonNull
  @Override
  public Set<Entry<K, V>> entrySet() {
    return mMap.entrySet();
  }
}
