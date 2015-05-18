package org.sef4j.testwebapp.config;

import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes= { ApplicationConfig.class })
public abstract class AbstractSpringTestWebappTestCase {

}
