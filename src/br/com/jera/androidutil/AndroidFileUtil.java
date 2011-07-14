package br.com.jera.androidutil;

import java.io.File;
import java.io.FileInputStream;

public class AndroidFileUtil {

	public static boolean fileEquals(File file1, File file2) {
		try {
			FileInputStream f1 = new FileInputStream(file1);
			FileInputStream f2 = new FileInputStream(file2);
			byte[] b1 = new byte[(int) file1.length()];
			byte[] b2 = new byte[(int) file2.length()];
			f1.read(b1);
			f2.read(b2);
			for (int j = 0; j < b1.length; j++) {
				if (b1[j] != b2[j])
					return false;
			}
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
}
