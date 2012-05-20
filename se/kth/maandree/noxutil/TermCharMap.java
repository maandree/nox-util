/**
 * TermCharMap — Character map for terminals
 * Copyright © 2011  Mattias Andrée <maandree@kth.se>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package se.kth.maandree.noxutil;

import se.kth.maandree.twt.*;

import java.util.*;
import java.io.*;


//TODO:  We need CJK character names…
//TODO:  Typing
//TODO:  Details
//TODO:  Scripts
//TODO:  Private block sets



/**
 * Terminal character map
 * 
 * @author  Mattias Andrée, <a href="mailto:maandree@kth.se">maandree@kth.se</a>
 */
public class TermCharMap implements TerminalReader.MouseListener, TerminalReader.KeyboardListener
{
    /**
     * The number of milliseconds between updatig if the terminal has been resized
     */
    private static final int UPDATE_RATE = 125;



    /**
     * Private constructor
     */
    private TermCharMap()
    {
        //For use of interface
    }



    /**
     * This is the main entry point of the (optionally stand-alone) subprogram
     *
     * @param  args  Start up arguments (unused)
     */
    public static void main(final String... args)
    {
	if (args.length == 1)
	    do
		try
		{
		    final String progfile = "se/kth/maandree/noxutil/TermCharMap.java";
		    String cmd = "echo ERROR";

		    if      (args[0].equals("--compile"))  cmd = "javac -cp . " + progfile;
		    else if (args[0].equals("--edit"))     cmd = "emacs -nw "   + progfile;
		    else if (args[0].equals("--md5"))      cmd = "md5sum " + progfile + " > " + progfile + ".md5";
		    else if (args[0].equals("--auto"))
		    {
			exec("cat " + progfile + ".md5 > " + progfile + ".md5~", false);
			exec("md5sum " + progfile + ".java > " + progfile + ".md5", false);
			if (identicalFile(progfile + ".md5~", progfile + ".md5"))
			    break;
			exec("javac -cp . " + progfile, true);
			exec("java -cp . se.kth.maandree.noxutil.TermCharMap", true);
			return;
		    }
		    else
			break;

		    exec(cmd, true);
		    return;
		}
		catch (final Throwable err)
	        {
		    err.printStackTrace(System.err);
		    return;
		}
	    while (false);


	FileInputStream fisBlocks = null, fisNames = null;
        try
	{
	    fisBlocks = new FileInputStream("se/kth/maandree/noxutil/charmap.blocks");
	    Scanner sc = new Scanner(fisBlocks);

	    while (sc.hasNext())
	    {
		final String line = sc.nextLine();
		if (line.isEmpty() || line.startsWith("#"))
		    continue;
		
		final String[] lineData = line.split("; ");
		TermCharMap.blocks.add(lineData[1] + "; " + lineData[0]);
	    }
	    Collections.sort(TermCharMap.blocks);

	    fisNames = new FileInputStream("charmap.names");
	    sc = new Scanner(fisNames);

	    while (sc.hasNext())
	    {
		String line = sc.nextLine();
		if (line.isEmpty())
		    continue;
		final char c = line.charAt(0);
		if (((('0' <= c) && (c <= '9')) || (('A' <= c) && (c <= 'F'))) == false)
		    continue;

		line = line.replace("\t", " ");
		line = line.replace("    ", " ");
		while (line.contains("  "))
		    line = line.replace("  ", " ");

		final String lineNumber = line.substring(0, line.indexOf(" "));
	        final String lineName = line.substring(line.indexOf(" ") + 1);

		TermCharMap.names.put(Integer.parseInt(lineNumber, 16), lineName);
	    }

	    TermCharMap.term   = new Terminal();
	    TermCharMap.width  = TermCharMap.term.getTerminalWidth();
	    TermCharMap.height = TermCharMap.term.getTerminalHeight();

	    final TermCharMap termCharMap = new TermCharMap();
	    final TerminalReader reader = new TerminalReader(TermCharMap.term, termCharMap, termCharMap);
	    TermCharMap.reader = reader;

	    exec("stty -isig", true);
	    term.clearScreen();
	    reader.setReadingMode(true);
	    term.flush();
	    try
	    {
		update();
		updateDaemon(UPDATE_RATE);
		reader.start();
	    }
	    finally
	    {
		exec("stty isig", true);
		reader.setReadingMode(false);
		TermCharMap.term.setColourStyle(null);
		TermCharMap.term.clearScreen();
		TermCharMap.term.flush();
	    }
	}
	catch (final IOException err)
	{
	    System.out.println("Sorry but it seems like there is some problem with the tty.");
	    System.err.println();
	    err.printStackTrace(System.err);
	    System.err.println();
        }
	finally
	{
	    if (fisBlocks != null)
		try
		{
		    fisBlocks.close();
		}
		catch (final Throwable err)
		{
		    //Ignore
		}

	    if (fisNames != null)
		try
		{
		    fisNames.close();
		}
		catch (final Throwable err)
		{
		    //Ignore
		}
	}
    }



    /**
     * The terminal
     */
    public static Terminal term;

    /**
     * The terminal reader
     */
    public static TerminalReader reader;

    /**
     * The width of the terminal
     */
    public static int width;

    /**
     * The height of the terminal
     */
    public static int height;

    /**
     * The Unicode blocks
     */
    public static final ArrayList<String> blocks = new ArrayList<String>();

    /**
     * The first block visible in the side panel
     */
    public static int blocksStart = 0;

    /**
     * The selected block in the side panel
     */
    public static int blocksSelected = 0;

    /**
     * The number character is the width of the block side panel
     */
    public static int blocksWidth = 26;

    /**
     * The names of the characters
     */
    public static final HashMap<Integer, String> names = new HashMap<Integer, String>();

    /**
     * The first visible character in the character table
     */
    public static int tableStart = 0;

    /**
     * The selected character index in the character table
     */
    public static int tableSelected = 0;

    /**
     * Whether the characters in the table should be bold
     */
    public static boolean previewBold = false;

    /**
     * Whether the characters in the table should be italic
     */
    public static boolean previewItalic = false;

    /**
     * The selected characters
     */
    public static String text = new String();



    /**
     * Updates the window
     *
     * @param  cells  The cells to update, ~ on the once in the side panel, none if everything
     */
    private static void update(final int... cells)
    {
	final int X = 5, Y = 3;

	synchronized (TermCharMap.class)
	{
	    term.clearScreen();
	    try
	    {
		TermCharMap.reader.setReadingMode(true);
	    }
	    catch (final Throwable err)
	    {
		//Ignore
	    }

	    term.goToRowAndColumn(0, 0, 0);
	    int bWidth = TermCharMap.blocksWidth;
	    bWidth += (TermCharMap.width - 1 - bWidth) % X;

	    final int tableWidth  = (TermCharMap.width - 1 - bWidth) / X;
	    final int tableHeight = (TermCharMap.height - 3)         / Y;

	    final String blockData = TermCharMap.blocks.get(TermCharMap.blocksSelected).split("; ")[1];
	    final int firstCharacter = Integer.parseInt(blockData.split("\\.\\.")[0], 16);
	    final int  lastCharacter = Integer.parseInt(blockData.split("\\.\\.")[1], 16);

	
	    if (cells.length == 0)
	    {
		term.setColourStyle(new ColourStyle(null, null, ColourStyle.REVERSE_VIDEO));
		term.insertBlankSpaces(TermCharMap.width);
		final String zBlocks    = ">Blocks<";
	        final String zScripts   = " Scripts ";
		final String zPrivate   = " Private ";
		final String zDetails   = " Details ";
		final String zBoldOpt   = "[" + (TermCharMap.previewBold   ? 'X' : ' ') + "]";
		final String zItalicOpt = "[" + (TermCharMap.previewItalic ? "X" : ' ') + "]";
		term.print(zBlocks + zScripts + zPrivate + ' ' + zDetails + "   " + zBoldOpt);
		term.setColourStyle(new ColourStyle(null, null, ColourStyle.BOLD | ColourStyle.REVERSE_VIDEO));
		term.print("B");
		term.setColourStyle(new ColourStyle(null, null, ColourStyle.REVERSE_VIDEO));
		term.print(" " + zItalicOpt);
		term.setColourStyle(new ColourStyle(null, null, ColourStyle.ITALIC | ColourStyle.REVERSE_VIDEO));
		term.print("I");
		term.setColourStyle(new ColourStyle(null, null, ColourStyle.REVERSE_VIDEO));
		term.print("   > ");
		term.setColourStyle(null);
		term.print(TermCharMap.text);
		term.setColourStyle(new ColourStyle(null, null, ColourStyle.REVERSE_VIDEO));
		term.print(" ");
		term.setColourStyle(null);
		term.println();
		
		term.setColourStyle(new ColourStyle(null, null, ColourStyle.REVERSE_VIDEO));
		for (int i = 1, n =  TermCharMap.height - 2; i < n; i++)
		{
		    term.goToRowAndColumn(i, bWidth, 0);
		    term.print(':');
		}

		term.goToRowAndColumn(TermCharMap.height, 1, 1);
		term.insertBlankSpaces(TermCharMap.width);
		term.goToRowAndColumn(TermCharMap.height - 1, 1, 1);
		term.insertBlankSpaces(TermCharMap.width);
		term.setColourStyle(null);

		term.goToRowAndColumn(0, 0, 0);
		final int bn = TermCharMap.blocks.size(), bm = TermCharMap.height - 3 + TermCharMap.blocksStart;
		for (int i = TermCharMap.blocksStart; (i < bn) && (i < bm); i++)
		{
		    term.println();
		    if (i == TermCharMap.blocksSelected)
			term.setColourStyle(new ColourStyle(null, Colour.SYSTEM_BLUE));
		    String block = TermCharMap.blocks.get(i);
		    block = block.substring(0, block.indexOf("; "));
		    if (block.length() > bWidth)
			block = block.substring(0, bWidth);
		    else if (i == TermCharMap.blocksSelected)
			for (int j = block.length(); j < bWidth; j++)
			    block += ' ';
		    term.print(block);
		    if (i == TermCharMap.blocksSelected)
			term.setColourStyle(null);
		}

		int previewStyle = ColourStyle.PLAIN;
		previewStyle |= TermCharMap.previewBold   ? ColourStyle.BOLD   : ColourStyle.PLAIN;
		previewStyle |= TermCharMap.previewItalic ? ColourStyle.ITALIC : ColourStyle.PLAIN;

		term.setColourStyle(new ColourStyle(null, null, previewStyle));
		final String backString;
		{
		    final char[] backCharacters = new char[X];
		    Arrays.fill(backCharacters, ' ');
		    backString = new String(backCharacters);
		}
		outer:
		    for (int y = 0; y < tableHeight; y++)
			for (int x = 0; x < tableWidth; x++)
			{
			    if (firstCharacter + y * tableWidth + x + tableStart > lastCharacter)
			    {
				term.setColourStyle(new ColourStyle(null, null, ColourStyle.REVERSE_VIDEO | previewStyle));
				{
				    final char[] backCharacters = new char[X * (tableWidth - x)];
				    Arrays.fill(backCharacters, ' ');
				    final String backStringL = new String(backCharacters);
				    for (int k = 1; k <= Y; k++)
				    {
					term.goToRowAndColumn(k + y * Y, bWidth + 1 + x * X, 0);
					term.print(backStringL);
				    }
				}
				{
				    final char[] backCharacters = new char[X * tableWidth];
				    Arrays.fill(backCharacters, ' ');
				    final String backStringL = new String(backCharacters);
				    for (int k = Y * y + Y, kn = TermCharMap.height - 2; k < kn; k++)
				    {
					term.goToRowAndColumn(1 + k, bWidth + 1, 0);
					term.print(backStringL);
				    }
				}
				break outer;
			    }

			    final int currentCharacter = firstCharacter + y * tableWidth + x + tableStart;
			    final Integer characterKey = Integer.valueOf(currentCharacter);

			    final boolean selected = (y * tableWidth + x + tableStart) == tableSelected;
			    final boolean notAssigned = TermCharMap.names.containsKey(characterKey) == false;
			    boolean notACharacter = false, reserved = false, control = false;
			    if (notAssigned == false)
			    {
				notACharacter = TermCharMap.names.get(characterKey).equals("<not a character>");
				reserved = TermCharMap.names.get(characterKey).equals("<reserved>");
				control  = TermCharMap.names.get(characterKey).equals("<control>");
			    }

			    Colour colour = Colour.SYSTEM_BLUE;
			    if (notACharacter)  colour = Colour.SYSTEM_RED;
			    if (notAssigned)    colour = Colour.SYSTEM_YELLOW;
			    if (reserved)       colour = Colour.SYSTEM_CYAN;
			    if (control)        colour = Colour.SYSTEM_MAGENTA;

			    if (selected || reserved || notACharacter || notAssigned || control)
			    {
				term.setColourStyle(new ColourStyle(null, colour, previewStyle));
				for (int k = 1; k <= Y; k++)
				{
				    term.goToRowAndColumn(k + y * Y, bWidth + 1 + x * X, 0);
				    term.print(backString);
				}

				if ((reserved || notACharacter || notAssigned || control) && selected)
				{
				    term.setColourStyle(new ColourStyle(null, Colour.SYSTEM_BLUE, previewStyle));
				    term.goToRowAndColumn(1 + y * Y, bWidth + 1 + x * X, 0);
				    term.print(backString);
				    term.goToRowAndColumn(Y + y * Y, bWidth + 1 + x * X, 0);
				    term.print(backString);
				    for (int k = 2; k < Y; k++)
				    {
					term.goToRowAndColumn(k + y * Y, bWidth + 1 + x * X, 0);
					term.print(' ');
					term.goToRowAndColumn(k + y * Y, bWidth + X + x * X, 0);
					term.print(' ');
				    }
				}
			    }
			    term.goToRowAndColumn(2 + y * Y, bWidth + (X >> 1) + x * X, 0);
			    if ((reserved || notAssigned || control) && selected)
				term.setColourStyle(new ColourStyle(null, colour, previewStyle));
			    if (notACharacter == false)
			    {
				term.print(" ");
				for (final int b : utf8Encode(firstCharacter + y * tableWidth + x + tableStart))
				    term.write(b);
			    }
			    if (selected || reserved || notACharacter || notAssigned || control)
				term.setColourStyle(new ColourStyle(null, null, previewStyle));
			}
	    }
	    else
	    {
		//XXX
	    }

	    final Integer selectedCharacter = Integer.valueOf(firstCharacter + tableSelected);
	    String characterLabel = Integer.toString(selectedCharacter.intValue(), 16).toUpperCase();
	    while (characterLabel.length() < 4)
		characterLabel = '0' + characterLabel;
	    characterLabel = "U+" + characterLabel + "  ";
	    if (TermCharMap.names.containsKey(selectedCharacter))
		characterLabel += TermCharMap.names.get(selectedCharacter);
	    else
		characterLabel += "<not assigned>";
	    
	    term.setColourStyle(new ColourStyle(null, null, ColourStyle.REVERSE_VIDEO));
	    term.goToRowAndColumn(TermCharMap.height - 2, 0, 0);
	    term.print(characterLabel);
	    term.goToRowAndColumn(TermCharMap.height - 1, 0, 0);
	    term.print(TermCharMap.blocks.get(TermCharMap.blocksSelected).replace("; ", "  U+").replace("..", "..U+"));
	    
	    term.setColourStyle(null);
	    term.flush();
	}
    }

    /**
     * Starts a daemon thread that automatically repaints the program if the terminal has been resized
     *
     * @param  rate  The number of milliseconds between updatig if the terminal has been resized
     */
    private static void updateDaemon(final int rate)
    {
	final Thread thread = new Thread()
	        {
	            /**
		     * The threads superrutine
		     */
		     @Override
		     public void run()
		     {
			try
			{
			    int lastX = TermCharMap.width;
			    int lastY = TermCharMap.height;

			    for (;;)
			    {
				int curX = TermCharMap.term.getTerminalWidth();
				int curY = TermCharMap.term.getTerminalHeight();

				Thread.sleep(rate);

				if ((curX != lastX) || (curY != lastY))
				{
				    lastX = TermCharMap.width  = curX;
				    lastY = TermCharMap.height = curY;


				    final int X = 5, Y = 3;

				    int bWidth = TermCharMap.blocksWidth;
				    bWidth += (TermCharMap.width - 1 - bWidth) % X;

				    final int w = (TermCharMap.width - 1 - bWidth) / X;
				    final int h = (TermCharMap.height - 3)         / Y;

				    final int selected = TermCharMap.tableSelected;
				    if (selected < TermCharMap.tableStart)
					TermCharMap.tableStart = selected - (selected % w);
				    else if (selected > TermCharMap.tableStart + w * h - w)
					TermCharMap.tableStart = selected - (selected % w) - w * h + w;

                                    TermCharMap.tableStart /= w;
				    TermCharMap.tableStart *= w;

				    String blockData = TermCharMap.blocks.get(TermCharMap.blocksSelected);
				    blockData = blockData.split("; ")[1];
				    final int firstCharacter = Integer.parseInt(blockData.split("\\.\\.")[0], 16);
				    final int  lastCharacter = Integer.parseInt(blockData.split("\\.\\.")[1], 16);

				    int max = lastCharacter - firstCharacter + 1;
				    max /= w;
				    max *= w;
				    max -= w * h - w;
				    if (max < 1)
					max = 1;

				    if (TermCharMap.tableStart >= max)
					TermCharMap.tableStart = max - 1;


				    TermCharMap.update();
				}
			    }
			}
			catch (final IOException err)
			{
			    System.out.println("Sorry but it seems like there is some problem with the tty.");
			    System.err.println();
			    err.printStackTrace(System.err);
			    System.err.println();
			    System.exit(0);
			}
			catch (final Throwable err)
			{
			    return;
		        }
		    }
	        };

	thread.setDaemon(true);
	thread.start();
    }

    /**
     * Encodes an integer long UCS-character to UTF-8
     *
     * @param   character  The character
     * @return             The encoding of the character
     */
    private static int[] utf8Encode(final int character)
    {
	final int[] rc;
	final int c = character;

	if      (character <= 0x0000007F)  rc = new int[] {c >>>  0};
	else if (character <= 0x000007FF)  rc = new int[] {c >>>  6, c >>>  0};
	else if (character <= 0x0000FFFF)  rc = new int[] {c >>> 12, c >>>  6, c >>>  0};
	else if (character <= 0x001FFFFF)  rc = new int[] {c >>> 18, c >>> 12, c >>>  6, c >>>  0};
	else if (character <= 0x03FFFFFF)  rc = new int[] {c >>> 24, c >>> 18, c >>> 12, c >>>  6, c >>> 0};
	else if (character <= 0x7FFFFFFF)  rc = new int[] {c >>> 30, c >>> 24, c >>> 18, c >>> 12, c >>> 6, c >>> 0};
	else
	{
	    assert false;
	    return new int[] {(int)' '};
	}
        
	if (rc.length == 1)
	    return rc;

	for (int i = 0; i < rc.length; i++)
	    rc[i] = (rc[i] & 63) | 128;
	int m = 256;
	for (int i = 0; i < rc.length; i++)
	    rc[0] |= (m >>>= 1);
	return rc;
    }


    /**
     * Invoked when a non-character key is being typed.
     *
     * @param   key                 The typed key.
     * @param   shift               Whether the shift               modifier is being held down.
     * @param   alternative         Whether the alternative         modifier is being held down.
     * @param   control             Whether the control             modifier is being held down.
     * @param   alternativeGraphic  Whether the alternative graphic modifier is being held down.
     * @return                      Return <code>true</code> iff the reading should be cancelled.
     */
    public boolean typed(final int key, final boolean shift, final boolean alternative, final boolean control, final boolean alternativeGraphic)
    {
	final int X = 5, Y = 3;

	int bWidth = TermCharMap.blocksWidth;
	bWidth += (TermCharMap.width - 1 - bWidth) % X;

	int sacg = 0;
	sacg += shift              ? 01000 : 0;
	sacg += alternative        ? 00100 : 0;
	sacg += control            ? 00010 : 0;
	sacg += alternativeGraphic ? 00001 : 0;
        
	if (((sacg == 00010) && (key == KEY_C)) || ((sacg == 00100) && (key == KEY_W)))
        {
	}
	if (((sacg == 00010) && (key == KEY_W)) || ((sacg == 00100) && (key == KEY_C)))
	{
	}
	if ((sacg == 00010) && (key == KEY_DELETE))
        {
	}
	if (((sacg == 00010) && (key == KEY_H)) || ((sacg == 00000) && (key == KEY_BACKSPACE)))
	{
	}
	if ((sacg == 00000) && (key == KEY_DELETE))
	{
	}
	if ((sacg == 00000) && (key == KEY_ENTER))
	{
	}
	if ((sacg == 00110) && (key == KEY_LEFT))
	{
	}
	if ((sacg == 00110) && (key == KEY_RIGHT))
	{
	}
	if ((sacg == 00000) && (key == KEY_HOME))
	{
	    System.out.println("HOME");
	    TermCharMap.tableStart = TermCharMap.tableSelected = 0;
	    TermCharMap.update();
	    return false;
	}
	if ((sacg == 00000) && ((key == KEY_END) || (key == KEY_PAGE_UP) || (key == KEY_PAGE_DOWN)))
	{
	    final int w = (TermCharMap.width - 1 - bWidth) / X;
	    final int h = (TermCharMap.height - 3)         / Y;

	    final String blockData = TermCharMap.blocks.get(TermCharMap.blocksSelected).split("; ")[1];
	    final int firstCharacter = Integer.parseInt(blockData.split("\\.\\.")[0], 16);
	    final int  lastCharacter = Integer.parseInt(blockData.split("\\.\\.")[1], 16);
	    
	    int max = lastCharacter - firstCharacter;

	    if (key == KEY_END)
		TermCharMap.tableSelected = TermCharMap.tableStart = max;
	    else if (key == KEY_PAGE_DOWN)
	    {
		TermCharMap.tableSelected += w * h;
		TermCharMap.tableStart += w * h;
		if (TermCharMap.tableSelected > max)
		    TermCharMap.tableSelected = max;
	    }
	    else if (key == KEY_PAGE_UP)
	    {
		TermCharMap.tableSelected -= w * h;
		TermCharMap.tableStart -= w * h;
		if (TermCharMap.tableSelected < 0)
		    TermCharMap.tableSelected = 0;
		if (TermCharMap.tableStart < 0)
		    TermCharMap.tableStart = 0;

		TermCharMap.update();
		return false;
	    }

	    max -= max % w;
	    max -= w * h - w;

	    if (max < 0)
		max = 0;

	    if (TermCharMap.tableStart > max)
		TermCharMap.tableStart = max;

	    TermCharMap.update();
	    return false;
	}
	if (((sacg == 01000) && (key == KEY_HOME)) || ((sacg == 00010) && (key == KEY_F)))
	{
	    TermCharMap.blocksStart = TermCharMap.blocksSelected = 0;
	    TermCharMap.update();
	    return false;
	}
	if (((sacg == 01000) && (key == KEY_END)) || ((sacg == 00010) && (key == KEY_V)))
	{
	    final int h = TermCharMap.height - 3;
	    TermCharMap.blocksSelected = TermCharMap.blocks.size() - 1;
	    TermCharMap.blocksStart = TermCharMap.blocks.size() - h;
	    TermCharMap.update();
	    return false;
	}
	if (((sacg == 01000) && (key == KEY_PAGE_UP)) || ((sacg == 00010) && (key == KEY_G)))
	{
	    if (TermCharMap.blocksSelected != TermCharMap.blocksStart)
		TermCharMap.blocksSelected = TermCharMap.blocksStart;
	    else
	    {
		final int h = TermCharMap.height - 3;
		TermCharMap.blocksStart -= h;
		if (TermCharMap.blocksStart < 0)
		    TermCharMap.blocksStart = 0;
		TermCharMap.blocksSelected = TermCharMap.blocksStart;
	    }
	    TermCharMap.update();
	    return false;
	}
	if (((sacg == 01000) && (key == KEY_PAGE_DOWN)) || ((sacg == 00010) && (key == KEY_B)))
	{
	    final int h = TermCharMap.height - 3;
	    if (TermCharMap.blocksSelected != TermCharMap.blocksStart + h - 1)
		TermCharMap.blocksSelected = TermCharMap.blocksStart + h - 1;
	    else
	    {
		TermCharMap.blocksStart += h;
		TermCharMap.blocksSelected += h;
	    }
	    if (TermCharMap.blocksSelected > TermCharMap.blocks.size())
		TermCharMap.blocksSelected = TermCharMap.blocks.size() - 1;
	    if (TermCharMap.blocksStart > TermCharMap.blocks.size() - h)
		TermCharMap.blocksStart = TermCharMap.blocks.size() - h;
	    TermCharMap.update();
	    return false;
	}

	if ((sacg == 00010) && (key == KEY_B))
	{
	    TermCharMap.previewBold = !TermCharMap.previewBold;
	    TermCharMap.update();
	    return false;
	}
	if (((sacg == 00010) && (key == KEY_I)) || /*synonym*/((sacg == 00000) && (key == KEY_TAB)))
        {
	    TermCharMap.previewItalic = !TermCharMap.previewItalic;
	    TermCharMap.update();
	    return false;
	}

	if ((sacg == 00010) && (key == KEY_X))
	    return true;

	if (((sacg == 0) || (sacg == 00100)) && ((key == KEY_UP) || (key == KEY_DOWN) || (key == KEY_LEFT) || (key == KEY_RIGHT)))
	{
	    final int multiplier = sacg == 0 ? 1 : 3;
	    final int w = (TermCharMap.width - 1 - bWidth) / X;
	    final int h = (TermCharMap.height - 3)         / Y;

	    if      (key == KEY_UP)     TermCharMap.tableSelected -= multiplier * w;
	    else if (key == KEY_DOWN)   TermCharMap.tableSelected += multiplier * w;
	    else if (key == KEY_LEFT)   TermCharMap.tableSelected -= multiplier;
	    else if (key == KEY_RIGHT)  TermCharMap.tableSelected += multiplier;

	    if (TermCharMap.tableSelected < 0)
		TermCharMap.tableSelected = 0;
	    else
	    {
		final String blockData = TermCharMap.blocks.get(TermCharMap.blocksSelected).split("; ")[1];
		final int firstCharacter = Integer.parseInt(blockData.split("\\.\\.")[0], 16);
		final int  lastCharacter = Integer.parseInt(blockData.split("\\.\\.")[1], 16);
		final int max = lastCharacter - firstCharacter;

		if (TermCharMap.tableSelected > max)
		    TermCharMap.tableSelected = max;
	    }

	    if (TermCharMap.tableSelected < TermCharMap.tableStart)
		TermCharMap.tableStart = TermCharMap.tableSelected - (TermCharMap.tableSelected % w);
	    else if (TermCharMap.tableSelected > TermCharMap.tableStart + w * h - w)
		TermCharMap.tableStart = TermCharMap.tableSelected - (TermCharMap.tableSelected % w) - w * h + w;

	    TermCharMap.update();
	}

	if ((sacg != 0) && !control && !alternativeGraphic)
	    if      (key == KEY_UP)    return this.mouse(SCROLL_UP,   shift, alternative, control, -1, -1);
	    else if (key == KEY_DOWN)  return this.mouse(SCROLL_DOWN, shift, alternative, control, -1, -1);

	return false;
    }

    /**
     * Invoked when a character key is being typed.
     *
     * @param   data  One of the characters bytes of the typed character (received in order).
     * @return        Return <code>true</code> iff the reading should be cancelled.
     */
    public boolean typed(final int data)
    {
        return false;
    }

    /**
     * Invoked when the mouse does something.
     *
     * @param   action       The mouse action.
     * @param   shift        Whether the shift       modifier is being held down.
     * @param   alternative  Whether the alternative modifier is being held down.
     * @param   control      Whether the control     modifier is being held down.
     * @param   x            The mouse's position on in the terminal on the x-axis (base 0).
     * @param   y            The mouse's position on in the terminal on the y-axis (base 0).
     * @return               Return <code>true</code> iff the reading should be cancelled.
     */
    public boolean mouse(final int action, final boolean shift, final boolean alternative, final boolean control, final int x, final int y)
    {
	final int X = 5, Y = 3;
	final int sac = (shift ? 0100 : 0) + (alternative ? 0010 : 0) + (control ? 0001 : 0);
	//System.out.println(Integer.toString(sac, 8));
	//System.out.println(action);

	int bWidth = TermCharMap.blocksWidth;
	bWidth += (TermCharMap.width - 1 - bWidth) % X;

	int tHeight = TermCharMap.height - 3;
	tHeight = tHeight / Y * Y;

	final boolean insideSide = (0 <= x) && (x < bWidth) && (1 <= y) && (y < TermCharMap.height - 2);
	final boolean nonaltSide = sac == (insideSide ? 0000 : 0100);
	final boolean altSide = sac == (insideSide ? 0010 : 0110);

	final boolean insideTable = (bWidth < x) && (1 <= y) && (y < tHeight);
	final boolean insideMenu = y == 0;

	if (insideTable && (sac == 0000) && (action == PRESSED_LEFT))
	{
	    final int tx = (x - bWidth - 1) / X;
	    final int ty = (y - 1) / Y;
	    final int tw = (TermCharMap.width - bWidth - 1) / X;

	    final int selectedIndex = TermCharMap.tableStart + tx + ty * tw;

            final String blockData = TermCharMap.blocks.get(TermCharMap.blocksSelected).split("; ")[1];
            final int firstCharacter = Integer.parseInt(blockData.split("\\.\\.")[0], 16);
            final int  lastCharacter = Integer.parseInt(blockData.split("\\.\\.")[1], 16);

	    final int selectedCharacter = selectedIndex + firstCharacter;

	    if (selectedCharacter <= lastCharacter)
	    {
		TermCharMap.tableSelected = selectedIndex;
		TermCharMap.update();
	    }
	}
	else if (insideSide && (sac == 0000) && (action == PRESSED_LEFT))
	{
	    TermCharMap.blocksSelected = TermCharMap.blocksStart + y - 1;
	    if (TermCharMap.blocksSelected >= TermCharMap.blocks.size())
		TermCharMap.blocksSelected = TermCharMap.blocks.size() - 1;
	    TermCharMap.tableSelected = TermCharMap.tableStart = 0;
	    TermCharMap.update();
	}
	else if (nonaltSide && (action == SCROLL_UP))
	{
	    TermCharMap.blocksSelected--;
	    if (TermCharMap.blocksSelected < 0)
		TermCharMap.blocksSelected = 0;
	    if (TermCharMap.blocksStart > TermCharMap.blocksSelected)
		TermCharMap.blocksStart = TermCharMap.blocksSelected;
	    TermCharMap.tableSelected = TermCharMap.tableStart = 0;
	    TermCharMap.update();
	}
	else if (nonaltSide && (action == SCROLL_DOWN))
	{
	    TermCharMap.blocksSelected++;
	    if (TermCharMap.blocksSelected >= TermCharMap.blocks.size())
		TermCharMap.blocksSelected = TermCharMap.blocks.size() - 1;
	    if (TermCharMap.blocksSelected >= TermCharMap.blocksStart + TermCharMap.height - 3)
		TermCharMap.blocksStart = TermCharMap.blocksSelected - TermCharMap.height + 4;
	    TermCharMap.tableSelected = TermCharMap.tableStart = 0;
	    TermCharMap.update();
	}
	else if (altSide && (action == SCROLL_UP))
	{
	    TermCharMap.blocksStart -= 3;
	    if (TermCharMap.blocksStart < 0)
		TermCharMap.blocksStart = 0;
	    TermCharMap.tableSelected = TermCharMap.tableStart = 0;
	    TermCharMap.update();
	}
	else if (altSide && (action == SCROLL_DOWN))
	{
	    TermCharMap.blocksStart += 3;
	    if (TermCharMap.blocksStart > TermCharMap.blocks.size() - TermCharMap.height + 3)
		TermCharMap.blocksStart = TermCharMap.blocks.size() - TermCharMap.height + 3;
	    TermCharMap.tableSelected = TermCharMap.tableStart = 0;
	    TermCharMap.update();
	}
	else if (insideTable && (action == SCROLL_UP))
	{
	    final int w = (TermCharMap.width - 1 - bWidth) / X;
	    TermCharMap.tableStart -= w;
	    if (TermCharMap.tableStart < 0)
		TermCharMap.tableStart = 0;
	    TermCharMap.update();
	}
	else if (insideTable && (action == SCROLL_DOWN))
	{
	    final int w = (TermCharMap.width - 1 - bWidth) / X;
	    final int h = (TermCharMap.height - 3) / Y;

	    final String blockData = TermCharMap.blocks.get(TermCharMap.blocksSelected).split("; ")[1];
            final int firstCharacter = Integer.parseInt(blockData.split("\\.\\.")[0], 16);
            final int  lastCharacter = Integer.parseInt(blockData.split("\\.\\.")[1], 16);

	    int max = lastCharacter - firstCharacter + 1;
	    max /= w;
	    max *= w;
	    max -= w * h - w;
	    if (max < 1)
		max = 1;

	    TermCharMap.tableStart += w;
	    if (TermCharMap.tableStart >= max)
		TermCharMap.tableStart = max - 1;
	    TermCharMap.update();
	}

        return false;
    }

    /**
     * Starts an external program
     *
     * @param   cmd      The execution command
     * @param   forward  Whether to forward input and output
     * @return           Any thrown error
     */
    private static Throwable exec(final String cmd, final boolean forward)
    {
	try
	{
	    String opts = new String();

	    if (forward)
	    {
		final String ttyin  = (new File("/dev/stdin" )).getCanonicalPath();
		final String ttyout = (new File("/dev/stdout")).getCanonicalPath();
		final String ttyerr = (new File("/dev/stderr")).getCanonicalPath();

		opts += " < " + ttyin + " > " + ttyout + " 2> " + ttyerr;
	    }

	    (new ProcessBuilder("/bin/sh", "-c", cmd + opts)).start().waitFor();
	}
	catch (final Throwable err)
	{
	    return err;
	}
	return null;
    }

    private static boolean identicalFile(final String file0, final String file1)
    {
	InputStream is0 = null, is1 = null;
	try
	{
	    is0 = new FileInputStream(file0);
	    is1 = new FileInputStream(file1);
	    
	    for (int d, b;;)
	    {
		d = is0.read();
		b = is1.read();
		if (d != b)   return false;
		if (d == -1)  return true;
	    }
	}
	catch (final Throwable err)
	{
	    return false;
	}
	finally
	{
	    if (is0 != null)
		try
		{
		    is0.close();
		}
	        catch (final Throwable err)
		{
		    //Do nothing
		}
	    if (is1 != null)
		try
		{
		    is1.close();
		}
		catch (final Throwable err)
		{
		    //Do nothing
		}
	}
    }

}

