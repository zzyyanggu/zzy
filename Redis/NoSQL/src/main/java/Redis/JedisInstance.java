package Redis;

import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class JedisInstance {
    //Privatized constructor
    private JedisInstance(){ }

    //Define a static enumeration class
    static enum SingletonEnum{
        INSTANCE;
        private JedisPool jedisPool;
        //Privatize the constructor of an enumeration
        private SingletonEnum(){
            JedisPoolConfig config = new JedisPoolConfig();
            config.setMaxTotal(300);
            config.setMaxIdle(10);
            jedisPool = new JedisPool(config, "127.0.0.1", 6379);
        }
        public JedisPool getInstnce(){
            return jedisPool;
        }
    }

    //Expose a static method to get the User object
    public static JedisPool getInstance(){
        return SingletonEnum.INSTANCE.getInstnce();
    }
}

