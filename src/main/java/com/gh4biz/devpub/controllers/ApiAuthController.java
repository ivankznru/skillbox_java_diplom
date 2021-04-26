package com.gh4biz.devpub.controllers;

import com.gh4biz.devpub.model.entity.CaptchaCode;
import com.gh4biz.devpub.model.response.CaptchaResponse;
import com.gh4biz.devpub.repo.CaptchaRepository;
import net.bytebuddy.utility.RandomString;
import nl.captcha.Captcha;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;


@RestController
@RequestMapping("/api")
public class ApiAuthController {
    private CaptchaRepository captchaRepository;

    public ApiAuthController(CaptchaRepository captchaRepository) {
        this.captchaRepository = captchaRepository;
    }

    @GetMapping("/auth/captcha")
    private ResponseEntity<CaptchaResponse> getCaptcha() throws IOException {

        Iterable<CaptchaCode> captchaCodes = captchaRepository.findAll();
        deleteOldCaptchas();

        return ResponseEntity.ok(testCaptcha());
    }

    private void deleteOldCaptchas() {
        Iterable<CaptchaCode> captchaCodes = captchaRepository.findAll();
        Calendar cSchedStartCal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
        long gmtTime = cSchedStartCal.getTime().getTime();
        long timezoneAlteredTime = gmtTime + TimeZone.getTimeZone("Europe/Moscow").getRawOffset();
        Calendar cSchedStartCal1 = Calendar.getInstance(TimeZone.getTimeZone("Europe/Moscow"));
        cSchedStartCal1.setTimeInMillis(timezoneAlteredTime);
//        TimeZone tm_curr = TimeZone.getDefault();
//        if (tm_curr.getID().equals("Europe/Moscow")) {   }
        long now = cSchedStartCal1.getTime().getTime() / 1000;
        for (CaptchaCode captchaCode : captchaCodes) {
            long captchaCodeLong = captchaCode.getTime().getTime() / 1000;
            if ((now - captchaCodeLong) > 3600) {
                captchaRepository.delete(captchaCode);
            }
        }
    }

    private CaptchaResponse testCaptcha() throws IOException {
        Captcha captcha = new Captcha.Builder(100, 30)
                .addText()
                .build();
        RandomString secret = new RandomString(5);
        byte[] bytes = toByteArray(captcha.getImage(), "png");

        String bytesBase64 = Base64.getEncoder().encodeToString(bytes);
        String prefix = "data:image/png;base64, ";
        return new CaptchaResponse(secret.nextString(), prefix.concat(bytesBase64));
    }

    public static byte[] toByteArray(BufferedImage bi, String format)
            throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(bi, format, baos);
        byte[] bytes = baos.toByteArray();
        return bytes;
    }
}
