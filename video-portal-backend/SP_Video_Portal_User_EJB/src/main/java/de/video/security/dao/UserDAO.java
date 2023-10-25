/* (C)2023 */
package de.video.security.dao;

import de.video.security.entity.User;
import java.util.HashMap;
import java.util.Map;

public class UserDAO extends GenericDAO<User> {

    public UserDAO() {
        super(User.class);
    }

    public User findUserByName(String username) {
        Map<String, Object> parameters = new HashMap<String, Object>();
        System.out.println("Username: " + username);
        parameters.put("username", username);

        return super.findOneResult(User.FIND_BY_NAME, parameters);
    }
}
