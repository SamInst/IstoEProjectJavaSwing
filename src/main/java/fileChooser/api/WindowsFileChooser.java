package fileChooser.api;

import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.WString;
import fileChooser.win32.Comdlg32;

import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;


public class WindowsFileChooser
{
	protected File selectedFile;
	protected File currentDirectory;
	protected ArrayList<String[]> filters;

	protected String defaultFilename = "";
	protected String dialogTitle = "";

	public WindowsFileChooser() {
		filters = new ArrayList<String[]>();
	}

	public WindowsFileChooser(File currentDirectory) {
		filters = new ArrayList<String[]>();
		if (currentDirectory != null) {
			this.currentDirectory = currentDirectory.isDirectory() ?
				currentDirectory : currentDirectory.getParentFile();
		}
	}

	public WindowsFileChooser(String currentDirectoryPath) {
		this(currentDirectoryPath != null ?
			new File(currentDirectoryPath) : null);
	}

	void setFilters(ArrayList<String[]> filters) {
		this.filters = filters;
	}

	public void addFilter(String name, String... filter) {
		if (filter.length < 1) {
			throw new IllegalArgumentException();
		}
		ArrayList<String> parts = new ArrayList<String>();
		parts.add(name);
		Collections.addAll(parts, filter);
		filters.add(parts.toArray(new String[parts.size()]));
	}

	public void setTitle(String tname) {
		this.dialogTitle = tname;
	}

	public boolean showOpenDialog(Window parent) {
		return showDialog(parent, true);
	}

	public boolean showSaveDialog(Window parent) {
		return showDialog(parent, false);
	}

	boolean showDialog(Window parent, boolean open) {
		final Comdlg32.OpenFileName params = new Comdlg32.OpenFileName();
		params.Flags =
			// use explorer-style interface
			Comdlg32.OFN_EXPLORER
			// the dialog changes the current directory when browsing,
			// this flag causes the original value to be restored after the
			// dialog returns
			| Comdlg32.OFN_NOCHANGEDIR
			// disable "open as read-only" feature
			| Comdlg32.OFN_HIDEREADONLY
			// enable resizing of the dialog
			| Comdlg32.OFN_ENABLESIZING;

		params.hwndOwner = parent == null ? null : Native.getWindowPointer(parent);

		final int bufferLength = 260;

		final int bufferSize = 4 * bufferLength + 1;
		params.lpstrFile = new Memory(bufferSize);
		if (!defaultFilename.isEmpty()) {
			params.lpstrFile.setWideString(0, defaultFilename);
		} else {
		    params.lpstrFile.clear(bufferSize);
		}
		if (!dialogTitle.isEmpty()) {
			params.lpstrTitle = new WString(dialogTitle);
		}

		params.nMaxFile = bufferLength;

		if (currentDirectory != null) {
			params.lpstrInitialDir = new WString(currentDirectory.getAbsolutePath());
		}

		if (filters.size() > 0) {
			params.lpstrFilter = new WString(buildFilterString());
			params.nFilterIndex = 1; // TODO don't hardcode here
		}

		final boolean approved = open ?
			Comdlg32.GetOpenFileNameW(params) :
			Comdlg32.GetSaveFileNameW(params);

		if (approved) {
			final String filePath = params.lpstrFile.getWideString(0);
			selectedFile = new File(filePath);
			final File dir = selectedFile.getParentFile();
			currentDirectory = dir;
		}
		else {
			final int errCode = Comdlg32.CommDlgExtendedError();
			if (errCode != 0) {
				throw new RuntimeException(
					"GetOpenFileName failed with error " + errCode);
			}
		}
		return approved;
	}

	private String buildFilterString() {
		final StringBuilder filterStr = new StringBuilder();
		for (final String[] spec : filters) {
			final String label = spec[0];

			filterStr.append(label);
			filterStr.append('\0');

			for (int i = 1; i < spec.length; ++i) {
				filterStr.append("*.");
				filterStr.append(spec[i]);
				filterStr.append(';');
			}

			filterStr.deleteCharAt(filterStr.length() - 1);
			filterStr.append('\0');
		}

		filterStr.append('\0');
		return filterStr.toString();
	}


	public File getSelectedFile() {
		return selectedFile;
	}

	public File getCurrentDirectory() {
		return currentDirectory;
	}

    public void setDefaultFilename(String defaultFilename) {
        this.defaultFilename = defaultFilename;
    }
}
