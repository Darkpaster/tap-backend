package com.human.tapMMO;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
@EntityScan(basePackages = "com.human.tapMMO.model")
public class TapMmoApplication {
//	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	public static void main(String[] args) {
		SpringApplication.run(TapMmoApplication.class, args);
	}

//	@Autowired
//	UserRepository repository;


//	@Override
//	public void run(String... args) throws Exception {
//
//		logger.info("Student entityId 10001 -> {}", repository.findById(10001L));
//
//		logger.info("All users 1 -> {}", repository.findAll());
//
//		//Insert
//		logger.info("Inserting -> {}", repository.save(new Student("John", "A1234657")));
//
//		//Update
//		logger.info("Update 10001 -> {}", repository.save(new Student(10001L, "Name-Updated", "New-Passport")));
//
//		repository.deleteById(10002L);
//
//		logger.info("All users 2 -> {}", repository.findAll());
//	}



}
