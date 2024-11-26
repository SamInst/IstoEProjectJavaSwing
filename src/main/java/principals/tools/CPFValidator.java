package principals.tools;

public class CPFValidator {
    public static boolean isCPFValid(String cpf) {
        cpf = cpf.replaceAll("[^0-9]", "");

        if (cpf.length() != 11) return false;

        if (cpf.chars().distinct().count() == 1) return false;

        int sum1 = 0;
        for (int i = 0; i < 9; i++) {
            sum1 += Character.getNumericValue(cpf.charAt(i)) * (10 - i);
        }
        int firstDigit = (sum1 * 10) % 11;
        if (firstDigit == 10) firstDigit = 0;

        if (firstDigit != Character.getNumericValue(cpf.charAt(9))) return false;

        int sum2 = 0;
        for (int i = 0; i < 10; i++) {
            sum2 += Character.getNumericValue(cpf.charAt(i)) * (11 - i);
        }
        int secondDigit = (sum2 * 10) % 11;
        if (secondDigit == 10) secondDigit = 0;

        return secondDigit == Character.getNumericValue(cpf.charAt(10));
    }
}

