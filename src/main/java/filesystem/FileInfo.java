package filesystem;

public class FileInfo {
    public String cFileName;
    public int directory_count;
    public int file_count;
    public int nFileSizeLow;

    public FileInfo(String cFileName, int directory_count, int file_count, int nFileSizeLow) {
        this.cFileName = cFileName;
        this.directory_count = directory_count;
        this.file_count = file_count;
        this.nFileSizeLow = nFileSizeLow;
    }

    public String getcFileName() {
        return cFileName;
    }

    public void setcFileName(String cFileName) {
        this.cFileName = cFileName;
    }

    public int getDirectory_count() {
        return directory_count;
    }

    public void setDirectory_count(int directory_count) {
        this.directory_count = directory_count;
    }

    public int getFile_count() {
        return file_count;
    }

    public void setFile_count(int file_count) {
        this.file_count = file_count;
    }

    public int getnFileSizeLow() {
        return nFileSizeLow;
    }

    public void setnFileSizeLow(int nFileSizeLow) {
        this.nFileSizeLow = nFileSizeLow;
    }

}
