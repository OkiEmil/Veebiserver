import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import io.github.bucket4j.Bucket;


import java.time.Duration;

public class RateLimiter {
    private Cache<String, Bucket> cache;

    public RateLimiter() {
        this.cache = Caffeine.newBuilder()
                .expireAfterAccess(Duration.ofMinutes(15))
                .maximumSize(10_000)
                .build();
    }

    public Bucket resolveBucket(String ipAddress) {
        if (cache.getIfPresent(ipAddress) == null ) {
            cache.put(ipAddress, newBucket());
        }
        return cache.getIfPresent(ipAddress);
    }

    private Bucket newBucket() {

        //Refill refill = Refill.intervally(60, Duration.ofMinutes(1));
        //Bandwidth limit = Bandwidth.classic(60, refill);

        // IP-based rate limitting, 30 tokens per ip, 1 gets added every 3 seconds
        return Bucket.builder()
                .addLimit(limit -> limit.capacity(30).refillGreedy(20, Duration.ofMinutes(1)))
                .build();
    }
}
