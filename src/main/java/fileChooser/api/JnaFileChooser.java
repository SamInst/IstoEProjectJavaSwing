
package fileChooser.api;


import com.sun.jna.Platform;
import lombok.Getter;
import lombok.Setter;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;


public class JnaFileChooser
{
	private static enum Action { Open, Save }

	public static enum Mode {
		Files(JFileChooser.FILES_ONLY),
		Directories(JFileChooser.DIRECTORIES_ONLY),
		FilesAndDirectories(JFileChooser.FILES_AND_DIRECTORIES);
		private int jFileChooserValue;
		private Mode(int jfcv) {
			this.jFileChooserValue = jfcv;
		}
		public int getJFileChooserValue() {
			return jFileChooserValue;
		}
	}

	@Getter
    protected File[] selectedFiles;
	@Getter
    protected File currentDirectory;
	protected ArrayList<String[]> filters;
	@Getter
    @Setter
    protected boolean multiSelectionEnabled;
	@Getter
    @Setter
    protected Mode mode;

	protected String defaultFile;
    protected String dialogTitle;
    @Setter
    protected String openButtonText;
    @Setter
    protected String saveButtonText;

	public JnaFileChooser() {
		filters = new ArrayList<String[]>();
		multiSelectionEnabled = false;
		mode = Mode.Files;
		selectedFiles = new File[] { null };

		defaultFile = "";
        dialogTitle = "";
        openButtonText = "";
        saveButtonText = "";
	}

	public JnaFileChooser(File currentDirectory) {
		this();
        if (currentDirectory != null) {
			this.currentDirectory = currentDirectory.isDirectory() ?
				currentDirectory : currentDirectory.getParentFile();
		}
	}

	public JnaFileChooser(String currentDirectoryPath) {
		this(currentDirectoryPath != null ?
			new File(currentDirectoryPath) : null);
	}

	public boolean showOpenDialog(Window parent) {
		return showDialog(parent, Action.Open);
	}

	public boolean showSaveDialog(Window parent) {
		return showDialog(parent, Action.Save);
	}

	private boolean showDialog(Window parent, Action action) {
		if (Platform.isWindows() && mode != Mode.FilesAndDirectories) {
			if (multiSelectionEnabled && mode == Mode.Files) {}
			else if (!multiSelectionEnabled) {
				if (mode == Mode.Files) {
					return showWindowsFileChooser(parent, action);
				}
				else if (mode == Mode.Directories) {
					return showWindowsFolderBrowser(parent);
				}
			}
		}

		return showSwingFileChooser(parent, action);
	}

	private boolean showSwingFileChooser(Window parent, Action action) {
		final JFileChooser fc = new JFileChooser(currentDirectory);
		fc.setMultiSelectionEnabled(multiSelectionEnabled);
		fc.setFileSelectionMode(mode.getJFileChooserValue());

		if (!defaultFile.isEmpty() & action == Action.Save) {
			File fsel = new File(defaultFile);
			fc.setSelectedFile(fsel);
		}
		if (!dialogTitle.isEmpty()) {
			fc.setDialogTitle(dialogTitle);
		}
		if (action == Action.Open & !openButtonText.isEmpty()) {
			fc.setApproveButtonText(openButtonText);
		} else if (action == Action.Save & !saveButtonText.isEmpty()) {
			fc.setApproveButtonText(saveButtonText);
		}

		if (!filters.isEmpty()) {
			boolean useAcceptAllFilter = false;
			for (final String[] spec : filters) {
				if (spec[1].equals("*")) {
					useAcceptAllFilter = true;
					continue;
				}
				fc.addChoosableFileFilter(new FileNameExtensionFilter(
					spec[0], Arrays.copyOfRange(spec, 1, spec.length)));
			}
			fc.setAcceptAllFileFilterUsed(useAcceptAllFilter);
		}

		int result = -1;
		if (action == Action.Open) {
			result = fc.showOpenDialog(parent);
		}
		else {
			if (saveButtonText.isEmpty()) {
				result = fc.showSaveDialog(parent);
            }
			else {
				result = fc.showDialog(parent, null);
            }
		}
		if (result == JFileChooser.APPROVE_OPTION) {
			selectedFiles = multiSelectionEnabled ?
				fc.getSelectedFiles() : new File[] { fc.getSelectedFile() };
			currentDirectory = fc.getCurrentDirectory();
			return true;
		}

		return false;
	}

	private boolean showWindowsFileChooser(Window parent, Action action) {
		final WindowsFileChooser fc = new WindowsFileChooser(currentDirectory);
		fc.setFilters(filters);

		if (!defaultFile.isEmpty())
			fc.setDefaultFilename(defaultFile);

		if (!dialogTitle.isEmpty()) {
			fc.setTitle(dialogTitle);
		}

		final boolean result = fc.showDialog(parent, action == Action.Open);
		if (result) {
			selectedFiles = new File[] { fc.getSelectedFile() };
			currentDirectory = fc.getCurrentDirectory();
		}
		return result;
	}

	private boolean showWindowsFolderBrowser(Window parent) {
		final WindowsFolderBrowser fb = new WindowsFolderBrowser();
		if (!dialogTitle.isEmpty()) {
			fb.setTitle(dialogTitle);
		}
		final File file = fb.showDialog(parent);
		if (file != null) {
			selectedFiles = new File[] { file };
			currentDirectory = file.getParentFile() != null ?
				file.getParentFile() : file;
			return true;
		}

		return false;
	}


	public void addFilter(String name, String... filter) {
		if (filter.length < 1) {
			throw new IllegalArgumentException();
		}
		ArrayList<String> parts = new ArrayList<String>();
		parts.add(name);
		Collections.addAll(parts, filter);
		filters.add(parts.toArray(new String[0]));
	}


    public void setCurrentDirectory(String currentDirectoryPath) {
		this.currentDirectory = (currentDirectoryPath != null ? new File(currentDirectoryPath) : null);
	}


    public void setDefaultFileName(String dfile) {
		this.defaultFile = dfile;
	}

	public void setTitle(String title) {
		this.dialogTitle = title;
	}


    public File getSelectedFile() {
		return selectedFiles[0];
	}

}
