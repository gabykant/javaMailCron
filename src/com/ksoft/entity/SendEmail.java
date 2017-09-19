package com.ksoft.entity;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.logging.Logger;

import javax.activation.DataHandler;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.util.ByteArrayDataSource;

import com.ksoft.utils.LogFile;
import com.sun.mail.smtp.SMTPTransport;

public class SendEmail {
	private String mailhost;
	private String title;
	private String from;
	private String subject;
	private String body;
	private String to, cc, bcc;
	InputStream inputStream;
	Logger log;
	
	public void send(){
		try {
			Properties config = new Properties();
			inputStream = getClass().getClassLoader().getResourceAsStream("config.properties");
			if(inputStream != null){
				config.load(inputStream);
			}else{
				throw new FileNotFoundException("property file config.properties not found in the classpath");
			}
			this.setFrom(config.getProperty("server.from"));
			this.setMailhost(config.getProperty("server.mailhost"));
			this.setSubject(config.getProperty("server.title"));
			
			Properties props = System.getProperties();
			props.put("mail.smtp.auth", "true");
			props.put("mail.smtp.from", this.getFrom());
			props.put("mail.smtp.host", this.getMailhost());
			props.put("mail.smtp.port", config.getProperty("server.port"));
			props.put("mail.smtp.socketFactory.port", config.getProperty("server.port"));
            props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
			
			Session session = Session.getInstance(props, null);
			session.setDebug(true); // Write this log file
			
			Message msg = new MimeMessage(session);
			msg.setFrom(new InternetAddress(this.getFrom(), config.getProperty("server.fromName")));
			if(to == "")
				to = config.getProperty("server.defaultto");
			msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to, true));
			msg.setRecipients(Message.RecipientType.CC, InternetAddress.parse(cc, false));
			if(bcc != null)
				msg.setRecipients(Message.RecipientType.BCC, InternetAddress.parse(bcc, false));
			
			msg.setSubject(this.getSubject() + " - " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
			StringBuffer sb = new StringBuffer();
			sb.append("<!DOCTYPE html><html>");
			sb.append("<head>");
			sb.append("</head><body>");
			sb.append("<h3>Dear all</h3>");
			sb.append("<p>Please find below the current Work Order</p>");
			sb.append("<p>Kindly report back if there is misunderstanding somewhere. <br /><br />Or do you have any feedback do not hesitate to reply us back</p>");
			sb.append("Regards");
			sb.append("<p>- - - - - - - - - - - - - - - - - - - - - -</p>");
			sb.append(this.getBody());
			sb.append("</body></html>");
			msg.setDataHandler(new DataHandler(new ByteArrayDataSource(sb.toString(), "text/html")));
			msg.setSentDate(new Date());
			
			SMTPTransport t = (SMTPTransport)session.getTransport("smtp");
			t.connect(config.getProperty("server.username"), config.getProperty("server.password"));
			t.sendMessage(msg, msg.getAllRecipients());
			inputStream.close();
			
		} catch (Exception e) {
			log.info(e.getMessage() + e.getCause());
			//e.printStackTrace();
		}
	}
	
	public String getMailhost() {
		return mailhost;
	}

	public void setMailhost(String mailhost) {
		this.mailhost = mailhost;
	}
	
	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getTo() {
		return to;
	}

	public void setTo(String to) {
		this.to = to;
	}

	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public String getBody() {
		return body;
	}
	public void setBody(String body) {
		this.body = body;
	}
	
	public String getCc() {
		return cc;
	}

	public void setCc(String cc) {
		this.cc = cc;
	}

	public String getBcc() {
		return bcc;
	}

	public void setBcc(String bcc) {
		this.bcc = bcc;
	}

	public SendEmail(String to, String cc, String bcc, String body) {
		super();
		this.to = to;
		this.cc = cc;
		this.bcc = bcc;
		this.body = body;
		log = LogFile.getLoggerFile();
	}
}
