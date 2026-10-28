package com.dong.service;

import com.dong.common.RedisCache;
import com.dong.common.constant.Constants;
import com.dong.domain.LoginUser;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.crypto.SecretKey;
import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * token 生成 / 解析 / 续期。JWT 里只放一个 uuid，真正的会话信息存 Redis（若依风格）。
 */
@Service
public class TokenService implements InitializingBean {

    /** 距离过期不足 20 分钟时自动续期 */
    private static final long REFRESH_THRESHOLD = 20 * 60 * 1000L;
    private static final String CLAIM_UUID = "uuid";

    @Autowired
    private RedisCache redisCache;

    @Value("${token.header:Authorization}")
    private String header;

    @Value("${token.secret:dong445}")
    private String secret;

    @Value("${token.expireTime:30}")
    private int expireTime;

    private SecretKey key;

    @Override
    public void afterPropertiesSet() throws Exception {
        // token.secret 很短（dong445），用 SHA-256 摘要出 32 字节，再作为 HS256 密钥
        byte[] digest = MessageDigest.getInstance("SHA-256")
                .digest(secret.getBytes(StandardCharsets.UTF_8));
        this.key = Keys.hmacShaKeyFor(digest);
    }

    /** 生成 token，并把登录用户写入 Redis */
    public String createToken(LoginUser loginUser) {
        String uuid = UUID.randomUUID().toString().replace("-", "");
        loginUser.setToken(uuid);

        String token = Jwts.builder()
                .claim(CLAIM_UUID, uuid)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        refreshToken(loginUser);
        return token;
    }

    /** 把登录用户（含过期时间）写入 Redis */
    public void refreshToken(LoginUser loginUser) {
        loginUser.setLoginTime(System.currentTimeMillis());
        loginUser.setExpireTime(loginUser.getLoginTime() + expireTime * 60 * 1000L);
        redisCache.setLoginUser(Constants.LOGIN_USER_KEY + loginUser.getToken(),
                loginUser, expireTime, TimeUnit.MINUTES);
    }

    /** 从请求头解析 token，还原登录用户 */
    public LoginUser getLoginUser(HttpServletRequest request) {
        String token = getToken(request);
        if (StringUtils.isEmpty(token)) {
            return null;
        }
        try {
            Claims claims = Jwts.parserBuilder().setSigningKey(key).build()
                    .parseClaimsJws(token).getBody();
            String uuid = (String) claims.get(CLAIM_UUID);
            return redisCache.getLoginUser(Constants.LOGIN_USER_KEY + uuid);
        } catch (JwtException | IllegalArgumentException e) {
            return null;
        }
    }

    /** 临近过期时续期 */
    public void verifyToken(LoginUser loginUser) {
        long diff = loginUser.getExpireTime() - System.currentTimeMillis();
        if (diff <= REFRESH_THRESHOLD) {
            refreshToken(loginUser);
        }
    }

    /** 退出登录：删除 Redis 里的会话 */
    public void deleteLoginUser(HttpServletRequest request) {
        LoginUser loginUser = getLoginUser(request);
        if (loginUser != null) {
            redisCache.deleteObject(Constants.LOGIN_USER_KEY + loginUser.getToken());
        }
    }

    private String getToken(HttpServletRequest request) {
        String token = request.getHeader(header);
        if (!StringUtils.isEmpty(token) && token.startsWith(Constants.TOKEN_PREFIX)) {
            token = token.substring(Constants.TOKEN_PREFIX.length());
        }
        return token;
    }
}
