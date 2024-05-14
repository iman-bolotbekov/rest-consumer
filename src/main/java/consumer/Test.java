package consumer;

import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;


@PropertySource("classpath:project.properties")
public class Test {
    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(Test.class);
        Environment env = context.getEnvironment();
        String URL = env.getProperty("URL");
        RestTemplate restTemplate = new RestTemplate();
        String sessionId = restTemplate.headForHeaders(URL).getFirst("Set-Cookie");
        HttpClient httpClient = HttpClientBuilder.create().build();
        restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(httpClient));

        String listOfUsers = restTemplate.getForObject(URL, String.class);
        System.out.println("List of users: " + listOfUsers);

        restTemplate.getInterceptors().add((request, body, execution) -> {
            request.getHeaders().set("Cookie", sessionId);
            return execution.execute(request, body);
        });

        Map<String, Object> json1 = new HashMap<>();
        json1.put("id", 3);
        json1.put("name", "James");
        json1.put("lastName", "Brown");
        json1.put("age", 30);
        HttpEntity<Map<String, Object>> request1 = new HttpEntity<>(json1);
        String response1 = restTemplate.postForObject(URL, request1, String.class);
        System.out.println("Response1: " + response1);

        Map<String, Object> json2 = new HashMap<>();
        json2.put("id", 3);
        json2.put("name", "Thomas");
        json2.put("lastName", "Shelby");
        json2.put("age", 30);
        HttpEntity<Map<String, Object>> request2 = new HttpEntity<>(json2);
        ResponseEntity<String> responseEntity2 = restTemplate.exchange(URL, HttpMethod.PUT, request2, String.class);
        String response2 = responseEntity2.getBody();
        System.out.println("Response2: " + response2);

        ResponseEntity<String> responseEntity3 = restTemplate.exchange(URL + "/3", HttpMethod.DELETE, null, String.class);
        String response3 = responseEntity3.getBody();
        System.out.println("Response3: " + response3);

        String resultCode = response1 + response2 + response3;
        System.out.println("Result code: " + resultCode);
        System.out.println("Result code size: " + resultCode.length());
        context.close();
    }
}
