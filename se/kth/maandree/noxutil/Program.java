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


/**
 * The main class of the program
 *
 * @author  Mattias Andrée, <a href="mailto:maandree@kth.se">maandree@kth.se</a>
 */
public class Program
{
    /**
     * Non-constructor
     */
    private Program()
    {
	assert false : "This class [Program] is not meant to be instansiated.";
    }
    
    
    
    /**
     * This is the main entry point of the program
     * 
     * @param  args  Startup arguments
     */
    public static void main(final String... args) throws Exception
    {
	if (args.length != 0)
        {
	    final String prog = args[0].toLowerCase().replace("-", "");
	    final String[] progArgs = new String[args.length - 1];
	    System.arraycopy(args, args.length - progArgs.length, progArgs, 0, progArgs.length);
	    
	    if      (prog.equals("noxalarm"))    NoxAlarm.main(progArgs);
	    else if (prog.equals("quack"))       Quack   .main(progArgs);
	    else if (prog.equals("winise"))      Winise  .main(progArgs);
	    else if (prog.equals("22:00"))  Bibinilnilium.main(progArgs);
	    else
		System.err.println("Unknown program: " + prog);
	}
	else
	    System.err.println("You must specify program!");
    }
    
}
