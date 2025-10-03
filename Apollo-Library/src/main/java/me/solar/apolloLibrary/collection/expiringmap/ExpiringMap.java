package me.solar.apolloLibrary.collection.expiringmap;

import lombok.NonNull;
import me.solar.apolloLibrary.Valid;

import java.lang.ref.WeakReference;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public final class ExpiringMap<K, V> implements ConcurrentMap<K, V> {
    static volatile ScheduledExecutorService EXPIRER;
    static volatile ThreadPoolExecutor LISTENER_SERVICE;
    static ThreadFactory THREAD_FACTORY;
    List<ExpirationListener<K, V>> expirationListeners;
    List<ExpirationListener<K, V>> asyncExpirationListeners;
    private final AtomicLong expirationNanos;
    private int maxSize;
    private final AtomicReference<ExpirationPolicy> expirationPolicy;
    private final EntryLoader<? super K, ? extends V> entryLoader;
    private final ExpiringEntryLoader<? super K, ? extends V> expiringEntryLoader;
    private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
    private final Lock readLock;
    private final Lock writeLock;
    private final EntryMap<K, V> entries;
    private final boolean variableExpiration;

    public static void setThreadFactory(@NonNull ThreadFactory threadFactory) {
        if (threadFactory == null) {
            throw new NullPointerException("threadFactory is marked non-null but is null");
        } else {
            THREAD_FACTORY = threadFactory;
        }
    }

    private ExpiringMap(Builder<K, V> builder) {
        this.readLock = this.readWriteLock.readLock();
        this.writeLock = this.readWriteLock.writeLock();
        if (EXPIRER == null) {
            synchronized(ExpiringMap.class) {
                if (EXPIRER == null) {
                    EXPIRER = Executors.newSingleThreadScheduledExecutor((ThreadFactory)(THREAD_FACTORY == null ? new org.sparkblock.net.sparkypaper.collection.expiringmap.NamedThreadFactory("ExpiringMap-Expirer") : THREAD_FACTORY));
                }
            }
        }

        if (LISTENER_SERVICE == null && builder.asyncExpirationListeners != null) {
            synchronized(ExpiringMap.class) {
                if (LISTENER_SERVICE == null) {
                    LISTENER_SERVICE = (ThreadPoolExecutor)Executors.newCachedThreadPool((ThreadFactory)(THREAD_FACTORY == null ? new org.sparkblock.net.sparkypaper.collection.expiringmap.NamedThreadFactory("ExpiringMap-Listener-%s") : THREAD_FACTORY));
                }
            }
        }

        this.variableExpiration = builder.variableExpiration;
        this.entries = (EntryMap<K, V>)(this.variableExpiration ? new EntryTreeHashMap() : new EntryLinkedHashMap());
        if (builder.expirationListeners != null) {
            this.expirationListeners = new CopyOnWriteArrayList(builder.expirationListeners);
        }

        if (builder.asyncExpirationListeners != null) {
            this.asyncExpirationListeners = new CopyOnWriteArrayList(builder.asyncExpirationListeners);
        }

        this.expirationPolicy = new AtomicReference(builder.expirationPolicy);
        this.expirationNanos = new AtomicLong(TimeUnit.NANOSECONDS.convert(builder.duration, builder.timeUnit));
        this.maxSize = builder.maxSize;
        this.entryLoader = builder.entryLoader;
        this.expiringEntryLoader = builder.expiringEntryLoader;
    }

    public static Builder<Object, Object> builder() {
        return new Builder<Object, Object>();
    }

    public static <K, V> ExpiringMap<K, V> create() {
        return new ExpiringMap<K, V>(builder());
    }

    public synchronized void addExpirationListener(ExpirationListener<K, V> listener) {
        Valid.checkNotNull(listener, "listener");
        if (this.expirationListeners == null) {
            this.expirationListeners = new CopyOnWriteArrayList();
        }

        this.expirationListeners.add(listener);
    }

    public synchronized void addAsyncExpirationListener(ExpirationListener<K, V> listener) {
        Valid.checkNotNull(listener, "listener");
        if (this.asyncExpirationListeners == null) {
            this.asyncExpirationListeners = new CopyOnWriteArrayList();
        }

        this.asyncExpirationListeners.add(listener);
    }

    public void clear() {
        this.writeLock.lock();

        try {
            for(ExpiringEntry<K, V> entry : this.entries.values()) {
                entry.cancel();
            }

            this.entries.clear();
        } finally {
            this.writeLock.unlock();
        }

    }

    public boolean containsKey(Object key) {
        this.readLock.lock();

        boolean var2;
        try {
            var2 = this.entries.containsKey(key);
        } finally {
            this.readLock.unlock();
        }

        return var2;
    }

    public boolean containsValue(Object value) {
        this.readLock.lock();

        boolean var2;
        try {
            var2 = this.entries.containsValue(value);
        } finally {
            this.readLock.unlock();
        }

        return var2;
    }

    public Set<Entry<K, V>> entrySet() {
        return new AbstractSet<Entry<K, V>>() {
            public void clear() {
                ExpiringMap.this.clear();
            }

            public boolean contains(Object entry) {
                if (!(entry instanceof Map.Entry<?, ?> e)) {
                    return false;
                } else {
                    return ExpiringMap.this.containsKey(e.getKey());
                }
            }

            public Iterator<Entry<K, V>> iterator() {
                Object var10000;
                if (ExpiringMap.this.entries instanceof EntryLinkedHashMap) {
                    EntryLinkedHashMap var10002 = (EntryLinkedHashMap)ExpiringMap.this.entries;
                    Objects.requireNonNull(var10002);
                    var10000 = var10002.new EntryIterator();
                } else {
                    EntryTreeHashMap var1 = (EntryTreeHashMap)ExpiringMap.this.entries;
                    Objects.requireNonNull(var1);
                    var10000 = var1.new EntryIterator();
                }

                return (Iterator<Map.Entry<K, V>>)var10000;
            }

            public boolean remove(Object entry) {
                if (entry instanceof Map.Entry<?, ?> e) {
                    return ExpiringMap.this.remove(e.getKey()) != null;
                } else {
                    return false;
                }
            }

            public int size() {
                return ExpiringMap.this.size();
            }
        };
    }

    public boolean equals(Object obj) {
        this.readLock.lock();

        boolean var2;
        try {
            var2 = this.entries.equals(obj);
        } finally {
            this.readLock.unlock();
        }

        return var2;
    }

    public V get(Object key) {
        ExpiringEntry<K, V> entry = this.getEntry(key);
        if (entry == null) {
            return (V)this.load((K) key);
        } else {
            if (ExpirationPolicy.ACCESSED.equals(entry.expirationPolicy.get())) {
                this.resetEntry(entry, false);
            }

            return entry.getValue();
        }
    }

    private V load(K key) {
        if (this.entryLoader == null && this.expiringEntryLoader == null) {
            return null;
        } else {
            this.writeLock.lock();

            V value;
            try {
                ExpiringEntry<K, V> entry = this.getEntry(key);
                if (entry == null) {
                    if (this.entryLoader != null) {
                        value = (V)this.entryLoader.load(key);
                        this.put(key, value);
                        Object var14 = value;
                        return (V)var14;
                    }

                    ExpiringValue<? extends V> expiringValue = this.expiringEntryLoader.load(key);
                    if (expiringValue == null) {
                        this.put(key, (V)null);
                        Object var13 = null;
                        return (V)var13;
                    }

                    long duration = expiringValue.getTimeUnit() == null ? this.expirationNanos.get() : expiringValue.getDuration();
                    TimeUnit timeUnit = expiringValue.getTimeUnit() == null ? TimeUnit.NANOSECONDS : expiringValue.getTimeUnit();
                    this.put(key, expiringValue.getValue(), expiringValue.getExpirationPolicy() == null ? (ExpirationPolicy)this.expirationPolicy.get() : expiringValue.getExpirationPolicy(), duration, timeUnit);
                    Object var7 = expiringValue.getValue();
                    return (V)var7;
                }

                value = entry.getValue();
            } finally {
                this.writeLock.unlock();
            }

            return value;
        }
    }

    public long getExpiration() {
        return TimeUnit.NANOSECONDS.toMillis(this.expirationNanos.get());
    }

    public long getExpiration(K key) {
        Valid.checkNotNull(key, "key");
        ExpiringEntry<K, V> entry = this.getEntry(key);
        return TimeUnit.NANOSECONDS.toMillis(entry.expirationNanos.get());
    }

    public ExpirationPolicy getExpirationPolicy(K key) {
        Valid.checkNotNull(key, "key");
        ExpiringEntry<K, V> entry = this.getEntry(key);
        Valid.checkNotNull(entry);
        return (ExpirationPolicy)entry.expirationPolicy.get();
    }

    public long getExpectedExpiration(K key) {
        Valid.checkNotNull(key, "key");
        ExpiringEntry<K, V> entry = this.getEntry(key);
        Valid.checkNotNull(entry);
        return TimeUnit.NANOSECONDS.toMillis(entry.expectedExpiration.get() - System.nanoTime());
    }

    public int getMaxSize() {
        return this.maxSize;
    }

    public int hashCode() {
        this.readLock.lock();

        int var1;
        try {
            var1 = this.entries.hashCode();
        } finally {
            this.readLock.unlock();
        }

        return var1;
    }

    public boolean isEmpty() {
        this.readLock.lock();

        boolean var1;
        try {
            var1 = this.entries.isEmpty();
        } finally {
            this.readLock.unlock();
        }

        return var1;
    }

    public Set<K> keySet() {
        return new AbstractSet<K>() {
            public void clear() {
                ExpiringMap.this.clear();
            }

            public boolean contains(Object key) {
                return ExpiringMap.this.containsKey(key);
            }

            public Iterator<K> iterator() {
                Object var10000;
                if (ExpiringMap.this.entries instanceof EntryLinkedHashMap) {
                    EntryLinkedHashMap var10002 = (EntryLinkedHashMap)ExpiringMap.this.entries;
                    Objects.requireNonNull(var10002);
                    var10000 = var10002.new KeyIterator();
                } else {
                    EntryTreeHashMap var1 = (EntryTreeHashMap)ExpiringMap.this.entries;
                    Objects.requireNonNull(var1);
                    var10000 = var1.new KeyIterator();
                }

                return (Iterator<K>)var10000;
            }

            public boolean remove(Object value) {
                return ExpiringMap.this.remove(value) != null;
            }

            public int size() {
                return ExpiringMap.this.size();
            }
        };
    }

    public V put(K key, V value) {
        Valid.checkNotNull(key, "key");
        return (V)this.putInternal(key, value, (ExpirationPolicy)this.expirationPolicy.get(), this.expirationNanos.get());
    }

    public V put(K key, V value, ExpirationPolicy expirationPolicy) {
        return (V)this.put(key, value, expirationPolicy, this.expirationNanos.get(), TimeUnit.NANOSECONDS);
    }

    public V put(K key, V value, long duration, TimeUnit timeUnit) {
        return (V)this.put(key, value, (ExpirationPolicy)this.expirationPolicy.get(), duration, timeUnit);
    }

    public V put(K key, V value, ExpirationPolicy expirationPolicy, long duration, TimeUnit timeUnit) {
        Valid.checkNotNull(key, "key");
        Valid.checkNotNull(expirationPolicy, "expirationPolicy");
        Valid.checkNotNull(timeUnit, "timeUnit");
        Valid.checkBoolean(this.variableExpiration, "Variable expiration is not enabled");
        return (V)this.putInternal(key, value, expirationPolicy, TimeUnit.NANOSECONDS.convert(duration, timeUnit));
    }

    public void putAll(Map<? extends K, ? extends V> map) {
        Valid.checkNotNull(map, "map");
        long expiration = this.expirationNanos.get();
        ExpirationPolicy expirationPolicy = (ExpirationPolicy)this.expirationPolicy.get();
        this.writeLock.lock();

        try {
            for(Map.Entry<? extends K, ? extends V> entry : map.entrySet()) {
                this.putInternal(entry.getKey(), entry.getValue(), expirationPolicy, expiration);
            }
        } finally {
            this.writeLock.unlock();
        }

    }

    public V putIfAbsent(K key, V value) {
        Valid.checkNotNull(key, "key");
        this.writeLock.lock();

        Object var3;
        try {
            if (this.entries.containsKey(key)) {
                var3 = ((ExpiringEntry)this.entries.get(key)).getValue();
                return (V)var3;
            }

            var3 = this.putInternal(key, value, (ExpirationPolicy)this.expirationPolicy.get(), this.expirationNanos.get());
        } finally {
            this.writeLock.unlock();
        }

        return (V)var3;
    }

    public V remove(Object key) {
        Valid.checkNotNull(key, "key");
        this.writeLock.lock();

        Object var3;
        try {
            ExpiringEntry<K, V> entry = (ExpiringEntry)this.entries.remove(key);
            if (entry != null) {
                if (entry.cancel()) {
                    this.scheduleEntry(this.entries.first());
                }

                var3 = entry.getValue();
                return (V)var3;
            }

            var3 = null;
        } finally {
            this.writeLock.unlock();
        }

        return (V)var3;
    }

    public boolean remove(Object key, Object value) {
        Valid.checkNotNull(key, "key");
        this.writeLock.lock();

        boolean var4;
        try {
            ExpiringEntry<K, V> entry = (ExpiringEntry)this.entries.get(key);
            if (entry == null || !entry.getValue().equals(value)) {
                var4 = false;
                return var4;
            }

            this.entries.remove(key);
            if (entry.cancel()) {
                this.scheduleEntry(this.entries.first());
            }

            var4 = true;
        } finally {
            this.writeLock.unlock();
        }

        return var4;
    }

    public V replace(K key, V value) {
        Valid.checkNotNull(key, "key");
        this.writeLock.lock();

        Object var3;
        try {
            if (!this.entries.containsKey(key)) {
                var3 = null;
                return (V)var3;
            }

            var3 = this.putInternal(key, value, (ExpirationPolicy)this.expirationPolicy.get(), this.expirationNanos.get());
        } finally {
            this.writeLock.unlock();
        }

        return (V)var3;
    }

    public boolean replace(K key, V oldValue, V newValue) {
        Valid.checkNotNull(key, "key");
        this.writeLock.lock();

        boolean var5;
        try {
            ExpiringEntry<K, V> entry = (ExpiringEntry)this.entries.get(key);
            if (entry == null || !entry.getValue().equals(oldValue)) {
                var5 = false;
                return var5;
            }

            this.putInternal(key, newValue, (ExpirationPolicy)this.expirationPolicy.get(), this.expirationNanos.get());
            var5 = true;
        } finally {
            this.writeLock.unlock();
        }

        return var5;
    }

    public void removeExpirationListener(ExpirationListener<K, V> listener) {
        Valid.checkNotNull(listener, "listener");

        for(int i = 0; i < this.expirationListeners.size(); ++i) {
            if (((ExpirationListener)this.expirationListeners.get(i)).equals(listener)) {
                this.expirationListeners.remove(i);
                return;
            }
        }

    }

    public void removeAsyncExpirationListener(ExpirationListener<K, V> listener) {
        Valid.checkNotNull(listener, "listener");

        for(int i = 0; i < this.asyncExpirationListeners.size(); ++i) {
            if (((ExpirationListener)this.asyncExpirationListeners.get(i)).equals(listener)) {
                this.asyncExpirationListeners.remove(i);
                return;
            }
        }

    }

    public void resetExpiration(K key) {
        Valid.checkNotNull(key, "key");
        ExpiringEntry<K, V> entry = this.getEntry(key);
        if (entry != null) {
            this.resetEntry(entry, false);
        }

    }

    public void setExpiration(K key, long duration, TimeUnit timeUnit) {
        Valid.checkNotNull(key, "key");
        Valid.checkNotNull(timeUnit, "timeUnit");
        Valid.checkBoolean(this.variableExpiration, "Variable expiration is not enabled");
        this.writeLock.lock();

        try {
            ExpiringEntry<K, V> entry = (ExpiringEntry)this.entries.get(key);
            if (entry != null) {
                entry.expirationNanos.set(TimeUnit.NANOSECONDS.convert(duration, timeUnit));
                this.resetEntry(entry, true);
            }
        } finally {
            this.writeLock.unlock();
        }

    }

    public void setExpiration(long duration, TimeUnit timeUnit) {
        Valid.checkNotNull(timeUnit, "timeUnit");
        Valid.checkBoolean(this.variableExpiration, "Variable expiration is not enabled");
        this.expirationNanos.set(TimeUnit.NANOSECONDS.convert(duration, timeUnit));
    }

    public void setExpirationPolicy(ExpirationPolicy expirationPolicy) {
        Valid.checkNotNull(expirationPolicy, "expirationPolicy");
        this.expirationPolicy.set(expirationPolicy);
    }

    public void setExpirationPolicy(K key, ExpirationPolicy expirationPolicy) {
        Valid.checkNotNull(key, "key");
        Valid.checkNotNull(expirationPolicy, "expirationPolicy");
        Valid.checkBoolean(this.variableExpiration, "Variable expiration is not enabled");
        ExpiringEntry<K, V> entry = this.getEntry(key);
        if (entry != null) {
            entry.expirationPolicy.set(expirationPolicy);
        }

    }

    public void setMaxSize(int maxSize) {
        Valid.checkBoolean(maxSize > 0, "maxSize");
        this.maxSize = maxSize;
    }

    public int size() {
        this.readLock.lock();

        int var1;
        try {
            var1 = this.entries.size();
        } finally {
            this.readLock.unlock();
        }

        return var1;
    }

    public String toString() {
        this.readLock.lock();

        String var1;
        try {
            var1 = this.entries.toString();
        } finally {
            this.readLock.unlock();
        }

        return var1;
    }

    public Collection<V> values() {
        return new AbstractCollection<V>() {
            public void clear() {
                ExpiringMap.this.clear();
            }

            public boolean contains(Object value) {
                return ExpiringMap.this.containsValue(value);
            }

            public Iterator<V> iterator() {
                Object var10000;
                if (ExpiringMap.this.entries instanceof EntryLinkedHashMap) {
                    EntryLinkedHashMap var10002 = (EntryLinkedHashMap)ExpiringMap.this.entries;
                    Objects.requireNonNull(var10002);
                    var10000 = var10002.new ValueIterator();
                } else {
                    EntryTreeHashMap var1 = (EntryTreeHashMap)ExpiringMap.this.entries;
                    Objects.requireNonNull(var1);
                    var10000 = var1.new ValueIterator();
                }

                return (Iterator<V>)var10000;
            }

            public int size() {
                return ExpiringMap.this.size();
            }
        };
    }

    void notifyListeners(ExpiringEntry<K, V> entry) {
        if (this.asyncExpirationListeners != null) {
            for(ExpirationListener<K, V> listener : this.asyncExpirationListeners) {
                LISTENER_SERVICE.execute(() -> {
                    try {
                        listener.expired(entry.key, entry.getValue());
                    } catch (Exception var3) {
                    }

                });
            }
        }

        if (this.expirationListeners != null) {
            for(ExpirationListener<K, V> listener : this.expirationListeners) {
                try {
                    listener.expired(entry.key, entry.getValue());
                } catch (Exception var5) {
                }
            }
        }

    }

    ExpiringEntry<K, V> getEntry(Object key) {
        this.readLock.lock();

        ExpiringEntry var2;
        try {
            var2 = (ExpiringEntry)this.entries.get(key);
        } finally {
            this.readLock.unlock();
        }

        return var2;
    }

    V putInternal(K key, V value, ExpirationPolicy expirationPolicy, long expirationNanos) {
        this.writeLock.lock();

        try {
            ExpiringEntry<K, V> entry = (ExpiringEntry)this.entries.get(key);
            V oldValue = null;
            if (entry == null) {
                entry = new ExpiringEntry<K, V>(key, value, this.variableExpiration ? new AtomicReference(expirationPolicy) : this.expirationPolicy, this.variableExpiration ? new AtomicLong(expirationNanos) : this.expirationNanos);
                if (this.entries.size() >= this.maxSize) {
                    ExpiringEntry<K, V> expiredEntry = this.entries.first();
                    this.entries.remove(expiredEntry.key);
                    this.notifyListeners(expiredEntry);
                }

                this.entries.put(key, entry);
                if (this.entries.size() == 1 || this.entries.first().equals(entry)) {
                    this.scheduleEntry(entry);
                }
            } else {
                oldValue = entry.getValue();
                if (!ExpirationPolicy.ACCESSED.equals(expirationPolicy) && (oldValue == null && value == null || oldValue != null && oldValue.equals(value))) {
                    Object expiredEntry = value;
                    return (V)expiredEntry;
                }

                entry.setValue(value);
                this.resetEntry(entry, false);
            }

            Object var14 = oldValue;
            return (V)var14;
        } finally {
            this.writeLock.unlock();
        }
    }

    void resetEntry(ExpiringEntry<K, V> entry, boolean scheduleFirstEntry) {
        this.writeLock.lock();

        try {
            boolean scheduled = entry.cancel();
            this.entries.reorder(entry);
            if (scheduled || scheduleFirstEntry) {
                this.scheduleEntry(this.entries.first());
            }
        } finally {
            this.writeLock.unlock();
        }

    }

    void scheduleEntry(ExpiringEntry<K, V> entry) {
        if (entry != null && !entry.scheduled) {
            Runnable runnable = null;
            synchronized(entry) {
                if (!entry.scheduled) {
                    WeakReference<ExpiringEntry<K, V>> entryReference = new WeakReference(entry);
                    runnable = () -> {
                        ExpiringEntry<K, V> entry1 = (ExpiringEntry)entryReference.get();
                        this.writeLock.lock();

                        try {
                            if (entry1 != null && entry1.scheduled) {
                                this.entries.remove(entry1.key);
                                this.notifyListeners(entry1);
                            }

                            try {
                                Iterator<ExpiringEntry<K, V>> iterator = this.entries.valuesIterator();
                                boolean schedulePending = true;

                                while(iterator.hasNext() && schedulePending) {
                                    ExpiringEntry<K, V> nextEntry = (ExpiringEntry)iterator.next();
                                    if (nextEntry.expectedExpiration.get() <= System.nanoTime()) {
                                        iterator.remove();
                                        this.notifyListeners(nextEntry);
                                    } else {
                                        this.scheduleEntry(nextEntry);
                                        schedulePending = false;
                                    }
                                }
                            } catch (NoSuchElementException var9) {
                            }
                        } finally {
                            this.writeLock.unlock();
                        }

                    };
                    Future<?> entryFuture = EXPIRER.schedule(runnable, entry.expectedExpiration.get() - System.nanoTime(), TimeUnit.NANOSECONDS);
                    entry.schedule(entryFuture);
                }
            }
        }
    }

    private static <K, V> Map.Entry<K, V> mapEntryFor(final ExpiringEntry<K, V> entry) {
        return new Map.Entry<K, V>() {
            public K getKey() {
                return entry.key;
            }

            public V getValue() {
                return entry.value;
            }

            public V setValue(V value) {
                throw new UnsupportedOperationException();
            }
        };
    }

    public static final class Builder<K, V> {
        private ExpirationPolicy expirationPolicy;
        private List<ExpirationListener<K, V>> expirationListeners;
        private List<ExpirationListener<K, V>> asyncExpirationListeners;
        private TimeUnit timeUnit;
        private boolean variableExpiration;
        private long duration;
        private int maxSize;
        private EntryLoader<K, V> entryLoader;
        private ExpiringEntryLoader<K, V> expiringEntryLoader;

        private Builder() {
            this.expirationPolicy = ExpirationPolicy.CREATED;
            this.timeUnit = TimeUnit.SECONDS;
            this.duration = 60L;
            this.maxSize = Integer.MAX_VALUE;
        }

        public <K1 extends K, V1 extends V> ExpiringMap<K1, V1> build() {
            return new ExpiringMap<K1, V1>((Builder<K1, V1>) this);
        }

        public Builder<K, V> expiration(long duration, @NonNull TimeUnit timeUnit) {
            if (timeUnit == null) {
                throw new NullPointerException("timeUnit is marked non-null but is null");
            } else {
                this.duration = duration;
                this.timeUnit = timeUnit;
                return this;
            }
        }

        public Builder<K, V> maxSize(int maxSize) {
            Valid.checkBoolean(maxSize > 0, "maxSize");
            this.maxSize = maxSize;
            return this;
        }

        public <K1 extends K, V1 extends V> Builder<K1, V1> entryLoader(@NonNull EntryLoader<? super K1, ? super V1> loader) {
            if (loader == null) {
                throw new NullPointerException("loader is marked non-null but is null");
            } else {
                this.assertNoLoaderSet();
                this.entryLoader = (EntryLoader<K, V>) loader;
                return (Builder<K1, V1>) this;
            }
        }

        public <K1 extends K, V1 extends V> Builder<K1, V1> expiringEntryLoader(@NonNull ExpiringEntryLoader<? super K1, ? super V1> loader) {
            if (loader == null) {
                throw new NullPointerException("loader is marked non-null but is null");
            } else {
                this.assertNoLoaderSet();
                this.expiringEntryLoader = (ExpiringEntryLoader<K, V>) loader;
                this.variableExpiration();
                return (Builder<K1, V1>) this;
            }
        }

        public <K1 extends K, V1 extends V> Builder<K1, V1> expirationListener(ExpirationListener<? super K1, ? super V1> listener) {
            Valid.checkNotNull(listener, "listener");
            if (this.expirationListeners == null) {
                this.expirationListeners = new ArrayList();
            }

            this.expirationListeners.add((ExpirationListener<K, V>) listener);
            return (Builder<K1, V1>) this;
        }

        public <K1 extends K, V1 extends V> Builder<K1, V1> expirationListeners(List<ExpirationListener<? super K1, ? super V1>> listeners) {
            Valid.checkNotNull(listeners, "listeners");
            if (this.expirationListeners == null) {
                this.expirationListeners = new ArrayList(listeners.size());
            }

            for(ExpirationListener<? super K1, ? super V1> listener : listeners) {
                this.expirationListeners.add((ExpirationListener<K, V>) listener);
            }

            return (Builder<K1, V1>) this;
        }

        public <K1 extends K, V1 extends V> Builder<K1, V1> asyncExpirationListener(ExpirationListener<? super K1, ? super V1> listener) {
            Valid.checkNotNull(listener, "listener");
            if (this.asyncExpirationListeners == null) {
                this.asyncExpirationListeners = new ArrayList();
            }

            this.asyncExpirationListeners.add((ExpirationListener<K, V>) listener);
            return (Builder<K1, V1>) this;
        }

        public <K1 extends K, V1 extends V> Builder<K1, V1> asyncExpirationListeners(List<ExpirationListener<? super K1, ? super V1>> listeners) {
            Valid.checkNotNull(listeners, "listeners");
            if (this.asyncExpirationListeners == null) {
                this.asyncExpirationListeners = new ArrayList(listeners.size());
            }

            for(ExpirationListener<? super K1, ? super V1> listener : listeners) {
                this.asyncExpirationListeners.add((ExpirationListener<K, V>) listener);
            }

            return (Builder<K1, V1>) this;
        }

        public Builder<K, V> expirationPolicy(@NonNull ExpirationPolicy expirationPolicy) {
            if (expirationPolicy == null) {
                throw new NullPointerException("expirationPolicy is marked non-null but is null");
            } else {
                this.expirationPolicy = expirationPolicy;
                return this;
            }
        }

        public Builder<K, V> variableExpiration() {
            this.variableExpiration = true;
            return this;
        }

        private void assertNoLoaderSet() {
            Valid.checkBoolean(this.entryLoader == null && this.expiringEntryLoader == null, "Either entryLoader or expiringEntryLoader may be set, not both");
        }
    }

    private static class EntryLinkedHashMap<K, V> extends LinkedHashMap<K, ExpiringEntry<K, V>> implements EntryMap<K, V> {
        private static final long serialVersionUID = 1L;

        public boolean containsValue(Object value) {
            for(ExpiringEntry<K, V> entry : this.values()) {
                V v = entry.value;
                if (v == value || value != null && value.equals(v)) {
                    return true;
                }
            }

            return false;
        }

        public ExpiringEntry<K, V> first() {
            return this.isEmpty() ? null : (ExpiringEntry)this.values().iterator().next();
        }

        public void reorder(ExpiringEntry<K, V> value) {
            this.remove(value.key);
            value.resetExpiration();
            this.put(value.key, value);
        }

        public Iterator<ExpiringEntry<K, V>> valuesIterator() {
            return this.values().iterator();
        }

        abstract class AbstractHashIterator {
            private final Iterator<Map.Entry<K, ExpiringEntry<K, V>>> iterator = EntryLinkedHashMap.this.entrySet().iterator();
            private ExpiringEntry<K, V> next;

            public boolean hasNext() {
                return this.iterator.hasNext();
            }

            public ExpiringEntry<K, V> getNext() {
                this.next = (ExpiringEntry)((Map.Entry)this.iterator.next()).getValue();
                return this.next;
            }

            public void remove() {
                this.iterator.remove();
            }
        }

        final class KeyIterator extends EntryLinkedHashMap<K, V>.AbstractHashIterator implements Iterator<K> {
            public K next() {
                return this.getNext().key;
            }
        }

        final class ValueIterator extends EntryLinkedHashMap<K, V>.AbstractHashIterator implements Iterator<V> {
            public V next() {
                return this.getNext().value;
            }
        }

        public final class EntryIterator extends EntryLinkedHashMap<K, V>.AbstractHashIterator implements Iterator<Map.Entry<K, V>> {
            public Map.Entry<K, V> next() {
                return ExpiringMap.<K, V>mapEntryFor(this.getNext());
            }
        }
    }

    private static class EntryTreeHashMap<K, V> extends HashMap<K, ExpiringEntry<K, V>> implements EntryMap<K, V> {
        private static final long serialVersionUID = 1L;
        SortedSet<ExpiringEntry<K, V>> sortedSet = new ConcurrentSkipListSet();

        public void clear() {
            super.clear();
            this.sortedSet.clear();
        }

        public boolean containsValue(Object value) {
            for(ExpiringEntry<K, V> entry : this.values()) {
                V v = entry.value;
                if (v == value || value != null && value.equals(v)) {
                    return true;
                }
            }

            return false;
        }

        public ExpiringEntry<K, V> first() {
            return this.sortedSet.isEmpty() ? null : (ExpiringEntry)this.sortedSet.first();
        }

        public ExpiringEntry<K, V> put(K key, ExpiringEntry<K, V> value) {
            this.sortedSet.add(value);
            return (ExpiringEntry)super.put(key, value);
        }

        public ExpiringEntry<K, V> remove(Object key) {
            ExpiringEntry<K, V> entry = (ExpiringEntry)super.remove(key);
            if (entry != null) {
                this.sortedSet.remove(entry);
            }

            return entry;
        }

        public void reorder(ExpiringEntry<K, V> value) {
            this.sortedSet.remove(value);
            value.resetExpiration();
            this.sortedSet.add(value);
        }

        public Iterator<ExpiringEntry<K, V>> valuesIterator() {
            return new ExpiringEntryIterator();
        }

        abstract class AbstractHashIterator {
            private final Iterator<ExpiringEntry<K, V>> iterator;
            protected ExpiringEntry<K, V> next;

            AbstractHashIterator() {
                this.iterator = EntryTreeHashMap.this.sortedSet.iterator();
            }

            public boolean hasNext() {
                return this.iterator.hasNext();
            }

            public ExpiringEntry<K, V> getNext() {
                this.next = (ExpiringEntry)this.iterator.next();
                return this.next;
            }

            public void remove() {
                ExpiringMap.EntryTreeHashMap.super.remove(this.next.key);
                this.iterator.remove();
            }
        }

        final class ExpiringEntryIterator extends EntryTreeHashMap<K, V>.AbstractHashIterator implements Iterator<ExpiringEntry<K, V>> {
            public ExpiringEntry<K, V> next() {
                return this.getNext();
            }
        }

        final class KeyIterator extends EntryTreeHashMap<K, V>.AbstractHashIterator implements Iterator<K> {
            public K next() {
                return this.getNext().key;
            }
        }

        final class ValueIterator extends EntryTreeHashMap<K, V>.AbstractHashIterator implements Iterator<V> {
            public V next() {
                return this.getNext().value;
            }
        }

        final class EntryIterator extends EntryTreeHashMap<K, V>.AbstractHashIterator implements Iterator<Map.Entry<K, V>> {
            public Map.Entry<K, V> next() {
                return ExpiringMap.<K, V>mapEntryFor(this.getNext());
            }
        }
    }

    static class ExpiringEntry<K, V> implements Comparable<ExpiringEntry<K, V>> {
        final AtomicLong expirationNanos;
        final AtomicLong expectedExpiration;
        final AtomicReference<ExpirationPolicy> expirationPolicy;
        final K key;
        volatile Future<?> entryFuture;
        V value;
        volatile boolean scheduled;

        ExpiringEntry(K key, V value, AtomicReference<ExpirationPolicy> expirationPolicy, AtomicLong expirationNanos) {
            this.key = key;
            this.value = value;
            this.expirationPolicy = expirationPolicy;
            this.expirationNanos = expirationNanos;
            this.expectedExpiration = new AtomicLong();
            this.resetExpiration();
        }

        public int compareTo(ExpiringEntry<K, V> other) {
            if (this.key.equals(other.key)) {
                return 0;
            } else {
                return this.expectedExpiration.get() < other.expectedExpiration.get() ? -1 : 1;
            }
        }

        public int hashCode() {
            return Objects.hash(new Object[]{this.key, this.value});
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            } else if (obj == null) {
                return false;
            } else if (this.getClass() != obj.getClass()) {
                return false;
            } else {
                ExpiringEntry<?, ?> other = (ExpiringEntry)obj;
                if (!this.key.equals(other.key)) {
                    return false;
                } else {
                    return Objects.equals(this.value, other.value);
                }
            }
        }

        public String toString() {
            return this.value != null ? this.value.toString() : "";
        }

        synchronized boolean cancel() {
            boolean result = this.scheduled;
            if (this.entryFuture != null) {
                this.entryFuture.cancel(false);
            }

            this.entryFuture = null;
            this.scheduled = false;
            return result;
        }

        synchronized V getValue() {
            return this.value;
        }

        void resetExpiration() {
            this.expectedExpiration.set(this.expirationNanos.get() + System.nanoTime());
        }

        synchronized void schedule(Future<?> entryFuture) {
            this.entryFuture = entryFuture;
            this.scheduled = true;
        }

        synchronized void setValue(V value) {
            this.value = value;
        }
    }

    private interface EntryMap<K, V> extends Map<K, ExpiringEntry<K, V>> {
        ExpiringEntry<K, V> first();

        void reorder(ExpiringEntry<K, V> var1);

        Iterator<ExpiringEntry<K, V>> valuesIterator();
    }

    public interface ExpirationListener<K, V> {
        void expired(K var1, V var2);
    }
}
