package ru.urururu.tests.revolut;

import com.sun.jersey.simple.container.SimpleServerFactory;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.junit.runners.model.Statement;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;

/**
 * @author <a href="mailto:dmitriy.g.matveev@gmail.com">Dmitry Matveev</a>
 */
public class AccountITCase {
    @Rule
    public TestRule server = (base, description) -> new Statement() {
        @Override
        public void evaluate() throws Throwable {
            AccountRepository.getInstance().reset();
            try (Closeable server = SimpleServerFactory.create("http://localhost:5555")) {
                base.evaluate();
            }
        }
    };

    @Test
    public void testInitialAccounts() throws IOException {
        try (CloseableHttpClient client = HttpClients.createMinimal()) {
            String result = get(client, "http://localhost:5555/api/v1/accounts");
            assertEquals("[]", result);
        }
    }

    @Test
    public void testAddAccount() throws IOException {
        try (CloseableHttpClient client = HttpClients.createMinimal()) {
            String result = post(client, "http://localhost:5555/api/v1/accounts");
            assertEquals("{\"id\":1,\"balance\":0}", result);
        }
    }

    @Test
    public void testMakeTransfer() throws IOException {
        try (CloseableHttpClient client = HttpClients.createMinimal()) {
            post(client, "http://localhost:5555/api/v1/accounts"); // add account 1
            put(client, "http://localhost:5555/api/v1/accounts/1/balance", nvp("value", "20"));
            post(client, "http://localhost:5555/api/v1/accounts"); // add account 2
            post(client, "http://localhost:5555/api/v1/accounts/1/transfers",
                    nvp("to", "2"), nvp("amount", "10"));

            String result = get(client, "http://localhost:5555/api/v1/accounts");
            assertEquals("[{\"id\":1,\"balance\":10},{\"id\":2,\"balance\":10}]", result);
        }
    }

    private NameValuePair nvp(String name, String value) {
        return new BasicNameValuePair(name, value);
    }

    private String get(CloseableHttpClient client, String getUri) throws IOException {
        try (CloseableHttpResponse response = client.execute(new HttpGet(getUri))) {
            assertStatus(response.getStatusLine(), 200);

            return bodyToString(response);
        }
    }

    private String post(CloseableHttpClient client, String postUri, NameValuePair... parameters) throws IOException {
        HttpPost request = new HttpPost(postUri);
        request.setEntity(new UrlEncodedFormEntity(Arrays.asList(parameters)));

        try (CloseableHttpResponse response = client.execute(request)) {
            assertStatus(response.getStatusLine(), 200);

            return bodyToString(response);
        }
    }

    private void put(CloseableHttpClient client, String putUri, NameValuePair... parameters) throws IOException {
        HttpPut request = new HttpPut(putUri);
        request.setEntity(new UrlEncodedFormEntity(Arrays.asList(parameters)));

        try (CloseableHttpResponse response = client.execute(request)) {
            assertStatus(response.getStatusLine(), 204);
        }
    }

    private void assertStatus(StatusLine statusLine, int expected) {
        assertEquals(statusLine.getReasonPhrase(), expected, statusLine.getStatusCode());
    }

    private String bodyToString(CloseableHttpResponse response) throws IOException {
        return new BufferedReader(new InputStreamReader(response.getEntity().getContent()))
                .lines().collect(Collectors.joining());
    }
}
