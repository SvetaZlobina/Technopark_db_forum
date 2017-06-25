package api.controllers;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import api.daoFiles.ServiceDAO;
import api.models.Status;

@RestController
public class ServiceController {
    @Autowired
    private ServiceDAO serviceDAO;

    @PostMapping("api/service/clear")
    public void clearDB() {
        serviceDAO.clear();
    }

    @GetMapping("api/service/status")
    public Status getDBStatus() {
        return serviceDAO.getStatus();
    }
}
