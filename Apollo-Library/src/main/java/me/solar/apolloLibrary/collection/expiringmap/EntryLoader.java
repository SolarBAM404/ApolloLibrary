package me.solar.apolloLibrary.collection.expiringmap;

public interface EntryLoader<K, V> {
  V load(K paramK);
}