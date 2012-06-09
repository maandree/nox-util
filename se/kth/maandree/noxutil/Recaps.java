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

import java.io.*;
import java.util.*;


/**
 * This main class of the recaps program
 *
 * @author  Mattias Andrée, <a href="mailto:maandree@kth.se">maandree@kth.se</a>
 */
public class Recaps
{
    /**
     * Non-constructor
     */
    private Recaps()
    {
	assert false : "This class [Recaps] is not meant to be instansiated.";
    }
    
    
    
    /**
     * Class initialiser
     */
    static
    {
	String dir = System.getProperty("java.class.path");
	if (dir.endsWith("/") == false)
	    dir += "/";
	System.load((new File(dir + "libRecaps.so")).getAbsolutePath());
    }
    
    
    
    /**
     * This is the main entry point of the program
     *
     * @param  args  Startup arguments
     */
    public static void main(final String... args) throws Throwable
    {
	final RandomAccessFile ptmx = new RandomAccessFile("/dev/ptmx", "rw");
	final File fddir = new File("/proc/self/fd");
	int fd = 3;
	for (final File candidate : fddir.listFiles())
	    if (candidate.getCanonicalPath().equals("/dev/ptmx"))
	    {
		String tmp = candidate.getAbsolutePath();
		tmp = tmp.substring(tmp.lastIndexOf('/') + 1);
		fd = Integer.valueOf(tmp);
		break;
	    }
	
	final String pts = createPTS(fd);
	final ProcessBuilder procBuilder = exec(pts, args);
	
	final Object min = new Object();
	final Thread tin = new Thread()
	        {
		    /**
		     * {@inheritDoc}
		     */
		    @Override
		    public void run()
		    {
			try
			{
			    final OutputStream stream = new FileOutputStream(new File(pts));
			    synchronized (min)
			    {   min.notify();
			    }
			    for (;;)
			    {
				stream.write(System.in.read());
				stream.flush();
			    }
			}
			catch (final Throwable err)
			{   // Just quit
			}
		    }
	        };
	
	tin.start();
	
	//final Object mout = new Object();
	//final Thread tout = new Thread()
	//        {
	//	    /**
	//	     * {@inheritDoc}
	//	     */
	//	    @Override
	//	    public void run()
	//	    {
	//		try
	//		{
	//		    final InputStream stream = new FileInputStream(new File(pts));
	//		    synchronized (mout)
	//		    {   mout.notify();
	//		    }
	//		    for (;;)
	//		    {
	//			System.out.write(stream.read());
	//			System.out.flush();
	//		    }
	//		}
	//		catch (final Throwable err)
	//		{   // Just quit
	//		}
	//	    }
	//        };
	//
	//tout.start();
	
	synchronized (min)
	{   min.wait();
	}
	//synchronized (mout)
	//{   mout.wait();
	//}
	
	String stty = execSystemProperty("/dev/stdin", "stty", "-F", (new File("/dev/stdin")).getCanonicalPath(), "-a");
	stty = stty.substring(stty.indexOf(';') + 2);
	stty = stty.replace("\n", " ");
	stty = stty.replace("\t", " ");
	stty = stty.replace(";", " ");
	while (stty.contains("  "))
	    stty = stty.replace("  " , " ");
	stty = stty.replace(" = ", "=");
	if (stty.endsWith(" "))
	    stty = stty.substring(0, stty.length() - 1);
	
	String[] sttys = stty.split(" ");
	String[] tmp = new String[sttys.length + 3];
	int ptr = 0;
	tmp[ptr++] = "stty";
	tmp[ptr++] = "-F";
	tmp[ptr++] = pts;
	for (final String param : sttys)
	    if (param.contains("=") == false)
		tmp[ptr++] = param;
	String[] params = new String[ptr];
	System.arraycopy(tmp, 0, params, 0, ptr);
	
	//String x = "";
	//for (final String p : params)
	//    x += " " + p;
	//System.out.println(x);
	
	execSystemProperty(pts, params);
	execSystemProperty("/dev/stdin", "stty", "-icanon", "-echo", "-isig", "-ixon", "-ixoff");
	
	final Process process = procBuilder.start();
	
	for (;;)
	    try
	    {   process.waitFor();
		process.exitValue();
		params[2] = (new File("/dev/stdin")).getCanonicalPath();
		execSystemProperty("/dev/stdin", params);
		ptmx.close();
		System.exit(process.exitValue());
	    }
	    catch (final Throwable err)
	    {   try
		{   Thread.sleep(200);
		}
		catch (final Throwable ierr)
	        {   // Just continue
	    }   }
    }
    
    
    
    /**
     * Creates a /dev/pts device
     * 
     * @param  fd  File descriptor
     */
    public static native String createPTS(final int fd);
    //javah7 -classpath . se.kth.maandree.noxutil.Recaps
    //gcc -I/opt/java7/include -shared se_kth_maandree_noxutil_Recaps.c -o libRecaps.so -fPIC
    
    
    
    /**
     * Executes a program
     * 
     * @param   pts   The PTS device
     * @param   cmds  Command line arguments
     * @return        The process builder
     */
    public static ProcessBuilder exec(final String pts, final String... cmds)
    {
	try
	{
	    final ProcessBuilder procBuilder = new ProcessBuilder(cmds);
	    
	    procBuilder.redirectError(ProcessBuilder.Redirect.INHERIT);
	    //procBuilder.redirectError(new File(pts));
	    procBuilder.redirectInput(new File(pts));
	    //procBuilder.redirectOutput(new File(pts));
	    procBuilder.redirectOutput(ProcessBuilder.Redirect.INHERIT);
	    
	    return procBuilder;
	}
        catch (final Throwable err)
	{
	    return null;
	}
    }
    
    /**
     * Gets or sets system properties by invoking another program
     * 
     * @param  pts   The PTS device
     * @param  cmd   The command to run
     */
    public static String execSystemProperty(final String pts, final String... cmd)
    {
	try
	{
	    byte[] buf = new byte[64];
	    int ptr = 0;
	    
	    final ProcessBuilder procBuilder = new ProcessBuilder(cmd);
	    //procBuilder.inheritIO();
	    procBuilder.redirectInput(ProcessBuilder.Redirect.from((new File(pts)).getCanonicalFile()));
	    final Process process = procBuilder.start();
	    final InputStream stream = process.getInputStream();
	    
	    for (int d; (d = stream.read()) != -1; )
	    {
		if (ptr == buf.length)
		{
		    final byte[] nbuf = new byte[ptr + 64];
		    System.arraycopy(buf, 0, nbuf, 0, ptr);
		    buf = nbuf;
		}
		buf[ptr++] = (byte)d;
	    }
	    
	    process.waitFor();
	    if (process.exitValue() != 0)
	    {   return null;
	    }
	    
	    return new String(buf, 0, ptr, "UTF-8");
	}
	catch (final Throwable err)
	{   return null;
	}
    }
    
}

