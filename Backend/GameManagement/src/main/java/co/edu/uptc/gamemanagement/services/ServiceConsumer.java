package co.edu.uptc.gamemanagement.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ServiceConsumer {

    private RestTemplate restTemplate;


    @Autowired
    public ServiceConsumer(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public Boolean validateExistenceNickNameUser(String nickName) {
        return restTemplate.getForObject(String.format("http://PlayerManagement/User/Validate/%s", nickName), Boolean.class);
    }
}
