package com.datagenio.crawler.util;

import java.net.URI;
import java.net.URLConnection;
import java.util.List;

public class FileTypeChecker {

    public static final String AAC = "audio/aac";
    public static final String ABW = "application/x-abiword";
    public static final String ARC = "application/x-freearc";
    public static final String AVI = "video/x-msvideo";
    public static final String AZW = "application/vnd.amazon.ebook";
    public static final String BIN = "application/octet-stream";
    public static final String BMP = "image/bmp";
    public static final String BZ = "application/x-bzip";
    public static final String BZ2 = "application/x-bzip2";
    public static final String CSH = "application/x-csh";
    public static final String CSS = "text/css";
    public static final String CSV = "text/csv";
    public static final String DOC = "application/msword";
    public static final String DOCX = "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
    public static final String EOT = "application/vnd.ms-fontobject";
    public static final String EPUB = "application/epub+zip";
    public static final String GIF = "image/gif";
    public static final String ICO = "image/vnd.microsoft.icon";
    public static final String ICS = "text/calendar";
    public static final String JAR = "application/java-archive";
    public static final String JPEG = "image/jpeg";
    public static final String JS = "text/javascript";
    public static final String JSON = "application/json";
    public static final String MID = "audio/midi";
    public static final String MIDI = "audio/x-midi";
    public static final String MP3 = "audio/mpeg";
    public static final String MPEG = "video/mpeg";
    public static final String MPKG = "application/vnd.apple.installer+xml";
    public static final String ODP = "application/vnd.oasis.opendocument.presentation";
    public static final String ODS = "application/vnd.oasis.opendocument.spreadsheet";
    public static final String ODT = "application/vnd.oasis.opendocument.text";
    public static final String OGA = "audio/ogg";
    public static final String OGV = "video/ogg";
    public static final String OGX = "application/ogg";
    public static final String OTF = "font/otf";
    public static final String PNG = "image/png";
    public static final String PDF = "application/pdf";
    public static final String PPT = "application/vnd.ms-powerpoint";
    public static final String PPTX = "application/vnd.openxmlformats-officedocument.presentationml.presentation";
    public static final String RAR = "application/x-rar-compressed";
    public static final String RTF = "application/rtf";
    public static final String SH = "application/x-sh";
    public static final String SVG = "image/svg+xml";
    public static final String SWF = "application/x-shockwave-flash";
    public static final String TAR = "application/x-tar";
    public static final String TIF = "image/tiff";
    public static final String TTF = "font/ttf";
    public static final String TXT = "text/plain";
    public static final String VSD = "application/vnd.visio";
    public static final String WAV = "audio/wav";
    public static final String WEBA = "audio/webm";
    public static final String WEBM = "video/webm";
    public static final String WEBP = "image/webp";
    public static final String WOFF = "font/woff";
    public static final String WOFF2 = "font/woff2";
    public static final String XLS = "application/vnd.ms-excel";
    public static final String XLSX = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
    public static final String XML = "application/xml";
    public static final String XUL = "application/vnd.mozilla.xul+xml";
    public static final String ZIP = "application/zip";
    public static final String THREEGP = "video/3gpp";
    public static final String THREEG2 = "video/3gpp2";
    public static final String SEVENZ = "application/x-7z-compressed";

    private static List<String> bannedFormats = List.of(
            AAC, ABW, ARC, AVI, AZW, BIN, BMP, BZ, BZ2, CSH, CSS, CSV, DOC, DOCX, EOT,
            EPUB, GIF, ICO, ICS, JAR, JPEG, JS, JSON, MID, MIDI, MP3, MPEG, MPKG, ODP,
            ODS, ODT, OGA, OGV, OGX, OTF, PNG, PDF, PPT, PPTX, RAR, RTF, SH, SVG, SWF,
            TAR, TIF, TTF, TXT, VSD, WAV, WEBA, WEBM, WEBP, WOFF, WOFF2, XLS, XLSX, XML,
            XUL, ZIP, THREEGP, THREEG2, SEVENZ
    );

    public static boolean isValidFileType(URI uri) {
        if (uri == null || uri.getHost() == null) {
            return false;
        }

        String format = URLConnection.guessContentTypeFromName(uri.toString());
        format = format == null ? "" : format;
        return !bannedFormats.contains(format);
    }
}
