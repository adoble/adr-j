package org.doble.adr;

import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

/**
 * The values used to specify how the dates are formatted in ADRs
 * Note that this enum needs to be in its own file. Without it, the picocli
 * generation of the reflection configuration files required for native images
 * with GraalVM does not work.
 * 
 * @author Andrew Doble
 *
 */

public enum DateFormatEnum {
	BASIC_ISO_DATE(DateTimeFormatter.BASIC_ISO_DATE),              // Basic ISO date 	'20111203'
	ISO_DATE(DateTimeFormatter.ISO_DATE),                          // ISO Date with or without offset 	'2011-12-03+01:00'; '2011-12-03'
	ISO_LOCAL_DATE(DateTimeFormatter.ISO_LOCAL_DATE),              // ISO Local Date 	'2011-12-03'
	ISO_OFFSET_DATE(DateTimeFormatter.ISO_OFFSET_DATE),            // Time with offset 	'10:15:30+01:00'
	ISO_ORDINAL_DATE(DateTimeFormatter.ISO_ORDINAL_DATE),          // Year and day of year 	'2012-337'
	ISO_WEEK_DATE(DateTimeFormatter.ISO_WEEK_DATE),                // Year and Week 	2012-W48-6'
	SHORT(DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT)),   // Short text style, typically numeric
	MEDIUM(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)), // Medium text style, with some detail.
	LONG(DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG)),     // Long text style, with lots of detail.
	FULL(DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL));     // Full text style, with the most detail.
	
	private DateTimeFormatter dateTimeFormatter;
	
	DateFormatEnum(DateTimeFormatter dateTimeFormatter) {
		this.dateTimeFormatter = dateTimeFormatter;
	}
	
	public DateTimeFormatter getDateTimeFormatter() {
		return dateTimeFormatter;
	}
}