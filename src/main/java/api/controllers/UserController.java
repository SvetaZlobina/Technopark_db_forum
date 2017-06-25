package api.controllers;

import api.models.UserModel;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.web.bind.annotation.*;
import api.daoFiles.DaoUser;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.StringWriter;
import java.util.List;
import java.util.logging.Logger;


@RestController
@RequestMapping(path = "/api/user/{nickname}")
public class UserController {

    private static final ObjectMapper objectMapper = new ObjectMapper()
            .enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);


    @Autowired
    private DaoUser daoUser;

    private static final Logger log = Logger.getLogger(UserController.class.getName());


    @GetMapping(path = "/profile")
    public UserModel getUserData(@PathVariable("nickname") final String nickname,
                                 final HttpServletResponse response) {
        try {

            final UserModel userModel = daoUser.getUser(nickname);
            response.setStatus(HttpServletResponse.SC_OK);
            return userModel;

        } catch (DataAccessException e) {

            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return null;
        }
    }

    @PostMapping(path = "/profile")
    public UserModel updateUserData(@PathVariable("nickname") final String nickname,
                                    @RequestBody final UserModel dataToUpdate,
                                    final HttpServletResponse response) {

        dataToUpdate.setNickname(nickname);
        try {

            try {
                daoUser.getUser(nickname);
            } catch (DataAccessException e) {

                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                return null;
            }

            daoUser.updateUser(dataToUpdate);
            response.setStatus(HttpServletResponse.SC_OK);
            return daoUser.getUser(nickname);

        } catch (DataAccessException e) {

            response.setStatus(HttpServletResponse.SC_CONFLICT);
            return null;
        }
    }

    @PostMapping(path = "/create")
    public String createUser(@RequestBody final UserModel userModel,
                             @PathVariable("nickname") final String nickname,
                             final HttpServletResponse response) throws IOException {
        try {

            final int id = daoUser.createUser(nickname, userModel.getFullname(), userModel.getEmail(), userModel.getAbout());
            response.setStatus(HttpServletResponse.SC_CREATED);

            userModel.setNickname(nickname);
            userModel.setId(id);
            return userModel.convertToObjectNode(objectMapper).toString();

        } catch (DataAccessException e) {

            response.setStatus(HttpServletResponse.SC_CONFLICT);

            final List<UserModel> oldUserModels = daoUser.getUserList(nickname, userModel.getEmail());

            final StringWriter sw = new StringWriter();
            objectMapper.writeValue(sw, oldUserModels);

            return sw.toString();
        }
    }
}


