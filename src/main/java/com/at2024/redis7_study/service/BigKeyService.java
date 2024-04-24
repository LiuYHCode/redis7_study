package com.at2024.redis7_study.service;

import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.exceptions.JedisException;
import redis.clients.jedis.params.ScanParams;
import redis.clients.jedis.resps.ScanResult;

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
        jedis.auth(password);

        //分批删除
        try {
            //每次删除100条
            ScanParams scanParams = new ScanParams().count(100);
            String cursor = "";
            while (!cursor.equals("0")){
                ScanResult<Map.Entry<String, String>> scanResult = jedis.hscan(bigHashKey, cursor, scanParams);
                List<Map.Entry<String, String>> entryList = scanResult.getResult();

                long t1 = System.currentTimeMillis();
                if (CollectionUtils.isNotEmpty(entryList)) {
                    for (Map.Entry<String, String> entry : entryList) {
                        jedis.hdel(bigHashKey, entry.getKey());
                    }
                }
                long t2 = System.currentTimeMillis();

//                LOGGER.info("删除"+ entryList.size() + "条数据，耗时: " + (t2-t1) + "毫秒,cursor:" + cursor);
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
}
