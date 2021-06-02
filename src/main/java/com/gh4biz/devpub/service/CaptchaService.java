package com.gh4biz.devpub.service;

import cn.apiclub.captcha.Captcha;
import com.gh4biz.devpub.model.entity.CaptchaCode;
import com.gh4biz.devpub.model.response.CaptchaResponse;
import com.gh4biz.devpub.repo.CaptchaRepository;
import net.bytebuddy.utility.RandomString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

@Service
public class CaptchaService {
    private final CaptchaRepository captchaRepository;

    @Value("${blogCaptchaTimeout}")
    private String captchaTimeout;

    @Autowired
    public CaptchaService(CaptchaRepository captchaRepository) {
        this.captchaRepository = captchaRepository;
    }

    private void deleteOldCaptchas() {
        Iterable<CaptchaCode> captchaCodes = captchaRepository.findAll();
        Calendar cSchedStartCal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
        long gmtTime = cSchedStartCal.getTime().getTime();
        //     long timezoneAlteredTime = gmtTime + TimeZone.getTimeZone("Europe/Moscow").getRawOffset();
        for (CaptchaCode captchaCode : captchaCodes) {
            long captchaCodeLong = captchaCode.getTime().getTime() / 1000;
            if ((gmtTime / 1000 - captchaCodeLong) > Long.parseLong(captchaTimeout)) {
                captchaRepository.delete(captchaCode);
            }
        }
    }

    public CaptchaResponse generateCaptcha() throws IOException {
        deleteOldCaptchas();
        Captcha captcha = new Captcha.Builder(100, 35)
                .addText()
                .build();
        RandomString randomString = new RandomString(5);
        String secret = randomString.nextString();

        byte[] bytes = toByteArray(captcha.getImage(), "png");
        String bytesBase64 = Base64.getEncoder().encodeToString(bytes);
        String prefix = "data:image/png;base64, ";
        CaptchaCode captchaCode = new CaptchaCode();

        String code = captcha.getAnswer();
        captchaCode.setCode(code);
        captchaCode.setTime(new Date());
        captchaCode.setSecretCode(secret);
        captchaRepository.save(captchaCode);
        return new CaptchaResponse(secret, prefix.concat(bytesBase64));
    }

    private static byte[] toByteArray(BufferedImage bi, String format)
            throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(bi, format, baos);
        byte[] bytes = baos.toByteArray();
        return bytes;
    }
}
