package br.com.cobello.mtls.twowayssl;

import java.security.KeyStore;

import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustAllStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@RestController
public class TwoWaySslApplication {

	public static void main(final String[] args) {
		SpringApplication.run(TwoWaySslApplication.class, args);
	}

	@GetMapping("/")
	public String hello() {
		return sslConnection();
	}

	private String sslConnection() {
		try {
			final RestTemplate rt = new RestTemplate();
			final ClassPathResource resource = new ClassPathResource("chave2.p12");

			final KeyStore ks = KeyStore.getInstance("PKCS12");
			ks.load(resource.getInputStream(), "".toCharArray());

			final SSLConnectionSocketFactory ssl = new SSLConnectionSocketFactory(new SSLContextBuilder()
					.loadTrustMaterial(null, new TrustAllStrategy()).loadKeyMaterial(ks, "".toCharArray()).build());

			final CloseableHttpClient cli = HttpClients.custom().setSSLSocketFactory(ssl).build();
			rt.setRequestFactory(new HttpComponentsClientHttpRequestFactory(cli));

			return rt.exchange("https://client.badssl.com/", HttpMethod.GET, null, String.class).getBody();
		} catch (final Exception e)
		{
			e.printStackTrace();
		}

		return "Fail";

	}

}
