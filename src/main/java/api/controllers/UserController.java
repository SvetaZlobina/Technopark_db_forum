package api.controllers;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.web.bind.annotation.*;
import api.daoFiles.UserDAO;
import api.models.User;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.StringWriter;
import java.util.List;


@RestController
public class UserController {
    private static final ObjectMapper mapper = new ObjectMapper()
            .enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
//    private static final Logger LOGGER = Logger.getLogger(UserController.class.getName());

    @Autowired
    UserDAO userDAO;

    @GetMapping(value = "/api/user/{nickname}/profile")
    public User getUser(@PathVariable("nickname") String nickname, HttpServletResponse response) {
        try {
            final User user = userDAO.getUser(nickname);
            response.setStatus(HttpServletResponse.SC_OK);
            return user;
        } catch (DataAccessException e) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return null;
        }
    }

    @PostMapping(value = "/api/user/{nickname}/profile")
    public User update(@PathVariable("nickname") String nickname, @RequestBody User updateData, HttpServletResponse response) {
        updateData.setNickname(nickname);
        try {
            try {
                userDAO.getUser(nickname);
            } catch (DataAccessException e) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                return null;
            }

            userDAO.updateUser(updateData);
            response.setStatus(HttpServletResponse.SC_OK);
            return userDAO.getUser(nickname);
        } catch (DataAccessException e) {
            response.setStatus(HttpServletResponse.SC_CONFLICT);
            return null;
        }
    }

    @PostMapping(value = "/api/user/{nickname}/create")
    public String createUser(@RequestBody User user, @PathVariable("nickname") String nickname, HttpServletResponse response) throws IOException {
        try {
            final int id = userDAO.createUser(nickname, user.getFullname(), user.getEmail(), user.getAbout());
            response.setStatus(HttpServletResponse.SC_CREATED);

            user.setNickname(nickname);
            user.setId(id);
            return user.convertToObjectNode(mapper).toString();
        } catch (DataAccessException e) {
            response.setStatus(HttpServletResponse.SC_CONFLICT);

            final List<User> oldUsers = userDAO.getUserList(nickname, user.getEmail());

            final StringWriter sw = new StringWriter();
            mapper.writeValue(sw, oldUsers);

            return sw.toString();
        }
    }
}


