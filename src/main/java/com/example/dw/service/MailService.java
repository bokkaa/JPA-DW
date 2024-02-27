package com.example.dw.service;

import com.example.dw.domain.entity.user.Users;
import com.example.dw.repository.user.UsersRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Random;


@Service
@RequiredArgsConstructor
public class MailService {

    private final UsersRepository usersRepository;
    private final JavaMailSender javaMailSender;

    //이메일 계정 등록하기
    @Value("${user.email}")
    private static final String senderEmail= "";
    private static int number;

    public static void createNumber(){
       number = (int)(Math.random() * (90000)) + 100000;// (int) Math.random() * (최댓값-최소값+1) + 최소값
    }

    /**
     * 이메일 인증번호 내용 양식 설정
     * @param mail 이메일 주소
     * @return 이메일 내용 양식
     */
    public MimeMessage CreateMail(String mail){
        createNumber();
        MimeMessage message = javaMailSender.createMimeMessage();

        try {
            message.setFrom(senderEmail);
            message.setRecipients(MimeMessage.RecipientType.TO, mail);
            message.setSubject("산책갈개에서 인증번호 발송드립니다.");
            String body = "";
            body += "<h3>" + "요청하신 인증 번호입니다." + "</h3>";
            body += "<h1>" + number + "</h1>";
            body += "<h3>" + "감사합니다." + "</h3>";
            message.setText(body,"UTF-8", "html");
        } catch (MessagingException e) {
            e.printStackTrace();
        }

        return message;
    }

    /**
     * 이메일 전송
     * @param mail 입력된 이메일 주소
     * @return 입력된 이메일 주소로 인증번호 전송
     */
    public int sendMail(String mail){
        MimeMessage message = CreateMail(mail);

        javaMailSender.send(message);

        return number;
    }

    /**
     * 인증번호 설정
     * @param leng 임시번호 자리수
     * @return 설정된 인증번호
     */
    public static String tempPassword(int leng){
   	int index = 0;
   	char[] charSet = new char[] {
   			'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
   			'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm',
   			'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
            '*', '@', '!', '#', '$', '%'
   	};	//배열안의 문자 숫자는 원하는대로

   	StringBuffer password = new StringBuffer();
   	Random random = new Random();

   	for (int i = 0; i < leng ; i++) {
   		double rd = random.nextDouble();
   		index = (int) (charSet.length * rd);

   		password.append(charSet[index]);

   		System.out.println("index::" + index + "	charSet::"+ charSet[index]);
   	}

   	return password.toString();
       //StringBuffer를 String으로 변환해서 return 하려면 toString()을 사용
   }


    /**
     * 전송된 인증번호를 입력한 후 올바른 인증번호일 경우 계정 정보 재전송
     * @param userName 입력된 회원 이름
     * @param mail 입력된 회원 이메일 주소
     * @param userPhone 입력된 회원 휴대전화 번호
     * @return 기입된 정보와 일치하는 계정 정보
     */
   public MimeMessage accountMail(String userName,String mail, String userPhone){

        Optional<Users> user = usersRepository.findUserAccountByUserNameAndUserEmailAndUserPhone(userName, mail, userPhone);
        String userAccountConfirm = user.get().getUserAccount();

       System.out.println("[ 조회된 userAccount ] : " + userAccountConfirm);

        MimeMessage message = javaMailSender.createMimeMessage();

        try{

            message.setFrom(senderEmail);
            message.setRecipients(MimeMessage.RecipientType.TO, mail);
            message.setSubject("산책갈개에서 회원님의 계정 정보를 알려드립니다.");
            String body = "";

            body += "<h3>"+"입력하신 정보와 일치하는 회원님의 아이디는" +"</h3>";
            body += "<h4>" + userAccountConfirm +"</h4>";
            body += "<h3>" + "감사합니다." + "</h3>";

            message.setText(body,"UTF-8", "html");
        }catch(MessagingException e){
            e.printStackTrace();
        }

        return message;
   }

    /**
     * 기입된 정보로 인증번호 전송 - 계정찾기
     * @param userName 입력된 회원 이름
     * @param mail 입력된 회원 이메일 주소
     * @param userPhone 입력된 회원 휴대전화 번호
     * @return 인증번호 
     */
   public int reworkAccountMail(String userName,String mail, String userPhone){
        MimeMessage message = accountMail(userName, mail, userPhone);
        javaMailSender.send(message);
        return number;
   }


    /**
     * 임시 비밀번호 전송 및 임시 비밀번호로 해당 계정 비밀번호 수정
     * @param userName 기입된 회원 이름
     * @param userAccount 기입된 회원 계정
     * @param mail 기입된 회원 이메일 주소
     * @return 임시비밀번호 전송
     */
    //임시비밀번호 전송 및 임시비밀번호로 수정
    public MimeMessage pwMail(String userName, String userAccount, String mail){

        String rePassword = tempPassword(10);
        System.out.println("임시비밀번호 :"+ rePassword);

        MimeMessage message = javaMailSender.createMimeMessage();

        try {
            message.setFrom(senderEmail);
            message.setRecipients(MimeMessage.RecipientType.TO, mail);
            message.setSubject("산책갈개 패스워드 재발송입니다.");
            String body = "";
            body += "<h3>" + "요청하신 임시 패스워드입니다." + "</h3>";
            body += "<h1>" + rePassword + "</h1>";
            body += "<h3>" + "감사합니다." + "</h3>";
            message.setText(body,"UTF-8", "html");
        } catch (MessagingException e) {
            e.printStackTrace();
        }



        updatePw(userName, userAccount, mail, rePassword);

        return message;
    }

    /**
     * 기입된 정보와 일치하는 계정의 비밀번호를 임시 비밀번호로 수정
     * @param userName 기입된 회원 이름
     * @param userAccount 기입된 회원 계정
     * @param mail 기입된 회원 이메일 주소
     * @param rePassword 임시 비밀번호
     * @return 회원 정보
     */
    public Users updatePw(String userName, String userAccount, String mail, String rePassword) {

        Users users = usersRepository.findByUserNameAndUserAccountAndUserEmail(userName, userAccount, mail).get();

        users.updatePassword(rePassword);

        usersRepository.save(users);

        return users;
    }


    /**
     * 기입된 정보로 인증번호 전송 - 비밀번호 찾기
     * @param userName 기입된 회원 이름
     * @param userAccount 기입된 회원 계정
     * @param mail 기입된 회원 이메일 주소
     * @return 인증번호 전송
     */
    public int reworkPwMail(String userName, String userAccount, String mail){

        MimeMessage message = pwMail(userName, userAccount, mail);

        javaMailSender.send(message);

        return number;
    }

}
