package org.doble.annotations;

import java.lang.annotation.*;

@Documented
@Target(ElementType.TYPE)
@Inherited
@Retention(RetentionPolicy.RUNTIME)
public @interface Cmd {
	public String name();
	public String usage();
	public String shorthelp(); 
	public String help(); 

}
