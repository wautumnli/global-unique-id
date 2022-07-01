package com.ql.uniqueId;

import com.ql.uniqueId.dao.InfoDao;
import com.ql.uniqueId.domain.Info;
import com.ql.uniqueId.service.IdService;
import com.ql.uniqueId.utils.IdUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

/**
 * @author wanqiuli
 * @date 2022/7/1 21:51
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class UniqueTest {

    @Resource
    private IdService idService;

    @Resource
    private InfoDao infoDao;

    @Test
    public void uniqueIdTest() {
        Long id = idService.getNextId("tb_test");
        System.out.println(id);
    }

    @Test
    public void insertData() {
        Info info = new Info();
        info.setId(IdUtils.getNextId("bas_info"));
        info.setName("test" + info.getId());
        infoDao.insert(info);
    }

    @Test
    public void createIdList() {
        long begin = System.currentTimeMillis();
        for (int i = 0; i < 1000000; i++) {
            IdUtils.getNextId("bas_info");
        }
        long end = System.currentTimeMillis();
        System.out.println(end - begin);
    }
}
