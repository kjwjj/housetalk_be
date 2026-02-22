package com.example.housetalk_be.auth.service;

import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class EmailAuthService {

    private final RedisTemplate<String, String> redisTemplate;
    private final JavaMailSender mailSender;

    private static final long CODE_EXPIRE = 180;

    // =========================
    // ✅ 회원가입 인증 코드 발송
    // =========================
    public void sendCode(String email) {
        String code = generateCode();

        // Redis 저장
        redisTemplate.opsForValue()
                .set("EMAIL_AUTH:" + email, code, CODE_EXPIRE, TimeUnit.SECONDS);

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, "UTF-8");

            helper.setTo(email);
            helper.setSubject("HouseTalk 회원가입 인증 코드");
            helper.setText("인증번호: " + code, false);

            // ⭐ 여기 핵심 (보낸 사람 이름)
            helper.setFrom(new InternetAddress(
                    "rhwjddn36@gmail.com",
                    "HouseTalk"
            ));

            // ⭐ HTML 메일로 변경
            helper.setText(buildHtml(code), true);

            mailSender.send(message);
        } catch (Exception e) {
            throw new RuntimeException("이메일 전송 실패", e);
        }
    }

    // =========================
// 🔑 임시 비밀번호 발송
// =========================
    public void sendTempPassword(String email, String tempPassword) {

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, "UTF-8");

            helper.setTo(email);
            helper.setSubject("HouseTalk 임시 비밀번호 안내");

            helper.setFrom(new InternetAddress(
                    "rhwjddn36@gmail.com",
                    "HouseTalk"
            ));

            helper.setText(buildTempPasswordHtml(tempPassword), true);

            mailSender.send(message);

        } catch (Exception e) {
            throw new RuntimeException("임시 비밀번호 이메일 전송 실패", e);
        }
    }

    private String buildHtml(String code) {
        return """
    <div style="font-family: Arial, sans-serif; background:#f4f6f8; padding:40px;">
      <div style="max-width:480px; margin:0 auto; background:#ffffff;
                  border-radius:12px; padding:32px; text-align:center;">

        <img src="https://raw.githubusercontent.com/kjwjj/kjwjj/main/ODUJEC0.jpg"
             alt="HouseTalk"
             style="width:120px; margin-bottom:24px;" />

        <h2 style="color:#222;">이메일 인증 코드</h2>

        <div style="
          display:inline-block;
          background:#e9f6ff;
          color:#007acc;
          font-size:28px;
          font-weight:bold;
          padding:16px 28px;
          border-radius:24px;
          margin:20px 0;
        ">
          """ + code + """
        </div>

        <p style="color:#555; font-size:14px;">
          인증 코드는 <b>3분 이내</b>에 입력해주세요.
        </p>

      </div>
    </div>
    """;
    }

    private String buildTempPasswordHtml(String tempPassword) {
        return """
    <div style="font-family: Arial, sans-serif; background:#f4f6f8; padding:40px;">
      <div style="max-width:480px; margin:0 auto; background:#ffffff;
                  border-radius:12px; padding:32px; text-align:center;">

        <img src="https://raw.githubusercontent.com/kjwjj/kjwjj/main/ODUJEC0.jpg"
             alt="HouseTalk"
             style="width:120px; margin-bottom:24px;" />

        <h2 style="color:#222;">임시 비밀번호 안내</h2>

        <p style="color:#555; font-size:14px;">
          아래 임시 비밀번호로 로그인 후 반드시 비밀번호를 변경해주세요.
        </p>

        <div style="
          display:inline-block;
          background:#ffe9e9;
          color:#d60000;
          font-size:24px;
          font-weight:bold;
          padding:16px 28px;
          border-radius:24px;
          margin:20px 0;
        ">
          """ + tempPassword + """
        </div>

        <p style="color:#999; font-size:12px;">
          보안을 위해 로그인 후 즉시 비밀번호 변경을 권장합니다.
        </p>

      </div>
    </div>
    """;
    }

    public void verify(String email, String code) {
        String key = "EMAIL_AUTH:" + email;
        String saved = redisTemplate.opsForValue().get(key);

        if (saved == null)
            throw new IllegalArgumentException("인증 시간이 만료되었습니다.");

        if (!saved.equals(code))
            throw new IllegalArgumentException("인증번호가 올바르지 않습니다.");

        redisTemplate.delete(key);
        redisTemplate.opsForValue()
                .set("EMAIL_VERIFIED:" + email, "true", 10, TimeUnit.MINUTES);
    }

    public boolean isVerified(String email) {
        return Boolean.TRUE.equals(
                redisTemplate.hasKey("EMAIL_VERIFIED:" + email)
        );
    }

    private String generateCode() {
        return String.valueOf((int)(Math.random() * 900000) + 100000);
    }
}