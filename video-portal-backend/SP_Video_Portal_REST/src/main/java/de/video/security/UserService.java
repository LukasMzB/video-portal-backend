/* (C)2023 */
package de.video.security;

import static jakarta.ws.rs.core.HttpHeaders.AUTHORIZATION;
import static jakarta.ws.rs.core.Response.Status.UNAUTHORIZED;

import de.video.jwt.KeyGenerator;
import de.video.jwt.LogbackLogger;
import de.video.security.entity.User;
import de.video.security.facade.IUserFacade;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.logging.Logger;

@Path("/users")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Transactional
public class UserService {

    @Inject private IUserFacade userFacade;

    @Inject private KeyGenerator keyGenerator;

    @Context private UriInfo uriInfo;

    @Inject @LogbackLogger private Logger logger;

    /* Username und Password im Body des request --> sicherste Methode, da body idR ssl-Verschlüsselt
    *
    * curl -X POST http://localhost:8080/bankREST_v2-1.0/api/users/login3 \
    -H 'Accept: application/json' \
    -H 'Content-Type: application/json' \
    -d '{"username": "chef", "password": "chef"}'
    */

    @POST
    @Path("/login")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response authenticateUser3(Credentials credential) {

        System.out.println(
                "login called with login: "
                        + credential.getUsername()
                        + " password: "
                        + credential.getPassword());
        User user = null;
        try {
            user = authenticate(credential.getUsername(), credential.getPassword());
        } catch (Exception e) {
            return Response.status(UNAUTHORIZED).build();
        }
        String token = issueToken(user);
        logger.info("### token =" + token);
        return Response.ok().header(AUTHORIZATION, "Bearer " + token).build();
    }

    private User authenticate(String username, String password) throws Exception {

        User aUser = userFacade.findUserByName(username);
        System.out.println(
                "authenticate called with username: " + username + " password: " + password);
        System.out.println("Loggd in User=" + aUser.getUsername());

        PlainSHA512PasswordHash hashAlgo = new PlainSHA512PasswordHash();
        if (aUser.getPassword().equals(hashAlgo.generate(password.toCharArray()))) {
            logger.info("### korrekte Authentifizierung ### ");
            logger.info(aUser.toString());
            return aUser;
        } else {
            System.out.println(
                    "Falsches Kennwort: "
                            + aUser.getPassword()
                            + " uebergebener Wert:"
                            + hashAlgo.generate(password.toCharArray()));
            throw new SecurityException("Falscher Benutzername/Kennwort");
        }
    }

    @SuppressWarnings("deprecation")
    private String issueToken(User user) {
        byte[] secret = keyGenerator.generateKey();
        logger.info("key= " + secret.toString());

        String jws =
                Jwts.builder()
                        .setIssuer(uriInfo.getAbsolutePath().toString())
                        .setSubject(user.getUsername())
                        .claim("Roles", user.getRoles().toString())
                        .setIssuedAt(new Date())
                        .setExpiration(toDate(LocalDateTime.now().plusMinutes(15L)))
                        .signWith(SignatureAlgorithm.HS256, secret)
                        .compact();
        return jws;
    }

    private Date toDate(LocalDateTime localDateTime) {
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }
}
