/**
 * This part of nox-util is public domain.
 */
package se.kth.maandree.noxutil;

import java.io.*;
import java.util.*;


/**
 * This is the main class of the program
 *
 * @author  Mattias Andrée, <a href="mailto:maandree@kth.se">maandree@kth.se</a>
 */
public class Winise
{
    /**
     * Non-constructor
     */
    private Winise()
    {
	assert false : "This class [Winise] is not meant to be instansiated.";
    }
    
    
    
    /**
     * This is the main entry point of the program
     *
     * @param  args  Executing arguments
     */
    public static void main(final String... args)
    {
	System.out.print("\033[0m");
	
	final ArrayList<String> files = new ArrayList<String>();
	boolean keepEncoding = false;
	boolean help = args.length == 0;
	boolean dashed = false;
	
	for (final String arg : args)
	    if (dashed)
		files.add(arg);
	    else if (arg.equals("--"))
		dashed = true;
	    else if (arg.equals("-e") || arg.equals("-k") || arg.equals("--keep-encoding"))
		keepEncoding = true;
	    else if (arg.equals("-?") || arg.equals("-h") || arg.equals("--help"))
		help = true;
	    else if (arg.startsWith("-"))
	    {
		System.out.println("\033[31;1mUnrecoginsed flag: " + arg + "\033[0m");
		dashed = keepEncoding = false;
		files.clear();
		help = true;
		break;
	    }
	    else
		files.add(arg);
	
	if (help)
	{
	    System.out.println("");
	    System.out.println("Winise - Tool for converting your plaintext files to a plaintext");
	    System.out.println("         files suitable for your core Windows user friends.");
	    System.out.println("");
	    if (!dashed && !keepEncoding)
	    {
		System.out.println("This program adds a UTF-8 signature to your files, converts all");
		System.out.println("new line characters to Windows's standard character combination");
		System.out.println("for new lines, ensures that the file tile only contains characters");
		System.out.println("supported by Windows, fixes casing issuses for Windows and adds");
		System.out.println(".txt to the end of the file namn (even if it alreadt exists).");
		System.out.println("");
		System.out.println("");
		System.out.println("USAGE: [-e | -k | --keep-encoding] [--] FILES...");
		System.out.println("");
		System.out.println("SYNOPSIS:");
		System.out.println("");
		System.out.println("--                 Disable parsing successing arguments as flags.");
		System.out.println("");
		System.out.println("-e");
		System.out.println("-k");
		System.out.println("--keep-encoding    Use this flag if your file is not in UTF-8.");
		System.out.println("                   [Execute with -h -e for more information...]");
	    }
	    else if (dashed)
	    {
		System.out.println("--                 Disable parsing successing arguments as flags.");
	    }
	    else if (keepEncoding)
	    {
		System.out.println("-e");
		System.out.println("-k");
		System.out.println("--keep-encoding    Use this flag if your file is not in UTF-8.");
		System.out.println("                   [Actually the encoding will never be changed,");
		System.out.println("                   however, a BOM/signature may be added.]");
		System.out.println("                   CAUTION: We do not known how well line breaking");
		System.out.println("                            converting works with other encodings");
		System.out.println("                            than UTF-8.");
	    }
	    System.out.println("");
	    System.out.println("");
	    System.out.println("Public domain, 2012, Mattias Andrée, maandree@kth.se");
	    System.out.println("");
	}
	
	
	for (final String file : files)
	    try
	    {
		final File ifile = new File(file);

		String dir = file.substring(0, file.lastIndexOf("/") + 1);
		String title = file.substring(file.lastIndexOf("/") + 1);
	    
		title = title.replace("\\", "[%]")
		             .replace( ":", "[;]")
		             .replace( "*", "[#]")
		             .replace( "?", "[Q]")
		             .replace("\"", "['']")
		             .replace( "<", "[(]")
		             .replace( ">", "[)]")
		             .replace( "|", "[-]");
	    
		File ofile = new File(dir + title + ".txt");
		if (ofile.exists())
		{
		    String base = dir + title + " (";
		    for (int i = 2; ofile.exists(); i++)
			ofile = new File(base + i + ").txt");
		}
		
		convert(ifile, ofile, !keepEncoding);
	    }
	    catch (final Throwable err)
	    {
		System.out.println("\033[0;31mAn error occured while converting " + file + "\033[0m");
		System.err.println(err);
	    }
    }
    
    
    /**
     * Reads an file and stores its content to another file while
     * convertering the content to fit Microsoft Notepad, as included
     * in Microsoft Windows releases.
     * 
     * @param   ifile        The input file
     * @param   ofile        The output file
     * @param   bom          Whether to as UTF-8 signature ("BOM") if missing
     * @throws  IOException  On I/O exception
     */
    public static void convert(final File ifile, final File ofile, final boolean bom) throws IOException
    {
	InputStream  is = null;
	OutputStream os = null;
	try
	{
	    is = new FileInputStream (ifile);
	    os = new FileOutputStream(ofile);
	    
	    final int total = is.available();
	    int read = 0;
	    
	    final int SIZE = 1 << 20;
	    final byte[] bs = new byte[SIZE];
	    final byte[] ob = new byte[SIZE << 1];
	    boolean onCR = false;
	    
	    while (read < total)
	    {
		int n = is.read(bs, 0, SIZE);
		if ((read == 0) && bom)
		    if (total < 3)
			os.write(new byte[] {(byte)0xEF, (byte)0xBB, (byte)0xBF});
		    else
		    {
			while (n < 3)
			{   read += n;
			    n = is.read(bs, read, SIZE - read);
			}
			read = 0;
			boolean utf8 = true;
			utf8 &= bs[0] == 0xEF;
			utf8 &= bs[1] == 0xBB;
			utf8 &= bs[2] == 0xBF;
			if (utf8 == false)
			    os.write(new byte[] {(byte)0xEF, (byte)0xBB, (byte)0xBF});
		    }
		if (n < 0)
		    break;
		if (n == 0)
		    try
		    {   Thread.sleep(100);
		    }
		    catch (final InterruptedException err)
		    {   return;
		    }
		else
		{
		    read += n;
		    int out = 0;
		    byte b;
		    for (int i = 0; i < n; i++)
			if ((b = bs[i]) == 10)
			{
			    if (onCR == false)
				ob[out++] = 13;
			    ob[out++] = 10;
			    onCR = false;
			}
		        else
			{
			    if (onCR)
				ob[out++] = 10;
			    onCR = b == 13;
			    ob[out++] = b;
			}
		    
		    os.write(ob, 0, out);
		}
	    }
	    
	    os.flush();
	}
	finally
        {
	    try
	    {   if (is != null)
		    is.close();
	    }
	    catch (final Throwable err)
	    {   System.err.println("Can't close input stream.");
	    }
	    
	    try
	    {   if (os != null)
		    os.close();
	    }
	    catch (final Throwable err)
	    {   System.err.println("Can't close output stream.");
	    }
	}
    }
    
}

