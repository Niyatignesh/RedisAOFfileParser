package com.parser.utils;

import redis.clients.jedis.Jedis;

public class RedisClient
{

	public static void main(String[] args)
	{
		// Connecting to Redis server on localhost
		Jedis jedis = new Jedis("10.80.1.185", 6379);

		System.out.println("Connection to server sucessfully");
		// check whether server is running or not
		System.out.println("Server is running: " + jedis.ping());
		System.out.println(String.format("First line\nSecond line"));
		jedis.set("NewLineKeywithout", new String("First lineyoyo\nSecond lineyoyo"));
	}

}
