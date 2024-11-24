package alstom.rms.springboot;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class RailwayManagementSystemApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(RailwayManagementSystemApplication.class, args);
    }

    @Autowired
    private TestData testData;

    @Override
    public void run(String... args) throws Exception {
        testData.setup();
    }

}
