package io.github.gasparbarancelli;

import jakarta.enterprise.context.ApplicationScoped;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;

@ApplicationScoped
public class RedisDataSource {

    RedissonClient client;

    public RedisDataSource() {
        Config config = new Config();
        config.useSingleServer()
                .setAddress("redis://127.0.0.1:6379")
                .setPassword("rinhaBackend");

        client = Redisson.create(config);
    }

    public RedissonClient getClient() {
        return client;
    }

}
