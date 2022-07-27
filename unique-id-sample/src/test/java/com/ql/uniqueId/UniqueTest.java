package com.ql.uniqueId;

import com.ql.uniqueId.dao.InfoDao;
import com.ql.uniqueId.dao.TextDao;
import com.ql.uniqueId.domain.Info;
import com.ql.uniqueId.domain.Text;
import com.ql.uniqueId.service.IdService;
import com.ql.uniqueId.utils.IdUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.StopWatch;

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

    @Resource
    private TextDao textDao;

    @Test
    public void uniqueIdTest() {
        long begin = System.currentTimeMillis();
        for (int i = 0; i < 10000; i++) {
            idService.getNextId("test");
        }
        long end = System.currentTimeMillis();
        System.out.println(end - begin);
    }

    @Test
    public void insertData() {
        insertInfo();
        insertText();
    }

    @Test
    public void insertInfo() {
    }

    @Test
    public void insertText() {
        Text text = new Text();
        text.setId(IdUtils.getNextId("bas_text"));
        text.setText("text" + text.getId());
        textDao.insert(text);
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
