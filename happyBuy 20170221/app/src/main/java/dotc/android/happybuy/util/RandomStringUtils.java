package dotc.android.happybuy.util;

import java.util.Random;

public class RandomStringUtils {
	private static char[] characters=new char[]{
		'a','b','c','d','e','f','g','h','i','j','k','l','m','n','o','p','q','r','s','t','u','v','w','x','y','z',
		'A','B','C','D','E','F','G','H','I','J','K','L','M','N','O','P','Q','R','S','T','U','V','W','X','Y','Z',
		'1','2','3','4','5','6','7','8','9','0'
	};

	protected static char[] alphas=new char[]{
		'a','b','c','d','e','f','g','h','i','j','k','l','m','n','o','p','q','r','s','t','u','v','w','x','y','z',
		'A','B','C','D','E','F','G','H','I','J','K','L','M','N','O','P','Q','R','S','T','U','V','W','X','Y','Z'
	};
	protected static char[] digitals=new char[]{
		'1','2','3','4','5','6','7','8','9','0'
	};
	public static String getRandomString(int length) {
		StringBuilder sb=new StringBuilder();
		Random r = new Random() ;
		for(int i=0;i<length;i++) {
			Integer index=r.nextInt(characters.length);
			sb.append(characters[index]);
		}
		return sb.toString();

	}
	public static String getRandomDigitals(int length) {
		StringBuilder sb=new StringBuilder();
		Random r = new Random() ;
		for(int i=0;i<length;i++) {
			Integer index=r.nextInt(digitals.length);
			sb.append(digitals[index]);
		}
		return sb.toString();

	}
}
