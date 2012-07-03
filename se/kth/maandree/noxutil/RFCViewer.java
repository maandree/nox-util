/**
 * RFC Viewer – Request for Comments (RFC) memorandum viewer for RFC
 *              databases built with se.kth.maandree.rfcdownloader
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


//UNUSED ARGUMENTS:
//     def   jk     q stu  xyz
//  ABCDEFGHIJKLM OPQRSTUVWXYZ
//  0123456789

/**
 * <p>This is the main class of the program</p>
 * <pre>
 *
 *   RFC Viewer –  Offline Request for Comments (RFC) memorandum viewer
 *                 and briefcase for RFC databases built with the program
 *                 se.kth.maandree.rfcdownloader or manually with files from
 *                 http://www.rfc-editor.org/download.html (luxaries are
 *                 currently not supported).
 *
 *
 *   USAGE:    rfc {@FILES} [-bchilmnNpvw] [-g TITLE [-a INDEX:TITLE] | -a TITLE]
 *                 [-o FROM:TO] [-r INDEX] [--] {INDICES}
 *   
 *   VERSION:  1.0 pre-beta
 *
 *
 *   SYNOPSIS:
 *
 *       --                   Avoids parsing all following arguments as options.
 *
 *       @FILE                Include the options stored in a file.
 *                            FILE = the file with the options.
 *
 *       -a TITLE
 *       --add=TITLE          [Without specified group]
 *                            Creates a RFC group.
 *                            TITLE = the new group's title.
 *
 *       -a INDEX:TITLE
 *       --add=INDEX:TITLE    [With specified group]
 *                            Adds a RFC to the specified group.
 *                            INDEX = the index of the RFC memorandum.
 *                            TITLE = the RFC's title as displied in the list.
 *
 *       -b
 *       --batch              Run in batch mode (non-interactive).
 *                            Batch mode is useful for pipelining.
 *
 *       -c
 *       --copying
 *       --copyright          Displays the copyright notice.    ****PENDING****
 *
 *       -g TITLE
 *       --group=TITLE        Specify the RFC group.
 *                            TITLE = the title of the group.
 *
 *       -h
 *       --help               Print this help text.
 *                            This nullifies all other directives. ****ENHANCEMENT PENDING****
 *
 *       -i
 *       --install            Lets the program do the best it can to install all   ****PENDING****
 *                            RFC entries to the local database.
 *
 *       -l
 *       --list               List all created RFC groups or all RFC in a
 *                            specified (with -g) groups.
 *
 *       -m
 *       --missing            List all missing memorandum indices.
 *                            Will list all missing files, if an index is
 *                            missing in the IETF RFC database it will be
 *                            listed anyway. The last RFC is not known by
 *                            the program.
 *
 *       -n
 *       --no                 Assume no.
 *
 *       -N
 *       --hardno             Do no removals at all.
 *
 *       -p
 *       --pdf                Open .pdf.xz memoranda using evince.
 *                            Recommended for graphical environments.
 *
 *       -o FROM:TO
 *       --reorder=FROM:TO    Moves an item to a new index.
 *                            FROM = the item's old index.
 *                            TO   = the item's new index.
 *
 *       -r INDEX
 *       --remove=INDEX       Removes and item from a specified (with -g)
 *                            group or a group by its list index.
 *                            INDEX = the items index in the lists.
 *
 *       -v
 *       --version            Displays the program namn, vendor and version      ****PENDING****
 *                            of the program.
 *
 *       -w
 *       --warranty
 *       --nonwarranty
 *       --non-warranty
 *       --disclaimer         Displays the non-warranty notice.     ****PENDING****
 *
 *
 *       These are arguments you can specify when starting the program,
 *       in the same command/line.
 *
 *
 *   COMMANDS
 *
 *       up/down      Select the item above/below the currently select item.
 * 
 *       left
 *       backspace    Go up one level if possible.
 * 
 *       right
 *       space
 *       enter        Open the selected item.
 * 
 *       t            Open the selected item in text mode if it is a RFC.
 * 
 *       p            Open the selected item with Evince if it is a RFC.
 * 
 *       del          Delete the selected menu item.
 * 
 *       a            Add a new item.
 * 
 *       e            Edit the selected item.
 * 
 *       m            Move the selected item.
 * 
 *       h            Show a quick help reference.
 *
 *
 *       These are key strokes you can use during the interactive session
 *       to perform various actions.
 *
 *
 *   INTERNAL SYSOPSIS
 *
 *       ---atparsing    Used for stupid support for complex expressing on @files.
 *
 *
 *       These are argument you should not add (except after -h) when starting
 *       the program; they are created for internal use only. You may use them,
 *       but there result will not be of interest for general use of the program.
 *
 * 
 *   NON-WARRANTY
 *
 *       This program is distributed in the hope that it will be useful,
 *       but WITHOUT ANY WARRANTY; without even the implied warranty of
 *       MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *       GNU General Public License for more details.
 *
 *
 *   COPYRIGHT
 *
 *       Copyright © 2012  Mattias Andrée (maandree@kth.se)
 * 
 *       This program is free software: you can redistribute it and/or modify
 *       it under the terms of the GNU General Public License as published by
 *       the Free Software Foundation, either version 3 of the License, or
 *       (at your option) any later version.
 * 
 *       You should have received a copy of the GNU General Public License
 *       along with this program.  If not, see &lt;http://www.gnu.org/licenses/&gt;.
 *
 * </pre>
 * @author   Mattias Andrée, <a href="mailto:maandree@kth.se">maandree@kth.se</a>
 * @version  1.0 pre-beta
 */
public class RFCViewer //FIXME  handle invalid arguments
{
    /**
     * This is the main entry point of the program
     *
     * @param   args       Startup arguments
     * @throws  Exception  On any exception
     */
    public static void main(final String... args) throws Exception
    {
	try
        {
	    ttyin  = (new File("/dev/stdin" )).getCanonicalPath();
	    ttyout = (new File("/dev/stdout")).getCanonicalPath();
	    ttyerr = (new File("/dev/stderr")).getCanonicalPath();
	    
	    scanner = new Scanner(System.in);
	    
	    final Arguments arguments = parseArguments(args, "-a", "-g", "-r", "-o");
	    ArrayList<String> database = new ArrayList<String>();
	    InputStream dbis = null;
	    
	    try
	    {
		dbis = new BufferedInputStream(new FileInputStream("/usr/share/nox-util/rfc/rfc.db"));
		
		final Scanner dbsc = new Scanner(dbis, "UTF-8");
		while (dbsc.hasNext())
		{
		    String line = dbsc.nextLine().replace("\t", "        ");
		    int s = 0;
		    while (line.charAt(s) == ' ')
			s++;
		    line = line.substring(s, line.length());
		    
		    if (line.isEmpty())
			continue;
		    
		    database.add(line);
		}
	    }
	    catch (final FileNotFoundException err)
	    {
		database = null;
	    }
	    finally
	    {
		if (dbis != null)
		    dbis.close();
	    }
	    

	    if ((args.length >= 0) && (args[0].equals("---atparsing")))
	    {
		for (int i = 1; i < args.length; i++)
		{
		    int n = 0;
		    final char[] cs = new char[(args[i].length() << 1) + 1];
		    
		    char c;
		    final String arg = args[i];
		    for (int j = 0; j < arg.length(); j++)
		        switch (c = arg.charAt(j))
			{
			    case '\\':
			    case ' ':
				cs[n++] = '\\';
				//$FALL-THROUGH$
			    default:
				cs[n++] = c;
				break;
			}
		    
		    cs[n++] = ' ';
		    System.out.print(new String(cs, 0, n));
		}
		return;
	    }

	    
	    if (arguments.options.containsKey("-h") || arguments.options.containsKey("--help"))
	    {
		System.out.println(
	        "\033[32;1mRFC Viewer\033[0m –  Offline Request for Comments (RFC) memorandum viewer"                  +"\n"+
		                 "              and briefcase for RFC databases built with the program"                +"\n"+
		                 "              se.kth.maandree.rfcdownloader or manually with files from"             +"\n"+
		          "\033[1m              http://www.rfc-editor.org/download.html\033[0;35m (luxaries are"       +"\n"+
		                 "              currently not supported).\033[0m"                                      +"\n"+
                ""                                                                                                     +"\n"+
		""                                                                                                     +"\n"+
 "\033[33mUSAGE:\033[0m    rfc {@FILES} [-bchilmnNpvw] [-g TITLE [-a INDEX:TITLE] | -a TITLE]"                         +"\n"+
                "              [-o FROM:TO] [-r INDEX] [--] {INDICES}"                                                 +"\n"+
		""                                                                                                     +"\n"+
		"\033[33mVERSION:\033[0m  1.0 pre-beta"                                                                +"\n"+
		""                                                                                                     +"\n"+
		""                                                                                                     +"\n"+
		"\033[33mSYNOPSIS:\033[0m"                                                                             +"\n"+
		"\033[36;1m"                                                                                           +"\n"+
      "    @\033[0;36mFILE               \033[0m Include the options stored in a file.\033[34m "                       +"\n"+
		"                         FILE = the file with the options.\033[0m"                                    +"\n"+
                "\033[36;1m"                                                                                           +"\n"+
	        "    -a\033[0;36m TITLE\033[1m"                                                                        +"\n"+
      "    --add=\033[0;36mTITLE         \033[34;1m [Without specified group] \033[0m"                                 +"\n"+
		"                         Creates a RFC group.\033[34m "                                               +"\n"+
		"                         TITLE = the new group's title.\033[0m"                                       +"\n"+
		"\033[36;1m"                                                                                           +"\n"+
		"    -a\033[0;36m INDEX\033[1m:\033[0;36mTITLE\033[1m"                                                 +"\n"+
      "    --add=\033[0;36mINDEX\033[1m:\033[0;36mTITLE   \033[34;1m [With specified group] \033[0m"                   +"\n"+
		"                         Adds a RFC to the specified group.\033[34m "                                 +"\n"+
		"                         INDEX = the index of the RFC memorandum."                                    +"\n"+
		"                         TITLE = the RFC's title as displied in the list.\033[0m"                     +"\n"+
		"\033[36;1m"                                                                                           +"\n"+
		"    -b"                                                                                               +"\n"+
		"    --batch             \033[0m Run in batch mode (non-interactive)."                                 +"\n"+
		"                         Batch mode is useful for pipelining."                                        +"\n"+
		"\033[36;1m"                                                                                           +"\n"+
		"    -g \033[0;36mTITLE\033[1m"                                                                        +"\n"+
      "    --group=\033[0;36mTITLE       \033[0m Specify the RFC group.\033[34m"                                       +"\n"+
		"                         TITLE = the title of the group.\033[0m"                                      +"\n"+
		"\033[36;1m"                                                                                           +"\n"+
		"    -h"                                                                                               +"\n"+
		"    --help              \033[0m Print this help text."                                                +"\n"+
		"                         This nullifies all other directives."                                        +"\n"+
		"\033[36;1m"                                                                                           +"\n"+
		"    -l"                                                                                               +"\n"+
		"    --list              \033[0m List all created RFC groups or all RFC in a"                          +"\n"+
		"                         specified (with -g) groups."                                                 +"\n"+
		"\033[36;1m"                                                                                           +"\n"+
		"    -m"                                                                                               +"\n"+
		"    --missing           \033[0m List all missing memorandum indices."                                 +"\n"+
		"                         Will list all missing files, if an index is"                                 +"\n"+
		"                         missing in the IETF RFC database it will be"                                 +"\n"+
		"                         listed anyway. The last RFC is not known by"                                 +"\n"+
		"                         the program."                                                                +"\n"+
		"\033[36;1m"                                                                                           +"\n"+
		"    -n"                                                                                               +"\n"+
		"    --no                \033[0m Assume no."                                                           +"\n"+
		"\033[36;1m"                                                                                           +"\n"+
		"    -N"                                                                                               +"\n"+
		"    --hardno            \033[0m Do no removals at all."                                               +"\n"+
		"\033[36;1m"                                                                                           +"\n"+
		"    -p"                                                                                               +"\n"+
		"    --pdf               \033[0m Open .pdf.xz memoranda using evince."                                 +"\n"+
		"                         Recommended for graphical environments."                                     +"\n"+
		"\033[36;1m"                                                                                           +"\n"+
		"    -o \033[0;36mFROM\033[1m:\033[0;36mTO\033[1m"                                                     +"\n"+
       "    --reorder=\033[0;36mFROM\033[1m:\033[0;36mTO   \033[0m Moves an item to a new index.\033[34m "             +"\n"+
		"                         FROM = the item's old index."                                                +"\n"+
		"                         TO   = the item's new index.\033[0m"                                         +"\n"+
		"\033[36;1m"                                                                                           +"\n"+
		"    -r \033[0;36mINDEX\033[1m"                                                                        +"\n"+
      "    --remove=\033[0;36mINDEX      \033[0m Removes and item from a specified (with -g)"                          +"\n"+
		"                         group or a group by its list index.\033[34m "                                +"\n"+
		"                         INDEX = the items index in the lists.\033[0m"                                +"\n"+
		""                                                                                                     +"\n"+
		"\033[35m"                                                                                             +"\n"+
		"    These are arguments you can specify when starting the program,"                                   +"\n"+
		"    in the same command/line.\033[0m"                                                                 +"\n"+
		""                                                                                                     +"\n"+
		""                                                                                                     +"\n"+
		"\033[33mCOMMANDS\033[0m"                                                                              +"\n"+
		"\033[36;1m"                                                                                           +"\n"+
		"    up/down\033[0m      Select the item above/below the currently select item."                       +"\n"+
		"\033[36;1m"                                                                                           +"\n"+
		"    left"                                                                                             +"\n"+
		"    backspace\033[0m    Go up one level if possible."                                                 +"\n"+
		"\033[36;1m"                                                                                           +"\n"+
		"    right"                                                                                            +"\n"+
		"    space"                                                                                            +"\n"+
		"    enter\033[0m        Open the selected item."                                                      +"\n"+
		"\033[36;1m"                                                                                           +"\n"+
		"    t\033[0m            Open the selected item in text mode if it is a RFC."                          +"\n"+
		"\033[36;1m"                                                                                           +"\n"+
		"    p\033[0m            Open the selected item with Evince if it is a RFC."                           +"\n"+
		"\033[36;1m"                                                                                           +"\n"+
		"    del\033[0m          Delete the selected menu item."                                               +"\n"+
		"\033[36;1m"                                                                                           +"\n"+
		"    a\033[0m            Add a new item."                                                              +"\n"+
		"\033[36;1m"                                                                                           +"\n"+
		"    e\033[0m            Edit the selected item."                                                      +"\n"+
		"\033[36;1m"                                                                                           +"\n"+
		"    m\033[0m            Move the selected item."                                                      +"\n"+
		"\033[36;1m"                                                                                           +"\n"+
		"    h\033[0m            Show a quick help reference."                                                 +"\n"+
		""                                                                                                     +"\n"+
		"\033[35m"                                                                                             +"\n"+
		"    These are key strokes you can use during the interactive session"                                 +"\n"+
		"    to perform various actions.\033[0m"                                                               +"\n"+
		""                                                                                                     +"\n"+
		""                                                                                                     +"\n"+
		"\033[33mINTERNAL SYNOPSIS\033[0m"                                                                     +"\n"+
		"\033[36;1m"                                                                                           +"\n"+
		"    ---atparsing\033[0m    Used for stupid support for complex expressing on @files."                 +"\n"+
		""                                                                                                     +"\n"+
		"\033[35m"                                                                                             +"\n"+
		"    These are argument you should not add (except after -h) when starting"                            +"\n"+
		"    the program; they are created for internal use only. You may use them,"                           +"\n"+
		"    but there result will not be of interest for general use of the program.\033[0m"                  +"\n"+
		""                                                                                                     +"\n"+
		""                                                                                                     +"\n"+
		"\033[33mNON-WARRANTY\033[0m"                                                                          +"\n"+
		""                                                                                                     +"\n"+
		"    This program is distributed in the hope that it will be useful,"                                  +"\n"+
		"    but WITHOUT ANY WARRANTY; without even the implied warranty of"                                   +"\n"+
		"    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the"                                    +"\n"+
		"    GNU General Public License for more details."                                                     +"\n"+
		""                                                                                                     +"\n"+
		""                                                                                                     +"\n"+
		"\033[33mCOPYING\033[0m"                                                                               +"\n"+
		""                                                                                                     +"\n"+
		"    Copyright © 2012  Mattias Andrée (\033[1mmaandree@kth.se\033[0m)"                                 +"\n"+
		""                                                                                                     +"\n"+
		"    This program is free software: you can redistribute it and/or modify"                             +"\n"+
		"    it under the terms of the GNU General Public License as published by"                             +"\n"+
		"    the Free Software Foundation, either version 3 of the License, or"                                +"\n"+
		"    (at your option) any later version."                                                              +"\n"+
		""                                                                                                     +"\n"+
		"    You should have received a copy of the GNU General Public License"                                +"\n"+
		"    along with this program.  If not, see <\033[1mhttp://www.gnu.org/licenses/\033[0m>."
		);
		return;
	    }
	    
	    final boolean batch = arguments.options.containsKey("-b") || arguments.options.containsKey("--batch");
	    
	    database = batch(arguments, database);
	    
	    if (batch)
		return;
	    
	    //FIXME  start interactive terminal mode
	}
	catch (final Exception err)
	{
	    System.err.println("\033[31mFatal exception:\033[m\n");
	    throw err;
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
     * The stdin scanner
     */
    private static Scanner scanner;
    
    
    
    /**
     * Parsed start up argument set
     */
    public static class Arguments
    {
	/**
	 * Constructor
	 *
	 * @param  options  The options
	 * @param  files    The files
	 */
	@SuppressWarnings("hiding")
	public Arguments(final HashMap<String, String> options, final ArrayList<String> files)
	{
	    this.options = options;
	    this.files = files;
	}
	
	
	
	/**
	 * The options
	 */
	public final HashMap<String, String> options;
	
	/**
	 * The files
	 */
	public final ArrayList<String> files;

    }
    
    
    
    /**
     * Executes a program pipeline in the same terminal
     *
     * @param   cmds         The program commands
     * @throws  IOException  On I/O exception
     */
    public static void exec(final String... cmds) throws IOException
    {
	final StringBuilder tot = new StringBuilder();
	for (int i = 0, n = cmds.length - 1; i <= n; i++)
	{
	    tot.append(cmds[i]);
	    tot.append(" 2> ");
	    tot.append(ttyerr);
	    if (i == 0)
	    {
		tot.append(" < ");
		tot.append(ttyin);
	    }
	    if (i == n)
	    {
		tot.append(" > ");
		tot.append(ttyout);
	    }
	    else
		tot.append(" | ");
	}
	final Process process = (new ProcessBuilder("/bin/sh", "-c", tot.toString())).start();
	final InputStream stream = process.getInputStream();
	for (;;)
	    if (stream.read() == -1)
		break;
	stream.close();
    }
    
    /**
     * Executes a program pipeline in the same terminal without stdout redirection
     *
     * @param   cmds         The program commands
     * @return               String the stdout output
     * @throws  IOException  On I/O exception
     */
    public static String execQuiet(final String... cmds) throws IOException
    {
	final StringBuilder tot = new StringBuilder();
	for (int i = 0, n = cmds.length - 1; i <= n; i++)
	{
	    tot.append(cmds[i]);
	    tot.append(" 2> ");
	    tot.append(ttyerr);
	    if (i == 0)
	    {
		tot.append(" < ");
		tot.append(ttyin);
	    }
	    else
		tot.append(" | ");
	}
	final Process process = (new ProcessBuilder("/bin/sh", "-c", tot.toString())).start();
	final InputStream stream = process.getInputStream();
	final StringBuilder sb = new StringBuilder();
	for (int d;;)
	{
	    if ((d = stream.read()) == -1)
		break;
	    sb.append(d);
	}
	return sb.toString();
    }
    
    /**
     * Parses the startup arguments
     *
     * @param   args           The startup arguments
     * @param   parameterised  The short arguments which are parameterised
     * @return                 The set of options and files
     */
    public static Arguments parseArguments(final String[] args, final String... parameterised)
    {
	final HashSet<String> pset = new HashSet<String>();
	for (final String arg : parameterised)
	    pset.add(arg);
	
	final ArrayList<String> argList = new ArrayList<String>();
	for (final String arg : args)
	    argList.add(arg);
	
	final ArrayList<String> files = new ArrayList<String>();
	final HashMap<String, String> optSet = new HashMap<String, String>();
	boolean dashed = false;
	
	for (int j = 0; j < argList.size(); j++)
        {
	    final String arg = argList.get(j);
	    
	    if (dashed)
		files.add(arg);
	    else if (arg.equals("--"))
		dashed = true;
	    else if (arg.startsWith("@"))
	    {
		InputStream atis = null;
		try
		{
		    atis = new FileInputStream(arg.substring(1));
		    final byte[] bs = new byte[atis.available()];
		    atis.read(bs);
		    final String content = new String(bs, "UTF-8");
		    final String returned = execQuiet("java -cp . se.kth.maandree.rfc.Program ---atparsing " + content);
		    //returned will always end with a space.
		    
		    boolean esc = false;
		    StringBuilder sb = new StringBuilder();
		    for (int i = 0, n = returned.length(); i < n; i++)
		    {
			final char c = returned.charAt(i);
			if (esc)
			    esc = false;
			else if (c == '\\')
			    esc = true;
			else if (c == ' ')
			{
			    argList.add(j, sb.toString());
			    sb = new StringBuilder();
			}
			else
			    sb.append(c);
		    }
		}
		catch (final Exception err)
		{
		    throw new RuntimeException("Could not apply @:ed arguments");
		}
		finally
		{
		    if (atis != null)
			try
			{
			    atis.close();
			}
			catch (final Exception err)
			{
			    throw new RuntimeException("Could not apply @:ed arguments");
			}
		}
	    }
	    else if (arg.startsWith("-") && !(arg.startsWith("--")))
		for (int i = 1, n = arg.length(); i < n; i++)
		{
		    final String opt = "-" + arg.charAt(i);
		    if (pset.contains(opt))
			optSet.put(opt, args[++j]);
		    else
			optSet.put(opt, null);
		}
	    else if (arg.startsWith("--"))
		if (arg.contains("="))
		{
		    final int e = arg.indexOf("=");
		    optSet.put(arg.substring(0, e), arg.substring(e + 1, arg.length()));
		}
		else
		    optSet.put(arg, null);
	    else
		files.add(arg);
	}
	
	return new Arguments(optSet, files);
    }
    
    
    /**
     * Prints a text line in orange to stderr
     *
     * @param  text  The text to print
     */
    private static void errprintln(final String text)
    {
	System.err.println("\033[33m" + text + "\033[0m");
    }
    
    /**
     * Prints a text without a line ending in orange to stderr
     *
     * @param  text  The text to print
     */
    private static void errprint(final String text)
    {
	System.err.print("\033[33m" + text + "\033[0m");
    }
    
    
    /**
     * Performs batch instructions
     *
     * @param   arguments    Start up arguments
     * @param   _database    The RFC database
     * @return               The RFC database
     * @throws  IOException  On I/O exception
     */
    private static ArrayList<String> batch(final Arguments arguments, final ArrayList<String> _database) throws IOException
    {
	ArrayList<String> database = _database;
	
	final HashMap<String, String> optSet = arguments.options;
	final ArrayList<String>       files  = arguments.files;
	
	
	final boolean add     = optSet.containsKey("-a") || optSet.containsKey("--add");
	final boolean batch   = optSet.containsKey("-b") || optSet.containsKey("--batch");
	//-c --copying --copyright
	//def
	final boolean group   = optSet.containsKey("-g") || optSet.containsKey("--group");
	//-i --install
	//jk
	final boolean list    = optSet.containsKey("-l") || optSet.containsKey("--list");
	final boolean missing = optSet.containsKey("-m") || optSet.containsKey("--missing");
	final boolean no      = optSet.containsKey("-n") || optSet.containsKey("--no");
	final boolean hardno  = optSet.containsKey("-N") || optSet.containsKey("--hardno");
	final boolean reorder = optSet.containsKey("-o") || optSet.containsKey("--reorder");
	final boolean evince  = optSet.containsKey("-p") || optSet.containsKey("--pdf");
	//q
	final boolean remove  = optSet.containsKey("-r") || optSet.containsKey("--remove");
	//stu
	//-v --version
	//-w --warranty --nonwarranty --non-warranty --disclaimer
	//xyz
	
	
	if (add     && !(optSet.containsKey("-a")))  optSet.put("-a", optSet.get("--add"));
	if (group   && !(optSet.containsKey("-g")))  optSet.put("-g", optSet.get("--group"));
	if (remove  && !(optSet.containsKey("-r")))  optSet.put("-r", optSet.get("--remove"));
	if (reorder && !(optSet.containsKey("-o")))  optSet.put("-o", optSet.get("--reorder"));
        
	/////////////////////////////////////////////////////////////////////////////////////////
	///                                     Open files                                    ///
	/////////////////////////////////////////////////////////////////////////////////////////

	if (files.size() > 0)
	    if (evince)
	    {
		final StringBuilder all = new StringBuilder("evince");
		for (final String file : files)
		{
		    final int index = Integer.parseInt(file);
		    final int groupi = index / 50 * 50;
		    
		    all.append(" /usr/share/nox-util/rfc/" + groupi + "/rfc" + index + ".pdf.xz");
		}
		all.append(" &");
		exec(all.toString());
	    }
	    else
		for (final String file : files)
		{
		    final int index = Integer.parseInt(file);
		    final int groupi = index / 50 * 50;
		    
		    exec("xz -dc /usr/share/nox-util/rfc/" + groupi + "/rfc" + index + ".xz", "less -r");
		}

	/////////////////////////////////////////////////////////////////////////////////////////
	///                                 List missing files                                ///
	/////////////////////////////////////////////////////////////////////////////////////////	

	if (missing)
	{
	    final HashSet<Integer> dirs = new HashSet<Integer>();
	    int max = -1;
	    for (final String path : (new File(".")).list())
		if ((new File("./" + path)).isDirectory())
		{
		    final Integer cur = Integer.valueOf(path);
		    dirs.add(cur);
		    if (cur.intValue() > max)
			max = cur.intValue();
		}
	    if (max < 0)
		errprintln("Database is empty, use se.kth.maandree.rfcdownloader");
	    else
	    {
		int[] plain = new int[max + 50];
		int[] pdf   = new int[max + 50];
		int plainptr = 0;
		int pdfptr = 0;
		
		for (int i = 0; i <= max; i += 50)
		    if ((new File("/usr/share/nox-util/rfc/" + i)).exists())
			for (final String path : (new File("/usr/share/nox-util/rfc/" + i)).list())
			    if (path.startsWith("rfc") && path.endsWith(".pdf.xz"))
				pdf[pdfptr++] = Integer.parseInt(path.substring(3, path.length() - 7));
			    else if (path.startsWith("rfc") && path.endsWith(".xz"))
				plain[plainptr++] = Integer.parseInt(path.substring(3, path.length() - 3));
		
		int last = 0;
		for (int[] x : new int[][] {plain, pdf})
		    for (int item : x)
			if (last < item)
			    last = item;
		
		final BitSet plainSet = new BitSet(last + 1);
		final BitSet pdfSet   = new BitSet(last + 1);
		
		for (int item : plain)  plainSet.set(item);
		for (int item : pdf)    pdfSet  .set(item);
		
		final int[] mode = new int[last + 1];
		for (int i = 1; i <= last; i++)
		    mode[i] = (plainSet.get(i) ? 0 : 1) | (pdfSet.get(i) ? 0 : 2);
		
		int m = 0;
		int start = 0;
		for (int i = 1; i <= last; i++)
		{
		    final int nm = mode[i];
		    if ((nm != m) || (i == last))
		    {
			if (m != 0)
		        {
			    final int e = i - 1;
			    if (start == e)
				System.out.print(start);
			    else
				System.out.print(start + "-" + e);
			    
			    if      (m == 3)  System.out.println();
			    else if (m == 2)  System.out.println(" (pdf)");
			    else if (m == 1)  System.out.println(" (plain)");
			}
			m = nm;
			start = i;
		    }
		}
		
		System.out.println("Last index = " + last);
	    }
		
	    if (batch == false)
		for (;;)
		    if (System.in.read() == 10)
			break;
	}
	
	/////////////////////////////////////////////////////////////////////////////////////////
	///                                     List items                                    ///
	/////////////////////////////////////////////////////////////////////////////////////////
	
	
	if (list)
	{
	    if (database == null)
		errprintln("No database has been created!");
	    else if (group)
	    {
		int start = database.indexOf("::" + optSet.get("-g")) + 1;
		int index = 0;
		if (database == null)
		    errprintln("Can't list items: No such group!");
		else
		    for (int i = start; i < database.size(); i++)
		    {
			if (database.get(i).startsWith("::"))
			    break;
			System.out.printf("%3d. %s\n", index++, database.get(i));
		    }
	    }
	    else
	    {
		int index = 0;
		for (final String line : database)
		    if (line.startsWith("::"))
			System.out.printf("%3d. %s\n", index++, line.substring(2));
	    }
	}
	
	/////////////////////////////////////////////////////////////////////////////////////////
	///                                    Remove items                                   ///
	/////////////////////////////////////////////////////////////////////////////////////////

	if (remove)
	    removeItem: do
	    {
		if (database == null)
		{
		    errprintln("Can't remove item: No database has been created!");
		    break;
		}
		
		if (hardno)
		{
		    errprintln("Can't remove item: -N is on!");
		    break;
		}
		
		if (group)
		{
		    int start = database.indexOf("::" + optSet.get("-g")) + 1;
		    int end;
		    for (end = start; end < database.size(); end++)
			if (database.get(end).startsWith("::"))
			    break;
		    end--;
		    
		    int index = Integer.parseInt(optSet.get("-r")) + start;
		    if (start <= 0)
		    {
			errprintln("Can't remove item: No such group!");
			break;
		    }
		    if ((index < start) || (start > end))
		    {
			errprintln("Can't remove item: Index out of range!");
			break;
		    }
		    
		    if (batch == false)
		    {
			errprintln("You have selected: " + database.get(index));
			errprint("Are you sure you want to remove the item? (yes/no) ");
			for (;;)
			{
			    final String input = scanner.nextLine().toLowerCase();
			    if (input.equals("no"))
				break removeItem;
			    if (input.equals("yes"))
				break;
			    errprintln("Invalid option, only ‘yes’ and ‘no’ is allowed.");
			}
		    }
		    
		    database.remove(index);
		}
		else
		{
		    int index = 0, want = Integer.parseInt(optSet.get("-r"));
		    for (int i = 0; i < database.size(); i++)
			if (database.get(i).startsWith("::"))
			    if (index++ == want)
			    {
				boolean empty = i + 1 == database.size();
				if (empty)
				    empty = database.get(i + 1).startsWith("::");
				
				if (no && !empty)
				{
				    errprintln("Can't remove item: It is not empty and -n is on!");
				    break removeItem;
				}
				
				if (batch == false)
				{
				    errprintln("You have selected: " + database.get(i));
				    if (empty == false)
					errprintln("\033[1mWarning!\033[m Not empty!");
				    errprint("Are you sure you want to remove the item? (yes/no) ");
				    for (;;)
				    {
					final String input = scanner.nextLine().toLowerCase();
					if (input.equals("no"))
					    break removeItem;
					if (input.equals("yes"))
					    break;
					errprintln("Invalid option, only ‘yes’ and ‘no’ is allowed.");
				    }
				}
				
				database.remove(i);
				while (i < database.size())
				{
				    if (database.get(i).startsWith("::"))
					break;
				    database.remove(i);
				}
				break removeItem;
			    }
		    
		    errprintln("Can't remove item: Index out of range!");
		}
	    }
	      while (false);
	
	/////////////////////////////////////////////////////////////////////////////////////////
	///                                Create database file                               ///
	/////////////////////////////////////////////////////////////////////////////////////////
	
	if (add)
	    if (database == null)
	    {
		database = new ArrayList<String>();
		exec("echo -n '' >> /usr/share/nox-util/rfc/rfc.db");
	    }
	

	/////////////////////////////////////////////////////////////////////////////////////////
	///                                     Add groups                                    ///
	/////////////////////////////////////////////////////////////////////////////////////////
	
	if (add && !(group && database.contains("::" + optSet.get("-g"))))
	    database.add("::" + optSet.get(group ? "-g" : "-a"));

	
	/////////////////////////////////////////////////////////////////////////////////////////
	///                                      Add RFCs                                     ///
	/////////////////////////////////////////////////////////////////////////////////////////
	
	if (add && group)
	    addRFC: do
	    {
		if (optSet.get("-a").contains(":") == false)
		{
		    errprintln("Syntax error: -a has tail argument INDEX:TITLE when used with -g!");
		    break;
		}
		String astart = optSet.get("-a").substring(0, optSet.get("-a").indexOf(":"));
		Integer.parseInt(astart); //just an assertion
		astart += ':';
		
		int start = database.indexOf("::" + optSet.get("-g")) + 1;
		int end;
		if (start <= 0)
		{
		    errprintln("Can't add item: No such group!");
		    break;
		}
		for (end = start; end < database.size(); end++)
		{
		    if (database.get(end).startsWith("::"))
			break;
		    if (database.get(end).startsWith(astart))
		    {
			errprintln("Can't add RFC: the RFC is already listed in the group!");
			break addRFC;
		    }
		}
		
		database.add(end, optSet.get("-a"));
	    }
	      while (false);

	/////////////////////////////////////////////////////////////////////////////////////////
	///                                     Move items                                    ///
	/////////////////////////////////////////////////////////////////////////////////////////
	
	
	if (reorder)
	    do
	    {
		if (database == null)
		{
		    errprintln("Can't reorder item: No database has been created!");
		    break;
		}
		
		if (optSet.get("-o").contains(":") == false)
		{
		    errprintln("Syntax error: -o has tail argument FROM:TO!");
		    break;
		}
		String zfrom = optSet.get("-o").substring(0, optSet.get("-o").indexOf(":"));
		String zto   = optSet.get("-o").substring(optSet.get("-o").indexOf(":") + 1);
		int from = Integer.parseInt(zfrom);
		int to   = Integer.parseInt(zto);

		if (group)
		{
		    int start = database.indexOf("::" + optSet.get("-g")) + 1;
		    int end;
		    for (end = start; end < database.size(); end++)
			if (database.get(end).startsWith("::"))
			    break;
		    
		    from += start;
		    end += start;
		    
		    if (start <= 0)
		    {
			errprintln("Can't move item: No such group!");
			break;
		    }
		    if ((from < start) || (from > end) || (to < start) || (to > end))
		    {
			errprintln("Can't reorder item: index or indices out of range!");
			break;
		    }
		    
		    final String polled = database.get(from);
		    database.remove(from);
		    database.add(to + 1, polled);
		}
		else
		{
		    int index = 0;
		    int pos = 0;
		    for (final String line : database)
		    {
			if (line.startsWith("::"))
			    if (index != from)
				index++;
			    else
				break;
			pos++;
		    }
		    
		    if (to > from)
			to--;
		    
		    final ArrayList<String> polled = new ArrayList<String>();
		    do
		    {
			polled.add(database.get(pos));
			database.remove(pos);
		    }
		      while ((pos < database.size()) && (database.get(pos).startsWith("::") == false));
		    
		    index = pos = 0;
		    for (final String line : database)
		    {
			if (line.startsWith("::"))
			    if (index != to)
				index++;
			    else
				break;
			pos++;
		    }
		    
		    for (final String item : polled)
			database.add(pos, item);
		}
	    }
	      while (false);
	
	/////////////////////////////////////////////////////////////////////////////////////////
	///                                 Save database file                                ///
	/////////////////////////////////////////////////////////////////////////////////////////
	
	if (database != null)
        {
	    final OutputStream dbos = new BufferedOutputStream(new FileOutputStream("/usr/share/nox-util/rfc/rfc.db"));
	    
	    try
	    {
		for (final String line : database)
		{
		    dbos.write(line.getBytes("UTF-8"));
		    dbos.write(10);
		}
		dbos.flush();
	    }
	    finally
	    {
		dbos.close();
	    }
	}
	
	
	
	return database;
    }
    
}

