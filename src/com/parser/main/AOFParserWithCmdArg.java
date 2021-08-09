package com.parser.main;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class AOFParserWithCmdArg
{
	public static void main(String[] args) throws IOException
{

		BufferedWriter write = new BufferedWriter(new FileWriter(args[1]));

		BufferedInputStream bf = new BufferedInputStream(new FileInputStream(args[0]));

		int pointer = bf.read();

		int count = 0;

		while ((pointer) != -1)
		{
			byte[] byteOfStar = { (byte) pointer };
			byte[] finalByteArray = new byte[3000];
			int finalBytes = 0;
			String stringOfStar = new String(byteOfStar, StandardCharsets.UTF_8);
			stringOfStar = stringOfStar.trim();

			if (!stringOfStar.equalsIgnoreCase("*"))
			{
				continue;
			}
			else
			{

				byte[] bytesNumberOfArgs = new byte[10];
				int countForNumberofArg = 0;
				pointer = bf.read();
				while (!(pointer == 10 || pointer == 13))
				{

					bytesNumberOfArgs[countForNumberofArg] = (byte) pointer;
					countForNumberofArg++;
					pointer = bf.read();

				}
				String stringOfNumberOfArgs = new String(bytesNumberOfArgs, StandardCharsets.UTF_8);
				stringOfNumberOfArgs = stringOfNumberOfArgs.trim();
				int NumberOfArgs = Integer.parseInt(stringOfNumberOfArgs);

				while (pointer == 10 || pointer == 13)
				{
					pointer = bf.read();
				}
				for (int i = 0; i < NumberOfArgs; i++)
				{

					byte[] byteOfDollar = { (byte) pointer };
					String stringOfDollar = new String(byteOfDollar, StandardCharsets.UTF_8);
					stringOfDollar = stringOfDollar.trim();
					if (!stringOfDollar.equalsIgnoreCase("$"))
					{
						continue;
					}
					else
					{
						byte[] bytesNumberOfChars = new byte[10];
						int countForNumberofChar = 0;
						pointer = bf.read();
						while (!(pointer == 10 || pointer == 13))
						{

							bytesNumberOfChars[countForNumberofChar] = (byte) pointer;
							countForNumberofChar++;
							pointer = bf.read();

						}
						String stringOfNumberOfChars = new String(bytesNumberOfChars, StandardCharsets.UTF_8);
						stringOfNumberOfChars = stringOfNumberOfChars.trim();
						int NumberOfChars = Integer.parseInt(stringOfNumberOfChars);
						while (pointer == 10 || pointer == 13)
						{
							pointer = bf.read();
						}

						for (int j = 0; j < NumberOfChars; j++)
						{

							if (pointer == 10)
							{
								finalByteArray[finalBytes] = (byte) 92;
								finalBytes++;
								finalByteArray[finalBytes] = (byte) 110;
								finalBytes++;
								pointer = bf.read();
							}
							else if (pointer == 13)
							{
								finalByteArray[finalBytes] = (byte) 92;
								finalBytes++;
								finalByteArray[finalBytes] = (byte) 114;
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
						finalByteArray[finalBytes] = (byte) 32;
						finalBytes++;

					}
					while (pointer == 10 || pointer == 13)
					{
						pointer = bf.read();
					}
				}

			}
			if (count == 10000)
			{
				write.flush();
			}
			count = count + 1;
			String stringOfFinal = new String(finalByteArray, StandardCharsets.UTF_8);
			stringOfFinal = stringOfFinal.trim();
			write.write(stringOfFinal);
			write.newLine();

		}
		write.flush();
		write.close();

	}
}
