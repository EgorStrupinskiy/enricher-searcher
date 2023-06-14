package com.innowise.enricherservice.util;

public class FileUtils {
    public static String extractFilenameFromContentDisposition(String contentDisposition) {
        String filename = null;
        if (contentDisposition != null) {
            String[] parts = contentDisposition.split(";");
            for (String part : parts) {
                if (part.trim().startsWith("filename=")) {
                    filename = part.substring(part.indexOf('=') + 1).trim();
                    filename = filename.replaceAll("\"", ""); // Удаление кавычек, если они присутствуют
                    break;
                }
            }
        }
        return filename;
    }

    public static String extractFileExtension(String filename) {
        if (filename != null) {
            int dotIndex = filename.lastIndexOf('.');
            if (dotIndex >= 0 && dotIndex < filename.length() - 1) {
                return filename.substring(dotIndex);
            }
        }
        return "";
    }
}
