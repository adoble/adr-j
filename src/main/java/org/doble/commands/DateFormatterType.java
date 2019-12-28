package org.doble.commands;

// The values used to specify how the dates are formatted in ADRs
// TODO These values are repeated in CommandNew. Refactor so that they are
//  only specified once.
// Note that this enum needs to have scope public. Without it, the picocli
// generation of the reflection configuration files required for native images
// with GraaVM do not work.  
public enum DateFormatterType {
	BASIC_ISO_DATE,   // Basic ISO date 	'20111203'
	ISO_DATE,         // ISO Date with or without offset 	'2011-12-03+01:00'; '2011-12-03'
	ISO_LOCAL_DATE,   // ISO Local Date 	'2011-12-03'
	ISO_OFFSET_DATE,  // Time with offset 	'10:15:30+01:00'
	ISO_ORDINAL_DATE, // Year and day of year 	'2012-337'
	ISO_WEEK_DATE,    // Year and Week 	2012-W48-6'
	SHORT,            // Short text style, typically numeric
	MEDIUM,           // Medium text style, with some detail.
	LONG,             // Long text style, with lots of detail.
	FULL	          // Full text style, with the most detail.
}