package cc.dmji.api.service.impl;

import cc.dmji.api.ApiApplicationTests;
import cc.dmji.api.entity.Episode;
import org.junit.Test;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.Assert.*;

@Transactional
@Rollback
public class EpisodeServiceImplTest extends ApiApplicationTests {
}
