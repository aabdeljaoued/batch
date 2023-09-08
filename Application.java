import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;

@SpringBootApplication
@EnableConfigurationProperties({ MongoConfigProperties.class })
public class YourApplication {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private MongoConfigProperties mongoConfigProperties;

    public static void main(String[] args) {
        SpringApplication.run(YourApplication.class, args);
    }

    @Bean
    @ConfigurationProperties(prefix = "spring.data.mongodb")
    public MongoConfigProperties mongoConfigProperties() {
        return new MongoConfigProperties();
    }

    @Autowired
    private ObjectMapper objectMapper;

    @Component
    public class DataLoader {

        @Autowired
        public DataLoader(MongoTemplate mongoTemplate, MongoConfigProperties mongoConfigProperties) {
            try {
                File jsonFile = new File("src/main/resources/data.json"); // Update the path accordingly
                DataObject[] dataObjects = objectMapper.readValue(jsonFile, DataObject[].class);

                for (DataObject dataObject : dataObjects) {
                    mongoTemplate.save(dataObject, mongoConfigProperties.getCollectionName());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
