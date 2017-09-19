package com.ksoft.model;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

import com.ksoft.utils.LogFile;

public class WoDataToPush {
	private static InputStream inputStream;
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
			String url = "jdbc:mysql://" + config.getProperty("server.wodbhost") + ":" + config.getProperty("server.wodbport") +"/" + config.getProperty("server.wodbname") + "?useSSL=false";
			conn = DriverManager.getConnection(url, config.getProperty("server.wodbuser"), config.getProperty("server.wodbpass"));
			inputStream.close();
		} catch (Exception e) {
			LogFile.getLoggerFile().info(e.getMessage() + " - " + e.getCause());
		}
		return conn;
	}
	
	public static String getWoData(){
		String html = "";
		Connection conn = strcon();
		String last_date = null;
		String sql_last_record_date = "SELECT DATE_OF_REPORT FROM work_order_t ORDER BY DATE_OF_REPORT DESC LIMIT ?";
		try {
			PreparedStatement preStatement = conn.prepareStatement(sql_last_record_date);
			preStatement.setInt(1, 1);
			ResultSet rs = preStatement.executeQuery();
			while (rs.next()) {
				last_date = (rs.getString("DATE_OF_REPORT")).substring(0, 19);
			}
			preStatement.close();
			rs.close();
			
			String sql_all_data = "SELECT * FROM work_order_t  WHERE `DATE_OF_REPORT` >= ?"; //ORDER BY DATE_OF_REPORT
			PreparedStatement preStatementData = conn.prepareStatement(sql_all_data);
			preStatementData.setString(1, last_date);
			ResultSet rs_data = preStatementData.executeQuery();
			html = parse_html(rs_data);
			conn.close();
		} catch (SQLException e) {
			LogFile.getLoggerFile().info(e.getMessage() + " - " + e.getCause());
		}
		return html;
	}
	
	private static String parse_html(ResultSet r){
		String body_text = "<table style='width:100%;text-align:left;border-collapse:collapse;border:1px solid #ccc' bgcolor='#007bff'>"
				+ "<thead style='background-color: #007bff!important; padding: 10px;border-color: #32383e;border:1px solid #ccc;color:#FFF; ' ><tr>"
				//+ "<th style='border-color: #32383e; vertical-align: bottom; border-bottom:2px solid #e9ecef;padding:.75rem; border-top:1px solid #e9ecef; border-bottom-width: 2px; border:1px solid #e9ecef;'>DATE_OF_REPORT</th>"
				+ "<th style='border-color: #32383e; vertical-align: bottom; border-bottom:2px solid #e9ecef;padding:.75rem; border-top:1px solid #e9ecef; border-bottom-width: 2px; border:1px solid #e9ecef;' >NOC_Ref_Id</th>"
				+ "<th style='border-color: #32383e; vertical-align: bottom; border-bottom:2px solid #e9ecef;padding:.75rem; border-top:1px solid #e9ecef; border-bottom-width: 2px; border:1px solid #e9ecef;'>Priority</th>"
				+ "<th style='border-color: #32383e; vertical-align: bottom; border-bottom:2px solid #e9ecef;padding:.75rem; border-top:1px solid #e9ecef; border-bottom-width: 2px; border:1px solid #e9ecef;'>TimeSLA</th>"
				+ "<th style='border-color: #32383e; vertical-align: bottom; border-bottom:2px solid #e9ecef;padding:.75rem; border-top:1px solid #e9ecef; border-bottom-width: 2px; border:1px solid #e9ecef;'>Status</th>"
				+ "<th style='border-color: #32383e; vertical-align: bottom; border-bottom:2px solid #e9ecef;padding:.75rem; border-top:1px solid #e9ecef; border-bottom-width: 2px; border:1px solid #e9ecef;'>Status_Before_Change</th>"
				+ "<th style='border-color: #32383e; vertical-align: bottom; border-bottom:2px solid #e9ecef;padding:.75rem; border-top:1px solid #e9ecef; border-bottom-width: 2px; border:1px solid #e9ecef;'>Time_Since_Last_Status</th>"
				+ "<th style='border-color: #32383e; vertical-align: bottom; border-bottom:2px solid #e9ecef;padding:.75rem; border-top:1px solid #e9ecef; border-bottom-width: 2px; border:1px solid #e9ecef;'>Actual_Time</th>"
				+ "<th style='border-color: #32383e; vertical-align: bottom; border-bottom:2px solid #e9ecef;padding:.75rem; border-top:1px solid #e9ecef; border-bottom-width: 2px; border:1px solid #e9ecef;'>Region</th>"
				//+ "<th style='border-color: #32383e; vertical-align: bottom; border-bottom:2px solid #e9ecef;padding:.75rem; border-top:1px solid #e9ecef; border-bottom-width: 2px; border:1px solid #e9ecef;'>FSO</th>"
				+ "<th style='border-color: #32383e; vertical-align: bottom; border-bottom:2px solid #e9ecef;padding:.75rem; border-top:1px solid #e9ecef; border-bottom-width: 2px; border:1px solid #e9ecef;'>Assigned_FT</th>"
				//+ "<th style='border-color: #32383e; vertical-align: bottom; border-bottom:2px solid #e9ecef;padding:.75rem; border-top:1px solid #e9ecef; border-bottom-width: 2px; border:1px solid #e9ecef;'>Office</th>"
				+ "<th style='border-color: #32383e; vertical-align: bottom; border-bottom:2px solid #e9ecef;padding:.75rem; border-top:1px solid #e9ecef; border-bottom-width: 2px; border:1px solid #e9ecef;'>Title</th>"
				+ "<th style='border-color: #32383e; vertical-align: bottom; border-bottom:2px solid #e9ecef;padding:.75rem; border-top:1px solid #e9ecef; border-bottom-width: 2px; border:1px solid #e9ecef;'>Creation_Date</th>"
				+ "<th style='border-color: #32383e; vertical-align: bottom; border-bottom:2px solid #e9ecef;padding:.75rem; border-top:1px solid #e9ecef; border-bottom-width: 2px; border:1px solid #e9ecef;'>Last_Status_Date</th>"
				+ "<th style='border-color: #32383e; vertical-align: bottom; border-bottom:2px solid #e9ecef;padding:.75rem; border-top:1px solid #e9ecef; border-bottom-width: 2px; border:1px solid #e9ecef;'>Reason_for_Status_Change</th>"
				+ "<th style='border-color: #32383e; vertical-align: bottom; border-bottom:2px solid #e9ecef;padding:.75rem; border-top:1px solid #e9ecef; border-bottom-width: 2px; border:1px solid #e9ecef;'>WO_Type</th>"
				+ "<th style='border-color: #32383e; vertical-align: bottom; border-bottom:2px solid #e9ecef;padding:.75rem; border-top:1px solid #e9ecef; border-bottom-width: 2px; border:1px solid #e9ecef;'>Manually_Created</th>"
				+ "</tr></thead><tbody bgcolor='#fff' color='#333'>";
		ResultSet rs_data = r;
		try {
			while(rs_data.next()){
				//body_text +="<tr><td style='border-color: #32383e;padding: .75rem; vertical-align: top; border-top: 1px solid #e9ecef; border:1px solid #e9ecef;'>" + rs_data.getString("DATE_OF_REPORT") + "</td>";
				body_text +="<tr><td style='background-color:#fff;border-color: #32383e;padding: .75rem; vertical-align: top; border-top: 1px solid #e9ecef; border:1px solid #e9ecef;'>" + rs_data.getString("NOC_Ref_Id")+ "</td><td style='background-color:#fff;border-color: #32383e;padding: .75rem; vertical-align: top; border-top: 1px solid #e9ecef; border:1px solid #e9ecef;'>" + rs_data.getInt("Priority") + "</td>";
				float tsla = rs_data.getFloat("TimeSLA") * 100;
				if(tsla >=0 && tsla < 25){
					body_text +="<td style='background-color:#fff;border-color: #32383e;padding: .75rem; vertical-align: top; border-top: 1px solid #e9ecef; border:1px solid #e9ecef; color:#000'>" + String.format("%.2f", rs_data.getFloat("TimeSLA") * 100) + "%</td>";
				}else if(tsla >=25 && tsla < 50){
					body_text +="<td style='background-color:#1D8348; color:#000; border-color: #32383e;padding: .75rem; vertical-align: top; border-top: 1px solid #e9ecef; border:1px solid #e9ecef;'>" + String.format("%.2f", rs_data.getFloat("TimeSLA") * 100) + "%</td>";
				}else if(tsla >= 50 && tsla < 75){
					body_text +="<td style='background-color:yellow;border-color: #32383e;padding: .75rem; vertical-align: top; border-top: 1px solid #e9ecef; border:1px solid #e9ecef;'>" + String.format("%.2f", rs_data.getFloat("TimeSLA") * 100) + "%</td>";
				}else if(tsla >= 75 && tsla < 100){
					body_text +="<td style='background-color:orange;border-color: #32383e;padding: .75rem; vertical-align: top; border-top: 1px solid #e9ecef; border:1px solid #e9ecef;'>" + String.format("%.2f", rs_data.getFloat("TimeSLA") * 100) + "%</td>";
				}else{
					body_text +="<td style='background-color:red;border-color: #32383e;padding: .75rem; vertical-align: top; border-top: 1px solid #e9ecef; border:1px solid #e9ecef;'>" + String.format("%.2f", rs_data.getFloat("TimeSLA") * 100) + "%</td>";
				}
				body_text +="<td style='background-color:#fff; border-color: #32383e;padding: .75rem; vertical-align: top; border-top: 1px solid #e9ecef; border:1px solid #e9ecef;'>" + rs_data.getString("Status") + "</td>";
				body_text +="<td style='background-color:#fff;border-color: #32383e;padding: .75rem; vertical-align: top; border-top: 1px solid #e9ecef; border:1px solid #e9ecef;'>" + rs_data.getString("Status_Before_Change") + "</td><td style='background-color:#fff;border-color: #32383e;padding: .75rem; vertical-align: top; border-top: 1px solid #e9ecef; border:1px solid #e9ecef;'>" + rs_data.getString("Time_Since_Last_Status") + "</td>";
				body_text +="<td style='background-color:#fff;border-color: #32383e;padding: .75rem; vertical-align: top; border-top: 1px solid #e9ecef; border:1px solid #e9ecef;'>" + rs_data.getString("Actual_Time") + "</td><td style='background-color:#fff;border-color: #32383e;padding: .75rem; vertical-align: top; border-top: 1px solid #e9ecef; border:1px solid #e9ecef;'>" + rs_data.getString("Region") + "</td>";
				//body_text +="<td style='background-color:#fff;border-color: #32383e;padding: .75rem; vertical-align: top; border-top: 1px solid #e9ecef; border:1px solid #e9ecef;'>" + rs_data.getString("FSO") + "</td>";
				body_text +="<td style='background-color:#fff;border-color: #32383e;padding: .75rem; vertical-align: top; border-top: 1px solid #e9ecef; border:1px solid #e9ecef;'>" + rs_data.getString("Assigned_FT") + "</td>";
				//body_text +="<td style='background-color:#fff;border-color: #32383e;padding: .75rem; vertical-align: top; border-top: 1px solid #e9ecef; border:1px solid #e9ecef;'>" + rs_data.getString("Office") + "</td>";
				body_text +="<td style='background-color:#fff;border-color: #32383e;padding: .75rem; vertical-align: top; border-top: 1px solid #e9ecef; border:1px solid #e9ecef;'>" + rs_data.getString("Title") + "</td>";
				body_text +="<td style='background-color:#fff;border-color: #32383e;padding: .75rem; vertical-align: top; border-top: 1px solid #e9ecef; border:1px solid #e9ecef;'>" + rs_data.getString("Creation_Date") + "</td><td style='background-color:#fff;border-color: #32383e;padding: .75rem; vertical-align: top; border-top: 1px solid #e9ecef; border:1px solid #e9ecef;'>" + rs_data.getString("Last_Status_Date") + "</td>";
				body_text +="<td style='background-color:#fff;border-color: #32383e;padding: .75rem; vertical-align: top; border-top: 1px solid #e9ecef; border:1px solid #e9ecef;'>" + rs_data.getString("Reason_for_Status_Change") + "</td><td style='background-color:#fff;border-color: #32383e;padding: .75rem; vertical-align: top; border-top: 1px solid #e9ecef; border:1px solid #e9ecef;'>" + rs_data.getString("WO_Type") + "</td>";
				body_text +="<td style='background-color:#fff;border-color: #32383e;padding: .75rem; vertical-align: top; border-top: 1px solid #e9ecef; border:1px solid #e9ecef;'>" + rs_data.getString("Manually_Created") + "</td></tr>";
			}
		} catch (SQLException e) {
			LogFile.getLoggerFile().info(e.getMessage() + " - " + e.getCause());
		}
		body_text +="</tbody></table>";
		return body_text;
	}
}
