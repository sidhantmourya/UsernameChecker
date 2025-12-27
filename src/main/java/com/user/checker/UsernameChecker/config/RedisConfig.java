package com.user.checker.UsernameChecker.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.ClusterServersConfig;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisNode;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

    @Value("${spring.redis.host}")
    private String redisHost;

    @Value("${spring.redis.port}")
    private int redisPort;

    @Bean
    public LettuceConnectionFactory redisConnectionFactory() {
        if(redisPort == 0)
        {
            // it is a cluster
            RedisClusterConfiguration config = new RedisClusterConfiguration();

            String[] redisNodes = redisHost.split(",");
            for(String nodeStr : redisNodes)
            {
                RedisNode node = new RedisNode(nodeStr.split(":")[0], Integer.parseInt(nodeStr.split(":")[1]));
                config.addClusterNode(node);


//                config.addClusterNode(new RedisClusterConfiguration.ClusterNode(node.split(":")[0], Integer.parseInt(node.split(":")[1])));
            }
            return new LettuceConnectionFactory(config);
        }

        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration(redisHost, redisPort);
        return new LettuceConnectionFactory(config);

    }


    @Bean
    public RedisTemplate<String,Object> redisTemplate(RedisConnectionFactory redisConnectionFactory, ObjectMapper mapper)
    {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        Jackson2JsonRedisSerializer<Object> jsonRedisSerializer = new Jackson2JsonRedisSerializer<>(mapper, Object.class);

        template.setConnectionFactory(redisConnectionFactory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(jsonRedisSerializer);
        template.setHashValueSerializer(jsonRedisSerializer);
        template.setHashKeySerializer(new StringRedisSerializer());

        return  template;

    }

    @Bean(destroyMethod = "shutdown")
    public RedissonClient redissonClient()
    {
        Config config = new Config();
        if(redisPort  == 0)
        {
            // it is a cluster
            ClusterServersConfig clusterConfig = config.useClusterServers()
                    .setScanInterval(2000);
            String[] redisNodes = redisHost.split(",");
            for(String nodeStr : redisNodes)
            {
                String[] parts = nodeStr.split(":");
                String host = parts[0].trim();
                int port = Integer.parseInt(parts[1].trim());
                clusterConfig.addNodeAddress("redis://" + host + ":" + port);
            }
        }
        else
        {
            config.useSingleServer()
                    .setAddress("redis://" + redisHost + ":" + redisPort);

        }
        return Redisson.create(config);
    }

}
