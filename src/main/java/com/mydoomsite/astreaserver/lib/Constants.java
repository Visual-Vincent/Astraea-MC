package com.mydoomsite.astreaserver.lib;

import java.time.format.DateTimeFormatter;

public final class Constants
{
	public static final boolean DEBUG = false;
	
	public static final int TICKS_SECOND = 20;
	public static final int TICKS_MINUTE = TICKS_SECOND * 60;
	public static final int TICKS_HOUR = TICKS_MINUTE * 60;
	
	public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("uuuu-MM-dd HH:mm:ss");
}
