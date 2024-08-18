package net.minecraft.server;

import com.google.common.collect.Maps;
import org.github.paperspigot.exception.ServerInternalException;
import xyz.zenithdev.spigot.config.FulfillSpigotConfig;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

public class RegionFileCache {

    public static final Map<File, RegionFile> a = Maps.newHashMap(); // Spigot - private -> public
    // FulfillSpigot start
    private static final int MAX_CACHE_SIZE = FulfillSpigotConfig.get().maxCacheSize;
    private static final Map<File, RegionFile> cache = new LinkedHashMap<File, RegionFile>(MAX_CACHE_SIZE, 0.75f, true) {
        @Override
        protected boolean removeEldestEntry(Map.Entry<File, RegionFile> eldest) {
            if (size() > MAX_CACHE_SIZE) {
                try {
                    // FulfillSpigot - close the RegionFile when it is removed from the cache
                    eldest.getValue().close();
                } catch (IOException e) {
                    e.printStackTrace();
                    ServerInternalException.reportInternalException(e);
                }
                return true;
            }
            return false;
        }
    };
    // FulfillSpigot end
    public static synchronized RegionFile a(File file, int i, int j) {
        return a(file, i, j, true);
    }

    public static synchronized RegionFile a(File file, int i, int j, boolean create) {
        File file1 = new File(file, "region");
        File file2 = new File(file1, "r." + (i >> 5) + "." + (j >> 5) + ".mca");
        RegionFile regionfile = cache.get(file2);

        if (regionfile != null) {
            return regionfile;
        } else {
            if (!create && !file2.exists()) {
                return null;
            }
            if (!file1.exists()) {
                file1.mkdirs();
            }

            if (cache.size() >= MAX_CACHE_SIZE) {
                a();
            }

            RegionFile regionfile1 = new RegionFile(file2);
            cache.put(file2, regionfile1);
            return regionfile1;
        }
    }

    public static synchronized void a() {
        Iterator<RegionFile> iterator = cache.values().iterator();

        while (iterator.hasNext()) {
            RegionFile regionfile = iterator.next();

            try {
                if (regionfile != null) {
                    regionfile.c();
                }
            } catch (IOException ioexception) {
                ioexception.printStackTrace();
                ServerInternalException.reportInternalException(ioexception);
            }
        }

        cache.clear();
    }

    public static DataInputStream c(File file, int i, int j) {
        RegionFile regionfile = a(file, i, j);
        return regionfile.a(i & 31, j & 31);
    }

    public static DataOutputStream d(File file, int i, int j) {
        RegionFile regionfile = a(file, i, j);
        return regionfile.b(i & 31, j & 31);
    }
}
