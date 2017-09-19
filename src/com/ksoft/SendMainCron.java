package com.ksoft;

import com.ksoft.entity.SendEmail;
import com.ksoft.model.DBParam;
import com.ksoft.model.WoDataToPush;

public class SendMainCron {
	public static void main(String[] args) {
		String body = WoDataToPush.getWoData();
		//System.out.println(DBParam.listEmails(1));
		// 2 for profile WorkOrder - 1 for profile Managers always in BCC
		SendEmail e = new SendEmail(DBParam.listEmails(2), "", DBParam.listEmails(1), body);
		e.send();
	}
}
