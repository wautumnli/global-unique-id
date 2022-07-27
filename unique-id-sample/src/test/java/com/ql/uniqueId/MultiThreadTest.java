package com.ql.uniqueId;

import com.ql.uniqueId.dao.InfoDao;
import com.ql.uniqueId.domain.Info;
import com.ql.uniqueId.service.IdService;
import com.ql.uniqueId.utils.IdUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.concurrent.CountDownLatch;

/**
 * @author wanqiuli
 * @date 2022/7/27 12:49
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = UniqueController.class)
public class MultiThreadTest {

    @Resource
    private IdService idService;
    @Resource
    private InfoDao infoDao;

    @Test
    public void testMultiThread() {
        long begin = System.currentTimeMillis();
        int num = 10;
        CountDownLatch countDownLatch = new CountDownLatch(num);
        for (int i = 0; i < num; i++) {
            Thread t = new Thread(() -> {
                String threadName = Thread.currentThread().getName();
                log.info(threadName + " started");
                for (int j = 0; j < 1000; j++) {
                    Long nextId = idService.getNextId("bas_info");
                    Info info = new Info(nextId, threadName);
                    infoDao.insert(info);
                    log.info("insert " + nextId);
                }
                countDownLatch.countDown();
            });
            t.start();
        }
        try {
            countDownLatch.await();
            long end = System.currentTimeMillis();
            log.info("time: {}", end - begin);
            log.warn("finished");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
