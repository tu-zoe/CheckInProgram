package joy.aksd.tools;

import java.util.Scanner;

public class isIp {
	/**
	 * isIpTest
	 * public static void main(String[] args) {
	 
		// TODO Auto-generated method stub
		Scanner sc = new Scanner(System.in);
		System.out.println(isIp(sc.next()));
	}
	*/
	public static String trimSpace(String ip) {
		while(ip.startsWith(" ")) {
			ip = ip.substring(1, ip.length()).trim();
		}
		while(ip.endsWith(" ")) {
			ip = ip.substring(0, ip.length()-1).trim();
		}
		
		return ip;
	}
	
	public static boolean isIp(String ip) {
		boolean isIpFlag = false;
		ip = trimSpace(ip);
		if(ip.matches("\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}")) {
			String[] str = ip.split("\\.");
			if(Integer.parseInt(str[0])< 255)
				if(Integer.parseInt(str[1])< 255)
					if(Integer.parseInt(str[2])< 255)
						if(Integer.parseInt(str[3])< 255)
							isIpFlag = true;
		}
		else
			System.out.println("IP地址输入格式非法");
		
		return isIpFlag;
	}
}
