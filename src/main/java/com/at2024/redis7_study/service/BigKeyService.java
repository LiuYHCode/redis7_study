package com.at2024.redis7_study.service;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.exceptions.JedisException;
import redis.clients.jedis.params.ScanParams;
import redis.clients.jedis.resps.ScanResult;
import redis.clients.jedis.resps.Tuple;

import java.util.List;
import java.util.Map;

/**
 * @author lyh
 * @date 2024/4/24  16:07
 */
@Service
public class BigKeyService {

    private static Logger LOGGER = LoggerFactory.getLogger(BigKeyService.class);

    public void delBigHash(String host, int port, String password, String bigHashKey) {
        Jedis jedis = new Jedis(host, port);
        if (StringUtils.isNotEmpty(password)) {
            jedis.auth(password);
        }

        //分批删除
        try {
            //每次删除100条
            ScanParams scanParams = new ScanParams().count(100);
            String cursor = "";
            while (!cursor.equals("0")){
                //这里的操作就和redis客户端登录之后，scan cursor MATCH * count 100一样
                ScanResult<Map.Entry<String, String>> scanResult = jedis.hscan(bigHashKey, cursor, scanParams);
                List<Map.Entry<String, String>> entryList = scanResult.getResult();

                long t1 = System.currentTimeMillis();
                if (CollectionUtils.isNotEmpty(entryList)) {
                    for (Map.Entry<String, String> entry : entryList) {
                        jedis.hdel(bigHashKey, entry.getKey());
                    }
                }
                long t2 = System.currentTimeMillis();
                LOGGER.info(String.format("删除%d条数据，耗时: %lf毫秒,cursor:%s", entryList.size(), (t2-t1), cursor));

                //返回0说明遍历完成
                cursor = scanResult.getCursor();
            }

            //删除bigKey
            jedis.del(bigHashKey);
        }catch (JedisException e){
            e.printStackTrace();
            LOGGER.error(e.getMessage(),e);
        }finally {
            if(jedis != null){
                jedis.close();
            }
        }
    }

    public void delBigList(String host, int port, String password, String bigListKey) {
        Jedis jedis = new Jedis(host, port);
        if (StringUtils.isNotEmpty(password)) {
            jedis.auth(password);
        }

        long llen = jedis.llen(bigListKey);
        int count = 0;
        int left = 100;
        while (count < llen) {
            //每次从左侧截掉100个
            jedis.ltrim(bigListKey, left, llen);
            count += left;
        }

        //最终将key进行删除
        jedis.del(bigListKey);
    }

    public void delBigSet(String host, int port, String password, String bigSetKey) {
        Jedis jedis = new Jedis(host, port);
        if (StringUtils.isNotEmpty(password)) {
            jedis.auth(password);
        }

        //分批删除
        try {
            //每次删除100条
            ScanParams scanParams = new ScanParams().count(100);
            String cursor = "";
            while (!cursor.equals("0")){
                //这里的操作就和redis客户端登录之后，scan cursor MATCH * count 100一样
                ScanResult<String> scanResult = jedis.sscan(bigSetKey, cursor, scanParams);
                List<String> memberList = scanResult.getResult();

                long t1 = System.currentTimeMillis();
                if (CollectionUtils.isNotEmpty(memberList)) {
                    for (String member : memberList) {
                        jedis.srem(bigSetKey, member);
                    }
                }
                long t2 = System.currentTimeMillis();
                LOGGER.info(String.format("删除%d条数据，耗时: %lf毫秒,cursor:%s", memberList.size(), (t2-t1), cursor));

                //返回0说明遍历完成
                cursor = scanResult.getCursor();
            }

            //删除bigKey
            jedis.del(bigSetKey);
        }catch (JedisException e){
            e.printStackTrace();
            LOGGER.error(e.getMessage(),e);
        }finally {
            if(jedis != null){
                jedis.close();
            }
        }
    }

    public void delBigZset(String host, int port, String password, String bigZsetKey) {
        Jedis jedis = new Jedis(host, port);
        if (StringUtils.isNotEmpty(password)) {
            jedis.auth(password);
        }

        //分批删除
        try {
            //每次删除100条
            ScanParams scanParams = new ScanParams().count(100);
            String cursor = "";
            while (!cursor.equals("0")){
                //这里的操作就和redis客户端登录之后，scan cursor MATCH * count 100一样
                ScanResult<Tuple> scanResult = jedis.zscan(bigZsetKey, cursor, scanParams);
                List<Tuple> tupleList = scanResult.getResult();

                long t1 = System.currentTimeMillis();
                if (CollectionUtils.isNotEmpty(tupleList)) {
                    for (Tuple tuple : tupleList) {
                        jedis.zrem(bigZsetKey, tuple.getElement());
                    }
                }
                long t2 = System.currentTimeMillis();
                LOGGER.info(String.format("删除%d条数据，耗时: %lf毫秒,cursor:%s", tupleList.size(), (t2-t1), cursor));

                //返回0说明遍历完成
                cursor = scanResult.getCursor();
            }

            //删除bigKey
            jedis.del(bigZsetKey);
        }catch (JedisException e){
            e.printStackTrace();
            LOGGER.error(e.getMessage(),e);
        }finally {
            if(jedis != null){
                jedis.close();
            }
        }
    }

}
