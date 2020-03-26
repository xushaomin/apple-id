package test.benchmark;

import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

import com.appleframework.id.RedisIdGenerator;
import redis.clients.jedis.JedisPoolConfig;


public class BenchmarkRedis extends BaseBenchmarkSerialId {
	
	private static String hostName = "localhost";
    private static String passWord = null;
    private static Integer port = 6379;
    private static Integer database = 0;
        
    @SuppressWarnings("deprecation")
	public static RedisConnectionFactory redisConnectionFactory(){  
        JedisPoolConfig poolConfig=new JedisPoolConfig();  
        poolConfig.setMaxIdle(10);  
        poolConfig.setMinIdle(1);  
        poolConfig.setTestOnBorrow(true);  
        poolConfig.setTestOnReturn(true);  
        poolConfig.setTestWhileIdle(true);  
        poolConfig.setNumTestsPerEvictionRun(10);  
        poolConfig.setTimeBetweenEvictionRunsMillis(60000);  
        JedisConnectionFactory jedisConnectionFactory = new JedisConnectionFactory(poolConfig);  
        jedisConnectionFactory.setHostName(hostName);  
        if(null != passWord && !passWord.isEmpty()){  
            jedisConnectionFactory.setPassword(passWord);  
        }  
        jedisConnectionFactory.setPort(port);  
        jedisConnectionFactory.setDatabase(database);  
        return jedisConnectionFactory;  
    }
    
    public static RedisTemplate<String, Long> redisTemplateObject() throws Exception {  
        RedisTemplate<String, Long> redisTemplateObject = new RedisTemplate<String, Long>();  
        redisTemplateObject.setConnectionFactory(redisConnectionFactory());  
        redisTemplateObject.afterPropertiesSet();  
        return redisTemplateObject;  
    }

    public static void main(String[] args) throws Exception {
        int numRuns, numThreads, numNamespaces;

        try {
            numRuns = Integer.parseInt(System.getProperty("numRuns"));
        } catch (Exception e) {
            numRuns = 100000;
        }
        try {
            numThreads = Integer.parseInt(System.getProperty("numThreads"));
        } catch (Exception e) {
            numThreads = 4;
        }
        try {
            numNamespaces = Integer.parseInt(System.getProperty("numNamespaces"));
        } catch (Exception e) {
            numNamespaces = 4;
        }


        System.out.println("Num runs: " + numRuns + " / Num threads: " + numThreads
                + " / Num namespaces: " + numNamespaces);

        RedisIdGenerator.invalidate();
        final RedisIdGenerator idGenerator = RedisIdGenerator.getInstance(redisTemplateObject());

        initValues(idGenerator, numNamespaces);

        for (int i = 0; i < 10; i++) {
            runTest(idGenerator, numRuns, numThreads, numNamespaces, "Redis");
        }

        printValues(idGenerator, numNamespaces);

        idGenerator.destroy();
    }
}
