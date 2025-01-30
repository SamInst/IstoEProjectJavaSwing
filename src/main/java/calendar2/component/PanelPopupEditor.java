package calendar2.component;

import calendar2.util.Utils;
import com.formdev.flatlaf.FlatClientProperties;
import lombok.Getter;

import javax.swing.*;
import java.awt.*;
import java.text.ParseException;

public abstract class PanelPopupEditor extends JPanel {

    protected JFormattedTextField editor;
    protected JPopupMenu popupMenu;

    @Getter
    protected boolean editorValidation = true;
    protected boolean isValid;
    @Getter
    protected boolean validationOnNull;
    protected String defaultPlaceholder;

    protected LookAndFeel oldThemes = UIManager.getLookAndFeel();

    public PanelPopupEditor() {
    }

    public void showPopup() {
        if (popupMenu == null) {
            popupMenu = new JPopupMenu();
            popupMenu.putClientProperty(FlatClientProperties.STYLE, "" +
                    "borderInsets:1,1,1,1");
            popupMenu.add(this);
        }
        if (UIManager.getLookAndFeel() != oldThemes) {
            SwingUtilities.updateComponentTreeUI(popupMenu);
            oldThemes = UIManager.getLookAndFeel();
        }
        Point point = Utils.adjustPopupLocation(popupMenu, editor);
        popupMenu.show(editor, point.x, point.y);
    }

    public void closePopup() {
        if (popupMenu != null) {
            popupMenu.setVisible(false);
            repaint();
        }
    }

    public void setEditorValidation(boolean editorValidation) {
        if (this.editorValidation != editorValidation) {
            this.editorValidation = editorValidation;
            if (editor != null) {
                if (editorValidation) {
                    validChanged(editor, isValid);
                } else {
                    validChanged(editor, true);
                }
            }
        }
    }

    public void setValidationOnNull(boolean validationOnNull) {
        if (this.validationOnNull != validationOnNull) {
            this.validationOnNull = validationOnNull;
            commitEdit();
        }
    }

    protected void checkValidation(boolean status) {
        boolean valid = status || isEditorValidationOnNull();
        if (isValid != valid) {
            isValid = valid;
            if (editor != null) {
                if (editorValidation) {
                    validChanged(editor, valid);
                }
            }
        }
    }

    protected void validChanged(JFormattedTextField editor, boolean isValid) {
        String style = isValid ? null : FlatClientProperties.OUTLINE_ERROR;
        editor.putClientProperty(FlatClientProperties.OUTLINE, style);
    }

    protected boolean isEditorValidationOnNull() {
        if (validationOnNull) {
            return false;
        }
        return editor != null && editor.getText().equals(getDefaultPlaceholder());
    }

    protected void commitEdit() {
        if (editor != null && editorValidation) {
            try {
                editor.commitEdit();
            } catch (ParseException e) {
            }
        }
    }

    protected abstract String getDefaultPlaceholder();
}
