package com.parser.main;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.nio.charset.StandardCharsets;

public class Parser2
{

	// used to maintain pointer which point to current reading bytes in input stream
	private static class Maintainer
	{
		private int pointer;

		private int numberofArgOrCharForGivenPointer;

		private String rediscommad;

		public Maintainer(int pointer)
		{

			this.pointer = pointer;
			this.numberofArgOrCharForGivenPointer = 0;
			this.rediscommad = "";
		}

		public int getPointer()
		{
			return pointer;
		}

		public String getRediscommad()
		{
			return rediscommad;
		}

		public void setRediscommad(String rediscommad)
		{
			this.rediscommad = rediscommad;
		}

		public void setPointer(int pointer)
		{
			this.pointer = pointer;
		}

		public int getNumberofArgOrCharForGivenPointer()
		{
			return numberofArgOrCharForGivenPointer;
		}

		public void setNumberofArgOrCharForGivenPointer(int numberofArgOrCharForGivenPointer)
		{
			this.numberofArgOrCharForGivenPointer = numberofArgOrCharForGivenPointer;
		}

	}

	public static void extractingNumberofArgsOrChar(Maintainer m1, BufferedInputStream bf, String starOrDollar) throws Exception
	{
		int pointer = m1.getPointer();

		byte[] firstByte = { (byte) pointer };
		String firstChar = new String(firstByte, StandardCharsets.UTF_8);
		firstChar = firstChar.trim();

		if (!starOrDollar.equalsIgnoreCase(firstChar))
		{
			throw new Exception("first byte is not " + starOrDollar);
		}

		byte[] bytesNumberOfArgs = new byte[10];
		int countForNumberofArg = 0;
		pointer = bf.read();

		// int 10 represent /n and 13 represent /r
		while (!(pointer == 10 || pointer == 13))
		{

			bytesNumberOfArgs[countForNumberofArg] = (byte) pointer;
			countForNumberofArg++;
			pointer = bf.read();

		}
		// we are currently at /n
		pointer = bf.read(); // at "/r"
		pointer = bf.read();
		String stringOfNumberOfArgs = new String(bytesNumberOfArgs, StandardCharsets.UTF_8);
		stringOfNumberOfArgs = stringOfNumberOfArgs.trim();
		int numberOfArgs = Integer.parseInt(stringOfNumberOfArgs);

		m1.setPointer(pointer);
		m1.setNumberofArgOrCharForGivenPointer(numberOfArgs);



	}

	public static void formingRedisCommand(Maintainer maintainer, BufferedInputStream bf) throws Exception
	{
		byte[] finalByteArray = new byte[3000];
		int finalBytes = 0;
		
		int pointer = maintainer.getPointer();
		int numberOfArg = maintainer.getNumberofArgOrCharForGivenPointer();

		for (int i = 0; i < numberOfArg; i++)
		{

			 extractingNumberofArgsOrChar(maintainer, bf, "$");
			pointer = maintainer.getPointer();

			for (int j = 0; j < maintainer.getNumberofArgOrCharForGivenPointer(); j++)
			{
				if (pointer == 10)
				{
					// only fall in this block if there is newline character in redis command so replacing it with string "/n"
					finalByteArray[finalBytes] = (byte) 92; // represent "/"
					finalBytes++;
					finalByteArray[finalBytes] = (byte) 110; // represent "n"
					finalBytes++;
					pointer = bf.read();
				}
				else if (pointer == 13)
				{
					finalByteArray[finalBytes] = (byte) 92; // represent "/"
					finalBytes++;
					finalByteArray[finalBytes] = (byte) 114; // represent "r"
					finalBytes++;
					pointer = bf.read();
				}
				else
				{
					finalByteArray[finalBytes] = (byte) pointer;
					finalBytes++;
					pointer = bf.read();
				}

			}
			pointer = bf.read(); // "/r"
			pointer = bf.read();// this must be *
			maintainer.setPointer(pointer);
			finalByteArray[finalBytes] = (byte) 32; // represent space character
			finalBytes++;


		}



		String stringOfFinal = new String(finalByteArray, StandardCharsets.UTF_8);
		stringOfFinal = stringOfFinal.trim();

		maintainer.setRediscommad(stringOfFinal);
		maintainer.setPointer(pointer);

		


	}

	public static void main(String[] args) throws Exception
	{

		String base = "/home/niyatignesh/redisAOF";

		String aofFILE = base + "/niyati.aof";
		BufferedWriter write = new BufferedWriter(new FileWriter(base + "/RedisCommand"));


		try (BufferedInputStream bf = new BufferedInputStream(new FileInputStream(aofFILE)))
		{
			int pointer = bf.read();

			int count = 0;

			while ((pointer) != -1)
			{

				Maintainer maintainer = new Maintainer(pointer);

				extractingNumberofArgsOrChar(maintainer, bf, "*");

				formingRedisCommand(maintainer, bf);

				pointer = maintainer.getPointer();



				if (count == 10000)
				{
					write.flush();
				}
				count = count + 1;

				write.write(maintainer.getRediscommad());
				write.newLine();

			}
			write.flush();

		}
		catch (Exception ignore)
		{
			System.err.println(ignore);
		}
		finally
		{
			write.close();
		}


	}
}
