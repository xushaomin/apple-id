package com.appleframework.id;

/**
 *
 * @author cruise
 * @version 21.01.13 17:18
 */
public interface IdentityGenerator {
	
	public long nextId(final String namespace);
	
}
