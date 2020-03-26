package com.appleframework.id;

import org.junit.After;
import org.junit.Before;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import redis.clients.jedis.JedisPoolConfig;

/**
 * Test case for {@link RedisIdGenerator}
 * 
 * @author Thanh Nguyen <btnguyen2k@gmail.com>
 * @since 0.2.0
 */
public class TestRedisIdGenerator extends TestCase {

    private RedisTemplate<String, Long> redisTemplate;
    private RedisIdGenerator idGenerator;
    
    private String hostName = "localhost";
    private String passWord = null;
    private Integer port = 6379;
    private Integer database = 0;
    

    public static Test suite() {
        return new TestSuite(TestRedisIdGenerator.class);
    }
    
    @SuppressWarnings("deprecation")
	public RedisConnectionFactory redisConnectionFactory(){  
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
    
    public RedisTemplate<String, Long> redisTemplateObject() throws Exception {  
        RedisTemplate<String, Long> redisTemplateObject = new RedisTemplate<String, Long>();  
        redisTemplateObject.setConnectionFactory(redisConnectionFactory());  
        redisTemplateObject.afterPropertiesSet();  
        return redisTemplateObject;  
    }

    @Before
    public void setUp() throws Exception {
        redisTemplate = this.redisTemplateObject();
        idGenerator = RedisIdGenerator.getInstance(redisTemplate);
    }

    @After
    public void tearDown() throws Exception {
        Thread.sleep(5000);
    }

    @org.junit.Test
    public void test1() throws Exception {
    	System.out.println(idGenerator.nextId("default"));
        assertEquals(0, idGenerator.currentId("default"));
    }

    @org.junit.Test
    public void test2() throws Exception {
    	System.out.println(idGenerator.nextId("default"));
        assertEquals(1, idGenerator.nextId("default"));
        assertEquals(2, idGenerator.nextId("default"));
    }

    @org.junit.Test
    public void test3() throws Exception {
    	System.out.println(idGenerator.nextId("default"));
        assertEquals(0, idGenerator.currentId("default"));
        assertEquals(1, idGenerator.nextId("default"));
        assertEquals(1, idGenerator.currentId("default"));
    }
}
