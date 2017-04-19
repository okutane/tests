package ru.urururu.tests.revolut;

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Test;

import javax.ws.rs.core.Application;
import javax.ws.rs.core.GenericType;
import java.util.Collection;
import java.util.Collections;

import static org.junit.Assert.assertEquals;

/**
 * @author <a href="mailto:dmitriy.g.matveev@gmail.com">Dmitry Matveev</a>
 */
public class AccountServiceTests extends JerseyTest {
    @Test
    public void test() {
        Collection<Account> accounts = target("api/v1/accounts").request().get(new GenericType<>(Collection.class));
        assertEquals(Collections.emptyList(), accounts);
    }

    @Override
    protected Application configure() {
        return new ResourceConfig(AccountService.class);
    }
}
