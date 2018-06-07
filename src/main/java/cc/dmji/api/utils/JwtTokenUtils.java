package cc.dmji.api.utils;

import cc.dmji.api.security.JwtUser;
import cc.dmji.api.enums.UserStatus;
import io.jsonwebtoken.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static cc.dmji.api.constants.SecurityConstants.*;
import static cc.dmji.api.constants.SecurityConstants.TOKEN_CLAIM_KEY_LOCK;

/**
 * Created by echisan on 2018/5/16
 */
@Component
public class JwtTokenUtils {

    private static final Logger logger = LoggerFactory.getLogger(JwtTokenUtils.class);

    /**
     * 生成token
     * @param claims
     * @param subject
     * @param rememberMe
     * @return
     */
    public String createToken(Map<String, Object> claims, String subject, boolean rememberMe) {
        return doCreateToken(claims, subject, rememberMe);
    }

    /**
     * 通过用户名生成token
     *
     * @param subject
     * @return
     */
    public String createToken(String subject) {
        return doCreateToken(new HashMap<>(),subject,false);
    }

    /**
     * 通过jwtUser生成token
     *
     * @param user
     * @param rememberMe
     * @return
     */
    public String createToken(JwtUser user, boolean rememberMe) {
        Map<String, Object> claims = new HashMap<>();
        Collection<? extends GrantedAuthority> grantedAuthorities = user.getAuthorities();
        String role = "";
        for (GrantedAuthority grantedAuthority : grantedAuthorities) {
            role = grantedAuthority.getAuthority();
        }
        claims.put(TOKEN_CLAIM_KEY_ROLE, role);
        claims.put(TOKEN_CLAIM_KEY_EMAIL, user.getIsEmailVerify());
        claims.put(TOKEN_CLAIM_KEY_LOCK, user.getIsLock());
        claims.put(TOKEN_CLAIM_KEY_UID, user.getId());
        return doCreateToken(claims, user.getUsername(), rememberMe);
    }

    /**
     * 生成token
     *
     * @param claimsMap
     * @param username
     * @param rememberMe
     * @return
     */
    private String doCreateToken(Map<String, Object> claimsMap, String username, boolean rememberMe) {

        return Jwts.builder().signWith(SignatureAlgorithm.HS512, TOKEN_SECRET)
                .setHeaderParam("typ","JWT")
                .setClaims(claimsMap)
                .setSubject(username)
                .setIssuedAt(new Date())
                .setIssuer(TOKEN_ISSUER)
                .setExpiration(getExpireDate(rememberMe ? TOKEN_REMEMBER_ME_EXPIRATION : TOKEN_EXPIRATION))
                .compact();

    }


    /**
     * 获取用户名
     * @param token
     * @return
     */
    public String getUsername(String token) {
        return getClaim(token).getSubject();
    }

    /**
     * 判断token是否过期
     *
     * @param token token
     * @return 过期则返回true
     */
    public Boolean isExpiration(String token) {
        return getClaim(token).getExpiration().after(new Date());
    }

    /**
     * 获取过期时间
     *
     * @param token token
     * @return 过期时间
     */
    public Date getExpirationDate(String token) {
        return getClaim(token).getExpiration();
    }

    public Claims getClaim(String token) {
        return Jwts.parser()
                .setSigningKey(TOKEN_SECRET)
                .parseClaimsJws(token)
                .getBody();
    }

    public String getIssuer(String token) {
        return getClaim(token).getIssuer();
    }

    public String getUid(String token){
        return (String) getClaim(token).get(TOKEN_CLAIM_KEY_UID);
    }

    /**
     * 验证token
     * @param token
     * @return
     */
    public Boolean validateToken(String token) {
        Payload payload = getPayload(token);
        if (payload.getUsername()==null){
            return false;
        }
        if (isTokenExpiration(payload.getClaims().getExpiration().getTime())){
            return false;
        }
        return true;
    }

    /**
     * 判断token是否能解析
     *
     * @param token
     * @return
     * @throws SignatureException
     */
    public boolean canTokenParse(String token) throws UnsupportedJwtException, SignatureException {
        try {
            Jwts.parser().setSigningKey(TOKEN_SECRET).parseClaimsJws(token);
            return true;
        } catch ( UnsupportedJwtException
                | MalformedJwtException | IllegalArgumentException e) {
            logger.info("token不合法，解析失败,原因:{}",e.getMessage());
            return false;
        } catch (ExpiredJwtException e){
            logger.info("token已过期,{}",e.getMessage());
            return false;
        }
    }


    /**
     * 获取颁发token的时间
     *
     * @param token token
     * @return 颁发token的时间
     */
    public Date getIssuerAt(String token) {
        return getClaim(token).getIssuedAt();
    }

    private Date getExpireDate(Long expiration) {
        return new Date(System.currentTimeMillis()+ expiration * 1000);
    }

    /**
     * token是否过期
     *
     * @param expiration 过期时间
     * @return true则过期
     */
    private boolean isTokenExpiration(Long expiration) {
        return System.currentTimeMillis()>expiration;
    }


    public Payload getPayload(String token) {

        try {
            return new Payload(token);
        } catch (UnsupportedJwtException e) {
            e.printStackTrace();
            logger.info("该token有问题，解析不到");
            return null;
        }
    }

    /**
     * token是否能刷新
     *
     * @param token token
     * @return
     */
    public boolean canTokenFresh(String token) {
        return !isExpiration(token);
    }

    /**
     * 刷新密钥
     *
     * @param token
     * @return
     */
    public String refreshToken(String token) {
        Claims claims = getClaim(token);
        claims.setExpiration(new Date());
        return doCreateToken(claims,claims.getSubject(),false);
    }


    /**
     * 包装一下
     */
    public class Payload {
        String token;
        String role;
        boolean isLock;
        boolean isEmailVerify;
        Claims claims;

        public Payload(String token) {
            this.token = token;
            this.claims = getClaim(token);
            init();
        }

        private void init() {
            role = (String) claims.get(TOKEN_CLAIM_KEY_ROLE);
            int lock = (Integer) claims.get(TOKEN_CLAIM_KEY_LOCK);
            int email = (Integer) claims.get(TOKEN_CLAIM_KEY_EMAIL);
            isLock = UserStatus.LOCK.getStatus().equals(lock);
            isEmailVerify = UserStatus.EMAIL_VERIFY.getStatus().equals(email);
        }

        public String getRole() {
            return role;
        }

        public boolean isLock() {
            return isLock;
        }

        public boolean isEmailVerify() {
            return isEmailVerify;
        }

        public Claims getClaims() {
            return claims;
        }

        public String getUsername() {
            return claims.getSubject();
        }

        @Override
        public String toString() {
            return "Payload{" +
                    "token='" + token + '\'' +
                    ", role='" + role + '\'' +
                    ", isLock=" + isLock +
                    ", isEmailVerify=" + isEmailVerify +
                    ", claims=" + claims +
                    '}';
        }
    }


}
