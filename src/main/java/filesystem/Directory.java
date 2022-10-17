package filesystem;
import java.io.Serializable;
public class Directory  implements Serializable{
    private static final long serialVersionUID = 1L;
	public String parent_path;
	String directory_name;
	public int subdirectories;
	public int files;
	public String size_bytes;
	public int size;
	public String timestamp;
	public int version;

	public Directory() {
		
	}
	public Directory(String parent_path,String directory_name, int subdirectories, int files, String size_bytes, int size, String timestamp,
			int version) {
		this.parent_path = parent_path;
		this.directory_name=directory_name;
		this.subdirectories = subdirectories;
		this.files = files;
		this.size_bytes = size_bytes;
		this.size = size;
		this.timestamp = timestamp;
		this.version = version;
	}

	public String getParentPath() {
		return parent_path;
	}

	public void setPath(String parent_path) {
		this.parent_path = parent_path;
	}
	public String getDirectoryName() {
		return directory_name;
	}

	public void setDirectoryName(String directory_name) {
		this.directory_name = directory_name;
	}

	public int getSubdirectories() {
		return subdirectories;
	}

	public void setSubdirectories(int subdirectories) {
		this.subdirectories = subdirectories;
	}

	public int getFiles() {
		return files;
	}

	public void setFiles(int files) {
		this.files = files;
	}

	public String getSize_bytes() {
		return size_bytes;
	}

	public void setSize_bytes(String size_bytes) {
		this.size_bytes = size_bytes;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public String getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}
}
