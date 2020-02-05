package com.bookshop01.common.mail;

import java.io.BufferedReader;
import java.io.Console;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.mail.internet.MimeMessage;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.eclipse.core.runtime.Path;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

//추가룔
import org.springframework.core.io.ClassPathResource; 
import org.springframework.core.io.Resource;


import com.bookshop01.member.vo.MemberVO;
import com.bookshop01.order.vo.OrderVO;

@Service("mailService")
public class MailService {
	@Autowired
	private JavaMailSenderImpl mailSender;
	//private JavaMailSender mailSender;
    //@Autowired
    //private SimpleMailMessage preConfiguredMessage;
 
    @Async
    public void sendMail(Map receiverMap, List<OrderVO> myOrderList, MemberVO orderer)
    {
    	Iterator<String> iterator = receiverMap.keySet().iterator();
    	// 반복자를 이용해서 출력
    	while (iterator.hasNext()) { 

    		String key = (String)iterator.next(); // 키 얻기
    		System.out.print("key="+key);  // 출력
    		System.out.print("value="+receiverMap.get(key) );  // 출력    	
        }
    	
    	String to, subject, body = "";
    	String mailString = "";
    	OrderVO orderVO = (OrderVO) myOrderList.get(0);
    	
    	//정문이가 만듬 +손대면 너 손모가지도 날림 ㅇㅇ+
    	to =find_Email(orderer.getEmail1(),orderer.getEmail2());
    	
    	subject = orderer.getMember_name() + "님 주문 내역입니다.";
    	
    	MimeMessage message = mailSender.createMimeMessage();
    	System.out.println("mail test");
    	try {
    		MimeMessageHelper messageHelper = new MimeMessageHelper(message, true, "UTF-8");
    		mailString = readHtmlFile();
    		System.out.println(mailString);
    		body = setOrderInfo(mailString, orderVO);
    		messageHelper.setSubject(subject);
    		messageHelper.setTo(to);
    		messageHelper.setFrom("kimgipyung90@gmail.com", "바지사장");
    		messageHelper.setText(body, true);
    		mailSender.send(message);
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    
    }
    
    /**
	 * create by choi
	 * 202020205
	 * in MemberVO
	 * out String
	 * 메일을 보내는 메일주소를 만들어 반환함
	 * 정문이가 만듬 +손대면 너 손모가지도 날려드릴 준비가 되있음 ㅇㅇ+
	 * **/
    private String find_Email(String email1, String email2) {
    	String email="";
		String[] emailSplit_result=email2.split(",");
		if(emailSplit_result[1].equals("non")) {
			email2=emailSplit_result[0];
		}else {
			email2=emailSplit_result[1];
		}
		
		email=email1+"@"+email2;
		
		return email;
	}

	private String setOrderInfo(String mailString, OrderVO orderVO) {
		String regEx = "_order_id";
		Pattern pat = Pattern.compile(regEx);
		
		Matcher m = pat.matcher(mailString);
		mailString = m.replaceAll(Integer.toString(orderVO.getOrder_id()));
		
		regEx = "_goods_id";
		pat = Pattern.compile(regEx);
		m = pat.matcher(mailString);
		mailString = m.replaceAll(Integer.toString(orderVO.getGoods_id()));
		

		regEx = "_goods_fileName";
		pat = Pattern.compile(regEx);
		m = pat.matcher(mailString);
		mailString = m.replaceAll(orderVO.getGoods_fileName());
		
		regEx = "_goods_title";
		pat = Pattern.compile(regEx);
		m = pat.matcher(mailString);
		mailString = m.replaceAll(orderVO.getGoods_title());
		
		System.out.println(mailString);
		return mailString;
	}

	private String readHtmlFile() {
    	String mailString = null;
    	BufferedReader br = null;
    	InputStreamReader isr = null;
    	FileInputStream fis = null;
    	File file = null;
    	String temp = null;
    	StringBuilder sb=new StringBuilder();
    	
    	
    	ClassPathResource resource = new ClassPathResource("template/mail.html"); 
    	Resource resource2 = new ClassPathResource("template/mail.html"); 

    	
    	
    	try {
    		System.out.println("파일사이즈::"+resource.getFile().length()); 
        	System.out.println("파일절대경로+파일명:"+resource.getURI().getPath().substring(1));
    		file = resource.getFile();
    		
    	}catch (Exception e) {
			System.out.println(e);
		}
    	
        try{
            //입력 스트림 생성
            FileReader filereader = new FileReader(file);
            //입력 버퍼 생성
            BufferedReader bufReader = new BufferedReader(filereader);
            String line = "";
            while((line = bufReader.readLine()) != null){
                System.out.println(line);
                sb.append(line);
            }
            //.readLine()은 끝에 개행문자를 읽지 않는다.            
            bufReader.close();
        }catch (FileNotFoundException e) {
            // TODO: handle exception
        }catch(IOException e){
            System.out.println(e);
        }
    	
    	System.out.println("mailtest정문");
    	System.out.println(sb.toString());
    	return sb.toString();
	}

//	@Async
//    public void sendPreConfiguredMail(String message) {
//            SimpleMailMessage mailMessage = new SimpleMailMessage(preConfiguredMessage);
//            mailMessage.setText(message);
//            mailSender.send(mailMessage);
//    }

}

