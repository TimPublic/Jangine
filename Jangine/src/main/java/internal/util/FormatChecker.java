package internal.util;


public class FormatChecker {


    public static boolean isFormat(String filePath, String format) {
        if (format.startsWith(".")) {
            return filePath.endsWith(format);
        }

        return filePath.endsWith("." + format);
    }
    public static void assertFormat(String filePath, String format) {
        if (isFormat(filePath, format)) return;

        throw new IllegalArgumentException("[FORMAT CHECKER ERROR] : File is not of required format!\n"
        + "|-> File Path : " + filePath + "\n"
        + "|-> Required Format : " + format);
    }


}