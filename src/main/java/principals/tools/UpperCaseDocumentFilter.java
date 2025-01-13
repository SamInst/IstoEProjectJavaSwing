package principals.tools;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

public class UpperCaseDocumentFilter extends DocumentFilter {
    @Override
    public void insertString(FilterBypass fb, int offset, String text, AttributeSet attr) throws BadLocationException {
        if (text != null) {
            text = text.toUpperCase(); // Converte o texto para maiúsculas antes de inserir
        }
        super.insertString(fb, offset, text, attr);
    }

    @Override
    public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
        if (text != null) {
            text = text.toUpperCase(); // Converte o texto para maiúsculas ao substituir
        }
        super.replace(fb, offset, length, text, attrs);
    }
}
