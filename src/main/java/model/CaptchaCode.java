package model;

import java.util.Date;

public class CaptchaCode {
    private int id; //id каптча
    private Date time; //дата и время генерации кода капчи
    private String code; //код, отображаемый на картинкке капчи
    private String secretCode; //код, передаваемый в параметре

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getSecretCode() {
        return secretCode;
    }

    public void setSecretCode(String secretCode) {
        this.secretCode = secretCode;
    }
}
