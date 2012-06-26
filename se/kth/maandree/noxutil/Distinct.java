/**
 * Distinct - Tool for filtering output only distinct (unique) lines
 * 
 * Public domain, 2012, Mattias Andrée, maandree@kth.se
 */
package se.kth.maandree.noxutil;

import java.io.*;
import java.util.*;


/**
 * This is the main class of the program
 *
 * @author  Mattias Andrée, <a href="mailto:maandree@kth.se">maandree@kth.se</a>
 */
public class Distinct
{
    /**
     * This is the main entry point of the program
     *
     * @param  args  Executing arguments: lines treated as already listed
     */
    public static void main(final String... args) throws Exception
    {
	final int SIZE = 1 << 10;
	
	final HashSet<String> set = new HashSet<String>();
	for (final String arg : args)
	    set.add(arg);
	
	final BufferedInputStream is = new BufferedInputStream(System.in);
	final ArrayDeque<byte[]> chunks = new ArrayDeque<byte[]>();
	byte[] bs = new byte[SIZE];
	int ptr = 0;
	boolean first = true;
	
	int d, last = 0;
	while ((d = is.read()) != -1)
	{
	    last = d;
	    if (d == 10)
	    {
		{
		    final byte[] data = new byte[chunks.size() * SIZE + ptr];
		    byte[] part;
		    int pos = 0;
		    while ((part = chunks.poll()) != null)
		    {
			System.arraycopy(part, 0, data, pos, SIZE);
			pos += SIZE;
		    }
		    System.arraycopy(bs, 0, data, pos, ptr);
		    final String line = new String(data, "UTF-8");
		    if (set.contains(line) == false)
		    {
			if (first == false)
			    System.out.println();
			first = false;
			set.add(line);
			System.out.print(line);
		    }
		}
		ptr = 0;
		continue;
	    }
	    
	    bs[ptr++] = (byte)d;
	    if (ptr == SIZE)
	    {
		chunks.offer(bs);
		bs = new byte[SIZE];
		ptr = 0;
	    }
	}
	
	{
	    final byte[] data = new byte[chunks.size() * SIZE + ptr];
	    byte[] part;
	    int pos = 0;
	    while ((part = chunks.poll()) != null)
	    {
		System.arraycopy(part, 0, data, pos, SIZE);
		pos += SIZE;
	    }
	    System.arraycopy(bs, 0, data, pos, ptr);
	    final String line = new String(data, "UTF-8");
	    if (set.contains(line) == false)
	    {
		if (first == false)
		    System.out.println();
		first = false;
		set.add(line);
		System.out.print(line);
	    }
	}
	if (last == 10)
	    System.out.println();
    }
    
}

