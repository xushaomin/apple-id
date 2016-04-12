package com.appleframework.id;

import java.math.BigInteger;

public class QndSnowflakeIdGenerator {

	public static void main(String[] args) {
		// get an instance of SnowflakeIdGenerator with default node-id.
		SnowflakeIdGenerator idGenerator = SnowflakeIdGenerator.getInstance();

		// generate a 64-bit long ID
		long id64bit = idGenerator.generateId64(); // 18λ
		System.out.println("generateId64=" + String.valueOf(id64bit).length());
		
		// generate a 48-bit long ID
		long id48bit = idGenerator.generateId48(); // 15λ
		System.out.println("generateId48=" + String.valueOf(id48bit).length());

		// generate a 64-bit long ID
		long idMini = idGenerator.generateIdMini(); // 13λ
		System.out.println("generateIdMini=" + String.valueOf(idMini).length());

		// generate a 64-bit long ID
		long idTiny = idGenerator.generateIdTiny(); // 7λ/12λ
		System.out.println("generateIdTiny=" + String.valueOf(idTiny).length());

		// generate a 128-bit long ID
		BigInteger id128bit = idGenerator.generateId128();

		long timestampMs;

		// extract timestamp (in milliseconds) from a 64-bit ID
		timestampMs = SnowflakeIdGenerator.extractTimestamp64(id64bit);
		System.out.println("extractTimestamp64=" + String.valueOf(timestampMs).length());

		// extract timestamp (in milliseconds) from a 128-bit ID
		timestampMs = SnowflakeIdGenerator.extractTimestamp128(id128bit);
		System.out.println("extractTimestamp128=" + String.valueOf(timestampMs).length());
	}
}
