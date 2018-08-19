package com.neo.env;

import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.PropertySource;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;

public class RandomMapPropertySource extends PropertySource<Random> {

	/**
	 * Name of the random {@link PropertySource}.
	 */
	public static final String RANDOM_PROPERTY_SOURCE_NAME = "randomMap";

	private static final Pattern PATTERN = Pattern.compile("^" + RANDOM_PROPERTY_SOURCE_NAME
			+ "(?:\\.([A-Za-z\\d_]+(?:\\.[A-Za-z\\d_]+)*)|\\[([A-Za-z\\d_]+(?:\\.[A-Za-z\\d_]+)*)\\])\\.(int|long|uuid)(?:\\[(\\d+(?:,\\d+)?)\\])?$");

	private static final Log logger = LogFactory.getLog(RandomMapPropertySource.class);

	private static final Map<String, Object> MAP = new ConcurrentHashMap<>();

	public RandomMapPropertySource(String name) {
		super(name, new Random());
	}

	public RandomMapPropertySource() {
		this(RANDOM_PROPERTY_SOURCE_NAME);
	}

	@Override
	public Object getProperty(String express) {
		String[] args = genArgs(express);
		if (args == null) {
			return null;
		}
		if (logger.isTraceEnabled()) {
			logger.trace("Generating randomMap property for '" + args[0] + "'");
		}
		return getRandomValue(args);
	}

	/** @return {[完整表达式], [name], [type], [range]} */
	public String[] genArgs(String express) {
		Matcher matcher = PATTERN.matcher(express);
		return matcher.matches() ? new String[] { matcher.group(0), matcher.group(1) != null ? matcher.group(1) : matcher.group(2),
				matcher.group(3), matcher.group(4) } : null;
	}

	/** @param args {[完整表达式], [name], [type], [range]} */
	private Object getRandomValue(String[] args) {
		String name = args[1], type = args[2], range = args[3];
		String k = name + "|" + type;
		if (range != null)
			k += "|" + range;
		Object v = MAP.get(k);
		if (v != null)
			return v;

		if (type.equals("int")) {
			v = range == null ? getSource().nextInt() : getNextIntInRange(range);
		} else if (type.equals("long")) {
			v = range == null ? getSource().nextLong() : getNextLongInRange(range);
		} else if (type.equals("uuid")) {
			v = UUID.randomUUID().toString();
		} else
			v = getRandomBytes();
		Object previous = MAP.putIfAbsent(k, v);
		return previous == null ? v : previous;
	}

	private int getNextIntInRange(String range) {
		try {
			String[] tokens = StringUtils.commaDelimitedListToStringArray(range);
			int start = Integer.parseInt(tokens[0]);
			if (tokens.length == 1) {
				return getSource().nextInt(start);
			}
			return start + getSource().nextInt(Integer.parseInt(tokens[1]) - start);
		} catch (Exception e) {
			logger.error("getNextIntInRange error!", e);
			throw e;
		}
	}

	private long getNextLongInRange(String range) {
		String[] tokens = StringUtils.commaDelimitedListToStringArray(range);
		if (tokens.length == 1) {
			return Math.abs(getSource().nextLong() % Long.parseLong(tokens[0]));
		}
		long lowerBound = Long.parseLong(tokens[0]);
		long upperBound = Long.parseLong(tokens[1]) - lowerBound;
		return lowerBound + Math.abs(getSource().nextLong() % upperBound);
	}

	private Object getRandomBytes() {
		byte[] bytes = new byte[32];
		getSource().nextBytes(bytes);
		return DigestUtils.md5DigestAsHex(bytes);
	}

	public static void addToEnvironment(ConfigurableEnvironment environment) {
		environment.getPropertySources().addAfter(StandardEnvironment.SYSTEM_ENVIRONMENT_PROPERTY_SOURCE_NAME, new RandomMapPropertySource());
		logger.trace("RandomMapPropertySource add to Environment");
	}
}