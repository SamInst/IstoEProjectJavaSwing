
package fileChooser.api;

import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import fileChooser.win32.Ole32;
import fileChooser.win32.Shell32;
import lombok.Setter;

import java.awt.*;
import java.io.File;

@Setter
public class WindowsFolderBrowser
{
	private String title;

	public WindowsFolderBrowser() {
		title = null;
	}

	public WindowsFolderBrowser(String title) {
		this.title = title;
	}

    public File showDialog(Window parent) {
		Ole32.OleInitialize(null);
		final Shell32.BrowseInfo params = new Shell32.BrowseInfo();
		params.hwndOwner = Native.getWindowPointer(parent);
		params.ulFlags = Shell32.BIF_RETURNONLYFSDIRS | Shell32.BIF_USENEWUI;
		if (title != null) {
			params.lpszTitle = title;
		}
		final Pointer pidl = Shell32.SHBrowseForFolder(params);
		if (pidl != null) {
			final Pointer path = new Memory(1024 * 4);
			Shell32.SHGetPathFromIDListW(pidl, path);
			final String filePath = path.getWideString(0);
			final File file = new File(filePath);
			Ole32.CoTaskMemFree(pidl);
			return file;
		}
		return null;
	}
}
