package com.parser.utils;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class SampleStringtoChar
{
	public static void main(String[] args) throws IOException
	{
		String string = "/home/niyatignesh/redisAOF/niyati.aof";
		String name;
		BufferedInputStream bf = new BufferedInputStream(new FileInputStream(string));

		int pointer = bf.read();

		while ((pointer) != -1)
		{
			byte[] byteOfStar = { (byte) pointer };
			String stringOfStar = new String(byteOfStar, StandardCharsets.UTF_8);
			System.out.println(pointer + " " + stringOfStar);
			pointer = bf.read();

		}

	}
}
