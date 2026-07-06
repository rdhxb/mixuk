package rdhxb.mixuk;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import rdhxb.mixuk.collectData.GetData;
import rdhxb.mixuk.repo.MixRepo;


@SpringBootApplication
@EnableScheduling
public class MixukApplication {

	public static void main(String[] args) {
		SpringApplication.run(MixukApplication.class, args);
	}

	@Bean
	CommandLineRunner runDataCollector(GetData getData, MixRepo repo){
		return args -> {
			if (repo.count() == 0){
				getData.getData();
			}
		};

	}
}
