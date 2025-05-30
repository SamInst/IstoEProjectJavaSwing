package tools;

import textField.TextFieldComSobra;
import javax.swing.*;
import javax.swing.text.AbstractDocument;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import static tools.CorPersonalizada.DARK_GRAY;
import static tools.CorPersonalizada.DARK_GREEN;

public class Mascaras {
    public static void adicionarMascaraCEP(JTextFieldComTextoFixoArredondado campo) {
        campo.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                String texto = campo.getText().replaceAll("[^0-9]", "");
                if (texto.length() > 8) {
                    texto = texto.substring(0, 8);
                }

                StringBuilder formatado = new StringBuilder("* CEP: ");
                if (texto.length() > 5) {
                    formatado.append(texto, 0, 5).append("-").append(texto.substring(5));
                } else {
                    formatado.append(texto);
                }

                campo.setText(formatado.toString());
            }
        });
    }

    public static void mascaraUpperCase(JTextField textField) {
        AbstractDocument doc = (AbstractDocument) textField.getDocument();
        doc.setDocumentFilter(new UpperCaseDocumentFilter());
    }

    public static void adicionarMascaraCNPJ(JTextFieldComTextoFixoArredondado campo) {
        campo.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                String texto = campo.getText().replaceAll("[^0-9]", "");
                if (texto.length() > 14) {
                    texto = texto.substring(0, 14);
                }

                StringBuilder formatado = new StringBuilder("* CNPJ: ");
                if (texto.length() > 2) {
                    formatado.append(texto, 0, 2).append(".");
                } else {
                    formatado.append(texto);
                }
                if (texto.length() > 5) {
                    formatado.append(texto, 2, 5).append(".");
                } else if (texto.length() > 2) {
                    formatado.append(texto.substring(2));
                }
                if (texto.length() > 8) {
                    formatado.append(texto, 5, 8).append("/");
                } else if (texto.length() > 5) {
                    formatado.append(texto.substring(5));
                }
                if (texto.length() > 12) {
                    formatado.append(texto, 8, 12).append("-");
                } else if (texto.length() > 8) {
                    formatado.append(texto.substring(8));
                }
                if (texto.length() > 12) {
                    formatado.append(texto.substring(12));
                }

                campo.setText(formatado.toString());
            }
        });
    }

    public static void adicionarMascaraTelefone(JTextFieldComTextoFixoArredondado campo) {
        campo.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                String texto = campo.getText().replaceAll("[^0-9]", "");
                if (texto.length() > 11) {
                    texto = texto.substring(0, 11);
                }

                StringBuilder formatado = new StringBuilder("* Fone: ");
                if (texto.length() >= 2) {
                    formatado.append("(").append(texto, 0, 2).append(") ");
                } else {
                    formatado.append(texto);
                }
                if (texto.length() > 7) {
                    formatado.append(texto, 2, 7).append("-").append(texto.substring(7));
                } else if (texto.length() > 2) {
                    formatado.append(texto.substring(2));
                }

                campo.setText(formatado.toString());
            }
        });
    }

    public static void adicionarMascaraRG(JTextFieldComTextoFixoArredondado campo) {
        campo.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                String texto = campo.getText().replaceAll("[^0-9]", "");


                if (texto.length() > 13) {
                    texto = texto.substring(0, 13);
                }

                StringBuilder formatado = new StringBuilder("RG: ");
                if (texto.length() > 2) {
                    formatado.append(texto, 0, 2).append(".");
                    if (texto.length() > 5) {
                        formatado.append(texto, 2, 5).append(".");
                        if (texto.length() > 8) {
                            formatado.append(texto, 5, 8).append(".");
                            if (texto.length() > 12) {
                                formatado.append(texto, 8, 12).append("-");
                            } else {
                                formatado.append(texto.substring(8));
                            }
                        } else {
                            formatado.append(texto.substring(5));
                        }
                    } else {
                        formatado.append(texto.substring(2));
                    }
                } else {
                    formatado.append(texto);
                }

                campo.setText(formatado.toString());
                campo.setCaretPosition(campo.getText().length());
            }
        });
    }

    public static void adicionarMascaraCPF(JTextFieldComTextoFixoArredondado campo, String cpf) {
        campo.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                String texto = cpf != null ? cpf : campo.getText().replaceAll("[^0-9]", "");
                if (texto.length() > 11) {
                    texto = texto.substring(0, 11);
                }

                StringBuilder formatado = new StringBuilder("* CPF: ");
                if (texto.length() > 3) {
                    formatado.append(texto, 0, 3).append(".");
                } else {
                    formatado.append(texto);
                }
                if (texto.length() > 6) {
                    formatado.append(texto, 3, 6).append(".");
                } else if (texto.length() > 3) {
                    formatado.append(texto.substring(3));
                }
                if (texto.length() > 9) {
                    formatado.append(texto, 6, 9).append("-");
                } else if (texto.length() > 6) {
                    formatado.append(texto.substring(6));
                }
                if (texto.length() > 9) {
                    formatado.append(texto.substring(9));
                }

                campo.setText(formatado.toString());
            }
        });
    }

    public static void adicionarMascaraCPF(TextFieldComSobra campo) {
        campo.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                String texto = campo.getText().replaceAll("[^0-9]", "");
                if (texto.length() > 11) {
                    texto = texto.substring(0, 11);
                }

                StringBuilder formatado = new StringBuilder();
                if (texto.length() > 3) {
                    formatado.append(texto, 0, 3).append(".");
                } else {
                    formatado.append(texto);
                }
                if (texto.length() > 6) {
                    formatado.append(texto, 3, 6).append(".");
                } else if (texto.length() > 3) {
                    formatado.append(texto.substring(3));
                }
                if (texto.length() > 9) {
                    formatado.append(texto, 6, 9).append("-");
                } else if (texto.length() > 6) {
                    formatado.append(texto.substring(6));
                }
                if (texto.length() > 9) {
                    formatado.append(texto.substring(9));
                }

                campo.setText(formatado.toString());
            }
        });
    }

    public static void adicionarMascaraData(JTextFieldComTextoFixoArredondado campo) {
        campo.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                String texto = campo.getText().replace("Nascimento: ", "").replaceAll("[^0-9]", "");

                if (texto.isEmpty()) {
                    campo.setText("Nascimento: ");
                    return;
                }

                if (texto.length() > 8) {
                    texto = texto.substring(0, 8);
                }

                StringBuilder formatado = new StringBuilder("Nascimento: ");
                if (texto.length() >= 2) {
                    formatado.append(texto, 0, 2).append("/");
                } else {
                    formatado.append(texto);
                }
                if (texto.length() > 4) {
                    formatado.append(texto, 2, 4).append("/").append(texto.substring(4));
                } else if (texto.length() > 2) {
                    formatado.append(texto.substring(2));
                }

                campo.setText(formatado.toString());
            }
        });
    }
    public static void mascaraValor(JFormattedTextField campoValor){
        campoValor.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                String texto = campoValor.getText().replaceAll("[^-0-9]", "");
                boolean isNegative = texto.startsWith("-");

                if (!texto.isEmpty() && !(texto.equals("-"))) {
                    double valor = Double.parseDouble(texto.replace("-", "")) / 100;

                    if (valor == 0) {
                        campoValor.setText("");
                        campoValor.setForeground(DARK_GRAY.brighter());
                        campoValor.setCaretPosition(campoValor.getText().length());
                    } else {
                        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.getDefault());
                        symbols.setDecimalSeparator(',');
                        symbols.setGroupingSeparator('.');
                        DecimalFormat formato = new DecimalFormat("#,##0.00", symbols);
                        String formattedValue = (isNegative ? "-" : "") + "R$ " + formato.format(valor);

                        campoValor.setText(formattedValue);
                        campoValor.setCaretPosition(campoValor.getText().length());

                        if (isNegative) {
                            campoValor.setForeground(Color.RED);
                        } else {
                            campoValor.setForeground(DARK_GREEN);
                        }
                    }
                } else if (texto.equals("-")) {
                    campoValor.setText("-");
                    campoValor.setForeground(Color.RED);
                    campoValor.setCaretPosition(campoValor.getText().length());
                } else {
                    campoValor.setText("");
                    campoValor.setForeground(DARK_GRAY.brighter());
                    campoValor.setCaretPosition(campoValor.getText().length());
                }
            }
        });
    }


}
