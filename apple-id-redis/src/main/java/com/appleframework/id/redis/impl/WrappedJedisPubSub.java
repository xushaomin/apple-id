package com.appleframework.id.redis.impl;

import redis.clients.jedis.BinaryJedisPubSub;
import redis.clients.util.SafeEncoder;

import com.appleframework.id.redis.MessageListener;

/**
 * Extends Jedis' {@link BinaryJedisPubSub} and wraps a {@link MessageListener}
 * underneath.
 * 
 * @author Thanh Ba Nguyen <btnguyen2k@gmail.com>
 * @since 0.1.0
 */
public class WrappedJedisPubSub extends BinaryJedisPubSub {

    private MessageListener messageListener;

    public WrappedJedisPubSub(MessageListener messageListener) {
        this.messageListener = messageListener;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onMessage(byte[] topic, byte[] message) {
        String strTopic = SafeEncoder.encode(topic);
        messageListener.onMessage(strTopic, message);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onPMessage(byte[] pattern, byte[] topic, byte[] message) {
        // NOT IMPLEMETED YET
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onPSubscribe(byte[] pattern, int numSubscribedChannels) {
        // EMPTY
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onPUnsubscribe(byte[] pattern, int numSubscribedChannels) {
        // EMPTY
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onSubscribe(byte[] topic, int numSubscribedChannels) {
        // EMPTY
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onUnsubscribe(byte[] topic, int numSubscribedChannels) {
        // EMPTY
    }

}
