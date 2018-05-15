package cc.dmji.api.service.impl;

import cc.dmji.api.ApiApplicationTests;
import cc.dmji.api.entity.Bangumi;
import cc.dmji.api.service.BangumiService;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.List;
import java.util.function.Consumer;

import static org.junit.Assert.*;

@Transactional
@Rollback
public class BangumiServiceImplTest extends ApiApplicationTests {
}
