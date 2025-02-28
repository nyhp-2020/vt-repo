package com.cubixedu.vt.loadtest;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.client.RestClient;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@SpringBootApplication
public class LoadtestApplication implements CommandLineRunner {

	private final RestClient.Builder restClientBuilder;
	private RestClient restClient;
	private CountDownLatch latch;

	//kontruktor
	public LoadtestApplication(RestClient.Builder restClientBuilder){
		this.restClientBuilder = restClientBuilder;
	}


	public static void main(String[] args) {
		SpringApplication.run(LoadtestApplication.class, args);}
	//Alt + Enter metódus implmentálás
	@Override
	public void run(String... args) throws Exception {
		int numThreads = Integer.parseInt(args[0]);
		int numRequests = Integer.parseInt(args[1]);
		latch = new CountDownLatch(numRequests);

		String uri = args[2];
		this.restClient = restClientBuilder.baseUrl(uri).build(); //http://localhost:8080/api/callBlocking/2 (VtController hívás (vtdemo))

		ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();

		long start = System.currentTimeMillis(); //kezdő időpont
		for(int i = 0; i < numThreads; i++ ) {  //megadott számú szál
			executor.submit(() -> {
				while(true){ // minden szál annyi kérést indít, amennyit csak tud. De számoljuk,hány futott le.
					restClient.get().retrieve().toBodilessEntity();
					latch.countDown(); //ha teljesült a hívás, csökkentjük a számlálót
				}
			});
		}
		latch.await(); /* várakozás amíg az összes megadott számú kérés lefut,várumk amíg lecsökken 0-ra*/
		long duration = System.currentTimeMillis() - start; //futási idő meghatározása
		executor.shutdownNow(); //szálak leállítása (ha megvolt a kívánt számú kérés)
		System.out.println(duration);
	}
}
