package Redis;

import java.util.*;
import java.text.SimpleDateFormat;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import redis.clients.jedis.Jedis;
import static Redis.JsontoString.JsontoString;

public class JedisTest {
    private Jedis jedis;
    private static HashMap<String,HashMap> action;
    private static HashMap<String,HashMap> counter;

    public JedisTest() {
        jedis = JedisInstance.getInstance().getResource();
        initAction();
        initCounter();
    }

    public void initAction(){
        action = new HashMap<>();
        if(action != null){
            action.clear();
        }
        String s = JsontoString("src/main/resources/Action.json");
        //The string is converted to a JSON object
        JSONObject jsonobj = JSON.parseObject(s);
        //Build the JSONArray array from json objects
        JSONArray array = jsonobj.getJSONArray("actions");
        //Gets each JSON element in the array
        for (int i = 0; i < array.size(); i++) {
            JSONObject elem = (JSONObject) array.get(i);
            HashMap<String, String> action1 = new HashMap<>();
            String name = (String) elem.get("name");
            action1.put("name", name);
            String describe = (String) elem.get("describe");
            action1.put("describe", describe);
            //Set array1
            JSONArray array1 = elem.getJSONArray("show");
            for (int m = 0; m < array1.size(); m++) {
                JSONObject o = (JSONObject) array1.get(m);
                String show = (String) o.get("CName");
                action1.put("show", show);
            }
            //Set array2
            JSONArray array2 = elem.getJSONArray("operation");
            if (array2 != null) {
                for (int n = 0; n < array2.size(); n++) {
                    JSONObject o = (JSONObject) array2.get(n);
                    String operation = (String) o.get("CName");
                    action1.put("operation", operation);
                }
            }
            action.put(name, action1);
        }
    }

    public void initCounter(){
        counter = new HashMap<>();
        if(counter != null){
            counter.clear();
        }
        String s = JsontoString("src/main/resources/Counter.json");
        //The string is converted to a JSON object
        JSONObject jsonobj = JSON.parseObject(s);
        //Build the JSONArray array from json objects
        JSONArray array = jsonobj.getJSONArray("counters");
        //Gets each JSON element in the array
        for (int i = 0; i < array.size(); i++) {
            JSONObject elem = (JSONObject) array.get(i);
            HashMap<String, String> counter1 = new HashMap<>();

            String counterName = (String) elem.get("name");
            counter1.put("name", counterName);

            String type = (String) elem.get("type");
            counter1.put("type", type);

            String key = (String) elem.get("key");
            counter1.put("key", key);

            String value = (String) elem.get("value");
            if(value!=null){
                counter1.put("value", value);
            }

            String field = (String) elem.get("field");
            if(field!=null){
                counter1.put("field", field);
            }
            counter.put(counterName, counter1);
        }
    }

    String key="totalCnt";
    public void ShowCnt(){
            System.out.println("The total number is:" + jedis.get(key));
    }

    public void AddCnt(String value){
        //Get current time
        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyymmddhhmm");
        String enterTime = dateFormat.format(date);
        SimpleDateFormat argformat = new SimpleDateFormat("hhmmss");
        String arg = argformat.format(date);
        int enterarg = Integer.parseInt(arg);
        //Get the added amount
        int num = Integer.parseInt(value);
        if(jedis.get(key) == null) {
            jedis.set(key,value);
            for(int i = 0; i < num; i++){
                jedis.rpush("enterList", enterTime);
                jedis.sadd("enterSet", enterTime);
                jedis.zadd("enterZset",enterarg,enterTime);
            }
        }
        else {
            for(int i = 0; i < num; i++) {
                jedis.incr(key);
                jedis.rpush("enterList", enterTime);
                jedis.sadd("enterSet", enterTime);
                jedis.zadd("enterZset",enterarg,enterTime);
            }
        }
    }
    //change number
    public void DeleteCnt(String value){
        //Get current time
        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyymmddhhmm");
        String leaveTime = dateFormat.format(date);
        SimpleDateFormat argformat = new SimpleDateFormat("hhmmss");
        String arg = argformat.format(date);
        int leavearg = Integer.parseInt(arg);
        //Get the reduced amount
        int num = Integer.parseInt(value);
        if(Integer.parseInt(jedis.get(key)) == 0){
            System.out.println("Now the number is 0, so we can't reduce it");
        }
        else {
            for(int i = 0;i < num; i++){
                jedis.decr(key);
                jedis.rpush("leaveList", leaveTime);
                jedis.sadd("leaveSet", leaveTime);
                jedis.zadd("leaveZset",leavearg,leaveTime);
            }
        }
    }

    //Get the change in the number over a period of time
    public void ShowFreq(String stime){

        String[] str = stime.split(" ");
        String start = str[0];
        String end = str[1];
        List<String> enter = jedis.lrange("enterList",0,-1);
        List<String> leave = jedis.lrange("leaveList",0,-1);
        if(enter.size() != 0){
            long enter1 = Long.valueOf(start), enter2 = Long.valueOf(end);
            int entertotal = 0;
            long elem;
            for(int i = 0; i < enter.size(); i++){
                elem = Long.valueOf(enter.get(i));
                if(elem >= enter1 && elem <= enter2){
                    entertotal++;
                }
            }
            System.out.println("Number of addition during the period " + stime + ":" + entertotal);
        }
        else{
            System.out.println("So far it has not added");
        }

        if(leave.size() != 0){
            long leave1 = Long.valueOf(start), leave2 = Long.valueOf(end), elem;
            int leavetotal = 0;
            for(int i = 0;i < leave.size(); i++){
                elem = Long.valueOf(leave.get(i));
                if(elem >= leave1 && elem <= leave2){
                    leavetotal++;
                }
            }
            System.out.println("Number of reduction during the period" + stime + ":" + leavetotal);
        }
        else{
            System.out.println("So far it has not left");
        }
    }

    //Get and display the list
    public void ShowList(String listkey){
        List<String> list = jedis.lrange(listkey,0,-1);
        if(list.size() == 0){
            System.out.println(listkey + "No content");
        }
        else{
            System.out.println(listkey + "The contents of the file are:");
            for(int i = 0; i < list.size(); i ++) {
                System.out.println(list.get(i));
            }
        }
    }
    //Get and display the set
    public void ShowSet(String setkey){
        Set<String> set = jedis.smembers(setkey);
        if(set.size() == 0){
            System.out.println(setkey + "No content");
        }
        else{
            System.out.println(setkey + "The contents of the file are:");
            System.out.println(set);
        }
    }
    //Get and display the z-set
    public void ShowZ_set(String z_setkey){
        Set<String> z_set = jedis.zrange(z_setkey,0,-1);
        if(z_set.size() == 0){
            System.out.println(z_setkey + "No content");
        }
        else{
            System.out.println(z_setkey + "The contents of the file are:");
            System.out.println(z_set);
        }
    }

    public HashMap<String,HashMap> getAction(){
        return action;
    }

    public HashMap<String,HashMap> getCounter(){
        return counter;
    }

}