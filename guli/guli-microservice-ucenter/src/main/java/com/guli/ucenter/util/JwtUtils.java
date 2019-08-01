package com.guli.ucenter.util;

import com.guli.ucenter.entity.Member;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.Date;

/**
 * @author helen
 * @since 2019/7/30
 */
public class JwtUtils {

	//秘钥
	public static final String APP_SECRET = "tCtxdBOQThGBUg8D1bfrbeazBV7nhT";

	//过期时间
	public static final long EXPIRE = 60 *30 * 1000;

	//令牌主题
	public static final String SUBJECT = "guli-user";

	/**
	 * 生成Jwt令牌
	 * @param member
	 * @return
	 */
	public static String genJWT(Member member){

		String token = Jwts.builder()
		//1、JWT头
		.setHeaderParam("alg", "HS256")
		.setHeaderParam("typ", "JWT")

		//2、有效载荷
		//预定义（默认）
		.setSubject(SUBJECT)//令牌主题
		.setIssuedAt(new Date())//令牌颁发时间
		.setExpiration(new Date(System.currentTimeMillis() + EXPIRE))//令牌的过期时间

		//自定义
		.claim("id", member.getId())
		.claim("nickname", member.getNickname())
		.claim("avatar", member.getAvatar())

		//3、签名哈希
		.signWith(SignatureAlgorithm.HS256, APP_SECRET).compact();

		return token;
	}

	public static Claims checkJwt(String jwtToken){

		Jws<Claims> claimsJws = Jwts.parser().setSigningKey(APP_SECRET).parseClaimsJws(jwtToken);
//		String signature = claimsJws.getSignature();
//		JwsHeader header = claimsJws.getHeader();
		Claims claims = claimsJws.getBody();

		return claims;
	}
}
