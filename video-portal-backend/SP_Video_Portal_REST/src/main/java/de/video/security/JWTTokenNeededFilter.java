/* (C)2023 */
package de.video.security;

import de.video.jwt.KeyGenerator;
import de.video.jwt.LogbackLogger;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.annotation.Priority;
import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.NotAuthorizedException;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.ResourceInfo;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;
import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Provider
@JWTTokenNeeded
@Priority(Priorities.AUTHENTICATION)
@SessionScoped
public class JWTTokenNeededFilter implements Serializable, ContainerRequestFilter {

    /**
     *
     */
    private static final long serialVersionUID = -3434364851812214865L;

    @Inject @LogbackLogger private transient Logger logger;

    @Inject private KeyGenerator keyGenerator;

    @Context private ResourceInfo resourceInfo;

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {

        String authorizationHeader = requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);
        logger.info("### authorizationHeader: " + authorizationHeader);

        String token = authorizationHeader.substring("Bearer".length()).trim();
        byte[] key = keyGenerator.generateKey();

        /* correct authorization header ???? */
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer")) {
            logger.severe("### fehlerhafter authorizationHeader: " + authorizationHeader);
            throw new NotAuthorizedException("Authorization Header muss unterstützt werden");
        }

        /* correct role permissions for rest end point */
        try {
            Claims claims =
                    Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
            Method method = resourceInfo.getResourceMethod();
            if (method != null) {
                JWTTokenNeeded JWTContext = method.getAnnotation(JWTTokenNeeded.class);
                Role permission = JWTContext.Permissions();
                logger.info("### permissions: " + permission.toString());

                if (permission != Role.NoRights) {

                    String roles = claims.get("Roles", String.class);
                    roles = roles.replace("{", "").replace("}", "");
                    roles = roles.replace("[", "").replace("]", "");
                    logger.info("roles: " + roles);

                    List<String> roleList =
                            Stream.of(roles.split(","))
                                    .map(String::trim)
                                    .collect(Collectors.toList());
                    // List<String> roleList = Arrays.asList(roles.split("[,\\s]+"));

                    List<Role> userRoles = new ArrayList<Role>();
                    for (String aStr : roleList) {
                        userRoles.add(Role.valueOf(aStr));
                    }

                    if (!userRoles.contains(permission)) {
                        throw new Exception("keine Rechte an diesem Request");
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).build());
        }

        /* token valid or expired */
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            logger.info("### korrektes token: " + token);
        } catch (Exception e) {
            logger.info("### token abgelaufen: " + token);
            requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).build());
        }
    }
}
