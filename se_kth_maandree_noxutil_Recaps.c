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
#include "se_kth_maandree_noxutil_Recaps.h"


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
 * Creates a PTS device
 *
 * @param  jenv  JNI parameter
 * @param  jobj  JNI parameter
 * @param  fd    The file descriptor
 *
 * @author  Mattias Andrée, <a href="mailto:maandree@kth.se">maandree@kth.se</a>
 */
JNIEXPORT jstring JNICALL Java_se_kth_maandree_noxutil_Recaps_createPTS(JNIEnv* jenv, jobject jobj, jint fd)
{
    String buf = malloc(sizeof(char) << 8);
    
    grantpt(fd);
    unlockpt(fd);
    ptsname_r(fd, buf, 256);
    
    jstring rc = (*jenv)->NewStringUTF(jenv, buf);
    free(buf);
    
    return rc;
}

