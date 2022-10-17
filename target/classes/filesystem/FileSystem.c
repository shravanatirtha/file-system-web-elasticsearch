// typedef struct _WIN32_FIND_DATA { // wfd
//     DWORD dwFileAttributes;
//     FILETIME ftCreationTime;
//     FILETIME ftLastAccessTime;
//     FILETIME ftLastWriteTime;
//     DWORD    nFileSizeHigh;
//     DWORD    nFileSizeLow;
//     DWORD    dwReserved0;
//     DWORD    dwReserved1;
//     TCHAR    cFileName[ MAX_PATH ];
//     TCHAR    cAlternateFileName[ 14 ];
// } WIN32_FIND_DATA;

// FILE_ATTRIBUTE_ARCHIVE	The file or directory is an archive file or directory. Applications use this attribute to mark files for backup or removal.
// FILE_ATTRIBUTE_COMPRESSED	The file or directory is compressed. For a file, this means that all of the data in the file is compressed. For a directory, this means that compression is the default for newly created files and subdirectories.
// FILE_ATTRIBUTE_DIRECTORY	The handle identifies a directory.
// FILE_ATTRIBUTE_ENCRYPTED	The file or directory is encrypted. For a file, this means that all data streams are encrypted. For a directory, this means that encryption is the default for newly created files and subdirectories.
// FILE_ATTRIBUTE_HIDDEN	The file or directory is hidden. It is not included in an ordinary directory listing.
// FILE_ATTRIBUTE_NORMAL	The file or directory has no other attributes set. This attribute is valid only if used alone.
// FILE_ATTRIBUTE_OFFLINE	The file data is not immediately available. Indicates that the file data has been physically moved to offline storage.
// FILE_ATTRIBUTE_READONLY	The file or directory is read-only. Applications can read the file but cannot write to it or delete it. In the case of a directory, applications cannot delete it.
// FILE_ATTRIBUTE_REPARSE_POINT	The file has an associated reparse point.
// FILE_ATTRIBUTE_SPARSE_FILE	The file is a sparse file.
// FILE_ATTRIBUTE_SYSTEM	The file or directory is part of the operating system or is used exclusively by the operating system.
// FILE_ATTRIBUTE_TEMPORARY	The file is being used for temporary storage. File systems attempt to keep all of the data in memory for quicker access, rather than flushing it back to mass storage. A temporary file should be deleted by the application as soon as it is no longer needed.

#include "C:/Program Files/Java/jdk1.8.0_333/include/jni.h"
#include "C:/Program Files/Java/jdk1.8.0_333/include/win32/jni_md.h"
#include <stdio.h>
#include "FileSystem.h"
#include <windows.h>
#include <dirent.h>
char **ref;
int j = 0;
void path_array(char *path);
int path_size(char *path, int size);
int files(char *path, int file_count);
int directories(char *path, int directory_count);
JNIEXPORT jobjectArray JNICALL Java_filesystem_PathServlet_filePath(JNIEnv *env, jobject clsobj, jstring path, jint dir_count)
{
	 const char *pathName = (*env)->GetStringUTFChars(env, path, NULL);
	    ref = (char **)malloc(sizeof(char *) * dir_count);
	    path_array(pathName);
	    jclass stringClass = (*env)->FindClass(env, "java/lang/String");
	    jobjectArray stringArray = (*env)->NewObjectArray(env, dir_count, stringClass, NULL);
	    for (int i = 0; i < dir_count; i++)
	    {
	        jstring string = (*env)->NewStringUTF(env, ref[i]);
	        (*env)->SetObjectArrayElement(env, stringArray, i, string);
	    }
	    return stringArray;
}
JNIEXPORT jobjectArray JNICALL Java_filesystem_SyncServlet_filePath(JNIEnv *env, jobject clsobj, jstring path, jint dir_count)
{
	 const char *pathName = (*env)->GetStringUTFChars(env, path, NULL);
	    ref = (char **)malloc(sizeof(char *) * dir_count);
	    path_array(pathName);
	    jclass stringClass = (*env)->FindClass(env, "java/lang/String");
	    jobjectArray stringArray = (*env)->NewObjectArray(env, dir_count, stringClass, NULL);
	    for (int i = 0; i < dir_count; i++)
	    {
	        jstring string = (*env)->NewStringUTF(env, ref[i]);
	        (*env)->SetObjectArrayElement(env, stringArray, i, string);
	    }
	    return stringArray;
}
// char **str_path = path_array(pathName, dir_count);
// jstring str;
// jobjectArray p = 0;
// jsize len = dir_count;
// int i;

// p = (*env)->NewObjectArray(env, len, (*env)->FindClass(env, "java/lang/String"), 0);

// for (i = 0; i < dir_count; i++)
// {
//     str = (*env)->NewStringUTF(env, str_path[i]);
//     (*env)->SetObjectArrayElement(env, p, i, str);
// }

// return p;

void path_array(char *path)
{
    char file_path[1000];
    sprintf(file_path, "%s\\*.*", path);
    HANDLE path_handle = NULL;
    WIN32_FIND_DATA data_file;
    if ((path_handle = FindFirstFile(file_path, &data_file)) == INVALID_HANDLE_VALUE)
    {
        // printf("Cannot be accessed: [%s]\n", path);
    }
    while (FindNextFile(path_handle, &data_file))
    {
        if (strcmp(data_file.cFileName, ".") != 0 && strcmp(data_file.cFileName, "..") != 0 && strcmp(data_file.cFileName, ".metadata") != 0 && strcmp(data_file.cFileName, ".settings") != 0 && (strcmp(data_file.cFileName, ".git") != 0 && strcmp(data_file.cFileName, ".vscode") != 0) && (strcmp(data_file.cFileName, ".config") != 0 && strcmp(data_file.cFileName, "System Volume Information") != 0))
        {
            sprintf(file_path, "%s\\%s", path, data_file.cFileName);
            if (data_file.dwFileAttributes & FILE_ATTRIBUTE_DIRECTORY)
            {
                ref[j] = (char *)malloc(1000 * sizeof(char));
                strcpy(ref[j], file_path);
                j++;
                path_array(file_path);
            }
        }
    }
    FindClose(path_handle);
}

JNIEXPORT jobject JNICALL Java_filesystem_PathServlet_fileSystem(JNIEnv *env, jobject clsobj, jobject path)
{
    const char *pathName = (*env)->GetStringUTFChars(env, path, NULL);
    int directoryCount = directories(pathName, 0);
    int fileCount = files(pathName, 0);
    int totalSize = path_size(pathName, 0);
    jclass cls = (*env)->FindClass(env, "filesystem/FileInfo");
    jmethodID mid = (*env)->GetMethodID(env, cls, "<init>", "(Ljava/lang/String;III)V");
    jstring jstr = (*env)->NewStringUTF(env, pathName);
    jobject obj = (*env)->NewObject(env, cls, mid, jstr, directoryCount, fileCount, totalSize);
    return obj;
}
JNIEXPORT jobject JNICALL Java_filesystem_SyncServlet_fileSystem(JNIEnv *env, jobject clsobj, jobject path)
{
    const char *pathName = (*env)->GetStringUTFChars(env, path, NULL);
    int directoryCount = directories(pathName, 0);
    int fileCount = files(pathName, 0);
    int totalSize = path_size(pathName, 0);
    jclass cls = (*env)->FindClass(env, "filesystem/FileInfo");
    jmethodID mid = (*env)->GetMethodID(env, cls, "<init>", "(Ljava/lang/String;III)V");
    jstring jstr = (*env)->NewStringUTF(env, pathName);
    jobject obj = (*env)->NewObject(env, cls, mid, jstr, directoryCount, fileCount, totalSize);
    return obj;
}

int path_size(char *path, int size)
{
    char file_path[1000];
    sprintf(file_path, "%s\\*.*", path);
    HANDLE path_handle = NULL;
    WIN32_FIND_DATA data_file;
    if ((path_handle = FindFirstFile(file_path, &data_file)) == INVALID_HANDLE_VALUE)
    {
        // printf("Cannot be accessed: [%s]\n", path);
    }

    while (FindNextFile(path_handle, &data_file))
    {
        if (strcmp(data_file.cFileName, ".") != 0 && strcmp(data_file.cFileName, "..") != 0 && strcmp(data_file.cFileName, ".metadata") != 0 && strcmp(data_file.cFileName, ".settings") != 0 && (strcmp(data_file.cFileName, ".git") != 0 && strcmp(data_file.cFileName, ".vscode") != 0) && (strcmp(data_file.cFileName, ".config") != 0 && strcmp(data_file.cFileName, "System Volume Information") != 0))
        {
            sprintf(file_path, "%s\\%s", path, data_file.cFileName);
            if (data_file.dwFileAttributes & FILE_ATTRIBUTE_DIRECTORY)
            {

                size = size + path_size(file_path, size);
            }
            else
            {

                size += data_file.nFileSizeLow;
            }
        }
    }

    FindClose(path_handle);
    if (size < 0)
        size = abs(size);
    return size;
}

int files(char *path, int file_count)
{
    char file_path[1000];
    sprintf(file_path, "%s\\*.*", path);
    HANDLE path_handle = NULL;
    WIN32_FIND_DATA data_file;
    if ((path_handle = FindFirstFile(file_path, &data_file)) == INVALID_HANDLE_VALUE)
    {
        // printf("Cannot be accessed: [%s]\n", path);
    }

    while (FindNextFile(path_handle, &data_file))
    {
       if (strcmp(data_file.cFileName, ".") != 0 && strcmp(data_file.cFileName, "..") != 0 && strcmp(data_file.cFileName, ".metadata") != 0 && strcmp(data_file.cFileName, ".settings") != 0 && (strcmp(data_file.cFileName, ".git") != 0 && strcmp(data_file.cFileName, ".vscode") != 0) && (strcmp(data_file.cFileName, ".config") != 0 && strcmp(data_file.cFileName, "System Volume Information") != 0))
        {
            sprintf(file_path, "%s\\%s", path, data_file.cFileName);
            if (data_file.dwFileAttributes & FILE_ATTRIBUTE_DIRECTORY)
            {

            }
            else
            {
            	file_count = files(file_path, file_count);
                file_count++;
            }
        }
    }

    FindClose(path_handle);
    return file_count;
}

int directories(char *path, int directory_count)
{
    char file_path[1000];
    sprintf(file_path, "%s\\*.*", path);
    HANDLE path_handle = NULL;
    WIN32_FIND_DATA data_file;
    if ((path_handle = FindFirstFile(file_path, &data_file)) == INVALID_HANDLE_VALUE)
    {
        // printf("Cannot be accessed: [%s]\n", path);
    }

    while (FindNextFile(path_handle, &data_file))
    {
        if (strcmp(data_file.cFileName, ".") != 0 && strcmp(data_file.cFileName, "..") != 0 && strcmp(data_file.cFileName, ".metadata") != 0 && strcmp(data_file.cFileName, ".settings") != 0 && (strcmp(data_file.cFileName, ".git") != 0 && strcmp(data_file.cFileName, ".vscode") != 0) && (strcmp(data_file.cFileName, ".config") != 0 && strcmp(data_file.cFileName, "System Volume Information") != 0))
        {
            sprintf(file_path, "%s\\%s", path, data_file.cFileName);
            if (data_file.dwFileAttributes & FILE_ATTRIBUTE_DIRECTORY)
            {

                directory_count = directories(file_path, directory_count);
                directory_count++;
            }
            else
            {
            }
        }
    }
    FindClose(path_handle);
    return directory_count;
}

/*

Old program...without recursion but a bug that doesnt traverse path completely


#include "C:/Program Files/Java/jdk1.8.0_333/include/jni.h"
#include "C:/Program Files/Java/jdk1.8.0_333/include/win32/jni_md.h"
#include <stdio.h>
#include "FileSystem.h"
#include <windows.h>
#include <dirent.h>
JNIEXPORT jobject JNICALL Java_FileSystem_fileSystem(JNIEnv *env, jobject clsobj, jstring path)
{
    int directoryCount = 0;
    int fileCount = 0;
    int totalSize = 0;
    const char *pathName = (*env)->GetStringUTFChars(env, path, NULL);
    char file_path[1000];
    sprintf(file_path, "%s\\*.*", pathName);
    HANDLE path_handle = NULL;
    WIN32_FIND_DATA data_file;
    if ((path_handle = FindFirstFile(file_path, &data_file)) == INVALID_HANDLE_VALUE)
    {
        printf("Cannot be accessed: [%s]\n", pathName);
        return NULL;
    }

    while (FindNextFile(path_handle, &data_file))
    {
        if (strcmp(data_file.cFileName, ".") != 0 && strcmp(data_file.cFileName, "..") != 0 && (strcmp(data_file.cFileName, ".git") != 0 && strcmp(data_file.cFileName, ".vscode") != 0) && (strcmp(data_file.cFileName, ".config") != 0 && strcmp(data_file.cFileName, "System Volume Information") != 0))
        {
            sprintf(file_path, "%s\\%s", pathName, data_file.cFileName);
            if (data_file.dwFileAttributes & FILE_ATTRIBUTE_DIRECTORY)
            {
                directoryCount++;
            }
            else
            {
                fileCount++;
                totalSize += data_file.nFileSizeLow;
            }
        }
    }
    FindClose(path_handle);
    jclass cls = (*env)->FindClass(env, "FileInfo");
    jmethodID mid = (*env)->GetMethodID(env, cls, "<init>", "(Ljava/lang/String;III)V");
    jstring jstr = (*env)->NewStringUTF(env, pathName);
    jobject obj = (*env)->NewObject(env, cls, mid, jstr, directoryCount, fileCount, totalSize);
    return obj;
}












*/

// printf("path: %s\n", pathName);
// printf("directoryCount: %d\n", directoryCount);
// printf("fileCount: %d\n", fileCount);
// printf("totalSize: %d\n", totalSize);
// FindClose(path_handle);
// (*env)->ReleaseStringUTFChars(env, path, pathName);
// WIN32_FIND_DATA wfd;
// HANDLE hFind = INVALID_HANDLE_VALUE;
// DWORD dwError = 0;
// int directoryCount = 0;
// int file_count = 0;
// const char *pathName = (*env)->GetStringUTFChars(env, path, NULL);
// hFind = FindFirstFile(pathName, &wfd);
// if (INVALID_HANDLE_VALUE == hFind)
// {
//     printf("Invalid file handle. Error is   %u. %s  %d  %d %d ", GetLastError(), pathName, wfd.cFileName,directory_count, file_count, wfd.nFileSizeLow);
// }
// else
// {
//     do
//     {
//         if (wfd.dwFileAttributes & FILE_ATTRIBUTE_DIRECTORY)
//         {
//             directory_count++;
//         }
//         else
//         {
//             file_count++;
//         }
//     } while (FindNextFile(hFind, &wfd) != 0);
//     dwError = GetLastError();
//     FindClose(hFind);
//     if (dwError != ERROR_NO_MORE_FILES)
//     {
//         printf("FindNextFile error. Error is %u. %s  %d  %d   %d", GetLastError(), pathName, wfd.cFileName,directory_count, file_count, wfd.nFileSizeLow);
//     }
// }
// jclass cls = (*env)->FindClass(env, "FileSystem");
// jmethodID mid=(*env)->GetMethodID(env,cls,"<init>","(java/lang/String;III)V");
// jstring jstr = (*env)->NewStringUTF(env, pathName);
// jint jdir = directory_count;
// jint jfile = file_count;
// jint jsize = wfd.nFileSizeLow;
// jobject obj1 = (*env)->NewObject(env,cls,mid,jstr,jdir,jfile,jsize);
// return obj1;

//     jclass cls = (*env)->FindClass(env, "FileInfo");
//     jobject obj1 = (*env)->AllocObject(env, cls);
//     jfieldID cfileName=(*env)->GetFieldID(env,cls,"cFileName","Ljava/lang/String;");
//     jfieldID dwFileAttributes=(*env)->GetFieldID(env,cls,"dwFileAttributes","I");
//     jfieldID ftCreationTime=(*env)->GetFieldID(env,cls,"ftCreationTime","I");
//     jfieldID ftLastAccessTime=(*env)->GetFieldID(env,cls,"ftLastAccessTime","I");
//     jfieldID ftLastWriteTime=(*env)->GetFieldID(env,cls,"ftLastWriteTime","I");
//     jfieldID nFileSizeLow=(*env)->GetFieldID(env,cls,"nFileSizeLow","I");
//     jfieldID directoryCount=(*env)->GetFieldID(env,cls,"directoryCount","I");
//     jfieldID fileCount=(*env)->GetFieldID(env,cls,"fileCount","I");
//     jstring cFileName=(*env)->NewStringUTF(env,wfd.cFileName);
//     jstring dwFileAttributes1=(*env)->NewStringUTF(env,wfd.dwFileAttributes);
//     jstring ftCreationTime1=(*env)->NewStringUTF(env,wfd.ftCreationTime.dwLowDateTime);
//     jstring ftLastAccessTime1=(*env)->NewStringUTF(env,wfd.ftLastAccessTime.dwLowDateTime);
//     jstring ftLastWriteTime1=(*env)->NewStringUTF(env,wfd.ftLastWriteTime.dwLowDateTime);
//     jstring nFileSizeLow1=(*env)->NewStringUTF(env,wfd.nFileSizeLow);
//     jstring directoryCount1=(*env)->NewStringUTF(env,directory_count);
//     jstring fileCount1=(*env)->NewStringUTF(env,file_count);
//     (*env)->SetObjectField(env,obj1,cfileName,cFileName);
//     (*env)->SetObjectField(env,obj1,dwFileAttributes,dwFileAttributes1);
//     (*env)->SetObjectField(env,obj1,ftCreationTime,ftCreationTime1);
//     (*env)->SetObjectField(env,obj1,ftLastAccessTime,ftLastAccessTime1);
//     (*env)->SetObjectField(env,obj1,ftLastWriteTime,ftLastWriteTime1);
//     (*env)->SetObjectField(env,obj1,nFileSizeLow,nFileSizeLow1);
//     (*env)->SetObjectField(env,obj1,directoryCount,directoryCount1);
//     (*env)->SetObjectField(env,obj1,fileCount,fileCount1);
//     return obj1;
// }
// JNIEXPORT  jobject  JNICALL Java_FileSystem_getFileList(JNIEnv *env, jobject obj, jstring path)
// {
//     WIN32_FIND_DATA ffd;
//     HANDLE hFind = INVALID_HANDLE_VALUE;
//     DWORD dwError=0;
//     const char *pathName = (*env)->GetStringUTFChars(env, path, 0);
//     char *pathName2 = (char *)malloc(strlen(pathName)+3);
//     strcpy(pathName2, pathName);
//     strcat(pathName2, "\\*");
//     hFind = FindFirstFile(pathName2, &ffd);
//     if (INVALID_HANDLE_VALUE == hFind)
//     {
//         printf("FindFirstFile failed (%d)   %s  %s  %s      %s  %s", GetLastError(), pathName, pathName2, pathName, pathName2, pathName);
//         return NULL;
//     }
//     jclass cls = (*env)->FindClass(env, "java/util/ArrayList");
//     jmethodID mid = (*env)->GetMethodID(env, cls, "<init>", "()V");
//     jobject obj2 = (*env)->NewObject(env, cls, mid);
//     jmethodID add = (*env)->GetMethodID(env, cls, "add", "(Ljava/lang/Object;)Z");
//     do
//     {
//         if (ffd.dwFileAttributes & FILE_ATTRIBUTE_DIRECTORY)
//         {
//             if (strcmp(ffd.cFileName, ".") != 0 && strcmp(ffd.cFileName, "..") != 0)
//             {
//                 char *pathName3 = (char *)malloc(strlen(pathName)+strlen(ffd.cFileName)+2);
//                 strcpy(pathName3, pathName);
//                 strcat(pathName3, "\\");
//                 strcat(pathName3, ffd.cFileName);
//                 jstring str = (*env)->NewStringUTF(env, pathName3);
//                 (*env)->CallBooleanMethod(env, obj2, add, str);
//             }
//         }
//     }
//     while (FindNextFile(hFind, &ffd) != 0);
//     dwError = GetLastError();
//     if (dwError != ERROR_NO_MORE_FILES)
//     {
//         printf("FindNextFile failed (%d)   %s  %s  %s      %s  %s", GetLastError(), pathName, pathName2, pathName, pathName2, pathName);
//     }
//     FindClose(hFind);
//     return obj2;

// }
//  jobject JNICALL
// Java_FileSystem_fileSystemUtility(JNIEnv *env, jclass cls, jstring pathObj)
// {
//     const char *path = (*env)->GetStringUTFChars(env, pathObj, 0);
//     WIN32_FIND_DATA wfd;
//     HANDLE hFind = FindFirstFile(path, &wfd);
//     int directory_count=0;
//     int file_count=0;
//     if (hFind == INVALID_HANDLE_VALUE)
//     {
//         printf("FindFirstFile failed (%d)\n", GetLastError());
//         return NULL;
//     }
//     do
//     {
//         if (wfd.dwFileAttributes & FILE_ATTRIBUTE_DIRECTORY)
//         {

//             printf("  %s   <DIR>    %d  %d  %d  %d  %d  %d      ", wfd.cFileName, wfd.dwFileAttributes, wfd.ftCreationTime.dwLowDateTime, wfd.ftLastAccessTime.dwLowDateTime, wfd.ftLastWriteTime.dwLowDateTime,directory_count, file_count, wfd.nFileSizeLow);
//         }
//         else
//         {
//             printf("  %s   <FILE>    %d  %d  %d  %d  %d  %d  ", wfd.cFileName, wfd.dwFileAttributes, wfd.ftCreationTime.dwLowDateTime, wfd.ftLastAccessTime.dwLowDateTime, wfd.ftLastWriteTime.dwLowDateTime,directory_count, file_count, wfd.nFileSizeLow);
//         }
//     } while (FindNextFile(hFind, &wfd));
//     FindClose(hFind);
//     (*env)->ReleaseStringUTFChars(env, pathObj, path);
//     return NULL;
// }
// // {
// //     jmethodID constructor;
// //     jvalue args[4];
// //     jobject object;
// //     cls = (*env)->FindClass(env, "FileInfo");

// //     constructor = (*env)->GetMethodID(env, cls, "<init>", "(ILjava/lang/String;)V");

//     // jmethodID mid = (*env)->GetMethodID(env, cls, "callback", "()V");

//     char *path = (*env)->GetStringUTFChars(env, pathObj, NULL);
//     int directory_count = 0;
//     int file_count = 0;
//     int file_size = 0;

//     printf("\n--------------------------------------------------------------------\n");
//     printf("\nFile system information\n");
//     printf("\n--------------------------------------------------------------------\n");
//     // function call for file utility
//     display(path, directory_count, file_count, file_size);
//     args[0].l = (*env)->NewStringUTF(env, path);
//     args[1].i = directory_count;
//     args[2].i = file_count;
//     args[3].i = file_size;
//     object = (*env)->NewObjectA(env, cls, constructor, args);
//     //(*env)->CallVoidMethodA(env, object, mid, args);

//     // logging and checking output
//     printf("Maps to: %s\n", path);
//     printf("Directories:  %d \n", directory_count);
//     printf("Files:  %d \n", file_count);
//     if (file_size <= 1024)
//     {
//         printf("Total size: %d bytes\n", file_size);
//     }
//     else if (file_size > 1024 && file_size <= 1048576)
//     {
//         printf("Total size: %d KB\n", file_size / 1024);
//     }
//     else if (file_size > 1048576 && file_size <= 1073741824)
//     {
//         printf("Total size: %d MB\n", file_size / 1048576);
//     }
//     else if (file_size > 1073741824)
//     {
//         printf("Total size: %d GB\n", file_size / 1073741824);
//     }

//     return object;
//     //(*env)->CallVoidMethod(env, obj, mid);
// }
// void display(char *path, int directory_count, int file_count, int file_size)
// {
//     DIR *directory;
//     struct dirent *directory_struc;
//     directory = opendir(".");
//     WIN32_FIND_DATA data_file;
//     if ((directory = opendir(path)) == NULL)
//     {
//         fprintf(stderr, "can't open %s\n", path);
//     }
//     while ((directory_struc = readdir(directory)) != NULL)
//     {

//         if (data_file.dwFileAttributes & FILE_ATTRIBUTE_DIRECTORY)
//         {
//             // printf("\nDirectory Path: %d - %s\n", directory_count, file_path);
//             directory_count++;
//             // display(file_path, directory_count, file_count, file_size);
//         }
//         else
//         {
//             file_count++;
//             file_size += data_file.nFileSizeLow;
//         }
//     }
//     closedir(directory);
//     // char file_path[1000];
//     // sprintf(file_path, "%s\\*.*", path);
//     // HANDLE path_handle = NULL;
//     // WIN32_FIND_DATA data_file;
//     // if ((path_handle = FindFirstFile(file_path, &data_file)) == INVALID_HANDLE_VALUE)
//     // {
//     //     printf("Cannot be accessed: [%s]\n", path);
//     // }

//     // while (FindNextFile(path_handle, &data_file))
//     // {
//     //     if (strcmp(data_file.cFileName, ".") != 0 && strcmp(data_file.cFileName, "..") != 0 && (strcmp(data_file.cFileName, ".git") != 0 && strcmp(data_file.cFileName, ".vscode") != 0) && (strcmp(data_file.cFileName, ".config") != 0 && strcmp(data_file.cFileName, "System Volume Information") != 0))
//     //     {
//     //         sprintf(file_path, "%s\\%s", path, data_file.cFileName);
//     //         if (data_file.dwFileAttributes & FILE_ATTRIBUTE_DIRECTORY)
//     //         {
//     //             // printf("\nDirectory Path: %d - %s\n", directory_count, file_path);
//     //             directory_count++;
//     //             display(file_path, directory_count, file_count, file_size);
//     //         }
//     //         else
//     //         {
//     //             file_count++;
//     //             file_size += data_file.nFileSizeLow;
//     //         }
//     //     }
//     // }
//     // FindClose(path_handle);

//     // printf("\n--------------------------------------------------------------------\n");
// }
