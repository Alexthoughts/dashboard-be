package dashboard.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.cache.annotation.EnableCaching;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

@Configuration
@EnableCaching
public class CacheConfig {

    @Bean
    public CacheManager cacheManager() {
        SimpleCacheManager cacheManager = new SimpleCacheManager();

        CaffeineCache holidaysCache = new CaffeineCache("holidaysCache",
                Caffeine.newBuilder()
                        .expireAfterWrite(1, TimeUnit.DAYS) // 1 day
                        .maximumSize(1000)
                        .build());

        CaffeineCache weatherCache = new CaffeineCache("weatherCache",
                Caffeine.newBuilder()
                        .expireAfterWrite(30, TimeUnit.MINUTES) // 30 min
                        .maximumSize(1000)
                        .build());

        CaffeineCache convertRateCache = new CaffeineCache("convertRateCache",
                Caffeine.newBuilder()
                        .expireAfterWrite(30, TimeUnit.MINUTES) // 30 min
                        .maximumSize(1000)
                        .build());

        CaffeineCache availableCurrenciesCache = new CaffeineCache("availableCurrenciesCache",
                Caffeine.newBuilder()
                        .expireAfterWrite(1, TimeUnit.DAYS) // 1 day
                        .maximumSize(1000)
                        .build());

        CaffeineCache notesCache = new CaffeineCache("notesCache",
                Caffeine.newBuilder()
                        .expireAfterWrite(1, TimeUnit.DAYS) // 1 day
                        .maximumSize(1000)
                        .build());

        cacheManager.setCaches(Arrays.asList(
                holidaysCache,
                weatherCache,
                convertRateCache,
                availableCurrenciesCache,
                notesCache
        ));

        return cacheManager;
    }
}