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

#include <stdio.h>
    // FILE* fopen(const String name, const String options);
    // int fileno(FILE *stream);

#define _XOPEN_SOURCE
#define _GNU_SOURCE
#include <stdlib.h>
    // int grantpt(int fd);
    // int unlockpt(int fd);
    // int ptsname_r(int fd, char *buf, size_t buflen);
    // int system(const char *command);


#define  String   char*
#define  File     FILE*


/**
 * Test program for creates PTS devices
 *
 * @param  argc  Startup argument count
 * @param  argv  Startup arguments
 *
 * @author  Mattias Andrée, <a href="mailto:maandree@kth.se">maandree@kth.se</a>
 */
int main(int argc, String* argv)
{
    File ptmx = fopen("/dev/ptmx", "w");
    int fd = fileno(ptmx);
    int err;
    
    String buf = malloc(sizeof(char) << 8);
    
    err = grantpt(fd);              if (err)  return err;
    err = unlockpt(fd);             if (err)  return err;
    err = ptsname_r(fd, buf, 256);  if (err)  return err;
    
    File pts = fopen(buf, "w");
    
    printf("%s\n", buf);
    free(buf);
    
    fclose(ptmx);
    fclose(pts);
}

