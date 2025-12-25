package indi.yoloz.sample.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

public class GuavaLoadCache {

    public static void main(String args[]) {
        LoadingCache<String, String> cache =
                CacheBuilder.newBuilder()
                        .maximumSize(100) // maximum 100 records can be cached
//                        .expireAfterAccess(30, TimeUnit.MINUTES) // cache will expire after 30 minutes of access
                        .build(new CacheLoader<String, String>() { // build the cacheloader
                            @Override
                            public String load(String id) throws Exception {
                                //make the expensive call
                                String result = getFromDatabase(id);
                                return result == null ? "empty" : result;
                            }
                        });

        try {
            System.out.println("##on first invocation, cache will be populated with corresponding record");
            System.out.println(cache.get("100"));
            System.out.println(cache.get("103"));
            System.out.println(cache.get("110"));
            System.out.println("##second invocation, data will be returned from cache");
            System.out.println(cache.get("100"));
            System.out.println(cache.get("103"));
            System.out.println(cache.get("110"));
            cache.invalidateAll();
            System.out.println("##third invocation, discard all data from cache");
            System.out.println(cache.get("100"));
            System.out.println(cache.get("103"));
            System.out.println(cache.get("110"));
            System.out.println("##update cache data");
            cache.put("103", "++++");
            System.out.println(cache.get("100"));
            System.out.println(cache.get("103"));
            System.out.println(cache.get("110"));
            System.out.println("##refresh cache data");
            cache.refresh("103");
            System.out.println(cache.get("100"));
            System.out.println(cache.get("103"));
            System.out.println(cache.get("110"));
            System.out.println("##get not exist data");
            System.out.println(cache.get("120"));

        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    private static String getFromDatabase(String empId) {
        Map<String, String> database = new HashMap<>();
        database.put("100", "123");
        database.put("103", "456");
        database.put("110", "789");
        System.out.println("Database hit for" + empId);
        return database.get(empId);
    }
}
