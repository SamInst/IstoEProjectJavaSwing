package fileChooser.win32;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;

import java.util.Arrays;
import java.util.List;


public class Shell32 {
	static { Native.register("shell32"); }

	public static native Pointer SHBrowseForFolder(BrowseInfo params);
	public static native boolean SHGetPathFromIDListW(Pointer pidl, Pointer path);

	public static class BrowseInfo extends Structure {
		public Pointer hwndOwner;
		public Pointer pidlRoot;
		public String pszDisplayName;
		public String lpszTitle;
		public int ulFlags;
		public Pointer lpfn;
		public Pointer lParam;
		public int iImage;

		protected List<String> getFieldOrder() {
		return Arrays.asList("hwndOwner","pidlRoot","pszDisplayName","lpszTitle"
                ,"ulFlags","lpfn","lParam","iImage");
		}
	}

	public static final int BIF_RETURNONLYFSDIRS = 0x00000001;
	public static final int BIF_DONTGOBELOWDOMAIN = 0x00000002;
	public static final int BIF_NEWDIALOGSTYLE = 0x00000040;
	public static final int BIF_EDITBOX = 0x00000010;
	public static final int BIF_USENEWUI = BIF_EDITBOX | BIF_NEWDIALOGSTYLE;
	public static final int BIF_NONEWFOLDERBUTTON = 0x00000200;
	public static final int BIF_BROWSEINCLUDEFILES = 0x00004000;
	public static final int BIF_SHAREABLE = 0x00008000;
	public static final int BIF_BROWSEFILEJUNCTIONS = 0x00010000;
}
