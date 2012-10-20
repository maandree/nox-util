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
import java.util.*;


/**
 * The main class of the reminder program
 *
 * @author  Mattias Andrée, <a href="mailto:maandree@kth.se">maandree@kth.se</a>
 */
public class RedAlert
{
    /**
     * Non-constructor
     */
    private RedAlert()
    {
	assert false : "This class [RedAlert] is not meant to be instansiated.";
    }
    
    
    
    /**
     * This is the main entry point of the program
     * 
     * @param  args  Startup arguments
     */
    public static void main(final String... args) throws Throwable
    {
	final Calendar now = Calendar.getInstance();

	int year   = now.get(Calendar.YEAR);
	int month  = now.get(Calendar.MONTH);
	int day    = now.get(Calendar.DAY_OF_MONTH);
	int hour   = now.get(Calendar.HOUR_OF_DAY);
	int minute = now.get(Calendar.MINUTE);
	int second = now.get(Calendar.SECOND);

	long timeD = ((year * 12) + month) * 31 + day;
	long timeT = ((hour * 60) + minute) * 60 + second;

	long timeN = timeD * 24 * 60 * 60 + timeT;


	year   = Integer.parseInt(args[0]);
	month  = Integer.parseInt(args[1]);
	day    = Integer.parseInt(args[2]);
	hour   = Integer.parseInt(args[3]);
	minute = Integer.parseInt(args[4]);
	second = Integer.parseInt(args[5]);

	timeD = ((year * 12) + month) * 31 + day;
	timeT = ((hour * 60) + minute) * 60 + second;

	long timeX = timeD * 24 * 60 * 60 + timeT;


	if (timeN >= timeX)
	{
	    System.out.print("\033c\033[0;41;1m");
	    System.out.print((args[6] + "\\n").replace("\\n", "\033[K\n"));
	    System.out.println("\033[K\033[m");
	}
    }

}

