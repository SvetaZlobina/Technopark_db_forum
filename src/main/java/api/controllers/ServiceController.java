package api.controllers;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import api.daoFiles.DaoService;
import api.models.StatusModel;

@RestController
@RequestMapping(path = "api/service")
public class ServiceController {

    @Autowired
    private DaoService daoService;

    @PostMapping("/clear")
    public void clearDB() {
        daoService.clearDB();
    }

    @GetMapping("/status")
    public StatusModel getDBStatus() {
        return daoService.getStatus();
    }
}
