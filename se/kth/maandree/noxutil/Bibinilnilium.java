/**
 * nox-util — Utility programs for headless environments
 *
 * Copyright © 2012  Mattias Andrée (maandree@kth.se)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package se.kth.maandree.noxutil;

import java.util.*;
import java.io.*;


/**
 * The main class of the alarm program
 *
 * @author  Mattias Andrée, <a href="mailto:maandree@kth.se">maandree@kth.se</a>
 */
public class Bibinilnilium
{
    /**
     * Non-constructor
     */
    private Bibinilnilium()
    {
	assert false : "This class [Bibinilnilium] is not meant to be instansiated.";
    }
    
    
    
    /**
     * This is the main entry point of the program
     * 
     * @param  args  Startup arguments
     */
    public static void main(final String... args) throws Throwable
    {
	final ProcessBuilder procBuilder = new ProcessBuilder(args);
	procBuilder.inheritIO();
	final Process process = procBuilder.start();
	
	final int hour = 22;
	final int minute = 00;
	final int halt = hour * 60 + minute;
	
	(new Thread()
	        {   @Override
		    public void run()
		    {   for (;;)
			    try
			    {
				final int nowHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
				final int nowMinute = Calendar.getInstance().get(Calendar.MINUTE);
				final int diff = halt - nowHour * 60 - nowMinute;
			
				if ((-2 <= diff) && (diff <= 0))
				{   process.destroy();
				    System.exit(0);
				    return;
				}
				if (diff == 1)
				{   final int remaining = 60 - Calendar.getInstance().get(Calendar.SECOND);
				    Thread.sleep((remaining > 10 ? 10 : remaining) * 1000);
				}
				else
				    Thread.sleep(20000);
			    }
			    catch (final Throwable err)
			    {    return;
			    }
		 }   }).start();
	
	process.waitFor();
	System.exit(process.exitValue());
    }
    
}
