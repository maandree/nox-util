/**
 * RFC Downloader – Request For Comments (RFC) plaintext and PDF downloader
 * 
 * Copyright © 2012  Mattias Andrée (maandree@kth.se)
 * 
 * This library is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this library.  If not, see <http://www.gnu.org/licenses/>.
 */
package se.kth.maandree.noxutil;

import java.io.*;


/**
 * This is the main class of the program
 *
 * @author  Mattias Andrée, <a href="mailto:maandree@kth.se">maandree@kth.se</a>
 */
public class RFCDownloader
{
    /**
     * Hidden constructor
     */
    private RFCDownloader()
    {
	//Nullify default constructor
    }
    
    
    
    /**
     * This is the main entry point of the program
     *
     * @param  args  Startup arguments
     */
    public static void main(final String... args)
    {
	try
        {
	    ttyin  = (new File("/dev/stdin" )).getCanonicalPath();
	    ttyout = (new File("/dev/stdout")).getCanonicalPath();
	    ttyerr = (new File("/dev/stderr")).getCanonicalPath();
	    
	    tty = " < " + ttyin + " > " + ttyout + " 2> " + ttyerr;
	    
	    final int start = Integer.parseInt(args[0]);
	    final int end   = Integer.parseInt(args[1]);
	    
	    if (args.length == 2)
		for (int i = start; i <= end; i++)
		{
		    exec("wget https://tools.ietf.org/rfc/rfc" + i + ".txt");
		    exec("wget https://tools.ietf.org/pdf/rfc" + i);
		    
		    exec("mv rfc" + i + " rfc" + i + ".pdf");
		    exec("mv rfc" + i + ".txt rfc" + i);
		    
		    exec("xz -z -e rfc" + i + ".pdf");
		    exec("xz -z -e rfc" + i);
		    
		    exec("mv rfc" + i + ".pdf.xz /dev/shm");
		    exec("mv rfc" + i + ".xz /dev/shm");
		}
	    else if (args.length == 3)
		for (int i = start; i <= end; i++)
		{
		    exec("mkdir " + i / 50 * 50);
		    exec("mv rfc" + i + ".pdf.xz " + i / 50 * 50);
		    exec("mv rfc" + i + ".xz " + i / 50 * 50);
		}
	}
	catch (final Exception err)
	{
	    System.err.println("Fatal exception");
	}
    }
    

    
    /**
     * The stdin
     */
    private static String ttyin;
    
    /**
     * The stdout
     */
    private static String ttyout;
    
    /**
     * The stderr
     */
    private static String ttyerr;
    
    /**
     * The tty redirection parameters
     */
    private static String tty;
    
    
    
    /**
     * Executes a program in the same terminal
     *
     * @param   cmd          The program command
     * @throws  IOException  On I/O exception
     */
    public static void exec(final String cmd) throws IOException
    {
	final Process process = (new ProcessBuilder("/bin/sh", "-c", cmd + tty)).start();
	final InputStream stream = process.getInputStream();
	for (;;)
	    if (stream.read() == -1)
		break;
    }
    
}

