package com.goldenage.project.email.controller;

import com.goldenage.project.email.model.service.EmailService;
import com.goldenage.project.login.model.dto.AdminDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.mail.MessagingException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/submit/*")
public class EmailController {

    private final EmailService emailService;
    private JavaMailSender mailSender;

    private PasswordEncoder passwordEncoder;

    @Autowired
    public EmailController(EmailService emailService, JavaMailSender mailSender, PasswordEncoder passwordEncoder){

        this.mailSender = mailSender;
        this.emailService = emailService;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/email")
    @ResponseBody
    public Object matchIdEmail(@RequestBody HashMap<String, String> adminMap) throws MessagingException, UnsupportedEncodingException {

        String id = adminMap.get("id");
        String email = adminMap.get("email");

        AdminDTO ckEmail = emailService.selectEmail(id);

        int result = 0;

        if(ckEmail != null){

            String dbEmail = ckEmail.getAdminEmail();

            if(email.equals(dbEmail)) {

                //인증번호생성
                AuthKey authKey = new AuthKey();
                String authNum = authKey.makeAuthKey();

                //인증번호 업데이트(id, email 기준)
                result = emailService.updateAuthNum(id, dbEmail, authNum);

                //인증메일 발송
                if(result > 0) {

                    MailHandler mailHandler = new MailHandler(mailSender);
                    mailHandler.setSubject("골든에이지 인증코드 발송 이메일입니다.");
                    mailHandler.setText(
                            "<h1>골든에이지 이메일 인증</h1>" +
                                    "<br>골든에이지 비밀번호를 재설정하기 위한 인증 이메일입니다." +
                                    "<br>아래 인증번호를 입력해주세요." +
                                    "<br>[" + authNum + "]"
                    );
                    mailHandler.setFrom("intectme@naver.com", "GOLDENAGE");
                    mailHandler.setTo(dbEmail);
                    mailHandler.send();
                }

            } else if(!email.equals(dbEmail)){

                result = 0;
            }

        } else {

            result = 0;
        }

        Map<String, Object> resultAjax = new HashMap<>();
        resultAjax.put("result", result);

        return resultAjax;
    }

    @PostMapping("/authNum")
    @ResponseBody
    public Object matchAuthNum(@RequestBody HashMap<String, String> adminMap){

        String id = adminMap.get("id");
        String email = adminMap.get("email");
        String authNum = adminMap.get("authNum");

        AdminDTO adminDTO = emailService.matchAuthNum(id, email);

        int result = 0;

        if(adminDTO != null){

            String dbAuthNum = adminDTO.getAuthNum();

            if(authNum.equals(dbAuthNum)){

                result = 1;

            } else {

                result = 2;
            }
        }

        Map<String, Object> resultAjax = new HashMap<>();
        resultAjax.put("result", result);

        return resultAjax;
    }

    @PostMapping("/password")
    public ModelAndView updatePassword(ModelAndView mv, @ModelAttribute AdminDTO adminDTO){

        String id = adminDTO.getAdminId();
        String pw = passwordEncoder.encode(adminDTO.getAdminPwd());

        int result = emailService.updatePassword(id,pw);

        if(result > 0){

            mv.setViewName("/admin/loginPage");
        } else {

            mv.setViewName("/common/failure");
        }
        return mv;
    }
}

