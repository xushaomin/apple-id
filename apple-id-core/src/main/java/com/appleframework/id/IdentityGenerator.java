package com.appleframework.id;

/**
 *
 * @author cruise
 * @version 21.01.13 17:18
 */
public interface IdentityGenerator {
	
	/**
     * Gets current id.
     * 
     * @param namespace
     * @return current id for the specified namespace as long, negative value if
     *         error.
     * @since 0.2.0
     */
	public long nextId(final String namespace);
	
	/**
     * Gets current id.
     * 
     * @param namespace
     * @return current id for the specified namespace as long, negative value if
     *         error.
     * @since 0.2.0
     */
    public abstract long currentId(final String namespace);
	
	/**
     * Sets a value.
     * 
     * @param namespace
     * @param value
     * @return
     * @since 0.4.0
     */
    public boolean setValue(final String namespace, final long value);
	
}
