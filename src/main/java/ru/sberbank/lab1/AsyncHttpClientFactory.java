package ru.sberbank.lab1;

import io.netty.util.HashedWheelTimer;
import io.netty.util.Timer;
import io.netty.util.concurrent.DefaultThreadFactory;
import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.DefaultAsyncHttpClient;
import org.asynchttpclient.DefaultAsyncHttpClientConfig;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

public class AsyncHttpClientFactory {

	public static AsyncHttpClient create(AsyncHttpClientConfig config) {
		DefaultAsyncHttpClientConfig.Builder builder = new DefaultAsyncHttpClientConfig.Builder();
		if (config.connectTimeout > 0) {
			builder = builder.setConnectTimeout(config.connectTimeout);
		}
		if (config.connectionRequestTimeout > 0) {
			builder = builder.setRequestTimeout(config.connectionRequestTimeout);
		}
		if (config.socketTimeout > 0) {
			builder = builder.setReadTimeout(config.socketTimeout);
		}
		if (config.connectionTtl > 0) {
			builder = builder.setConnectionTtl(config.connectionTtl);
		}
		if (config.maxConnections > 0) {
			builder = builder.setMaxConnections(config.maxConnections);
		}
		if (config.maxConnectionsPerRoute > 0) {
			builder = builder.setMaxConnectionsPerHost(config.maxConnectionsPerRoute);
		}
		if (config.retry >= 0) {
			builder = builder.setMaxRequestRetry(config.retry);
		}
		builder = builder.setThreadFactory(new DefaultThreadFactory(config.getName() + "-async-http", true));
		if (config.nettyTimer != null) {
			builder = builder.setNettyTimer(config.nettyTimer);
		} else {
			builder = builder.setThreadPoolName(config.getName() + "-async");
		}
		return new DefaultAsyncHttpClient(builder.build());
	}


	public static class AsyncHttpClientConfig {
		int connectTimeout = 0;
		int connectionRequestTimeout = 0;
		int socketTimeout = 0;
		int maxConnections = 0;
		int maxConnectionsPerRoute = 0;
		int connectionTtl = (int) Duration.ofMinutes(1).toMillis();
		int retry = -1;
		String name;
		HashedWheelTimerConfig timerConfig;
		Timer nettyTimer;

		@PostConstruct
		public void init() {
			if (timerConfig != null && nettyTimer == null) {
				HashedWheelTimer hashedWheelTimer = new HashedWheelTimer(new DefaultThreadFactory(getName() + "-async-timer", true), timerConfig.getTickDurationInMillis(), TimeUnit.MILLISECONDS, timerConfig.getTicksPerWheel());
				nettyTimer = hashedWheelTimer;
				hashedWheelTimer.start();
			}
		}

		@PreDestroy
		public void destroy() {
			if (nettyTimer != null) {
				nettyTimer.stop();
			}
		}

		public String getName() {
			return name;
		}
	}

	public static class HashedWheelTimerConfig {
		long tickDurationInMillis = 20;
		int ticksPerWheel = 4096;

		public long getTickDurationInMillis() {
			return tickDurationInMillis;
		}

		public int getTicksPerWheel() {
			return ticksPerWheel;
		}
	}
}
