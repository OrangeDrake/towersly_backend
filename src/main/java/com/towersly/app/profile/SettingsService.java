package com.towersly.app.profile;

import org.springframework.stereotype.Service;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@AllArgsConstructor
public class SettingsService {

    private UserDAO userDAO;
    private UserService userService;

    public int getNumberOfVisibleWorks() {
        int userId = userService.getUserId();
        return userDAO.readNumberOfVisibleWorks(userId);
    }

    public void setNumberOfVisibleWorks(int numberOfVisibleWorks) {
        int userId = userService.getUserId();
        userDAO.updateNumberOfVisibleWorks(userId, numberOfVisibleWorks);
    }

}
