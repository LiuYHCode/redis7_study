package com.at2024.redis7_study;

import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnels;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Redis7StudyApplication.class)
public class BloomFilterTest {

    @Test
    public void guavaWithBloomFilterTest() {
        //1.创建guava布隆过滤器
        BloomFilter bloomFilter = BloomFilter.create(Funnels.integerFunnel(), 1000000);
        //2.判断指定的元素是否存在
        System.out.println(bloomFilter.mightContain(1));
        System.out.println(bloomFilter.mightContain(2));
        //3.添加测试数据
        bloomFilter.put(1);
        bloomFilter.put(2);
        System.out.println(bloomFilter.mightContain(1));
        System.out.println(bloomFilter.mightContain(2));
    }
}
