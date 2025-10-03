package me.solar.apolloLibrary.collection.expiringmap;

public interface ExpiringEntryLoader<K, V> {
  ExpiringValue<V> load(K paramK);
}


/* Location:              E:\Users\Solar\.m2\repo\org\sparkblock\sparky\Sparky-Paper\1.0.0-SNAPSHOT\Sparky-Paper-1.0.0-SNAPSHOT.jar!\org\sparkblock\net\sparkypaper\collection\expiringmap\ExpiringEntryLoader.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */