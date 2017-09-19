package com.ksoft.model;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;

import org.apache.commons.lang.StringUtils;

import com.ksoft.utils.LogFile;

public class DBParam {
	private static InputStream inputStream;
	Logger log;
	private static Connection strcon(){
		Connection conn=null;
		try {
			Properties config = new Properties();
			inputStream = DBParam.class.getClassLoader().getResourceAsStream("config.properties");
			if(inputStream != null){
				config.load(inputStream);
			}else{
				throw new FileNotFoundException("The property file config.properties not found in the classpath");
			}
			String url = "jdbc:mysql://" + config.getProperty("server.dbhost") + ":" + config.getProperty("server.dbport") +"/" + config.getProperty("server.dbname") + "?useSSL=false";
			conn = DriverManager.getConnection(url, config.getProperty("server.dbuser"), config.getProperty("server.dbpass"));
			inputStream.close();
		} catch (Exception e) {
			LogFile.getLoggerFile().info(e.getMessage() + " - " + e.getCause());
		}
		return conn;
	}
	
	public static String listEmails(int id){
		List<String> list_email = new ArrayList<String>();
		Connection conn = strcon();
		String sql = "SELECT lb_email FROM tbl_user WHERE lb_group = ?";
		try {
			PreparedStatement preStatement = conn.prepareStatement(sql);
			preStatement.setInt(1, id);
			ResultSet rs = preStatement.executeQuery();
			while (rs.next()) {
				list_email.add(rs.getString("lb_email"));
			}
			conn.close();
		} catch (SQLException e) {
			LogFile.getLoggerFile().info(e.getMessage() + " - " + e.getCause());
		}
		return StringUtils.join(list_email, ",");
	}
}
