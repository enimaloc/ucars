package com.useful.ucars.util;

import org.bukkit.metadata.MetadataValue;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class UMeta {
    private static final ConcurrentHashMap<WeakKey, Map<String, List<MetadataValue>>> metadata = new ConcurrentHashMap<WeakKey, Map<String, List<MetadataValue>>>(100, 0.75f, 3);

    public static void removeAllMeta(Object key) {
        WeakKey weakKey = new WeakKey(key);
        metadata.remove(weakKey);
    }

    public static Map<String, List<MetadataValue>> getAllMeta(Object key) {
        if (key == null) {
            return new ConcurrentHashMap<>(10, 0.75f, 2);
        }
        WeakKey weakKey = new WeakKey(key);
        Map<String, List<MetadataValue>> res = metadata.get(weakKey);
        if (res == null) {
            synchronized (metadata) {
                res = metadata.get(weakKey);
                if (res == null) {
                    res = new ConcurrentHashMap<>(10, 0.75f, 2);
                    metadata.put(weakKey, res);
                }
            }
        }
        return res;
    }

    public static List<MetadataValue> getMeta(Object key, String metaKey) {
        Map<String, List<MetadataValue>> meta = getAllMeta(key);
        List<MetadataValue> list = meta.get(metaKey);
        if (list == null) {
            synchronized (USchLocks.getMonitor(key)) {
                list = meta.computeIfAbsent(metaKey, k -> new ArrayList<>());
            }
        }
        return list;
    }

    public static void removeMeta(Object key, String metaKey) {
        Map<String, List<MetadataValue>> meta = getAllMeta(key);
        meta.remove(metaKey);
    }

    public static void gc() {
        /*System.gc();*/
        clean();
    }

    public static void clean() {
        for (Map.Entry<WeakKey, Map<String, List<MetadataValue>>> entry : metadata.entrySet()) {
            WeakKey ref = entry.getKey();
            try {
                if (ref.get() == null) {
                    metadata.remove(ref);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static int getTotalMetaSize() {
        clean();
        return metadata.size();
    }

    private static class WeakKey extends WeakReference<Object> {

        private int hash;

        public WeakKey(Object obj) {
            super(obj);
            this.hash = obj.hashCode();
        }

        @Override
        public int hashCode() {
            Object self = get();
            if (self != null) { //Update the hash code
                this.hash = self.hashCode();
            }
            return hash;
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof WeakKey)) {
                return false;
            }
            Object self = get();
            Object other = ((WeakKey) o).get();
            if (self == null || other == null) {
                return super.equals(o);
            }
            return self.equals(other);
        }

    }
}
